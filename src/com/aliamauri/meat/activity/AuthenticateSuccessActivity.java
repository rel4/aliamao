package com.aliamauri.meat.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 认证 *
 * 
 * @author ych
 * 
 */
public class AuthenticateSuccessActivity extends BaseActivity implements
		OnClickListener {

	private TextView tv_authenicate_success_time;

	@Override
	protected View getRootView() {
		View view = View.inflate(AuthenticateSuccessActivity.this,
				R.layout.authenticate_success, null);
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "会员认证成功";
	}

	@Override
	protected void initView() {
		tv_authenicate_success_time = (TextView) findViewById(R.id.tv_authenicate_success_time);

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

	@Override
	protected void setListener() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		setListener();
		countdown();
	}

	/**
	 * 设置倒计时启动装置
	 */
	private void countdown() {
		new TimeCount(6000, 1000).start();
	}

	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			// 参数依次为总时长,和计时的时间间隔
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			tv_authenicate_success_time.setText(String
					.valueOf((millisUntilFinished / 1000)) + "秒后返回首页");
		}

		@Override
		public void onFinish() {

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

}
