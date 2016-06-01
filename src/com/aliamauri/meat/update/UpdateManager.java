package com.aliamauri.meat.update;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.VersionInfo;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.LoadRequestCallBack;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.NetWorkUtil;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

public class UpdateManager implements android.view.View.OnClickListener {
	private int notification_id = 123486;
	private Context mContext;
	private TextView umeng_update_content;
	private Dialog mNoticeDialog;
	private HttpHelp httpHelp;
	private NotificationManager nm;
	private Notification notification;
	

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 检测软件更新
	 */
	public void checkUpdate() {
		if (!NetWorkUtil.isWifiAvailable(mContext)) {// wifi情况下下载更新包
			return;
		}
		//本都存储的升级号与当前软件的版本号3种情况：
			//a:大于,说明本地已有下载好的升级包，用户没有安装
			//b:等于,说明当前版本相同，需要进行网络检查是否有更新版本
			//c:小于，说明当前存储错误，需要进行网络检查是否有更新版本
		int currentCode = PrefUtils.getInt(GlobalConstant.UPLOADVERSIONCODE,0);
		if(currentCode>getVersionCode(mContext)){  //情况a
			File apk_file = new File(GlobalConstant.SAVE_APK_PATH);
			if(apk_file.exists()){//当前路径下有升级版本包给出升级弹框
				// 显示提示对话框
				showNoticeDialog(null);
			}else{//当前路径下没有升级升级包，重新网络下载
				checkUpdate_net();
			}
		}else{ //情况b,c;
			checkUpdate_net();
		}
	}

