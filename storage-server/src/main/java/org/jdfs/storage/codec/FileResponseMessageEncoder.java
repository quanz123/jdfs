package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.jdfs.storage.request.FileRequest;
import org.jdfs.storage.request.FileResponse;

/**
 * 文件请求响应信息编码器的基础实现
 * @author James Quan
 * @version 2015年1月30日 下午3:37:30
 */
public class FileResponseMessageEncoder implements MessageEncoder<FileResponse>{

	@Override
	public void encode(IoSession session, FileResponse message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(8, false);
		buffer.putInt(FileRequest.REQUEST_RESPONSE);
		buffer.putInt(message.getStatus());
		buffer.flip();
		out.write(buffer);
	}

}
