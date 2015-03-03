package org.jdfs.tracker.ha;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IteratorUtils;
import org.jdfs.client.handler.AbstractServerAction;
import org.jdfs.client.handler.ActionChain;
import org.jdfs.commons.request.JdfsRequest;
import org.jdfs.commons.request.JdfsRequestConstants;
import org.jdfs.commons.request.JdfsStatusResponse;
import org.jdfs.tracker.request.RemoveFileInfoSyncRequest;
import org.jdfs.tracker.request.UpdateFileInfoSyncRequest;
import org.jdfs.tracker.service.FileInfo;
import org.jdfs.tracker.service.FileInfoEvent;
import org.jdfs.tracker.service.RemoveFileInfoEvent;
import org.jdfs.tracker.service.UpdateFileInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInfoSyncTask extends AbstractServerAction implements Runnable {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private FileInfoEvent message;

	public FileInfoSyncTask(FileInfoEvent message) {
		super();
		this.message = message;
	}

	@Override
	public void run() {
		boolean success = (boolean) process(message, null, null);
		logger.debug("sync file {}: {}", message, (success ? "success"
				: "failure"));
	}

	@Override
	protected Iterator<JdfsRequest> getRequestIterator(Object request,
			Map<String, Object> context, ActionChain chain) {
		FileInfoEvent event = (FileInfoEvent) request;
		if (event instanceof UpdateFileInfoEvent) {
			UpdateFileInfoSyncRequest r = new UpdateFileInfoSyncRequest();
			FileInfo file = event.getFileInfo();
			r.setId(file.getId());
			r.setGroup(file.getGroup());
			r.setName(file.getName());
			r.setSize(file.getSize());
			long lastModified = file.getLastModified() == null ? 0 : file
					.getLastModified().getMillis();
			r.setLastModified(lastModified);
			return IteratorUtils.arrayIterator(new JdfsRequest[] { r });
		} else if (event instanceof RemoveFileInfoEvent) {
			RemoveFileInfoSyncRequest r = new RemoveFileInfoSyncRequest();
			r.setId(event.getFileInfo().getId());
			return IteratorUtils.arrayIterator(new JdfsRequest[] { r });
		} else {
			return IteratorUtils.emptyIterator();
		}
	}

	@Override
	protected Object createResponse(Object request,
			Map<String, Object> context, List<JdfsRequest> serverRequests,
			List<JdfsRequest> serverResponses, ActionChain chain) {
		if (serverResponses.size() != 1) {
			chain.throwException("sync file info error, illegal response",
					new IOException("sync file info error, illegal response"));
			return false;
		}
		JdfsStatusResponse resp = (JdfsStatusResponse) serverResponses.get(0);
		return resp.getStatus() == JdfsRequestConstants.STATUS_OK;
	}

}