	private void checkUpdate_net() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		httpHelp.sendGet(NetworkConfig.getgetVersionInfo(
				GlobalConstant.APP_CHANNEL, getVersionCode(mContext)),
				VersionInfo.class, new MyRequestCallBack<VersionInfo>() {
					@Override
					public void onSucceed(VersionInfo bean) {
						if (bean == null || bean.cont==null || bean.cont.vcode == null) {
							return;
						}
						int vcodeInt =0;
						try {
							vcodeInt = Integer.parseInt(bean.cont.vcode);
						} catch (Exception e) {
							vcodeInt = 0;
						}
						if (isUpdate(vcodeInt)) {
							PrefUtils.setString(GlobalConstant.UPLOAD_DESC, bean.cont.desc);
							PrefUtils.setString(GlobalConstant.UPLOAD_VNAME, bean.cont.vname);
							PrefUtils.setString(GlobalConstant.UPLOAD_FSIZE, bean.cont.fsize);
							PrefUtils.setString(GlobalConstant.UPLOAD_PDATE, bean.cont.pdate);
							downloadApk(bean);
							
						}
					}

				});
	}


	/**
	 * 检查软件是否有更新版本
	 * 
	 * @param versionCode2
	 * 
	 * @return
	 */
	private boolean isUpdate(int version) {
		if (getVersionCode(mContext) >= version) {
			return false;
		}
		return true;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 1;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					UIUtils.getContext().getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 获取版本名称
	 * 
	 * @param context
	 * @return
	 */
	private String getVersionName(Context context) {
		String versionName = "";
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionName = context.getPackageManager().getPackageInfo(
					UIUtils.getContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 显示软件更新对话框
	 * @param bean 
	 */
	private void showNoticeDialog(VersionInfo bean) {
		if(mNoticeDialog !=null){
			return;
		}
		// 构造对话框
		AlertDialog.Builder builder = new Builder(mContext);
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.umeng_update_dialog, null);
		mNoticeDialog = builder.create();
		mNoticeDialog.show();
		mNoticeDialog.getWindow().setContentView(v);
		// v.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View arg0, MotionEvent arg1) {
		// String forcedUpdating = mVersionInfo.cont.forcedUpdating;
		// // 强制更新显示
		// if ("1".equals(forcedUpdating)) {
		// MyApplication.getMainActivity().finish();
		// }
		// return false;
		// }
		// });
		initDialogView(v,bean);

	}

	/**
	 * 初始化dialog布局
	 * 
	 * @param v
	 * @param bean 
	 */
	private void initDialogView(View v, VersionInfo bean) {
		umeng_update_content = (TextView) v
				.findViewById(R.id.umeng_update_content);

		v.findViewById(R.id.umeng_update_id_cancel).setOnClickListener(this);
		v.findViewById(R.id.umeng_update_id_ok).setOnClickListener(this);
		
		initVersionInfo(bean);
	}

	private void initVersionInfo(VersionInfo bean) {
		String desc = null;
		String vname = null;
		String fsize = null;
		String pdate = null;
		if (bean == null) {
			desc = PrefUtils.getString(GlobalConstant.UPLOAD_DESC, "正在获取……");
			vname = PrefUtils.getString(GlobalConstant.UPLOAD_VNAME, "正在获取……");
			fsize = PrefUtils.getString(GlobalConstant.UPLOAD_FSIZE, "正在获取……");
			pdate = PrefUtils.getString(GlobalConstant.UPLOAD_PDATE, "正在获取……");
		}else{
			desc = bean.cont.desc;
			vname = bean.cont.vname;
			fsize = bean.cont.fsize;
			pdate = bean.cont.pdate;
		}
		
		if (!"".equals(desc)) {
			desc = "\n" + desc;
		}
		if (!"".equals(pdate)) {
			pdate = "\n更新时间  ：" + pdate;
		}
		if (!"".equals(fsize)) {
			fsize = "\n文件大小  ： " + fsize;
		}
		if (!"".equals(vname)) {
			vname = "\n版本号  ： " + vname;
		}
		umeng_update_content.setText(vname + fsize + pdate + desc);
	}

	/**
	 * 下载apk文件
	 * @param bean 
	 */
	private void downloadApk(final VersionInfo bean) {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		File apkfile = new File(GlobalConstant.SAVE_APK_PATH);
		if (apkfile.exists()) {
			apkfile.delete();
		}
		httpHelp.downLoad(bean.cont.durl, GlobalConstant.SAVE_APK_PATH,
				new LoadRequestCallBack() {

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						if (notification != null) {
							notification.contentView.setProgressBar(R.id.pb,
									(int) total, (int) current, false);
							// 设置当前值为count
							showNotification();// 这里是更新notification,就是更新进度条
						} else {
							showNotifi();
						}
					}

					@Override
					public void onSucceed(ResponseInfo t) {
						if (nm != null) {
							nm.cancel(notification_id);
						}
						//本地记录当前apk的版本号
						if ( bean != null&& bean.cont != null&& bean.cont.vcode != null) {
							int code =0;
							try{
								code = Integer.parseInt(bean.cont.vcode);
							}catch(Exception e){
								code =0;
							}
							PrefUtils.setInt(mContext,GlobalConstant.UPLOADVERSIONCODE,code);
						}
						// 显示提示对话框
						showNoticeDialog(bean);
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						UIUtils.showToast(mContext, "更新包下载失败！");

					}
				});
	}

	private void showNotifi() {
		String appName = UIUtils.getContext().getResources()
				.getString(R.string.app_name)
				+ "正在升级";
		nm = (NotificationManager) UIUtils.getContext().getSystemService(
				Context.NOTIFICATION_SERVICE);
		notification = new Notification(R.drawable.ic_launch, appName,
				System.currentTimeMillis());
		notification.contentView = new RemoteViews(UIUtils.getContext()
				.getPackageName(), R.layout.up_notification);
		notification.flags = Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_AUTO_CANCEL;
		notification.icon = android.R.drawable.stat_sys_download;
		// 使用notification.xml文件作VIEW
		// 设置进度条，最大值 为100,当前值为0，最后一个参数为true时显示条纹
		// （就是在Android Market下载软件，点击下载但还没获取到目标大小时的状态）
		// Intent notificationIntent = new Intent(UIUtils.getContext(),
		// MainActivity.class);
		// PendingIntent contentIntent =
		// PendingIntent.getActivity(UIUtils.getContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		// notification.contentIntent = contentIntent;
	}

	public void showNotification() {
		nm.notify(notification_id, notification);
	}

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		final File apkfile = new File(GlobalConstant.SAVE_APK_PATH);
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
		MyApplication.getMainActivity().finish();
		android.os.Process.killProcess(android.os.Process.myPid());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.umeng_update_id_ok:
			if (mNoticeDialog != null) {
				mNoticeDialog.dismiss();
			}
			installApk();
			break;
		case R.id.umeng_update_id_cancel:
			if (mNoticeDialog != null) {
				mNoticeDialog.dismiss();
			}
			break;
		}

	}
}
