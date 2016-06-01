/************************************************************
 *  * EaseMob CONFIDENTIAL 
 * __________________ 
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved. 
 *  
 * NOTICE: All information contained herein is, and remains 
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material 
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */
package com.aliamauri.meat.activity.IM.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.ShowShortVideoActivity;
import com.aliamauri.meat.activity.IM.utils.CommonUtils;
import com.aliamauri.meat.activity.IM.video.utils.Utils;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.utils.LogUtil;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.umeng.analytics.MobclickAgent;

@SuppressWarnings("deprecation")
public class RecorderVideoActivity extends BaseActivity implements
		OnClickListener, SurfaceHolder.Callback, OnErrorListener,
		OnInfoListener {
	private static final String TAG = "RecorderVideoActivity";
	private final static String CLASS_LABEL = "RecordActivity";
	private PowerManager.WakeLock mWakeLock;
	private TextView btnStart;// 开始录制按钮
	private MediaRecorder mediaRecorder;// 录制视频的类
	private VideoView mVideoView;// 显示视频的控件
	String localPath = "";// 录制的视频路径
	private Camera mCamera;
	// 预览的宽高
	private int previewWidth = 480;
	private int previewHeight = 480;
//	private Chronometer chronometer;
	private int frontCamera = 0;// 0是后置摄像头，1是前置摄像头
	private ImageView btn_switch;
	private View recorder_send;
	private Parameters cameraParameters = null;
	private SurfaceHolder mSurfaceHolder;
	int defaultVideoFrameRate = -1;
	private final static int FLAG_PROGRESS_UP = 1;
	private int progress;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FLAG_PROGRESS_UP:
				progress += 10;
				if (pb_show_progress != null) {
					pb_show_progress.setProgress(progress);
					upProgrss();
				}
				break;

			}
		};
	};
	private boolean isStartRecord = false;

	private void upProgrss() {
		if (isStartRecord) {
			mHandler.sendEmptyMessageDelayed(FLAG_PROGRESS_UP, 1000);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
//		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		 WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
//		 选择支持半透明模式，在有surfaceview的activity中使用
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.recorder_activity);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				CLASS_LABEL);
		mWakeLock.acquire();
		initViews();
	}

	private void initViews() {
		((TextView) findViewById(R.id.tv_title_title)).setText(getResources().getString(R.string.recording_video));
		pb_show_progress = (ProgressBar) findViewById(R.id.pb_show_progress);
		recorder_play = findViewById(R.id.recorder_play);
		recorder_play.setOnClickListener(this);
		recorder_send = findViewById(R.id.recorder_send);
		recorder_send.setOnClickListener(this);
		findViewById(R.id.iv_title_backicon).setOnClickListener(this);
		btn_switch = (ImageView) findViewById(R.id.iv_title_righticon);
		btn_switch.setVisibility(View.VISIBLE);
		btn_switch.setImageResource(R.drawable.camera_switch_selector);
		btn_switch.setOnClickListener(this);
		mVideoView = (VideoView) findViewById(R.id.mVideoView);
		btnStart = (TextView) findViewById(R.id.recorder_start);
		btnStart.setOnTouchListener(new RecorderTouchListener());
		mSurfaceHolder = mVideoView.getHolder();
		mSurfaceHolder.addCallback(this);
		// mSurfaceHolder.setFixedSize(720, 539);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		chronometer = (Chronometer) findViewById(R.id.chronometer);
	}

	class RecorderTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			try {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startRecorder();
					break;
				case MotionEvent.ACTION_UP:
					stopRecorder();
					break;
				default:
					return false;
				}
				return true;
			} catch (Exception e) {
				stopRecorder();
			}
			return false;
		}

	}

	/**
	 * 开始录像
	 */
	private void startRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder = null;
		}
		if (localPath != null) {
			File file = new File(localPath);
			if (file.exists())
				file.delete();
		}
		// start recording
		if (!startRecording())
			return;
		if (recorder_play != null) {
			recorder_play.setVisibility(View.INVISIBLE);
			recorder_play.setClickable(true);
		}
		if (recorder_send != null) {
			recorder_send.setVisibility(View.INVISIBLE);
			recorder_send.setClickable(true);

		}
		// 重置其他
		// chronometer.setBase(SystemClock.elapsedRealtime());
		// chronometer.start();
		isStartRecord = true;
		if (progress != 0) {
			progress = 0;
		}
		upProgrss();
	}

	/**
	 * 结束录像
	 */
	private void stopRecorder() {
		// 停止拍摄
		stopRecording();
		// chronometer.stop();
		isStartRecord = false;
		if (recorder_play != null) {
			recorder_play.setVisibility(View.VISIBLE);
			recorder_play.setClickable(true);
		}
		if (recorder_send != null) {
			recorder_send.setVisibility(View.VISIBLE);
			recorder_send.setClickable(true);

		}
	}

	

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (mWakeLock == null) {
			// 获取唤醒锁,保持屏幕常亮
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
					CLASS_LABEL);
			mWakeLock.acquire();
		}
		// if (!initCamera()) {
		// showFailDialog();
		// }
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private boolean initCamera() {
		try {
			if (frontCamera == 0) {
				mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
			} else {
				mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
			}
			Camera.Parameters camParams = mCamera.getParameters();
			camParams=mCamera.getParameters();  
//			camParams.setPictureFormat(PixelFormat.JPEG);  
            //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  // 部分定制手机，无法正常识别该方法。  
			camParams.setFlashMode(Parameters.ANTIBANDING_AUTO);     
			camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);//1连续对焦  
//            setDispaly(camParams,mCamera);  
            mCamera.setParameters(camParams);  
            mCamera.startPreview();  
            mCamera.cancelAutoFocus();
			mCamera.lock();
			mSurfaceHolder = mVideoView.getHolder();
			mSurfaceHolder.addCallback(this);
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mCamera.setDisplayOrientation(90);

		} catch (RuntimeException ex) {
			EMLog.e("video", "init Camera fail " + ex.getMessage());
			return false;
		}
		return true;
	}

	private void handleSurfaceChanged() {
		if (mCamera == null) {
			finish();
			return;
		}
		boolean hasSupportRate = false;
		List<Integer> supportedPreviewFrameRates = mCamera.getParameters()
				.getSupportedPreviewFrameRates();
		if (supportedPreviewFrameRates != null
				&& supportedPreviewFrameRates.size() > 0) {
			Collections.sort(supportedPreviewFrameRates);
			for (int i = 0; i < supportedPreviewFrameRates.size(); i++) {
				int supportRate = supportedPreviewFrameRates.get(i);
				LogUtil.e(this, "supportRate = "+supportRate);
				if (supportRate == 30) {
					hasSupportRate = true;
				}

			}
			if (hasSupportRate) {
				defaultVideoFrameRate = 30;
			} else {
				defaultVideoFrameRate = supportedPreviewFrameRates.get(0);
			}

		}
		// 获取摄像头的所有支持的分辨率
		List<Camera.Size> resolutionList = Utils.getResolutionList(mCamera);
		if (resolutionList != null && resolutionList.size() > 0) {
			Collections.sort(resolutionList, new Utils.ResolutionComparator());
			Camera.Size previewSize = null;
			boolean hasSize = false;
			// 如果摄像头支持640*480，那么强制设为640*480
			for (int i = 0; i < resolutionList.size(); i++) {
				Size size = resolutionList.get(i);
				LogUtil.e(this , "width   =  "+size.width+" :　　　height  =  "+size.height);
				if (size != null && size.width ==640&&size.height==480) {
					
					previewSize = size;
					previewWidth = previewSize.width;
					previewHeight = previewSize.height;
					hasSize = true;
					break;
				}
			}
			// 如果不支持设为中间的那个
			if (!hasSize) {
				int mediumResolution = resolutionList.size() / 2;
				if (mediumResolution >= resolutionList.size())
					mediumResolution = resolutionList.size() - 1;
				previewSize = resolutionList.get(mediumResolution);
				previewWidth = previewSize.width;
				previewHeight = previewSize.height;
				LogUtil.e(RecorderVideoActivity.class, "previewWidth   =  "+previewWidth);
				LogUtil.e(RecorderVideoActivity.class, "previewHeight  =  "+previewHeight);

			}

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		 MobclickAgent.onPause(this);
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_title_righticon:
			switchCamera();
			break;
		case R.id.iv_title_backicon:
			releaseRecorder();
			releaseCamera();
			finish();
			break;
		case R.id.recorder_send:
			sendVideo();
			break;
		case R.id.recorder_play:
			if (recorder_play != null && !TextUtils.isEmpty(localPath)) {
				startActivity(new Intent(this, ShowShortVideoActivity.class)
						.putExtra(GlobalConstant.SHOW_SHORT_VIDEO_PATH,
								localPath));
			}
			break;

		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera == null) {
			if (!initCamera()) {
				showFailDialog();
				return;
			}

		}
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
			mCamera.startPreview();
			handleSurfaceChanged();
		} catch (Exception e1) {
			EMLog.e("video", "start preview fail " + e1.getMessage());
			showFailDialog();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		EMLog.v("video", "surfaceDestroyed");
	}

	public boolean startRecording() {
		if (mediaRecorder == null) {
			if (!initRecorder())
				return false;
		}
		try {
			mediaRecorder.setOnInfoListener(this);
			mediaRecorder.setOnErrorListener(this);
			mediaRecorder.start();
		} catch (Exception e) {
			stopRecording();
			return false;
		}
		
//		
//			}   
		
		return true;
	}

	@SuppressLint("NewApi")
	private boolean initRecorder() {
		if (!CommonUtils.isExitsSdcard()) {
			showNoSDCardDialog();
			return false;
		}

		if (mCamera == null) {
			if (!initCamera()) {
				showFailDialog();
				return false;
			}
		}
		mVideoView.setVisibility(View.VISIBLE);
		// TODO init button
		mCamera.stopPreview();
		mediaRecorder = new MediaRecorder();
		
		/*****************************************/
//		 //设置视频源  
//		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);  
//        //设置音频源  
//		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);  
//        
//        //相机参数配置类
//       CamcorderProfile cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH); 
//       mediaRecorder.setProfile(cProfile);
        //设置录制的视频帧率,注意文档的说明:  
//       mediaRecorder.setVideoFrameRate(30);   
        //设置输出路径  
//       mediaRecorder.setOutputFile("/sdcard/Document/data/"+ currentTemp + "/"
//				+ currentXml + "/"+System.currentTimeMillis()+".mp4");
        //设置预览画面  
		/*****************************************/
		mCamera.unlock();
		mediaRecorder.setCamera(mCamera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		// 设置录制视频源为Camera（相机）
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		if (frontCamera == 1) {
			mediaRecorder.setOrientationHint(270);
		} else {
			mediaRecorder.setOrientationHint(90);
		}
		// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		// 设置录制的视频编码h263 h264
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
		mediaRecorder.setVideoSize(previewWidth, previewHeight);

		LogUtil.e(RecorderVideoActivity.class, "mediaRecorder  previewWidth = "+previewWidth+ "  ：  previewHeight   =  "+ previewHeight);
		// mediaRecorder.setVideoSize(720, 539);
		// 设置视频的比特率  5*1920*1080 //384 * 1024
		mediaRecorder.setVideoEncodingBitRate(3*1280*720);
		// mediaRecorder.setVideoEncodingBitRate(4000);
		// // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
		if (defaultVideoFrameRate != -1) {
			mediaRecorder.setVideoFrameRate(defaultVideoFrameRate);
			LogUtil.e(this, "defaultVideoFrameRate = " + defaultVideoFrameRate);
		}
		// 设置视频文件输出的路径
		File file = new File(PathUtil.getInstance().getVideoPath().toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		
		localPath = PathUtil.getInstance().getVideoPath().getAbsolutePath() + "/"
				+ System.currentTimeMillis() + ".mp4";
		// localPath = SDUtils.getSDPath() + "/"
		// + System.currentTimeMillis() + ".mp4";
		Log.e(TAG, localPath);
		mediaRecorder.setOutputFile(localPath);
		mediaRecorder.setMaxDuration(6000);
		mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {

		}
		return true;

	}

	public void stopRecording() {
		if (mediaRecorder != null) {
			mediaRecorder.setOnErrorListener(null);
			mediaRecorder.setOnInfoListener(null);
			try {
				mediaRecorder.stop();
			} catch (IllegalStateException e) {
				EMLog.e("video", "stopRecording error:" + e.getMessage());
			}
		}
		 releaseRecorder();
			if (mCamera != null) {
				mCamera.stopPreview();
				releaseCamera();
			}
	}

	@Override
	protected void onStop() {
		releaseRecorder();
		super.onStop();
	}

	private void releaseRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}
	protected void releaseCamera() {
		try {
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		} catch (Exception e) {
		}
	}

	@SuppressLint("NewApi")
	public void switchCamera() {

		if (mCamera == null) {
			return;
		}
		if (Camera.getNumberOfCameras() >= 2) {
			btn_switch.setEnabled(false);
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}

			switch (frontCamera) {
			case 0:
				mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
				frontCamera = 1;
				break;
			case 1:
				mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
				frontCamera = 0;
				break;
			}
			try {
				mCamera.lock();
				mCamera.setDisplayOrientation(90);
				mCamera.setPreviewDisplay(mVideoView.getHolder());
				mCamera.startPreview();
			} catch (IOException e) {
				mCamera.release();
				mCamera = null;
			}
			btn_switch.setEnabled(true);

		}

	}

	MediaScannerConnection msc = null;
	ProgressDialog progressDialog = null;
	private View recorder_play;
	private ProgressBar pb_show_progress;

	public void sendVideo() {
		if (TextUtils.isEmpty(localPath)) {
			EMLog.e("Recorder", "recorder fail please try again!");
			return;
		}

		if (msc == null)
			msc = new MediaScannerConnection(this,
					new MediaScannerConnectionClient() {

						@Override
						public void onScanCompleted(String path, Uri uri) {
							EMLog.d(TAG, "scanner completed");
							msc.disconnect();
							progressDialog.dismiss();
							// setResult(RESULT_OK,
							// getIntent().putExtra("uri", uri));
							backAndData(uri);
							// finish();
						}

						@Override
						public void onMediaScannerConnected() {
							msc.scanFile(localPath, "video/*");
						}
					});

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("processing...");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
		msc.connect();

	}

	private void backAndData(Uri uri) {
		String[] projects = new String[] { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.DURATION };
		
		Cursor cursor =getContentResolver().query(uri, projects, null, null,
				null);
		int duration = 0;
		String filePath = null;

		if (cursor.moveToFirst()) {
			// 路径：MediaStore.Audio.Media.DATA
			filePath = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
			// 总播放时长：MediaStore.Audio.Media.DURATION
			duration = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
			EMLog.d(TAG, "duration:" + duration);
		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}

		setResult(Activity.RESULT_OK, getIntent().putExtra("path", filePath).putExtra("dur", duration));
		finish();
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		EMLog.v("video", "onInfo");
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
			EMLog.v("video", "max duration reached");
			stopRecording();
//			chronometer.stop();
//			chronometer.stop();
			if (localPath == null) {
				return;
			}
			String st3 = getResources().getString(R.string.Whether_to_send);
			new AlertDialog.Builder(this)
					.setMessage(st3)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();
									sendVideo();

								}
							}).setNegativeButton(R.string.cancel, null)
					.setCancelable(false).show();
		}

	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		EMLog.e("video", "recording onError:");
		stopRecording();
		Toast.makeText(this,
				"Recording error has occurred. Stopping the recording",
				Toast.LENGTH_SHORT).show();

	}

	public void saveBitmapFile(Bitmap bitmap) {
		File file = new File(Environment.getExternalStorageDirectory(), "a.jpg");
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseCamera();

		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}

	}

	@Override
	public void onBackPressed() {
		back(null);
	}

	private void showFailDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.prompt)
				.setMessage(R.string.Open_the_equipment_failure)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();

							}
						}).setCancelable(false).show();

	}

	private void showNoSDCardDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.prompt)
				.setMessage("No sd card!")
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();

							}
						}).setCancelable(false).show();
	}

}
