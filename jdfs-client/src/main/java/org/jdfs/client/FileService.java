package org.jdfs.client;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件操作接口
 * @author James Quan
 * @version 2015年2月27日 上午10:11:14
 */
public interface FileService {

	/**
	 * 读取文件的基本信息
	 * @param id 文件的id
	 * @return 文件的基本信息，如果文件不存在则返回{@code null}
	 */
	public FileInfo getFileInfo(long id);
	
	/**
	 * 读取文件的数据
	 * @param id 文件的id
	 * @return 文件的数据流，如果文件不存在则返回{@code null}
	 */
	public InputStream getFileData(long id);
	
	/**
	 * 写入文件数据
	 * 
	 * @param id
	 *            文件的id
	 * @param name
	 *            文件的名字
	 * @param size
	 *            文件的大小
	 * @param data
	 *            待写入的文件数据
	 * @param offset
	 *            文件数据的写入位置
	 * @param length
	 *            待写入文件数据的长度
	 * @return
	 * @throws IOException
	 */
	public FileInfo updateFile(long id, String name, long size,
			InputStream data, long offset, long length) throws IOException;
	
	/**
	 * 删除指定的文件
	 * @param id 文件的id
	 */
	public void removeFile(long id);
}
