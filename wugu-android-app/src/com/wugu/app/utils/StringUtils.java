package com.wugu.app.utils;

import java.io.Serializable;

public class StringUtils implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 判断字符串是否是空串
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		return null == str || str.trim().equals("") || "null".equals(str) ;
	}
}
