package org.jdfs.commons.request;

/**
 * 功能请求的基本定义
 * @author James Quan
 * @version 2015年2月3日 下午5:39:36
 */
public class JdfsRequest {
			
		private int code;
		
		/**
		 * 空白构造函数
		 */
		public JdfsRequest() {
			super();
		}

		/**
		 * 指定请求代码的构造函数
		 * @param code 请求的功能代码
		 */
		public JdfsRequest(int code) {
			super();
			this.code = code;
		}

		/**
		 * 返回请求的功能代码
		 * @return
		 */
		public int getCode() {
			return code;
		}

		/**
		 * 设置请求的功能代码
		 * @param code
		 */
		public void setCode(int code) {
			this.code = code;
		}

	}
