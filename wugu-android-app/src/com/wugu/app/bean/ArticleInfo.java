package com.wugu.app.bean;

import java.io.Serializable;

public class ArticleInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;//id
	private String title;//标题
	private String detail;//内容
	private int avatar;//图片id
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
	public int getAvatar() {
		return avatar;
	}
	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}
}
