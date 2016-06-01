package com.aliamauri.meat.listener;

import java.io.File;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.IM.adapter.VoicePlayClickListener;
import com.aliamauri.meat.activity.IM.utils.CommonUtils;
import com.aliamauri.meat.parse.JsonParser;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.UIUtils;
import com.easemob.util.PathUtil;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

public abstract class LMRecognizerListener implements View.OnTouchListener {
	private String TAG = "LMRecognizerListener";
	private Context mContext;
	private Drawable[] micImages;
	private String toChatUsername;
	private View recordingContainer;
	private TextView recordingHint;
	private ImageView micImage;
	private SpeechRecognizer mIat;
	private String voiceFilePath;// 文件的路径
	private String mRecordingTime;
	private Long mCurrentTime;

	/**
	 * 环信ID
	 * 
	 * @param HXid
	 */
	public LMRecognizerListener(Context context, String toChatUsername,
			View recordingContainer) {
		mContext = UIUtils.getContext();
		mRecordingTime = "-1";
		mCurrentTime = -1L;
		this.toChatUsername = toChatUsername;
		this.recordingContainer = recordingContainer;
		mIat = SpeechRecognizer.createRecognizer(mContext, null);
		recordingHint = (TextView) recordingContainer
				.findViewById(R.id.recording_hint);
		micImage = (ImageView) recordingContainer.findViewById(R.id.mic_image);
		LogUtil.e("LMRecognizerListener", "LMRecognizerListener-->开启");

	}

