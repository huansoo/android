package com.wugu.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wugu.app.adapter.newsAdapter;
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
		
		view.setAdapter(new newsAdapter(this, articleList, R.layout.item_main));
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
		for(int i = 0; i < 10; i++){
			ArticleInfo article = new ArticleInfo();
			article.setId(100+i);
			article.setTitle("新闻标题"+i);
			article.setDetail("我是详细描述信息啊"+i);
			article.setAvatar(getArticleImg(i));
			articleList.add(article);
		}
	}
	private int getArticleImg(int i){
		switch (i) {
		case 1:
			return R.drawable.f001;
		case 2:
			return R.drawable.f002;
		case 3:
			return R.drawable.f003;
		case 4:
			return R.drawable.f004;
		case 5:
			return R.drawable.f005;
		case 6:
			return R.drawable.f006;
		case 7:
			return R.drawable.f007;
		case 8:
			return R.drawable.f008;
		default:
			return 0;
		}
	}
	
}
