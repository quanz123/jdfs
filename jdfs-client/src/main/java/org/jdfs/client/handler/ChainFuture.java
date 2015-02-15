package org.jdfs.client.handler;

public interface ChainFuture {
	public boolean isDone();
	
	public ChainFuture await()  throws InterruptedException;
	
	public void setResult(Object result);
}
