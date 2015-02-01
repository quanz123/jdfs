package org.jdfs.storage.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.storage.request.FileRequestResponse;
import org.jdfs.storage.request.RemoveFileRequest;
import org.jdfs.storage.store.StoreService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class RemoveFileRequestMessageHandler implements
		MessageHandler<RemoveFileRequest>, InitializingBean {
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
	public void handleMessage(IoSession session, RemoveFileRequest message)
			throws Exception {
		long id = message.getId();
		storeService.removeFile(id);
		FileRequestResponse resp = new FileRequestResponse(0);
		session.write(resp);
	}
}
