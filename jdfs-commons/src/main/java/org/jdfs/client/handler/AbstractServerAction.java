package org.jdfs.client.handler;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketConnector;
import org.jdfs.commons.request.JdfsRequest;

/**
 * 通过远端服务器执行操作的操作基础实现
 * 
 * @author James Quan
 * @version 2015年2月25日 上午11:06:44
 */
public class AbstractServerAction implements Action {
	private SocketConnector connector;
	private SocketAddress serverAddress;

	private final Object mutex = new Object();
	private volatile boolean connected;
	private IoSession session;
	private JdfsRequest serverResult;

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

	@Override
	public Object process(Object request, Map<String, Object> context,
			ActionChain chain) {

		List<JdfsRequest> requestList = new ArrayList<JdfsRequest>();
		List<JdfsRequest> responseList = new ArrayList<JdfsRequest>();
		IoSession session = null;
		try {
			for (Iterator<JdfsRequest> requestIterator = getRequestIterator(
					request, context, chain); requestIterator.hasNext();) {
				if (session == null) {
					session = getSession();
				}
				JdfsRequest req = requestIterator.next();
				serverResult = null;
				synchronized (mutex) {
					session.write(req);
					try {
						mutex.wait();
					} catch (InterruptedException e) {
					}
				}
				if (!handleServerResponse(req, serverResult, requestIterator, chain)) {
					break;
				}
				requestList.add(req);
				responseList.add(serverResult);
			}
		} finally {
			if (session != null) {
				session.removeAttribute("callback");
				session.close(true);
				session = null;
				connected = false;
			}
		}

		Object ret = createResponse(request, context, requestList,
				responseList, chain);
		return chain.doAction(ret, context);
	}

	protected IoSession getSession() {
		if (connected) {
			return session;
		}
		synchronized (mutex) {
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

	protected Iterator<JdfsRequest> getRequestIterator(Object request,
			Map<String, Object> context, ActionChain chain) {
		return IteratorUtils
				.arrayIterator(new JdfsRequest[] { (JdfsRequest) request });
	}

	protected boolean handleServerResponse(JdfsRequest request,
			JdfsRequest response, Iterator<JdfsRequest> requestIterator,
			ActionChain chain) {
		return true;
	}

	/**
	 * 建立向应用返回的处理结果对象
	 * 
	 * @param request
	 *            原始的请求对象
	 * @param context
	 *            请求的上下文
	 * @param serverRequests
	 *            向服务器发送的请求对象集合
	 * @param serverResponses
	 *            从服务器返回的处理结果对象集合
	 * @param chain
	 *            操作链
	 * @return 向应用返回的处理结果对象
	 */
	protected Object createResponse(Object request,
			Map<String, Object> context, List<JdfsRequest> serverRequests,
			List<JdfsRequest> serverResponses, ActionChain chain) {
		return serverResponses;
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
