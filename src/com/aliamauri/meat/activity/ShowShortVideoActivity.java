package com.aliamauri.meat.activity;

import java.io.File;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.VideoView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

public class ShowShortVideoActivity extends BaseActivity implements
		OnTouchListener {
	private String TAG = "ShowShortVideoActivity";
	private VideoView vv;
	private String path;

	@Override
	protected View getRootView() {

		return UIUtils.inflate(R.layout.activity_show_short_video);
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
		vv = (VideoView) rootView.findViewById(R.id.vv);
		Intent intent = getIntent();
		path = intent.getStringExtra(GlobalConstant.SHOW_SHORT_VIDEO_PATH);
		
		if (TextUtils.isEmpty(path)) {
			finish();
			return;
		}
		if(!new File(path).exists()){
			Log.e(TAG, "********文件不存在*******");
			finish();
			return;
		}
		vv.setVideoURI(Uri.fromFile(new File(path)));
		
		vv.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
					if (vv!=null) {
						finish();
					}
				
			}
		});
		vv.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				if (vv != null) {
					startPlay();
				}
			}
		});

	}

	protected void startPlay() {
		try{
			vv.start();
			rootView.setOnTouchListener(this);
		}catch(Exception e){
			UIUtils.showToast(this, "该视频不支持播放");
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		finish();
		return true;
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

}