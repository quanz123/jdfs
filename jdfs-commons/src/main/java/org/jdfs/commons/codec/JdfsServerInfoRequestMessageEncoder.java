package org.jdfs.commons.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdfs.commons.request.JdfsServerInfoRequest;

/**
 * server状态通告请求编码器
 * @author James Quan
 * @version 2015年2月6日 上午10:51:19
 */
public class JdfsServerInfoRequestMessageEncoder extends JdfsRequestMessageEncoder<JdfsServerInfoRequest> {
	private boolean ipv6 = false;
	
	/**
	 * 返回是否使用ipv6，缺省为{@code false}
	 * @return
	 */
	public boolean isIpv6() {
		return ipv6;
	}
	
	/**
	 * 设置是否使用ipv6，缺省为{@code false}
	 * @param ipv6
	 */
	public void setIpv6(boolean ipv6) {
		this.ipv6 = ipv6;
	}
	
	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			JdfsServerInfoRequest message) throws Exception {
		int l = 16 + (ipv6 ? 16  : 4);
		return IoBuffer.allocate(l, false);
	}

	@Override
	protected void encodeMessageData(JdfsServerInfoRequest message,
			IoBuffer buffer, IoSession session, ProtocolEncoderOutput out)
			throws Exception {
		buffer.put(message.getHostAddress());
		buffer.putInt(message.getHostPort());
		buffer.putInt(message.getStatus());
	}
	
}
