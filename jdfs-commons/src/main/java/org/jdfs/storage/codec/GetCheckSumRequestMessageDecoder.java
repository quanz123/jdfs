package org.jdfs.storage.codec;

import org.jdfs.commons.codec.JdfsFileRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.storage.request.GetCheckSumRequest;

/**
 * 用于对{@link GetCheckSumRequest}进行解码的解码器
 * @author James Quan
 * @version 2015年2月8日 下午12:17:31
 */
public class GetCheckSumRequestMessageDecoder extends JdfsFileRequestMessageDecoder<GetCheckSumRequest> {
	 
	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_DATA_CHECKSUM;
	}

	@Override
	protected GetCheckSumRequest createRequest(int batchId, int code) {
		GetCheckSumRequest r = new GetCheckSumRequest();
		r.setBatchId(batchId);
		return r;
	}
}
