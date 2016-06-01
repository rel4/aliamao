package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.connetJS.MyWebViewClient;
import com.aliamauri.meat.connetJS.WebAppInterface;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.utils.WebViewUtils;

public class WebViewActivity extends BaseActivity implements OnClickListener {

	private WebView wv_webpage;
	private WebAppInterface webAppInterface;
	private MyWebViewClient myWebViewClient;

	private LinearLayout ll_flush; //
	private TextView tv_refresh;
	private ProgressBar pbProgress;
	private String url = "";

	// private Intent webIntent;

	@Override
	protected View getRootView() {
		if (getIntent().getStringExtra(GlobalConstant.INTENT_DATA) != null) {
			url = getIntent().getStringExtra(GlobalConstant.INTENT_DATA);
		}
		return View.inflate(UIUtils.getContext(), R.layout.activity_webview,
				null);
	}

	@Override
	protected void initView() {
		wv_webpage = (WebView) findViewById(R.id.wv_webpage);
		ll_flush = (LinearLayout) findViewById(R.id.ll_flush);
		tv_refresh = $(R.id.tv_refresh);
		pbProgress = (ProgressBar) findViewById(R.id.pb_progress);

		tv_refresh.setOnClickListener(this);
		setWebView();
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "";
	}

	private void setWebView() {
		WebViewUtils.a(wv_webpage);
		myWebViewClient = new MyWebViewClient(ll_flush, pbProgress, null);
		wv_webpage.setWebViewClient(myWebViewClient);
		wv_webpage.addJavascriptInterface(webAppInterface, "Web");
		myWebViewClient.shouldOverrideUrlLoading(wv_webpage, url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && wv_webpage.canGoBack()) {
			wv_webpage.goBack();// 返回前一个页面

			return false;

		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 根据id查找控件
	 * 
	 * @param id
	 * @return
	 */
	public <T extends View> T $(int id) {
		return (T) findViewById(id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_refresh:
			if (wv_webpage.getUrl() != null && !"".equals(wv_webpage.getUrl())) {
				myWebViewClient.shouldOverrideUrlLoading(wv_webpage,
						wv_webpage.getUrl());
			} else {
				if (wv_webpage.canGoBack()) {
					wv_webpage.goBack();
				} else {
					myWebViewClient.shouldOverrideUrlLoading(wv_webpage, url);
				}
				UIUtils.showToast(UIUtils.getContext(), "请检查您的网络");
			}
			break;

		default:
			break;
		}
	}

}
