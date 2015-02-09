package org.jdfs.tracker.service;

import java.io.Serializable;

import org.joda.time.DateTime;

/**
 * 文件信息
 * 
 * @author James Quan
 * @version 2015年2月3日 上午9:42:57
 */
public class FileInfo implements Serializable {
	private long id;
	private String name;
	private long size;
	private DateTime lastModified;
	private String group;

	public FileInfo() {
		super();
	}

	public FileInfo(FileInfo file) {
		super();
		id = file.getId();
		name = file.getName();
		size = file.getSize();
		lastModified = file.getLastModified();
		group = file.getGroup();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public DateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(DateTime lastModified) {
		this.lastModified = lastModified;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
