package org.jdfs.tracker.ha;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.tracker.request.RemoveFileInfoSyncRequest;
import org.jdfs.tracker.service.FileInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 删除文件信息消息处理器
 * 
 * @author James Quan
 * @version 2015年2月3日 下午5:15:29
 */
public class RemoveFileInfoSyncMessageHandler implements
		MessageHandler<RemoveFileInfoSyncRequest>, InitializingBean {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private FileInfoService fileInfoService;

	public FileInfoService getFileInfoService() {
		return fileInfoService;
	}

	public void setFileInfoService(FileInfoService fileInfoService) {
		this.fileInfoService = fileInfoService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(fileInfoService, "fileInfoService is required!");
	}

	@Override
	public void handleMessage(IoSession session, RemoveFileInfoSyncRequest message)
			throws Exception {
		long id = message.getId();
		fileInfoService.removeFileInfo(id);
		JdfsStatusResponse resp = new JdfsStatusResponse();
		resp.setBatchId(message.getBatchId());
		resp.setStatus(JdfsRequestConstants.STATUS_OK);
		session.write(resp);
		logger.debug("sync file remove {}", id);
	}
}
