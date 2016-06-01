package com.aliamauri.meat.global;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.listener.ConnectionChangeListener;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.NetWorkUtil;
import com.aliamauri.meat.utils.PrefUtils;
import com.easemob.EMCallBack;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.limaoso.phonevideo.p2p.P2PConfig;

/**
 * 获取全局application对象 在里面获取相应的对象，对外提供 处理全局未扑获的异常
 * 
 * @author liang
 * 
 */
public class MyApplication extends Application {

	private static Context appContext; // context对象
	private static MainActivity mainActivity;
	private static Handler handler;

	public static boolean ISKEYBORD_OPEN = false;// 判断当前是否打开了键盘
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	private static String data;
	private static MyApplication instance;
	private static int mainThreadId;
	private static ArrayList<String> albumLists; // 发布页面的相册集合
	public static Map<String, String> UpDowns; // 存储资源库点赞的条目id

	// login user name
	public final String PREF_USERNAME = "username";
	public static MySDKHelper hxSDKHelper = new MySDKHelper();

	public static String getData() {
		return data;
	}

	public static ArrayList<String> getAlbumLists() {
		return albumLists;

	}

	public static void setAlbumLists(ArrayList<String> album) {
		albumLists = album;

	}

	public static void setData(String data) {
		MyApplication.data = data;
	}

	public static MainActivity getMainActivity() {
		return mainActivity;
	}

	public static void setMainActivity(MainActivity mainActivity) {
		MyApplication.mainActivity = mainActivity;
	}

	public static int getMainThreadId() {
		return mainThreadId;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		appContext = this;
		// Handler对象
		handler = new Handler();
		// 主线程id,获取当前方法运行线程id,此方法运行在主线程中,所以获取的是主线程id
		mainThreadId = android.os.Process.myTid();

		// 本地联系人超时
		if (NetWorkUtil.isWifiAvailable(this)) {
			long contactTimeOut = PrefUtils.getLong(
					GlobalConstant.CONTACT_TIME_OUT, 0);
			if (contactTimeOut > 24 * 60 * 60 * 1000) {
				SDKHelper.getInstance().reset();
				PrefUtils.setLong(this, GlobalConstant.CONTACT_TIME_OUT,
						System.currentTimeMillis());
			}
		}
		// 设置未扑获异常的处理器

		if (!LogUtil.getDeBugState()) {

			Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler()); // 设置未扑获的异常
		}
		init();
		P2PConfig.getInstance().init(getApplicationContext());
	}

	/**
	 * 初始化
	 */
	private void init() {
		hxSDKHelper.onInit(appContext);
		ConnectionChangeListener.getInstance().onInit(appContext);
		// 将“12345678”替换成您申请的APPID，申请地址：http://open.voicecloud.cn
		SpeechUtility.createUtility(appContext, SpeechConstant.APPID
				+ "=566a7830");

	}

	public static Context getContext() {
		return appContext;
	}

	public static Handler getHandler() {
		return handler;
	}

	/**
	 * 自定义全局未扑获异常的处理器
	 * 
	 * @author liang
	 * 
	 */
	class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {
		/**
		 * 一旦发生未扑获的异常，产生崩溃就会回调此方法
		 */
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			ex.printStackTrace();
			// 结束线程
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	public static MyApplication getInstance() {
		// TODO Auto-generated method stub
		return instance;
	}

	public String getVersionName() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 * 
	 * @param user
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 * 
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final boolean isGCM, final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(isGCM, emCallBack);
	}
}
