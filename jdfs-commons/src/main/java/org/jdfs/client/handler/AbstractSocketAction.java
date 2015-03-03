package org.jdfs.client.handler;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketConnector;
import org.jdfs.commons.request.JdfsRequest;

public abstract class AbstractSocketAction implements Action {
	/** A number of seconds to wait between two deadlock controls ( 5 seconds ) */
	private static final long DEAD_LOCK_CHECK_INTERVAL = 5000L;

	protected final Object mutex = new Object();
	private SocketConnector connector;
	private SocketAddress serverAddress;
	protected volatile boolean connected;

	protected IoSession session;

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
//			cf.addListener(new IoFutureListener<ConnectFuture>() {
//				@Override
//				public void operationComplete(ConnectFuture future) {
//					synchronized (mutex) {
//						session = future.getSession();
//						// ActionResponseCallback cb = new
//						// ActionResponseCallback();
//						// ActionFuture cb = new DefaultActionFuture(session);
//						// session.setAttribute("callback", cb);
//						Map<Integer, ActionFuture> map = new ConcurrentHashMap<Integer, ActionFuture>();
//						session.setAttribute("callbackHolder", map);
//						connected = true;
//						// mutex.notifyAll();
//					}
//				}
//			});
			cf.awaitUninterruptibly();
			session = cf.getSession();
			Map<Integer, ActionFuture> map = new ConcurrentHashMap<Integer, ActionFuture>();
			session.setAttribute("callbackHolder", map);
			connected = true;
			
			// try {
			// mutex.wait();
			// } catch (InterruptedException e) {
			// }
		}
		return session;
	}

	protected void closeSession() {
		synchronized (mutex) {
			if (connected) {
				session.close(false);
				session = null;
				connected = false;
			}
		}
	}

	protected ActionFuture sendRequest(JdfsRequest request, IoSession session) {
		ActionFuture future = null;
		Map<Integer, ActionFuture> map = (Map<Integer, ActionFuture>) session
				.getAttribute("callbackHolder");
		if (map != null) {
			int batchId = request.getBatchId();
			future = new DefaultActionFuture(session);
			map.put(batchId, future);
		}
		session.write(request);
		return future;
	}

	// protected class ActionResponseCallback implements ServerActionCallback {
	// @Override
	// public Object handleResponse(JdfsRequest response, IoSession session) {
	// serverResult = response;
	// synchronized (mutex) {
	// mutex.notifyAll();
	// }
	// return response;
	// }
	// }
}