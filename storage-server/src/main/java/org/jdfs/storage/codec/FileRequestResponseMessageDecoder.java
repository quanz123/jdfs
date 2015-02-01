package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.storage.request.FileRequest;
import org.jdfs.storage.request.FileRequestResponse;

public class FileRequestResponseMessageDecoder extends FileRequestMessageDecoder {

	@Override
	protected boolean support(int code) {
		return code == FileRequest.REQUEST_RESPONSE;
	}
	
	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if(in.remaining() <8 ){
			return MessageDecoderResult.NEED_DATA;
		}
		int code = in.getInt();
		int status = in.getInt();
		FileRequestResponse resp = new FileRequestResponse(status);
		out.write(resp);
		return MessageDecoderResult.OK;
	}

}
