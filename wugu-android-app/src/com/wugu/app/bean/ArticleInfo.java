package com.wugu.app.bean;

import java.io.Serializable;

import android.graphics.Bitmap;

public class ArticleInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;//id
	private String title;//标题
	private String detail;//内容
	private String imgUrl;//图片url
	private String url;//详情url
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
