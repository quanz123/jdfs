package org.jdfs.commons.request;

/**
 * 对文件进行处理的Jdfs请求
 * 
 * @author James Quan
 * @version 2015年2月3日 下午9:47:42
 */
public class JdfsFileRequest extends JdfsRequest {

	private long id;

	/**
	 * 空白构造函数
	 */
	public JdfsFileRequest() {
		super();
	}

	/**
	 * 直接指定请求分组id及请求代码的构造函数
	 * 
	 * @param batchId
	 *            请求所隶属的分组的id
	 * @param code
	 *            请求的功能代码
	 */
	public JdfsFileRequest(int batchId, int code) {
		super(batchId, code);
	}

	/**
	 * 直接指定请求分组id、请求代码和所处理文件的id的构造函数
	 * 
	 * @param batchId
	 *            请求所隶属的分组的id
	 * @param code
	 *            请求的功能代码
	 * @param id
	 *            所处理的文件的id
	 */
	public JdfsFileRequest(int batchId, int code, long id) {
		super(batchId, code);
		this.id = id;
	}

	/**
	 * 返回所处理的文件的id
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * 设置所处理的文件的id
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}
}
