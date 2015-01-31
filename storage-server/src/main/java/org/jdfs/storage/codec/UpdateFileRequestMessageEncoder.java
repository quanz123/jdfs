package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.jdfs.storage.request.UpdateFileRequest;

/**
 * 用于对{@link UpdateFileRequest}进行编码的编码器
 * 
 * @author James Quan
 * @version 2015年1月30日 下午3:24:34
 */
public class UpdateFileRequestMessageEncoder extends
		FileRequestMessageEncoder<UpdateFileRequest> {

	@Override
	protected IoBuffer allocateBuffer(IoSession session,
			UpdateFileRequest message) {
		return IoBuffer.allocate(12, false);
	}

}
