package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.storage.request.FileRequest;
import org.jdfs.storage.request.ReadFileRequest;

/**
 * 
 * @author James Quan
 * @version 2015年1月31日 上午9:50:38
 */
public class ReadFileRequestMessageDecoder extends FileRequestMessageDecoder {

	@Override
	protected boolean support(int code) {
		return code == FileRequest.REQUEST_READ;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if (in.remaining() < 36) {
			return MessageDecoderResult.NEED_DATA;
		}
		int code = in.getInt();
		long id = in.getLong();
		long start = in.getLong();
		long end = in.getLong();
		long length = in.getLong();
		ReadFileRequest request = new ReadFileRequest(id, start, end, length);
		out.write(request);
		return MessageDecoderResult.OK;
	}
}
