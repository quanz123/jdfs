package org.jdfs.commons.service;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
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
	protected ZkClient zk;
	private InetSocketAddress serviceAddress;
	private ObjectMapper objectMapper;
	

	/**
	 * 返回服务器所使用的zookeeper服务
	 * @return
	 */
	public ZkClient getZk() {
		return zk;
	}

	/**
	 * 设置服务器所使用的zookeeper服务
	 * @param zk
	 */
	public void setZk(ZkClient zk) {
		this.zk = zk;
	}


	/**
	 * 返回对外服务的地址
	 * @return
	 */
	public InetSocketAddress getServiceAddress() {
		return serviceAddress;
	}
	
	/**
	 * 设置对外服务的地址
	 * @param serviceAddress
	 */
	public void setServiceAddress(InetSocketAddress serviceAddress) {
		this.serviceAddress = serviceAddress;
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
		Assert.notNull(zk, "zk is required!");
		if(objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		registerServer();
		initJdfsServer();
	}
	
	/**
	 * 将服务器注册到zookeeper
	 * @throws Exception
	 */
	protected void registerServer() throws Exception{
		String path = getZkBasePath();
		mkdirs(path);
		String nodePath = path + '/' + getZkNodeName();
		byte[] data = getServerData();
			zk.create(nodePath, data,
					Ids.READ_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
	
	/**
	 * 返回服务器在zk上注册时的基础节点路径
	 * @return
	 */
	protected abstract String getZkBasePath();
	
	/**
	 * 返回服务器在zk上注册时的节点名字
	 * @return
	 */
	protected abstract String getZkNodeName();

	/**
	 * 在zk服务器上建立永久节点
	 * @param path 待建立节点的路径
	 * @throws Exception
	 */
	protected void mkdirs(String path) throws Exception {		
		if (zk.exists(path)) {
			return;
		}
		int slash = path.lastIndexOf('/');
		if (slash > 0) {
			String parent = path.substring(0, slash);
			mkdirs(parent);
		}
		try {
			zk.create(path, new byte[0],
					Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} catch (ZkNodeExistsException e) {
		}
	}
	
	/**
	 * 返回服务器状态节点所包含的数据
	 * @return
	 * @throws Exception
	 */
	protected byte[] getServerData() throws Exception{
		Map<String, String> props = new LinkedHashMap<String, String>();
		props.put("address", toString(serviceAddress));
		prepareServerData(props);
		byte[] data = objectMapper.writeValueAsBytes(props);
		return data;
	}
	
	protected String toString(InetSocketAddress addr) {
		if(addr.isUnresolved()){
			return addr.getHostString() + ':' + addr.getPort();			
		} else {
			InetAddress ia = addr.getAddress();
			return ia.getHostAddress()+ ':' + addr.getPort();			
		}
	}
	
	/**
	 * 建立服务器状态节点前执行的回调函数，供子类重写后加入其他节点数据
	 * @param props 服务器节点所包含的数据
	 * @throws Exception
	 */
	protected void prepareServerData(Map<String, String> props) throws Exception{		
	}
	
	/**
	 * 基础系统执行完毕后调用的回调函数，供子类重写后加入扩展逻辑
	 * @throws Exception
	 */
	protected void initJdfsServer() throws Exception {		
	}
	
	
}
