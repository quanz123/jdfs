package org.jdfs.tracker.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdfs.tracker.request.FileInfoRequest;
import org.jdfs.tracker.request.UpdateFileInfoRequest;

public class UpdateFileInfoRequestMessageDecoder extends
		FileRequestMessageDecoder {

	private int maxNameLength = 255 * 3;
	public static final String UPDATE_FILE_INFO_REQUEST = "updateFileInfoRequest";

	public int getMaxNameLength() {
		return maxNameLength / 3;
	}

	public void setMaxNameLength(int maxNameLength) {
		this.maxNameLength = maxNameLength * 3;
	}

	@Override
	protected boolean support(int code) {
		return code == FileInfoRequest.REQUEST_UPDATE;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		DecoderState state = (DecoderState) session
				.getAttribute(UPDATE_FILE_INFO_REQUEST);
		if (state == null) {
			state = new DecoderState();
			session.setAttribute(UPDATE_FILE_INFO_REQUEST, state);
		}
		if (state.request == null) {
			if (in.remaining() < 28) {
				return MessageDecoderResult.NEED_DATA;
			}
			int code = in.getInt();
			long id = in.getLong();
			long size = in.getLong();
			long lastModified = in.getLong();
			UpdateFileInfoRequest request = new UpdateFileInfoRequest();
			request.setCode(FileInfoRequest.REQUEST_UPDATE);
			request.setId(id);
			request.setSize(size);
			request.setLastModified(lastModified);
		}
		if (!in.prefixedDataAvailable(4, maxNameLength)) {
			return MessageDecoderResult.NEED_DATA;
		}
		int len = in.getInt();
		if(len > 0) {
		byte[] data = new byte[len];
		in.get(data);
		String name = new String(data, "UTF-8");
		state.request.setName(name);
		} else {
			state.request.setName("");
		}
		out.write(state.request);
		state.request = null;
		return MessageDecoderResult.OK;
	}

	private static class DecoderState {
		UpdateFileInfoRequest request;
	}

}
