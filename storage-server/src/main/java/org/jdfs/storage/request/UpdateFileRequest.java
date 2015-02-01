package org.jdfs.storage.request;

/**
 * 保存文件内容的请求
 * 
 * @author James Quan
 * @version 2015年1月30日 上午11:07:16
 */
public class UpdateFileRequest extends FileRequest {
	private byte[] data;

	public int getLength() {
		return data == null ? 0 : data.length;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
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
	 * @param data
	 *            待写入的文件数据
	 */
	public UpdateFileRequest(long id, byte[] data) {
		super();
		setCode(FileRequest.REQUEST_UPDATE);
		setId(id);
		setData(data);
	}
}
