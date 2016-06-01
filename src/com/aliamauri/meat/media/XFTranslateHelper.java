package com.aliamauri.meat.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.aliamauri.meat.parse.JsonParser;
import com.aliamauri.meat.utils.UIUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

/**
 * 讯飞的翻译
 * @author admin
 *
 */
public class XFTranslateHelper {
	
	public XFTranslateListenter listener;
	public interface XFTranslateListenter {
		void onResult(String result);
	}
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	private XFTranslateHelper(){}
	private SpeechRecognizer mIat;
	private String voiceFilePath;//文件的路径
	protected String TAG=XFTranslateHelper.class.getName();
	private static XFTranslateHelper instace =new XFTranslateHelper();
	public static  XFTranslateHelper getInstance(){
		return instace;
	}
	/**
	 * 设置翻译文件的路径
	 */
	private void setFilePath(String filePath){
		voiceFilePath=filePath;
//		initXF();
		setParam();
	}
	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	private void setParam() {
		if (mIat==null) {
			mIat = SpeechRecognizer.createRecognizer(UIUtils.getContext(), null);
		}
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
		
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "10000");
		
		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, "1");
		
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"pcm");
//		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
		
		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA, "0");
		mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
		mIat.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
		mIat.stopListening();
	}
	int ret;
	public void startTranslate(String path, XFTranslateListenter listener){
		ResultText="";
		this.listener =listener;
		//初始化
		setFilePath(path);
		ret = mIat.startListening(mRecoListener);
		if (ret != ErrorCode.SUCCESS) {
//			showTip("识别失败,错误码：" + ret);
			Log.e(TAG, "识别失败,错误码：" + ret);
		} else {
//			byte[] audioData = FucUtil.readAudioFile(IatDemo.this, "1.wav");
			File file = new File(voiceFilePath);
			if (!file.exists()) {
				Log.e(TAG, file.getName()+"  路径 ： "+voiceFilePath);
				return;
			}
			Log.e(TAG, "开始翻译");
			byte[] audioData = readAudioFileFromSd( voiceFilePath);
			
			if (null != audioData) {
//				showTip(getString(R.string.text_begin_recognizer));
				// 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，云端都支持），位长16bit，单声道的wav或者pcm
				// 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstantSAMPLE_RATE., "8000")设置正确的采样率
				// 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别
				mIat.writeAudio(audioData, 0, audioData.length);
				mIat.stopListening();
			} else {
				mIat.cancel();
				Log.e(TAG, "读取音频流失败");
//				showTip("读取音频流失败");
			}
		}
	}
	private  String ResultText;
	// 听写监听器
	private RecognizerListener mRecoListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
//			if (recordingContainer != null) {
//				recordingContainer.setVisibility(View.VISIBLE);
//			}

		}

		@Override
		public void onEndOfSpeech() {
//			if (recordingContainer != null) {
//				recordingContainer.setVisibility(View.INVISIBLE);
//			}
		}

		@Override
		public void onError(SpeechError arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onError----->" + arg0.toString());
			if (listener!=null) {
				listener.onResult("您说什么呢,我没听明白?");
			}
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onEvent----->" + arg0 + "");
		}

		@Override
		public void onResult(RecognizerResult arg0, boolean isLast) {
			Log.e(TAG, "onResult解析前----->" + arg0.getResultString());
			String text = JsonParser.parseIatResult(arg0.getResultString());
			Log.e(TAG, "onResult解析后----->" + text);
			if (isLast) {
				if (listener!=null) {
					if (TextUtils.isEmpty(ResultText)) {
						ResultText="您说什么呢,我没听明白?";
					}
					listener.onResult(ResultText);
				}
			}else {
				ResultText=ResultText+text;
			}
		}

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onVolumeChanged----->" + arg0 + "");
//			if (micImages != null) {
//				micImageHandler.sendEmptyMessage(arg0);
//			}
		}
	};
	/**
	 * 读取信息
	 * @param path
	 * @return
	 */
	public static byte[] readAudioFileFromSd(String path) {
		try {
			File file = new File(path);
			InputStream ins=new FileInputStream(file);
			byte[] data = new byte[ins.available()];
			
			ins.read(data);
			ins.close();
			
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	
}
}
