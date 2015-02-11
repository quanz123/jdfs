package org.jdfs.storage.codec;

import org.jdfs.commons.codec.JdfsFileRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.storage.request.RemoveFileRequest;

/**
 * 用于对{@link RemoveFileRequest}进行解码的解码器
 * @author James Quan
 * @version 2015年2月4日 下午3:11:47
 */
public class RemoveFileRequestMessageDecoder extends JdfsFileRequestMessageDecoder<RemoveFileRequest> {
	 
	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_DATA_DELETE;
	}

	@Override
	protected RemoveFileRequest createRequest(int batchId, int code) {
		RemoveFileRequest r = new RemoveFileRequest();
		r.setBatchId(batchId);
		return r;
	}
}
