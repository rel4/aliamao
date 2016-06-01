package com.aliamauri.meat.top.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.aliamauri.meat.R;
import com.aliamauri.meat.top.manager.BitmapHelper;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

public class BasePullToRefreshListView extends PullToRefreshListView {

	public BasePullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BasePullToRefreshListView(
			Context context,
			com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode,
			com.handmark.pulltorefresh.library.PullToRefreshBase.AnimationStyle style) {
		super(context, mode, style);
		init();
	}

	public BasePullToRefreshListView(Context context,
			com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode) {
		super(context, mode);
		init();
	}

	public BasePullToRefreshListView(Context context) {
		super(context);
		init();
	}

	// setSelector 点击显示的颜色
	// setCacheColorHint 拖拽的颜色
	// setDivider 每个条目的间隔
	private void init() {
//		this.demo();
		ILoadingLayout startLabels = this    
                .getLoadingLayoutProxy(true, false);    
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示    
        startLabels.setRefreshingLabel("正在载入...");// 刷新时    
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示    
//        startLabels.setTextTypeface(tf)
        ILoadingLayout endLabels = this.getLoadingLayoutProxy(    
                false, true);    
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示    
        endLabels.setRefreshingLabel("正在载入...");// 刷新时    
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示    
		// this.setSelector(getResources().getDrawable(R.drawable.nothing));
		// // lv.setCacheColorHint(android.R.color.transparent);
		// this.setDivider(getResources().getDrawable(R.drawable.nothing));

		// BitmapUtils 不是单例的
		BitmapUtils bitmapUtils = BitmapHelper.getBitmapUtils();
		bitmapUtils.configDefaultLoadingImage(R.drawable.default_logo); // 加载中显示的图片
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.default_logo); // 加载失败
		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		// listView飞速滑动的时候 不需要加载图片 参数2 慢慢滑动的时候加载图片 true不加载 false 加载
		// 参数3 飞速滑动的时候 不加载图片
		this.setOnScrollListener(new PauseOnScrollListener(bitmapUtils, false,
				true));
	}

}
