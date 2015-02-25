package org.jdfs.client.handler;

import java.util.Map;


/**
 * 操作定义
 * 
 * @author James Quan
 * @version 2015年2月25日 上午10:37:08
 */
public interface Action {
	/**
	 * 执行操作处理
	 * 
	 * @param request
	 *            操作的请求对象
	 * @param context
	 *            操作的上下文
	 * @param chain
	 *            操作链
	 * @return 操作的执行结果
	 */
	public Object process(Object request, Map<String, Object> context,
			ActionChain chain);

}