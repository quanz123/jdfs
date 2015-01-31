package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.jdfs.storage.request.RemoveFileRequest;

/**
 * 
 * @author James Quan
 * @version 2015年1月31日 上午9:50:59
 */
public class RemoveFileRequestMessageEncoder extends
		FileRequestMessageEncoder<RemoveFileRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			RemoveFileRequest message) {
		return IoBuffer.allocate(12, false);
	}

}
