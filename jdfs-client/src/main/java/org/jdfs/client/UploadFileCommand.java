package org.jdfs.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketConnector;
import org.jdfs.client.handler.Command;
import org.jdfs.client.handler.CommandChain;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.storage.request.UpdateFileRequest;

public class UploadFileCommand implements Command {

	private SocketConnector storageConnector;

	public SocketConnector getStorageConnector() {
		return storageConnector;
	}
	
	public void setStorageConnector(SocketConnector storageConnector) {
		this.storageConnector = storageConnector;
	}
	
	
	@Override
	public void process(IoSession session, Object message, Map<String, Object> data,  CommandChain chain){
		JdfsStatusResponse resp = (JdfsStatusResponse) message;
		if (resp.getStatus() != JdfsRequestConstants.STATUS_OK) {
			chain.throwException(session, new IOException(
					"get upload server error: " + resp.getMessage()));
		} else {
			String addr = resp.getMessage();
			int colon = addr.indexOf(':');
			String host = addr.substring(0, colon);
			int port = Integer.parseInt(addr.substring(colon + 1));
			long id = (Long) data.get("id");
			String name = (String) data.get("name");
			long size = (Long) data.get("size");
			long offset = (Long) data.get("offset");
			InputStream stream = (InputStream) data.get("data");
			UpdateFileRequest request = new UpdateFileRequest();
			request.setBatchId(chain.getId());
			request.setId(id);
			request.setSize(size);
			request.setPosition(0);
			try {
				byte[] dat = IOUtils.toByteArray(stream);
				request.setData(dat);
			} catch (IOException e) {
				chain.throwException(session, e);
				return;
			}

			InetSocketAddress dest = new InetSocketAddress(host, port);
			ConnectFuture cf = storageConnector.connect(dest);
			cf.awaitUninterruptibly();
			cf.getSession().write(request);
		}
	}

}
