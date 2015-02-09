package org.jdfs.tracker.codec;

import org.jdfs.commons.codec.JdfsFileRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.tracker.request.ReadFileInfoRequest;

/**
 * 用于对{@link ReadFileInfoRequest}进行解码的解码器
 * @author James Quan
 * @version 2015年2月4日 下午3:11:47
 */
public class ReadFileRequestMessageDecoder extends JdfsFileRequestMessageDecoder<ReadFileInfoRequest> {
	 
	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_INFO_READ;
	}

	@Override
	protected ReadFileInfoRequest createRequest(int code) {
		return new ReadFileInfoRequest();
	}
}