	// 动画资源文件,用于录制语音时

	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[(msg.what) / 2]);
		}
	};

	/**
	 * 初始化
	 */
	private void init() {

		initImages();
		// 1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener

		// 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "10000");

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "10000");
		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA, "0");
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		voiceFilePath = getVoiceFilePath(toChatUsername);
		LogUtil.e(TAG, "pcm  路径  ： " + voiceFilePath);
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, voiceFilePath);
		// 3.开始听写
		// mIat.startListening(mRecoListener);
		// mIat.stopListening();
	}

	// 听写监听器
	private RecognizerListener mRecoListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			if (recordingContainer != null) {
				recordingContainer.setVisibility(View.VISIBLE);
			}

		}

		@Override
		public void onEndOfSpeech() {
			if (recordingContainer != null) {
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void onError(SpeechError arg0) {
			// TODO Auto-generated method stub
			LogUtil.e(TAG, "onError----->" + arg0.toString());
			File file = new File(voiceFilePath);
			if (!isErrorVoice(Long.parseLong(mRecordingTime))) {
			sendListener(file.getAbsoluteFile().toString(), file.getName(),
					"-1".equals(mRecordingTime)?"0":mRecordingTime, "");
			}
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO Auto-generated method stub
			LogUtil.e(TAG, "onEvent----->" + arg0 + "");

		}

		@Override
		public void onResult(RecognizerResult arg0, boolean isLast) {
			String text = JsonParser.parseIatResult(arg0.getResultString());
			LogUtil.e(TAG, "onResult----->" + arg0.getResultString());
			LogUtil.e(TAG, "onResult----->" + text);
			File file = new File(voiceFilePath);
			if (isLast) {
				if (!isErrorVoice(Long.parseLong(mRecordingTime))) {
				sendListener(file.getAbsoluteFile().toString(), file.getName(),
						"-1".equals(mRecordingTime)?"0":mRecordingTime, ResultText);}
			}else {
				ResultText=ResultText+text;
			}
			
		}

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			LogUtil.e(TAG, "onVolumeChanged----->" + arg0 + "");
			if (micImages != null) {
				micImageHandler.sendEmptyMessage(arg0);
			}
		}
	};
	private String ResultText;
	public String getVoiceFilePath(String paramString) {
		Time localTime = new Time();
		localTime.setToNow();
		File file = PathUtil.getInstance().getVoicePath();
		if (!file.exists()) {
			file.mkdirs();
		}
		return file + File.separator + paramString
				+ localTime.toString().substring(0, 15) + ".wav";
	}

	/**
	 * 停止录入
	 */
	private void stopRecordVoice() {
		if(mCurrentTime != -1){
			mRecordingTime = String.valueOf(System.currentTimeMillis()-mCurrentTime);
		}
		if (mIat != null) {
			mIat.stopListening();
		}

	}
	
	private void startRecordVoice() {
		mCurrentTime = System.currentTimeMillis();
		if (mIat != null) {
			ResultText="";
			mIat.startListening(mRecoListener);
		}
	}
	/**
	 * 是否录音合法
	 * @param voiceLength
	 * @return
	 */
	private boolean isErrorVoice(long voiceLength){
		if (voiceLength<=1000) {
			UIUtils.showToast(UIUtils.getContext(), mContext.getResources().getString(R.string.The_recording_time_is_too_short));
			return true;
		}
		return false;
	}
	

	private void initImages() {
		micImages = new Drawable[] {
				mContext.getResources().getDrawable(
						R.drawable.record_animate_01),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_01),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_02),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_03),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_04),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_05),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_06),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_07),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_08),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_09),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_10),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_11),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_12),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_13),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_14),
				mContext.getResources().getDrawable(
						R.drawable.record_animate_14) };

	}

	// public String getVoiceFileName(String paramString)
	// {
	//
	// return paramString + localTime.toString().substring(0, 15) + ".amr";
	// }

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	//
	// break;
	// case MotionEvent.a:
	//
	// break;
	//
	// default:
	// break;
	// }
	// return false;
	// }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!CommonUtils.isExitsSdcard()) {
				String st4 = mContext.getResources().getString(
						R.string.Send_voice_need_sdcard_support);
				Toast.makeText(mContext, st4, Toast.LENGTH_SHORT).show();
				return false;
			}
			try {
				v.setPressed(true);
				if (VoicePlayClickListener.isPlaying)
					VoicePlayClickListener.currentPlayListener.stopPlayVoice();
				if (recordingContainer != null) {
					recordingContainer.setVisibility(View.VISIBLE);
				}
				recordingHint.setText(mContext
						.getString(R.string.move_up_to_cancel));
				recordingHint.setBackgroundColor(Color.TRANSPARENT);
				init();
				mIat.startListening(mRecoListener);
				startRecordVoice();
			} catch (Exception e) {
				e.printStackTrace();
				v.setPressed(false);
				// if (wakeLock.isHeld())
				// wakeLock.release();
				// if (voiceRecorder != null)
				// voiceRecorder.discardRecording();
				if (recordingContainer != null) {
					recordingContainer.setVisibility(View.INVISIBLE);
				}
				Toast.makeText(mContext, R.string.recoding_fail,
						Toast.LENGTH_SHORT).show();
				return false;
			}

			return true;
		case MotionEvent.ACTION_MOVE: {
			if (event.getY() < 0) {
				recordingHint.setText(mContext
						.getString(R.string.release_to_cancel));
				recordingHint
						.setBackgroundResource(R.drawable.recording_text_hint_bg);
			} else {
				recordingHint.setText(mContext
						.getString(R.string.move_up_to_cancel));
				recordingHint.setBackgroundColor(Color.TRANSPARENT);
			}
			return true;
		}
		case MotionEvent.ACTION_UP:
			v.setPressed(false);
			if (recordingContainer != null) {
				recordingContainer.setVisibility(View.INVISIBLE);
			}
			stopRecordVoice();
			// if (wakeLock.isHeld())
			// wakeLock.release();
			if (event.getY() < 0) {
				// discard the recorded audio.
				// voiceRecorder.discardRecording();
				stopRecordVoice();

			} else {
				// stop recording and send voice file
				String st1 = mContext.getResources().getString(
						R.string.Recording_without_permission);
				String st2 = mContext.getResources().getString(
						R.string.The_recording_time_is_too_short);
				String st3 = mContext.getResources().getString(
						R.string.send_failure_please);
				try {
					// mIat.getRecognizer().getParameter(arg0)
					// // int length = voiceRecorder.stopRecoding();
					// if (length > 0) {
					// sendListener(voiceRecorder.getVoiceFilePath(),voiceRecorder.getVoiceFileName(toChatUsername),Integer.toString(length),
					// false);
					// // sendVoice(voiceRecorder.getVoiceFilePath(),
					// // voiceRecorder.getVoiceFileName(toChatUsername),
					// // Integer.toString(length), false);
					// } else if (length == EMError.INVALID_FILE) {
					// Toast.makeText(mContext, st1,
					// Toast.LENGTH_SHORT).show();
					// } else {
					// Toast.makeText(mContext, st2,
					// Toast.LENGTH_SHORT).show();
					// }
					stopRecordVoice();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(mContext, st3, Toast.LENGTH_SHORT).show();
				}

			}
			return true;
		default:
			if (recordingContainer != null) {
				recordingContainer.setVisibility(View.INVISIBLE);
			}
			stopRecordVoice();
			return false;
		}
	}

	/**
	 * 发送语音回调
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	public abstract void sendListener(String filePath, String fileName,
			String length, String voiceToText);

}
