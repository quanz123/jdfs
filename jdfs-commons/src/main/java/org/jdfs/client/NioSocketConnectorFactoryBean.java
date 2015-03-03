package org.jdfs.client;

import java.util.Map;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;

public class NioSocketConnectorFactoryBean extends AbstractFactoryBean<NioSocketConnector> {
	private Map<String, IoFilter> filters;
	private IoHandler handler;	

	public Map<String, IoFilter> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, IoFilter> filters) {
		this.filters = filters;
	}

	public IoHandler getHandler() {
		return handler;
	}

	public void setHandler(IoHandler handler) {
		this.handler = handler;
	}

	@Override
	public Class<?> getObjectType() {		
		return NioSocketConnector.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(handler, "handler is required!");
		super.afterPropertiesSet();
	}
	
	@Override
	protected NioSocketConnector createInstance() throws Exception {
		NioSocketConnector connector = new NioSocketConnector();
		if(filters != null && !filters.isEmpty()) {
			for(Map.Entry<String, IoFilter> entry : filters.entrySet()) {
				String name = entry.getKey();
				IoFilter filter = entry.getValue();
				connector.getFilterChain().addLast(name, filter);
			}
		}
		connector.setHandler(handler);
		return connector;
	}

}
