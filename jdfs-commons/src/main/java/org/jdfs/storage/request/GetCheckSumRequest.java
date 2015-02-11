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
		super();
		setCode(JdfsRequestConstants.REQUEST_DATA_CHECKSUM);
	}

}
