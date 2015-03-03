package org.jdfs.tracker.service;

import java.io.IOException;

import org.joda.time.DateTime;

/**
 * 文件信息管理服务
 * 
 * @author James Quan
 * @version 2015年2月3日 上午9:42:20
 */
public interface FileInfoSyncService {

	/**
	 * 删除指定文件的信息
	 * 
	 * @param id
	 *            文件的id
	 * @throws IOException
	 */
	public void syncRemoveFileInfo(long id) throws IOException;

	/**
	 * 更改文件名
	 * 
	 * @param fid
	 *            待保存文件的id
	 * @param name
	 *            文件的新名字
	 * @throws IOException
	 */
	public void syncUpdateFileName(long id, String name) throws IOException;

	/**
	 * 更改文件的数据属性
	 * 
	 * @param id
	 *            文件的id
	 * @param group
	 *            文件数据所在的存储组
	 * @param size
	 *            文件的大小
	 * @param lastModified
	 *            文件的最后修改时间
	 */
	public void syncUpdateFileDataInfo(long id, int group, long size,
			DateTime lastModified);
}
