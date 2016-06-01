package com.aliamauri.meat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.aliamauri.meat.global.GlobalConstant;

/**
 * SharePreference封装
 * 
 * @author Kevin
 * 
 */
public class PrefUtils {
	public static final String PREF_NAME = "config";

	public static boolean getBoolean(Context ctx, String key,
			boolean defaultValue) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		SharedPreferences sp = UIUtils.getContext().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}

	public static int getInt(String key, int defaultValue) {
		SharedPreferences sp = UIUtils.getContext().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}

	public static void setInt(String key, int value) {
		SharedPreferences sp = UIUtils.getContext().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(key, value).commit();
	}

	public static void setInt(Context ctx, String key, int value) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putInt(key, value).commit();
	}

	public static void setBoolean(Context ctx, String key, boolean value) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}

	public static void setBoolean(String key, boolean value) {
		SharedPreferences sp = UIUtils.getContext().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}

	public static String getString(Context ctx, String key, String defaultValue) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}

	public static String getString(String key, String defaultValue) {
		SharedPreferences sp = UIUtils.getContext().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}

	public static long getLong(String key, long defaultValue) {
		SharedPreferences sp = UIUtils.getContext().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		return sp.getLong(key, defaultValue);
	}

	public static void setLong(Context ctx, String key, long value) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putLong(key, value).commit();
	}

	public static void setString(Context ctx, String key, String value) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}

	public static void setString(String key, String value) {
		SharedPreferences sp = UIUtils.getContext().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}

	public static void clearSP(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
		initSP(ctx);
	}

	/**
	 * 保存一些必要的sp数据
	 */
	public static void initSP(Context ctx) {
		PrefUtils.setBoolean(GlobalConstant.NOT_NEWHAND_CHANNEL, true);
		PrefUtils.setBoolean(GlobalConstant.NOT_NEWHAND_GOTOSETTING, true);
		PrefUtils.setBoolean(GlobalConstant.NOT_NEWHAND_GOTOTAKEVIDEO, true);
		PrefUtils.setBoolean(GlobalConstant.NOT_NEWHAND_PLAY, true);
	}

	/**
	 * 保存搜索关键字
	 */
	public static void saveHotKey(String searchkey) {
		String key = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.SERACH_CACHE_KEY, "");
		if (key == "") {
			PrefUtils.setString(UIUtils.getContext(),
					GlobalConstant.SERACH_CACHE_KEY, searchkey);
		} else {
			if (key.contains(searchkey + GlobalConstant.FLAG_APP_SPLIT)) {
				key = key
						.replace(searchkey + GlobalConstant.FLAG_APP_SPLIT, "");
			}
			if (key.contains(GlobalConstant.FLAG_APP_SPLIT + searchkey)) {
				key = key
						.replace(GlobalConstant.FLAG_APP_SPLIT + searchkey, "");
			}
			if (key.contains(searchkey)) {
				return;
			}
			String[] split = key.split(GlobalConstant.FLAG_APP_SPLIT);
			if (split.length == 6) {
				key = key.substring(0,
						key.lastIndexOf(GlobalConstant.FLAG_APP_SPLIT));
			}

			key = searchkey + GlobalConstant.FLAG_APP_SPLIT + key;
			PrefUtils.setString(UIUtils.getContext(),
					GlobalConstant.SERACH_CACHE_KEY, key.trim());
		}

	}

}
