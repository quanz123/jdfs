package org.jdfs.tracker.codec;

import org.jdfs.commons.codec.JdfsFileRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.tracker.request.RemoveFileInfoRequest;

/**
 * 用于对{@link RemoveFileRequest}进行解码的解码器
 * @author James Quan
 * @version 2015年2月4日 下午3:11:47
 */
public class RemoveFileRequestMessageDecoder extends JdfsFileRequestMessageDecoder<RemoveFileInfoRequest> {
	 
	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_INFO_DELETE;
	}

	@Override
	protected RemoveFileInfoRequest createRequest(int batchId, int code) {
		RemoveFileInfoRequest r = new RemoveFileInfoRequest();
		r.setBatchId(batchId);
		return r;
	}
}
