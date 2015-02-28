package org.jdfs.client.handler;

import java.net.SocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketConnector;
import org.jdfs.commons.request.JdfsRequest;

public abstract class AbstractSocketAction  implements Action{
	protected final Object mutex = new Object();
	private SocketConnector connector;
	private SocketAddress serverAddress;
	protected volatile boolean connected;
	
	protected IoSession session;
	protected JdfsRequest serverResult;
	
	public AbstractSocketAction() {
		super();
	}

	public SocketConnector getConnector() {
		return connector;
	}

	public void setConnector(SocketConnector connector) {
		this.connector = connector;
	}

	public SocketAddress getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(SocketAddress addr) {
		this.serverAddress = addr;
	}

	protected IoSession getSession() {
		synchronized (mutex) {
			if (connected) {
				return session;
			}
			ConnectFuture cf = getConnector().connect(getServerAddress());
			cf.addListener(new IoFutureListener<ConnectFuture>() {
				@Override
				public void operationComplete(ConnectFuture future) {
					synchronized (mutex) {
						session = future.getSession();
						ActionResponseCallback cb = new ActionResponseCallback();
						session.setAttribute("callback", cb);
						connected = true;
						mutex.notifyAll();
					}
				}
			});
			try {
				mutex.wait();
			} catch (InterruptedException e) {
			}
		}
		return session;
	}
	
	protected void closeSession() {
		synchronized (mutex) {
			if(connected) {
				session.close(false);
				session = null;
				connected = false;
			}
		}
	}
	
	protected class ActionResponseCallback implements ServerActionCallback {
		@Override
		public Object handleResponse(JdfsRequest response, IoSession session) {
			serverResult = response;
			synchronized (mutex) {
				mutex.notifyAll();
			}
			return response;
		}
	}
}