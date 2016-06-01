package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.aliamauri.meat.R;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.view.MySwitch;
import com.aliamauri.meat.view.MySwitch.OnCheckedChangeListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 消息设置
 * 
 * @author ych
 * 
 */
public class SettingMessageActivity extends BaseActivity implements
		OnClickListener {

	private RelativeLayout rl_sm_showmessage;

	private MySwitch ms_sm_getmessage;// 接收新消息提醒
	private MySwitch ms_sm_showmessage;// 提醒时显示消息内容
	private MySwitch ms_sm_voice;// 声音
	private MySwitch ms_sm_vibrate;// 振动
	private MySwitch ms_sm_nightmessage;// 夜间免打扰
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
	protected View getRootView() {
		View view = View.inflate(SettingMessageActivity.this,
				R.layout.setting_message, null);

		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "消息设置";
	}

	@Override
	protected void initView() {
		ms_sm_getmessage = (MySwitch) findViewById(R.id.ms_sm_getmessage);
		ms_sm_voice = (MySwitch) findViewById(R.id.ms_sm_voice);
		ms_sm_vibrate = (MySwitch) findViewById(R.id.ms_sm_vibrate);
		ms_sm_showmessage = (MySwitch) findViewById(R.id.ms_sm_showmessage);
		rl_sm_showmessage = (RelativeLayout) findViewById(R.id.rl_sm_showmessage);
		ms_sm_nightmessage = (MySwitch) findViewById(R.id.ms_sm_nightmessage);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initNet() {
		initData();
		setMySwitchListener();
	}

	private void initData() {
		ms_sm_getmessage.setOpen(PrefUtils.getBoolean(
				GlobalConstant.SM_GETMESSAGE, false));
		ms_sm_voice.setOpen(PrefUtils
				.getBoolean(GlobalConstant.SM_VOICE, false));
		ms_sm_vibrate.setOpen(PrefUtils.getBoolean(GlobalConstant.SM_VIBRATE,
				false));
		if (ms_sm_getmessage.getOpen()) {
			rl_sm_showmessage.setVisibility(View.VISIBLE);
			ms_sm_showmessage.setOpen(PrefUtils.getBoolean(
					GlobalConstant.SM_SHOWMESSAGE, false));

		} else {
			rl_sm_showmessage.setVisibility(View.GONE);
			PrefUtils.setBoolean(GlobalConstant.SM_SHOWMESSAGE, false);
			ms_sm_showmessage.setOpen(false);
		}

		ms_sm_nightmessage.setOpen(PrefUtils.getBoolean(
				GlobalConstant.SM_NIGHTMESSAGE, false));
	}

	private void setMySwitchListener() {

		ms_sm_getmessage.setOnChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MySwitch mySwitch, boolean isOpen) {
				PrefUtils.setBoolean(GlobalConstant.SM_GETMESSAGE, isOpen);
				if (isOpen) {
					rl_sm_showmessage.setVisibility(View.VISIBLE);
					ms_sm_showmessage.setOpen(true);

				} else {
					rl_sm_showmessage.setVisibility(View.GONE);
				}

			}
		});
		ms_sm_voice.setOnChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MySwitch mySwitch, boolean isOpen) {
				PrefUtils.setBoolean(GlobalConstant.SM_VOICE, isOpen);

			}
		});
		ms_sm_vibrate.setOnChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MySwitch mySwitch, boolean isOpen) {
				PrefUtils.setBoolean(GlobalConstant.SM_VIBRATE, isOpen);

			}
		});
		ms_sm_showmessage.setOnChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MySwitch mySwitch, boolean isOpen) {
				PrefUtils.setBoolean(GlobalConstant.SM_SHOWMESSAGE, isOpen);

			}
		});
		ms_sm_nightmessage.setOnChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MySwitch mySwitch, boolean isOpen) {
				PrefUtils.setBoolean(GlobalConstant.SM_NIGHTMESSAGE, isOpen);

			}
		});
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
