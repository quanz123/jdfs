package org.jdfs.storage.request;

import org.jdfs.commons.request.JdfsFileRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 保存文件内容的请求
 * 
 * @author James Quan
 * @version 2015年1月30日 上午11:07:16
 */
public class UpdateFileRequest extends JdfsFileRequest {
	private long size;
	private long position;	
	private byte[] data;

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
	 * 返回文件的数据
	 * @return
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * 设置文件的数据
	 * @param data
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
	/**
	 * 空白构造函数
	 */
	public UpdateFileRequest() {
		super(JdfsRequestConstants.REQUEST_DATA_UPDATE);
	}

	/**
	 * 指定待更新文件的构造函数
	 * 
	 * @param id
	 *            待更新文件的id
	 */
	public UpdateFileRequest(long id) {
		super(JdfsRequestConstants.REQUEST_DATA_UPDATE, id);
	}
}
