package org.jdfs.commons.request;

/**
 * 通告server状态的请求
 * 
 * @author James Quan
 * @version 2015年2月6日 上午10:20:15
 */
public class JdfsServerInfoRequest extends JdfsRequest {
	private byte[] hostAddress;
	private int hostPort;
	private int status;

	/**
	 * 返回服务器的对外服务地址
	 * @return
	 */
	public byte[] getHostAddress() {
		return hostAddress;
	}

	/**
	 * 设置服务器的对外服务地址
	 * @param hostAddress
	 */
	public void setHostAddress(byte[] hostAddress) {
		this.hostAddress = hostAddress;
	}

	/**
	 * 返回服务器的对外服务端口
	 * @return
	 */
	public int getHostPort() {
		return hostPort;
	}

	/**
	 * 设置服务器的对外服务端口
	 * @param hostPort
	 */
	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}

	/**
	 * 返回服务器的状态
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置服务器的状态
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 空白构造函数
	 */
	public JdfsServerInfoRequest() {
		super();
		setCode(JdfsRequestConstants.REQUEST_SERVER_INFO);
	}
}
