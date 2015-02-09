package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.commons.codec.DecoderState;
import org.jdfs.commons.codec.JdfsFileRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.storage.request.UpdateFileRequest;

/**
 * 用于对{@link UpdateFileRequest}进行解码的解码器
 * @author James Quan
 * @version 2015年1月31日 上午9:50:38
 */
public class UpdateFileRequestMessageDecoder extends JdfsFileRequestMessageDecoder<UpdateFileRequest> {
	 
	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_DATA_UPDATE;
	}

	@Override
	protected UpdateFileRequest createRequest(int code) {
		return new UpdateFileRequest();
	}

	@Override
	protected MessageDecoderResult decodeFileRequest(
			DecoderState<UpdateFileRequest> state, IoSession session,
			IoBuffer in) {
		UpdateFileRequest request = state.getRequest();
		if(state.getState() == 2) {
			if(in.remaining() < 16) {
				return MessageDecoderResult.NEED_DATA;
			}
			long size = in.getLong();
			long position = in.getLong();
			request.setSize(size);
			request.setPosition(position);
			state.toNextState();
		}
		if(!in.prefixedDataAvailable(4, maxDataSize)) {
			return MessageDecoderResult.NEED_DATA;
		}
		int l = in.getInt();
		byte[] data = new byte[l];
		in.get(data);
		request.setData(data);
		return MessageDecoderResult.OK;
	}	
}
