package org.jdfs.tracker.codec;

import org.jdfs.commons.codec.JdfsFileRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.tracker.request.GetUploadServerRequest;

/**
 * 用于对{@link GetUploadServerRequest}进行解码的解码器
 * @author James Quan
 * @version 2015年2月8日 下午12:17:31
 */
public class GetUploadServerRequestMessageDecoder extends JdfsFileRequestMessageDecoder<GetUploadServerRequest> {
	 
	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_UPLOAD_SERVER;
	}

	@Override
	protected GetUploadServerRequest createRequest(int batchId, int code) {
		GetUploadServerRequest r = new GetUploadServerRequest();
		r.setBatchId(batchId);
		return r;
	}
}
