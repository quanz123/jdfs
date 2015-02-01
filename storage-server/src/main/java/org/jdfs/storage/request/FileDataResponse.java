package org.jdfs.storage.request;

/**
 * 文件数据请求的处理结果
 * 
 * @author James Quan
 * @version 2015年1月30日 上午10:51:53
 */
public class FileDataResponse extends FileResponse {
	private byte[] data;
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public int getLength() {
		return data == null ? 0 : data.length;
	}
	
	public FileDataResponse() {
		super();
		setCode(FileRequest.REQUEST_DATA_RESPONSE);
	}

	public FileDataResponse(int status, byte[] data) {
		this();
		setCode(FileRequest.REQUEST_DATA_RESPONSE);
		setStatus(status);
		setData(data);
	}

}
