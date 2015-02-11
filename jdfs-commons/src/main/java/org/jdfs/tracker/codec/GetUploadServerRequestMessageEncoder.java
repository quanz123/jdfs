package org.jdfs.tracker.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.jdfs.commons.codec.JdfsFileRequestMessageEncoder;
import org.jdfs.tracker.request.GetUploadServerRequest;

/**
 * 用于对{@link GetUploadServerRequest}进行编码的编码器
 * 
 * @author James Quan
 * @version 2015年2月8日 下午12:15:40
 */
public class GetUploadServerRequestMessageEncoder extends
		JdfsFileRequestMessageEncoder<GetUploadServerRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			GetUploadServerRequest message) throws Exception {
		return IoBuffer.allocate(16, false);
	}
}
