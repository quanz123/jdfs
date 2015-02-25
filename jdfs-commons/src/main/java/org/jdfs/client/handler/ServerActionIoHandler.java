package org.jdfs.client.handler;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.jdfs.commons.request.JdfsRequest;

/**
 * 用于处理server操作集的{@link org.apache.mina.core.service.IoHandler}实现
 * @author James Quan
 * @version 2015年2月25日 上午11:56:51
 * @see Action
 * @see ActionChain
 * @see AbstractServerAction
 * @see ServerActionCallback
 */
public class ServerActionIoHandler extends IoHandlerAdapter{
    private int readTimeout;
    private int writeTimeout;
    
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
    }

    /**
     * Forwards read data to input stream.
     */
    @Override
    public void messageReceived(IoSession session, Object buf) {
    	if(!(buf instanceof JdfsRequest)) {
    		return;
    	}
    	JdfsRequest request = (JdfsRequest) buf;
    	int batchId = request.getBatchId();
    	int code = request.getCode();
//    	CommandChainHolder holder = (CommandChainHolder) session.getAttribute(COMMAND_CHAIN);
//    	CommandChain chain = holder == null ? null : holder.getChain(batchId);
//    	if(chain != null) {
//    		chain.doCommand(session, buf, chain.getData());
//    	}
    	ServerActionCallback cb = (ServerActionCallback) session.getAttribute("callback");
    	if(cb != null) {
    		cb.handleResponse(request, session);
    	}
    }

    /**
     * Forwards caught exceptions to input stream.
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
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
