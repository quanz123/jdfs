package org.jdfs.client.handler;

import org.apache.mina.core.future.IoFuture;
import org.jdfs.commons.request.JdfsRequest;

public interface ActionFuture extends IoFuture{

	public JdfsRequest getResponse();
	
	public void setResponse(JdfsRequest resp);
}
