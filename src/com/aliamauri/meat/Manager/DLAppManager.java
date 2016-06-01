package com.aliamauri.meat.Manager;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.aliamauri.meat.activity.find_activity.TjyyActivity;
import com.aliamauri.meat.app.APPManager;
import com.aliamauri.meat.db.dlapp.FindDao;
import com.aliamauri.meat.db.dlapp.FindInfo;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.LoadRequestCallBack;
import com.aliamauri.meat.utils.AppUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.limaoso.phonevideo.p2p.P2PManager;

public class DLAppManager {
	private static DLAppManager dLAppManager;
	private List<FindInfo> findInfo;
	private Handler appHandler;
	private boolean startDL = false;// 判断有没有开始下载
	private String Appid;// 记录当前的appid和下载的appid是否一样，如果一样则发送下载进度
	private TjyyActivity tjyy;

	// private FindPager findPager;

	private DLAppManager() {
		findInfo = new ArrayList<FindInfo>();
	}

	public static synchronized DLAppManager getInstance() {
		if (dLAppManager == null) {
			dLAppManager = new DLAppManager();
		}
		return dLAppManager;
	}

	// 添加info
	public void addFindInfo(FindInfo info) {
		for (FindInfo finfo : getFindInfo()) {
			if (info.getAppid().equals(finfo.getAppid())) {
				return;
			}
		}
		getFindInfo().add(info);
		if (!startDL) {
			startDL = true;
			APPManager.getInstance(UIUtils.getContext()).startDLApp();
		}
	}

	private HttpHelp httpHelp;
	private Message obtain;
	private FindDao findDao;

	// 下载方法
	public void DLapp(final FindInfo info) {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		if (findDao == null) {
			findDao = new FindDao();
		}
		httpHelp.downLoad(info.getDownloadurl(), info.getLocalurl(),
				new LoadRequestCallBack() {
					@Override
					public void onSucceed(ResponseInfo t) {
						findDao.UpdateState(DLAppManager.getInstance()
								.getFindInfo().get(0).getAppid(), 2);
						DLAppManager.getInstance().getFindInfo().remove(0);
						AppUtils.installApk(info);
						if (DLAppManager.getInstance().getFindInfo().size() > 0) {
							DLAppManager.getInstance().getFindInfo().get(0);
						} else {
							DLAppManager.getInstance().setStartDL(false);
						}
						if (getTjyy() != null) {
							getTjyy().initDB();
						}

					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						if (DLAppManager.getInstance().getAppHandler() != null
								&& DLAppManager.getInstance().getAppid() != null
								&& DLAppManager
										.getInstance()
										.getAppid()
										.equals(DLAppManager.getInstance()
												.getFindInfo().get(0)
												.getAppid())) {
							obtain = Message.obtain();
							obtain.what = 1;
							if (current / total == 1) {
								obtain.arg1 = 100;
							} else {
								obtain.arg1 = (int) ((current * 100) / total);
							}
							DLAppManager.getInstance().getAppHandler()
									.sendMessage(obtain);
						}
					}

				});
	}

	public List<FindInfo> getFindInfo() {
		return findInfo;
	}

	public void setFindInfo(List<FindInfo> findInfo) {
		this.findInfo = findInfo;
	}

	public Handler getAppHandler() {
		return appHandler;
	}

	public void setAppHandler(Handler appHandler) {
		this.appHandler = appHandler;
	}

	public boolean isStartDL() {
		return startDL;
	}

	public void setStartDL(boolean startDL) {
		this.startDL = startDL;
	}

	public String getAppid() {
		return Appid;
	}

	public void setAppid(String appid) {
		Appid = appid;
	}

	public TjyyActivity getTjyy() {
		return tjyy;
	}

	public void setTjyy(TjyyActivity tjyy) {
		this.tjyy = tjyy;
	}

}
