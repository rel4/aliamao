package com.aliamauri.meat.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;

import com.aliamauri.meat.global.GlobalConstant;

public class WebViewUtils {

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	public static void a(WebView wv_webpage) {
		WebSettings webSettings = wv_webpage.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setDefaultTextEncodingName("UTF-8"); // 设置默认的显示编码
		if (Build.VERSION.SDK_INT >= 11)
			webSettings.setDisplayZoomControls(false);
		webSettings.setSupportZoom(false);
		String ua = wv_webpage.getSettings().getUserAgentString();

		String str = "Mozilla/5.0 (Linux; U; Android "
				+ Build.VERSION.RELEASE
				+ "; en-us; "
				+ Build.MODEL
				+ " Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
		webSettings.setUserAgentString(ua + " " + str);
		// webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		setChache(webSettings);
		// webSettings.setDefaultTextEncodingName("utf-8");
		wv_webpage.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReachedMaxAppCacheSize(long spaceNeeded,
					long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
				quotaUpdater.updateQuota(spaceNeeded * 2);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		});
	}

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private static void setChache(WebSettings webSettings) {
		// 设置 缓存模式
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		// 开启 DOM storage API 功能
		webSettings.setDomStorageEnabled(true);
		// 开启 database storage API 功能
		webSettings.setDatabaseEnabled(true);
		// 开启 Application Caches 功能
		webSettings.setAppCacheEnabled(true);

		String cacheDirPath = GlobalConstant.HEAD_ICON_SAVEPATH + "chache";
		// 设置数据库缓存路径
		webSettings.setDatabasePath(cacheDirPath); // API 19
		// deprecated
		// 设置Application caches缓存目录
		webSettings.setAppCachePath(cacheDirPath);
		webSettings.setAppCacheMaxSize(1024 * 1024 * 8);//
		// 设置缓冲大小，8M
		webSettings.setAllowFileAccess(true);
	}

}
