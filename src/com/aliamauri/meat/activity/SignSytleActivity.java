package com.aliamauri.meat.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 个性签名
 * 
 * @author ych
 * 
 */
public class SignSytleActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title_title;
	private TextView tv_title_right;
	private Intent signIntent;
	private EditText et_singnstyle_sign;

	@Override
	protected View getRootView() {
		View view = View.inflate(SignSytleActivity.this, R.layout.sign_style,
				null);
		signIntent = getIntent();
		return view;
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
	
	@Override
	protected void initView() {
		tv_title_title = (TextView) findViewById(R.id.tv_title_title);
		if (signIntent.getStringExtra("talkdata") != null) {
			tv_title_title.setText("话题名字");
		} else {
			tv_title_title.setText("个性签名");
		}
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("保存");
		et_singnstyle_sign = (EditText) findViewById(R.id.et_singnstyle_sign);
		if (signIntent.getStringExtra("editdata") != null) {
			et_singnstyle_sign.setText(signIntent.getStringExtra("editdata")
					+ "");
		}
		KeyBoardUtils.openKeybord(et_singnstyle_sign, SignSytleActivity.this);
	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 显示键盘
	 * 
	 * @param context
	 * @param view
	 */
	public static void showInputMethod(Context context, View view) {
		InputMethodManager im = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.showSoftInput(view, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			Intent i = new Intent();
			i.putExtra("editdata", et_singnstyle_sign.getText().toString()
					.trim());
			setResult(5, i);
			finish();
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

}
