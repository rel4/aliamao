package com.aliamauri.meat.listener;

import java.io.File;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.IM.adapter.VoicePlayClickListener;
import com.aliamauri.meat.activity.IM.utils.CommonUtils;
import com.aliamauri.meat.media.LMVoiceRecorder;
import com.aliamauri.meat.media.LMVoiceRecorder.VoiceFileType;
import com.aliamauri.meat.utils.UIUtils;
/**
 *	录音监听并翻译语音
 * @author admin
 *
 */
public abstract class LMPressToSpeakListenr implements View.OnTouchListener{
	private Context mContext;
	private PowerManager.WakeLock wakeLock;
	private View recordingContainer;
	private TextView recordingHint;
	private Drawable[] micImages;
	//声音改变
	public final static int VOLUME_CHANGE_SIZE=1;
	public final static int VOICE_RESULT=2;
	// 动画资源文件,用于录制语音时
			
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			switch (msg.what) {
			case VOLUME_CHANGE_SIZE:
				int v=	(int) msg.obj/3;
				micImage.setImageDrawable(micImages[v]);
				break;
			case VOICE_RESULT:
				Map<VoiceFileType, String> map = (Map<VoiceFileType, String>) msg.obj;
			parseResult(map);
				break;
			}
			
		}
	};
	private ImageView micImage;
	private LMVoiceRecorder voiceRecorder;
	private String toChatUsername;
	/**
	 * 
	 * @param context
	 * @param toChatUsername
	 * @param recordingContainer
	 */
	public LMPressToSpeakListenr(Context context,String toChatUsername,View recordingContainer){
		this.mContext = context;
		this.toChatUsername =toChatUsername;
		this.recordingContainer = recordingContainer;
		init();
		initImages();
	}
	/**
	 * 解析录音返回的信息
	 * @param map
	 */
	private void parseResult(Map<VoiceFileType, String> map){
		if (map==null) {
			return;
		}
		String amrFilePath=null;
		String result=null;
		long voiceTimeLeng =0;
		if (map.containsKey(VoiceFileType.FILE_TYPE_AMR)) {
			amrFilePath = map.get(VoiceFileType.FILE_TYPE_AMR);
		}
		if (map.containsKey(VoiceFileType.FILE_TYPE_XF_TEXT)) {
			result=map.get(VoiceFileType.FILE_TYPE_XF_TEXT);
		}
		if (map.containsKey(VoiceFileType.FILE_TYPE_AMR_TIME)) {
		
			try {
				String timeLength = map.get(VoiceFileType.FILE_TYPE_AMR_TIME);
				voiceTimeLeng=Long.parseLong(timeLength);
			} catch (Exception e) {
				
			}
			
		}
		File file = new File(amrFilePath);
		if (!isErrorVoice(voiceTimeLeng)) {
			sendListener(file.getAbsolutePath(), file.getName(), voiceTimeLeng, result,false);
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
				mContext.getResources().getDrawable(R.drawable.record_animate_01),
				mContext.getResources().getDrawable(R.drawable.record_animate_01),
				mContext.getResources().getDrawable(R.drawable.record_animate_02),
				mContext.getResources().getDrawable(R.drawable.record_animate_03),
				mContext.getResources().getDrawable(R.drawable.record_animate_04),
				mContext.getResources().getDrawable(R.drawable.record_animate_05),
				mContext.getResources().getDrawable(R.drawable.record_animate_06),
				mContext.getResources().getDrawable(R.drawable.record_animate_07),
				mContext.getResources().getDrawable(R.drawable.record_animate_08),
				mContext.getResources().getDrawable(R.drawable.record_animate_09),
				mContext.getResources().getDrawable(R.drawable.record_animate_10),
				mContext.getResources().getDrawable(R.drawable.record_animate_11),
				mContext.getResources().getDrawable(R.drawable.record_animate_12),
				mContext.getResources().getDrawable(R.drawable.record_animate_13),
				mContext.getResources().getDrawable(R.drawable.record_animate_14),
				mContext.getResources().getDrawable(R.drawable.record_animate_14) };
		
	}

	private void init() {
		voiceRecorder = new LMVoiceRecorder(micImageHandler);
//		voiceRecorder.
		wakeLock=wakeLock = ((PowerManager) mContext.getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
//		recording_container = View.inflate(mContext, R.layout.recording_container, null);
		recordingHint = (TextView) recordingContainer.findViewById(R.id.recording_hint);
		micImage = (ImageView) recordingContainer.findViewById(R.id.mic_image);
	}

	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!CommonUtils.isExitsSdcard()) {
				String st4 = mContext.getResources().getString(
						R.string.Send_voice_need_sdcard_support);
				Toast.makeText(mContext, st4, Toast.LENGTH_SHORT)
						.show();
				return false;
			}
			try {
				v.setPressed(true);
				wakeLock.acquire();
				if (VoicePlayClickListener.isPlaying)
					VoicePlayClickListener.currentPlayListener
							.stopPlayVoice();
				if (recordingContainer!=null) {
					recordingContainer.setVisibility(View.VISIBLE);
				}
				recordingHint.setText(mContext.getString(R.string.move_up_to_cancel));
				recordingHint.setBackgroundColor(Color.TRANSPARENT);
				voiceRecorder.startRecording(null, toChatUsername,mContext);
			} catch (Exception e) {
				e.printStackTrace();
				v.setPressed(false);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();   
				if (recordingContainer!=null) {
					recordingContainer.setVisibility(View.INVISIBLE);
				}
				Toast.makeText(mContext, R.string.recoding_fail,Toast.LENGTH_SHORT).show();
				return false;
			}

			return true;   
		case MotionEvent.ACTION_MOVE: {
			if (event.getY() < 0) {
				recordingHint
						.setText(mContext.getString(R.string.release_to_cancel));
				recordingHint
						.setBackgroundResource(R.drawable.recording_text_hint_bg);
			} else {
				recordingHint
						.setText(mContext.getString(R.string.move_up_to_cancel));
				recordingHint.setBackgroundColor(Color.TRANSPARENT);
			}
			return true;
		}
		case MotionEvent.ACTION_UP:
			v.setPressed(false);
			if (recordingContainer!=null) {
				recordingContainer.setVisibility(View.INVISIBLE);
			}
			if (wakeLock.isHeld())
				wakeLock.release();
			if (event.getY() < 0) {
				// discard the recorded audio.
				voiceRecorder.discardRecording();

			} else {
				// stop recording and send voice file
//				String st1 = mContext.getResources().getString(
//						R.string.Recording_without_permission);
//				String st2 = mContext.getResources().getString(
//						R.string.The_recording_time_is_too_short);
//				String st3 =mContext. getResources().getString(
//						R.string.send_failure_please);
//				try {
					voiceRecorder.stopRecoding();
//					if (length > 0) {
////						sendListener(voiceRecorder.getVoiceFilePath(),voiceRecorder.getVoiceFileName(toChatUsername),Integer.toString(length), false);
////						sendVoice(voiceRecorder.getVoiceFilePath(),
////								voiceRecorder.getVoiceFileName(toChatUsername),
////								Integer.toString(length), false);
//					} else if (length == EMError.INVALID_FILE) {
//						Toast.makeText(mContext, st1,
//								Toast.LENGTH_SHORT).show();
//					} else {
//						Toast.makeText(mContext, st2,
//								Toast.LENGTH_SHORT).show();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					Toast.makeText(mContext, st3,
//							Toast.LENGTH_SHORT).show();
//				}

			}
			return true;
		default:
			if (recordingContainer!=null) {
				recordingContainer.setVisibility(View.INVISIBLE);
			}
			if (voiceRecorder != null)
				voiceRecorder.discardRecording();
			return false;
		}
	}	
	/**
	 * 发送语音回调
	 * 
	 * @param filePath 录音文件路径
	 * @param fileName 录音文件名
	 * @param length 录音时长
	 * @param result  翻译结果
	 * @param isResend 是否从新发
	 */
	public abstract void sendListener(String filePath, String fileName, long length,String result, boolean isResend);
	public void discardRecording(){
		
		try {
			// 停止录音
			if (voiceRecorder!=null&&voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
	}
	
}
