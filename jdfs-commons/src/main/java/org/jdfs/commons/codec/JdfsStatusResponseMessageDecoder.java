package org.jdfs.commons.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;

/**
 * jdfs请求状态响应信息解码器的缺省实现
 * 
 * @author James Quan
 * @version 2015年2月4日 下午1:39:19
 */
public class JdfsStatusResponseMessageDecoder extends
		JdfsRequestMessageDecoder<JdfsStatusResponse> {

	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_STATUS_RESULT;
	}

	@Override
	protected JdfsStatusResponse createRequest(int batchId, int code) {
		JdfsStatusResponse r = new JdfsStatusResponse();
		r.setBatchId(batchId);
		return r;
	}

	@Override
	protected MessageDecoderResult decodeRequest(
			DecoderState<JdfsStatusResponse> state, IoSession session,
			IoBuffer in) throws Exception {
		JdfsStatusResponse request = state.getRequest();
		if (state.getState() == 1) {
			if (in.remaining() < 4) {
				return MessageDecoderResult.NEED_DATA;
			}
			int status = in.getInt();
			request.setStatus(status);
		}
		if (!in.prefixedDataAvailable(4, maxDataSize)) {
			return MessageDecoderResult.NEED_DATA;
		}
		int len = in.getInt();
		if (len > 0) {
			byte[] data = new byte[len];
			in.get(data);
			String msg = new String(data, "UTF-8");
			request.setMessage(msg);
		}
		return MessageDecoderResult.OK;
	}

}
