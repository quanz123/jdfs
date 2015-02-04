package org.jdfs.commons.request;

/**
 * 返回请求执行结果的请求
 * @author James Quan
 * @version 2015年2月4日 上午10:15:47
 */
public class JdfsStatusResponse extends JdfsRequest {
	private int status = JdfsRequestConstants.STATUS_OK;;
	private String message;
	
	/**
	 * 返回执行的结果代码
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置执行的结果代码
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 返回执行的结果信息
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 设置执行的结果信息
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 空白构造函数
	 */
	public JdfsStatusResponse() {
		super(JdfsRequestConstants.REQUEST_STATUS_RESULT);
	}

	/**
	 * 指定结果代码的构造函数
	 * @param status
	 */
	public JdfsStatusResponse(int status) {
		this();
		setStatus(status);
	}

	/**
	 * 指定结果代码和信息的构造函数
	 * @param status
	 * @param message
	 */
	public JdfsStatusResponse(int status, String message) {
		this(status);
		setMessage(message);
	}
}
