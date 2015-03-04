package org.jdfs.tracker.client;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsServerInfoRequest;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.storage.request.ReadFileRequest;
import org.jdfs.tracker.request.FileInfoResponse;
import org.jdfs.tracker.request.GetDownloadServerRequest;
import org.jdfs.tracker.request.GetUploadServerRequest;
import org.jdfs.tracker.request.ReadFileInfoRequest;
import org.jdfs.tracker.request.RemoveFileInfoRequest;
import org.jdfs.tracker.request.UpdateFileInfoRequest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TrackerClientTest {

	private ProtocolCodecFactory codecFactory;
	private String host = "localhost";
	private int port = 2200;

	public ProtocolCodecFactory getCodecFactory() {
		return codecFactory;
	}

	@Autowired
	public void setCodecFactory(ProtocolCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Before
	public void init() throws Exception {

	}

	public void testUpdate() throws Exception {
		// 创建客户端连接器.
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
		UpdateFileIoHandler handler = new UpdateFileIoHandler();
		connector.setHandler(handler);// 设置事件处理器
		ConnectFuture cf = connector.connect(new InetSocketAddress(host, port));// 建立连接
		cf.awaitUninterruptibly();// 等待连接创建完成
		long now = DateTime.now().getMillis();
		UpdateFileInfoRequest request = new UpdateFileInfoRequest();
		request.setId(100l);
		request.setName("file测试_" + now);
		request.setSize(100);
		request.setLastModified(now);
		// request.setSize(request.getData().length);
		WriteFuture wf = cf.getSession().write(request);// 发送消息
		wf.await();
		Thread.sleep(2000);
		cf.getSession().close(true);
		cf.getSession().getCloseFuture().awaitUninterruptibly();// 等待连接断开
		connector.dispose();
	}

	@Test
	public void testRead() throws Exception {
		// 创建客户端连接器.
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
		ReadFileIoHandler handler = new ReadFileIoHandler(connector);
		connector.setHandler(handler);// 设置事件处理器
		ConnectFuture cf = connector.connect(new InetSocketAddress(host, port));// 建立连接
		cf.awaitUninterruptibly();// 等待连接创建完成
		ReadFileInfoRequest request = new ReadFileInfoRequest();
		request.setId(100l);
		request.setBatchId(10);
		WriteFuture wf = cf.getSession().write(request);// 发送消息
		wf.await();
		Thread.sleep(2000);
	}

	public void testRemove() throws Exception {
		// 创建客户端连接器.
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
		UpdateFileIoHandler handler = new UpdateFileIoHandler();
		connector.setHandler(handler);// 设置事件处理器
		ConnectFuture cf = connector.connect(new InetSocketAddress(host, port));// 建立连接
		cf.awaitUninterruptibly();// 等待连接创建完成
		RemoveFileInfoRequest request = new RemoveFileInfoRequest();
		request.setId(100l);
		WriteFuture wf = cf.getSession().write(request);// 发送消息
		wf.await();
		Thread.sleep(2000);
		cf.getSession().close(true);
		cf.getSession().getCloseFuture().awaitUninterruptibly();// 等待连接断开
		connector.dispose();
	}

	public void testGetDownloadServer() throws Exception {
		// 创建客户端连接器.
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
		ReadFileIoHandler handler = new ReadFileIoHandler(connector);
		connector.setHandler(handler);// 设置事件处理器
		ConnectFuture cf = connector.connect(new InetSocketAddress(host, port));// 建立连接
		cf.awaitUninterruptibly();// 等待连接创建完成
		GetDownloadServerRequest request = new GetDownloadServerRequest();
		request.setId(100l);
		request.setBatchId(10);
		WriteFuture wf = cf.getSession().write(request);// 发送消息
		wf.await();
		Thread.sleep(2000);
	}

	public void testGetUploadServer() throws Exception {
		// 创建客户端连接器.
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
		ReadFileIoHandler handler = new ReadFileIoHandler(connector);
		connector.setHandler(handler);// 设置事件处理器
		ConnectFuture cf = connector.connect(new InetSocketAddress(host, port));// 建立连接
		cf.awaitUninterruptibly();// 等待连接创建完成
		GetUploadServerRequest request = new GetUploadServerRequest();
		request.setId(200l);
		request.setBatchId(10);
		WriteFuture wf = cf.getSession().write(request);// 发送消息
		wf.await();
		Thread.sleep(2000);
	}

	public void testMultiServer() throws Exception {
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
		ReadFileIoHandler handler = new ReadFileIoHandler(connector);
		connector.setHandler(handler);// 设置事件处理器
		ConnectFuture cf = connector.connect(new InetSocketAddress(host, port));// 建立连接
		cf.awaitUninterruptibly();// 等待连接创建完成
		GetUploadServerRequest request = new GetUploadServerRequest();
		request.setId(200l);
		request.setBatchId(10);
		WriteFuture wf = cf.getSession().write(request);// 发送消息
		wf.await();
		Thread.sleep(2000);
	}

	protected class UpdateFileIoHandler extends IoHandlerAdapter {

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			if (message instanceof JdfsStatusResponse) {
				System.out.println("recv resp: "
						+ ((JdfsStatusResponse) message).getStatus());
			} else {
				System.out.println("recv: " + message);
			}
		}

		@Override
		public void messageSent(IoSession session, Object message)
				throws Exception {
			System.out.println("sent: " + message);
		}
	}

	protected class ReadFileIoHandler extends IoHandlerAdapter {
		private NioSocketConnector connector;

		public ReadFileIoHandler(NioSocketConnector connector) {
			super();
			this.connector = connector;
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			if (message instanceof FileInfoResponse) {
				FileInfoResponse resp = (FileInfoResponse) message;
				System.out.println("recv file info: ");
				System.out.println("batchId: " + resp.getBatchId());
				System.out.println("status: " + resp.getStatus());
				System.out.println("id: " + resp.getId());
				System.out.println("name: " + resp.getName());
				System.out.println("size: " + resp.getSize());
				System.out.println("lastModified: "
						+ new DateTime(resp.getLastModified())
								.toString("yyyy-M-d H:mm:ss"));
			} else if (message instanceof JdfsStatusResponse) {
				JdfsStatusResponse resp = (JdfsStatusResponse) message;
				System.out.println("recv status:");
				System.out.println("batchId: " + resp.getBatchId());
				System.out.println("status: " + resp.getStatus());
				System.out.println("message: " + resp.getMessage());
			} else {
				System.out.println("recv: " + message);
			}
			closeSession(session);
		}

		@Override
		public void messageSent(IoSession session, Object message)
				throws Exception {
			System.out.println("sent: " + message);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			closeSession(session);
		}

		protected void closeSession(IoSession session) {
			session.close(true);
			session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
			if (connector != null) {
				connector.dispose();
				connector = null;
			}
		}
	}
}