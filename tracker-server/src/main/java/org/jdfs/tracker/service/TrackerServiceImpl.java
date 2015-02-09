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
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.jdfs.commons.service.AbstractJdfsServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

/**
 * 
 * @author James Quan
 * @version 2015年2月9日 下午4:40:44
 */
public class TrackerServiceImpl extends AbstractJdfsServer implements
		TrackerService, DisposableBean {
	private String base = "/jdfs";

	private ConcurrentMap<String, ConcurrentMap<String, Map<String, String>>> serverInfos = new ConcurrentHashMap<String, ConcurrentMap<String, Map<String, String>>>();
	private ConcurrentMap<String, String[]> serverAddrs = new ConcurrentHashMap<String, String[]>();
	private Map<String, IZkChildListener> nodeListeners = new LinkedHashMap<String, IZkChildListener>();
	private IZkChildListener groupListener = null;

	private FileInfoService fileInfoService;

	/**
	 * 返回服务器注册信息节点的根路径，缺省为{@code /jdfs}
	 * 
	 * @return
	 */
	public String getBase() {
		return base;
	}

	/**
	 * 设置服务器注册信息节点的根路径，缺省为{@code /jdfs}
	 * 
	 * @param base
	 */
	public void setBase(String base) {
		this.base = base;
	}

	@Override
	protected String getZkBasePath() {
		return getBase() + "/trackers";
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
		for (String group : serverInfos.keySet()) {
			clearServerList(group);
		}
		String basePath = getBase() + "/storages";
		zk.unsubscribeChildChanges(basePath, groupListener);
	}

	@Override
	protected void initJdfsServer() throws Exception {
		Assert.notNull(fileInfoService, "fileInfoService is required!");
		String basePath = getBase() + "/storages";
		mkdirs(basePath);
		groupListener = new GroupWatcher();
		zk.subscribeChildChanges(basePath, groupListener);
		reloadAllServerList();
	}

	@Override
	public String getDownloadServerAddress(long id) throws IOException {
		FileInfo file = fileInfoService.getFileInfo(id);
		if (file == null) {
			return null;
		}
		String group = file.getGroup();
		ConcurrentMap<String, Map<String, String>> nodes = serverInfos
				.get(group);
		String[] names = nodes.keySet().toArray(new String[nodes.size()]);
		Arrays.sort(names, Collections.reverseOrder());
		Map<String, String> props = names.length > 0 ? nodes.get(names[0])
				: null;
		return props == null ? null : props.get("address");
	}

	@Override
	public String getUploadServerAddress(long id) throws IOException {
		String group = null;
		FileInfo file = fileInfoService.getFileInfo(id);
		if (file == null) {
			String[] groups = serverInfos.keySet().toArray(
					new String[serverInfos.size()]);
			if (groups.length > 0) {
				group = groups[0];
			}
		} else {
			group = file.getGroup();
		}
		if (group == null) {
			return null;
		}
		ConcurrentMap<String, Map<String, String>> nodes = serverInfos
				.get(group);
		String[] names = nodes.keySet().toArray(new String[nodes.size()]);
		Arrays.sort(names);
		Map<String, String> props = names.length > 0 ? nodes.get(names[0])
				: null;
		return props == null ? null : props.get("address");
	}

	/**
	 * 读取服务器节点的数据信息
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	protected Map<String, String> getServerInfo(String path) throws IOException {
		byte[] data;
		try {
			data = zk.readData(path);
		} catch (ZkNoNodeException e) {
			logger.warn("read node \"" + path + "\" error", e);
			return null;
		}
		if (data == null || data.length == 0) {
			logger.warn("node \"" + path + "\" data is empty!");
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String, String> props = getObjectMapper()
				.readValue(data, Map.class);
		return props;
	}

	/**
	 * 重新读取存储服务器地址列表
	 * 
	 * @throws Exception
	 */
	protected void reloadAllServerList() throws Exception {
		for (String group : serverInfos.keySet()) {
			clearServerList(group);
		}
		String basePath = getBase() + "/storages";
		for (String group : zk.getChildren(basePath)) {
			reloadServerList(group);
		}
	}

	protected void clearServerList(String group) {
		IZkChildListener listener = nodeListeners.get(group);
		if (listener != null) {
			String path = getBase() + "/storages/" + group;
			zk.unsubscribeChildChanges(path, listener);
		}
		ConcurrentMap<String, Map<String, String>> nodes = serverInfos
				.get(group);
		if (nodes != null) {
			for (Map.Entry<String, Map<String, String>> entry : nodes
					.entrySet()) {
				String node = entry.getKey();
				Map<String, String> info = entry.getValue();
				String address = info.get("address");
				String[] infos = serverAddrs.get(address);
				if (infos != null && group.equals(infos[0])
						&& node.equals(infos[1])) {
					serverAddrs.remove(address);
					logger.debug("unregister server: {}/{} - {}", group, node,
							address);
				}
			}
			nodes.clear();
		}
	}

	protected void reloadServerList(String group) throws IOException {
		ConcurrentMap<String, Map<String, String>> nodes = serverInfos
				.get(group);
		if (nodes == null) {
			nodes = new ConcurrentHashMap<String, Map<String, String>>();
			serverInfos.put(group, nodes);
		} else {
			clearServerList(group);
		}
		String path = getBase() + "/storages/" + group;
		for (String node : zk.getChildren(path)) {
			Map<String, String> info = getServerInfo(path);
			if (info != null) {
				nodes.put(node, info);
				String address = info.get("address");
				serverAddrs.put(address, new String[] { group, node });
				logger.debug("register server: {}/{} - {}", group, node,
						address);
			}
		}
		IZkChildListener listener = new NodeWatcher();
		zk.subscribeChildChanges(path, listener);
		nodeListeners.put(group, listener);
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
				HashSet<String> toBeDeleted = new HashSet<String>(
						serverInfos.keySet());
				HashSet<String> toBeAdd = new HashSet<String>();
				for (String child : currentChilds) {
					if (!toBeDeleted.remove(child)) {
						toBeAdd.add(child);
					}
				}
				for (String name : toBeDeleted) {
					clearServerList(name);
					serverInfos.remove(name);
				}
				for (String name : toBeAdd) {
					reloadServerList(name);
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
			String group = parentPath.substring(slash + 1);
			if (currentChilds == null) {
				clearServerList(group);
				serverInfos.remove(group);
			} else {
				ConcurrentMap<String, Map<String, String>> nodes = serverInfos
						.get(group);
				if (nodes == null) {
					nodes = new ConcurrentHashMap<String, Map<String, String>>();
					ConcurrentMap<String, Map<String, String>> old = serverInfos
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
					Map<String, String> info = nodes.remove(name);
					if (info != null) {
						String address = info.get("address");
						String[] nodeInfos = serverAddrs.get(address);
						if (nodeInfos != null && group.equals(nodeInfos[0])
								&& name.equals(nodeInfos[1])) {
							serverAddrs.remove(address);
							logger.debug("unregister server: {}/{} - {}",
									group, name, address);
						}
					}
				}
				for (String name : toBeAdd) {
					String path = parentPath + '/' + name;
					Map<String, String> info = getServerInfo(path);
					if (info != null) {
						nodes.put(name, info);
						String address = info.get("address");
						serverAddrs.put(address, new String[] { group, name });
						logger.debug("register server: {}/{} - {}", group,
								name, address);
					}
				}
			}
		}
	}
}
