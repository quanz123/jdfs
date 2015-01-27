package org.jdfs.tracker.client;

	import java.net.InetSocketAddress;
import java.nio.charset.Charset;

	import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;


public class ClientTest {

	  /**
	   * @param args
	   */
	  public static void main(String[] args) {
	    // 创建客户端连接器. 
	    NioSocketConnector connector = new NioSocketConnector(); 
	    connector.getFilterChain().addLast( "logger", new LoggingFilter() ); 
	    connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" )))); //设置编码过滤器 
	    connector.setHandler(new HandlerOne());//设置事件处理器 
	    ConnectFuture cf = connector.connect( 
	    new InetSocketAddress("127.0.0.1", 1235));//建立连接 
	    cf.awaitUninterruptibly();//等待连接创建完成 
	    cf.getSession().write("知识");//发送消息 
	    cf.getSession().close(true);
	    cf.getSession().getCloseFuture().awaitUninterruptibly();//等待连接断开 
	    connector.dispose(); 

	  }

	}