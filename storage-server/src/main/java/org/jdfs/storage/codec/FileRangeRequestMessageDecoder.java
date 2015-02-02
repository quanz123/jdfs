package org.jdfs.storage.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.storage.request.FileRange;
import org.jdfs.storage.request.FileRangeRequest;
import org.jdfs.storage.request.FileRequest;
import org.jdfs.storage.request.UpdateFileDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRangeRequestMessageDecoder extends MessageDecoderAdapter {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public static final String RANGE_DATA = "rangeData";

	private int maxDataSize = 1024 * 1024;
	
	public int getMaxDataSize() {
		return maxDataSize;
	}
	
	public void setMaxDataSize(int maxDataSize) {
		this.maxDataSize = maxDataSize;
	}

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		FileRangeRequest request = (FileRangeRequest) session
				.getAttribute(RANGE_DATA);
		if (request != null) {
			return MessageDecoderResult.OK;
		}
		if (in.remaining() < 4) {
			return MessageDecoderResult.NEED_DATA;
		} else {
			int code = in.getInt();
			return code == FileRequest.REQUEST_UPDATE ? MessageDecoderResult.OK
					: MessageDecoderResult.NOT_OK;
		}
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		FileRange range = (FileRange) session.getAttribute(RANGE_DATA);
		int bufSize = in.remaining();
		if (range == null) {
			if (in.remaining() < 32) {
				return MessageDecoderResult.NEED_DATA;
			}
			int code = in.getInt();
			long id = in.getLong();
			long size = in.getLong();
			long position = in.getLong();
			int dataSize = in.getInt();
			if(dataSize > maxDataSize) {
				logger.error("dataSize " + dataSize + " too big!");
				throw new IllegalArgumentException("dataSize " + dataSize + " too big!");
			}
			size = Math.max(size, position + dataSize);
			range = new FileRange(id, size, position, dataSize);
			bufSize -= 32;
		}
		byte[] data = new byte[Math.min(bufSize, range.getRemainingBytes())];
		in.get(data);
		UpdateFileDataRequest request = new UpdateFileDataRequest(
				range.getId(), range.getSize(), range.getPosition(), data);
		int remain = range.getRemainingBytes() - data.length;
		if (remain == 0) {
			request.setSendResponse(true);
			session.removeAttribute(RANGE_DATA);
		} else {
			range.setPosition(range.getPosition() + data.length);
			range.setRemainingBytes(remain);
			session.setAttribute(RANGE_DATA, range);
		}
		out.write(request);
		return MessageDecoderResult.OK;
	}

}
