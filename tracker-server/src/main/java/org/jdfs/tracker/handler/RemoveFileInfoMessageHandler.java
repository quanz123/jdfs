package org.jdfs.tracker.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.tracker.request.RemoveFileInfoRequest;
import org.jdfs.tracker.service.FileInfoService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 删除文件信息消息处理器
 * 
 * @author James Quan
 * @version 2015年2月3日 下午5:15:29
 */
public class RemoveFileInfoMessageHandler implements
		MessageHandler<RemoveFileInfoRequest>, InitializingBean {
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
	public void handleMessage(IoSession session, RemoveFileInfoRequest message)
			throws Exception {
		long id = message.getId();
		fileInfoService.removeFileInfo(id);
		JdfsStatusResponse resp = new JdfsStatusResponse(
				JdfsRequestConstants.STATUS_OK);
		session.write(resp);		
	}
}
