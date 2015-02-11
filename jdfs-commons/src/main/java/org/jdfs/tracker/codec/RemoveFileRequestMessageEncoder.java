package org.jdfs.tracker.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.jdfs.commons.codec.JdfsFileRequestMessageEncoder;
import org.jdfs.tracker.request.RemoveFileInfoRequest;

/**
 * 用于对{@link RemoveFileInfoRequest}进行编码的编码器
 * @author James Quan
 * @version 2015年2月4日 下午3:06:22
 */
public class RemoveFileRequestMessageEncoder extends
		JdfsFileRequestMessageEncoder<RemoveFileInfoRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			RemoveFileInfoRequest message) {
		return IoBuffer.allocate(16, false);
	}
}
