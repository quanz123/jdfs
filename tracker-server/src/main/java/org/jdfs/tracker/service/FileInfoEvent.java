package org.jdfs.tracker.service;

import org.springframework.context.ApplicationEvent;

public class FileInfoEvent extends ApplicationEvent {

	public FileInfoEvent(FileInfo file) {
		super(file);
	}

	public FileInfo getFileInfo(){
		return (FileInfo) getSource();
	}
}
