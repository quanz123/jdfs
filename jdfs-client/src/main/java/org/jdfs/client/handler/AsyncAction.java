package org.jdfs.client.handler;

import org.apache.mina.core.session.IoSession;

public class AsyncAction {

	private Object lock = new Object();
	private IoSession session;
	
	
	public void execute(Object request) throws InterruptedException {
		synchronized(lock) {
			session.write(request);
			lock.wait();
		}
		
	}
	
	public void sendRequest(Object request){
		
	}
	
	
	public void onResponse(Object message) {
		synchronized(lock) {
			lock.notifyAll();
		}
	}
}
