package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.jdfs.commons.codec.JdfsFileRequestMessageEncoder;
import org.jdfs.storage.request.RemoveFileRequest;

/**
 * 用于对{@link RemoveFileRequest}进行编码的编码器
 * @author James Quan
 * @version 2015年2月4日 下午3:06:22
 */
public class RemoveFileRequestMessageEncoder extends
		JdfsFileRequestMessageEncoder<RemoveFileRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			RemoveFileRequest message) {
		return IoBuffer.allocate(16, false);
	}
}
