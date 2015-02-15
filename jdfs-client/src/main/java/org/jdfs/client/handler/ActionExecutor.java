package org.jdfs.client.handler;

import java.net.SocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketConnector;
import org.jdfs.commons.request.JdfsRequest;

public class ActionExecutor {
	private SocketConnector connector;
	
	public SocketConnector getConnector() {
		return connector;
	}
	
	public void setConnector(SocketConnector connector) {
		this.connector = connector;
	}
	
	public void process(SocketAddress addr, JdfsRequest request){
		ConnectFuture cf = connector.connect(addr);
		cf.addListener(new ConnectFutureListener());
	}
	
	protected class ConnectFutureListener implements IoFutureListener<ConnectFuture> {		
		private JdfsRequest request;
		@Override
		public void operationComplete(ConnectFuture future) {
			IoSession session = future.getSession();
			session.setAttribute(key, value);
			session.write(request);
		}		
	}
	
	protected class ActionResponseCallback implements AsyncActionCallback {
		
	}
}
