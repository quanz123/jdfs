package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdfs.storage.request.ReadFileRequest;

/**
 * 
 * @author James Quan
 * @version 2015年1月31日 上午9:53:05
 */
public class ReadFileRequestMessageEncoder extends
		FileRequestMessageEncoder<ReadFileRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session, ReadFileRequest message) {
		return IoBuffer.allocate(36, false);
	}

	@Override
	protected void encodeMessageData(ReadFileRequest message, IoBuffer buffer,
			IoSession session, ProtocolEncoderOutput out) throws Exception {
		buffer.putLong(message.getStart());
		buffer.putLong(message.getEnd());
		buffer.putLong(message.getLength());
	}
}
