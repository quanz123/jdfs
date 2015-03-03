package org.jdfs.tracker.request;

import org.jdfs.commons.request.JdfsFileRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 通知其他服务器删除文件信息的请求
 * @author James Quan
 * @version 2015年2月4日 下午5:24:47
 */
public class RemoveFileInfoSyncRequest extends JdfsFileRequest {

	/**
	 * 空白构造函数
	 */
	public RemoveFileInfoSyncRequest() {
		super();
		setCode(JdfsRequestConstants.REQUEST_SYNC_INFO_DELETE);
	}
	
}
