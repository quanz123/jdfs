package org.jdfs.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jdfs.client.handler.CommandChainHolder;
import org.jdfs.client.handler.CommandChainImpl;
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
	private SocketConnector trackerConnector;
	private SocketConnector storageConnector;
	private IoHandler trackerHandler;
	private IoHandler storageHandler;
	
	private boolean needDestroyTrackerConnector=false;
	private boolean needDestroyStorageConnector=false;
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

	public SocketConnector getTrackerConnector() {
		return trackerConnector;
	}
	
	public void setTrackerConnector(SocketConnector connector) {
		this.trackerConnector = connector;
	}
	
	public SocketConnector getStorageConnector() {
		return storageConnector;
	}
	
	public void setStorageConnector(SocketConnector storageConnector) {
		this.storageConnector = storageConnector;
	}
	
	public IoHandler getTrackerHandler() {
		return trackerHandler;
	}
	
	public void setTrackerHandler(IoHandler handler) {
		this.trackerHandler = handler;
	}
	
	public IoHandler getStorageHandler() {
		return storageHandler;
	}
	
	public void setStorageHandler(IoHandler storageHandler) {
		this.storageHandler = storageHandler;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (trackers == null || trackers.length == 0) {
			throw new IllegalArgumentException("trackers is required!");
		}
		if(trackerConnector == null) {
			Assert.notNull(trackerHandler, "trackerHandler is required!");
			// 创建客户端连接器.
			needDestroyTrackerConnector = true;
			trackerConnector = new NioSocketConnector();
			trackerConnector.getFilterChain().addLast("logger", new LoggingFilter());
			trackerConnector.getFilterChain().addLast("codec",
					new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
			trackerConnector.setHandler(trackerHandler);// 设置事件处理器
		}		if(storageConnector == null) {
			Assert.notNull(storageHandler, "storageHandler is required!");
			// 创建客户端连接器.
			needDestroyStorageConnector = true;
			storageConnector = new NioSocketConnector();
			storageConnector.getFilterChain().addLast("logger", new LoggingFilter());
			storageConnector.getFilterChain().addLast("codec",
					new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
			storageConnector.setHandler(storageHandler);// 设置事件处理器
		}
	}

	@Override
	public void destroy() throws Exception {
		if(needDestroyTrackerConnector) {
			trackerConnector.dispose();
		}
		if(needDestroyStorageConnector) {
			storageConnector.dispose();
		}
	}

	@Override
	public FileInfo updateFile(long id, String name, InputStream data,
			long offset, long size) throws IOException{
		InetSocketAddress tracker = getTrackerAddress();
		ConnectFuture cf = trackerConnector.connect(tracker);
		if(!cf.awaitUninterruptibly(connectTimeout)){
			logger.error("connect tracker {} error!", tracker);
			throw new IOException("connect tracker " + tracker + " error!");
		}
		IoSession session = cf.getSession();
		CommandChainHolder holder = (CommandChainHolder) session.getAttribute(FileIoHandler.COMMAND_CHAIN);
		if(holder == null) {
			holder = new CommandChainHolder();
			session.setAttribute(FileIoHandler.COMMAND_CHAIN, holder);
		}
		CommandChainImpl chain = new CommandChainImpl();
		holder.addChain(chain);
		chain.addCommand(new GetUploadServerCommand());
		UploadFileCommand ufc = new UploadFileCommand();
		ufc.setStorageConnector(storageConnector);
		chain.addCommand(ufc);
		
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("name", name);
		ctx.put("data", data);
		ctx.put("offset", offset);
		ctx.put("size", size);
		
		GetUploadServerRequest request = new GetUploadServerRequest();
		request.setId(id);		
		chain.doCommand(session, request, ctx);
		try {
			chain.getFuture().await();
		} catch (InterruptedException e) {
			throw new IOException("error on updateFile", e);
		}
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
