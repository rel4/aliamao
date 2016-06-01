package com.aliamauri.meat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.RetrievePasswordBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("JavascriptInterface")
public class RetrievePasswordActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_title;
	private ImageButton ibtn_left_home_back;
	private WebView wv_webpage;
	private HttpHelp httpHelp;
	private RetrievePasswordBean retrievePasswordBean;
	private WebAppInterface webAppInterface;

	@Override
	protected View getRootView() {
		View view = View.inflate(RetrievePasswordActivity.this,
				R.layout.web_page, null);
		return view;
	}

	@Override
	protected void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		wv_webpage = (WebView) findViewById(R.id.wv_webpage);
		ibtn_left_home_back = (ImageButton) findViewById(R.id.ibtn_left_home_back);

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
		ibtn_left_home_back.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		initViewData();
		netWork();
	}

	private void netWork() {
		httpHelp = new HttpHelp();
		httpHelp.sendGet(NetworkConfig.getRetrievePassword(),
				RetrievePasswordBean.class,
				new MyRequestCallBack<RetrievePasswordBean>() {

					@Override
					public void onSucceed(RetrievePasswordBean bean) {
						if (bean == null) {
							return;
						}
						retrievePasswordBean = bean;
						initWebView();
						setWebView();
					}
				});
	}

	private void setWebView() {
		wv_webpage.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);// 使用当前WebView处理跳转
				return true;// true表示此事件在此处被处理，不需要再广播
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// 有页面跳转时被回调
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// 页面跳转结束后被回调
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// 出错
			}
		});
	}

	private void initWebView() {
		WebSettings webSettings = wv_webpage.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webAppInterface = new WebAppInterface();
		wv_webpage.addJavascriptInterface(webAppInterface, "Login");
		wv_webpage.loadUrl(retrievePasswordBean.url);
	}

	private void initViewData() {
		tv_title.setText("找回密码");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibtn_left_home_back:
			if (wv_webpage.canGoBack()) {
				wv_webpage.goBack();
			} else {
				finish();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 自定义的Android代码和JavaScript代码之间的桥梁类
	 * 
	 * @author 1
	 * 
	 */
	final class WebAppInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		WebAppInterface() {
		}

		/** Show a toast from the web page */
		@JavascriptInterface
		public void getUcode(String tel) {
			PrefUtils.setBoolean(UIUtils.getContext(),
					GlobalConstant.IS_LOGINED, false);
			Intent i = new Intent(RetrievePasswordActivity.this,
					LoginActivity.class);
			i.putExtra("tel", tel);
			setResult(2, i);
			finish();
		}

		@JavascriptInterface
		public void gotoLogin(String tel) {
			Intent i = new Intent();
			i.putExtra("tel", tel);
			setResult(2, i);
			finish();
		}

		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
		}
	}

}
