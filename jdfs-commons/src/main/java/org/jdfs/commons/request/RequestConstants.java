package org.jdfs.commons.request;

/**
 * 请求常量定义
 * 
 * @author James Quan
 * @version 2015年2月3日 下午5:24:25
 */
public interface RequestConstants {

	/**
	 * 保存文件信息的请求代码
	 */
	public int REQUEST_INFO_UPDATE = 101;
	
	/**
	 * 删除文件信息的请求代码
	 */
	public int REQUEST_INFO_DELETE = 102;
	
	/**
	 * 读取文件信息的请求代码
	 */
	public int REQUEST_INFO_READ = 103;

	/**
	 * 保存文件数据的请求代码
	 */
	public int REQUEST_DATA_UPDATE = 201;
	
	/**
	 * 删除文件数据的请求代码
	 */
	public int REQUEST_DATA_DELETE = 202;
	
	/**
	 * 读取文件数据的请求代码
	 */
	public int REQUEST_DATA_READ = 203;

	/**
	 * 返回处理的结果状态的请求代码
	 */
	public int REQUEST_STATUS_RESULT = 501;

	/**
	 * 返回处理的结果数据的请求代码
	 */
	public int REQUEST_DATA_RESULT = 502;

}
