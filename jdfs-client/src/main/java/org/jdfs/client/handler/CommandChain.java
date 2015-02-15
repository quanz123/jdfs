package org.jdfs.client.handler;

import java.util.Map;

import org.apache.mina.core.session.IoSession;

public interface CommandChain {
	public int getId();
	
	public void setId(int id);
	
	public void doCommand(IoSession session, Object message, Map<String, Object> data);
	
	public void throwException(IoSession session, Throwable cause);
	
	public Map<String, Object> getData();
	
	public ChainFuture getFuture();
}
