package org.jdfs.storage.request;

/**
 * 删除文件的请求
 * 
 * @author James Quan
 * @version 2015年1月30日 上午11:06:42
 */
public class RemoveFileRequest extends FileRequest {
	/**
	 * 构造空白删除请求的构造函数
	 */
	public RemoveFileRequest() {
		super();
		setCode(FileRequest.REQUEST_DELETE);
	}

	/**
	 * 指定待删除文件的构造函数
	 * 
	 * @param id
	 *            待删除文件的id
	 */
	public RemoveFileRequest(long id) {
		super();
		setCode(FileRequest.REQUEST_DELETE);
		setId(id);
	}

}
