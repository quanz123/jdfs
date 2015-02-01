package org.jdfs.storage.request;

/**
 * 文件处理请求的处理结果
 * 
 * @author James Quan
 * @version 2015年1月30日 上午10:51:53
 */
public class FileRequestResponse extends FileResponse {

	public FileRequestResponse() {
		super();
		setCode(FileRequest.REQUEST_RESPONSE);
	}

	public FileRequestResponse(int status) {
		this();
		setCode(FileRequest.REQUEST_RESPONSE);
		setStatus(status);
	}

}
