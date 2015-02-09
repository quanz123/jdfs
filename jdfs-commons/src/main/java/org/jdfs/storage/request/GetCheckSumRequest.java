package org.jdfs.storage.request;

import org.jdfs.commons.request.JdfsFileRequest;
import org.jdfs.commons.request.JdfsRequestConstants;

/**
 * 返回文件校验值的请求
 * @author James Quan
 * @version 2015年2月8日 下午1:49:09
 */
public class GetCheckSumRequest extends JdfsFileRequest {

	/**
	 * 空白构造函数
	 */
	public GetCheckSumRequest() {
		super(JdfsRequestConstants.REQUEST_DATA_CHECKSUM);
	}

	/**
	 * 指定文件id的构造函数
	 * @param id 待读取的文件的id
	 * @param offset 文件内容读取的起始位置
	 * @param length 文件内容读取的长度，如果为-1表示读取所有内容
	 */
	public GetCheckSumRequest(long id) {
		super(JdfsRequestConstants.REQUEST_DATA_CHECKSUM, id);
	}
	
}
