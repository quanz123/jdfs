package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdfs.storage.request.UpdateFileRequest;

/**
 * 用于对{@link UpdateFileRequest}进行编码的编码器
 * 
 * @author James Quan
 * @version 2015年1月30日 下午3:24:34
 */
public class UpdateFileRequestMessageEncoder extends
		FileRequestMessageEncoder<UpdateFileRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			UpdateFileRequest message) {
		return IoBuffer.allocate(16 + message.getLength(), false);
	}
	
	@Override
	protected void encodeMessageData(UpdateFileRequest message,
			IoBuffer buffer, IoSession session, ProtocolEncoderOutput out)
			throws Exception {
		int length = message.getLength();
		buffer.putInt(length);
		if(length > 0) {
			buffer.put(message.getData());
		}
	}

}
