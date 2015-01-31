package org.jdfs.storage.request;

/**
 * 保存文件内容的请求
 * @author James Quan
 * @version 2015年1月30日 上午11:07:16
 */
public class UpdateFileRequest extends FileRequest{

	/**
	 * 空白构造函数
	 */
	public UpdateFileRequest() {
		super();
		setCode(FileRequest.REQUEST_UPDATE);
	}

	/**
	 * 指定待更新文件的构造函数
	 * 
	 * @param id
	 *            待更新文件的id
	 */
	public UpdateFileRequest(long id) {
		super();
		setCode(FileRequest.REQUEST_UPDATE);
		setId(id);
	}
}
