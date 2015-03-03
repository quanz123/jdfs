package org.jdfs.tracker.service;

public class RemoveFileInfoEvent extends FileInfoEvent {

	public RemoveFileInfoEvent(FileInfo file) {
		super(file);
	}

	@Override
	public String toString() {
		return new StringBuilder("remove event: ").append(getFileInfo())
				.toString();
	}
}
