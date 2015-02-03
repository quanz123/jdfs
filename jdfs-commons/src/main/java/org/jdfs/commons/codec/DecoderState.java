package org.jdfs.commons.codec;

import java.io.Serializable;

import org.jdfs.commons.request.JdfsRequest;

/**
 * 请求解码器用于缓存解码过程的状态对象
 * 
 * @author James Quan
 * @version 2015年2月3日 下午9:59:27
 */
public class DecoderState<T extends JdfsRequest> implements Serializable {
	private T request;
	private int state;

	public DecoderState() {
		super();
	}

	/**
	 * 返回解码器创建的请求对象
	 * 
	 * @return
	 */
	public T getRequest() {
		return request;
	}

	/**
	 * 设置解码器创建的请求对象
	 * 
	 * @param request
	 */
	public void setRequest(T request) {
		this.request = request;
	}

	/**
	 * 返回当前解码状态，用于供解码器分段读取各段数据
	 * @return
	 */
	public int getState() {
		return state;
	}

	/**
	 * 设置当前解码状态，用于供解码器分段读取各段数据
	 * @param status
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * 将当前解码状态加1
	 */
	public void toNextState() {
		state++;
	}
	
	/**
	 * 重置处理状态，以便重新解码
	 */
	public void reset() {
		request = null;
		state = 0;
	}
}
