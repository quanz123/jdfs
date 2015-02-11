package org.jdfs.client;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.jdfs.client.handler.CommandChain;
import org.jdfs.client.handler.CommandChainBuilder;

public class FileIoHandler extends IoHandlerAdapter{
	public static String COMMAND_CHAIN = "commandChain";
    private int readTimeout;
    private int writeTimeout;
    
    private CommandChainBuilder commandChainBuilder;

    /**
     * Returns read timeout in seconds.
     * The default value is <tt>0</tt> (disabled).
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Sets read timeout in seconds.
     * The default value is <tt>0</tt> (disabled).
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * Returns write timeout in seconds.
     * The default value is <tt>0</tt> (disabled).
     */
    public int getWriteTimeout() {
        return writeTimeout;
    }

    /**
     * Sets write timeout in seconds.
     * The default value is <tt>0</tt> (disabled).
     */
    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
    
    public CommandChainBuilder getCommandChainBuilder() {
		return commandChainBuilder;
	}
    
    public void setCommandChainBuilder(CommandChainBuilder commandChainBuilder) {
		this.commandChainBuilder = commandChainBuilder;
	}

    /**
     * Initializes streams and timeout settings.
     */
    @Override
    public void sessionOpened(IoSession session) {
        // Set timeouts
        session.getConfig().setWriteTimeout(writeTimeout);
        session.getConfig().setIdleTime(IdleStatus.READER_IDLE, readTimeout);
    }

    /**
     * Closes streams
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
    	CommandChain chain = (CommandChain) session.getAttribute(COMMAND_CHAIN);
    	if(chain != null) {
    		
    	}
    }

    /**
     * Forwards read data to input stream.
     */
    @Override
    public void messageReceived(IoSession session, Object buf) {
    	CommandChain chain = (CommandChain) session.getAttribute(COMMAND_CHAIN);
    	if(chain == null) {
    		chain = commandChainBuilder.buildCommandChain(session, buf);
    		if(chain != null) {
    			session.setAttribute(COMMAND_CHAIN, chain);
    		}
    	}
    	if(chain != null) {
    		chain.doCommand(session, buf);
    	}
    }

    /**
     * Forwards caught exceptions to input stream.
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
    	CommandChain chain = (CommandChain) session.getAttribute(COMMAND_CHAIN);
    	if(chain != null) {
    		chain.throwException(session, cause);
    	}
//        final IoSessionInputStream in = (IoSessionInputStream) session.getAttribute(KEY_IN);
//
//        IOException e = null;
//        if (cause instanceof StreamIoException) {
//            e = (IOException) cause.getCause();
//        } else if (cause instanceof IOException) {
//            e = (IOException) cause;
//        }
//
//        if (e != null && in != null) {
//            in.throwException(e);
//        } else {
//            LOGGER.warn("Unexpected exception.", cause);
//            session.close(true);
//        }
    }

    /**
     * Handles read timeout.
     */
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        if (status == IdleStatus.READER_IDLE) {
            throw new FileIoException(new SocketTimeoutException("Read timeout"));
        }
    }
	
    public static class FileIoException extends RuntimeException {

        public FileIoException(IOException cause) {
            super(cause);
        }
    }
}
