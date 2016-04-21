package com.alphabet.jack.listview_asnc;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class PaginationListView extends ListView implements OnScrollListener{
	//底部View
	private  View footerView;
	//ListView item个数
	int totalItemCount = 0;
	//最后可见的Item
	int lastVisibleItem = 0;
	//是否加载标示
	boolean isLoading = false;
	
	public PaginationListView(Context context) {
		super(context);
		initView(context);
	}
	
	public PaginationListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
	}

	public PaginationListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	
	/**
	 * 初始化ListView
	 */
	private void initView(Context context){
		LayoutInflater mInflater = LayoutInflater.from(context);
		footerView = mInflater.inflate(R.layout.listview_foot, null);
		footerView.setVisibility(View.GONE);
		this.setOnScrollListener(this);
		//添加底部View
		this.addFooterView(footerView);
	}

	public void setAdapter(ICall iCall,BaseAdapter adapter){
		if (iCall != null)
			iCall.onAdapter(adapter);
	}

	public ICall iCall;
	public interface ICall{
		public void onAdapter(BaseAdapter aAdapter);
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//当滑动到底端，并滑动状态为 not scrolling
		if(lastVisibleItem == totalItemCount && scrollState == SCROLL_STATE_IDLE){
			if(!isLoading){
				isLoading = true;
				//设置可见
			 footerView.setVisibility(VISIBLE);
				//加载数据
				if (onLoadListener != null)
				onLoadListener.onLoad();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.lastVisibleItem = firstVisibleItem + visibleItemCount;
		this.totalItemCount = totalItemCount;
		 Log.i("MainActivity", "onScroll滚动：\n"
                 + firstVisibleItem + "\t  " + visibleItemCount +"\t  " + totalItemCount);
	}
	
	private OnLoadListener onLoadListener;
	public void setOnLoadListener(OnLoadListener onLoadListener){
		this.onLoadListener = onLoadListener;
	}
	
	/**
	 * 加载数据接口
	 * @author Administrator
	 *
	 */
	public interface OnLoadListener{
		void onLoad();
	}
	
	/**
	 * 数据加载完成
	 */
	public void loadComplete(){
		footerView.setVisibility(View.GONE);
		isLoading = false;
		this.invalidate();
	}

}
