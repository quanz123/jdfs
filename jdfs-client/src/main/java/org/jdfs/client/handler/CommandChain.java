package org.jdfs.client.handler;

import org.apache.mina.core.session.IoSession;

public interface CommandChain {
	public int getId();
	
	public void setId(int id);
	
	public void doCommand(IoSession session, Object message);
	
	public void throwException(IoSession session, Throwable cause);
}
