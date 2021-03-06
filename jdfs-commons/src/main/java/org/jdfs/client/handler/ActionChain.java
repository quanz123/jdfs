package org.jdfs.client.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 处理链，用于串联多个处理
 * 
 * @author James Quan
 * @version 2015年2月25日 上午10:58:38
 */
public class ActionChain {

	private List<Action> actions;
	private int currentPosition = 0;
	private String errorMessage = null;
	private Throwable error = null;

	/**
	 * 返回处理链中所包含的处理集合，一定不为{@code null}
	 * 
	 * @return
	 */
	public Action[] getActions() {
		return actions == null ? new AbstractServerAction[0] : actions
				.toArray(new AbstractServerAction[0]);
	}

	/**
	 * 设置处理链中所包含的处理集合
	 * 
	 * @param actions
	 */
	public void setActions(Action[] actions) {
		if (actions == null || actions.length == 0) {
			this.actions = null;
		} else {
			this.actions = new ArrayList<Action>(actions.length);
			for (Action action : actions) {
				this.actions.add(action);
			}
		}
	}

	/**
	 * 向处理链的末尾添加处理
	 * 
	 * @param action
	 */
	public void addAction(Action action) {
		if (actions == null) {
			actions = new ArrayList<Action>();
		}
		actions.add(action);
	}

	/**
	 * 执行处理操作
	 * 
	 * @param request
	 *            操作的请求数据
	 * @param data
	 *            操作的上下文
	 * @return 操作的返回值
	 */
	public Object doAction(Object request, Map<String, Object> data) {
		if (hasError()) {
			throw new RuntimeException(errorMessage, error);
		} 
		Action action = null;
		if (actions != null && actions.size() > currentPosition) {
			action = actions.get(currentPosition);
			currentPosition++;
		}
		Object result = null;
		try {
			result = action == null ? request : action.process(request, data,
					this);
		} catch (Throwable t) {
			error = t;
			errorMessage = t.getMessage();
		}
		if (hasError()) {
			throw new RuntimeException(errorMessage, error);
		} else {
			return result;
		}
	}

	protected boolean hasError() {
		return errorMessage != null || error != null;
	}

	/**
	 * 执行处理操作出错时执行的回调函数
	 * 
	 * @param message
	 * @param error
	 */
	public void throwException(String message, Throwable error) {
		this.errorMessage = message;
		this.error = error;
	}
}
