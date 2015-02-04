package org.jdfs.tracker.request;

import org.jdfs.commons.request.JdfsFileRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 删除文件信息的请求
 * @author James Quan
 * @version 2015年2月4日 下午5:24:47
 */
public class RemoveFileInfoRequest extends JdfsFileRequest {

	/**
	 * 空白构造函数
	 */
	public RemoveFileInfoRequest() {
		super(JdfsRequestConstants.REQUEST_INFO_DELETE);
	}
	
	/**
	 * 指定待删除文件id的构造函数
	 * 
	 * @param id
	 *            文件id
	 */
	public RemoveFileInfoRequest(long id) {
		super(JdfsRequestConstants.REQUEST_INFO_DELETE, id);
	}
}
