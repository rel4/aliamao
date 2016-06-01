package com.aliamauri.meat.top.manager;

import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.UIUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class HttpHelper {
	private static String TAG = "HttpHelper";
	private static HttpUtils httpUtils;

	// public static final String SERVER_URL = "http://127.0.0.1:8090/";

	public static String sendGet(String url) {
		if (httpUtils == null) {
			httpUtils = new HttpUtils();
		}
		try {
			LogUtil.e(TAG, "url=" + url);
			ResponseStream sendSync = httpUtils.sendSync(HttpMethod.GET, url);

			return sendSync.readString();
		} catch (HttpException e) {
			UIUtils.runInMainThread(new Runnable() {

				@Override
				public void run() {
					UIUtils.showToast(UIUtils.getContext(), "网络有问题，请检查！");
				}
			});
				LogUtil.e(TAG, "访问失败地址： " + url);
				LogUtil.e(TAG, "ExceptionCode： " + e.getExceptionCode());
				return null;
		} catch (Exception e) {
			return null;

		}
	}
}
