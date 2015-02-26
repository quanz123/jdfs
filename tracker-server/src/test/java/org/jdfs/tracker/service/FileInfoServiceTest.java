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
		fileInfoService.updateFileName(100, "测试文件1.txt");
		fileInfoService.updateFileDataInfo(100, 1, 2345, now);
		
		
		FileInfo file2 = fileInfoService.getFileInfo(100);
		Assert.assertNotNull(file2);
		Assert.assertEquals(100, file2.getId());
		Assert.assertEquals("测试文件1.txt", file2.getName());
		Assert.assertEquals(2345, file2.getSize());
		Assert.assertEquals(now, file2.getLastModified());
		
	}
}
