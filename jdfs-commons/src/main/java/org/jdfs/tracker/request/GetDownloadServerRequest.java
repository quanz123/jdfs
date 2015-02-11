package org.jdfs.tracker.request;

import org.jdfs.commons.request.JdfsFileRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 返回数据下载服务器地址的请求
 * @author James Quan
 * @version 2015年2月8日 上午11:48:31
 */
public class GetDownloadServerRequest extends JdfsFileRequest {

	/**
	 * 空白构造函数
	 */
	public GetDownloadServerRequest() {
		super();
		setCode(JdfsRequestConstants.REQUEST_DOWNLOAD_SERVER);
	}
	
}
