package org.jdfs.commons.service;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * 以spring方式创建ZooKeeper实例的工厂类
 * 
 * @author James Quan
 * @version 2015年2月8日 下午12:39:17
 */
public class ZkClientFactoryBean extends AbstractFactoryBean<ZkClient> {

	private String zkServer = "localhost:2181";
	private int sessionTimeout = 60 * 1000;
	private int connectionTimeout = 180 * 1000;

	/**
	 * 返回zk服务器的连接字符串，缺省为localhost:2181
	 * 
	 * @return
	 */
	public String getZkServer() {
		return zkServer;
	}

	/**
	 * 设置zk服务器的连接字符串，缺省为localhost:2181
	 * 
	 * @param zkServer
	 */
	public void setZkServer(String zkServer) {
		this.zkServer = zkServer;
	}

	/**
	 * 返回zk session的过期时间，以秒为单位，缺省为60
	 * 
	 * @return
	 */
	public int getSessionTimeout() {
		return sessionTimeout / 1000;
	}

	/**
	 * 设置zk session的过期时间，以秒为单位，缺省为60
	 * 
	 * @param sessionTimeout
	 */
	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout * 1000;
	}

	/**
	 * 返回zkClient的连接超时时间，以秒为单位，缺省为180
	 * 
	 * @return
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * 设置zkClient的连接超时时间，以秒为单位，缺省为180
	 * 
	 * @param connectionTimeout
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	@Override
	public Class<?> getObjectType() {
		return ZkClient.class;
	}

	@Override
	protected ZkClient createInstance() throws Exception {
		ZkClient zk = new ZkClient(zkServer, sessionTimeout, connectionTimeout,
				new BytesPushThroughSerializer());
		return zk;
	}

}
