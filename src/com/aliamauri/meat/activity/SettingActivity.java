package com.aliamauri.meat.activity;

import java.io.File;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.DataCleanManager;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.view.ShowAlertDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 系统设置
 * 
 * @author ych
 * 
 */
public class SettingActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout rl_setting_message;// 消息设置
	private RelativeLayout rl_setting_accountbind;// 绑定帐号
	private RelativeLayout rl_setting_modifypwd;// 修改密码
	private RelativeLayout rl_setting_quiet;
	private RelativeLayout rl_cache_clean;  //清除缓存
	private TextView tv_cache_message;  

	@Override
	protected View getRootView() {
		View view = View.inflate(SettingActivity.this, R.layout.setting, null);
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "系统设置";
	}

	@Override
	protected void initView() {

		rl_setting_message = (RelativeLayout) findViewById(R.id.rl_setting_message);
		rl_setting_accountbind = (RelativeLayout) findViewById(R.id.rl_setting_accountbind);
		rl_setting_modifypwd = (RelativeLayout) findViewById(R.id.rl_setting_modifypwd);
		rl_setting_quiet = (RelativeLayout) findViewById(R.id.rl_setting_quiet);
		tv_cache_message = (TextView) findViewById(R.id.tv_cache_message);	
		showCacheMessage(tv_cache_message);
		rl_cache_clean = (RelativeLayout) findViewById(R.id.rl_cache_clean);

	}

	private void showCacheMessage(TextView tv) {
		try {
			String cacheSize = DataCleanManager.getInstance().getCacheSize(new File(GlobalConstant.HEAD_ICON_SAVEPATH));
			tv.setText(cacheSize);
		} catch (Exception e) {
			e.printStackTrace();
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
	protected void setListener() {
		rl_setting_message.setOnClickListener(this);
		rl_setting_accountbind.setOnClickListener(this);
		rl_setting_modifypwd.setOnClickListener(this);
		rl_setting_quiet.setOnClickListener(this);
		rl_cache_clean.setOnClickListener(this);
	}

	// private void clearSP() {
	//
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		case R.id.rl_cache_clean:
			DataCleanManager.getInstance().cleanCustomCache(GlobalConstant.DT_UPDATE_ICON_SAVEPATH);//动态拍照上传图片路径
			DataCleanManager.getInstance().cleanCustomCache(GlobalConstant.YJZQ_HEAD_ICON_SAVEPATH);//一见钟情好友头像图片路径
			DataCleanManager.getInstance().cleanCustomCache(GlobalConstant.DYNAMIC_SMALLIMAGE_SAVEPATH);//动态数据中的小图片图片路径
			DataCleanManager.getInstance().cleanCustomCache(GlobalConstant.DYNAMIC_BIGIMAGE_SAVEPATH);//动态数据中的大图片图片路径
			DataCleanManager.getInstance().cleanCustomCache(GlobalConstant.DYNAMIC_HEADIMAGE_SAVEPATH);//动态数据中的好友头像图片路径
			DataCleanManager.getInstance().cleanCustomCache(GlobalConstant.DYNAMIC_MOVICE_SAVEPATH);//动态数据中的语音数据的路径
			DataCleanManager.getInstance().cleanCustomCache(GlobalConstant.DYNAMIC_VIDEOIMGBG_SAVEPATH);//动态数据中的保存视频背景图的路径
			showCacheMessage(tv_cache_message);
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.rl_setting_message:
			startActivity(new Intent(this, SettingMessageActivity.class));
			break;
		case R.id.rl_setting_accountbind:
			startActivity(new Intent(this, AccountBindActivity.class));	
			break;
		case R.id.rl_setting_modifypwd:
			startActivity(new Intent(this, ModifyPasswordActivity.class));
			break;
		case R.id.rl_setting_quiet:
			// clearSP();
			ShowAlertDialog s = new ShowAlertDialog(this, "是否要退出", "quit");
			break;
		default:
			break;
		}
	}

}
