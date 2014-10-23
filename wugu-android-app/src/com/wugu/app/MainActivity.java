package com.wugu.app;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wugu.app.adapter.NewsAdapter;
import com.wugu.app.api.ApiClient;
import com.wugu.app.bean.ArticleInfo;

public class MainActivity extends Activity {
	private List<ArticleInfo> articleList = new ArrayList<ArticleInfo>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//listview 标签id
		ListView view = (ListView) findViewById(R.id.main_list_view_id);
		setArticleInfo();
		
		view.setAdapter(new NewsAdapter(this, articleList, R.layout.item_main));
		view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			//处理Item的点击事件
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				ArticleInfo article = articleList.get(position);
				Toast.makeText(MainActivity.this, "title: "+article.getTitle()+", detail: "+ article.getDetail(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setArticleInfo(){
		articleList.clear();
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
					articleList.add(article);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
