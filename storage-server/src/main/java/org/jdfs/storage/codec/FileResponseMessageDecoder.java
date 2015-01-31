package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.storage.request.FileRequest;
import org.jdfs.storage.request.FileResponse;

public class FileResponseMessageDecoder extends FileRequestMessageDecoder {

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
		FileResponse resp = new FileResponse(status);
		out.write(resp);
		return MessageDecoderResult.OK;
	}

}
