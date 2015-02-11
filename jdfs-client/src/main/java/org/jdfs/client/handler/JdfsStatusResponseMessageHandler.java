package org.jdfs.client.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.springframework.beans.factory.InitializingBean;

public class JdfsStatusResponseMessageHandler implements
		MessageHandler<JdfsStatusResponse>, InitializingBean {
	public static String COMMAND_STATE = "commandState";
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

	@Override
	public void handleMessage(IoSession session, JdfsStatusResponse message)
			throws Exception {
		CommandState state = (CommandState) session.getAttribute(COMMAND_STATE);
		if(message.getStatus() == JdfsRequestConstants.STATUS_OK) {
			
		}
	}

}
