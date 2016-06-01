package com.aliamauri.meat.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.aliamauri.meat.Manager.DLAppManager;
import com.aliamauri.meat.db.dlapp.FindInfo;
import com.aliamauri.meat.utils.LogUtil;
import com.limaoso.phonevideo.download.DownServerControl;
import com.limaoso.phonevideo.p2p.P2PManagerThread;
import com.limaoso.phonevideo.p2p.TimerThreadManager;

public class APPService extends Service {

	public  final String TAG = getClass().getSimpleName();

	public class MyBinder extends Binder {
		public Service getService() {
			return APPService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.e(TAG, "onStartCommand开启");
		return super.onStartCommand(intent, flags, startId);
	}

	// 下载app方法
	public void DLapp(FindInfo info) {
		DLAppManager.getInstance().DLapp(info);
	}

}
