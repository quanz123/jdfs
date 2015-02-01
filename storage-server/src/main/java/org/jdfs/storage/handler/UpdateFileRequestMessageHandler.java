package org.jdfs.storage.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.storage.request.FileRequestResponse;
import org.jdfs.storage.request.UpdateFileRequest;
import org.jdfs.storage.store.StoreService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class UpdateFileRequestMessageHandler implements
		MessageHandler<UpdateFileRequest>, InitializingBean {
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
	public void handleMessage(IoSession session, UpdateFileRequest message)
			throws Exception {
		long id = message.getId();
		byte[] data = message.getData();
		storeService.storeFile(id, data);
		FileRequestResponse resp = new FileRequestResponse(0);
		session.write(resp);
	}
}
