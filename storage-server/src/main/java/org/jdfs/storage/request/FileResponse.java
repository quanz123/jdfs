package org.jdfs.storage.request;

/**
 * 请求的处理结果
 * @author James Quan
 * @version 2015年1月30日 上午10:51:53
 */
public class FileResponse {
	private int code;
	private int status;
	
	public FileResponse() {
		super();
	}
	
	public FileResponse(int code, int status) {
		super();
		this.status = status;
	}

	/**
	 * 返回结果类型
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 设置结果类型
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}
	
	/**
	 * 返回请求的结果代码
	 * @return
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * 设置请求的结果代码
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
}
