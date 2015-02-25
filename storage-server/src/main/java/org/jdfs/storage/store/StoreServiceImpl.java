package org.jdfs.storage.store;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

public class StoreServiceImpl implements StoreService, InitializingBean {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private File rootDir;

	private String root;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isEmpty(root)) {
			root = "data";
		}
		rootDir = new File(root);
		initStoreDir(rootDir);
	}

	@Override
	public File readFile(long id) throws IOException {
		String path = getPath(id);
		logger.debug("readFile({}) -> {}", id, path);
		File file = new File(rootDir, path);
		return file.exists() ? file : file;
	}

	@Override
	public void setFileSize(long id, long size) throws IOException {
		String path = getPath(id);
		logger.debug("setFileSize({}, {}) -> {}", id, size, path);
		File file = new File(rootDir, path);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		try {
			if (raf.length() != size) {
				raf.setLength(size);
			}
		} finally {
			raf.close();
		}
	}

	@Override
	public void storeFile(long id, long position, byte[] data)
			throws IOException {
		String path = getPath(id);
		logger.debug("storeFile({}, {}, {} bytes) -> {}", id, position,
				data.length, path);
		File file = new File(rootDir, path);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		try {
			FileChannel fc = raf.getChannel();
			try {
				ByteBuffer buf = ByteBuffer.wrap(data);
				fc.write(buf, position);
			} finally {
				fc.close();
			}
		} finally {
			raf.close();
		}
	}

	@Override
	public void removeFile(long id) throws IOException {
		String path = getPath(id);
		logger.debug("removeFileSize({}) -> {}", id, path);
		File file = new File(rootDir, path);
		file.delete();
	}

	/**
	 * 返回指定文件的存储路径
	 * 
	 * @param id
	 *            文件的id
	 * @return
	 */
	protected String getPath(long id) {
		char[] md5 = DigestUtils.md5DigestAsHex(Long.toString(id).getBytes())
				.toCharArray();
		String path = new StringBuilder().append(md5, 0, 2)
				.append(File.separatorChar).append(md5, 2, 2)
				// .append(File.separatorChar).append(md5, 4, 2)
				.append(File.separatorChar).append(id).append(".dat")
				.toString();
		return path;
	}

	protected void initStoreDir(File rootDir) throws IOException {
		if (rootDir.exists()) {
			return;
		}
		logger.info("initialize store dir: " + rootDir.getCanonicalPath());
		rootDir.mkdirs();
		for (char c1 : HEX_CHARS) {
			for (char c2 : HEX_CHARS) {
				File f1 = mkdir(rootDir, c1, c2);
				for (char c3 : HEX_CHARS) {
					for (char c4 : HEX_CHARS) {
						File f2 = mkdir(f1, c3, c4);
						// for (char c5 : HEX_CHARS) {
						// for (char c6 : HEX_CHARS) {
						// File f3 = mkdir(f2, c5, c6);
						// }
						// }
					}
				}
			}
		}
		logger.info("initialize store dir success");
	}

	protected File mkdir(File parent, char c1, char c2) {
		String p = String.valueOf(new char[] { c1, c2 });
		File file = new File(parent, p);
		if (!file.exists()) {
			file.mkdir();
		}
		return file;
	}
}
