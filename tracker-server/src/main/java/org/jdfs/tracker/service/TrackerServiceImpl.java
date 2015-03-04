package org.jdfs.tracker.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.I0Itec.zkclient.IZkChildListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.WatchedEvent;
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

	private ConcurrentMap<Integer, ConcurrentMap<String, ServerInfo>> serverInfos = new ConcurrentHashMap<Integer, ConcurrentMap<String, ServerInfo>>();
	// private ConcurrentMap<String, ServerInfo> serverAddrs = new
	// ConcurrentHashMap<String, ServerInfo>();
	// private Map<Integer, IZkChildListener> nodeListeners = new
	// LinkedHashMap<Integer, IZkChildListener>();
	// private IZkChildListener groupListener = null;

	private FileInfoService fileInfoService;

	@Override
	protected String getType() {
		return "trackers";
	}

	@Override
	protected String getZkNodeName() {
		return "tracker";
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
			clearServerList(group);
		}
		String basePath = getBase() + "/storages";
		// zk.unsubscribeChildChanges(basePath, groupListener);
	}

	@Override
	protected void initJdfsServer() throws Exception {
		Assert.notNull(fileInfoService, "fileInfoService is required!");
		String basePath = getBase() + "/storages";
		EnsurePath ensurePath = new EnsurePath(basePath);
		ensurePath.ensure(curator.getZookeeperClient());
		List<String> children = curator.getChildren().usingWatcher(new CuratorWatcher() {
			@Override
			public void process(WatchedEvent event) throws Exception {
				String path = event.getPath();
				switch (event.getType()) {
				case NodeChildrenChanged:
					List<String> children = curator.getChildren().usingWatcher(this).forPath(path);
					reloadGroupList(children);
					break;
				default:
					curator.getChildren().usingWatcher(this).forPath(path);
					break;
				}
			}
		}).forPath(basePath);
		reloadGroupList(children);
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

	protected void reloadGroupList(List<String> list){
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
			for (int group : toBeAdd) {
				reloadServerList(group);
			}
		}
	}
	/**
	 * 重新读取存储服务器地址列表
	 * 
	 * @throws Exception
	 */
	protected void reloadAllServerList() throws Exception {
		for (int group : serverInfos.keySet()) {
			clearServerList(group);
		}
		String basePath = getBase() + "/storages";
		// for (String group : zk.getChildren(basePath)) {
		// reloadServerList(Integer.parseInt(group));
		// }
	}

	protected void clearServerList(int group) {
		String groupName = Integer.toString(group);
//		IZkChildListener listener = nodeListeners.get(groupName);
//		if (listener != null) {
//			String path = getBase() + "/storages/" + groupName;
//			// zk.unsubscribeChildChanges(path, listener);
//		}
		ConcurrentMap<String, ServerInfo> nodes = serverInfos.get(groupName);
		if (nodes != null) {
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
			nodes.clear();
		}
	}

	protected void reloadServerList(int group) throws IOException {
		ConcurrentMap<String, ServerInfo> nodes = serverInfos.get(group);
		if (nodes == null) {
			nodes = new ConcurrentHashMap<String, ServerInfo>();
			serverInfos.put(group, nodes);
		} else {
			clearServerList(group);
		}
		String path = getBase() + "/storages/" + group;
		String prefix = path + '/';
		// for (String name : zk.getChildren(path)) {
		// ServerInfo server = readServerInfo(name, group, prefix + name);
		// logger.debug(
		// "register storage server: {}/{} -> {}",
		// group,
		// name,
		// InetSocketAddressHelper.toString(server.getServiceAddress()));
		// }
		// IZkChildListener listener = new NodeWatcher();
		// zk.subscribeChildChanges(path, listener);
		// nodeListeners.put(group, listener);
	}

	/**
	 * 存储组变化监听器
	 */
	protected class GroupWatcher implements IZkChildListener {
		@Override
		public void handleChildChange(String parentPath,
				List<String> currentChilds) throws Exception {
			if (currentChilds == null) {
				serverInfos.clear();
			} else {
				HashSet<Integer> toBeDeleted = new HashSet<Integer>(
						serverInfos.keySet());
				HashSet<Integer> toBeAdd = new HashSet<Integer>();
				for (String child : currentChilds) {
					if (!toBeDeleted.remove(child)) {
						toBeAdd.add(Integer.parseInt(child));
					}
				}
				for (int group : toBeDeleted) {
					clearServerList(group);
					serverInfos.remove(group);
				}
				for (int group : toBeAdd) {
					reloadServerList(group);
				}
			}
		}
	}

	/**
	 * 存储节点变化监听器
	 */
	protected class NodeWatcher implements IZkChildListener {
		@Override
		public void handleChildChange(String parentPath,
				List<String> currentChilds) throws Exception {
			int slash = parentPath.lastIndexOf('/');
			int group = Integer.parseInt(parentPath.substring(slash + 1));
			if (currentChilds == null) {
				clearServerList(group);
				serverInfos.remove(group);
			} else {
				ConcurrentMap<String, ServerInfo> nodes = serverInfos
						.get(group);
				if (nodes == null) {
					nodes = new ConcurrentHashMap<String, ServerInfo>();
					ConcurrentMap<String, ServerInfo> old = serverInfos
							.putIfAbsent(group, nodes);
					if (old != null) {
						nodes = old;
					}
				}
				HashSet<String> toBeDeleted = new HashSet<String>(
						nodes.keySet());
				HashSet<String> toBeAdd = new HashSet<String>();
				for (String child : currentChilds) {
					if (!toBeDeleted.remove(child)) {
						toBeAdd.add(child);
					}
				}
				for (String name : toBeDeleted) {
					ServerInfo info = nodes.remove(name);
					if (info != null) {
						String address = InetSocketAddressHelper.toString(info
								.getServiceAddress());
						// ServerInfo info2 = serverAddrs.get(address);
						// if (info.equals(info2)) {
						// serverAddrs.remove(address);
						logger.debug("unregister storage server: {}/{} - {}",
								group, name, address);
						// }
					}
				}
				String prefix = parentPath + '/';
				for (String name : toBeAdd) {
					ServerInfo server = readServerInfo(name, group, prefix
							+ name);
					logger.debug("register storgage server: {}/{} - {}", group,
							name, InetSocketAddressHelper.toString(server
									.getServiceAddress()));
				}
			}
		}
	}
}
