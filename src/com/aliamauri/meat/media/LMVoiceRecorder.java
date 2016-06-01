package com.aliamauri.meat.media;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AmrInputStream;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import com.aliamauri.meat.listener.LMPressToSpeakListenr;
import com.aliamauri.meat.media.XFTranslateHelper.XFTranslateListenter;
import com.aliamauri.meat.media.utils.Pcm2Wav;
import com.easemob.util.PathUtil;

public class LMVoiceRecorder {
	private String TAG = "LMAudioRecord";
	
	
	private AudioRecord audioRecord = null;
    private boolean isRecording = false;
    
    private String pcmFilePath = null;
    
    private String wavFilePath = null;
    
    private OutputStream os = null;
    
    private BufferedOutputStream bos = null;
    
    private DataOutputStream dos = null;
    //录音时长
    private long voiceTimeLength;
	private Handler micImageHandler;
    
    public LMVoiceRecorder(Handler micImageHandler) {
    	this.micImageHandler=micImageHandler;
	}
    /**
     * 初始化
     * @param toUserName
     */
	public void init(String toUserName){
    	  pcmFilePath = getVoiceFilePath(toUserName, VoiceFileType.FIIE_TYPE_PCM);
    			  
          wavFilePath =getVoiceFilePath(toUserName, VoiceFileType.FILE_TYPE_WAV);
    }
	
	public void startRecording(Object object, String toChatUsername, Context mContext)
	{
		init(toChatUsername);
		  new Thread(new Runnable(){

              @Override
              public void run()
              {
                  startRecording();

              }
              
          }).start();
	}
	
