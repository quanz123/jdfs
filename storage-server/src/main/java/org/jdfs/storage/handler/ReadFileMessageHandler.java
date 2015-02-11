package org.jdfs.storage.handler;

import java.io.File;
import java.io.RandomAccessFile;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsDataResponse;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.storage.request.ReadFileRequest;
import org.jdfs.storage.store.StoreService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 读取文件请求的处理器
 * 
 * @author James Quan
 * @version 2015年2月4日 下午3:21:40
 */
public class ReadFileMessageHandler implements MessageHandler<ReadFileRequest>,
		InitializingBean {
	private StoreService storeService;

	public StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(StoreService storeService) {
		this.storeService = storeService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(storeService, "storeService is required!");
	}

	@Override
	public void handleMessage(IoSession session, ReadFileRequest message)
			throws Exception {
		long id = message.getId();
		File file = storeService.readFile(id);
		if (file == null) {
			JdfsDataResponse resp = new JdfsDataResponse();
			resp.setStatus(JdfsRequestConstants.STATUS_FILE_NOT_FOUND);
			session.write(resp);
		} else {
			JdfsDataResponse resp = new JdfsDataResponse();
			resp.setStatus(JdfsRequestConstants.STATUS_OK);
			resp.setBatchId(message.getBatchId());
			long offset = Math.max(0, message.getOffset());
			int length = message.getLength();
			if (length < 0) {
				length = (int) file.length();
			}
			if (length > 0) {
				RandomAccessFile raf = new RandomAccessFile(file, "r");
				try {
					if (offset > 0) {
						raf.seek(offset);
					}
					byte[] buf = new byte[length];
					int l = raf.read(buf);
					resp.setData(buf);
				} finally {
					raf.close();
				}
			}
			session.write(resp);
		}
	}
}
