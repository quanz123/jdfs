package org.jdfs.commons.handler;

import java.util.Map;

import org.apache.mina.handler.demux.DemuxingIoHandler;
import org.apache.mina.handler.demux.MessageHandler;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * 以spring方式创建DemuxingIoHandler的工厂类
 * 
 * @author James Quan
 * @version 2015年2月3日 上午11:41:41
 */
public class DemuxingIoHandlerFactoryBean extends
		AbstractFactoryBean<DemuxingIoHandler> {
	private Map<Class<?>, MessageHandler<?>> receivedMessageHandlers;
	private Map<Class<?>, MessageHandler<?>> sentMessageHandlers;

	public Map<Class<?>, MessageHandler<?>> getReceivedMessageHandlers() {
		return receivedMessageHandlers;
	}

	public void setReceivedMessageHandlers(
			Map<Class<?>, MessageHandler<?>> receivedMessageHandlers) {
		this.receivedMessageHandlers = receivedMessageHandlers;
	}

	@Override
	public Class<?> getObjectType() {
		return DemuxingIoHandler.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected DemuxingIoHandler createInstance() throws Exception {
		DemuxingIoHandler handler = new DemuxingIoHandler();
		if (receivedMessageHandlers != null
				&& !receivedMessageHandlers.isEmpty()) {
			for (Map.Entry<Class<?>, MessageHandler<?>> entry : receivedMessageHandlers
					.entrySet()) {
				Class type = entry.getKey();
				MessageHandler h = entry.getValue();
				handler.addReceivedMessageHandler(type, h);
			}
		}
		if (sentMessageHandlers != null && !sentMessageHandlers.isEmpty()) {
			for (Map.Entry<Class<?>, MessageHandler<?>> entry : sentMessageHandlers
					.entrySet()) {
				Class type = entry.getKey();
				MessageHandler h = entry.getValue();
				handler.addSentMessageHandler(type, h);
			}
		}
		return handler;
	}

}
