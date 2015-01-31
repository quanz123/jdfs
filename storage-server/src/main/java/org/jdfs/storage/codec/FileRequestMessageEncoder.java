package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.jdfs.storage.request.FileRequest;

/**
 * 文件请求信息编码器的基础实现
 * 
 * @author James Quan
 * @version 2015年1月30日 下午3:18:01
 */
public abstract class FileRequestMessageEncoder<T extends FileRequest>
		implements MessageEncoder<T> {

	@Override
	public void encode(IoSession session, T message, ProtocolEncoderOutput out)
			throws Exception {
		IoBuffer buffer = allocateBuffer(session, message);
		buffer.putInt(message.getCode());
		buffer.putLong(message.getId());
		encodeMessageData(message, buffer, session, out);
		buffer.flip();
		out.write(buffer);
	}

	/**
	 * 供子类重写的回调函数，用于输出请求中除功能代码和文件id以外的数据
	 * 
	 * @param message
	 *            待输出的请求
	 * @param buffer
	 *            用于承接请求数据的{@link IoBuffer}，其中请求的{@link FileRequest#getCode()
	 *            代码}和{@link FileRequest#getId() 文件id}已写入
	 * @param session
	 * @param out
	 * @throws Exception
	 */
	protected void encodeMessageData(T message, IoBuffer buffer, IoSession session,
			ProtocolEncoderOutput out) throws Exception {
	}

	/**
	 * 建立用于写入请求信息的{@link IoBuffer}
	 * 
	 * @param session
	 * @param message
	 * @return
	 */
	protected abstract IoBuffer allocateBuffer(IoSession session, T message);

}
