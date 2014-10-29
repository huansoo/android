package com.wugu.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wugu.app.api.ApiClient;
import com.wugu.app.bean.ArticleInfo;
import com.wugu.app.bean.FarmingTongInfo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppContext extends Application {
	
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	public static final int PAGE_SIZE = 20;//默认分页大小
	private static final int CACHE_TIME = 60*60000;//缓存失效时间
	
	private boolean login = false;	//登录状态
	private int loginUid = 0;	//登录用户的id
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	
	private String saveImagePath;//保存图片路径
	
	
	@Override
	public void onCreate() {
		super.onCreate();
        //注册App异常崩溃处理器
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        
	}

	/**
	 * 检测当前系统声音是否为正常模式
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE); 
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}
	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	
	
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	
	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}

	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file){
		if(!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
			//反序列化失败 - 删除缓存文件
			if(e instanceof InvalidClassException){
				File data = getFileStreamPath(file);
				data.delete();
			}
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile)
	{
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	/**
	 * 获取农事通列表
	 * @return
	 */
	@SuppressLint("NewApi")
	public static List<FarmingTongInfo> getFarmingTongList() {
		List<FarmingTongInfo> farmingTongList = new ArrayList<FarmingTongInfo>();
		String results = ApiClient.http_get_return_string("http://10.1.8.33:9080/ycyp_api/original.action");
		try{
			JSONObject obj = new JSONObject(results);
			String status = obj.getString("status");
			if("200".equals(status)){
				JSONArray resultList = obj.getJSONArray("dataList");
				for(int i = 0; i < resultList.length(); i++){
					JSONObject news = resultList.getJSONObject(i);
					FarmingTongInfo article = new FarmingTongInfo();
					article.setId(100+i);
					article.setTitle(news.getString("title"));
					article.setDetail(news.getString("detail"));
					article.setImgUrl(String.valueOf(news.get("img")));
					article.setUrl(news.getString("url"));
					farmingTongList.add(article);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return farmingTongList;
	}
	/**
	 * 获取新闻列表
	 * @return
	 */
	public static List<ArticleInfo> getArticleList() {
		List<ArticleInfo> articleList = new ArrayList<ArticleInfo>();
		String results = ApiClient.http_get_return_string("http://10.1.8.33:9080/ycyp_api/main.action");
		try {
			JSONObject json = new JSONObject(results);
			int status = (Integer) json.get("status");
			if(200 == status){
				JSONArray dataList = json.getJSONArray("dataList");
				for(int i = 0; i < dataList.length(); i++){
					JSONObject news = dataList.getJSONObject(i);
					ArticleInfo article = new ArticleInfo();
					article.setId(100+i);
					article.setTitle(news.getString("title"));
					article.setDetail(news.getString("detail"));
					article.setImgUrl(String.valueOf(news.get("img")));
					article.setUrl(news.getString("url"));
					articleList.add(article);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return articleList;
	}
	
}
