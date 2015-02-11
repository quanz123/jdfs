package org.jdfs.storage.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.storage.request.RemoveFileRequest;
import org.jdfs.storage.store.StoreService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 删除文件请求的处理器
 * 
 * @author James Quan
 * @version 2015年2月4日 下午3:20:26
 */
public class RemoveFileMessageHandler implements
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
		JdfsStatusResponse resp = new JdfsStatusResponse();
		resp.setBatchId(message.getBatchId());
		resp.setStatus(JdfsRequestConstants.STATUS_OK);
		session.write(resp);
	}
}
