package org.jdfs.commons.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.commons.request.JdfsFileRequest;

/**
 * jdfs文件请求的基本实现
 * @author James Quan
 * @version 2015年2月3日 下午10:36:13
 */
public abstract class JdfsFileRequestMesageDecoder<T extends JdfsFileRequest> extends JdfsRequestMessageDecoder<T>{

	@Override
	protected MessageDecoderResult decodeRequest(DecoderState<T> state,
			IoSession session, IoBuffer in) {
		T request = state.getRequest();
		if(state.getState() == 1) {
			if(in.remaining() < 8) {
				return MessageDecoderResult.NEED_DATA;
			}
			long id = in.getLong();
			request.setId(id);
			state.toNextState();
		}
		return  decodeFileRequest(state, session, in);
	}

	/**
	 * 供子类重写的请求解码回调函数，用于实现对文件请求的其他参数的解码
	 * 
	 * @param state 缓存解码过程的状态对象
	 * @param session
	 * @param in
	 * @return
	 */
	protected MessageDecoderResult decodeFileRequest(DecoderState<T> state, IoSession session,
			IoBuffer in) {
		return MessageDecoderResult.OK;
	}
}
