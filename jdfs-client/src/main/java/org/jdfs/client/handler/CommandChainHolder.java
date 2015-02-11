package org.jdfs.client.handler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandChainHolder {
	private AtomicInteger batchIds = new AtomicInteger(1);
	private ConcurrentMap<Integer, CommandChain> chains = new ConcurrentHashMap<Integer, CommandChain>();

	public CommandChain getChain(int batchId) {
		return chains == null ? null : chains.get(batchId);
	}

	public int addChain(CommandChain chain) {
		int batchId = batchIds.getAndIncrement();
		chain.setId(batchId);
		chains.put(batchId, chain);
		return batchId;
	}

	public void removeCommandChain(CommandChain chain) {
		if(chain == null || chains == null) {
			return;
		}
		chains.remove(chain.getId());
	}
	
}
