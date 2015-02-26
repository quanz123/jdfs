package org.jdfs.tracker.service;

import java.io.IOException;

public interface TrackerService {

	public ServerInfo getDownloadServerForFile(long id) throws IOException;
	
	public ServerInfo getUploadServerForFile(long id)throws IOException;
	
}
