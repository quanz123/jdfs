package org.jdfs.tracker.codec;

import org.jdfs.commons.codec.JdfsFileRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.tracker.request.RemoveFileInfoSyncRequest;

/**
 * 用于对{@link RemoveFileSyncRequest}进行解码的解码器
 * @author James Quan
 * @version 2015年2月4日 下午3:11:47
 */
public class RemoveFileSyncRequestMessageDecoder extends JdfsFileRequestMessageDecoder<RemoveFileInfoSyncRequest> {
	 
	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_SYNC_INFO_DELETE;
	}

	@Override
	protected RemoveFileInfoSyncRequest createRequest(int batchId, int code) {
		RemoveFileInfoSyncRequest r = new RemoveFileInfoSyncRequest();
		r.setBatchId(batchId);
		return r;
	}
}
