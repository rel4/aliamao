package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.umeng.analytics.MobclickAgent;

/**
 * ID搜索
 * 
 * @author ych
 * 
 */
public class AddTalkMemberActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_title_right;

	@Override
	protected View getRootView() {
		View view = View.inflate(AddTalkMemberActivity.this,
				R.layout.talk_member_add, null);
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "邀请新的成员";
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
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("完成");
	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

}
