package com.wugu.app.common;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.wugu.app.api.ApiClient;
/**
 * 图片的处理类
 * @author yangch
 *
 */
public class BitMapManager implements Serializable{

	private static final long serialVersionUID = 4151382823920927309L;
	private static ExecutorService pool;
	private static Map<String, SoftReference<Bitmap>> cache;
	private static Map<ImageView, String> imageViews;
	private Bitmap defaultBmp;  
	static {
		//初始化线程池
		pool = Executors.newFixedThreadPool(5);
		//初始化图片缓存
		cache = new HashMap<String, SoftReference<Bitmap>>();
		//视图和url
		imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	}
	
	public BitMapManager(Bitmap defaultBmp) {
		this.defaultBmp = defaultBmp;
	}
	
	/**
     * 加载图片
     * @param url
     * @param imageView
     */
	public void loadBitmap(String url, ImageView imageView){
		loadBitmap(url, imageView, defaultBmp, 0, 0);
	}
	/**
     * 加载图片-可设置加载失败后显示的默认图片
     * @param url
     * @param imageView
     * @param defaultBmp
     */
	public void loadBitmap(String url, ImageView imageView , Bitmap defaultBitmap){
		loadBitmap(url, imageView, defaultBitmap, 0, 0);
	}
	
	/**
     * 加载图片-设置宽度和高度，使用系统默认的图片
     * @param url
     * @param imageView
     * @param defaultBmp
     */
	public void loadBitmap(String url, ImageView imageView, int width, int height){
		loadBitmap(url, imageView, defaultBmp, width, height);
	}
	/**
	 * 获取图片，并设置图片大小
	 * @param url
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBitmap, int width, int height){
		imageViews.put(imageView, url);
		//从内存缓存中获取图片
		Bitmap bitmap = getBitmapFromCache(url);
		
		//如果内存缓存中存在该图片
		if(null != bitmap){
			imageView.setImageBitmap(bitmap);
		}else{
			//从sd卡中加载图片
			String filename = FileUtils.getFileName(url);
			String path = imageView.getContext().getFilesDir()+File.separator+filename;
			File file = new File(path);
			if(file.exists()){
				bitmap = ImageUtils.getBitmap(imageView.getContext(), filename);
				imageView.setImageBitmap(bitmap);
			}else{
				//设置默认图片
				imageView.setImageBitmap(defaultBitmap);
				queryJob(url, imageView, width, height);
			}
		}
		
	}
	
	/**
	 * 创建子线程，执行指定下载任务，并将下载结果传送到ui线程
	 * @param url 图片url
	 * @param width 图片宽度
	 * @param height 图片高度
	 */
	public void queryJob(final String url, final ImageView imageView,final int width, final int height){
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(null != msg.obj){
					Bitmap bitmap = (Bitmap) msg.obj;
					imageView.setImageBitmap(bitmap);
					String tag = imageViews.get(imageViews);
					if(null == tag || !tag.equals(url)){
						try{
							//向SD卡中写入图片缓存
							ImageUtils.saveImage(imageView.getContext(), FileUtils.getFileName(url), bitmap);
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		pool.execute(new Runnable() {
			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				Bitmap bitmap = downloadBitmap(url, width, height);
				msg.obj = bitmap;
				System.out.println(url);
				handler.sendMessage(msg);
			}
		});
	}
	/**
	 * 从内存缓存中获取图片
	 * @param url 图片url
	 * @return
	 */
	public Bitmap getBitmapFromCache(String url){
		Bitmap bitmap = null;
		if(cache.containsKey(url)){
			bitmap = cache.get(url).get();
		}
		return bitmap;
	}
	/**
	 * 从网络中下载指定url的图片,可以指定大小，如果widht 和height同时不为0，这设置指定高度
	 * @param url
	 * @param width
	 * @param height
	 * @return
	 */
	private Bitmap downloadBitmap(String url, int width, int height){
		Bitmap bitmap = ApiClient.getNetBitMap(url);
		if(width > 0 && height > 0  && bitmap != null){
			//指定图片宽度和高度
			bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
		}
		return bitmap;
	}
}