    public void startRecording()
    {
    	
        int frequency = 8000;
//        int frequency = 16000;
        int channel = AudioFormat.CHANNEL_IN_MONO;
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize = AudioRecord.getMinBufferSize(frequency,
                channel,
                audioEncoding);
        
        try
        {
            File file = new File(pcmFilePath);
            if (file.exists())
            { 
//            	file.mkdirs();
            	File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
            	file.renameTo(to);
            	to.delete();  
            }
            file.createNewFile();
          
            
            // New a DataOuputStream to write the audio data into the file.
            os = new FileOutputStream(file);
            bos = new BufferedOutputStream(os);
            dos = new DataOutputStream(bos);
            
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, //指定音频来源，这里为麦克风
                    frequency, //8000HZ采样频率
                    channel, //录制通道
                    audioEncoding, //录制编码格式
                    bufferSize); //录制缓冲区大小
            
            short[] buffer = new short[bufferSize];
            audioRecord.startRecording();
            
            isRecording = true;
            Log.e(TAG, "录音开始");
            voiceTimeLength=0;
          
            long startTime=  System.currentTimeMillis();
            while (isRecording)
            {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++)
                    dos.write(getBytes(buffer[i]));//把short转成byte[]，注意大头还是小头
                volumeChangeSize(buffer, bufferReadResult);
            }
            audioRecord.stop();
            audioRecord.release();
            voiceTimeLength=System.currentTimeMillis()-startTime;
            Log.e(TAG, "录音结束");
            Log.e(TAG, "录音路径  ："+file.getAbsolutePath()+"   文件大小"+file.length());
            String pcm2amr = PCM2AMR();
            if (TextUtils.isEmpty(pcm2amr)) {
				pcm2amr=wavFilePath;
			}
            startXFTranslate(file.getAbsolutePath(),pcm2amr);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to recording pcm File:" + e.getMessage());
        }
        finally{
        	 try
             {
                 if (dos != null)
                 {	
                     dos.close();
                     dos = null;
                 }
                 if (bos != null)
                 {
                     bos.close();
                     bos = null;
                 }
                 if (os != null)
                 {
                     os.close();
                     os = null;
                 }
             }
             catch (IOException e)
             {
                 e.printStackTrace();
             }
        }
       
        
    }
    /**
     * 声音大小改变
     * @param buffer
     * @param r
     */
    private void volumeChangeSize(short[] buffer,int r){
    	  long v = 0;  
          // 将 buffer 内容取出，进行平方和运算  
          for (int i = 0; i < buffer.length; i++) {  
              v += buffer[i] * buffer[i];  
          }  
          // 平方和除以数据总长度，得到音量大小。  
          double mean = v / (double) r;  
          int volume = (int) (10 * Math.log10(mean)); 
          if (volume>=40&&volume<=85) {
        	  if (micImageHandler!=null) {
        		  Message message = micImageHandler.obtainMessage();
        		  message.obj=volume-40;
        		  message.what=LMPressToSpeakListenr.VOLUME_CHANGE_SIZE;
        		  micImageHandler.sendMessage(message);
			}
		}
          Log.d(TAG, "分贝值:" + volume);  
    }	
    /**
     * 开始讯飞语音翻译
     * @param file
     */
    private void startXFTranslate(final String pcmFilePath,final String amrFilePath) {
		XFTranslateHelper.getInstance().startTranslate(pcmFilePath,new XFTranslateListenter() {
			@Override
			public void onResult(String result) {
				if (micImageHandler!=null) {
					Message message = micImageHandler.obtainMessage();
					Map<VoiceFileType, String> hashMap = new HashMap<VoiceFileType, String>();
					hashMap.put(VoiceFileType.FILE_TYPE_AMR,amrFilePath);
					hashMap.put(VoiceFileType.FILE_TYPE_XF_TEXT,result );
					hashMap.put(VoiceFileType.FILE_TYPE_AMR_TIME, voiceTimeLength+"");
					message.what=LMPressToSpeakListenr.VOICE_RESULT;
					message.obj=hashMap;
					//删除pcm文件
					File file = new File(pcmFilePath);
					if (file.exists()) {
						file.delete();
					}
					micImageHandler.sendMessage(message);
					
				}
				Log.e(TAG, "翻译结果 ： "+result);
			}
		});
		
	}
    /**
     * PCM转化为AMR
     */
    private String PCM2AMR(){
    	 String path;
    	 try {
    		 pcm2wav();
    		 path = wav2amr(wavFilePath);
			Log.e(TAG,"amr文件路径   ：  "+ path);
			return path;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 return null;
    }
	public byte[] getBytes(short s)
    {
        byte[] buf = new byte[2];
        for (int i = 0; i < buf.length; i++)
        {
            buf[i] = (byte) (s & 0x00ff);
            s >>= 8;
        }
        return buf;
    }
    private void stopRecord()
    {
        isRecording = false;
//        discardRecording();
    }
    
    private void pcm2wav()
    {
        Pcm2Wav tool = new Pcm2Wav();
        try
        {
        	Log.e(TAG, "wavFilePath "+wavFilePath);
            tool.convertAudioFiles(pcmFilePath, wavFilePath);
        }
        catch (Exception e)
        {
            Log.e(TAG, "pcm failed to convert into wav File:" + e.getMessage());
        }
    }
    
    public String wav2amr(String wavFileName) throws IOException
    {
        InputStream inStream;
        inStream = new FileInputStream(wavFileName);
        AmrInputStream aStream = new AmrInputStream(inStream);
        
        File file = new File(wavFileName.replace(".wav", ".amr"));
        file.createNewFile();
        OutputStream out = new FileOutputStream(file);
        
        byte[] x = new byte[1024];
        int len;
        out.write(0x23);
        out.write(0x21);
        out.write(0x41);
        out.write(0x4D);
        out.write(0x52);
        out.write(0x0A);
        Log.e(TAG, "开始转换");
        while ((len = aStream.read(x)) > 0)
        {
            out.write(x, 0, len);
        }
        
        out.close();
        aStream.close();
        Log.e(TAG, "结束转换");
        Log.e(TAG, "amr文件大小  ： "+file.length());
        //删除wav文件
        File wavFile = new File(wavFileName);
        if (wavFile.exists()) {
			wavFile.delete();
		}
        return file.getAbsolutePath();
    }
    /**
     * 销毁录音器
     */
	public void discardRecording() {
		if (audioRecord!=null) {
			isRecording=false;
			audioRecord.stop();
			audioRecord.release();
			audioRecord=null;
		}
		
	}
	/**
	 * 停止录音
	 * @return
	 */
	public void stopRecoding() {
		 stopRecord();
		
	}
	/**
	 * 是否正在录音
	 * @return
	 */
	public boolean isRecording() {
		return isRecording;
	}
	/**
	 * 获取录音文件的路径
	 * @param toFromName
	 * @param flieName
	 * @return
	 */
	public String getVoiceFilePath(String toFromName, VoiceFileType fileType ) {
		Time localTime = new Time();
		localTime.setToNow();
		File file = PathUtil.getInstance().getVoicePath();
		if (!file.exists()) {
			file.mkdirs();
		}
		String fileName = "";
		switch (fileType) {
		case FILE_TYPE_AMR:
			fileName=".amr";
			break;
		case FILE_TYPE_WAV:
			fileName=".wav";
			break;
		case FIIE_TYPE_PCM:
			fileName=".pcm";
			break;
		}
		return  file+ File.separator
				+ toFromName + localTime.toString().substring(0, 15) + fileName;
	}
	public enum VoiceFileType{
		FILE_TYPE_AMR,
		FILE_TYPE_WAV,
		FIIE_TYPE_PCM,
		//讯飞翻译文本
		FILE_TYPE_XF_TEXT,
		//录音的时长
		FILE_TYPE_AMR_TIME;
	}
    
}
