package com.aliamauri.meat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.weight.wheelview.NumericWheelAdapter;
import com.aliamauri.meat.weight.wheelview.OnWheelScrollListener;
import com.aliamauri.meat.weight.wheelview.WheelAdapter;
import com.aliamauri.meat.weight.wheelview.WheelView;
import com.umeng.analytics.MobclickAgent;

/**
 * ID搜索
 * 
 * @author ych
 * 
 */
public class PopupwindowConditionActivity extends Activity implements
		OnClickListener {
	private Intent i;

	private TextView tv_pop_condition_title;

	private WheelView wv_condition_left;

	private WheelView wv_condition_right;

	private TextView tv_pop_condition_success;// 完成按钮

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popupwindow_condition_sex);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.activity_title);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		i = getIntent();
		initView();
		getContentView();
		setListener();
	}

	private void getContentView() {
		if ("sex".equals(i.getExtras().get("popuptype"))) {
			initSexView();
		} else if ("age".equals(i.getExtras().get("popuptype"))) {
			initAgeView();
		} else if ("far".equals(i.getExtras().get("popuptype"))) {
			initFarView();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		  MobclickAgent.onResume(this);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		 MobclickAgent.onPause(this);
	}
	
	private void initFarView() {
		String sexData = i.getExtras().get("coditiondata").toString().trim();
		sexData = sexData.substring(0, sexData.length() - 2);
		String[] strIndex = sexData.split("-");

		tv_pop_condition_title.setText("距离");
		// wv_condition_left.SET_TEXT_SIZE = true;
		// wv_condition_left.CUSTOMER_TEXT_SIZE = 42;
		wv_condition_left.setLabel("km");
		wv_condition_left.setAdapter(new NumericWheelAdapter(1, 100));
		// wv_condition_right.SET_TEXT_SIZE = true;
		// wv_condition_right.CUSTOMER_TEXT_SIZE = 42;
		wv_condition_right.setLabel("km");
		wv_condition_right.setAdapter(new NumericWheelAdapter(1, 100));

		// wv_condition_left.setCurrentItem(Integer.parseInt(strIndex[0]) - 1);
		// wv_condition_right.setCurrentItem(Integer.parseInt(strIndex[1]) - 1);
		wv_condition_left.setCurrentItem(Integer.parseInt(strIndex[0]) - 1);

		leftPosition = wv_condition_left.getCurrentItem();
		ageRightAdapter = new NumericWheelAdapter(leftPosition + 1, 100);
		wv_condition_right.setAdapter(ageRightAdapter);
		wv_condition_right.setCurrentItem(Integer.parseInt(strIndex[1])
				- Integer.parseInt(ageRightAdapter.getItem(0)));
		wv_condition_left.addScrollingListener(sexLeftListener);

	}

	private static NumericWheelAdapter ageRightAdapter;

	private void initAgeView() {
		String sexData = i.getExtras().get("coditiondata").toString().trim();
		sexData = sexData.substring(0, sexData.length() - 1);
		String[] strIndex = sexData.split("-");

		tv_pop_condition_title.setText("年龄");
		wv_condition_left.SET_TEXT_SIZE = true;
		wv_condition_left.CUSTOMER_TEXT_SIZE = 42;
		wv_condition_left.setAdapter(new NumericWheelAdapter(1, 100));
		wv_condition_right.SET_TEXT_SIZE = true;
		wv_condition_right.CUSTOMER_TEXT_SIZE = 42;

		wv_condition_left.setCurrentItem(Integer.parseInt(strIndex[0]) - 1);

		leftPosition = wv_condition_left.getCurrentItem();
		ageRightAdapter = new NumericWheelAdapter(leftPosition + 1, 100);
		wv_condition_right.setAdapter(ageRightAdapter);
		wv_condition_right.setCurrentItem(Integer.parseInt(strIndex[1])
				- Integer.parseInt(ageRightAdapter.getItem(0)));
		wv_condition_left.addScrollingListener(sexLeftListener);
	}

	private static int leftPosition = 0;

	private static int rightPosition = 0;
	private static int rightFirst = 0;
	OnWheelScrollListener sexLeftListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			leftPosition = wheel.getCurrentItem();
			rightPosition = wv_condition_right.getCurrentItem();
			rightFirst = Integer.parseInt(ageRightAdapter.getItem(0));
			ageRightAdapter = new NumericWheelAdapter(leftPosition + 1, 100);
			wv_condition_right.setAdapter(ageRightAdapter);
			if (rightFirst + rightPosition > leftPosition + 1) {
				wv_condition_right
						.setCurrentItem(rightPosition
								- (Integer.parseInt(ageRightAdapter.getItem(0)) - rightFirst));
			} else {
				wv_condition_right.setCurrentItem(0);
			}
		}
	};

	private void initSexView() {
		String sexData = i.getExtras().get("coditiondata").toString().trim();
		int sexIndex = 0;
		if ("不限".equals(sexData)) {
			sexIndex = 0;
		} else if ("男".equals(sexData)) {
			sexIndex = 1;
		} else if ("女".equals(sexData)) {
			sexIndex = 2;
		}
		tv_pop_condition_title.setText("性别");
		wv_condition_right.setVisibility(View.GONE);
		wv_condition_left.SET_TEXT_SIZE = true;
		wv_condition_left.CUSTOMER_TEXT_SIZE = 42;
		wv_condition_left.setAdapter(new WheelAdapter() {
			String sex[] = { "女", "男", "不限" };

			@Override
			public int getMaximumLength() {
				// TODO Auto-generated method stub
				return sex.length;
			}

			@Override
			public int getItemsCount() {
				// TODO Auto-generated method stub
				return sex.length;
			}

			@Override
			public String getItem(int index) {
				// TODO Auto-generated method stub
				return sex[index];
			}
		});
		wv_condition_left.setCurrentItem(sexIndex);
	}

	private void setListener() {
		tv_pop_condition_success.setOnClickListener(this);
	}

	private void initView() {
		wv_condition_left = (WheelView) findViewById(R.id.wv_condition_left);
		wv_condition_right = (WheelView) findViewById(R.id.wv_condition_right);
		tv_pop_condition_title = (TextView) findViewById(R.id.tv_pop_condition_title);
		tv_pop_condition_success = (TextView) findViewById(R.id.tv_pop_condition_success);

		// wv_condition_left.itemHeight = 98;
		// wv_condition_left.ADDITIONAL_ITEM_HEIGHT = 55;
		// wv_condition_right.itemHeight = 98;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if ("sex".equals(i.getExtras().get("popuptype"))) {
			setResult(1, i);
		} else if ("age".equals(i.getExtras().get("popuptype"))) {
			setResult(2, i);
		} else if ("far".equals(i.getExtras().get("popuptype"))) {
			setResult(3, i);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_pop_condition_success:
			if ("sex".equals(i.getExtras().get("popuptype"))) {
				i.putExtra("popCondition", wv_condition_left.getCurrentItem());
				setResult(1, i);
			} else if ("age".equals(i.getExtras().get("popuptype"))) {
				i.putExtra("right", wv_condition_right.getCurrentItem()
						+ Integer.parseInt(ageRightAdapter.getItem(0)));
				i.putExtra("left", wv_condition_left.getCurrentItem() + 1);
				setResult(2, i);
			} else if ("far".equals(i.getExtras().get("popuptype"))) {
				i.putExtra("right", wv_condition_right.getCurrentItem() + 1);
				i.putExtra("left", wv_condition_left.getCurrentItem() + 1);
				setResult(3, i);
			}
			finish();
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}
}
