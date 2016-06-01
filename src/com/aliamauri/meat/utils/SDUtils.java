package com.aliamauri.meat.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.aliamauri.meat.global.GlobalConstant;

public class SDUtils {

	/**
	 * 
	 * @return ROM存储路径
	 */
	public static String getInternalMemoryPath() {
		return Environment.getDataDirectory().getPath();
	}

	/**
	 * 
	 * @return 内置sd卡路径
	 */
	public static String getExternalMemoryPath() {
		return Environment.getExternalStorageDirectory().getPath();
		// return "/mnt/sdcard";
	}

	/**
	 * 
	 * @return 外置sd卡路径
	 */
	public static String getSDCard2MemoryPath() {
		return "/mnt/sdcard1";
	}

	/**
	 * 
	 * @param path
	 *            文件路径
	 * @return 文件路径的StatFs对象
	 * @throws Exception
	 *             路径为空或非法异常抛出
	 */
	public static StatFs getStatFs(String path) {
		try {
			return new StatFs(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param stat
	 *            文件StatFs对象
	 * @return 剩余存储空间的MB数
	 * 
	 */
	public static float calculateSizeInMB(StatFs stat) {
		if (stat != null)
			return stat.getAvailableBlocks()
					* (stat.getBlockSize() / (1024f * 1024f));
		return 0.0f;
	}

	/**
	 * 
	 * @return ROM剩余存储空间的MB数
	 */
	public static float getAvailableInternalMemorySize() {

		String path = getInternalMemoryPath();// 获取数据目录
		StatFs stat = getStatFs(path);
		return calculateSizeInMB(stat);
	}

	/**
	 * 
	 * @return 内置SDCard剩余存储空间MB数
	 */
	public static float getAvailableExternalMemorySize() {

		String path = getExternalMemoryPath();// 获取数据目录
		StatFs stat = getStatFs(path);
		return calculateSizeInMB(stat);

	}

	/**
	 * 
	 * @return 外置SDCard剩余存储空间MB数
	 */
	public static float getAvailableSDCard2MemorySize() {

		// String status = Environment.getExternalStorageState();
		// if (status.equals(Environment.MEDIA_MOUNTED)) {
		// }
		String path = getSDCard2MemoryPath(); // 获取数据目录
		StatFs stat = getStatFs(path);
		return calculateSizeInMB(stat);

	}

	public static List<String> getExtSDCardPath() {
		List<String> lResult = new ArrayList<String>();
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("extSdCard")) {
					String[] arr = line.split(" ");
					String path = arr[1];
					File file = new File(path);
					if (file.isDirectory()) {
						lResult.add(path);
					}
				}
			}
			isr.close();
		} catch (Exception e) {
		}
		return lResult;
	}

	/**
	 * 得到SD路径
	 * 
	 * @return
	 */
	public static String getSDPath() {
		String sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在

		// if(Environment.getStorageState(Environment.STORAGE_PATH_SD2).equals(Environment.MEDIA_MOUNTED))
		// {
		// //为true的话，外置sd卡存在
		// }
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory().toString();// 获取跟目录
		} else {
			sdDir = Environment.getDataDirectory().toString();
		}
		return sdDir;

	}

	// public String

	/**
	 * 获取手机自身内存路径
	 * 
	 */
	public static String getPhoneCardPath() {
		return Environment.getDataDirectory().getPath();

	}

	/**
	 * 获取sd卡路径 双sd卡时，根据”设置“里面的数据存储位置选择，获得的是内置sd卡或外置sd卡
	 * 
	 * @return
	 */
	public static String getNormalSDCardPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 获取sd卡路径 双sd卡时，获得的是外置sd卡
	 * 
	 * @return
	 */
	public static String getSDCardPath() {
		String cmd = "cat /proc/mounts";
		Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
		BufferedInputStream in = null;
		BufferedReader inBr = null;
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			in = new BufferedInputStream(p.getInputStream());
			inBr = new BufferedReader(new InputStreamReader(in));

			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				// 获得命令执行后在控制台的输出信息
				Log.i("CommonUtil:getSDCardPath", lineStr);
				if (lineStr.contains("sdcard")
						&& lineStr.contains(".android_secure")) {
					String[] strArray = lineStr.split(" ");
					if (strArray != null && strArray.length >= 5) {
						String result = strArray[1].replace("/.android_secure",
								"");
						return result;
					}
				}
				// 检查命令是否执行失败。
				if (p.waitFor() != 0 && p.exitValue() == 1) {
					// p.exitValue()==0表示正常结束，1：非正常结束
					Log.e("CommonUtil:getSDCardPath", "命令执行失败!");
				}
			}
		} catch (Exception e) {
			Log.e("CommonUtil:getSDCardPath", e.toString());
			// return Environment.getExternalStorageDirectory().getPath();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (inBr != null) {
					inBr.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Environment.getExternalStorageDirectory().getPath();
	}

	// 查看所有的sd路径
	public static String getSDCardPathEx() {
		String mount = new String();
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				if (line.contains("secure"))
					continue;
				if (line.contains("asec"))
					continue;

				if (line.contains("fat")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						mount = mount.concat("*" + columns[1] + "\n");
					}
				} else if (line.contains("fuse")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						mount = mount.concat(columns[1] + "\n");
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mount;
	}

	// 获取当前路径，可用空间
	public static long getAvailableSize(String path) {
		try {
			File base = new File(path);
			StatFs stat = new StatFs(base.getPath());
			long nAvailableCount = stat.getBlockSize()
					* ((long) stat.getAvailableBlocks());
			return nAvailableCount;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取跟路径
	 * 
	 * @return
	 */

	public static String getRootFile() {
		String flagRootPath = "rootPath";
		String rootPath = PrefUtils.getString(UIUtils.getContext(),
				flagRootPath, null);
		if (rootPath != null) {
			File file = new File(rootPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			if (!file.isDirectory()) {
				file.delete();
				file.mkdirs();
			}
			return rootPath;
		}
		String sdPath = getSDPath() + File.separator
				+ GlobalConstant.ROOT_DIR_NAME + File.separator;
		if (sdPath != null) {
			File file = new File(sdPath);
			if (!file.isDirectory()) {
				file.delete();
			}
			if (!file.exists() || !file.isDirectory()) {
				file.mkdirs();
			}
			PrefUtils.setString(UIUtils.getContext(), flagRootPath, file
					.getAbsolutePath().toString());
			return file.getAbsolutePath().toString();
		}
		return null;
	}
}
