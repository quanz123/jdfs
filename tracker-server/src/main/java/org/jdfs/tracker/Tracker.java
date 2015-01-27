package org.jdfs.tracker;

import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Application entry point
 * 
 * @author James Quan
 * @version 2015年1月24日 下午8:58:37
 */
public class Tracker implements Runnable {
	public static void main(String[] args) {
		Tracker tracker = new Tracker();
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
