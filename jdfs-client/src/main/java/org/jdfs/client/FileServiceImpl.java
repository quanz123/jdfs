package org.jdfs.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jdfs.storage.request.UpdateFileRequest;
import org.jdfs.tracker.request.GetUploadServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class FileServiceImpl implements FileService, InitializingBean,
		DisposableBean {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private int chunkSize = 64 * 1024;
	private long connectTimeout = 2000 * 1000l;
	private InetSocketAddress[] trackers = new InetSocketAddress[] { new InetSocketAddress(
			"localhost", 2200) };
	private ProtocolCodecFactory codecFactory;
	private NioSocketConnector connector;
	private IoHandler handler;
	
	private boolean needDestroy=false;
	private int currentTracker = 0;

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int maxChunkSize) {
		this.chunkSize = maxChunkSize;
	}

	public int getConnectTimeout() {
		return (int)(connectTimeout/ 1000l);
	}
	
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout * 1000l;
	}
	
	public String[] getTrackers() {
		if(trackers == null || trackers.length == 0) {
			return new String[0];
		}
		String[] result = new String[trackers.length];
		for(int i = 0; i < trackers.length; i++) {
			result[i] = trackers[i].toString();
		}
		return result;
	}

	public void setTrackers(String[] trackers) {
		if(trackers == null || trackers.length == 0) {
			this.trackers = null;
		}
		List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
		for (String tracker : trackers) {
			tracker = tracker.trim();
			int colon = tracker.indexOf(':');
			String host = colon == -1 ? tracker : tracker.substring(0, colon);
			int port = colon == -1 ? 2200 : Integer.parseInt(tracker
					.substring(colon + 1));
			InetSocketAddress addr = new InetSocketAddress(host, port);
			list.add(addr);
		}
		this.trackers = list.toArray(new InetSocketAddress[list.size()]);
	}

	public ProtocolCodecFactory getCodecFactory() {
		return codecFactory;
	}

	public void setCodecFactory(ProtocolCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
	}

	public NioSocketConnector getConnector() {
		return connector;
	}
	
	public void setConnector(NioSocketConnector connector) {
		this.connector = connector;
	}
	
	public IoHandler getHandler() {
		return handler;
	}
	
	public void setHandler(IoHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (trackers == null || trackers.length == 0) {
			throw new IllegalArgumentException("trackers is required!");
		}
		if(connector != null) {
			Assert.notNull(handler, "handler is required!");
			// 创建客户端连接器.
			needDestroy = true;
			connector = new NioSocketConnector();
			connector.getFilterChain().addLast("logger", new LoggingFilter());
			connector.getFilterChain().addLast("codec",
					new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
			connector.setHandler(handler);// 设置事件处理器
		}
	}

	@Override
	public void destroy() throws Exception {
		if(needDestroy) {
			connector.dispose();
		}
	}

	@Override
	public FileInfo updateFile(long id, String name, InputStream data,
			long offset, long size) throws IOException{
		InetSocketAddress tracker = getTrackerAddress();
		ConnectFuture cf = connector.connect(tracker);
		if(!cf.awaitUninterruptibly(connectTimeout)){
			logger.error("connect tracker {} error!", tracker);
			throw new IOException("connect tracker " + tracker + " error!");
		}
		IoSession session = cf.getSession();
		GetUploadServerRequest request = new GetUploadServerRequest();
		request.setId(id);
		WriteFuture wf = session.write(request);
		
		
		int c= (int) Math.ceil(size / chunkSize);
		// TODO Auto-generated method stub
		return null;
	}

	
	protected InetSocketAddress getTrackerAddress() {
		if(trackers.length == 1) {
			return trackers[0];
		}
		InetSocketAddress tracker = trackers[0];
		currentTracker = currentTracker + 1 % trackers.length;
		return tracker;
	}
	
}
