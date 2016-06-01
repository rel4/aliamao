package com.aliamauri.meat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.utils.UIUtils;

public class DbOpenHelper extends SQLiteOpenHelper {

	private static int DATABASE_VERSION = 1;

	private DbOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);

	}

	private static DbOpenHelper instance;

	public static DbOpenHelper getInstance() {
		if (instance == null) {
			instance = new DbOpenHelper(UIUtils.getContext());
		}
		return instance;
	}

	/**
	 * 临时上传文件表
	 */
	private static final String TEMP_UPLOAD_TABLE_CREATE = "CREATE TABLE "
			+ TempUploadFileDao.TABLE_NAME + " ("
			+ TempUploadFileDao.COLUMN_NAME_IMAGE_FILES + " TEXT, "
			+ TempUploadFileDao.COLUMN_NAME_VIDEO_FILES + " TEXT, "
			+ TempUploadFileDao.COLUMN_NAME_AUDIO_FILES + " TEXT, "
			+ TempUploadFileDao.COLUMN_NAME_TEXT_FILES + " TEXT, "
			+ TempUploadFileDao.COLUMN_NAME_TIME + " TEXT PRIMARY KEY );";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TEMP_UPLOAD_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private static String getUserDatabaseName() {
		return SDKHelper.getInstance().getHXId() + "_limao_cache.db";
	}

	public void closeDB() {
		if (instance != null) {
			try {
				SQLiteDatabase db = instance.getWritableDatabase();
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			instance = null;
		}
	}

}
