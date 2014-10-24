package com.wugu.app.common;

import java.io.Serializable;
import java.net.URLEncoder;

public class URLs implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String formatRUL(String path){
		try{
			if(path.startsWith("http://")||path.startsWith("https://")){
//				return URLEncoder.encode(path, "utf-8");
				return path;
			}
			return "http://"+URLEncoder.encode(path, "utf-8");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
