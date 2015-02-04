package org.jdfs.tracker.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.tracker.request.RemoveFileInfoRequest;
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
		FileInfo file = fileInfoService.getFileInfo(id);
		if(file == null) {
			
		} else {
			
		}
		JdfsStatusResponse resp = new JdfsStatusResponse(
				JdfsRequestConstants.STATUS_FILE_NOT_FOUND);
		session.write(resp);		
	}
}
