package org.jdfs.client.handler;

public class ChainFutureImpl implements ChainFuture{
	private Object lock = new Object();
	private CommandChain chain;
	private boolean done = false;
    /** A number of seconds to wait between two deadlock controls ( 5 seconds ) */
    private static final long DEAD_LOCK_CHECK_INTERVAL = 5000L;

    private Object result = null;
    private boolean ready;

    private int waiters;
    
	public ChainFutureImpl() {
		super();
	}
	
	public CommandChain getChain() {
		return chain;
	}
	
	public void setChain(CommandChain chain) {
		this.chain = chain;
	}
	
	public boolean isDone() {
		synchronized(lock) {
			return done;
		}
	}
	
	@Override
	public void setResult(Object result) {
		setValue(result);		
	}

    /**
     * Sets the result of the asynchronous operation, and mark it as finished.
     */
    public void setValue(Object newValue) {
        synchronized (lock) {
            // Allow only once.
            if (ready) {
                return;
            }

            result = newValue;
            ready = true;
            if (waiters > 0) {
                lock.notifyAll();
            }
        }

        //notifyListeners();
    }

    public ChainFuture await() throws InterruptedException {
        synchronized (lock) {
            while (!ready) {
                waiters++;
                try {
                    // Wait for a notify, or if no notify is called,
                    // assume that we have a deadlock and exit the
                    // loop to check for a potential deadlock.
                    lock.wait(DEAD_LOCK_CHECK_INTERVAL);
                } finally {
                    waiters--;
                    if (!ready) {
                        checkDeadLock();
                    }
                }
            }
        }
        return this;
    }
	

    /**
     * 
     * TODO checkDeadLock.
     *
     */
    private void checkDeadLock() {
        // Only read / write / connect / write future can cause dead lock.
//        if (!(this instanceof CloseFuture || this instanceof WriteFuture || this instanceof ReadFuture || this instanceof ConnectFuture)) {
//            return;
//        }

        // Get the current thread stackTrace.
        // Using Thread.currentThread().getStackTrace() is the best solution,
        // even if slightly less efficient than doing a new Exception().getStackTrace(),
        // as internally, it does exactly the same thing. The advantage of using
        // this solution is that we may benefit some improvement with some
        // future versions of Java.
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // Simple and quick check.
//        for (StackTraceElement s : stackTrace) {
//            if (AbstractPollingIoProcessor.class.getName().equals(s.getClassName())) {
//                IllegalStateException e = new IllegalStateException("t");
//                e.getStackTrace();
//                throw new IllegalStateException("DEAD LOCK: " + IoFuture.class.getSimpleName()
//                        + ".await() was invoked from an I/O processor thread.  " + "Please use "
//                        + IoFutureListener.class.getSimpleName() + " or configure a proper thread model alternatively.");
//            }
//        }

        // And then more precisely.
//        for (StackTraceElement s : stackTrace) {
//            try {
//                Class<?> cls = DefaultIoFuture.class.getClassLoader().loadClass(s.getClassName());
//                if (IoProcessor.class.isAssignableFrom(cls)) {
//                    throw new IllegalStateException("DEAD LOCK: " + IoFuture.class.getSimpleName()
//                            + ".await() was invoked from an I/O processor thread.  " + "Please use "
//                            + IoFutureListener.class.getSimpleName()
//                            + " or configure a proper thread model alternatively.");
//                }
//            } catch (Exception cnfe) {
//                // Ignore
//            }
//        }
    }

}
