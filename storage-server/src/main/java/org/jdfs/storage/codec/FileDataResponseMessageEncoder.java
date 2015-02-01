package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.jdfs.storage.request.FileDataResponse;
import org.jdfs.storage.request.FileRequest;
import org.jdfs.storage.request.FileResponse;

/**
 * 文件数据响应信息编码器的基础实现
 * @author James Quan
 * @version 2015年1月30日 下午3:37:30
 */
public class FileDataResponseMessageEncoder implements MessageEncoder<FileDataResponse>{

	@Override
	public void encode(IoSession session, FileDataResponse message,
			ProtocolEncoderOutput out) throws Exception {
		int length = message.getLength();		
		IoBuffer buffer = IoBuffer.allocate(12 + length, false);
		buffer.putInt(FileRequest.REQUEST_DATA_RESPONSE);
		buffer.putInt(message.getStatus());
		buffer.putInt(length);
		if(length > 0) {
			buffer.put(message.getData());
		}
		buffer.flip();
		out.write(buffer);
	}

}
