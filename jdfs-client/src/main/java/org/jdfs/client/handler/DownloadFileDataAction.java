package org.jdfs.client.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketConnector;
import org.jdfs.commons.request.JdfsDataResponse;
import org.jdfs.commons.request.JdfsRequest;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.storage.request.ReadFileRequest;
import org.jdfs.tracker.service.ServerInfo;

/**
 * 下载文件数据的操作实现
 * 
 * @author James Quan
 * @version 2015年2月25日 下午5:16:04
 */
public class DownloadFileDataAction extends AbstractSocketAction {
	private long id;
	private long length;
	private long offset;

	private int chunkSize = 8192;

	private MultipartRequestIterator iterator;
	private ServerInputStream stream;

	/**
	 * 返回待下载文件的id
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * 设置待下载文件的id
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * 返回待下载数据的长度
	 * 
	 * @return
	 */
	public long getLength() {
		return length;
	}

	/**
	 * 设置待下载数据的长度
	 * 
	 * @param length
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * 返回待下载数据在文件中的偏移量
	 * 
	 * @return
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * 设置待下载数据在文件中的偏移量
	 * 
	 * @param offset
	 */
	public void setOffset(long offset) {
		this.offset = offset;
	}

	/**
	 * 返回下载数据所允许的数据块大小
	 * 
	 * @return
	 */
	public int getChunkSize() {
		return chunkSize;
	}

	/**
	 * 设置下载数据所允许的数据块大小
	 * 
	 * @param chunkSize
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public DownloadFileDataAction() {
		super();
	}

	/**
	 * 直接指定请求参数的构造函数
	 * 
	 * @param id
	 *            待下载文件的id
	 * @param offset
	 *            待下载数据在文件中的偏移量
	 * @param length
	 *            待下载数据的长度
	 * @param storageAddress
	 *            存储服务器的地址
	 * @param connector
	 *            用于建立网络连接的{@link SocketConnector}
	 */
	public DownloadFileDataAction(long id, long offset, long length,
			SocketAddress storageAddress, SocketConnector connector) {
		super();
		setId(id);
		setLength(length);
		setOffset(offset);
		setServerAddress(storageAddress);
		setConnector(connector);
	}

	@Override
	public Object process(Object request, Map<String, Object> context,
			ActionChain chain) {
		iterator = getRequestIterator(request,
				context, chain);
		stream =  new ServerInputStream(iterator);
		return stream;
		//
		//
		// IoSession session = null;
		// try {
		// for (Iterator<JdfsRequest> requestIterator = getRequestIterator(
		// request, context, chain); requestIterator.hasNext();) {
		// if (session == null) {
		// session = getSession();
		// }
		// JdfsRequest req = requestIterator.next();
		// serverResult = null;
		// synchronized (mutex) {
		// session.write(req);
		// try {
		// mutex.wait();
		// } catch (InterruptedException e) {
		// }
		// }
		// if (!handleServerResponse(req, serverResult, requestIterator, chain))
		// {
		// break;
		// }
		// requestList.add(req);
		// responseList.add(serverResult);
		// }
		// } finally {
		// if (session != null) {
		// session.removeAttribute("callback");
		// session.close(true);
		// session = null;
		// connected = false;
		// }
		// }
	}

	protected MultipartRequestIterator getRequestIterator(Object request,
			Map<String, Object> context, ActionChain chain) {
		if (request instanceof ServerInfo) {
			ServerInfo server = (ServerInfo) request;
			setServerAddress(server.getServiceAddress());
			context.put("server", request);
		}
		return new MultipartRequestIterator(id, offset, length);
	}

	protected void requestData(JdfsRequest request) {
		IoSession session = getSession();
		serverResult = null;
		synchronized (mutex) {
			session.write(request);
			try {
				mutex.wait();
			} catch (InterruptedException e) {
			}
			JdfsDataResponse resp = (JdfsDataResponse) serverResult;
			if (resp.getStatus() == JdfsRequestConstants.STATUS_OK) {
				byte[] data = resp.getData();				
				stream.write(data);
				if(data != null) {
					iterator.forward(data.length);
				}
			} else if(resp.getStatus() == JdfsRequestConstants.STATUS_EOF) {
				stream.write(null);
			} else {
				stream.throwException(new IOException(resp.getMessage()));				
			}
		}
	}
	// @Override
	// protected boolean handleServerResponse(JdfsRequest request,
	// JdfsRequest response, Iterator<JdfsRequest> requestIterator,
	// ActionChain chain) {
	// JdfsDataResponse resp = (JdfsDataResponse) response;
	// if (resp.getStatus() != JdfsRequestConstants.STATUS_OK) {
	// chain.throwException("download data error", new IOException(
	// "download data error: " + resp.getMessage()));
	// return false;
	// } else {
	// return false;
	// }
	// }

