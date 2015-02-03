package org.jdfs.storage.request;

/**
 * 文件操作请求的基本信息
 * 
 * @author James Quan
 * @version 2015年1月29日 下午6:06:43
 */
public class FileRequest {
		
	private int code;
	private long id;
	
	public FileRequest() {
		super();
	}
	
	public FileRequest(int code, long id) {
		super();
		this.code = code;
		this.id = id;
	}

	/**
	 * 返回请求执行的功能
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 设置请求执行的功能
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * 返回待操作的文件的id
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * 设置待操作的文件的id
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}
}
