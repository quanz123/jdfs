package org.jdfs.commons.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 将{@link InetSocketAddress}与字符串相互转化的工具类
 * 
 * @author James Quan
 * @version 2015年2月26日 下午5:03:33
 */
public abstract class InetSocketAddressHelper {

	/**
	 * 返回字符形式的地址
	 * 
	 * @param addr
	 * @return
	 * @see #parse(String)
	 */
	public static String toString(InetSocketAddress addr) {
		if (addr == null) {
			return null;
		}
		if (addr.isUnresolved()) {
			return addr.getHostString() + ':' + addr.getPort();
		} else {
			InetAddress ia = addr.getAddress();
			return ia.getHostAddress() + ':' + addr.getPort();
		}
	}

	/**
	 * 将字符形式的地址还原
	 * 
	 * @param address
	 * @return
	 * @see #toString(InetSocketAddress)
	 */
	public static InetSocketAddress parse(String address) {
		address = address == null ? null : address.trim();
		if (address == null || address.length() == 0) {
			return null;
		}
		int colon = address.lastIndexOf(':');
		if (colon == -1) {
			throw new IllegalArgumentException();
		}
		String host = address.substring(0, colon);
		int port = Integer.parseInt(address.substring(colon + 1));
		return new InetSocketAddress(host, port);
	}
}