	// @Override
	// protected Object createResponse(Object request,
	// Map<String, Object> context, List<JdfsRequest> serverRequests,
	// List<JdfsRequest> serverResponses, ActionChain chain) {
	// FileInfo file = new FileInfo();
	// file.setId(getId());
	// file.setLastModified(DateTime.now());
	// return file;
	// }

	protected class MultipartRequestIterator implements Iterator<JdfsRequest> {
		private long id;
		private long offset;
		private long length;

		private long position = 0;

		public MultipartRequestIterator(long id, long offset, long length) {
			super();
			this.id = id;
			this.length = length;
		}

		@Override
		public boolean hasNext() {
			return position < length;
		}

		@Override
		public JdfsRequest next() {
			int len = (int) Math.min((long) chunkSize, length - position);
			ReadFileRequest req = new ReadFileRequest();
			req.setId(id);
			req.setOffset(offset + position);
			req.setLength(len);
			System.out.println("request " + id + ": " + (offset + position) + " - " + len);
			return req;
		}

		public void forward(int len) {
			position += len;
		}
	}

	protected class ServerInputStream extends InputStream {
		private Iterator<JdfsRequest> requestIterator;
		private final Object mutex = new Object();

		private volatile boolean closed;
		private volatile boolean finished;
		private IOException exception;

		private byte[] buf = new byte[0];
		private int pos = 0;

		public ServerInputStream(Iterator<JdfsRequest> requestIterator) {
			super();
			this.requestIterator = requestIterator;
		}

	    @Override
	    public int available() {
	        synchronized (mutex) {
	            return buf.length - pos;
	        }
	    }

	    @Override
	    public void close() {
	        if (closed) {
	            return;
	        }

	        synchronized (mutex) {
	            closed = true;
	            releaseBuffer();
	            mutex.notifyAll();
	        }
	    }

		@Override
		public int read() throws IOException {
			synchronized (mutex) {
				if (!waitForData()) {
					return -1;
				}
				return buf[pos++] & 0xff;
			}
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			synchronized (mutex) {
				if (!waitForData()) {
					return -1;
				}

				int readBytes = Math.min(len, buf.length - pos);
				System.arraycopy(buf, pos, b, off, readBytes);
				pos += readBytes;

				return readBytes;
			}
		}

		private boolean waitForData() throws IOException {
			if (closed) {
				return false;
			}
			synchronized (mutex) {
				if (!finished && pos >= buf.length && exception == null) {
					if(requestIterator.hasNext()) {
						requestData(requestIterator.next());
					} else {
						releaseBuffer();
					}
//					IoSession session = getSession();
//					JdfsRequest request = requestIterator.next();
//					session.write(request);
//					try {
//						mutex.wait();
//					} catch (InterruptedException e) {
//						IOException ioe = new IOException(
//								"Interrupted while waiting for more data");
//						ioe.initCause(e);
//						throw ioe;
//					}
				}
			}

			if (exception != null) {
				releaseBuffer();
				throw exception;
			}

			if (finished && pos >= buf.length) {
				releaseBuffer();
				return false;
			}

			return true;
		}

		private void releaseBuffer() {
			if (finished) {
				return;
			}
			buf = new byte[0];
			pos = 0;
			finished = true;
		}

		public void write(byte[] src) {
			synchronized (mutex) {
				if (closed) {
					return;
				}
				if (pos < buf.length) {
					int len = buf.length - pos;
					byte[] data = new byte[buf.length - pos + src.length];
					System.arraycopy(buf, pos, data, 0, len);
					System.arraycopy(src, 0, data, len, src.length);
					pos = 0;
				} else {
					buf = new byte[src.length];
					System.arraycopy(src, 0, buf, 0, src.length);
					pos = 0;
					mutex.notifyAll();
				}
			}
		}

		public void throwException(IOException e) {
			synchronized (mutex) {
				if (exception == null) {
					exception = e;

					mutex.notifyAll();
				}
			}
		}
	}
}
