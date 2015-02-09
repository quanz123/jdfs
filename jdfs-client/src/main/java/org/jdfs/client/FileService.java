package org.jdfs.client;

import java.io.IOException;
import java.io.InputStream;

public interface FileService {

	public FileInfo updateFile(long id, String name, InputStream data,
			long offset, long size) throws IOException;
}
