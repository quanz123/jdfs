package org.jdfs.storage.client;

import java.net.InetSocketAddress;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jdfs.commons.request.JdfsDataResponse;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.storage.request.ReadFileRequest;
import org.jdfs.storage.request.RemoveFileRequest;
import org.jdfs.storage.request.UpdateFileRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StorageClientTest {

	private ProtocolCodecFactory codecFactory;

	public ProtocolCodecFactory getCodecFactory() {
		return codecFactory;
	}

	@Autowired
	public void setCodecFactory(ProtocolCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
	}

	@Before
	public void init() throws Exception {

	}

	@Test
	public void testUpdate() throws Exception {
		// 创建客户端连接器.
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
		UpdateFileIoHandler handler = new UpdateFileIoHandler();
		connector.setHandler(handler);// 设置事件处理器
		ConnectFuture cf = connector.connect(new InetSocketAddress("127.0.0.1",
				2210));// 建立连接
		cf.awaitUninterruptibly();// 等待连接创建完成
		StringBuilder b = new StringBuilder();
		b.append("测试test信息123:").append(new Date()).append('\n');
		char[] buf = new char[1024];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (char) ((int) '0' + i % 10);
		}
		String line = new String(buf);
		for (int i = 0; i < 10; i++) {
			b.append("line ").append(i).append('\n').append(line).append('\n');
		}
		b.append("----------------------------------------finished-------------------------------------------------");
		UpdateFileRequest request = new UpdateFileRequest();
		request.setId(100l);
		request.setData(b.toString().getBytes("UTF-8"));
		//request.setSize(request.getData().length);
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
		ConnectFuture cf = connector.connect(new InetSocketAddress("127.0.0.1",
				2010));// 建立连接
		cf.awaitUninterruptibly();// 等待连接创建完成
		ReadFileRequest request = new ReadFileRequest();
		request.setId(100l);
		WriteFuture wf = cf.getSession().write(request);// 发送消息
		wf.await();
		Thread.sleep(2000);
	}

	@Test
	public void testRemove() throws Exception {
		// 创建客户端连接器.
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
		UpdateFileIoHandler handler = new UpdateFileIoHandler();
		connector.setHandler(handler);// 设置事件处理器
		ConnectFuture cf = connector.connect(new InetSocketAddress("127.0.0.1",
				2210));// 建立连接
		cf.awaitUninterruptibly();// 等待连接创建完成
		RemoveFileRequest request = new RemoveFileRequest();
		request.setId(100l);
		WriteFuture wf = cf.getSession().write(request);// 发送消息
		wf.await();
		Thread.sleep(2000);
		cf.getSession().close(true);
		cf.getSession().getCloseFuture().awaitUninterruptibly();// 等待连接断开
		connector.dispose();
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
			if (message instanceof JdfsDataResponse) {
				String msg = new String(((JdfsDataResponse) message).getData(),
						"UTF-8");
				System.out.println("recv data: "
						+ StringUtils.abbreviate(msg, 100));
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