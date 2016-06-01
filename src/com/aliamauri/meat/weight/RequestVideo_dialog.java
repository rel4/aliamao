package com.aliamauri.meat.weight;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.DoyenBean;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.MessageUtils;
import com.aliamauri.meat.utils.UIUtils;

public class RequestVideo_dialog implements OnClickListener {

	private Hide_dialog mDialog;
	private Activity mActivity;
	private String mHXId;

	public RequestVideo_dialog(Activity a, String tag) {
		this.mActivity = a;
		this.mHXId = tag;
		mDialog = new Hide_dialog() {

			@Override
			public void initView(View view) {
				setData(view);
			}

			@Override
			public int setDialogView() {
				return R.layout.request_video_dialog;
			}

		};
		mDialog.show(a.getFragmentManager(), tag);
	}

	protected void setData(View view) {
		if (view == null) {
			return;
		}
		$(view, R.id.btn_yaoqing).setOnClickListener(this);
		$(view, R.id.btn_tuichu).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_yaoqing:
			// requestNet();
			MessageUtils.sendMessage(mHXId,
					"您的好友向你发出邀请:共享视界，推荐精彩，将您看过，看到，拍摄的优质视频发布出来吧！");
			if (mDialog.isVisible()) {
				mDialog.dismiss();
			}
			mActivity.finish();
			break;
		case R.id.btn_tuichu:
			if (mDialog.isVisible()) {
				mDialog.dismiss();
			}
			mActivity.finish();
			break;

		}
	}

	/**
	 * 给邀请好友发邀请发视频请求
	 */
	private void requestNet() {
		new HttpHelp().sendGet(NetworkConfig.getInviteUserGo(mHXId),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || bean.status == null
								|| bean.msg == null) {
							return;
						}
						if ("1".equals(bean.status)) {
							if (mDialog.isVisible()) {
								mDialog.dismiss();
							}
							mActivity.finish();
						} else {
							UIUtils.showToast(mActivity, bean.msg);
						}
					}
				});

	}

	@SuppressWarnings("unchecked")
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

}
