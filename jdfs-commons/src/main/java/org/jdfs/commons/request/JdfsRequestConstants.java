package org.jdfs.commons.request;

/**
 * 请求常量定义
 * 
 * @author James Quan
 * @version 2015年2月3日 下午5:24:25
 */
public interface JdfsRequestConstants {

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
	 * 请求获取数据上传服务器地址的请求代码
	 */
	public int REQUEST_UPLOAD_SERVER = 104;
	
	/**
	 * 请求获取数据下载服务器地址的请求代码
	 */
	public int REQUEST_DOWNLOAD_SERVER = 105;
	
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
	 * 读取文件数据的校验值
	 */
	public int REQUEST_DATA_CHECKSUM = 204;

	/**
	 * 返回处理的结果状态的请求代码
	 */
	public int REQUEST_STATUS_RESULT = 301;

	/**
	 * 返回处理的结果数据的请求代码
	 */
	public int REQUEST_DATA_RESULT = 302;

	/**
	 * 返回读取到的文件信息的请求代码
	 */
	public int REQUEST_INFO_RESULT = 303;

	/**
	 * 通告server信息的请求代码
	 */
	public int REQUEST_SERVER_INFO = 401;
	
	// ------------------------------------处理结果代码

	/**
	 * 处理成功
	 */
	public int STATUS_OK = 0;
	
	/**
	 * 待处理的文件不存在
	 */
	public int STATUS_FILE_NOT_FOUND = 1;
	
	/**
	 * 没有可用的存储服务器
	 */
	public int STATUS_STORAGE_NOT_FOUND = 101;
}
