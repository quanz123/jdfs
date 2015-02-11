package org.jdfs.commons.request;

/**
 * 功能请求的基本定义
 * 
 * @author James Quan
 * @version 2015年2月3日 下午5:39:36
 */
public class JdfsRequest {
	private int batchId;
	private int code;

	/**
	 * 空白构造函数
	 */
	public JdfsRequest() {
		super();
	}

	/**
	 * 指定分组id及请求代码的构造函数
	 * 
	 * @param batchId
	 *            请求所隶属的分组的id
	 * @param code
	 *            请求的功能代码
	 */
	public JdfsRequest(int batchId, int code) {
		super();
		this.batchId = batchId;
		this.code = code;
	}

	/**
	 * 返回请求所隶属的分组的id
	 * 
	 * @return
	 */
	public int getBatchId() {
		return batchId;
	}

	/**
	 * 设置请求所隶属的分组的id
	 * 
	 * @param batchId
	 */
	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}

	/**
	 * 返回请求的功能代码
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 设置请求的功能代码
	 * 
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}

}
