package com.aliamauri.meat.listener;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.aliamauri.meat.utils.UIUtils;
/**
 * 网络连接
 * @author admin
 *
 */
public class ConnectionChangeListener {
	
	
	private static List<OnConnectionChangeListener> changeListeners ;

	public void onInit(Context context ){
		UIUtils.getContext().registerReceiver(new ConnectionChangeReceiver(),new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));	
		if (changeListeners==null) {
			changeListeners = new ArrayList<ConnectionChangeListener.OnConnectionChangeListener>();

		}
	}
	private final static ConnectionChangeListener instance = new ConnectionChangeListener();
	public  static ConnectionChangeListener getInstance(){
		return instance;
	}

	/**
	 * 添加网络改变监听
	 */
	public  void addConnectionChangeListener(
			OnConnectionChangeListener mConnectionChangeListener) {
		if (mConnectionChangeListener == null) {
			return;
		}
		if (!changeListeners.contains(mConnectionChangeListener)) {
			changeListeners.add(mConnectionChangeListener);
		}
	}
	/**
	 * 移除网络改变监听
	 * @param mConnectionChangeListener
	 */
	public static void removeConnectionChangeListener(OnConnectionChangeListener mConnectionChangeListener){
		if (changeListeners.contains(mConnectionChangeListener)) {
			changeListeners.remove(mConnectionChangeListener);
		}
	}
	public OnConnectionChangeListener mConnectionChangeListener;

	/**
	 * 注册网络变化的监听
	 * 
	 * @param mConnectionChangeListener
	 */
	public void setOnConnectionChangeListener(
			OnConnectionChangeListener mConnectionChangeListener) {
		this.mConnectionChangeListener = mConnectionChangeListener;
	}

	/**
	 * 网络监听
	 * 
	 * @author jjm
	 * 
	 */
	public interface OnConnectionChangeListener {
		void onConnectionChange(PhoneNetType type);
	}

	public class ConnectionChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			PhoneNetType netType = null;
			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
				netType = PhoneNetType.NOT_NET;
			} else if (mobNetInfo.isConnected()) {
				netType = PhoneNetType.PHONE_NET;

			} else if (wifiNetInfo.isConnected()) {
				netType = PhoneNetType.WIFI_NET;

			}
			for (OnConnectionChangeListener mConnectionChangeListener : changeListeners) {
				if (mConnectionChangeListener != null && netType != null) {
					mConnectionChangeListener.onConnectionChange(netType);
				}
			}

		}
	}

	/**
	 * 网络类型
	 * 
	 * @author jjm
	 * 
	 */
	public enum PhoneNetType {
		WIFI_NET, PHONE_NET, NOT_NET
	}
}