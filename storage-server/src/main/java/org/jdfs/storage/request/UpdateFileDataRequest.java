package org.jdfs.storage.request;

/**
 * 写入文件数据请求
 * @author James Quan
 * @version 2015年2月2日 下午4:56:09
 */
public class UpdateFileDataRequest {
	private long id;
	private long size;
	private long position;
	private byte[] data;
	private boolean sendResponse;

	public UpdateFileDataRequest() {
		super();
	}

	public UpdateFileDataRequest(long id, long size, long position, byte[] data) {
		super();
		this.id = id;
		this.size = size;
		this.position = position;
		this.data = data;
	}

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
	 * @return
	 */
	public long getSize() {
		return size;
	}
	
	/**
	 * 设置文件的大小
	 * @param size
	 */
	public void setSize(long size) {
		this.size = size;
	}
	
	/**
	 * 返回文件数据的写入位置
	 * @return
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * 设置文件数据的写入位置
	 * @param position
	 */
	public void setPosition(long position) {
		this.position = position;
	}

	/**
	 * 返回待写入的文件数据
	 * @return
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * 设置待写入的文件数据
	 * @param data
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
	/**
	 * 返回请求处理完毕后是否需要发送响应，缺省为{@code false}，不发送
	 * @return
	 */
	public boolean isSendResponse() {
		return sendResponse;
	}
	
	/**
	 * 设置请求处理完毕后是否需要发送响应，缺省为{@code false}，不发送
	 * @param sendResponse
	 */
	public void setSendResponse(boolean sendResponse) {
		this.sendResponse = sendResponse;
	}
}
