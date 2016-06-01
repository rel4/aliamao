package com.aliamauri.meat.db.dlapp;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aliamauri.meat.utils.UIUtils;
import com.limaoso.phonevideo.db.DbOpenHelper;

public class FindDao {
	public static final String TABLENAME = "app_info";
	public static final String ID = "id";
	public static final String APPID = "appid";
	public static final String DOWNLOADURL = "downloadurl";
	public static final String APPNAME = "appname";
	public static final String APPPACKAGE = "apppackage";
	public static final String STATE = "state";
	public static final String LOCALURL = "localurl";

	public static final String REMARK1 = "remark1";
	public static final String REMARK2 = "remark2";
	public static final String REMARK3 = "remark3";
	private DbOpenHelper dbHelper;

	public FindDao() {
		dbHelper = DbOpenHelper.getInstance(UIUtils.getContext());
	}

	synchronized public long SaveDL(FindInfo info) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = -1;
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(APPID, info.getAppid());
			values.put(DOWNLOADURL, info.getDownloadurl());
			values.put(APPNAME, info.getAppname());
			values.put(APPPACKAGE, info.getApppackage());
			values.put(STATE, info.getState());
			values.put(LOCALURL, info.getLocalurl());

			id = SelectByappId(info.getAppid());
			if (id >= 0) {
				values.put(ID, id);
				id = db.replace(TABLENAME, null, values);
			} else {
				id = db.insert(TABLENAME, null, values);
			}
		}
		return id;
	}

	synchronized public void UpdateState(String appid, int state) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			String sql = "update " + TABLENAME + " set  " + STATE + " = "
					+ state + " where " + APPID + "= " + appid;
			db.execSQL(sql);
		}
	}

	synchronized public long SelectByappId(String appid) {
		long id = -1;
		if (appid == null || "".endsWith(appid.trim())) {
			return id;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLENAME
					+ " where " + APPID + "= " + appid, null);
			while (cursor.moveToNext()) {
				id = cursor.getLong(cursor.getColumnIndex(ID));
			}
		}
		return id;
	}

	synchronized public FindInfo SelectInfoByAppid(String appid) {
		FindInfo info = null;
		if (appid == null || "".endsWith(appid.trim())) {
			return info;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLENAME
					+ " where " + APPID + " = " + appid, null);
			while (cursor.moveToNext()) {
				info = new FindInfo();
				info.setId(cursor.getLong(cursor.getColumnIndex(ID)));
				info.setAppid(cursor.getString(cursor.getColumnIndex(APPID)));
				info.setAppname(cursor.getString(cursor.getColumnIndex(APPNAME)));
				info.setApppackage(cursor.getString(cursor
						.getColumnIndex(APPPACKAGE)));
				info.setDownloadurl(cursor.getString(cursor
						.getColumnIndex(DOWNLOADURL)));
				info.setLocalurl(cursor.getString(cursor
						.getColumnIndex(LOCALURL)));
				info.setState(cursor.getInt(cursor.getColumnIndex(STATE)));
			}
		}
		return info;
	}

	/**
	 * 获取全部播放信息
	 * 
	 * @return
	 */
	synchronized public List<FindInfo> getAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<FindInfo> listInfo = new ArrayList<FindInfo>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLENAME + " ",
					null);
			while (cursor.moveToNext()) {
				FindInfo info = new FindInfo();
				info.setId(cursor.getLong(cursor.getColumnIndex(ID)));
				info.setAppid(cursor.getString(cursor.getColumnIndex(APPID)));
				info.setAppname(cursor.getString(cursor.getColumnIndex(APPNAME)));
				info.setApppackage(cursor.getString(cursor
						.getColumnIndex(APPPACKAGE)));
				info.setDownloadurl(cursor.getString(cursor
						.getColumnIndex(DOWNLOADURL)));
				info.setLocalurl(cursor.getString(cursor
						.getColumnIndex(LOCALURL)));
				info.setState(cursor.getInt(cursor.getColumnIndex(STATE)));
				listInfo.add(info);
			}
			cursor.close();
		}
		return listInfo;
	}
}
