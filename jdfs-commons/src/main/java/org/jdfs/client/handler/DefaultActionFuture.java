package org.jdfs.client.handler;

import org.apache.mina.core.future.DefaultIoFuture;
import org.apache.mina.core.session.IoSession;
import org.jdfs.commons.request.JdfsRequest;

public class DefaultActionFuture extends DefaultIoFuture implements
		ActionFuture {
	
	public DefaultActionFuture(IoSession session) {
		super(session);
	}

	@Override
	public JdfsRequest getResponse() {
		return (JdfsRequest) getValue();
	}
	
	@Override
	public void setResponse(JdfsRequest resp) {
		setValue(resp);
	}
}
