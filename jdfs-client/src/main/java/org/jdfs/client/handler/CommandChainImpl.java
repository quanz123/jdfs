package org.jdfs.client.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.jdfs.client.FileIoHandler;

public class CommandChainImpl implements CommandChain {
	private int id;
	private List<Command> commands;
	private int currentPosition = 0;
	private Map<String, Object> data;
	private ChainFuture future;
	
	public CommandChainImpl() {
		super();
		ChainFutureImpl f = new ChainFutureImpl();
		f.setChain(this);
		future = f;
	}
	
	

	public void addCommand(Command command) {
		if (commands == null) {
			commands = new ArrayList<Command>();
		}
		commands.add(command);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public Map<String, Object> getData() {
		return data;
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	@Override
	public void doCommand(IoSession session, Object message, Map<String, Object> data) {
		Command command = null;
		setData(data);
		if (commands != null && commands.size() > currentPosition) {
			command = commands.get(currentPosition);
			currentPosition++;
		}
		if (command != null) {
			command.process(session, message, data, this);
		} else {
			CommandChainHolder holder = (CommandChainHolder) session
					.getAttribute(FileIoHandler.COMMAND_CHAIN);
			holder.removeCommandChain(this);
			future.setResult(null);
		}
	}

	@Override
	public void throwException(IoSession session, Throwable cause) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public ChainFuture getFuture() {
		return future;
	}
	
}
