package org.jdfs.commons.request;

/**
 * 功能请求的基本定义
 * @author James Quan
 * @version 2015年2月3日 下午5:39:36
 */
public class JdfsRequest {
			
		private int code;
		/**
		 * 返回请求执行的功能
		 * @return
		 */
		public int getCode() {
			return code;
		}

		/**
		 * 设置请求执行的功能
		 * @param code
		 */
		public void setCode(int code) {
			this.code = code;
		}

	}
