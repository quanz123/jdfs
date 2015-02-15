package org.jdfs.commons.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.commons.request.JdfsRequest;

/**
 * Jdfs请求解码器的基础实现
 * 
 * @author James Quan
 * @version 2015年2月3日 下午9:39:05
 */
public abstract class JdfsRequestMessageDecoder<T extends JdfsRequest> extends
		MessageDecoderAdapter {
	protected String stateObjectSessionKey = null;
	protected int maxDataSize = 1024 * 1024;

	public int getMaxDataSize() {
		return maxDataSize;
	}

	public void setMaxDataSize(int maxDataSize) {
		this.maxDataSize = maxDataSize;
	}

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		if (in.remaining() < 8) {
			return MessageDecoderResult.NEED_DATA;
		} else {
			int batchId = in.getInt();
			int code = in.getInt();
			return support(batchId, code, session, in) ? MessageDecoderResult.OK
					: MessageDecoderResult.NOT_OK;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		DecoderState<T> state = (DecoderState<T>) session
				.getAttribute(getStateObjectSessionKey());
		if (state == null) {
			state = createDecoderState();
			session.setAttribute(getStateObjectSessionKey(), state);
		}
		if (state.getState() == 0) {
			if (in.remaining() < 8) {
				return MessageDecoderResult.NEED_DATA;
			}
			int batchId = in.getInt();
			int code = in.getInt();
			T request = createRequest(batchId, code);
			state.setRequest(request);
			state.toNextState();
		}
		MessageDecoderResult result = decodeRequest(state, session, in);
		if (result != MessageDecoderResult.OK) {
			return result;
		}
		out.write(state.getRequest());
		state.reset();
		return MessageDecoderResult.OK;
	}

	/**
	 * 返回是否支持对指定的功能代码进行处理
	 * 
	 * @param batchId
	 * @param code
	 * @return
	 */
	protected boolean support(int batchId, int code, IoSession session, IoBuffer in) {
		return code == getRequestCode();
	}

	/**
	 * 返回session对象中用于存储解码状态对象的key
	 * 
	 * @return
	 */
	protected String getStateObjectSessionKey() {
		if (stateObjectSessionKey == null) {
			stateObjectSessionKey = "jdfsRequestState" + getRequestCode();
		}
		return stateObjectSessionKey;
	}

	/**
	 * 创建新的解码状态对象
	 * 
	 * @return
	 */
	protected DecoderState<T> createDecoderState() {
		return new DecoderState<T>();
	}

	/**
	 * 供子类重写的请求解码回调函数，用于实现对分组id和请求代码以外的其他参数的解码
	 * 
	 * @param state
	 *            缓存解码过程的状态对象
	 * @param session
	 * @param in
	 * @return
	 * @throws Exception
	 */
	protected MessageDecoderResult decodeRequest(DecoderState<T> state,
			IoSession session, IoBuffer in) throws Exception {
		return MessageDecoderResult.OK;
	}

	/**
	 * 返回所处理的请求的代码
	 * 
	 * @return
	 */
	protected abstract int getRequestCode();

	/**
	 * 建立空白的请求对象
	 * 
	 * @param batchId
	 *            请求的分组id
	 * @param code
	 *            请求的代码
	 * @return
	 */
	protected abstract T createRequest(int batchId, int code);
}