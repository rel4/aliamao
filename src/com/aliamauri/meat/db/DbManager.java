package com.aliamauri.meat.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 操作数据库
 * 
 * @author admin
 * 
 */
public class DbManager {
	private DbManager() {
		dbHelper = DbOpenHelper.getInstance();
	}

	private static final String FLAG_SPLIT = "$";// 切割符
	private static final String FLAG_LASH = "^";// 装换斜杠
	private static DbManager instance;
	private DbOpenHelper dbHelper;

	public static DbManager getInstance() {
		if (instance == null) {
			instance = new DbManager();
		}
		return instance;
	}

	/**
	 * 保存单个临时上传文件
	 * 
	 * @param fileUpBeanTask
	 */
	public void saveTempFileTask(TempFileUpBeanTask fileUpBeanTask) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(TempUploadFileDao.COLUMN_NAME_TIME,
					fileUpBeanTask.getSubmitTime());
			values.put(TempUploadFileDao.COLUMN_NAME_TEXT_FILES,
					fileUpBeanTask.getFileTypeText());
			values.put(TempUploadFileDao.COLUMN_NAME_VIDEO_FILES,
					Dbutils.jionFileName(fileUpBeanTask.getFileTypeVideo()));
			values.put(TempUploadFileDao.COLUMN_NAME_AUDIO_FILES,
					Dbutils.jionFileName(fileUpBeanTask.getFileTypeAudio()));
			values.put(TempUploadFileDao.COLUMN_NAME_IMAGE_FILES,
					Dbutils.jionFileName(fileUpBeanTask.getFileTypeImage()));
			db.insert(TempUploadFileDao.TABLE_NAME, null, values);
		}
	}

	/**
	 * 获取临时上传文件数据
	 * 
	 * @param submitTime
	 * @return
	 */
	public TempFileUpBeanTask getTempFileTask(String submitTime) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from "
					+ TempUploadFileDao.TABLE_NAME + " where "
					+ TempUploadFileDao.COLUMN_NAME_TIME + "=?",
					new String[] { submitTime });
			TempFileUpBeanTask tempFileUpBeanTask = null;
			while (cursor.moveToNext()) {
				tempFileUpBeanTask = new TempFileUpBeanTask();
				tempFileUpBeanTask
						.setFileTypeText(cursor.getString(cursor
								.getColumnIndex(TempUploadFileDao.COLUMN_NAME_TEXT_FILES)));
				tempFileUpBeanTask
						.setFileTypeAudio(Dbutils.splitFileName(cursor.getString(cursor
								.getColumnIndex(TempUploadFileDao.COLUMN_NAME_AUDIO_FILES))));
				tempFileUpBeanTask
						.setFileTypeImage(Dbutils.splitFileName(cursor.getString(cursor
								.getColumnIndex(TempUploadFileDao.COLUMN_NAME_IMAGE_FILES))));
				tempFileUpBeanTask
						.setFileTypeVideo(Dbutils.splitFileName(cursor.getString(cursor
								.getColumnIndex(TempUploadFileDao.COLUMN_NAME_VIDEO_FILES))));
				tempFileUpBeanTask.setSubmitTime(cursor.getString(cursor
						.getColumnIndex(TempUploadFileDao.COLUMN_NAME_TIME)));
			}
			cursor.close();
			return tempFileUpBeanTask;
		}
		return null;
	}

	/**
	 * 获取临时上传文件数据
	 * 
	 * @param submitTime
	 * @return
	 */
	public List<TempFileUpBeanTask> getTempFileTaskList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from "
					+ TempUploadFileDao.TABLE_NAME, null);
			List<TempFileUpBeanTask> arrayList = new ArrayList<TempFileUpBeanTask>();
			TempFileUpBeanTask tempFileUpBeanTask = null;
			while (cursor.moveToNext()) {
				tempFileUpBeanTask = new TempFileUpBeanTask();
				tempFileUpBeanTask
						.setFileTypeText(cursor.getString(cursor
								.getColumnIndex(TempUploadFileDao.COLUMN_NAME_TEXT_FILES)));
				tempFileUpBeanTask
						.setFileTypeAudio(Dbutils.splitFileName(cursor.getString(cursor
								.getColumnIndex(TempUploadFileDao.COLUMN_NAME_AUDIO_FILES))));
				tempFileUpBeanTask
						.setFileTypeImage(Dbutils.splitFileName(cursor.getString(cursor
								.getColumnIndex(TempUploadFileDao.COLUMN_NAME_IMAGE_FILES))));
				tempFileUpBeanTask
						.setFileTypeVideo(Dbutils.splitFileName(cursor.getString(cursor
								.getColumnIndex(TempUploadFileDao.COLUMN_NAME_VIDEO_FILES))));
				tempFileUpBeanTask.setSubmitTime(cursor.getString(cursor
						.getColumnIndex(TempUploadFileDao.COLUMN_NAME_TIME)));
				arrayList.add(tempFileUpBeanTask);
			}
			cursor.close();
			return arrayList;
		}
		return null;
	}

	/**
	 * 更新单个文件
	 * 
	 * @param fileUpBean
	 */
	public void upTempFileTask(TempFileUpBeanTask fileUpBeanTask) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(TempUploadFileDao.COLUMN_NAME_TIME,
					fileUpBeanTask.getSubmitTime());

			// if (!TextUtils.isEmpty(fileUpBeanTask.getFileTypeText())) {
			values.put(TempUploadFileDao.COLUMN_NAME_TEXT_FILES,
					fileUpBeanTask.getFileTypeText());
			// }
			// if
			// (!TextUtils.isEmpty(jionFileName(fileUpBeanTask.getFileTypeAudio())))
			// {
			values.put(TempUploadFileDao.COLUMN_NAME_AUDIO_FILES,
					Dbutils.jionFileName(fileUpBeanTask.getFileTypeAudio()));
			// }
			// if
			// (!TextUtils.isEmpty(jionFileName(fileUpBeanTask.getFileTypeVideo())))
			// {
			values.put(TempUploadFileDao.COLUMN_NAME_VIDEO_FILES,
					Dbutils.jionFileName(fileUpBeanTask.getFileTypeVideo()));
			// }
			// if
			// (!TextUtils.isEmpty(jionFileName(fileUpBeanTask.getFileTypeImage())))
			// {
			values.put(TempUploadFileDao.COLUMN_NAME_IMAGE_FILES,
					Dbutils.jionFileName(fileUpBeanTask.getFileTypeImage()));
			// }
			db.update(TempUploadFileDao.TABLE_NAME, values,
					TempUploadFileDao.COLUMN_NAME_TIME + " = ?",
					new String[] { fileUpBeanTask.getSubmitTime() + "" });
		}

	}

	public void deleteTempFileTask(String submitTime) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TempUploadFileDao.TABLE_NAME,
					TempUploadFileDao.COLUMN_NAME_TIME + " = ?",
					new String[] { submitTime });
		}
	}

	
	/**
	 * 拼接文件名称(对外提供)
	 * 
	 * @param fileNames
	 * @return
	 */
	public String jionFileName_p(List<String> fileNames) {
		return Dbutils.jionFileName(fileNames);
	}
	/**
	 * 切分文件名称(对外提供)
	 * 
	 * @param fileNames
	 * @return
	 */
	public List<String> splitFileName_p(String fileNames) {
		return Dbutils.splitFileName(fileNames);
	}


}
