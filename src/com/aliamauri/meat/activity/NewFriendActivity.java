package com.aliamauri.meat.activity;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.IM.activity.AddContactActivity;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

public class NewFriendActivity extends BaseActivity implements OnClickListener {
	private TextView tv_anf_myphone;

	private RelativeLayout rl_anf_search;// 跳转到查找页面
	private RelativeLayout rl_anf_telsfriend;// 跳转到通讯录好友页面

	@Override
	protected View getRootView() {
		return View.inflate(UIUtils.getContext(), R.layout.activity_newfriend,
				null);
	}

	/**
	 * 设置activity切换动画
	 * 
	 * @return
	 */
	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}

	@Override
	protected void initView() {
		tv_anf_myphone = (TextView) findViewById(R.id.tv_anf_myphone);

		rl_anf_search = (RelativeLayout) findViewById(R.id.rl_anf_search);
		rl_anf_telsfriend = (RelativeLayout) findViewById(R.id.rl_anf_telsfriend);
		initData();
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

	private void initData() {
		try {
			TelephonyManager tm = (TelephonyManager) UIUtils.getContext()
					.getSystemService(Context.TELEPHONY_SERVICE);

			// String myIMSI = SystemProperties
			// .get(TelephonyProperties.PROPERTY_IMSI);
			// String phoneNumber = SystemProperties
			// .get(TelephonyProperties.PROPERTY_LINE1_NUMBER);
			int simState = tm.getSimState();
			// TelephonyManager.SIM_STATE_ABSENT;
			// TelephonyManager.SIM_STATE_NETWORK_LOCKED;
			// tm.get
			if (simState == TelephonyManager.SIM_STATE_READY) {
			}
			if (CheckUtils.getInstance().isMobile(tm.getLine1Number())) {
				tv_anf_myphone.setText("本机号码:" + tm.getLine1Number());
			} else {
				tv_anf_myphone.setText("未获取权限或没有插入sm卡");
			}

			// if
			// (CheckUtils.getInstance().isMobile(PhoneInfoUtils.getPhoneNumber(UIUtils
			// .getContext()))) {
			// tv_anf_myphone.setText("本机号码:"
			// + PhoneInfoUtils.getPhoneNumber(UIUtils.getContext()));
			// } else {
			// tv_anf_myphone.setText("未获取权限或没有插入sm卡");
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "添加新好友";
	}

	@Override
	protected void setListener() {
		rl_anf_search.setOnClickListener(this);
		rl_anf_telsfriend.setOnClickListener(this);
	}

	private long exitTime = 0;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_anf_search:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				startActivity(new Intent(NewFriendActivity.this,
						AddContactActivity.class));
			}
			break;
		case R.id.rl_anf_telsfriend:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				startActivity(new Intent(NewFriendActivity.this,
						TelsFriendActivity.class));
			}
			break;
		default:
			break;
		}
	}

}
