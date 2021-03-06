package org.jdfs.tracker.service;

public class UpdateFileInfoEvent extends FileInfoEvent {
	private FileInfo oldFileInfo;

	public UpdateFileInfoEvent(FileInfo oldFile, FileInfo newFile) {
		super(newFile);
		this.oldFileInfo = oldFile;
	}

	public FileInfo getOldFileInfo() {
		return oldFileInfo;
	}

	@Override
	public String toString() {
		return new StringBuilder("update event: ").append(getFileInfo())
				.toString();
	}
}
