package com.aliamauri.meat.connetJS;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.utils.UIUtils;

public class MyWebViewClient extends WebViewClient {
	private static final int UPDATE_DIALOG = 1;// 消息类型-弹出升级对话框
	private static final int NETWORK_ERROR = 2;// 消息类型-网络异常
	private static final int JSON_ERROR = 3;// 消息类型-JSON解析失败
	private static final int URL_ERROR = 4; // 请求的网址错误
	private static final int SERVER_ERROR = 5; // 请求的服务器错误
	private static final int URL_SUCCESS = 6; // 请求成功

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			pbProgress.setVisibility(View.GONE);
			switch (msg.what) {
			case NETWORK_ERROR:
				ll_flush.setVisibility(View.VISIBLE);
				UIUtils.showToast(UIUtils.getContext(), "网络异常");
				break;
			case JSON_ERROR:
				ll_flush.setVisibility(View.VISIBLE);
				UIUtils.showToast(UIUtils.getContext(), "数据解析异常");
				break;
			case URL_ERROR:
				UIUtils.showToast(UIUtils.getContext(), "亲~~资源找不到了~~");
				ll_flush.setVisibility(View.VISIBLE);
				break;
			case SERVER_ERROR:
				UIUtils.showToast(UIUtils.getContext(), "亲~~服务器太累了~~");
				ll_flush.setVisibility(View.VISIBLE);
				break;
			case URL_SUCCESS:
				ll_flush.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 加载网页并获取网络状态，做出相应的判断
	 * 
	 * @param view
	 * @param url
	 */
	public void getNetworkState(WebView view, String url, boolean firstTime,
			boolean goback) {

		if (url != null && UIUtils.checkNetworkAvailable(UIUtils.getContext())) {
			try {
				final HttpGet httpGet = new HttpGet(url);
				view.loadUrl(url);
				// final HttpGet httpGet = new
				// HttpGet("http://lminfo.cntttt.com:8888/api/test/500.php");
				ThreadManager.getThreadProxyPool().execute(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Message obtain = Message.obtain();

						HttpResponse response = null;
						int statusCode = 0;
						try {
							String htmlContent;
							HttpClient httpClient = new DefaultHttpClient();
							response = httpClient.execute(httpGet);
						} catch (IOException e) {
							obtain.what = NETWORK_ERROR;
							mHandler.sendMessage(obtain);
							e.printStackTrace();
							return;
						} catch (IllegalStateException ise) {
							obtain.what = NETWORK_ERROR;
							mHandler.sendMessage(obtain);
							ise.printStackTrace();
							return;
						}
						statusCode = response.getStatusLine().getStatusCode();

						if (statusCode >= 400 && statusCode <= 499) {
							obtain.what = URL_ERROR;
							mHandler.sendMessage(obtain);
						} else if (statusCode >= 500 && statusCode <= 599) {
							obtain.what = SERVER_ERROR;
							mHandler.sendMessage(obtain);
						} else {
							obtain.what = URL_SUCCESS;
							mHandler.sendMessageDelayed(obtain, 3000);

						}
					}
				});
			} catch (IllegalArgumentException iae) {
				view.loadUrl(url);
				return;
			}
		} else {
			UIUtils.showToast(UIUtils.getContext(), "请检查您的网络");
			pbProgress.setVisibility(View.GONE);
			ll_flush.setVisibility(View.VISIBLE);
			return;
		}

	}

	private LinearLayout ll_flush;
	private ProgressBar pbProgress;
	private ImageView iv_fbt_backicon;

	public MyWebViewClient(LinearLayout ll_flush, ProgressBar pbProgress,
			ImageView iv_fbt_backicon) {
		this.ll_flush = ll_flush;
		this.pbProgress = pbProgress;
		this.iv_fbt_backicon = iv_fbt_backicon;
	}

	private boolean firstTime = true;

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// System.out.println("WVURL==" + url);
		if (url.contains("weixin:")) {
			try {
				MyApplication.getMainActivity().startActivity(
						new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				return true;
			} catch (ActivityNotFoundException e) {
				UIUtils.showToast(UIUtils.getContext(), "亲,还没安装微信!!");
				e.printStackTrace();
				return true;
			}

		} else {
			// view.loadUrl(url);// 使用当前WebView处理跳转
			getNetworkState(view, url, firstTime, false);
		}
		return true;// true表示此事件在此处被处理，不需要再广播
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		// 有页面跳转时被回调
		pbProgress.setVisibility(View.VISIBLE);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		// 页面跳转结束后被回调
		pbProgress.setVisibility(View.GONE);
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		// 出错
		UIUtils.showToast(UIUtils.getContext(), "请检查您的网络");
		pbProgress.setVisibility(View.GONE);
		ll_flush.setVisibility(View.VISIBLE);

	}

}
