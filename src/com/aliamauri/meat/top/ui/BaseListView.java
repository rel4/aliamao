package com.aliamauri.meat.top.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ListView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.top.manager.BitmapHelper;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

public class BaseListView extends ListView {

	public BaseListView(Context context) {
		super(context);
		init();
	}

	public BaseListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();

	}

	public BaseListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	// setSelector 点击显示的颜色
	// setCacheColorHint 拖拽的颜色
	// setDivider 每个条目的间隔
	private void init() {
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
