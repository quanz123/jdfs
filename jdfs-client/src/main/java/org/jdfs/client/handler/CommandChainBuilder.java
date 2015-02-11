package org.jdfs.client.handler;

import org.apache.mina.core.session.IoSession;

public interface CommandChainBuilder {

	public CommandChain buildCommandChain(IoSession session, Object message);
}
