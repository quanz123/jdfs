package org.jdfs.tracker.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.tracker.request.FileInfoResponse;
import org.jdfs.tracker.request.ReadFileInfoRequest;
import org.jdfs.tracker.service.FileInfo;
import org.jdfs.tracker.service.FileInfoService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 读取文件信息消息处理器
 * 
 * @author James Quan
 * @version 2015年2月3日 下午5:15:29
 */
public class ReadFileInfoMessageHandler implements
		MessageHandler<ReadFileInfoRequest>, InitializingBean {
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
	public void handleMessage(IoSession session, ReadFileInfoRequest message)
			throws Exception {
		long id = message.getId();
		FileInfo file = fileInfoService.getFileInfo(id);
		FileInfoResponse resp;
		if (file == null) {
			resp = new FileInfoResponse();
			resp.setBatchId(message.getBatchId());
			resp.setStatus(JdfsRequestConstants.STATUS_FILE_NOT_FOUND);
		} else {
			resp = new FileInfoResponse();
			resp.setBatchId(message.getBatchId());
			resp.setStatus(JdfsRequestConstants.STATUS_OK);
			resp.setId(file.getId());
			resp.setName(file.getName());
			resp.setSize(file.getSize());
			resp.setLastModified(file.getLastModified().getMillis());
		}
		session.write(resp);
	}
}
