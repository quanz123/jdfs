package org.jdfs.tracker.request;

/**
 * 保存文件信息的请求
 * 
 * @author James Quan
 * @version 2015年2月3日 上午11:02:13
 */
public class UpdateFileInfoRequest extends FileInfoRequest {
	private long size;
	private long lastModified;
	private String name;

	public UpdateFileInfoRequest() {
		super();
		setCode(FileInfoRequest.REQUEST_UPDATE);
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
