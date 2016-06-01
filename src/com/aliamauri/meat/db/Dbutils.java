package com.aliamauri.meat.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.text.TextUtils;

import com.aliamauri.meat.db.Dynamic_db.DynamicShowDao;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.utils.StringUtils;

public class Dbutils {
	/**
	 * 拼接文件名称
	 * 
	 * @param fileTypeVideo
	 * @return
	 */
	public static String jionFileName(List<String> fileNames) {
		if (fileNames == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		int size = fileNames.size();
		for (int i = 0; i < size; i++) {
			sb.append(fileNames.get(i));
			if (i != size - 1) {
				sb.append("|");
			}
		}
		String string = sb.toString();
		// string= string.replace("/", FLAG_LASH);
		return string;
	}

	/**
	 * 切分文件名称
	 * 
	 * @param fileTypeVideo
	 * @return
	 */
	public static List<String> splitFileName(String fileNames) {

		if (TextUtils.isEmpty(fileNames)) {
			return null;
		}
		ArrayList<String> arrayList = null;
		String[] split = fileNames.split("\\|");
		for (int i = 0; i < split.length; i++) {
			if (arrayList == null) {
				arrayList = new ArrayList<String>();
			}
			// String str = split[i].replace(FLAG_LASH, "/");
			arrayList.add(split[i]);
		}
		return arrayList;
	}
	
	/**
	 * 切分文件名称
	 * 
	 * @param fileTypeVideo
	 * @return
	 */
	public static String[] splitFileNameArray(String fileNames) {
		
		if (TextUtils.isEmpty(fileNames)) {
			return null;
		}
		return fileNames.split("\\|");
		
	}
	
	/**
	 * 将时间戳格式化
	 * 
	 * @return
	 */
	public String format_date(long date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
		return format.format(new Date(date));
	}

	/**
	 * 判断当前数据库动态中是否照片数据
	 * 
	 * @param string
	 * @param imgs
	 */
	public ArrayList<Imgs_model> getImgsDate_db(String imgId) {
		if(StringUtils.isEmpty(imgId)){
			return null;
		}
		List<String> ids = DbManager.getInstance().splitFileName_p(imgId);
		ArrayList<Imgs_model> imgModels = DynamicShowDao.getInstance()
				.getListItems_imgs(DynamicShowDao.COLUMN_NAME_ID,
						ids.toArray(new String[ids.size()]));
		return imgModels;
	}

	/**
	 * 判断当前数据库动态中是否有语音数据
	 * 
	 * @param string
	 * @param imgs
	 */
	public ArrayList<Voices_model> getVoicesDate_db(String voiceId) {
		if(StringUtils.isEmpty(voiceId)){
			return null;
		}
		List<String> ids = DbManager.getInstance().splitFileName_p(voiceId);
		ArrayList<Voices_model> voiceModels = (ArrayList<Voices_model>) DynamicShowDao
				.getInstance().getListItem_voices(DynamicShowDao.COLUMN_NAME_ID,ids.toArray(new String[ids.size()]));
		return voiceModels;
	}

	/**
	 * 判断当前数据库动态中是否有语音数据
	 * 
	 * @param string
	 * @param imgs
	 */
	public ArrayList<Videos_model> getVideosDate_db(String videoId) {
		if(StringUtils.isEmpty(videoId)){
			return null;
		}
		List<String> ids = DbManager.getInstance().splitFileName_p(videoId);
		ArrayList<Videos_model> videoModels = (ArrayList<Videos_model>) DynamicShowDao
				.getInstance().getListItem_videos(
						DynamicShowDao.COLUMN_NAME_ID,
						ids.toArray(new String[ids.size()]));
		return videoModels;
	}
	
}
