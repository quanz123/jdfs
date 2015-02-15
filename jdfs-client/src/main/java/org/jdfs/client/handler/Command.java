package org.jdfs.client.handler;

import java.util.Map;

import org.apache.mina.core.session.IoSession;

public interface Command {

	public void process(IoSession session, Object message, Map<String, Object> data, CommandChain chain);
}
