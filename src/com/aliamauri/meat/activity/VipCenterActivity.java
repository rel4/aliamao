package com.aliamauri.meat.activity;

import android.view.View;
import android.view.View.OnClickListener;

import com.aliamauri.meat.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 会员中心
 * 
 * @author ych
 * 
 */
public class VipCenterActivity extends BaseActivity implements OnClickListener {

	@Override
	protected View getRootView() {
		View view = View.inflate(VipCenterActivity.this, R.layout.vip_center,
				null);
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "会员中心";
	}

	@Override
	protected void initView() {
	}

	@Override
	protected void setListener() {

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
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

}
