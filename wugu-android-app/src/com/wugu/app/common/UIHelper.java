package com.wugu.app.common;

import java.io.Serializable;

import com.wugu.app.utils.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class UIHelper implements Serializable{

	private static final long serialVersionUID = 1L;

	public static void showUrlRedirect(Context context, String path){
		if(StringUtils.isEmpty(path)){
			ToastMessage(context, "连接找不到");
			return ;
		}
		String url = URLs.formatRUL(path);
		openBrowser(context, url);
	}
	/**
	 * 用浏览器打开url
	 * @param context
	 * @param url
	 */
	private static void openBrowser(Context context, String url){
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(intent);
	}
	
	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}
}
