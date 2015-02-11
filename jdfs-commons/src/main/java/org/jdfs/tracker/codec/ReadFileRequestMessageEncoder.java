package org.jdfs.tracker.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.jdfs.commons.codec.JdfsFileRequestMessageEncoder;
import org.jdfs.tracker.request.ReadFileInfoRequest;

/**
 * 用于对{@link ReadFileInfoRequest}进行编码的编码器
 * @author James Quan
 * @version 2015年2月4日 下午3:06:22
 */
public class ReadFileRequestMessageEncoder extends
		JdfsFileRequestMessageEncoder<ReadFileInfoRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			ReadFileInfoRequest message) {
		return IoBuffer.allocate(16, false);
	}
}
