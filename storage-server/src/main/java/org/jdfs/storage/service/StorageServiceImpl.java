package org.jdfs.storage.service;

import org.apache.curator.utils.EnsurePath;
import org.jdfs.commons.service.AbstractJdfsServer;

public class StorageServiceImpl extends AbstractJdfsServer {

	private String trackerServerType = "trackers";
	private String trackerNodeName = "tracker";
	private String storageServerType = "storages";
	private String storageNodeName = "storage";

	public String getTrackerServerType() {
		return trackerServerType;
	}

	public void setTrackerServerType(String trackerServerType) {
		this.trackerServerType = trackerServerType;
	}

	public String getTrackerNodeName() {
		return trackerNodeName;
	}

	public void setTrackerNodeName(String trackerNodeName) {
		this.trackerNodeName = trackerNodeName;
	}

	public String getStorageServerType() {
		return storageServerType;
	}

	public void setStorageServerType(String storageServerType) {
		this.storageServerType = storageServerType;
	}

	public String getStorageNodeName() {
		return storageNodeName;
	}

	public void setStorageNodeName(String storageNodeName) {
		this.storageNodeName = storageNodeName;
	}

	@Override
	protected String getType() {		
		return storageServerType;
	}
	
	@Override
	protected String getZkNodeName() {
		return storageNodeName;
	}

	@Override
	protected void initJdfsServer() throws Exception {		
		String path = getBase() + '/' + trackerServerType;
		EnsurePath ensurePath = new EnsurePath(path);
		ensurePath.ensure(curator.getZookeeperClient());
	}
}
