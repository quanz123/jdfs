package org.jdfs.storage.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.storage.request.FileRequestResponse;
import org.jdfs.storage.request.UpdateFileDataRequest;
import org.jdfs.storage.store.StoreService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class UpdateFileDataMessageHandler implements
		MessageHandler<UpdateFileDataRequest>, InitializingBean {
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
	public void handleMessage(IoSession session, UpdateFileDataRequest message)
			throws Exception {
		long id = message.getId();
		long size = message.getSize();
		long position = message.getPosition();
		byte[] data = message.getData();
		// if (position == 0) {
		storeService.setFileSize(id, size);
		// }
		if (data != null && data.length > 0) {
			storeService.storeFile(id, position, data);
		}
		if (message.isSendResponse()) {
			FileRequestResponse resp = new FileRequestResponse(0);
			session.write(resp);
		}
	}
}
