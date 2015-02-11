package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.commons.codec.DecoderState;
import org.jdfs.commons.codec.JdfsFileRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.storage.request.ReadFileRequest;

/**
 * 用于对{@link ReadFileRequest}进行解码的解码器
 * 
 * @author James Quan
 * @version 2015年2月4日 下午3:14:08
 */
public class ReadFileRequestMessageDecoder extends
		JdfsFileRequestMessageDecoder<ReadFileRequest> {

	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_DATA_READ;
	}

	@Override
	protected ReadFileRequest createRequest(int batchId, int code) {
		ReadFileRequest r = new ReadFileRequest();
		r.setBatchId(batchId);
		return r;
	}

	@Override
	protected MessageDecoderResult decodeFileRequest(
			DecoderState<ReadFileRequest> state, IoSession session, IoBuffer in) {
		ReadFileRequest request = state.getRequest();
		if (in.remaining() < 12) {
			return MessageDecoderResult.NEED_DATA;
		}
		long offset = in.getLong();
		int length = in.getInt();
		request.setOffset(offset);
		request.setLength(length);
		return MessageDecoderResult.OK;
	}
}
