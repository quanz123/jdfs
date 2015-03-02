package org.jdfs.tracker.service;

import java.io.Serializable;
import java.net.InetSocketAddress;

import org.jdfs.commons.utils.InetSocketAddressHelper;

/**
 * 服务器信息
 * 
 * @author James Quan
 * @version 2015年2月26日 下午3:23:51
 */
public class ServerInfo implements Serializable {
	private String name;
	private int group;
	private InetSocketAddress serviceAddress;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public InetSocketAddress getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(InetSocketAddress serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ServerInfo)) {
			return false;
		}
		ServerInfo server = (ServerInfo) obj;
		if(group != server.getGroup()) {
			return false;
		}
		if(serviceAddress == null) {
			return server.getServiceAddress() == null;
		} else {
			return serviceAddress.equals(server.getServiceAddress());
		}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(group).append(':');
		if(serviceAddress != null) {
			b.append(InetSocketAddressHelper.toString(serviceAddress));
		}
		return b.toString();
	}
	
	/**
	 * 将{@link #toString()}产生的字符串还原为对应的对象
	 * @param info
	 * @return
	 */
	public static ServerInfo parse(String info) {
		info = info == null ? null : info.trim();
		if(info == null || info.length() == 0) {
			return null;
		}
		int colon = info.indexOf(':');
		if(colon == -1) {
			throw new IllegalArgumentException();
		}
		int group = Integer.parseInt(info.substring(0, colon));
		InetSocketAddress addr = InetSocketAddressHelper.parse(info.substring(colon + 1));
		ServerInfo s = new ServerInfo();
		s.setGroup(group);
		s.setServiceAddress(addr);
		return s;
	}
}
