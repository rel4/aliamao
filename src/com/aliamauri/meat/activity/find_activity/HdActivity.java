package com.aliamauri.meat.activity.find_activity;

import android.app.Activity;
import android.os.Bundle;

import com.aliamauri.meat.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 发现----活动界面
 * 
 * @author limaokeji-windosc
 * 
 */
public class HdActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_hd);
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
}
