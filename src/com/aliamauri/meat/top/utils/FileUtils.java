package com.aliamauri.meat.top.utils;

import java.io.File;

import android.os.Environment;

import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.utils.UIUtils;

public class FileUtils {

	private static String CACHE = "cache";
	private static String ICON = "icon";
	private static String DOWNLOAD = "download";
	private static String ROOT_DIR =GlobalConstant.ROOT_DIR_NAME;

	public static String getDownloadDir() {
		return getDir(DOWNLOAD); // 缓存json
	}

	public static String getCacheDir() {
		return getDir(CACHE); // 缓存json
	}

	public static String getIconDir() { // /mnt/sdcard/GooglePlay/icon
		return getDir(ICON); // 缓存图片
	}

	private static String getDir(String string) {
		String path = null;
		StringBuilder sb = new StringBuilder();
		if (isSDAvaiable()) {

			// /mnt/sdcard
			path = Environment.getExternalStorageDirectory().getPath();
			sb.append(path);
			sb.append(File.separator);// /mnt/sdcard/
			sb.append(ROOT_DIR);// /mnt/sdcard/GooglePlay
			sb.append(File.separator);// /mnt/sdcard/GooglePlay/
			sb.append(string);// /mnt/sdcard/GooglePlay/cache

		} else {
			// /data/data/包名/cache
			String cacheDir = UIUtils.getContext().getCacheDir().getPath();
			sb.append(cacheDir);
			sb.append(File.separator);// /data/data/包名/cache/
			sb.append(string);

		}
		path = sb.toString();
		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();// 创建文件夹
		}

		return path;
	}

	/**
	 * 判断sd卡是否可以用
	 * 
	 * @return
	 */
	private static boolean isSDAvaiable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);

	}
}
