package org.jdfs.tracker.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.tracker.request.GetDownloadServerRequest;
import org.jdfs.tracker.service.TrackerService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 返回数据上传服务器地址请求的处理器
 * 
 * @author James Quan
 * @version 2015年2月8日 下午3:38:19
 */
public class GetDownloadServerMessageHandler  implements
MessageHandler<GetDownloadServerRequest>, InitializingBean {
	private TrackerService trackerService;
	
	public TrackerService getTrackerService() {
		return trackerService;
	}
	
	public void setTrackerService(TrackerService trackerService) {
		this.trackerService = trackerService;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(trackerService, "trackerService is required!");
	}
	
	@Override
	public void handleMessage(IoSession session, GetDownloadServerRequest message)
			throws Exception {
		long id = message.getId();
		String address = trackerService.getDownloadServerAddress(id);
		if(StringUtils.isEmpty(address)) {
			JdfsStatusResponse resp = new JdfsStatusResponse(
					JdfsRequestConstants.STATUS_STORAGE_NOT_FOUND);
			session.write(resp);
		} else {
			JdfsStatusResponse resp = new JdfsStatusResponse(
					JdfsRequestConstants.STATUS_OK);
			resp.setMessage(address);
			session.write(resp);
			return;
		}
	}	
}
