package com.aliamauri.meat.weight;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aliamauri.meat.R;

public abstract class DeleteFriend_dialog implements OnClickListener {

	private Hide_dialog mDialog;

	public DeleteFriend_dialog(Activity a, String tag) {
		mDialog = new Hide_dialog() {

			@Override
			public void initView(View view) {
				setData(view);
			}

			@Override
			public int setDialogView() {
				return R.layout.net_work_dialog;
			}

		};
		mDialog.show(a.getFragmentManager(), tag);
	}

	protected void setData(View view) {
		if (view == null) {
			return;
		}
		TextView tv = $(view, R.id.tv_dialog_nam_e);
		tv.setText(getTitleText());
		$(view, R.id.btn_dialog_ok).setOnClickListener(this);
		$(view, R.id.btn_dialog_no).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dialog_ok:
			setButton_ok();
			mDialog.dismiss();
			break;
		case R.id.btn_dialog_no:
			mDialog.dismiss();
			break;
	
		}
	}
	/**
	 * 设置确定按钮
	 */
	public abstract void setButton_ok();

	/**
	 * 获取标题
	 * 
	 * @return
	 */
	public abstract String getTitleText();

	@SuppressWarnings("unchecked")
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

}
