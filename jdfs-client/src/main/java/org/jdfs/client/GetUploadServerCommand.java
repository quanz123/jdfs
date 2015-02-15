package org.jdfs.client;

import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.jdfs.client.handler.Command;
import org.jdfs.client.handler.CommandChain;
import org.jdfs.tracker.request.GetUploadServerRequest;

public class GetUploadServerCommand implements Command {

	@Override
	public void process(IoSession session, Object message, Map<String, Object> data, CommandChain chain) {
		GetUploadServerRequest request = (GetUploadServerRequest) message;
		request.setBatchId(chain.getId());
		session.write(request);
	}
	
	
}
