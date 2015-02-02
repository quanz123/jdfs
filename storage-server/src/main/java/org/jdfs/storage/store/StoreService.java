package org.jdfs.storage.store;

import java.io.File;
import java.io.IOException;

/**
 * 物理数据存储服务
 * 
 * @author James Quan
 * @version 2015年2月1日 下午8:36:43
 */
public interface StoreService {

	/**
	 * 读取指定的文件，如果文件不存在则返回{@code null}
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public File readFile(long id) throws IOException;

	/**
	 * 设置文件的大小
	 * 
	 * @param id
	 * @param size
	 * @throws IOException
	 */
	public void setFileSize(long id, long size) throws IOException;

	/**
	 * 写入指定的文件
	 * 
	 * @param id
	 * @param position
	 * @param data
	 * @throws IOException
	 */
	public void storeFile(long id, long position, byte[] data)
			throws IOException;

	/**
	 * 删除指定的文件
	 * 
	 * @param id
	 * @throws IOException
	 */
	public void removeFile(long id) throws IOException;
}
