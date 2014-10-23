package com.wugu.app.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.wugu.app.api.ApiClient;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestWuGu extends AndroidTestCase {
	public static final String YCYP_API_URL = "http://10.1.8.33:9080/ycyp_api/main.action?api=Article";
	public void testHttpClient(){
		//此类在commons-httpclient-3.1.jar中
				HttpClient client = new HttpClient();
				GetMethod httpGet = new GetMethod(YCYP_API_URL);
				
				//设置默认重新连接策略，发生异常时，自动重试3次
				httpGet.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
				try {
					//xecuteMethod返回值是一个整数，表示了执行该方法后服务器返回的状态码，该状态码能表示出该方法执行是否成功、需要认证或者页面发生了跳转（默认状态下GetMethod的实例是自动处理跳转的）
					int statusCode  = client.executeMethod(httpGet);
					if(statusCode == HttpStatus.SC_OK){
						//取得目标地址的内容有三种方法：第一种，getResponseBody，该方法返回的是目标的二进制的byte流；第二种，getResponseBodyAsString，这个方法返回的是String类型第三种，getResponseBodyAsStream，这个方法对于目标地址中有大量数据需要传输是最佳的
						InputStream stream = httpGet.getResponseBodyAsStream();
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
	}
}
