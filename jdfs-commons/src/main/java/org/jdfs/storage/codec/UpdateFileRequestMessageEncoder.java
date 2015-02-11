package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdfs.commons.codec.JdfsFileRequestMessageEncoder;
import org.jdfs.storage.request.UpdateFileRequest;

/**
 * 用于对{@link UpdateFileRequest}进行编码的编码器
 * 
 * @author James Quan
 * @version 2015年1月30日 下午3:24:34
 */
public class UpdateFileRequestMessageEncoder extends
		JdfsFileRequestMessageEncoder<UpdateFileRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			UpdateFileRequest message) {
		byte[] data = message.getData();
		int l = data == null ? 0 : data.length;
		return IoBuffer.allocate(36 + l, false);
	}
	
	@Override
	protected void encodeFileMessageData(UpdateFileRequest message,
			IoBuffer buffer, IoSession session, ProtocolEncoderOutput out)
			throws Exception {
		byte[] data = message.getData();
		buffer.putLong(message.getSize());	//size
		buffer.putLong(message.getPosition());	//position
		if(data != null && data.length > 0) {
			buffer.putInt(data.length);
			buffer.put(data);
		} else {
			buffer.putInt(0);
		}
	}

}
