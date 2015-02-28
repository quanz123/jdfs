package org.jdfs.client.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.mina.core.session.IoSession;
import org.jdfs.commons.request.JdfsRequest;

/**
 * 通过远端服务器执行操作的操作基础实现
 * 
 * @author James Quan
 * @version 2015年2月25日 上午11:06:44
 */
public class AbstractServerAction extends AbstractSocketAction {

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

		Object response = createResponse(request, context, requestList,
				responseList, chain);
		return processInternal(request, response, context, chain);
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
	
	protected Object processInternal(Object request, Object response, Map<String, Object> context,
			ActionChain chain) {
		return chain.doAction(response, context);
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


}
