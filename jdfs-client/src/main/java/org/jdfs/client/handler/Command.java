package org.jdfs.client.handler;

import org.apache.mina.core.session.IoSession;

public interface Command {

	public void process(IoSession session, Object message, CommandChain chain);
}
