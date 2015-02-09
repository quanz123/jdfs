package org.jdfs.tracker.request;

import org.jdfs.commons.request.JdfsFileRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 返回数据上传服务器地址的请求
 * @author James Quan
 * @version 2015年2月8日 上午11:48:31
 */
public class GetUploadServerRequest extends JdfsFileRequest {

	/**
	 * 空白构造函数
	 */
	public GetUploadServerRequest() {
		super(JdfsRequestConstants.REQUEST_UPLOAD_SERVER);
	}
	
	/**
	 * 指定待上传文件id的构造函数
	 * 
	 * @param id
	 *            文件id
	 */
	public GetUploadServerRequest(long id) {
		super(JdfsRequestConstants.REQUEST_UPLOAD_SERVER, id);
	}
}
