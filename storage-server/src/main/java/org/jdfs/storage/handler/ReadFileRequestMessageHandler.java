package org.jdfs.storage.handler;

import java.io.File;
import java.nio.channels.FileChannel;

import org.apache.commons.io.FileUtils;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.storage.request.FileDataResponse;
import org.jdfs.storage.request.FileRequestResponse;
import org.jdfs.storage.request.FileResponse;
import org.jdfs.storage.request.ReadFileRequest;
import org.jdfs.storage.store.StoreService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class ReadFileRequestMessageHandler implements
		MessageHandler<ReadFileRequest>, InitializingBean {
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
		File file  = storeService.readFile(id);
		if(file == null) {
			FileResponse resp = new FileDataResponse(1, null);
			session.write(resp);			
		} else {
			byte[] data = FileUtils.readFileToByteArray(file);
			FileResponse resp = new FileDataResponse(0, data);
			session.write(resp);			
		}		
	}
}
