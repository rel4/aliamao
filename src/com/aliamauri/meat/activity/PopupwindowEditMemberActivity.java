package com.aliamauri.meat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.umeng.analytics.MobclickAgent;

/*
 * 
 author 院彩华
 弹出性别选择菜单

 */
public class PopupwindowEditMemberActivity extends Activity implements
		OnClickListener {

	private LinearLayout ll_member_edit;
	private TextView tv_memberedit_add;
	private TextView tv_memberedit_del;
	private TextView tv_memberedit_cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popupwindow_member_edit);
		init();// 初始化控件
		setListener();// 设置监听器
	}
	@Override
	protected void onResume() {
		super.onResume();
		  MobclickAgent.onResume(this);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		 MobclickAgent.onPause(this);
	}
	
	private void setListener() {
		ll_member_edit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
						Toast.LENGTH_SHORT).show();
			}
		});
		// 添加按钮监听
		tv_memberedit_add.setOnClickListener(this);
		tv_memberedit_del.setOnClickListener(this);
		tv_memberedit_cancel.setOnClickListener(this);
	}

	private void init() {
		ll_member_edit = (LinearLayout) findViewById(R.id.ll_member_edit);
		tv_memberedit_add = (TextView) findViewById(R.id.tv_memberedit_add);
		tv_memberedit_del = (TextView) findViewById(R.id.tv_memberedit_del);
		tv_memberedit_cancel = (TextView) findViewById(R.id.tv_memberedit_cancel);
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.tv_memberedit_add:
			i = new Intent(PopupwindowEditMemberActivity.this,
					AddTalkMemberActivity.class);
			startActivity(i);
			finish();
			break;
		case R.id.tv_memberedit_del:
			i = new Intent(PopupwindowEditMemberActivity.this,
					DelTalkMemberActivity.class);
			startActivity(i);
			finish();
			break;
		case R.id.tv_memberedit_cancel:
			finish();
			break;
		default:
			break;
		}

	}
}