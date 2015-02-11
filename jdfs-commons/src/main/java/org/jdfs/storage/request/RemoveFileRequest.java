package org.jdfs.storage.request;

import org.jdfs.commons.request.JdfsFileRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 删除文件的请求
 * 
 * @author James Quan
 * @version 2015年1月30日 上午11:06:42
 */
public class RemoveFileRequest extends JdfsFileRequest {

	/**
	 * 空白构造函数
	 */
	public RemoveFileRequest() {
		super();
		setCode(JdfsRequestConstants.REQUEST_DATA_DELETE);
	}
}
