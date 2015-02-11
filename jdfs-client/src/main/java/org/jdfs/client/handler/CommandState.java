package org.jdfs.client.handler;

import java.io.Serializable;

public class CommandState implements Serializable {
	private Command command;
	
	public Command getCommand() {
		return command;
	}
	
	public void setCommand(Command command) {
		this.command = command;
	}
}
