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
import org.jdfs.tracker.request.UpdateFileInfoRequest;
import org.jdfs.tracker.service.ServerInfo;

/**
 * 保存文件信息的操作实现
 * 
 * @author James Quan
 * @version 2015年2月25日 下午5:18:19
 */
public class SaveFileInfoAction extends AbstractServerAction {

	private FileInfo file;

	public FileInfo getFile() {
		return file;
	}

	public void setFile(FileInfo file) {
		this.file = file;
	}

	public SaveFileInfoAction() {
		super();
	}

	/**
	 * 指定服务器地址的构造函数
	 * 
	 * @param trackerAddress
	 *            tracker服务器的地址
	 * @param connector
	 *            用于建立网络连接的{@link SocketConnector}
	 */
	public SaveFileInfoAction(SocketAddress trackerAddress,
			SocketConnector connector) {
		super();
		setFile(file);
		setServerAddress(trackerAddress);
		setConnector(connector);
	}
	
	/**
	 * 直接指定请求参数的构造函数
	 * 
	 * @param file
	 *            待保存的文件信息
	 * @param trackerAddress
	 *            tracker服务器的地址
	 * @param connector
	 *            用于建立网络连接的{@link SocketConnector}
	 */
	public SaveFileInfoAction(FileInfo file, SocketAddress trackerAddress,
			SocketConnector connector) {
		super();
		setFile(file);
		setServerAddress(trackerAddress);
		setConnector(connector);
	}

	@Override
	protected Iterator<JdfsRequest> getRequestIterator(Object request,
			Map<String, Object> context, ActionChain chain) {
		if (request instanceof FileInfo) {
			file = (FileInfo) request;
		}
		ServerInfo server = (ServerInfo) context.get("server");
		UpdateFileInfoRequest req = new UpdateFileInfoRequest();
		req.setId(file.getId());
		req.setName(file.getName());
		if(server != null) {
			req.setGroup(server.getGroup());			
		}
		req.setSize(file.getSize());
		req.setLastModified(file.getLastModified().getMillis());
		return IteratorUtils.arrayIterator(new JdfsRequest[] { req });
	}
	

	@Override
	protected Object createResponse(Object request,
			Map<String, Object> context, List<JdfsRequest> serverRequests,
			List<JdfsRequest> serverResponses, ActionChain chain) {
		if (serverResponses.size() != 1) {
			chain.throwException(
					"save file info error, illegal response",
					new IOException("save file info error, illegal response"));
			return null;
		}
		JdfsStatusResponse resp = (JdfsStatusResponse) serverResponses.get(0);
		if (resp.getStatus() != JdfsRequestConstants.STATUS_OK) {
			chain.throwException("save file info error", new IOException(
					"save file info error: " + resp.getMessage()));
			return null;
		} else {
			return new FileInfo(file);
		}
	}
}
