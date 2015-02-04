package org.jdfs.tracker.request;

import org.jdfs.commons.request.JdfsFileRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 读取文件信息的请求
 * @author James Quan
 * @version 2015年2月4日 下午5:24:47
 */
public class ReadFileInfoRequest extends JdfsFileRequest {

	/**
	 * 空白构造函数
	 */
	public ReadFileInfoRequest() {
		super(JdfsRequestConstants.REQUEST_INFO_READ);
	}
	
	/**
	 * 指定待读取文件id的构造函数
	 * 
	 * @param id
	 *            文件id
	 */
	public ReadFileInfoRequest(long id) {
		super(JdfsRequestConstants.REQUEST_INFO_READ, id);
	}
}
