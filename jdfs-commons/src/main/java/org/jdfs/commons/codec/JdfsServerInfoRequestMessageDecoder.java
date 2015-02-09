package org.jdfs.commons.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsServerInfoRequest;

/**
 *  server状态通告请求解码器的缺省实现
 * 
 * @author James Quan
 * @version 2015年2月4日 下午1:39:19
 */
public class JdfsServerInfoRequestMessageDecoder extends
		JdfsRequestMessageDecoder<JdfsServerInfoRequest> {

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
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_STATUS_RESULT;
	}

	@Override
	protected JdfsServerInfoRequest createRequest(int code) {
		return new JdfsServerInfoRequest();
	}

	@Override
	protected MessageDecoderResult decodeRequest(
			DecoderState<JdfsServerInfoRequest> state, IoSession session,
			IoBuffer in) throws Exception {
		int l = 8 + (ipv6 ? 16  : 4);
		if(in.remaining() < l) {
			return MessageDecoderResult.NEED_DATA;
		}
		byte[] addr = ipv6 ? new byte[16] : new byte[4];
		in.get(addr);
		int port = in.getInt();
		int status = in.getInt();		
		JdfsServerInfoRequest request = state.getRequest();
		request.setHostAddress(addr);
		request.setHostPort(port);
		request.setStatus(status);
		return MessageDecoderResult.OK;
	}

}
