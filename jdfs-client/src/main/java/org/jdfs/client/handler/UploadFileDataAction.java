package org.jdfs.client.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.mina.transport.socket.SocketConnector;
import org.jdfs.client.FileInfo;
import org.jdfs.commons.request.JdfsRequest;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.storage.request.UpdateFileRequest;
import org.jdfs.tracker.service.ServerInfo;
import org.joda.time.DateTime;

/**
 * 上传文件数据的操作实现
 * @author James Quan
 * @version 2015年2月25日 下午5:16:04
 */
public class UploadFileDataAction extends AbstractServerAction {
	private long id;
	private String name;
	private long size;

	private long length;
	private long offset;
	private InputStream data;
	
	private int group;

	private int chunkSize = 8192;

	/**
	 * 返回待上传文件的id
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * 设置待上传文件的id
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * 返回待上传文件的名字
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置待上传文件的名字
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 返回待上传文件的大小
	 * 
	 * @return
	 */
	public long getSize() {
		return size;
	}

	/**
	 * 设置待上传文件的大小
	 * 
	 * @param size
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * 返回待上传数据的长度
	 * 
	 * @return
	 */
	public long getLength() {
		return length;
	}

	/**
	 * 设置待上传数据的长度
	 * 
	 * @param length
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * 返回待上传数据在文件中的偏移量
	 * 
	 * @return
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * 设置待上传数据在文件中的偏移量
	 * 
	 * @param offset
	 */
	public void setOffset(long offset) {
		this.offset = offset;
	}

	/**
	 * 返回待上传数据的数据流
	 * 
	 * @return
	 */
	public InputStream getData() {
		return data;
	}

	/**
	 * 设置待上传数据的数据流
	 * 
	 * @param data
	 */
	public void setData(InputStream data) {
		this.data = data;
	}

	/**
	 * 返回接收数据的服务器组
	 * @return
	 */
	public int getGroup() {
		return group;
	}
	
	/**
	 * 设置接收数据的服务器组
	 * @param group
	 */
	public void setGroup(int group) {
		this.group = group;
	}
	
	/**
	 * 返回上传数据所允许的数据块大小
	 * 
	 * @return
	 */
	public int getChunkSize() {
		return chunkSize;
	}

	/**
	 * 设置上传数据所允许的数据块大小
	 * 
	 * @param chunkSize
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public UploadFileDataAction() {
		super();
	}

	/**
	 * 直接指定请求参数的构造函数
	 * 
	 * @param id
	 *            待上传文件的id
	 * @param name
	 *            待上传文件的名字
	 * @param size
	 *            待上传文件的大小
	 * @param data
	 *            待上传数据的数据流
	 * @param offset
	 *            待上传数据在文件中的偏移量
	 * @param length
	 *            待上传数据的长度
	 * @param storageAddress
	 *            存储服务器的地址
	 * @param connector
	 *            用于建立网络连接的{@link SocketConnector}
	 */
	public UploadFileDataAction(long id, String name, long size, InputStream data,
			long offset, long length, SocketAddress storageAddress,
			SocketConnector connector) {
		super();
		setId(id);
		setName(name);
		setSize(size);
		setLength(length);
		setOffset(offset);
		setData(data);
		setServerAddress(storageAddress);
		setConnector(connector);
	}

	@Override
	protected Iterator<JdfsRequest> getRequestIterator(Object request,
			Map<String, Object> context, ActionChain chain) {
		if (request instanceof ServerInfo) {
			ServerInfo server = (ServerInfo) request;
			setGroup(server.getGroup());
			setServerAddress(server.getServiceAddress());
			context.put("server", request);
		} else {
			ServerInfo server = new ServerInfo();
			server.setGroup(getGroup());
			server.setServiceAddress((InetSocketAddress) getServerAddress());
		}
		return new MultipartRequestIterator(id, size, data, length);
	}

	@Override
	protected boolean handleServerResponse(JdfsRequest request,
			JdfsRequest response, Iterator<JdfsRequest> requestIterator,
			ActionChain chain) {
		JdfsStatusResponse resp = (JdfsStatusResponse) response;
		if (resp.getStatus() != JdfsRequestConstants.STATUS_OK) {
			chain.throwException("upload data error", new IOException(
					"upload data error: " + resp.getMessage()));
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected Object createResponse(Object request,
			Map<String, Object> context, List<JdfsRequest> serverRequests,
			List<JdfsRequest> serverResponses, ActionChain chain) {
		FileInfo file = new FileInfo();
		file.setId(getId());
		file.setName(getName());
		file.setSize(getSize());
		file.setLastModified(DateTime.now());
		return file;
	}

	protected class MultipartRequestIterator implements Iterator<JdfsRequest> {
		private long id;
		private InputStream data;
		private long size;
		private long length;

		private long position = 0;

		public MultipartRequestIterator(long id, long size, InputStream data, long length) {
			super();
			this.id = id;
			this.size = size;
			this.data = data;
			this.length = length;
		}

		@Override
		public boolean hasNext() {
			return position < length;
		}

		@Override
		public JdfsRequest next() {
			int len = (int)Math.min((long)chunkSize, length - position);
			UpdateFileRequest req = new UpdateFileRequest();
			req.setId(id);
			req.setSize(size);
			req.setPosition(position);
			byte[] buf = new byte[(int) len];
			try {
				IOUtils.readFully(data, buf);
			} catch (IOException e) {
				throw new RuntimeException("read data error!", e);
			}
			req.setData(buf);
			System.out.println("writing " + len + " at " + position + "....");
			position += len;
			return req;
		}

	}
}
