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
	/**
	 * 字符串转化为整形
	 * @param obj
	 * @param defaultVal
	 * @return
	 */
	public static int toInt(String obj, int defaultVal){
		try {
			return Integer.parseInt(obj);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return defaultVal;
		}
	}
	public static int toInt(Object obj){
		if(null == obj){
			return 0;
		}
		return toInt(String.valueOf(obj));
	}
}
