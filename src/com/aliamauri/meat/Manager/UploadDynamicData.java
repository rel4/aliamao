package com.aliamauri.meat.Manager;

import java.util.ArrayList;

import android.util.Log;

import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.db.Dynamic_db.DynamicShowDao;
import com.aliamauri.meat.db.Dynamic_db.model.Dynamic_model;
import com.aliamauri.meat.db.Dynamic_db.model.HomeTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.eventBus.UpdateDynamicLists;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.MyBDmapUtlis;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.lidroid.xutils.http.RequestParams;

import de.greenrobot.event.EventBus;

/**
 * 发布后上传动态数据
 * 
 * @author limaokeji-windosc
 * 
 */
public class UploadDynamicData {
	private HttpHelp mHttpHelp;
	private String TAG ="UploadDynamicData";
	private String mId;
	public UploadDynamicData() {
		super();
		mHttpHelp = new HttpHelp();
		
	}
	public String getUploadId(){
		return mId;
	}
	/**
	 * 将后台上传的获取的数据总的提交给服务器
	 * @param id  
	 */
	public void  uploadDataToNet(String id) {
		if(id == null){
			return ;
		}
		MyBDmapUtlis dbUtlis = new MyBDmapUtlis();
		this.mId = id;
		RequestParams params = new RequestParams();
		final Dynamic_model dynamicData = DynamicShowDao.getInstance().getDynamicData(id);
		
		if (dynamicData == null ) {
			return;
		}
		if(dynamicData.isnm !=null){   //是否匿名
			params.addBodyParameter("isnm", dynamicData.isnm);
		}

		if (dynamicData.infos !=null) {   //文字描述信息
			params.addBodyParameter("infos", dynamicData.infos);
		}
		if (dynamicData.type !=null) {   //动态的发布类型
			params.addBodyParameter("type", dynamicData.type);
		}
		if (dynamicData.zf_flid !=null) {   //被转发人的id
			params.addBodyParameter("flid", dynamicData.zf_flid);
		}
		if (dynamicData.wd !=null) {   //当前用户所在纬度
			params.addBodyParameter("wd", dynamicData.wd);
		}
		
		if (dynamicData.jd !=null) {   //当前用户所在经度
			params.addBodyParameter("jd", dynamicData.jd);
		}
		String[] strs = dbUtlis.splitCityNameArray(PrefUtils.getString(GlobalConstant.USER_LOCATION_MSG,null));
		if(strs!=null && strs[0]!=null && !"err".equals(strs[0])){  //省
			params.addBodyParameter("province", strs[0]);
			
		}
		if(strs!=null && strs[1]!=null && !"err".equals(strs[1])){  //市
			params.addBodyParameter("city", strs[1]);
		}
		if(strs!=null && strs[2]!=null && !"err".equals(strs[2])){  //县
			params.addBodyParameter("district", strs[2]);
		}
		
		if (dynamicData.tags !=null) {   //tags标签
			params.addBodyParameter("tags", dynamicData.tags);
		}
		if (dynamicData.imgs !=null && dynamicData.imgs.size()>0) {   //图片数据
			params.addBodyParameter("imgstr", getImgData(dynamicData.imgs));
		}
		if (dynamicData.voices !=null && dynamicData.voices.size()>0) {   //语音数据
			params.addBodyParameter("voicestr", getVoiceData(dynamicData.voices));
		}
		if (dynamicData.videos !=null && dynamicData.videos.size()>0) {   //视频数据
			params.addBodyParameter("videostr", getVideoData(dynamicData.videos));
		}
		mHttpHelp.sendPost(NetworkConfig.getDtAllUrl(), params, BaseBaen.class,
				new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean != null && bean.status !=null && bean.latestid !=null) {
							if ("1".equals(bean.status)) {
								mId = null;
								EventBus.getDefault().post(new UpdateDynamicLists(dynamicData.id,bean.latestid));
								HomeTable_model model = new HomeTable_model();
								model.setId(bean.latestid);
								model.setUpdate_type(GlobalConstant.UPLOADTYPE_3);
								Log.e(TAG, "上传数据成功,评论ID------->"+bean.latestid);
								DynamicShowDao.getInstance().singleItemUpdate(GlobalConstant.DYNAMIC_TABLE, dynamicData.id, model, null, null, null, null);
								DynamicShowDao.getInstance().updateIndexTable(DynamicShowDao.COLUMN_NAME_NEW_DYNAMIC_ID,dynamicData.id,bean.latestid);
								DynamicShowDao.getInstance().updateIndexTable(DynamicShowDao.COLUMN_NAME_FRIEND_DYNAMIC_ID,dynamicData.id,bean.latestid);
								
							}else{	
								mId = null;
								Log.e(TAG, "上传数据失败");
								UIUtils.showToast(UIUtils.getContext(), bean.msg);
							}
						}

					}
				});
	}
	/**
	 * 将视频数据拼接字符串
	 * @param imgs 
	 * @return
	 */
	private String getVideoData(ArrayList<Videos_model> videos) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < videos.size(); i++) {
			builder.append(videos.get(i).getId())
			.append("|||").append(videos.get(i).getSrc())
			.append("|||").append(getSrcbg_img(videos,i))
			.append("|||").append(videos.get(i).getSc())
			.append("|||").append(videos.get(i).getFilesize()).append("$$$");
		}
		return builder.toString();
	}
	private String getSrcbg_img(ArrayList<Videos_model> videos, int i) {
		String srcbg = videos.get(i).getSrcbg();
		if(srcbg !=null){
			return srcbg;
		}
		return "";
	}
	/**
	 * 将语音数据拼接字符串
	 * @param imgs 
	 * @return
	 */
	private String getVoiceData(ArrayList<Voices_model> voices) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < voices.size(); i++) {
			builder.append(voices.get(i).getId())
			.append("|||").append(voices.get(i).getSrc())
			.append("|||").append(voices.get(i).getSrcbg())
			.append("|||").append(voices.get(i).getSc())
			.append("|||").append(voices.get(i).getFilesize()).append("$$$");
		}
		return builder.toString();
	}

	/**
	 * 将照片数据拼接字符串
	 * @param imgs 
	 * @return
	 */
	private String getImgData(ArrayList<Imgs_model> imgs) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < imgs.size(); i++) {
			builder.append(imgs.get(i).getId())
			.append("|||").append(imgs.get(i).getImg())
			.append("|||").append(imgs.get(i).getImgori())
			.append("|||").append(imgs.get(i).getInfo()).append("$$$");
		}
		return builder.toString();
	}

}
