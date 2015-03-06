package org.jdfs.tracker.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.jdfs.commons.service.AbstractJdfsServer;
import org.jdfs.commons.utils.InetSocketAddressHelper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

/**
 * 
 * @author James Quan
 * @version 2015年2月9日 下午4:40:44
 */
public class TrackerServiceImpl extends AbstractJdfsServer implements
		TrackerService, DisposableBean {

	private String trackerServerType = "trackers";
	private String trackerNodeName = "tracker";
	private String storageServerType = "storages";
	private String storageNodeName = "storage";

	private ConcurrentMap<Integer, ConcurrentMap<String, ServerInfo>> serverInfos = new ConcurrentHashMap<Integer, ConcurrentMap<String, ServerInfo>>();
	// private ConcurrentMap<String, ServerInfo> serverAddrs = new
	// ConcurrentHashMap<String, ServerInfo>();
	// private Map<Integer, IZkChildListener> nodeListeners = new
	// LinkedHashMap<Integer, IZkChildListener>();
	// private IZkChildListener groupListener = null;

	private FileInfoService fileInfoService;

	private ServerInfo serverInfo;
	private ServerInfo[] members = new ServerInfo[0];

	public String getTrackerServerType() {
		return trackerServerType;
	}

	public void setTrackerServerType(String trackerServerType) {
		this.trackerServerType = trackerServerType;
	}

	public String getTrackerNodeName() {
		return trackerNodeName;
	}

	public void setTrackerNodeName(String trackerNodeName) {
		this.trackerNodeName = trackerNodeName;
	}

	public String getStorageServerType() {
		return storageServerType;
	}

	public void setStorageServerType(String storageServerType) {
		this.storageServerType = storageServerType;
	}

	public String getStorageNodeName() {
		return storageNodeName;
	}

	public void setStorageNodeName(String storageNodeName) {
		this.storageNodeName = storageNodeName;
	}

	public FileInfoService getFileInfoService() {
		return fileInfoService;
	}

	public void setFileInfoService(FileInfoService fileInfoService) {
		this.fileInfoService = fileInfoService;
	}

	@Override
	public void destroy() throws Exception {
		for (int group : serverInfos.keySet()) {
			// clearServerList(group);
		}
		// String basePath = getBase() + "/storages";
		// zk.unsubscribeChildChanges(basePath, groupListener);
	}

	/**
	 * 返回Tracker管理服务器自身的信息
	 * 
	 * @return
	 */
	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	/**
	 * 返回同组的其他管理服务器的信息
	 * 
	 * @return
	 */
	public ServerInfo[] getMembers() {
		return members;
	}

	@Override
	protected String getType() {
		return trackerServerType;
	}

	@Override
	protected String getZkNodeName() {
		return trackerNodeName;
	}

	@Override
	protected void initJdfsServer() throws Exception {
		Assert.notNull(fileInfoService, "fileInfoService is required!");
		// 监控tracker服务器列表
		String trackerBase = getZkBasePath() + '/' + getGroup();
		List<String> trackerChildren = curator.getChildren()
				.usingWatcher(new CuratorWatcher() {
					@Override
					public void process(WatchedEvent event) throws Exception {
						Watcher.Event.EventType type = event.getType();
						String path = event.getPath();
						switch (type) {
						case NodeChildrenChanged:
							List<String> children = curator.getChildren()
									.usingWatcher(this).forPath(path);
							reloadMembers(path, children, false);
							break;
						default:
							curator.getChildren().usingWatcher(this)
									.forPath(path);
							break;
						}
					}
				}).forPath(trackerBase);
		reloadMembers(trackerBase, trackerChildren, true);

		// 监控存储服务器列表
		String storageServerBasePath = getBase() + '/' + getStorageServerType();
		EnsurePath ensurePath = new EnsurePath(storageServerBasePath);
		ensurePath.ensure(curator.getZookeeperClient());
		List<String> storageGroupChildren = curator.getChildren()
				.usingWatcher(new CuratorWatcher() {
					@Override
					public void process(WatchedEvent event) throws Exception {
						String path = event.getPath();
						switch (event.getType()) {
						case NodeChildrenChanged:
							List<String> children = curator.getChildren()
									.usingWatcher(this).forPath(path);
							reloadGroupList(path, children);
							curator.getChildren().usingWatcher(this)
									.forPath(path);
							break;
						default:
							curator.getChildren().usingWatcher(this)
									.forPath(path);
							break;
						}
					}
				}).forPath(storageServerBasePath);
		reloadGroupList(storageServerBasePath, storageGroupChildren);
		// mkdirs(basePath);
		// groupListener = new GroupWatcher();
		// zk.subscribeChildChanges(basePath, groupListener);
		// reloadAllServerList();
	}

	@Override
	public ServerInfo getDownloadServerForFile(long id) throws IOException {
		FileInfo file = fileInfoService.getFileInfo(id);
		if (file == null) {
			return null;
		}
		int group = file.getGroup();
		if (group == 0) {
			return null;
		}
		ConcurrentMap<String, ServerInfo> nodes = serverInfos.get(group);
		String[] names = nodes.keySet().toArray(new String[nodes.size()]);
		Arrays.sort(names, Collections.reverseOrder());
		return names.length > 0 ? nodes.get(names[0]) : null;
	}

	@Override
	public ServerInfo getUploadServerForFile(long id) throws IOException {
		int group = 0;
		FileInfo file = fileInfoService.getFileInfo(id);
		if (file == null) {
			Integer[] groups = serverInfos.keySet().toArray(
					new Integer[serverInfos.size()]);
			if (groups.length > 0) {
				group = groups[0];
			}
		} else {
			group = file.getGroup();
		}
		if (group == 0) {
			return null;
		}
		ConcurrentMap<String, ServerInfo> nodes = serverInfos.get(group);
		String[] names = nodes.keySet().toArray(new String[nodes.size()]);
		Arrays.sort(names);
		return names.length > 0 ? nodes.get(names[0]) : null;
	}

	protected void reloadGroupList(String base, List<String> list)
			throws Exception {
		logger.debug("reload group list...");
		if (list == null || list.isEmpty()) {
			serverInfos.clear();
		} else {
			HashSet<Integer> toBeDeleted = new HashSet<Integer>(
					serverInfos.keySet());
			HashSet<Integer> toBeAdd = new HashSet<Integer>();
			for (String item : list) {
				int group = Integer.parseInt(item);
				if (!toBeDeleted.remove(group)) {
					toBeAdd.add(group);
				}
			}
			for (int group : toBeDeleted) {
				clearServerList(group);
				serverInfos.remove(group);
			}
			String prefix = base + '/';
			for (int group : toBeAdd) {
				ConcurrentMap<String, ServerInfo> nodes = serverInfos
						.get(group);
				if (nodes == null) {
					ConcurrentMap<String, ServerInfo> map = new ConcurrentHashMap<String, ServerInfo>();
					nodes = serverInfos.putIfAbsent(group, map);
					if (nodes == null) {
						nodes = map;
					}
				}
				List<String> children = curator.getChildren()
						.usingWatcher(new CuratorWatcher() {
							@Override
							public void process(WatchedEvent event)
									throws Exception {
								String path = event.getPath();
								int slash = path.lastIndexOf('/');
								int group = Integer.parseInt(path
										.substring(slash + 1));
								switch (event.getType()) {
								case NodeChildrenChanged:
									List<String> children = curator
											.getChildren().usingWatcher(this)
											.forPath(path);
									reloadServerList(path, group, children);
									break;
								default:
									curator.getChildren().usingWatcher(this)
											.forPath(path);
									break;
								}
							}
						}).forPath(prefix + group);
				reloadServerList(prefix + group, group, children);
			}
		}
	}

	/**
	 * 重新读取存储服务器地址列表
	 * 
	 * @throws Exception
	 */
	// protected void reloadAllServerList() throws Exception {
	// for (int group : serverInfos.keySet()) {
	// clearServerList(group);
	// }
	// String basePath = getBase() + "/storages";
	// // for (String group : zk.getChildren(basePath)) {
	// // reloadServerList(Integer.parseInt(group));
	// // }
	// }
	//
	protected void clearServerList(int group) {
		// String groupName = Integer.toString(group);
		// IZkChildListener listener = nodeListeners.get(groupName);
		// if (listener != null) {
		// String path = getBase() + "/storages/" + groupName;
		// // zk.unsubscribeChildChanges(path, listener);
		// }
		ConcurrentMap<String, ServerInfo> servers = serverInfos.get(group);
		if (servers != null) {
			for (Map.Entry<String, ServerInfo> entry : servers.entrySet()) {
				String name = entry.getKey();
				ServerInfo server = entry.getValue();
				String address = InetSocketAddressHelper.toString(server
						.getServiceAddress());
				logger.debug("unregister storage server: {}/{} - {}", group,
						name, address);
			}
			// for (Map.Entry<String, ServerInfo> entry : nodes.entrySet()) {
			// String node = entry.getKey();
			// ServerInfo info = entry.getValue();
			// String address = InetSocketAddressHelper.toString(info
			// .getServiceAddress());
			// ServerInfo info2 = serverAddrs.get(address);
			// if (info.equals(info2)) {
			// serverAddrs.remove(address);
			// logger.debug("unregister server: {}/{} - {}", groupName,
			// node, address);
			// }
			// }
			servers.clear();
		}
	}

	//
	// protected void reloadServerList(String base, int group) throws Exception
	// {
	// ConcurrentMap<String, ServerInfo> nodes = serverInfos.get(group);
	// if (nodes == null) {
	// ConcurrentMap<String, ServerInfo> map = new ConcurrentHashMap<String,
	// ServerInfo>();
	// nodes = serverInfos.putIfAbsent(group, map);
	// if (nodes == null) {
	// nodes = map;
	// }
	// }
	// String prefix = base + '/';
	// // for (String name : zk.getChildren(path)) {
	// // ServerInfo server = readServerInfo(name, group, prefix + name);
	// // logger.debug(
	// // "register storage server: {}/{} -> {}",
	// // group,
	// // name,
	// // InetSocketAddressHelper.toString(server.getServiceAddress()));
	// // }
	// // IZkChildListener listener = new NodeWatcher();
	// // zk.subscribeChildChanges(path, listener);
	// // nodeListeners.put(group, listener);
	// }

	protected void reloadServerList(String basePath, int group,
			List<String> list) throws Exception {
		logger.debug("refresh server list, group {} ...", group);
		ConcurrentMap<String, ServerInfo> servers = serverInfos.get(group);
		if (servers == null) {
			ConcurrentMap<String, ServerInfo> map = new ConcurrentHashMap<String, ServerInfo>();
			servers = serverInfos.putIfAbsent(group, map);
			if (servers == null) {
				servers = map;
			}
		}
		if (list == null) {
			list = Collections.emptyList();
		}
		HashSet<String> toBeDeleted = new HashSet<String>(servers.keySet());
		HashSet<String> toBeAdd = new HashSet<String>();
		for (String item : list) {
			String name = item;
			if (!toBeDeleted.remove(name)) {
				toBeAdd.add(name);
			}
		}
		for (String name : toBeDeleted) {
			ServerInfo server = servers.remove(name);
			if (server != null) {
				String address = InetSocketAddressHelper.toString(server
						.getServiceAddress());
				logger.debug("unregister storage server: {}/{} - {}", group,
						name, address);
			}
		}
		// String base = getBase() + "/storages/" + group;
		// String prefix = base + '/';
		String prefix = basePath + '/';
		for (String name : toBeAdd) {
			ServerInfo server = readServerInfo(name, group, prefix + name);
			servers.put(name, server);
			logger.debug(
					"register storgage server: {}/{} - {}",
					group,
					name,
					InetSocketAddressHelper.toString(server.getServiceAddress()));
		}
	}

	/**
	 * 存储组变化监听器
	 */
	// protected class GroupWatcher implements IZkChildListener {
	// @Override
	// public void handleChildChange(String parentPath,
	// List<String> currentChilds) throws Exception {
	// if (currentChilds == null) {
	// serverInfos.clear();
	// } else {
	// HashSet<Integer> toBeDeleted = new HashSet<Integer>(
	// serverInfos.keySet());
	// HashSet<Integer> toBeAdd = new HashSet<Integer>();
	// for (String child : currentChilds) {
	// if (!toBeDeleted.remove(child)) {
	// toBeAdd.add(Integer.parseInt(child));
	// }
	// }
	// for (int group : toBeDeleted) {
	// clearServerList(group);
	// serverInfos.remove(group);
	// }
	// for (int group : toBeAdd) {
	// reloadServerList(group);
	// }
	// }
	// }
	// }

	/**
	 * 存储节点变化监听器
	 */
	// protected class NodeWatcher implements IZkChildListener {
	// @Override
	// public void handleChildChange(String parentPath,
	// List<String> currentChilds) throws Exception {
	// int slash = parentPath.lastIndexOf('/');
	// int group = Integer.parseInt(parentPath.substring(slash + 1));
	// if (currentChilds == null) {
	// clearServerList(group);
	// serverInfos.remove(group);
	// } else {
	// ConcurrentMap<String, ServerInfo> nodes = serverInfos
	// .get(group);
	// if (nodes == null) {
	// nodes = new ConcurrentHashMap<String, ServerInfo>();
	// ConcurrentMap<String, ServerInfo> old = serverInfos
	// .putIfAbsent(group, nodes);
	// if (old != null) {
	// nodes = old;
	// }
	// }
	// HashSet<String> toBeDeleted = new HashSet<String>(
	// nodes.keySet());
	// HashSet<String> toBeAdd = new HashSet<String>();
	// for (String child : currentChilds) {
	// if (!toBeDeleted.remove(child)) {
	// toBeAdd.add(child);
	// }
	// }
	// for (String name : toBeDeleted) {
	// ServerInfo info = nodes.remove(name);
	// if (info != null) {
	// String address = InetSocketAddressHelper.toString(info
	// .getServiceAddress());
	// // ServerInfo info2 = serverAddrs.get(address);
	// // if (info.equals(info2)) {
	// // serverAddrs.remove(address);
	// logger.debug("unregister storage server: {}/{} - {}",
	// group, name, address);
	// // }
	// }
	// }
	// String prefix = parentPath + '/';
	// for (String name : toBeAdd) {
	// ServerInfo server = readServerInfo(name, group, prefix
	// + name);
	// logger.debug("register storgage server: {}/{} - {}", group,
	// name, InetSocketAddressHelper.toString(server
	// .getServiceAddress()));
	// }
	// }
	// }
	// }

	protected void reloadMembers(String path, List<String> children,
			boolean reloadAll) throws Exception {
		if (children == null) {
			children = Collections.emptyList();
		}
		String prefix = path + '/';
		List<ServerInfo> list = new ArrayList<ServerInfo>();
		for (String child : children) {
			if (child.equals(getName())) {
				if (reloadAll) {
					ServerInfo server = readServerInfo(child, getGroup(),
							prefix + child);
					serverInfo = server;
				}
			} else {
				ServerInfo server = readServerInfo(child, getGroup(), prefix
						+ child);
				list.add(server);
			}
		}
		members = list.toArray(new ServerInfo[list.size()]);
		if (logger.isDebugEnabled()) {
			logger.debug("reload member list:");
			logger.debug("server: {}/{} -> {}", serverInfo.getGroup(),
					serverInfo.getName(), InetSocketAddressHelper
							.toString(serverInfo.getServiceAddress()));
			logger.debug("members:");
			for (ServerInfo server : members) {
				logger.debug("server: {}/{} -> {}", server.getGroup(), server
						.getName(), InetSocketAddressHelper.toString(server
						.getServiceAddress()));
			}
		}
	}

}
