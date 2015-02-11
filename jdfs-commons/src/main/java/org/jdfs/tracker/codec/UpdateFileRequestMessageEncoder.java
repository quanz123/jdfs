package org.jdfs.tracker.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdfs.commons.codec.JdfsFileRequestMessageEncoder;
import org.jdfs.tracker.request.UpdateFileInfoRequest;
import org.springframework.util.StringUtils;

/**
 * 用于对{@link UpdateFileInfoRequest}进行编码的编码器
 * 
 * @author James Quan
 * @version 2015年1月30日 下午3:24:34
 */
public class UpdateFileRequestMessageEncoder extends
		JdfsFileRequestMessageEncoder<UpdateFileInfoRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			UpdateFileInfoRequest message) {
		String name = message.getName();
		if(StringUtils.isEmpty(name)) {
			return IoBuffer.allocate(36, false);
		} else {
			IoBuffer buf = IoBuffer.allocate(36 + name.length() * 3, false);
			buf.setAutoExpand(true);
			return buf;
		}
	}
	
	@Override
	protected void encodeFileMessageData(UpdateFileInfoRequest message,
			IoBuffer buffer, IoSession session, ProtocolEncoderOutput out)
			throws Exception {
		buffer.putLong(message.getSize());	
		buffer.putLong(message.getLastModified());	
		String name = message.getName();
		if(StringUtils.isEmpty(name)) {
			buffer.putInt(0);
		} else {
			byte[] data = name.getBytes("UTF-8");
			buffer.putInt(data.length);
			buffer.put(data);
		}
	}

}
