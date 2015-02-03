package org.jdfs.tracker.service;

import java.io.IOException;

/**
 * 文件信息管理服务
 * 
 * @author James Quan
 * @version 2015年2月3日 上午9:42:20
 */
public interface FileInfoService {

	public FileInfo getFileInfo(long id) throws IOException;
	
	public void updateFileInfo(FileInfo file) throws IOException;

	public void removeFileInfo(long id) throws IOException;
}
