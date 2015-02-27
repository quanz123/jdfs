package org.jdfs.client.handler;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.mina.transport.socket.SocketConnector;
import org.jdfs.client.FileInfo;
import org.jdfs.commons.request.JdfsRequest;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.tracker.request.FileInfoResponse;
import org.jdfs.tracker.request.ReadFileInfoRequest;
import org.jdfs.tracker.request.RemoveFileInfoRequest;
import org.joda.time.DateTime;

/**
 * 删除文件信息的操作实现
 * 
 * @author James Quan
 * @version 2015年2月27日 上午11:18:19
 */
public class RemoveFileInfoAction extends AbstractServerAction {
	private long id;

	/**
	 * 返回待删除文件的id
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * 设置待删除文件的id
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	public RemoveFileInfoAction() {
		super();
	}

	/**
	 * 直接指定请求参数的构造函数
	 * 
	 * @param id
	 *            待删除文件的id
	 * @param trackerAddress
	 *            tracker服务器的地址
	 * @param connector
	 *            用于建立网络连接的{@link SocketConnector}
	 */
	public RemoveFileInfoAction(long id, SocketAddress trackerAddress,
			SocketConnector connector) {
		super();
		setId(id);
		setServerAddress(trackerAddress);
		setConnector(connector);
	}

	@Override
	protected Iterator<JdfsRequest> getRequestIterator(Object request,
			Map<String, Object> context, ActionChain chain) {
		RemoveFileInfoRequest req = new RemoveFileInfoRequest();
		req.setId(id);
		return IteratorUtils.arrayIterator(new JdfsRequest[] { req });
	}

	@Override
	protected Object createResponse(Object request,
			Map<String, Object> context, List<JdfsRequest> serverRequests,
			List<JdfsRequest> serverResponses, ActionChain chain) {
		if (serverResponses.size() != 1) {
			chain.throwException("remove file info error, illegal response",
					new IOException("remove file info error, illegal response"));
			return null;
		}
		JdfsStatusResponse resp = (JdfsStatusResponse) serverResponses.get(0);
		if (resp.getStatus() != JdfsRequestConstants.STATUS_OK) {
			chain.throwException("remove file info error", new IOException(
					"remove file info error: " + resp.getMessage()));
		}
		return null;
	}
}
