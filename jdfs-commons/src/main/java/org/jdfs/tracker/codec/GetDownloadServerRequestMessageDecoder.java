package org.jdfs.tracker.codec;

import org.jdfs.commons.codec.JdfsFileRequestMessageDecoder;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.tracker.request.GetDownloadServerRequest;

/**
 * 用于对{@link GetDownloadServerRequest}进行解码的解码器
 * @author James Quan
 * @version 2015年2月8日 下午12:17:31
 */
public class GetDownloadServerRequestMessageDecoder extends JdfsFileRequestMessageDecoder<GetDownloadServerRequest> {
	 
	@Override
	protected int getRequestCode() {
		return JdfsRequestConstants.REQUEST_DOWNLOAD_SERVER;
	}

	@Override
	protected GetDownloadServerRequest createRequest(int code) {
		return new GetDownloadServerRequest();
	}
}
