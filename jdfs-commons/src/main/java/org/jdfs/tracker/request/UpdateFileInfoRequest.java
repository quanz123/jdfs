package org.jdfs.tracker.request;

import org.jdfs.commons.request.JdfsFileRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 保存文件信息的请求
 * 
 * @author James Quan
 * @version 2015年2月3日 上午11:02:13
 */
public class UpdateFileInfoRequest extends JdfsFileRequest {
	private long size;
	private long lastModified;
	private String name;

	/**
	 * 空白构造函数
	 */
	public UpdateFileInfoRequest() {
		super();
		setCode(JdfsRequestConstants.REQUEST_INFO_UPDATE);
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
}
