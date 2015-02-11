package org.jdfs.tracker.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.tracker.request.UpdateFileInfoRequest;
import org.jdfs.tracker.service.FileInfo;
import org.jdfs.tracker.service.FileInfoService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 保存文件信息消息处理器
 * 
 * @author James Quan
 * @version 2015年2月3日 下午5:15:29
 */
public class UpdateFileInfoMessageHandler implements
		MessageHandler<UpdateFileInfoRequest>, InitializingBean {
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
	public void handleMessage(IoSession session, UpdateFileInfoRequest message)
			throws Exception {
		FileInfo file = new FileInfo();
		file.setId(message.getId());
		file.setName(message.getName());
		file.setSize(message.getSize());
		file.setLastModified(new DateTime(message.getLastModified()));
		fileInfoService.updateFileInfo(file);
		JdfsStatusResponse resp = new JdfsStatusResponse();
		resp.setBatchId(message.getBatchId());
		resp.setStatus(JdfsRequestConstants.STATUS_OK);
		session.write(resp);
	}
}
