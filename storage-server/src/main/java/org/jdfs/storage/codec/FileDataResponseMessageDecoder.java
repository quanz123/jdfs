package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.storage.request.FileDataResponse;
import org.jdfs.storage.request.FileRequest;

public class FileDataResponseMessageDecoder extends FileRequestMessageDecoder {
	private int maxFileSize = 1024 * 1024;
	
	 public int getMaxFileSize() {
		return maxFileSize;
	}
	 
	 public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	 
	@Override
	protected boolean support(int code) {
		return code == FileRequest.REQUEST_DATA_RESPONSE;
	}
	
	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if(in.remaining() < 8 ){
			return MessageDecoderResult.NEED_DATA;
		}
		int code = in.getInt();
		int status = in.getInt();
		if(in.prefixedDataAvailable(4, maxFileSize)) {			
			  int length = in.getInt();
		        byte[] bytes = new byte[length];
		        in.get(bytes);
				FileDataResponse resp = new FileDataResponse(status, bytes);
		        out.write(resp);
		        return MessageDecoderResult.OK;
		} else {
			return MessageDecoderResult.NEED_DATA;			
		}		
	}

}
