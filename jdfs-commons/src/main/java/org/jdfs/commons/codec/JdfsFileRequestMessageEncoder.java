package org.jdfs.commons.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdfs.commons.request.JdfsFileRequest;

/**
 * jdfs文件请求信息编码器的缺省实现
 * 
 * @author James Quan
 * @version 2015年2月4日 上午10:56:39
 */
public abstract class JdfsFileRequestMessageEncoder<T extends JdfsFileRequest>
		extends JdfsRequestMessageEncoder<T> {
	
	@Override
	protected void encodeMessageData(T message, IoBuffer buffer,
			IoSession session, ProtocolEncoderOutput out) throws Exception {
		buffer.putLong(message.getId());
		encodeFileMessageData(message, buffer, session, out);
	}

	/**
	 * 供子类重写的回调函数，用于输出请求中除分组id、功能代码和文件id以外的数据
	 * 
	 * @param message
	 *            待输出的请求
	 * @param buffer
	 *            用于承接请求数据的{@link IoBuffer}，其中请求的
	 *            {@link JdfsFileRequest#getCode() 代码}和
	 *            {@link JdfsFileRequest#getId() 文件id}已写入
	 * @param session
	 * @param out
	 * @throws Exception
	 */
	protected void encodeFileMessageData(T message, IoBuffer buffer,
			IoSession session, ProtocolEncoderOutput out) throws Exception {
	}
}
