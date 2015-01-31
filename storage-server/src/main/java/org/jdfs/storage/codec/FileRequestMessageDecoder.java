package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

/**
 * 文件请求信息解码器的基础实现
 * @author James Quan
 * @version 2015年1月30日 下午3:49:19
 */
public abstract class FileRequestMessageDecoder extends MessageDecoderAdapter {
	
	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		if (in.remaining() < 4) {
			return MessageDecoderResult.NEED_DATA;
		} else {
			int code = in.getInt();
			return support(code) ? MessageDecoderResult.OK : MessageDecoderResult.NOT_OK;
		}
	}
	
	/**
	 * 返回是否支持对指定的功能代码进行处理
	 * @param code
	 * @return
	 */
	protected abstract boolean support(int code);
}
