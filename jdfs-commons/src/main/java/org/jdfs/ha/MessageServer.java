package org.jdfs.ha;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jdfs.tracker.service.ServerInfo;

public class MessageServer {
	private NioSocketConnector connector;
	private ConcurrentMap<String, SessionHolder> sessions = new ConcurrentHashMap<String, SessionHolder>();

	public NioSocketConnector getConnector() {
		return connector;
	}

	public void setConnector(NioSocketConnector connector) {
		this.connector = connector;
	}

	public void sendMessage(Object message, ServerInfo server) {
		SessionHolder holder = getSessionHolder(server.getName());
		holder.queue.add(message);
		boolean running = holder.running.getAndSet(true);
		if(!running) {
			synchronized(holder.mutex) {
				holder.mutex.notifyAll();
			}
		}
	}

	protected IoSession getSession(ServerInfo server) {
		final SessionHolder holder = getSessionHolder(server.getName());
		synchronized (holder.mutex) {
			if (holder.session != null) {
				return holder.session;
			}
			ConnectFuture cf = getConnector().connect(
					server.getServiceAddress());
			cf.addListener(new IoFutureListener<ConnectFuture>() {
				@Override
				public void operationComplete(ConnectFuture future) {
					synchronized (holder.mutex) {
						holder.session = future.getSession();
						holder.mutex.notifyAll();
					}
				}
			});
			try {
				holder.mutex.wait();
			} catch (InterruptedException e) {
			}
		}
		return holder.session;
	}

	protected SessionHolder getSessionHolder(String name) {
		SessionHolder holder = sessions.get(name);
		if (holder != null) {
			return holder;
		}
		SessionHolder newHolder = new SessionHolder();
		holder = sessions.putIfAbsent(name, newHolder);
		if (holder == null) {
			holder = newHolder;
		}
		return holder;
	}

	protected class SessionHolder {
		public Object mutex = new Object();
		public BlockingQueue queue = new LinkedBlockingQueue();
		public AtomicBoolean running = new AtomicBoolean(false);
		public IoSession session = null;
	}
}
