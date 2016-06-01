package com.aliamauri.meat.db.Dynamic_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aliamauri.meat.utils.UIUtils;

public class DbOpenHelper_dynamic extends SQLiteOpenHelper {
	
	private final String TAG = "DbOpenHelper_dynamic";
	private static final String DATABASE_NAME = "dynamic_limao_cache.db";
	private static int DATABASE_VERSION=1;
		
			

	private DbOpenHelper_dynamic(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	private static DbOpenHelper_dynamic instance;
	public static DbOpenHelper_dynamic getInstance(){
		if(instance == null){
			synchronized (DbOpenHelper_dynamic.class) {
				if(instance == null){
					instance = new DbOpenHelper_dynamic(UIUtils.getContext());
				}
			}
		}
		return instance;
	}
	
	/**
	 * 创建动态展示列表
	 */
	private static final String HOME_TABLE_CREATE = "CREATE TABLE "
			+ DynamicShowDao.TABLE_NAME_HOME + " (" 
			+ DynamicShowDao.COLUMN_NAME_AUTOID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DynamicShowDao.COLUMN_NAME_ID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_UPDATETYPE + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_CREATETIME + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_DISTANCE + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_DZ + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_FACE + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_LOCALFACE + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ISZAN + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_INFOS + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ISNM + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ISOPEN + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_JD + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_NICKNAME + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_PJ + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_RELINFO + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_TAGS + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_MMTYPE + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_TYPE + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_UID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_WD + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_YD + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ZF + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ZFINFO + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_IMGS + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_VIDEOS + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_VOICES + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_SPARE1 + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_SPARE2 + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_SPARE3 + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ZFINFORX + " TEXT );";
	/**
	 * 创建转发动态数据列表
	 */
	private static final String RELAY_DYNAMIC_TABLE_CREATE = "CREATE TABLE "
			+ DynamicShowDao.TABLE_NAME_RELAY_DYNAMIC + " (" 
			+ DynamicShowDao.COLUMN_NAME_AUTOID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DynamicShowDao.COLUMN_NAME_ID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_CREATETIME + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_FLID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_FNICKNAME + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_FUID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_OLID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ONICKNAME + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_OUID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_DZ + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_INFOS + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ISNM + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ISOPEN + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_JD + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_PJ + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_RELINFO + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_TAGS + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_TYPE + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_UID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_WD + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_YD + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ZF + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_ZFINFO + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_IMGS + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_VIDEOS + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_VOICES + " TEXT );";
	
	/**
	 * 创建相册集合列表
	 */
	private static final String IMAGS_TABLE_CREATE = "CREATE TABLE "
			+ DynamicShowDao.TABLE_NAME_IMGS+" ("
			+ DynamicShowDao.COLUMN_NAME_AUTOID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DynamicShowDao.COLUMN_NAME_ID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_IMG + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_LOCALIMG + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_IMGORI + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_LOCALIMGORI + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_INFO + " TEXT );";
	
	/**
	 * 创建视频数据表
	 */
	private static final String VIDEOS_TABLE_CREATE = "CREATE TABLE "
			+ DynamicShowDao.TABLE_NAME_VIDEOS+" ("
			+ DynamicShowDao.COLUMN_NAME_AUTOID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DynamicShowDao.COLUMN_NAME_ID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_SRC + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_LOCALSRC + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_SRCBG + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_LOCALSRCBG + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_FILESIZE + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_SC + " TEXT );";
	
	/**
	 * 创建语音数据表
	 */
	private static final String VOICES_TABLE_CREATE = "CREATE TABLE "
			+ DynamicShowDao.TABLE_NAME_VOICES+" ("
			+ DynamicShowDao.COLUMN_NAME_AUTOID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DynamicShowDao.COLUMN_NAME_ID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_SRC + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_LOCALSRC + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_SRCBG + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_VOICETEXT + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_LOCALSRCBG + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_FILESIZE + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_SC + " TEXT );";
	
	/**
	 * 创建索引表，分别保存，最新动态最热动态，朋友圈的id
	 */
	private static final String INDEX_TABLE_CREATE = "CREATE TABLE "
			+ DynamicShowDao.TABLE_NAME_INDEX+" ("
			+ DynamicShowDao.COLUMN_NAME_AUTOID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DynamicShowDao.COLUMN_NAME_NEW_DYNAMIC_ID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_HOT_DYNAMIC_ID + " TEXT, "
			+ DynamicShowDao.COLUMN_NAME_FRIEND_DYNAMIC_ID + " TEXT );";
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(INDEX_TABLE_CREATE);
		db.execSQL(HOME_TABLE_CREATE);
		db.execSQL(RELAY_DYNAMIC_TABLE_CREATE);
		db.execSQL(IMAGS_TABLE_CREATE);
		db.execSQL(VIDEOS_TABLE_CREATE);
		db.execSQL(VOICES_TABLE_CREATE);
		

	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	public void closeDB() {
		if (instance != null) {
			try {
				SQLiteDatabase db = instance.getReadableDatabase();
				if(db.isOpen()){
					db.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			instance = null;
		}
	}
	

}
