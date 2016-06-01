package com.aliamauri.meat.db.Dynamic_db;

import java.io.File;
import java.util.ArrayList;

import com.aliamauri.meat.db.Dynamic_db.model.HomeTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.eventBus.sendDownLoadedOrder;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpInterface.LoadRequestCallBack;
import com.aliamauri.meat.utils.UIUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import de.greenrobot.event.EventBus;
/**
 * 下载 语音，视频，图片数据到数据库中
 * @author limaokeji-windosc
 *
 */
public class DownloadFileTools implements LoadRequestCallBack<File> {
	private static final String TAG = "DOWNLOADFILETOOLS";
	private String mId;   //该条数据的id值
	private int mTableTag;  //要操作的数据库表
	private String mType;//通过该字段值来确定哪个类调用了该方法
	

	public DownloadFileTools(String id, int TableTag, String type) {
		this.mId = id;
		this.mTableTag = TableTag;
		this.mType = type;
	}

	@Override
	public void onSucceed(ResponseInfo<File> file) {
		switch (mTableTag) {
		case GlobalConstant.IMAGE_TABLE:
			updateLocalPathToDB_img(file.result);
			break;
		case GlobalConstant.DYNAMIC_TABLE:
			updateLocalPathToDB_home(file.result);
			break;
		case GlobalConstant.VIDEO_TABLE:
			updatelocalpathtodb_videos(file.result);
			break;
		case GlobalConstant.VOICE_TABLE:
			updateLocalPathToDB_voices(file.result);
			break;

		default:
			break;
		}
	}
	/**
	 * 更新本地file路径到动态表中
	 * @param result
	 */
	private void updateLocalPathToDB_home(File result) {
		HomeTable_model home = new HomeTable_model();
		home.setLocalFace(result.getAbsolutePath());
		
		DynamicShowDao.getInstance().singleItemUpdate(GlobalConstant.DYNAMIC_TABLE, mId, home, null, null, null, null);
	}
	/**
	 * 更新本地file路径到相册表中
	 * @param result
	 */
	private void updateLocalPathToDB_img(File result) {
		if(result.getAbsolutePath().contains(GlobalConstant.DYNAMIC_SMALLIMAGE_SAVEPATH)){
			Imgs_model img = new Imgs_model();
			img.setLocalImg(result.getAbsolutePath());
			DynamicShowDao.getInstance().singleItemUpdate( GlobalConstant.IMAGE_TABLE, mId, null, img, null, null, null);
		}
		if(result.getAbsolutePath().contains(GlobalConstant.DYNAMIC_BIGIMAGE_SAVEPATH)){
			Imgs_model img = new Imgs_model();
			img.setLocalImgOri(result.getAbsolutePath());
			DynamicShowDao.getInstance().singleItemUpdate( GlobalConstant.IMAGE_TABLE, mId, null, img, null, null, null);
		}
		
	}
	/**
	 * 更新本地file路径到语音表中
	 * @param result
	 */
	private void updateLocalPathToDB_voices(File result) {
		Voices_model voice = new Voices_model();
		voice.setLocalSrc(result.getAbsolutePath());
		if(DynamicShowDao.getInstance().getSingleItem_voice(DynamicShowDao.COLUMN_NAME_ID, new String[]{mId}) !=null){
			DynamicShowDao.getInstance().singleItemUpdate( GlobalConstant.VOICE_TABLE, mId, null, null, null, null, voice);
		}else{
			ArrayList<Voices_model> list = new ArrayList<>();
			voice.setId(mId);
			list.add(voice);
			DynamicShowDao.getInstance().singleItemInsert(null, null, null, null,list);
		}
		EventBus.getDefault().post(new sendDownLoadedOrder(mTableTag,mId,mType));  //使主页开始播放
	}
	/**
	 * 更新本地file路径到视频表中
	 * @param result
	 */
	private void updatelocalpathtodb_videos(File result) {
		Videos_model video = new Videos_model();
		video.setLocalSrcbg(result.getAbsolutePath());
		DynamicShowDao.getInstance().singleItemUpdate( GlobalConstant.VIDEO_TABLE, mId, null, null, null, video, null);
	}
	
	

	@Override
	public void onFailure(HttpException arg0, String arg1) {
		switch (mTableTag) {
		case GlobalConstant.DYNAMIC_TABLE:
			break;
		case GlobalConstant.VIDEO_TABLE:
			break;
		case GlobalConstant.VOICE_TABLE:
			UIUtils.showToast(UIUtils.getContext(), "语音数据下载失败");
			EventBus.getDefault().post(new sendDownLoadedOrder(mTableTag,"Failure",mType));
			break;
		default:
			break;
		}

	}

	@Override
	public void onLoading(long total, long current, boolean isUploading) {
//		Log.e(TAG, "********"+total+"*********"+current+"***********"+isUploading+"*********");
		switch (mTableTag) {
		case GlobalConstant.DYNAMIC_TABLE:
			break;
		case GlobalConstant.VIDEO_TABLE:
			break;
		case GlobalConstant.VOICE_TABLE:
			
			break;
		default:
			break;
		}
	}

}
