package org.jdfs.client.handler;

import org.apache.mina.core.session.IoSession;
import org.jdfs.commons.request.JdfsRequest;

/**
 * 接收到Server响应时执行的回调函数，用于执行Server端操作
 * 
 * @author James Quan
 * @version 2015年2月25日 下午12:00:50
 * @see AbstractServerAction
 * @see ServerActionIoHandler
 */
public interface ServerActionCallback {
	
	/**
	 * 接收到Server响应时执行的回调函数
	 * 
	 * @param response server端的响应
	 * @param session 与server端通信的{@link IoSession}
	 * @return
	 */
	public Object handleResponse(JdfsRequest response, IoSession session);
}
