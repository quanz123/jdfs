package org.jdfs.commons.codec;

import java.io.UnsupportedEncodingException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.jdfs.commons.request.JdfsRequest;
import org.springframework.util.StringUtils;

/**
 * jdfs请求信息编码器的缺省实现
 * 
 * @author James Quan
 * @version 2015年2月4日 上午10:45:27
 */
public abstract class JdfsRequestMessageEncoder<T extends JdfsRequest>
		implements MessageEncoder<T> {

	@Override
	public void encode(IoSession session, T message, ProtocolEncoderOutput out)
			throws Exception {
		IoBuffer buffer = allocateBuffer(session, message);
		buffer.putInt(message.getCode());
		encodeMessageData(message, buffer, session, out);
		buffer.flip();
		out.write(buffer);
	}

	/**
	 * 建立用于写入请求信息的{@link IoBuffer}
	 * 
	 * @param session
	 * @param message
	 * @return
	 * @throws Exception
	 */
	protected abstract IoBuffer allocateBuffer(IoSession session, T message)
			throws Exception;

	/**
	 * 供子类重写的回调函数，用于输出请求中除分组id和功能代码以外的数据
	 * 
	 * @param message
	 *            待输出的请求
	 * @param buffer
	 *            用于承接请求数据的{@link IoBuffer}，其中请求的{@link JdfsRequest#getBatchId()
	 *            代码}和{@link JdfsRequest#getCode()
	 *            代码}已写入
	 * @param session
	 * @param out
	 * @throws Exception
	 */
	protected void encodeMessageData(T message, IoBuffer buffer,
			IoSession session, ProtocolEncoderOutput out) throws Exception {
	}

	/**
	 * 输出字符串
	 * @param str
	 * @param buffer
	 * @throws UnsupportedEncodingException
	 */
	protected void putString(String str, IoBuffer buffer) throws UnsupportedEncodingException {
		if(StringUtils.isEmpty(str)) {
			buffer.putInt(0);
			return;
		} else {
			byte[] data = str.getBytes("UTF-8");
			buffer.putInt(data.length);
			buffer.put(data);
		}
	}
}
