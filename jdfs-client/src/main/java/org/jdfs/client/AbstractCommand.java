package org.jdfs.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.FutureTask;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketConnector;
import org.jdfs.client.handler.Command;
import org.jdfs.client.handler.CommandChain;
import org.jdfs.commons.request.JdfsRequest;

public class AbstractCommand implements Command {
	private SocketConnector connector;
	private long connectTimeout = 10 * 1000l;

	private IoSession session;
	private boolean needClose;
	
	private ConcurrentMap<String, Map<String, Object>> sessions ;

	public void process(JdfsRequest  request, Map<String, Object> context, IoSession session) throws IOException{
		int batchId = request.getBatchId();
		sessions.get(batchId);
		FutureTask task;
		
		
		
		if(session != null) {
			this.session = session;
			needClose = 
		} else {
			
		}
		session.write(message);
	}

	public void handleResponse(IoSession session, Object message) {
		
	}
	
	protected IoSession getSession() throws IOException {
		SocketAddress addr = null;
		ConnectFuture cf = connector.connect(addr);
		if (cf.awaitUninterruptibly(connectTimeout)) {
			return cf.getSession();
		} else {
			if (cf.getException() != null) {
				throw new IOException("connect error!", cf.getException());
			} else {
				throw new IOException("connect error: timeout!");
			}
		}
	}

	@Override
	public void process(IoSession session, Object message,
			Map<String, Object> data, CommandChain chain) {
		// TODO Auto-generated method stub

	}

}
