package org.jdfs.commons.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.springframework.util.StringUtils;

/**
 * jdfs请求状态响应信息编码器的缺省实现
 * @author James Quan
 * @version 2015年2月4日 上午11:02:29
 */
public class JdfsStatusResponseMessageEncoder extends JdfsRequestMessageEncoder<JdfsStatusResponse> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			JdfsStatusResponse message) throws Exception{
		String msg = message.getMessage(); 
		IoBuffer buf;
		if(StringUtils.isEmpty(msg)) {
			buf =  IoBuffer.allocate(12, false);
		} else {
			buf =  IoBuffer.allocate(12 + msg.length() * 3, false);
			buf.setAutoExpand(true);
		}
		return buf;
	}
	
	@Override
	protected void encodeMessageData(JdfsStatusResponse message,
			IoBuffer buffer, IoSession session, ProtocolEncoderOutput out)
			throws Exception {
		buffer.putInt(message.getStatus());
		putString(message.getMessage(), buffer);
	}

}
