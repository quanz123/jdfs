package org.jdfs.storage.request;

import org.jdfs.commons.request.JdfsFileRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 读取文件内容的请求
 * 
 * @author James Quan
 * @version 2015年1月30日 上午11:06:30
 */
public class ReadFileRequest extends JdfsFileRequest {
	private long offset = 0;
	private int length = -1;

	/**
	 * 返回文件内容读取的起始位置
	 * 
	 * @return
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * 设置文件内容读取的起始位置
	 * 
	 * @param offset
	 */
	public void setOffset(long offset) {
		this.offset = offset;
	}

	/**
	 * 返回文件内容的读取长度，如果为-1表示读取所有内容
	 * 
	 * @return
	 */
	public int getLength() {
		return length;
	}

	/**
	 * 设置文件内容的读取长度，如果为-1表示读取所有内容
	 * 
	 * @param length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * 空白构造函数
	 */
	public ReadFileRequest() {
		super(JdfsRequestConstants.REQUEST_DATA_READ);
	}

	/**
	 * 详细指定文件id及读取设置的构造函数
	 * @param id 待读取的文件的id
	 * @param offset 文件内容读取的起始位置
	 * @param length 文件内容读取的长度，如果为-1表示读取所有内容
	 */
	public ReadFileRequest(long id, long offset, int length) {
		super(JdfsRequestConstants.REQUEST_DATA_READ, id);
		setOffset(offset);
		setLength(length);
	}
	
	/**
	 * 指定文件id的构造函数
	 * @param id
	 */
	public ReadFileRequest(long id) {
		super(JdfsRequestConstants.REQUEST_DATA_READ, id);
	}

}
