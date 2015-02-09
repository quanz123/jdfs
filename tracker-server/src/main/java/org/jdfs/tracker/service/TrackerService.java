package org.jdfs.tracker.service;

import java.io.IOException;

public interface TrackerService {

	public String getDownloadServerAddress(long id) throws IOException;
	
	public String getUploadServerAddress(long id)throws IOException;
	
}
