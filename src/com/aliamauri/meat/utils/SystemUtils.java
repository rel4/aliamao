package com.aliamauri.meat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.WindowManager;

public class SystemUtils {

	public static final int V2_2 = 8;
	public static final int V2_3 = 9;
	public static final int V2_3_3 = 10;
	public static final int V3_0 = 11;
	public static final int V3_1 = 12;
	public static final int V3_2 = 13;
	public static final int V4_0 = 14;
	public static final int V4_0_3 = 15;
	public static final int V4_1 = 16;
	public static final int V4_2 = 17;
	public static final int V4_3 = 18;
	public static final int V4_4 = 19;
	private static WindowManager mWM;

	/**
	 * 
	 * @Description: 检测当前的版本信息
	 * @param
	 * @return int
	 * @throws
	 */
	public static int getSystemVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 得到屏幕宽
	 * 
	 * @return
	 */
	public static int getScreenWidth() {
		if (mWM == null) {

			mWM = (WindowManager) UIUtils.getContext().getSystemService(
					Context.WINDOW_SERVICE);
		}
		return mWM.getDefaultDisplay().getWidth();
	}

	/**
	 * 得到屏幕高
	 * 
	 * @return
	 */
	public static int getScreenHeight() {
		if (mWM == null) {

			mWM = (WindowManager) UIUtils.getContext().getSystemService(
					Context.WINDOW_SERVICE);
		}
		return mWM.getDefaultDisplay().getHeight();
	}
	
	/**
	 * 将时间格式，转换为时间戳
	 * @param createtime 
	 * @return
	 */
	public static long getTime(String createtime) {
		long time = 0;
		try {
			time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(createtime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
	
}