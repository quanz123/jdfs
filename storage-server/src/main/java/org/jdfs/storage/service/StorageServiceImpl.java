package org.jdfs.storage.service;

import org.jdfs.commons.service.AbstractJdfsServer;

public class StorageServiceImpl extends AbstractJdfsServer {

	@Override
	protected String getType() {		
		return "storages";
	}
	
	@Override
	protected String getZkNodeName() {
		return "storage";
	}

	@Override
	protected void initJdfsServer() throws Exception {		
		mkdirs(getBase() +  "/trackers");
	}
}
