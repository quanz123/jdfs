package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.storage.request.FileRequest;
import org.jdfs.storage.request.UpdateFileRequest;

/**
 * 
 * @author James Quan
 * @version 2015年1月31日 上午9:50:38
 */
public class UpdateFileRequestMessageDecoder extends FileRequestMessageDecoder {
	private int maxFileSize = 1024 * 1024;
	
	 public int getMaxFileSize() {
		return maxFileSize;
	}
	 
	 public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	 
	@Override
	protected boolean support(int code) {
		return  code == FileRequest.REQUEST_UPDATE;
	}
	
	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if (in.remaining() < 16) {
			return MessageDecoderResult.NEED_DATA;
		}
		int code = in.getInt();
		long id = in.getLong();
		if(in.prefixedDataAvailable(4, maxFileSize)) {			
			  int length = in.getInt();
		        byte[] bytes = new byte[length];
		        in.get(bytes);
		        UpdateFileRequest request = new UpdateFileRequest(id, bytes);
		        out.write(request);
		        return MessageDecoderResult.OK;
		} else {
			return MessageDecoderResult.NEED_DATA;			
		}		
	}
}