package org.jdfs.storage.client;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jdfs.storage.request.RemoveFileRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StorageClientTest extends IoHandlerAdapter {

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
	public void testRemove() throws Exception {
		// 创建客户端连接器.
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(codecFactory)); // 设置编码过滤器
		connector.setHandler(this);// 设置事件处理器
		ConnectFuture cf = connector.connect(new InetSocketAddress("127.0.0.1",
				2010));// 建立连接
		cf.awaitUninterruptibly();// 等待连接创建完成
		RemoveFileRequest request = new RemoveFileRequest(100l);
		cf.getSession().write(request);// 发送消息
		cf.getSession().close(true);
		cf.getSession().getCloseFuture().awaitUninterruptibly();// 等待连接断开
		connector.dispose();
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println("recv: " + message);
	}
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("sent: " + message);
	}
}