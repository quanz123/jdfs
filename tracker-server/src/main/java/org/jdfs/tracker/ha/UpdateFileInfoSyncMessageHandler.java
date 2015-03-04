package org.jdfs.tracker.ha;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.tracker.request.UpdateFileInfoSyncRequest;
import org.jdfs.tracker.service.FileInfo;
import org.jdfs.tracker.service.FileInfoSyncService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 保存文件信息消息处理器
 * 
 * @author James Quan
 * @version 2015年2月3日 下午5:15:29
 */
public class UpdateFileInfoSyncMessageHandler implements
		MessageHandler<UpdateFileInfoSyncRequest>, InitializingBean {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private FileInfoSyncService fileInfoService;

	public FileInfoSyncService getFileInfoService() {
		return fileInfoService;
	}

	public void setFileInfoService(FileInfoSyncService fileInfoService) {
		this.fileInfoService = fileInfoService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(fileInfoService, "fileInfoService is required!");
	}

	@Override
	public void handleMessage(IoSession session, UpdateFileInfoSyncRequest message)
			throws Exception {
		long id = message.getId();
		String name = message.getName();
		int group = message.getGroup();
		long size = message.getSize();
		DateTime lastModified = new DateTime(message.getLastModified());
		FileInfo file = new FileInfo();
		file.setId(message.getId());
		file.setName(message.getName());
		file.setSize(message.getSize());
		file.setLastModified(new DateTime(message.getLastModified()));
		fileInfoService.syncUpdateFileName(id, name);
		fileInfoService.syncUpdateFileDataInfo(id, group, size, lastModified);
		JdfsStatusResponse resp = new JdfsStatusResponse();
		resp.setBatchId(message.getBatchId());
		resp.setStatus(JdfsRequestConstants.STATUS_OK);
		session.write(resp);
		logger.debug("sync file update {} - {}", id, name);
	}
}
