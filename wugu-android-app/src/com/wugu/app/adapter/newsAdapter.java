package com.wugu.app.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wugu.app.R;
import com.wugu.app.api.ApiClient;
import com.wugu.app.bean.ArticleInfo;
import com.wugu.app.common.BitMapManager;
import com.wugu.app.utils.StringUtils;

public class NewsAdapter extends BaseAdapter implements Serializable{
	
	private static final long serialVersionUID = -625565531539707986L;
	//资源链接器(读取xml文件返回视图)
	private LayoutInflater inflater ;
	//需要显示的新闻列表
	private List<ArticleInfo> newsList;
	//视图列表文件layout/xx
	private int resource;
	private BitMapManager bitmapManger;
	
	public NewsAdapter(Context context, List<ArticleInfo> list, int resource) {
		this.inflater = LayoutInflater.from(context);
		this.newsList = list;
		this.resource = resource;
		
		//创建图片管理器，并设置默认图片
		bitmapManger = new BitMapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dface_loading));
	}

	@Override
	public int getCount() {
		return newsList.size();
	}

	@Override
	public Object getItem(int position) {
		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	/**
	 * Get a View that displays the data at the specified position in the data set
	 * You can either create a View manually or inflate it from an XML layout file.
	 * @param position 需要获取的元素的下标
	 * @param convertView
	 * @param parent
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView;
		if(convertView == null){
			convertView = inflater.inflate(resource, null);
			listItemView = new ListItemView();
			
			listItemView.title = (TextView) convertView.findViewById(R.id.item_main_title_id);
			listItemView.detail = (TextView) convertView.findViewById(R.id.item_main_detail_id);
			listItemView.image = (ImageView) convertView.findViewById(R.id.item_main_img_id);
			convertView.setTag(listItemView);
		}else{
			listItemView = (ListItemView) convertView.getTag();
		}
		ArticleInfo article = newsList.get(position);
		String imgurl = article.getImgUrl();
		//判断是否有图片，如果有图片则获取到图片并显示
		if(!StringUtils.isEmpty(imgurl)){
			bitmapManger.loadBitmap(imgurl, listItemView.image, 60, 60);
			listItemView.image.setVisibility(ImageView.VISIBLE);
		}else{
			listItemView.image.setVisibility(ImageView.GONE);
		}
		listItemView.title.setText(article.getTitle());
		listItemView.detail.setText(article.getDetail());
		return convertView;
	}

	static class ListItemView{
		TextView title;
		TextView detail;
		ImageView image;
	}
}
