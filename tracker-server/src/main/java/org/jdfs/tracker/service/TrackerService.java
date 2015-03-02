package org.jdfs.tracker.service;

import java.io.IOException;

/**
 * Tracker管理服务
 * @author James Quan
 * @version 2015年3月2日 下午4:35:40
 */
public interface TrackerService {

	/**
	 * 返回Tracker管理服务器自身的信息
	 * @return
	 */
	public ServerInfo getServerInfo();
	
	/**
	 * 返回同组的其他管理服务器的信息
	 * @return
	 */
	public ServerInfo[] getMembers();
	
	/**
	 * 返回用于下载文件的storage服务器地址
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public ServerInfo getDownloadServerForFile(long id) throws IOException;
	
	/**
	 * 返回返回用于上传文件的storage服务器地址
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public ServerInfo getUploadServerForFile(long id)throws IOException;
	
}
