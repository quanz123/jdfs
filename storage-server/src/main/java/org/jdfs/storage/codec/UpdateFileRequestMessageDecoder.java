package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.storage.request.FileRequest;

/**
 * 
 * @author James Quan
 * @version 2015年1月31日 上午9:50:38
 */
public class UpdateFileRequestMessageDecoder extends FileRequestMessageDecoder {
	
	@Override
	protected boolean support(int code) {
		return  code == FileRequest.REQUEST_UPDATE;
	}
	
	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
