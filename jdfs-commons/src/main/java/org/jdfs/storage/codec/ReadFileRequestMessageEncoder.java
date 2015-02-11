package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdfs.commons.codec.JdfsFileRequestMessageEncoder;
import org.jdfs.storage.request.ReadFileRequest;

/**
 * 用于对{@link ReadFileRequest}进行编码的编码器
 * @author James Quan
 * @version 2015年2月4日 下午3:07:38
 */
public class ReadFileRequestMessageEncoder extends
		JdfsFileRequestMessageEncoder<ReadFileRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			ReadFileRequest message) {
		return IoBuffer.allocate(28, false);
	}
	
	@Override
	protected void encodeFileMessageData(ReadFileRequest message,
			IoBuffer buffer, IoSession session, ProtocolEncoderOutput out)
			throws Exception {
		buffer.putLong(message.getOffset());	
		buffer.putInt(message.getLength());	
	}

}
