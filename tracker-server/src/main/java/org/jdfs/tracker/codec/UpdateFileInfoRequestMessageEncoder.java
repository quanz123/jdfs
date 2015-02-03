package org.jdfs.tracker.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdfs.tracker.request.UpdateFileInfoRequest;
import org.springframework.util.StringUtils;

/**
 * 保存文件信息请求的编码器
 * @author James Quan
 * @version 2015年2月3日 下午2:03:56
 */
public class UpdateFileInfoRequestMessageEncoder extends
		FileInfoRequestMessageEncoder<UpdateFileInfoRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			UpdateFileInfoRequest message) throws Exception {
		String name = message.getName();
		byte[] data = StringUtils.isEmpty(name) ? null : name.getBytes("UTF-8");
		int len = data == null ? 32 : 32 + data.length;
		return IoBuffer.allocate(len, false);
	}

	@Override
	protected void encodeMessageData(UpdateFileInfoRequest message, IoBuffer buffer,
			IoSession session, ProtocolEncoderOutput out) throws Exception {
		buffer.putLong(message.getSize());
		buffer.putLong(message.getLastModified());
		String name = message.getName();
		byte[] data = StringUtils.isEmpty(name) ? null : name.getBytes("UTF-8");
		if(data == null || data.length == 0) {
			buffer.putInt(0);
		} else {
			buffer.putInt(data.length);
			buffer.put(data);
		}
	}
}
