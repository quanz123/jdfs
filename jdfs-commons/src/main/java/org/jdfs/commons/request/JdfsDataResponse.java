package org.jdfs.commons.request;

/**
 * 返回请求执行结果及结果数据的请求
 * 
 * @author James Quan
 * @version 2015年2月4日 上午10:15:47
 */
public class JdfsDataResponse extends JdfsRequest {
	private int status = JdfsRequestConstants.STATUS_OK;;
	private String message;
	private byte[] data;

	/**
	 * 返回执行的结果代码
	 * 
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置执行的结果代码
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 返回执行的结果信息
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 设置执行的结果信息
	 * 
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 返回执行的结果数据
	 * 
	 * @return
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * 设置执行的结果数据
	 * 
	 * @param data
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * 空白构造函数
	 */
	public JdfsDataResponse() {
		super(JdfsRequestConstants.REQUEST_DATA_RESULT);
	}

	/**
	 * 指定结果代码的构造函数
	 * 
	 * @param status
	 *            结果代码
	 */
	public JdfsDataResponse(int status) {
		this();
		setStatus(status);
	}

	/**
	 * 指定结果代码和信息的构造函数
	 * 
	 * @param status
	 *            结果代码
	 * @param message
	 *            结果信息
	 */
	public JdfsDataResponse(int status, String message) {
		this(status);
		setMessage(message);
	}

	/**
	 * 指定结果代码、信息及数据的构造函数
	 * 
	 * @param status
	 *            结果代码
	 * @param message
	 *            结果信息
	 * @param data
	 *            结果数据
	 */
	public JdfsDataResponse(int status, String message, byte[] data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
	}
}
