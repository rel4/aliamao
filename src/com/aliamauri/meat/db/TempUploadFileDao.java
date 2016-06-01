package com.aliamauri.meat.db;

import java.util.List;

public class TempUploadFileDao {
	public final static String TABLE_NAME = "temp_file_cache";
	public final static String COLUMN_NAME_TIME = "time";
	public final static String COLUMN_NAME_AUDIO_FILES = "audio_file_list";
	public final static String COLUMN_NAME_VIDEO_FILES = "video_file_list";
	public final static String COLUMN_NAME_IMAGE_FILES = "image_file_list";
	public final static String COLUMN_NAME_TEXT_FILES = "text_file";
	/**
	 * 保存单个上传文件
	 * @param fileUpBean
	 */
	public void saveTempFileTask(TempFileUpBeanTask fileUpBean) {
		DbManager.getInstance().saveTempFileTask(fileUpBean);
	}
	/**
	 * 获取单个上传文件
	 * @param submitTime
	 * @return
	 */
	public TempFileUpBeanTask getTempFileTask(String submitTime){
		return DbManager.getInstance().getTempFileTask(submitTime);
	}
	/**
	 * 获取所有单个文件
	 * @return
	 */
	public List<TempFileUpBeanTask> getTempFileUpTask(){
		return DbManager.getInstance().getTempFileTaskList();
	}
	/**
	 * 删除单个文件
	 * @param submitTime
	 */
	public void deleteTempFileTask(String submitTime){
		DbManager.getInstance().deleteTempFileTask(submitTime);
	}
	/**
	 * 更新单个文件
	 * @param fileUpBean
	 */
	public void upTempFileTask(TempFileUpBeanTask fileUpBean){
		DbManager.getInstance().upTempFileTask(fileUpBean);
	}
}
