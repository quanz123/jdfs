package org.jdfs.client;

import java.io.ByteArrayInputStream;
import java.util.Date;

import org.junit.Assert;
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
	public void testUpdate() throws Exception {

		StringBuilder b = new StringBuilder();
		b.append("Test测试123456789-").append(new Date()).append('\n');
		char[] buf = new char[1024];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (char) ((int) '0' + i % 10);
		}
		String line = new String(buf);
		for (int i = 0; i < 10; i++) {
			b.append("line ").append(i).append('\n').append(line).append('\n');
		}
		b.append("!----------------------------------------finished-------------------------------------------------!");
		byte[] data = b.toString().getBytes("UTF-8");
		FileInfo file = fileService.updateFile(101, "test文件.txt", data.length,
				new ByteArrayInputStream(data), 0, data.length);
		Assert.assertNotNull(file);
		Assert.assertEquals(101l, file.getId());
		Assert.assertEquals("test文件.txt", file.getName());
		Assert.assertEquals(data.length, file.getSize());
		Thread.sleep(100);		
		FileInfo file2 = fileService.updateFile(101, "test文件xx.txt", data.length, null, 0, 0);
		Assert.assertNotNull(file2);
		Assert.assertEquals(101l, file2.getId());
		Assert.assertEquals("test文件xx.txt", file2.getName());
		Assert.assertEquals(data.length, file2.getSize());
		Assert.assertTrue(file2.getLastModified().isAfter(file.getLastModified()));
	}
}
