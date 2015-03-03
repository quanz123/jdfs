package org.jdfs.tracker.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.commons.codec.DecoderState;
import org.jdfs.commons.codec.JdfsFileRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.tracker.request.UpdateFileInfoRequest;
import org.jdfs.tracker.request.UpdateFileInfoSyncRequest;

/**
 * 用于对{@link UpdateFileInfoSyncRequest}进行解码的解码器
 * @author James Quan
 * @version 2015年1月31日 上午9:50:38
 */
public class UpdateFileSyncRequestMessageDecoder extends JdfsFileRequestMessageDecoder<UpdateFileInfoSyncRequest> {
	 
	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_SYNC_INFO_UPDATE;
	}

	@Override
	protected UpdateFileInfoSyncRequest createRequest(int batchId, int code) {
		UpdateFileInfoSyncRequest r = new UpdateFileInfoSyncRequest();
		r.setBatchId(batchId);
		return r;
	}

	@Override
	protected MessageDecoderResult decodeFileRequest(
			DecoderState<UpdateFileInfoSyncRequest> state, IoSession session,
			IoBuffer in) throws Exception{
		UpdateFileInfoSyncRequest request = state.getRequest();
		if(state.getState() == 2) {
			if(in.remaining() < 20) {
				return MessageDecoderResult.NEED_DATA;
			}
			int group = in.getInt();
			long size = in.getLong();
			long lastModified = in.getLong();
			request.setGroup(group);
			request.setSize(size);
			request.setLastModified(lastModified);
			state.toNextState();
		}
		if(!in.prefixedDataAvailable(4, maxDataSize)) {
			return MessageDecoderResult.NEED_DATA;
		}
		int l = in.getInt();
		byte[] data = new byte[l];
		in.get(data);
		String name = new String(data, "UTF-8");
		request.setName(name);
		return MessageDecoderResult.OK;
	}	
}
