package org.jdfs.storage.handler;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.jdfs.storage.request.FileRequest;

/**
 * 存储服务请求处理器
 * @author James Quan
 * @version 2015年1月30日 上午10:31:29
 */
public class StorageServerIOHandler extends IoHandlerAdapter{

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		FileRequest request = (FileRequest) message;
		System.out.println("recv: " + request);
	}
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.print("sent: " + message);
	}
}
