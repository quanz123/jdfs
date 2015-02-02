package org.jdfs.storage.request;

/**
 * 文件范围信息对象，用于会话中保存处理信息
 * 
 * @author James Quan
 * @version 2015年2月2日 下午4:59:14
 */
public class FileRange {
	private long id;
	private long size;
	private long position;
	private int remainingBytes;

	public FileRange() {
		super();
	}

	public FileRange(long id, long size, long position, int remainingBytes) {
		super();
		this.id = id;
		this.size = size;
		this.position = position;
		this.remainingBytes = remainingBytes;
	}

	/**
	 * 返回文件的id
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * 设置文件的id
	 * 
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
	 * 返回文件数据的写入位置
	 * 
	 * @return
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * 设置文件数据的写入位置
	 * 
	 * @param position
	 */
	public void setPosition(long position) {
		this.position = position;
	}

	/**
	 * 返回尚未读取的数据大小
	 * 
	 * @return
	 */
	public int getRemainingBytes() {
		return remainingBytes;
	}

	/**
	 * 设置尚未读取的数据大小
	 * 
	 * @param remainingBytes
	 */
	public void setRemainingBytes(int remainingBytes) {
		this.remainingBytes = remainingBytes;
	}

}
