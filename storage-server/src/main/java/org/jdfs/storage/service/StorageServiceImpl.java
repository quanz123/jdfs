package org.jdfs.storage.service;

import org.jdfs.commons.service.AbstractJdfsServer;

public class StorageServiceImpl extends AbstractJdfsServer {
	private String base = "/jdfs";
	private String group = "group1";

	/**
	 * 返回服务器注册信息节点的根路径，缺省为{@code /jdfs}
	 * 
	 * @return
	 */
	public String getBase() {
		return base;
	}

	/**
	 * 设置服务器注册信息节点的根路径，缺省为{@code /jdfs}
	 * 
	 * @param base
	 */
	public void setBase(String base) {
		this.base = base;
	}

	/**
	 * 返回服务器所隶属的分组
	 * 
	 * @return
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * 设置服务器所隶属的分组
	 * 
	 * @param group
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	
	@Override
	protected String getZkBasePath() {
		return getBase() + "/storages/" +  getGroup() ;
	}
	
	@Override
	protected String getZkNodeName() {
		return "storage";
	}

	@Override
	protected void initJdfsServer() throws Exception {		
		mkdirs(getBase() +  "/trackers");
	}
}
