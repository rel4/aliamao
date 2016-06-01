package com.aliamauri.meat.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.aliamauri.meat.R;

/**
 * 动态详情页 照片/视频选择对话框
 * 
 * @author limaokeji-windosc
 * 
 */
public abstract class MyPhoneOrVideoDialog implements OnClickListener {
	private Activity mActivity;
	private Dialog dialog;
	private LinearLayout layout;

	public MyPhoneOrVideoDialog(Activity activity, LinearLayout layout) {
		this.mActivity = activity;
		this.layout = layout;
		init();
	}

	private void init() {

		dialog = new AlertDialog.Builder(mActivity).create();
		dialog.show();
		dialog.getWindow().setContentView(layout);

		// dialog拍照按钮
		$(layout, R.id.tv_dialog_broadcast_camear).setOnClickListener(this);

		// diaLog相册按钮
		$(layout, R.id.tv_dialog_broadcast_album).setOnClickListener(this);
		// dialog取消按钮
		$(layout, R.id.tv_dialog_broadcast_cancel).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_dialog_broadcast_camear:
			setButton_camear();
			dialog.dismiss();
			break;
		case R.id.tv_dialog_broadcast_album:
			setButton_album();
			dialog.dismiss();
			break;
		case R.id.tv_dialog_broadcast_cancel:
			dialog.dismiss();
			break;

		}

	}

	/**
	 * 点击照相机按钮所做的操作
	 */
	public abstract void setButton_camear();

	/**
	 * 点击相册按钮所做的操作
	 */
	public abstract void setButton_album();

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

}
