package org.jdfs.storage.request;

/**
 * 读取文件内容的请求
 * 
 * @author James Quan
 * @version 2015年1月30日 上午11:06:30
 */
public class ReadFileRequest extends FileRequest {
	private long start = 0;
	private long end = -1;
	private long length = -1;

	/**
	 * 返回文件内容读取的起始位置
	 * 
	 * @return
	 */
	public long getStart() {
		return start;
	}

	/**
	 * 设置文件内容读取的起始位置
	 * 
	 * @param start
	 */
	public void setStart(long start) {
		this.start = start;
	}

	/**
	 * 返回文件内容读取的结束位置，如果为-1表示读取所有内容
	 * 
	 * @return
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * 设置文件内容读取的结束位置，如果为-1表示读取所有内容
	 * 
	 * @param end
	 */
	public void setEnd(long end) {
		this.end = end;
	}

	/**
	 * 返回文件内容的读取长度，如果为-1表示读取所有内容
	 * 
	 * @return
	 */
	public long getLength() {
		return length;
	}

	/**
	 * 设置文件内容的读取长度，如果为-1表示读取所有内容
	 * 
	 * @param length
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * 空白构造函数
	 */
	public ReadFileRequest() {
		super();
		setCode(FileRequest.REQUEST_READ);
	}

	/**
	 * 指定文件id的构造函数
	 * @param id
	 */
	public ReadFileRequest(long id) {
		this(id, 0, -1, -1);
	}

	/**
	 * 详细指定文件id及读取设置的构造函数
	 * @param id 待读取的文件的id
	 * @param start 文件内容读取的起始位置
	 * @param end 文件内容读取的结束位置，如果为-1表示读取所有内容
	 * @param length 文件内容读取的长度，如果为-1表示读取所有内容
	 */
	public ReadFileRequest(long id, long start, long end, long length) {
		super();
		setCode(FileRequest.REQUEST_READ);
		setId(id);
		setStart(Math.max(0, start));
		setEnd(end < 0 ? -1 : end);
		setLength(length < 0 ? -1 : length);
	}
}
