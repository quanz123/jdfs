package org.jdfs.storage.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.storage.request.UpdateFileRequest;
import org.jdfs.storage.store.StoreService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 保存文件请求的处理器
 * 
 * @author James Quan
 * @version 2015年2月4日 下午3:19:46
 */
public class UpdateFileMessageHandler implements
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
		long size = message.getSize();
		long position = Math.max(0, message.getPosition());
		byte[] data = message.getData();
		int l = data == null ? 0 : data.length;
		size = Math.max(size, position + l);
		storeService.setFileSize(id, size);
		if (l > 0) {
			storeService.storeFile(id, position, data);
		}
		JdfsStatusResponse resp = new JdfsStatusResponse();
		resp.setBatchId(message.getBatchId());
		resp.setStatus(JdfsRequestConstants.STATUS_OK);
		session.write(resp);
	}
}
