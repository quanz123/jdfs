package org.jdfs.tracker.service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * 文件信息管理服务的缺省实现
 * 
 * @author James Quan
 * @version 2015年3月2日 下午2:35:17
 */
public class FileInfoServiceImpl implements FileInfoService,
		ApplicationContextAware, InitializingBean, DisposableBean {
	private String root;
	private File rootDir;
	private DB infoDb;
	private BTreeMap<Long, FileInfo> infos;
	private AtomicInteger compactCounter;
	private int compactRange = 1000;
	private ApplicationContext applicationContext;

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
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
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
	public void updateFileName(long id, String name) throws IOException {
		FileInfo[] files = getOrCreateFileInfoForUpdate(id);
		FileInfo file = files[1];
		// TODO 如何处理并发修改
		file.setName(name);
		infos.put(id, file);
		infoDb.commit();
		compactDb();
		fireEvent(new UpdateFileInfoEvent(files[0], files[1]));
	}

	@Override
	public void updateFileDataInfo(long id, int group, long size,
			DateTime lastModified) {
		FileInfo[] files = getOrCreateFileInfoForUpdate(id);
		// TODO 如何处理并发修改
		FileInfo file = files[1];
		// TODO 对group的方法是否应该单独分离
		if (group != 0) {
			file.setGroup(group);
		}
		file.setSize(size);
		file.setLastModified(lastModified);
		infos.put(id, file);
		infoDb.commit();
		compactDb();
		fireEvent(new UpdateFileInfoEvent(files[0], files[1]));
	}

	@Override
	public void removeFileInfo(long id) throws IOException {
		FileInfo file = infos.remove(id);
		if (file != null) {
			infoDb.commit();
			compactDb();
			fireEvent(new RemoveFileInfoEvent(file));
		}
	}

	protected FileInfo[] getOrCreateFileInfoForUpdate(long id) {
		FileInfo oldFile = infos.get(id);
		FileInfo newFile;
		if (oldFile == null) {
			FileInfo f = new FileInfo();
			f.setId(id);
			oldFile = infos.putIfAbsent(id, f);
			if (oldFile == null) {
				newFile = new FileInfo(f);
			} else {
				newFile = new FileInfo(oldFile);
			}
		} else {
			newFile = new FileInfo(oldFile);
		}
		return new FileInfo[] { oldFile, newFile };
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

	protected void fireEvent(FileInfoEvent event) {
		applicationContext.publishEvent(event);
	}
}
