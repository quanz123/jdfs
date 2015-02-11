package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.jdfs.commons.codec.JdfsFileRequestMessageEncoder;
import org.jdfs.storage.request.GetCheckSumRequest;

/**
 * 用于对{@link GetCheckSumRequest}进行编码的编码器
 * 
 * @author James Quan
 * @version 2015年2月8日 下午12:15:40
 */
public class GetCheckSumRequestMessageEncoder extends
		JdfsFileRequestMessageEncoder<GetCheckSumRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			GetCheckSumRequest message) throws Exception {
		return IoBuffer.allocate(16, false);
	}
}
