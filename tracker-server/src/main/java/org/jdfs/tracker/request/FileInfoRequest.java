package org.jdfs.tracker.request;

/**
 * 文件信息操作请求
 * @author James Quan
 * @version 2015年2月3日 上午11:01:02
 */
public class FileInfoRequest {

	/**
	 * 写入文件数据的请求
	 */
	public static final int REQUEST_UPDATE = 1;
	/**
	 * 删除文件数据的请求
	 */
	public static final int REQUEST_DELETE = 2;
	/**
	 * 读取文件数据的请求
	 */
	public static final int REQUEST_READ = 3;
	/**
	 * 文件请求的处理结果
	 */
	public static final int REQUEST_RESPONSE = 10000;
	
	
	private int code;
	private long id;
	
	public FileInfoRequest() {
		super();
	}
	
	public FileInfoRequest(int code, long id) {
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
