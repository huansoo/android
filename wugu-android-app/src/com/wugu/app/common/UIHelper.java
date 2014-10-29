package com.wugu.app.common;

import java.io.Serializable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.wugu.app.AppManager;
import com.wugu.app.R;
import com.wugu.app.utils.StringUtils;

public class UIHelper implements Serializable{

	private static final long serialVersionUID = 1L;
	private final static String TAG = "UIHelper";
	
	public static final int LISTVIEW_ACTION_INIT = 0x01;
	public static final int LISTVIEW_ACTION_REFRESH = 0x02;
	public static final int LISTVIEW_ACTION_SCROLL = 0x03;
	public static final int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;
	
	
	public static final int LISTVIEW_DATA_MORE = 0x01;
	public static final int LISTVIEW_DATA_LOADING = 0x02;
	public static final int LISTVIEW_DATA_FULL = 0x03;
	public static final int LISTVIEW_DATA_EMPTY = 0x04;
	
	public static final int LISTVIEW_DATATYPE_NEWS = 0x01;
	public static final int LISTVIEW_DATATYPE_FARMING_TONG = 0x02;

	/**
	 * 点击某条新闻描述，查看具体的新闻
	 * @param context
	 * @param path
	 */
	public static void showUrlRedirect(Context context, String path){
		if(StringUtils.isEmpty(path)){
			ToastMessage(context, R.string.resource_is_not_found);
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
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //模拟器
						i.setType("message/rfc822"); // 真机
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "zhangdeyi@oschina.net" });
						i.putExtra(Intent.EXTRA_SUBJECT,
								"吾谷网Android客户端 - 错误报告");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "发送错误报告"));
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.show();
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
	
	public static void ToastMessageLong(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_LONG).show();
	}
}
