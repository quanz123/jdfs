package org.jdfs.storage.request;

/**
 * 请求的处理结果
 * @author James Quan
 * @version 2015年1月30日 上午10:51:53
 */
public class FileResponse {
	private int status;
	
	public FileResponse() {
		super();
	}
	
	public FileResponse(int status) {
		super();
		this.status = status;
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
