package com.wugu.app.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;


public class ApiClient {

	public static final String YCYP_API_URL = "http://10.1.8.33:9080/ycyp_api/main.action?api=Article";
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	public static final String UTF_8 = "UTF-8";
	private static final String RETURN_STRING = "string"; 
	private static final String RETURN_STREAM = "stream"; 
	
	/**
	 * get请求返回string
	 * @return
	 */
	public static String http_get_return_string(String url){
		return (String) http_get(RETURN_STRING, url);
	}
	/**
	 * get 请求返回stream
	 * @return
	 */
	public static InputStream http_get_return_stream(String url){
		return (InputStream) http_get(RETURN_STREAM, url);
	}
	/**
	 * get请求
	 * @param type 请求的返回值类型 string or stream
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static Object http_get(String type, String url){
		//此类在commons-httpclient-3.1.jar中
		HttpClient client = getHttpClient();
		GetMethod httpGet = new GetMethod(url);
		
		//设置默认重新连接策略，发生异常时，自动重试3次
		try {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
			//xecuteMethod返回值是一个整数，表示了执行该方法后服务器返回的状态码，该状态码能表示出该方法执行是否成功、需要认证或者页面发生了跳转（默认状态下GetMethod的实例是自动处理跳转的）
			int statusCode  = client.executeMethod(httpGet);
			if(statusCode == HttpStatus.SC_OK){
				//取得目标地址的内容有三种方法：第一种，getResponseBody，该方法返回的是目标的二进制的byte流；第二种，getResponseBodyAsString，这个方法返回的是String类型第三种，getResponseBodyAsStream，这个方法对于目标地址中有大量数据需要传输是最佳的
				if(RETURN_STREAM.equals(type)){
					return httpGet.getResponseBodyAsStream();
				}else {
					return httpGet.getResponseBodyAsString();
				}
			}
		} catch (HttpException e) {
			//原因主要可能是在构造getMethod的时候传入的协议不对，比如不小心将"http"写成"htp"，或者服务器端返回的内容不正常等，并且该异常发生是不可恢复的
			Log.v("HttpException", "协议不对，或内容有误");
			e.printStackTrace();
		} catch (IOException e) {
			//原因可能是网络异常，httpClient会根据指定的回复策略进行重试executeMethod方法.
			Log.v("IOException", "网络有异常");
			e.printStackTrace();
		} finally{
			//释放连接
			httpGet.releaseConnection();
		}
		return null;
	}
	/**
	 * 获取httpClient
	 * @return
	 */
	public static HttpClient getHttpClient(){
		HttpClient client = new HttpClient();
		//设置连接超时时间
		client.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		//设置请求响应超时时间
		client.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		//设置超时重新连接策略
		client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		//设置字符集
		client.getParams().setContentCharset(UTF_8);
		return client;
	}
	/**
	 * 将url网址中包含的图片转化为bitmap
	 * @param url
	 * @return
	 */
	public static Bitmap getBitMapFromUri(String url){
		Bitmap bitmap = null;
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setDoInput(true);
			InputStream is = con.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
