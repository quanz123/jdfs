package org.jdfs.storage;

import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Application entry point
 * @author James Quan
 * @version 2015年1月29日 下午4:40:46
 */
public class Storage implements Runnable {
	public static void main(String[] args) {
		Storage tracker = new Storage();
		tracker.run();
	}

	public void run() {
		String[] xmlCfg = new String[] { "conf/*.xml" };
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
				xmlCfg);
		try {

		} finally {
			//context.close();
		}
	}
}
