package com.aliamauri.meat.weight;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
/**
 * 动态详情页  匿名/实名dialog
 * @author limaokeji-windosc
 *
 */
public abstract class MyDialog implements OnClickListener {
	private int position;     //当前条目的位置
	private Activity mActivity;
	private Dialog dialog;
	private RelativeLayout layout;
	
	public MyDialog(Activity activity,int position, RelativeLayout layout){
		this.mActivity = activity;
		this.position = position;
		this.layout = layout;
		init();
	}
	
	private void init() {
	
		
		dialog = new AlertDialog.Builder(mActivity).create();
		dialog.show();
		
		dialog.getWindow().setContentView(layout);
		
		
		//dialog确定按钮
		$(layout,R.id.btn_dialog_ok).setOnClickListener(this);
		//dialog取消按钮
		$(layout,R.id.btn_dialog_no).setOnClickListener(this);
		((TextView)$(layout,R.id.tv_dialog_nam_e)).setText(getTitleText());
	}
	/**
	 * 修改标题名称
	 * @return
	 */
	public abstract String getTitleText();

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dialog_no:
			dialog.dismiss();
			break;
		case R.id.btn_dialog_ok:
			setButton_ok(position);
			dialog.dismiss();
			break;

		}
		
	}
/**
 * 点击确定按钮所做的操作
 */
	public abstract void setButton_ok(int position);
}
