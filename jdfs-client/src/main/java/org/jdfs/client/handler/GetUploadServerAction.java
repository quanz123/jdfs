package org.jdfs.client.handler;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.mina.transport.socket.SocketConnector;
import org.jdfs.commons.request.JdfsRequest;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.tracker.request.GetUploadServerRequest;
import org.jdfs.tracker.service.ServerInfo;

/**
 * 获取可用上传服务器地址的操作实现
 * 
 * @author James Quan
 * @version 2015年2月25日 上午10:36:47
 */
public class GetUploadServerAction extends AbstractServerAction {
	private long id;

	/**
	 * 返回待上传文件的id
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * 设置待上传文件的id
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	public GetUploadServerAction() {
		super();
	}

	/**
	 * 直接指定请求参数的构造函数
	 * 
	 * @param id
	 *            待上传文件的id
	 * @param trackerAddress
	 *            tracker服务器的地址
	 * @param connector
	 *            用于建立网络连接的{@link SocketConnector}
	 */
	public GetUploadServerAction(long id, SocketAddress trackerAddress,
			SocketConnector connector) {
		super();
		setId(id);
		setServerAddress(trackerAddress);
		setConnector(connector);
	}

	@Override
	protected Iterator<JdfsRequest> getRequestIterator(Object request,
			Map<String, Object> context, ActionChain chain) {
		GetUploadServerRequest req = new GetUploadServerRequest();
		req.setId(id);
		return IteratorUtils.arrayIterator(new JdfsRequest[] { req });
	}

	@Override
	protected Object createResponse(Object request,
			Map<String, Object> context, List<JdfsRequest> serverRequests,
			List<JdfsRequest> serverResponses, ActionChain chain) {
		if (serverResponses.size() != 1) {
			chain.throwException(
					"get upload server error, illegal response",
					new IOException("get upload server error, illegal response"));
			return null;
		}
		JdfsStatusResponse resp = (JdfsStatusResponse) serverResponses.get(0);
		if (resp.getStatus() != JdfsRequestConstants.STATUS_OK) {
			chain.throwException("get upload server error", new IOException(
					"get upload server error: " + resp.getMessage()));
			return null;
		} else {
			String message = resp.getMessage();
			ServerInfo server = ServerInfo.parse(message);
			return server;
		}
	}
}
