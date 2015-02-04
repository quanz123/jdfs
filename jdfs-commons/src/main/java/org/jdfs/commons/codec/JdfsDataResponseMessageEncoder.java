package org.jdfs.commons.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdfs.commons.request.JdfsDataResponse;
import org.springframework.util.StringUtils;

/**
 * jdfs请求信息编码器的缺省实现
 * @author James Quan
 * @version 2015年2月4日 上午11:02:29
 */
public class JdfsDataResponseMessageEncoder extends JdfsRequestMessageEncoder<JdfsDataResponse> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			JdfsDataResponse message) throws Exception{
		int l = 16;
		String msg = message.getMessage();
		if(!StringUtils.isEmpty(msg)) {
			l += msg.length() * 3;
		}
		byte[] data = message.getData();
		if(data != null && data.length > 0) {
			l += data.length;
		}
		IoBuffer buf;
		if(l == 16) {
			buf =  IoBuffer.allocate(l, false);
		} else {
			buf =  IoBuffer.allocate(l, false);
			buf.setAutoExpand(true);
		}
		return buf;
	}
	
	@Override
	protected void encodeMessageData(JdfsDataResponse message,
			IoBuffer buffer, IoSession session, ProtocolEncoderOutput out)
			throws Exception {
		buffer.putInt(message.getStatus());
		putString(message.getMessage(), buffer);
		byte[] data = message.getData();
		if(data != null && data.length > 0) {
			buffer.putInt(data.length);
			buffer.put(data);
		} else {
			buffer.putInt(0);
		}
	}

}
