package org.jdfs.tracker.service;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FileInfoServiceTest {
	private FileInfoService fileInfoService;
	
	public FileInfoService getFileInfoService() {
		return fileInfoService;
	}
	
	@Autowired
	public void setFileInfoService(FileInfoService fileInfoService) {
		this.fileInfoService = fileInfoService;
	}

	@Test
	public void testLoadSave() throws IOException {
		System.out.println(fileInfoService.getFileInfo(100));
		DateTime now = DateTime.now();
		FileInfo file = new FileInfo();
		file.setId(100);
		file.setName("测试文件1");
		file.setSize(2345);
		file.setLastModified(now);
		fileInfoService.updateFileInfo(file);
		
		FileInfo file2 = fileInfoService.getFileInfo(100);
		Assert.assertNotNull(file2);
		Assert.assertEquals(file.getId(), file2.getId());
		Assert.assertEquals(file.getName(), file2.getName());
		Assert.assertEquals(file.getSize(), file2.getSize());
		Assert.assertEquals(file.getLastModified(), file2.getLastModified());
		
	}
}
