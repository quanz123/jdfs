package org.jdfs.tracker.ha;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jdfs.tracker.service.FileInfoEvent;
import org.jdfs.tracker.service.ServerInfo;
import org.jdfs.tracker.service.TrackerService;
import org.springframework.context.ApplicationListener;
import org.springframework.core.task.AsyncTaskExecutor;

public class ClusterFileInfoListener implements
		ApplicationListener<FileInfoEvent> {
	private TrackerService trackerService;
	private NioSocketConnector trackerConnector;
	private AsyncTaskExecutor executor; 
	
	public TrackerService getTrackerService() {
		return trackerService;
	}
	
	public void setTrackerService(TrackerService trackerService) {
		this.trackerService = trackerService;
	}
	
	public NioSocketConnector getTrackerConnector() {
		return trackerConnector;
	}
	
	public void setTrackerConnector(NioSocketConnector trackerConnector) {
		this.trackerConnector = trackerConnector;
	}
	
	public AsyncTaskExecutor getExecutor() {
		return executor;
	}
	
	public void setExecutor(AsyncTaskExecutor executor) {
		this.executor = executor;
	}
	
	@Override
	public void onApplicationEvent(FileInfoEvent event) {
		for(ServerInfo server : trackerService.getMembers()){
			FileInfoSyncTask task = new FileInfoSyncTask(event);
			task.setConnector(trackerConnector);
			task.setServerAddress(server.getServiceAddress());
			executor.submit(task);
		}
	}
	

}
