package org.jdfs.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FileServiceTest {

	private FileService fileService;

	public FileService getFileService() {
		return fileService;
	}

	@Autowired
	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	@Test
	public void testUpdate() throws IOException {
		byte[] data = ("Test测试123456789-" + new Date()).getBytes("UTF-8");
		fileService.updateFile(101, "test文件.txt",
				new ByteArrayInputStream(data), 0, data.length);
	}
}
