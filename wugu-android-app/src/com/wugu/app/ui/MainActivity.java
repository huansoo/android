package com.wugu.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wugu.app.AppContext;
import com.wugu.app.AppException;
import com.wugu.app.R;
import com.wugu.app.adapter.FarmingTongListAdapter;
import com.wugu.app.adapter.NewsListAdapter;
import com.wugu.app.bean.ArticleInfo;
import com.wugu.app.bean.FarmingTongInfo;
import com.wugu.app.common.UIHelper;
import com.wugu.app.utils.StringUtils;
import com.wugu.app.widget.PullToRefreshListView;

public class MainActivity extends Activity {
	
	private List<ArticleInfo> articleList = new ArrayList<ArticleInfo>();
	private List<FarmingTongInfo> farmingTongList = new ArrayList<FarmingTongInfo>();
	
	private NewsListAdapter newsListAdapter;
	private FarmingTongListAdapter farmingTongListAdapter;
	
	private PullToRefreshListView pullToRefreshArticle;
	private PullToRefreshListView pullToRefreshOriginal;
	private PullToRefreshListView pullToRefreshFarmingTong;
	private PullToRefreshListView pullToRefreshColumnist;
	private PullToRefreshListView pullToRefreshSubject;
	
	private Button frameNewsButton;
	private Button frameOriginalButton;
	private Button frameFarmingTongButton;
	private Button frameColumnistButton;
	private Button frameSubjectButton;
	
	private Handler articleHandler;
	private Handler originalHandler;
	private Handler farmingTongHandler;
	private Handler columnistHandler;
	private Handler subjectHandler;
	
	private View article_footer;
	private View farming_tong_footer;
	private View original_footer;
	private View columnist_footer;
	private View subject_footer;

	private TextView article_foot_more;
	private TextView farming_tong_foot_more;
	private TextView original_foot_more;
	private TextView columnist_foot_more;
	private TextView subject_foot_more;
	
	private ProgressBar article_progress_bar;
	private ProgressBar farming_tong_progress_bar;
	private ProgressBar original_progress_bar;
	private ProgressBar columnist_progress_bar;
	private ProgressBar subject_progress_bar;
	
	private int articleSumData;
	private int farmingTongSumData;
	
	private static final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(!isNetworkConnected()){
			UIHelper.ToastMessageLong(MainActivity.this, R.string.network_is_not_connected);
		}
		this.initFrameButton();
		this.initFrameListView();
	}
	/**
	 * 初始化所有listView
	 */
	private void initFrameListView(){
		this.initNewsListView();
		this.initFarmingTongListView();
		// 加载listview数据
		this.initFrameListViewData();
		
	}
	/**
	 * 初始化handler和article数据
	 */
	private void initFrameListViewData(){
		articleHandler = getWuguHandler(pullToRefreshArticle, newsListAdapter, article_foot_more, article_progress_bar, AppContext.PAGE_SIZE);
		farmingTongHandler = getWuguHandler(pullToRefreshFarmingTong, farmingTongListAdapter, farming_tong_foot_more, farming_tong_progress_bar, AppContext.PAGE_SIZE);
		//加载新闻列表数据
		if(articleList.isEmpty()){
			loadArticleListData(1, 0, articleHandler, UIHelper.LISTVIEW_ACTION_INIT);
		}
	}
	
	/**
	 * 初始化新闻列表
	 */
	private void initNewsListView(){
		newsListAdapter = new NewsListAdapter(this, articleList, R.layout.item_main);
		pullToRefreshArticle =  (PullToRefreshListView) findViewById(R.id.frame_listview_news);
		article_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		//listView 下方的进度条
		article_progress_bar = (ProgressBar) article_footer.findViewById(R.id.listview_foot_progress);
		//listView 下方的加载更多
		article_foot_more = (TextView) article_footer.findViewById(R.id.listview_foot_more);
		pullToRefreshArticle.addFooterView(article_footer);// 添加底部视图 必须在setAdapter前
		
		pullToRefreshArticle.setAdapter(newsListAdapter);
		//监听条目
		pullToRefreshArticle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				ArticleInfo article = articleList.get(position);
				UIHelper.showUrlRedirect(MainActivity.this, article.getUrl());
			}
		});
		//监听滚动listView
		pullToRefreshArticle.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				pullToRefreshArticle.onScrollStateChanged(view, scrollState);

			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				pullToRefreshArticle.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
