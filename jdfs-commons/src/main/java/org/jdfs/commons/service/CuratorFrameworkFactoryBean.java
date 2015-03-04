package org.jdfs.commons.service;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class CuratorFrameworkFactoryBean extends
		AbstractFactoryBean<CuratorFramework> {

	private String connectString = "localhost:2181";
	private int sessionTimeout = 60 * 1000;
	private int connectionTimeout = 15 * 1000;
	private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

	/**
	 * 返回zk服务器的连接字符串，缺省为localhost:2181
	 * 
	 * @return
	 */
	public String getConnectString() {
		return connectString;
	}

	/**
	 * 设置zk服务器的连接字符串，缺省为localhost:2181
	 * 
	 * @param connectString
	 */
	public void setConnectString(String connectString) {
		this.connectString = connectString;
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
	 * 返回zkClient的连接超时时间，以秒为单位，缺省为15
	 * 
	 * @return
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * 设置zkClient的连接超时时间，以秒为单位，缺省为15
	 * 
	 * @param connectionTimeout
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	public void setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	@Override
	public Class<?> getObjectType() {
		return CuratorFramework.class;
	}

	@Override
	protected CuratorFramework createInstance() throws Exception {
		CuratorFramework curator = CuratorFrameworkFactory.newClient(connectString, sessionTimeout,
				connectionTimeout, retryPolicy);
		curator.start();
		return curator;
	}

	@Override
	protected void destroyInstance(CuratorFramework instance) throws Exception {
		instance.close();
	}
}
