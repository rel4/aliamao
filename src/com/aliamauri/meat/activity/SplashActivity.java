package com.aliamauri.meat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.SplashBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.PhoneInfoUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

/**
 * 闪屏页面
 * 
 * @author ych
 * 
 */
public class SplashActivity extends Activity implements OnClickListener {

	HttpHelp httpHelp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		initData();
	}

	private void initData() {
		sentToCount();
		if (PrefUtils.getBoolean(UIUtils.getContext(),
				GlobalConstant.IS_LOGINED, false)) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					goMainActivity();
				}
			}, 2000);

		} else {
			// initNet();
			// finish();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					startActivity(new Intent(UIUtils.getContext(),
							RegisterActivity.class));
					SplashActivity.this.finish();
				}
			}, 2000);
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

	private void sentToCount() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter("macinfo",
				PhoneInfoUtils.getPhoneMac(UIUtils.getContext()));
		params.addBodyParameter("time", "");
		params.addBodyParameter("channelc", GlobalConstant.APP_CHANNEL_C);
		params.addBodyParameter("channel", GlobalConstant.APP_CHANNEL);
		params.addBodyParameter("imei",
				PhoneInfoUtils.getPhoneImei(UIUtils.getContext()));
		httpHelp.sendPost(NetworkConfig.getSplashCount(), params,
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null) {
							return;
						}
					}
				});
	}

	private void initNet() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		String isLoginUrl = NetworkConfig.getSplash();
		httpHelp.sendGet(isLoginUrl, SplashBean.class,
				new MyRequestCallBack<SplashBean>() {
					@Override
					public void onSucceed(SplashBean bean) {
						if (bean == null) {
							SplashActivity.this.finish();
							return;
						}
						if ("1".equals(bean.status)) {
							goLoginActivity(bean);

						}
						SplashActivity.this.finish();
					}

				});

	}

	/**
	 * 进入mainActivity 的方法。
	 */
	private void goMainActivity() {
		startActivity(new Intent(UIUtils.getContext(), MainActivity.class));
		SplashActivity.this.finish();
	}

	/**
	 * 进入mainActivity 的方法。
	 */
	private void goLoginActivity(SplashBean bean) {
		Intent i = new Intent(UIUtils.getContext(), LoginActivity.class);
		i.putExtra("splash", bean.cont.userinfo.id);
		startActivity(i);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

}
