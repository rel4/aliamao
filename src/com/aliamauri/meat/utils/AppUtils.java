package com.aliamauri.meat.utils;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.aliamauri.meat.Manager.DLAppManager;
import com.aliamauri.meat.app.APPManager;
import com.aliamauri.meat.db.dlapp.FindInfo;
import com.aliamauri.meat.global.MyApplication;
import com.limaoso.phonevideo.p2p.P2PManager;

public class AppUtils {
	/**
	 * 安装APK文件
	 */
	public static void installApk(FindInfo info) {
		if (info == null || info.getLocalurl() == null) {
			DLAppManager.getInstance().getFindInfo().add(info);
			APPManager.getInstance(UIUtils.getContext()).startDLApp();
			return;
		}
		final File apkfile = new File(info.getLocalurl());
		if (!apkfile.exists()) {
			DLAppManager.getInstance().getFindInfo().add(info);
			APPManager.getInstance(UIUtils.getContext()).startDLApp();
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		MyApplication.getMainActivity().startActivity(i);
		// 杀死进程
		// android.os.Process.killProcess(android.os.Process.myPid());

	}

	public static void startAPP(FindInfo info) {
		if (info == null || info.getApppackage() == null
				|| "".equals(info.getApppackage())) {
			installApk(info);
			return;
		}
		try {
			PackageManager packageManager = MyApplication.getMainActivity()
					.getPackageManager();
			Intent intent = packageManager.getLaunchIntentForPackage(info
					.getApppackage());
			MyApplication.getMainActivity().startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			installApk(info);
			return;
		}
	}

	// 判断有没有想要的数据
	public static int IsDownload(String appid, List<FindInfo> listInfo) {
		int position = -1;
		if (appid == null || "".equals(appid) || listInfo == null) {
			return position;
		}
		for (int i = 0; i < listInfo.size(); i++) {
			if (appid.equals(listInfo.get(i).getAppid())) {
				return i;
			}
		}
		return position;
	}
}
