package org.jdfs.tracker.request;

import org.jdfs.commons.request.JdfsRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 用于返回文件信息结果的请求
 * @author James Quan
 * @version 2015年2月4日 下午6:15:19
 */
public class FileInfoResponse extends JdfsRequest {
	private long id;
	private long size;
	private long lastModified;
	private String name;

	/**
	 * 返回文件的id
	 * @return
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * 设置文件的id
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * 返回文件的大小
	 * 
	 * @return
	 */
	public long getSize() {
		return size;
	}

	/**
	 * 设置文件的大小
	 * 
	 * @param size
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * 返回文件的最后修改时间
	 * 
	 * @return
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * 设置文件的最后修改时间
	 * 
	 * @param lastModified
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * 返回文件的名字
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置文件的名字
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 空白构造函数
	 */
	public FileInfoResponse() {
		super(JdfsRequestConstants.REQUEST_INFO_RESULT);
	}

	/**
	 * 直接指定文件信息的构造函数
	 * 
	 * @param id
	 *            文件id
	 * @param name
	 *            文件名字
	 * @param size
	 *            文件大小
	 * @param lastModified
	 *            文件的最后修改时间
	 */
	public FileInfoResponse(long id, String name, long size,
			long lastModified) {
		super(JdfsRequestConstants.REQUEST_INFO_RESULT);
		this.id = id;
		this.name = name;
		this.size = size;
		this.lastModified = lastModified;
	}

}
