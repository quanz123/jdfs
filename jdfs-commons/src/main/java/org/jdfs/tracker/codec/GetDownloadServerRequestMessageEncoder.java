package org.jdfs.tracker.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.jdfs.commons.codec.JdfsFileRequestMessageEncoder;
import org.jdfs.tracker.request.GetDownloadServerRequest;

/**
 * 用于对{@link GetDownloadServerRequest}进行编码的编码器
 * 
 * @author James Quan
 * @version 2015年2月8日 下午12:15:40
 */
public class GetDownloadServerRequestMessageEncoder extends
		JdfsFileRequestMessageEncoder<GetDownloadServerRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			GetDownloadServerRequest message) throws Exception {
		return IoBuffer.allocate(16, false);
	}
}
