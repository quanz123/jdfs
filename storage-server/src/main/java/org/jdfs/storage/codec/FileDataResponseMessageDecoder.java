package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.storage.request.FileDataResponse;
import org.jdfs.storage.request.FileRequest;

public class FileDataResponseMessageDecoder extends FileRequestMessageDecoder {
	private int maxFileSize = 1024 * 1024;
	public static final String DATA_RESPONSE = "fileDataResponse";
	
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

		DecoderState state = (DecoderState) session.getAttribute(DATA_RESPONSE);
		if(state == null){
			state = new DecoderState();
			session.setAttribute(DATA_RESPONSE, state);
		}
		if(state.response == null) {					
			if(in.remaining() < 8 ){
				return MessageDecoderResult.NEED_DATA;
			}
			int code = in.getInt();
			int status = in.getInt();
			FileDataResponse response = new FileDataResponse(status, null);
			state.response = response;
		}
		if (!in.prefixedDataAvailable(4, maxFileSize)) {
			return MessageDecoderResult.NEED_DATA;
		}
		int dataLength = in.getInt();
		byte[] data = new byte[dataLength];
		in.get(data);
		FileDataResponse response = state.response;
		response.setData(data);
		out.write(response);
		state.response = null;
		return MessageDecoderResult.OK;
	}
	
    private static class DecoderState {
    	FileDataResponse response;
    }
}
