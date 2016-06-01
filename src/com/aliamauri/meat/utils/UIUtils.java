package com.aliamauri.meat.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.global.MyApplication;

public class UIUtils {
	private static Toast toast;

	public static void showToast(Context ctx, String text) {
		if (toast == null) {
			toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
		}
		toast.show();
	}

	/**
	 * 将文字转换成全角字符，防止textview排版不一
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static Context getContext() {
		return MyApplication.getContext();
	}

	public static View inflate(int layoutId) {
		return View.inflate(getContext(), layoutId, null);
	}

	public static Handler getHandler() {
		return MyApplication.getHandler();
	}

	// 此代码运行的线程是否为主线程判断(子线程开启网络请求操作)
	public static boolean isRunInMainThread() {
		return android.os.Process.myTid() == getMainThreadId();
	}

	// handler机制
	public static void runInMainThread(Runnable runnable) {
		if (isRunInMainThread()) {
			// 运行在主线程中的任务
			runnable.run();
		} else {
			// 不是运行在主线程中的任务,通过handler机制,将其传递至主线程运行
			getHandler().post(runnable);
		}
	}

	public static int getMainThreadId() {
		return MyApplication.getMainThreadId();
	}

	/**
	 * 移除handler中的线程任务
	 * 
	 * @param runnableTask
	 */
	public static void removeCallBack(Runnable runnableTask) {
		// 移除传递进来任务
		getHandler().removeCallbacks(runnableTask);
	}

	/**
	 * 根据说话的时常来改变语音的条的长度 ()
	 * 
	 * @param tv_dynamic_item_video
	 * @param sc
	 */
	public static void setVideoShape(final TextView video, final String sc,
			Activity a) {
		WindowManager wm = (WindowManager) a
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		int widthPixels = metrics.widthPixels;

		int length = (int) Float.parseFloat(sc);
		if (length >= 0 && length <= 10) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					widthPixels * 150 / 720, LayoutParams.WRAP_CONTENT);
			video.setLayoutParams(params);
		} else if (length >= 11 && length <= 20) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					widthPixels * 200 / 720, LayoutParams.WRAP_CONTENT);
			video.setLayoutParams(params);
		} else if (length >= 21 && length <= 30) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					widthPixels * 300 / 720, LayoutParams.WRAP_CONTENT);
			video.setLayoutParams(params);
		} else if (length >= 31 && length <= 45) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					widthPixels * 400 / 720, LayoutParams.WRAP_CONTENT);
			video.setLayoutParams(params);
		} else {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					widthPixels * 500 / 720, LayoutParams.WRAP_CONTENT);
			video.setLayoutParams(params);
		}
	}

	/**
	 * 根据说话的时常来改变语音的条的长度 (设定padding值)
	 * 
	 * @param tv_dynamic_item_video
	 * @param sc
	 */
	public static void setVideoShape_padding(TextView tv, final String sc) {
		WindowManager ss = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		ss.getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		int left = 20 * width / 720;
		int top = 10 * height / 1280;
		int bot = 10 * height / 1280;

		int length = (int) Float.parseFloat(sc);
		if (length >= 0 && length <= 10) {
			tv.setPadding(left, top, 40 * width / 720, bot);
		} else if (length >= 11 && length <= 20) {
			tv.setPadding(left, top, 100 * width / 720, bot);
		} else if (length >= 21 && length <= 30) {
			tv.setPadding(left, top, 150 * width / 720, bot);
		} else if (length >= 31 && length <= 45) {
			tv.setPadding(left, top, 250 * width / 720, bot);
		} else {
			tv.setPadding(left, top, 300 * width / 720, bot);
		}
	}

	/**
	 * 获取横向间距的适配 以720*1280的手机为适配标准
	 * 
	 * @param mHorizontalSpacing
	 * @return
	 */
	public static int getHorizontalSpacing(int mHorizontalSpacing) {
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		return 720 / metrics.widthPixels * mHorizontalSpacing;
	}

	/**
	 * 获取纵向间距的适配 以720*1280的手机为适配标准
	 * 
	 * @param mHorizontalSpacing
	 * @return
	 */
	public static int getVerticalSpacing(int mVerticalSpacing) {
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		return 1280 / metrics.heightPixels * mVerticalSpacing;
	}

	/**
	 * 开启软件的时候判断网络的状态
	 */
	public static boolean checkNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						NetworkInfo netWorkInfo = info[i];
						if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							return true;
						} else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
