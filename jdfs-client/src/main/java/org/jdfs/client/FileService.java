package org.jdfs.client;

import java.io.IOException;
import java.io.InputStream;

public interface FileService {

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
}