//				// 数据为空--不用继续下面代码了
//				if (articleList.isEmpty())
//					return;
//
//				// 判断是否滚动到底部
//				boolean scrollEnd = false;
//				try {
//					if (view.getPositionForView(article_footer) == view
//							.getLastVisiblePosition())
//						scrollEnd = true;
//				} catch (Exception e) {
//					scrollEnd = false;
//				}
//
//				int lvDataState = StringUtils.toInt(pullToRefreshArticle.getTag());
//				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
//					pullToRefreshArticle.setTag(UIHelper.LISTVIEW_DATA_LOADING);
//					article_foot_more.setText(R.string.load_ing);
//					article_progress_bar.setVisibility(View.VISIBLE);
//					// 当前pageIndex
//					int pageIndex = articleSumData / AppContext.PAGE_SIZE;
//					loadArticleListData(1, pageIndex, articleHandler,
//							UIHelper.LISTVIEW_ACTION_SCROLL);
//				}
				Log.v("onScroll", "firstVisibleItem="+firstVisibleItem+",visibleItemCount="+visibleItemCount+",totalItemCount="+totalItemCount);
			}
		});
		
	}
	/**
	 * 初始化农事通列表
	 */
	private void initFarmingTongListView(){
		farmingTongListAdapter = new FarmingTongListAdapter(this, farmingTongList, R.layout.item_farming_tong);
		pullToRefreshFarmingTong = (PullToRefreshListView) findViewById(R.id.frame_listview_farming_tong);
		
		farming_tong_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		//listView 下方的进度条
		farming_tong_progress_bar = (ProgressBar) farming_tong_footer.findViewById(R.id.listview_foot_progress);
		//listView 下方的加载更多
		farming_tong_foot_more = (TextView) farming_tong_footer.findViewById(R.id.listview_foot_more);
		pullToRefreshFarmingTong.addFooterView(farming_tong_footer);// 添加底部视图 必须在setAdapter前
		pullToRefreshFarmingTong.setAdapter(farmingTongListAdapter);
		
		pullToRefreshFarmingTong.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				FarmingTongInfo article = farmingTongList.get(position);
				UIHelper.showUrlRedirect(getApplicationContext(), article.getUrl());
			}
		});
	}
	/**
	 * 获取handler
	 * @param lv
	 * @param adapter
	 * @param more
	 * @param progress
	 * @param pageSize
	 * @return
	 */
	private Handler getWuguHandler(final PullToRefreshListView lv,
			final BaseAdapter adapter, final TextView more,
			final ProgressBar progress, final int pageSize) {
		return new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what > 0){
					handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
					if (msg.what < pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						//刷新listView中数据
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					} else if (msg.what == pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
					}
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					((AppException) msg.obj).makeToast(MainActivity.this);
				}
				if (adapter.getCount() == 0) {
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(ProgressBar.GONE);
//				mHeadProgress.setVisibility(ProgressBar.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					Log.v(TAG, String.valueOf(lv));
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
					lv.setSelection(0);
				} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
			}
			
		};
	}
	/**
	 * 加载新闻列表数据
	 */
	private void loadArticleListData(final int catalog, final int pageIndex,
			final Handler handler, final int action){
		new Thread(){
			@Override
			public void run() {
				Message msg = Message.obtain();
				boolean refresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL){
					refresh = true;
				}
				List<ArticleInfo> articleList = AppContext.getArticleList();
				try {
					msg.what = articleList.size();
					msg.obj = articleList;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
				handler.sendMessage(msg);
			}
		}.start();
	}
	/**
	 * 加载农事通数据
	 */
	@SuppressLint("NewApi")
	private void loadFarmingTongListData(final int pageIndex,
			final Handler handler, final int action){
		new Thread(){
			@Override
			public void run() {
				Message msg = Message.obtain();
				boolean refresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL){
					refresh = true;
				}
				List<FarmingTongInfo> farmingTongList = AppContext.getFarmingTongList();
				try {
					msg.what = farmingTongList.size();
					msg.obj = farmingTongList;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_FARMING_TONG;
				handler.sendMessage(msg);
			}
		}.start();
	}
	/**
	 * 初始化按钮
	 */
	private void initFrameButton(){
		frameNewsButton = (Button) findViewById(R.id.frame_news_button_id);
		frameOriginalButton = (Button) findViewById(R.id.frame_original_button_id);
		frameFarmingTongButton = (Button) findViewById(R.id.frame_farming_tong_button_id);
		frameColumnistButton = (Button) findViewById(R.id.frame_columnist_button_id);
		frameSubjectButton = (Button) findViewById(R.id.frame_subject_button_id);
		
		frameNewsButton.setOnClickListener(frameButtonOncliClickListener(frameNewsButton, 1));
		frameOriginalButton.setOnClickListener(frameButtonOncliClickListener(frameOriginalButton, 2));
		frameFarmingTongButton.setOnClickListener(frameButtonOncliClickListener(frameFarmingTongButton, 3));
		frameColumnistButton.setOnClickListener(frameButtonOncliClickListener(frameColumnistButton, 4));
		frameSubjectButton.setOnClickListener(frameButtonOncliClickListener(frameSubjectButton, 5));
	}
	/**
	 * frame btn监听事件
	 * @return
	 */
	private View.OnClickListener frameButtonOncliClickListener(final Button btn, final int type){
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//如果是原创
				if(btn == frameOriginalButton){
					pullToRefreshArticle.setVisibility(View.VISIBLE);
				}else{
					frameOriginalButton.setEnabled(true);
				}
				
				//如果农事通
				if(btn == frameFarmingTongButton){
					pullToRefreshArticle.setVisibility(View.GONE);
					pullToRefreshFarmingTong.setVisibility(View.VISIBLE);
					loadFarmingTongListData(0, farmingTongHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
					frameFarmingTongButton.setEnabled(false);
				}else{
					frameFarmingTongButton.setEnabled(true);
				}
				//如果是专栏 
				if(btn == frameColumnistButton){
					frameColumnistButton.setEnabled(false);
				}else{
					frameColumnistButton.setEnabled(true);
				}
					
				//如果是专题
				if(btn == frameSubjectButton){
					frameSubjectButton.setEnabled(false);
				}else{
					frameSubjectButton.setEnabled(true);
				}
				
				//如果为新闻
				if(btn == frameNewsButton){
					frameNewsButton.setEnabled(false);
					pullToRefreshArticle.setVisibility(View.VISIBLE);
					pullToRefreshFarmingTong.setVisibility(View.GONE);
					loadArticleListData(1, 0, articleHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
				}else{
					frameNewsButton.setEnabled(true);
				}
			}
		};
	}
	/**
	 * listview数据处理
	 * 
	 * @param what
	 *            数量
	 * @param obj
	 *            数据
	 * @param objtype
	 *            数据类型
	 * @param actiontype
	 *            操作类型
	 * @return notice 通知信息
	 */
	private void handleLvData(int what, Object obj, int objtype,
			int actiontype) {
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newdata = 0;// 新加载数据-只有刷新动作才会使用到
			switch (objtype) {
			case UIHelper.LISTVIEW_DATATYPE_NEWS:
				List<ArticleInfo> newsTempList = (List<ArticleInfo>) obj;
				articleSumData = what;
				articleList.clear();// 先清除原有数据
				
				Log.v(TAG, String.valueOf(articleList));
				articleList.addAll(newsTempList);
				Log.v(TAG, String.valueOf(articleList));
				
				break;
			case UIHelper.LISTVIEW_DATATYPE_FARMING_TONG:
				List<FarmingTongInfo> farmingTongTempList = (List<FarmingTongInfo>) obj;
				farmingTongSumData = what;
				farmingTongList.clear();
				
				farmingTongList.addAll(farmingTongTempList);
				break;
//			case UIHelper.LISTVIEW_DATATYPE_BLOG:
//				BlogList blist = (BlogList) obj;
//				notice = blist.getNotice();
//				lvBlogSumData = what;
//				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//					if (lvBlogData.size() > 0) {
//						for (Blog blog1 : blist.getBloglist()) {
//							boolean b = false;
//							for (Blog blog2 : lvBlogData) {
//								if (blog1.getId() == blog2.getId()) {
//									b = true;
//									break;
//								}
//							}
//							if (!b)
//								newdata++;
//						}
//					} else {
//						newdata = what;
//					}
//				}
//				lvBlogData.clear();// 先清除原有数据
//				lvBlogData.addAll(blist.getBloglist());
//				break;
//			case UIHelper.LISTVIEW_DATATYPE_POST:
//				PostList plist = (PostList) obj;
//				notice = plist.getNotice();
//				lvQuestionSumData = what;
//				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//					if (lvQuestionData.size() > 0) {
//						for (Post post1 : plist.getPostlist()) {
//							boolean b = false;
//							for (Post post2 : lvQuestionData) {
//								if (post1.getId() == post2.getId()) {
//									b = true;
//									break;
//								}
//							}
//							if (!b)
//								newdata++;
//						}
//					} else {
//						newdata = what;
//					}
//				}
//				lvQuestionData.clear();// 先清除原有数据
//				lvQuestionData.addAll(plist.getPostlist());
//				break;
//			case UIHelper.LISTVIEW_DATATYPE_TWEET:
//				TweetList tlist = (TweetList) obj;
//				notice = tlist.getNotice();
//				lvTweetSumData = what;
//				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//					if (lvTweetData.size() > 0) {
//						for (Tweet tweet1 : tlist.getTweetlist()) {
//							boolean b = false;
//							for (Tweet tweet2 : lvTweetData) {
//								if (tweet1.getId() == tweet2.getId()) {
//									b = true;
//									break;
//								}
//							}
//							if (!b)
//								newdata++;
//						}
//					} else {
//						newdata = what;
//					}
//				}
//				lvTweetData.clear();// 先清除原有数据
//				lvTweetData.addAll(tlist.getTweetlist());
//				break;
//			case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
//				ActiveList alist = (ActiveList) obj;
//				notice = alist.getNotice();
//				lvActiveSumData = what;
//				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//					if (lvActiveData.size() > 0) {
//						for (Active active1 : alist.getActivelist()) {
//							boolean b = false;
//							for (Active active2 : lvActiveData) {
//								if (active1.getId() == active2.getId()) {
//									b = true;
//									break;
//								}
//							}
//							if (!b)
//								newdata++;
//						}
//					} else {
//						newdata = what;
//					}
//				}
//				lvActiveData.clear();// 先清除原有数据
//				lvActiveData.addAll(alist.getActivelist());
//				break;
//			case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
//				MessageList mlist = (MessageList) obj;
//				notice = mlist.getNotice();
//				lvMsgSumData = what;
//				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//					if (lvMsgData.size() > 0) {
//						for (Messages msg1 : mlist.getMessagelist()) {
//							boolean b = false;
//							for (Messages msg2 : lvMsgData) {
//								if (msg1.getId() == msg2.getId()) {
//									b = true;
//									break;
//								}
//							}
//							if (!b)
//								newdata++;
//						}
//					} else {
//						newdata = what;
//					}
//				}
//				lvMsgData.clear();// 先清除原有数据
//				lvMsgData.addAll(mlist.getMessagelist());
//				break;
//			}
//			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//				// 提示新加载数据
//				if (newdata > 0) {
//					NewDataToast
//							.makeText(
//									this,
//									getString(R.string.new_data_toast_message,
//											newdata), appContext.isAppSound())
//							.show();
//				} else {
//					NewDataToast.makeText(this,
//							getString(R.string.new_data_toast_none), false)
//							.show();
//				}
//			}
//			break;
//		case UIHelper.LISTVIEW_ACTION_SCROLL:
//			switch (objtype) {
//			case UIHelper.LISTVIEW_DATATYPE_NEWS:
//				NewsList list = (NewsList) obj;
//				notice = list.getNotice();
//				lvNewsSumData += what;
//				if (lvNewsData.size() > 0) {
//					for (News news1 : list.getNewslist()) {
//						boolean b = false;
//						for (News news2 : lvNewsData) {
//							if (news1.getId() == news2.getId()) {
//								b = true;
//								break;
//							}
//						}
//						if (!b)
//							lvNewsData.add(news1);
//					}
//				} else {
//					lvNewsData.addAll(list.getNewslist());
//				}
//				break;
//			case UIHelper.LISTVIEW_DATATYPE_BLOG:
//				BlogList blist = (BlogList) obj;
//				notice = blist.getNotice();
//				lvBlogSumData += what;
//				if (lvBlogData.size() > 0) {
//					for (Blog blog1 : blist.getBloglist()) {
//						boolean b = false;
//						for (Blog blog2 : lvBlogData) {
//							if (blog1.getId() == blog2.getId()) {
//								b = true;
//								break;
//							}
//						}
//						if (!b)
//							lvBlogData.add(blog1);
//					}
//				} else {
//					lvBlogData.addAll(blist.getBloglist());
//				}
//				break;
//			case UIHelper.LISTVIEW_DATATYPE_POST:
//				PostList plist = (PostList) obj;
//				notice = plist.getNotice();
//				lvQuestionSumData += what;
//				if (lvQuestionData.size() > 0) {
//					for (Post post1 : plist.getPostlist()) {
//						boolean b = false;
//						for (Post post2 : lvQuestionData) {
//							if (post1.getId() == post2.getId()) {
//								b = true;
//								break;
//							}
//						}
//						if (!b)
//							lvQuestionData.add(post1);
//					}
//				} else {
//					lvQuestionData.addAll(plist.getPostlist());
//				}
//				break;
//			case UIHelper.LISTVIEW_DATATYPE_TWEET:
//				TweetList tlist = (TweetList) obj;
//				notice = tlist.getNotice();
//				lvTweetSumData += what;
//				if (lvTweetData.size() > 0) {
//					for (Tweet tweet1 : tlist.getTweetlist()) {
//						boolean b = false;
//						for (Tweet tweet2 : lvTweetData) {
//							if (tweet1.getId() == tweet2.getId()) {
//								b = true;
//								break;
//							}
//						}
//						if (!b)
//							lvTweetData.add(tweet1);
//					}
//				} else {
//					lvTweetData.addAll(tlist.getTweetlist());
//				}
//				break;
//			case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
//				ActiveList alist = (ActiveList) obj;
//				notice = alist.getNotice();
//				lvActiveSumData += what;
//				if (lvActiveData.size() > 0) {
//					for (Active active1 : alist.getActivelist()) {
//						boolean b = false;
//						for (Active active2 : lvActiveData) {
//							if (active1.getId() == active2.getId()) {
//								b = true;
//								break;
//							}
//						}
//						if (!b)
//							lvActiveData.add(active1);
//					}
//				} else {
//					lvActiveData.addAll(alist.getActivelist());
//				}
//				break;
//			case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
//				MessageList mlist = (MessageList) obj;
//				notice = mlist.getNotice();
//				lvMsgSumData += what;
//				if (lvMsgData.size() > 0) {
//					for (Messages msg1 : mlist.getMessagelist()) {
//						boolean b = false;
//						for (Messages msg2 : lvMsgData) {
//							if (msg1.getId() == msg2.getId()) {
//								b = true;
//								break;
//							}
//						}
//						if (!b)
//							lvMsgData.add(msg1);
//					}
//				} else {
//					lvMsgData.addAll(mlist.getMessagelist());
//				}
//				break;
			}
			break;
		}
	}
	/**
	 * 检查是否连接网络
	 * @return
	 */
	private boolean isNetworkConnected(){
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = manager.getActiveNetworkInfo();
		return null != network && network.isConnectedOrConnecting();
	}
}
