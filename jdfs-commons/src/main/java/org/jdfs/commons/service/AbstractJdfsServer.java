package org.jdfs.commons.service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.jdfs.commons.utils.InetSocketAddressHelper;
import org.jdfs.tracker.service.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 服务器的基础实现
 * 
 * @author James Quan
 * @version 2015年2月6日 下午6:04:24
 */
public abstract class AbstractJdfsServer implements InitializingBean {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected CuratorFramework curator;
	private InetSocketAddress serviceAddress;
	private ObjectMapper objectMapper;
	private String base = "/jdfs";
	private int group = 1;
	private String name;

	private ServerInfo serverInfo;
	private ServerInfo[] members = new ServerInfo[0];

	/**
	 * 返回服务器所使用的zookeeper服务
	 * 
	 * @return
	 */
	public CuratorFramework getCurator() {
		return curator;
	}

	/**
	 * 设置服务器所使用的zookeeper服务
	 * 
	 * @param curator
	 */
	public void setCurator(CuratorFramework curator) {
		this.curator = curator;
	}

	/**
	 * 返回对外服务的地址
	 * 
	 * @return
	 */
	public InetSocketAddress getServiceAddress() {
		return serviceAddress;
	}

	/**
	 * 设置对外服务的地址
	 * 
	 * @param serviceAddress
	 */
	public void setServiceAddress(InetSocketAddress serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	/**
	 * 返回服务器所在的服务器组
	 * 
	 * @return
	 */
	public int getGroup() {
		return group;
	}

	/**
	 * 设置服务器所在的服务器组
	 * 
	 * @param group
	 */
	public void setGroup(int group) {
		this.group = group;
	}

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

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(serviceAddress, "serviceAddress is required!");
		Assert.notNull(curator, "curator is required!");
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		registerServer();
		initJdfsServer();
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

	/**
	 * 将服务器注册到zookeeper
	 * 
	 * @throws Exception
	 */
	protected void registerServer() throws Exception {
		String base = getZkBasePath();
		String path = base + '/' + getZkNodeName();
		byte[] data = getServerData();
		EnsurePath ensurePath = new EnsurePath(base);
		ensurePath.ensure(curator.getZookeeperClient());
		name = curator.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
				.forPath(path, data);
		name = name.substring(base.length() + 1);
		List<String> children = curator.getChildren()
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
				}).forPath(base);
		reloadMembers(base, children, true);
	}

	/**
	 * 返回服务器在zk上注册时的基础节点路径
	 * 
	 * @return
	 */
	protected String getZkBasePath() {
		return getBase() + '/' + getType();
	}

	/**
	 * 返回服务器的类型
	 * 
	 * @return
	 */
	protected abstract String getType();

	/**
	 * 返回服务器在zk上注册时的节点名字
	 * 
	 * @return
	 */
	protected abstract String getZkNodeName();

	/**
	 * 在zk服务器上建立永久节点
	 * 
	 * @param path
	 *            待建立节点的路径
	 * @throws Exception
	 */
	// protected void mkdirs(String path) throws Exception {
	// if (zk.exists(path)) {
	// return;
	// }
	// int slash = path.lastIndexOf('/');
	// if (slash > 0) {
	// String parent = path.substring(0, slash);
	// mkdirs(parent);
	// }
	// try {
	// zk.create(path, new byte[0], Ids.OPEN_ACL_UNSAFE,
	// CreateMode.PERSISTENT);
	// } catch (ZkNodeExistsException e) {
	// }
	// }

	/**
	 * 返回需要存储到服务器状态节点的数据
	 * 
	 * @return
	 * @throws Exception
	 * @see {@link #getServerInfo()}
	 */
	protected byte[] getServerData() throws Exception {
		Map<String, String> props = new LinkedHashMap<String, String>();
		props.put("address", InetSocketAddressHelper.toString(serviceAddress));
		prepareServerData(props);
		byte[] data = objectMapper.writeValueAsBytes(props);
		return data;
	}

	/**
	 * 返回服务器状态节点数据对应的 数据信息
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	protected Map<String, String> getServerInfo(String path) throws Exception {
		byte[] data;
		try {
			data = curator.getData().forPath(path);
			// data = zk.readData(path);
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

	protected void reloadMembers(String path, List<String> children,
			boolean reloadAll) throws Exception {
		if (children == null) {
			children = Collections.emptyList();
		}
		String prefix = path + '/';
		List<ServerInfo> list = new ArrayList<ServerInfo>();
		for (String child : children) {
			if (child.equals(name)) {
				if (reloadAll) {
					ServerInfo server = readServerInfo(child, group, prefix
							+ child);
					serverInfo = server;
				}
			} else {
				ServerInfo server = readServerInfo(child, group, prefix + child);
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

	/**
	 * 返回指定节点所代表的服务器的信息
	 * 
	 * @param name
	 *            服务器的名字
	 * @param group
	 *            服务器所隶属的组
	 * @param path
	 *            服务器数据节点的路径
	 * @return
	 * @throws Exception
	 */
	protected ServerInfo readServerInfo(String name, int group, String path)
			throws Exception {
		Map<String, String> props = getServerInfo(path);
		ServerInfo server = new ServerInfo();
		server.setName(name);
		server.setGroup(group);
		String address = props.get("address");
		server.setServiceAddress(InetSocketAddressHelper.parse(address));
		return server;
	}

	/**
	 * 建立服务器状态节点前执行的回调函数，供子类重写后加入其他节点数据
	 * 
	 * @param props
	 *            服务器节点所包含的数据
	 * @throws Exception
	 */
	protected void prepareServerData(Map<String, String> props)
			throws Exception {
	}

	/**
	 * 基础系统执行完毕后调用的回调函数，供子类重写后加入扩展逻辑
	 * 
	 * @throws Exception
	 */
	protected void initJdfsServer() throws Exception {
	}

	/**
	 * 组成员变化监听器
	 */
	protected class MemberWatcher implements IZkChildListener {
		@Override
		public void handleChildChange(String parentPath,
				List<String> currentChilds) throws Exception {
			if (currentChilds == null) {
				members = new ServerInfo[0];
			} else {
				List<ServerInfo> list = new ArrayList<ServerInfo>();
				String prefix = parentPath + '/';
				for (String child : currentChilds) {
					if (child.equals(name)) {
						continue;
					}
					ServerInfo server = readServerInfo(child, group, prefix
							+ child);
					list.add(server);
				}
				members = list.toArray(new ServerInfo[list.size()]);
				if (logger.isDebugEnabled()) {
					logger.debug("members change detected, members:");
					for (ServerInfo server : members) {
						logger.debug("server: {}/{} -> {}", server.getGroup(),
								server.getName(), InetSocketAddressHelper
										.toString(server.getServiceAddress()));
					}
				}
			}
		}
	}

}
