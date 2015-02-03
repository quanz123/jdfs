package org.jdfs.commons.codec;

import java.util.Map;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * 以spring方式创建DemuxingProtocolCodecFactory的工厂类
 * 
 * @author James Quan
 * @version 2015年1月31日 上午10:17:33
 */
public class DemuxingProtocolCodecFactoryFactoryBean extends
		AbstractFactoryBean<ProtocolCodecFactory> {

	private Map<Class<?>, MessageEncoder<?>> encoders;
	private MessageDecoder[] decoders;

	public Map<Class<?>, MessageEncoder<?>> getEncoders() {
		return encoders;
	}

	public void setEncoders(Map<Class<?>, MessageEncoder<?>> encoders) {
		this.encoders = encoders;
	}

	public MessageDecoder[] getDecoders() {
		return decoders;
	}

	public void setDecoders(MessageDecoder[] decoders) {
		this.decoders = decoders;
	}

	@Override
	public Class<?> getObjectType() {
		return ProtocolCodecFactory.class;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected ProtocolCodecFactory createInstance() throws Exception {
		DemuxingProtocolCodecFactory codecFactory = new DemuxingProtocolCodecFactory();
		if (encoders != null && !encoders.isEmpty()) {
			for (Map.Entry<Class<?>, MessageEncoder<?>> entry : encoders
					.entrySet()) {
				Class messageType = entry.getKey();
				MessageEncoder encoder = entry.getValue();
				codecFactory.addMessageEncoder(messageType, encoder);
			}
		}
		if (decoders != null && decoders.length > 0) {
			for (MessageDecoder decoder : decoders) {
				codecFactory.addMessageDecoder(decoder);
			}
		}
		return codecFactory;
	}

}
