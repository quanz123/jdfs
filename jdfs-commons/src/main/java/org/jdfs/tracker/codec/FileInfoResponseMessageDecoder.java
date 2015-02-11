package org.jdfs.tracker.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.commons.codec.DecoderState;
import org.jdfs.commons.codec.JdfsRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.tracker.request.FileInfoResponse;
import org.jdfs.tracker.request.UpdateFileInfoRequest;

/**
 * 用于对{@link UpdateFileInfoRequest}进行解码的解码器
 * 
 * @author James Quan
 * @version 2015年1月31日 上午9:50:38
 */
public class FileInfoResponseMessageDecoder extends
		JdfsRequestMessageDecoder<FileInfoResponse> {

	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_INFO_RESULT;
	}

	@Override
	protected FileInfoResponse createRequest(int batchId, int code) {
		FileInfoResponse f = new FileInfoResponse();
		f.setBatchId(batchId);
		return f;
	}

	@Override
	protected MessageDecoderResult decodeRequest(
			DecoderState<FileInfoResponse> state, IoSession session, IoBuffer in)
			throws Exception {
		FileInfoResponse request = state.getRequest();
		if (state.getState() == 1) {
			if (in.remaining() < 28) {
				return MessageDecoderResult.NEED_DATA;
			}
			int status = in.getInt();
			long id = in.getLong();
			long size = in.getLong();
			long lastModified = in.getLong();
			request.setStatus(status);
			request.setId(id);
			request.setSize(size);
			request.setLastModified(lastModified);
			state.toNextState();
		}
		if (!in.prefixedDataAvailable(4, maxDataSize)) {
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
