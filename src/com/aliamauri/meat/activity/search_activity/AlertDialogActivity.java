package com.aliamauri.meat.activity.search_activity;



import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.activity.IM.utils.CommonUtils;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

public class AlertDialogActivity extends BaseActivity implements OnClickListener {
	private String TAG=this.getClass().getName();

	@Override
	protected int setActivityAnimaMode() {
		return 5;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_agree:
			LogUtil.e(TAG, "topActivity: "+CommonUtils.getTopActivity(UIUtils.getContext()));
			LogUtil.e(TAG, "AlertDialogActivity : "+PlayCacheAcivity.class.getName());
			if (PlayCacheAcivity.instance==null) {
				startActivity(new Intent(this, PlayCacheAcivity.class));
			}
			finish();
			break;
		case R.id.tv_not_agree:
			finish();
			break;
		}

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
		rootView.findViewById(R.id.tv_agree).setOnClickListener(this);
		rootView.findViewById(R.id.tv_not_agree).setOnClickListener(this);
	}

	@Override
	protected View getRootView() {
		// TODO Auto-generated method stub
		return UIUtils.inflate(R.layout.activity_alert_dialog);
	}

}
