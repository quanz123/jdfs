package org.jdfs.tracker.service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

public class FileInfoServiceImpl implements FileInfoService, InitializingBean,
		DisposableBean {
	private String root;
	private File rootDir;
	private DB infoDb;
	private BTreeMap<Long, FileInfo> infos;
	private AtomicInteger compactCounter;
	private int compactRange = 1000;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public int getCompactRange() {
		return compactRange;
	}

	public void setCompactRange(int compactRange) {
		this.compactRange = compactRange;
	}

	public File getRootDir() {
		return rootDir;
	}

	public DB getInfoDb() {
		return infoDb;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isEmpty(root)) {
			root = "data";
		}
		rootDir = new File(root);
		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}
		File file = new File(rootDir, "files");
		infoDb = DBMaker.newFileDB(file).make();
		infos = infoDb.getTreeMap("infos");
		compactCounter = new AtomicInteger();
		compactCounter.set(getCompactRandom());
	}

	@Override
	public void destroy() throws Exception {
		infoDb.compact();
		infoDb.close();
	}

	@Override
	public FileInfo getFileInfo(long id) throws IOException {
		FileInfo file = infos.get(id);
		return file == null ? null : new FileInfo(file);
	}

	@Override
	public void updateFileInfo(FileInfo file) throws IOException {
		long id = file.getId();
		infos.put(id, file);
		infoDb.commit();
		compactDb();
	}

	@Override
	public void removeFileInfo(long id) throws IOException {
		infos.remove(id);
		infoDb.commit();
		compactDb();
	}

	protected void compactDb() {
		int n = compactCounter.decrementAndGet();
		if (n <= 0) {
			compactCounter.set(getCompactRandom());
			infoDb.compact();
		}
	}

	protected int getCompactRandom() {
		for (;;) {
			int n = (int) (Math.random() * compactRange);
			if (n != 0) {
				return n;
			}
		}
	}
}