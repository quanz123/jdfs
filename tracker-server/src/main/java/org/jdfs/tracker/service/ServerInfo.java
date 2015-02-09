package org.jdfs.tracker.service;

import java.io.Serializable;
import java.net.InetSocketAddress;

import org.joda.time.DateTime;

public class ServerInfo implements Serializable {
	private InetSocketAddress serverAddress;
	private InetSocketAddress serviceAddress;
	private DateTime lastActive;

	public InetSocketAddress getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(InetSocketAddress serverAddress) {
		this.serverAddress = serverAddress;
	}

	public InetSocketAddress getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(InetSocketAddress serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	
	public DateTime getLastActive() {
		return lastActive;
	}
	
	public void setLastActive(DateTime lastActive) {
		this.lastActive = lastActive;
	}
}
