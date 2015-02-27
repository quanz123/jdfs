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
import org.jdfs.commons.utils.InetSocketAddressHelper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author James Quan
 * @version 2015年2月9日 下午4:40:44
 */
public class TrackerServiceImpl extends AbstractJdfsServer implements
		TrackerService, DisposableBean {
	private String base = "/jdfs";

	private ConcurrentMap<Integer, ConcurrentMap<String, ServerInfo>> serverInfos = new ConcurrentHashMap<Integer, ConcurrentMap<String, ServerInfo>>();
	private ConcurrentMap<String, ServerInfo> serverAddrs = new ConcurrentHashMap<String, ServerInfo>();
	private Map<Integer, IZkChildListener> nodeListeners = new LinkedHashMap<Integer, IZkChildListener>();
	private IZkChildListener groupListener = null;

	private FileInfoService fileInfoService;
	private ObjectMapper objectMapper;

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
	public ObjectMapper getObjectMapper() {
		// TODO Auto-generated method stub
		return super.getObjectMapper();
	}

	@Override
	public void setObjectMapper(ObjectMapper objectMapper) {
		// TODO Auto-generated method stub
		super.setObjectMapper(objectMapper);
	}

	@Override
	public void destroy() throws Exception {
		for (int group : serverInfos.keySet()) {
			clearServerList(group);
		}
		String basePath = getBase() + "/storages";
		zk.unsubscribeChildChanges(basePath, groupListener);
	}

	@Override
	protected void initJdfsServer() throws Exception {
		Assert.notNull(fileInfoService, "fileInfoService is required!");
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		String basePath = getBase() + "/storages";
		mkdirs(basePath);
		groupListener = new GroupWatcher();
		zk.subscribeChildChanges(basePath, groupListener);
		reloadAllServerList();
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
		for (int group : serverInfos.keySet()) {
			clearServerList(group);
		}
		String basePath = getBase() + "/storages";
		for (String group : zk.getChildren(basePath)) {
			reloadServerList(Integer.parseInt(group));
		}
	}

	protected void clearServerList(int group) {
		String groupName = Integer.toString(group);
		IZkChildListener listener = nodeListeners.get(groupName);
		if (listener != null) {
			String path = getBase() + "/storages/" + groupName;
			zk.unsubscribeChildChanges(path, listener);
		}
		ConcurrentMap<String, ServerInfo> nodes = serverInfos.get(groupName);
		if (nodes != null) {
			for (Map.Entry<String, ServerInfo> entry : nodes.entrySet()) {
				String node = entry.getKey();
				ServerInfo info = entry.getValue();
				String address = InetSocketAddressHelper.toString(info
						.getServiceAddress());
				ServerInfo info2 = serverAddrs.get(address);
				if (info.equals(info2)) {
					serverAddrs.remove(address);
					logger.debug("unregister server: {}/{} - {}", groupName,
							node, address);
				}
			}
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
		for (String node : zk.getChildren(path)) {
			Map<String, String> info = getServerInfo(prefix + node);
			if (info != null) {
				// nodes.put(node, info);
				ServerInfo server = new ServerInfo();
				server.setGroup(group);
				String address = info.get("address");
				server.setServiceAddress(InetSocketAddressHelper.parse(address));
				nodes.put(node, server);
				serverAddrs.put(InetSocketAddressHelper.toString(server
						.getServiceAddress()), server);
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
						ServerInfo info2 = serverAddrs.get(address);
						if (info.equals(info2)) {
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
						// nodes.put(name, info);
						ServerInfo server = new ServerInfo();
						server.setGroup(group);
						String address = info.get("address");
						server.setServiceAddress(InetSocketAddressHelper
								.parse(address));
						nodes.put(name, server);
						serverAddrs.put(InetSocketAddressHelper.toString(server
								.getServiceAddress()), server);
						logger.debug("register server: {}/{} - {}", group,
								name, address);
					}
				}
			}
		}
	}
}
