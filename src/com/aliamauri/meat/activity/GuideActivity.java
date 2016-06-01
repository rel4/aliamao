package com.aliamauri.meat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

public class GuideActivity extends Activity implements OnPageChangeListener {
	private ViewPager viewPager;
	private ImageView[] mImageViews;
	private int[] imgIdArray;
	private MyAdapter myAdapter;
	private Button tv_guide_click;
	private HttpHelp httpHelp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		// Intent i = getIntent();
		// init();
		// setListener(i.getExtras().get("type").toString().trim());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SplashScreen"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SplashScreen"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
													// onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

	/**
	 * 进入mainActivity 的方法。
	 */
	private void goMainActivity() {
		startActivity(new Intent(UIUtils.getContext(), MainActivity.class));
		finish();
	}

	private void setListener(final String type) {
		tv_guide_click.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (type) {
				case "1": // 已有该用户进入首页，储存用户信息
					goMainActivity();
					finish();
					break;
				case "2": // 新用户进入倒计时界面
					startActivity(new Intent(UIUtils.getContext(),
							RegisterActivity.class));
					finish();
					break;

				default:
					break;
				}
			}
		});
	}

	private void init() {
		tv_guide_click = (Button) findViewById(R.id.tv_guide_click);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		// R.drawable.guide01
		imgIdArray = new int[] {};

		mImageViews = new ImageView[imgIdArray.length];
		for (int i = 0; i < mImageViews.length; i++) {
			ImageView imageView = new ImageView(this);
			mImageViews[i] = imageView;
			imageView.setBackgroundResource(imgIdArray[i]);
		}
		myAdapter = new MyAdapter();
		viewPager.setAdapter(myAdapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(0);
		// viewPager.setScanScroll();
	}

	public class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageViews.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mImageViews[position
					% mImageViews.length]);

		}

		@Override
		public Object instantiateItem(View container, int position) {

			((ViewPager) container).addView(mImageViews[position], 0);
			return mImageViews[position];

		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 > 4) {
			this.onPageSelected(arg0 - 1);
		}
		if (arg0 == 4) {
			tv_guide_click.setVisibility(View.VISIBLE);
		} else {
			tv_guide_click.setVisibility(View.GONE);
		}
	}

}