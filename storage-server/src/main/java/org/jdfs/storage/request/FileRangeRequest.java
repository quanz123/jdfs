package org.jdfs.storage.request;

public class FileRangeRequest extends FileRequest {
	private int remainingBytes;
	private int readBytes;

	public long getRemainingBytes() {
		return remainingBytes;
	}

	public void setRemainingBytes(int remainingBytes) {
		this.remainingBytes = remainingBytes;
	}

	public long getReadBytes() {
		return readBytes;
	}

	public void setReadBytes(int readBytes) {
		this.readBytes = readBytes;
	}

}
