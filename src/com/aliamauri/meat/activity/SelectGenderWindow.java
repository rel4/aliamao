package com.aliamauri.meat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
public class SelectGenderWindow extends Activity implements OnClickListener {

	private TextView btn_selectgender_male;// 男
	private TextView btn_selectgender_female;// 女
	private TextView btn_selectgender_cancel;// 取消

	private LinearLayout pop_selectgender_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.popupwindow_editdata_sex);

		init();// 初始化控件
		setListener();// 设置监听器
	}

	private void setListener() {
		pop_selectgender_layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
						Toast.LENGTH_SHORT).show();
			}
		});
		// 添加按钮监听
		btn_selectgender_male.setOnClickListener(this);
		btn_selectgender_female.setOnClickListener(this);
		btn_selectgender_cancel.setOnClickListener(this);
	}

	private void init() {
		pop_selectgender_layout = (LinearLayout) findViewById(R.id.pop_selectgender_layout);
		btn_selectgender_male = (TextView) findViewById(R.id.btn_selectgender_male);
		btn_selectgender_female = (TextView) findViewById(R.id.btn_selectgender_female);
		btn_selectgender_cancel = (TextView) findViewById(R.id.btn_selectgender_cancel);
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
	
	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
		Intent i = new Intent();
		switch (v.getId()) {
		case R.id.btn_selectgender_male:
			i.putExtra("editdata", btn_selectgender_male.getText().toString()
					.trim());
			setResult(4, i);
			finish();
			break;
		case R.id.btn_selectgender_female:
			i.putExtra("editdata", btn_selectgender_female.getText().toString()
					.trim());
			setResult(4, i);
			finish();
			break;
		case R.id.btn_selectgender_cancel:
			finish();
			break;
		default:
			break;
		}

	}
}