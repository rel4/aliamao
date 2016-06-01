package com.aliamauri.meat.db.telsfriend;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aliamauri.meat.app.db.DbAppOpenHelper;
import com.aliamauri.meat.bean.cont.TelFriend.TelFriends;
import com.limaoso.phonevideo.db.DbOpenHelper;

public class TelsFriendDao {
	public static final String TABLENAME = "db_telsfriend";
	public static final String MYID = "myid";
	public static final String ID = "id";
	public static final String FACE = "face";
	public static final String UID = "uid";
	public static final String NICKNAME = "nickname";
	public static final String IS_FRIEND = "is_friend";
	public static final String TEL = "tel";
	public static final String TITLE = "title";
	public static final String TELNAME = "telName";

	public static final String REMARK1 = "remark1";
	public static final String REMARK2 = "remark2";
	public static final String REMARK3 = "remark3";
	private DbAppOpenHelper dbHelper;
	public static TelsFriendDao telsFriend;

	public static synchronized TelsFriendDao getInstance() {
		if (telsFriend == null) {
			telsFriend = new TelsFriendDao();
		}
		return telsFriend;
	}

	private TelsFriendDao() {
		dbHelper = DbAppOpenHelper.getInstance();
	}

	synchronized public long SaveDL(TelFriends info) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = -1;
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(ID, info.id);
			values.put(FACE, info.face);
			values.put(UID, info.uid);
			values.put(NICKNAME, info.nickname);
			values.put(IS_FRIEND, info.is_friend);
			values.put(TEL, info.tel);
			values.put(TITLE, info.title);
			values.put(TELNAME, info.telName);

			id = SelectByTEL(info.tel);
			if (id >= 0) {
				values.put(MYID, id);
				id = db.replace(TABLENAME, null, values);
			} else {
				id = db.insert(TABLENAME, null, values);
			}
		}
		return id;
	}

	synchronized public long SelectByTEL(String tel) {
		long idpos = -1;
		if (tel == null || "".equals(tel)) {
			return idpos;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLENAME
					+ " where " + TEL + "= " + tel, null);
			while (cursor.moveToNext()) {
				idpos = cursor.getLong(cursor.getColumnIndex(MYID));
			}
		}
		return idpos;
	}

	synchronized public TelFriends SelectInfoById(String id) {
		TelFriends info = null;
		if (id == null || "".endsWith(id.trim())) {
			return info;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLENAME
					+ " where " + ID + " = " + id, null);
			while (cursor.moveToNext()) {
				info = new TelFriends();
				info.myid = cursor.getLong(cursor.getColumnIndex(MYID));
				info.id = cursor.getString(cursor.getColumnIndex(ID));
				info.face = cursor.getString(cursor.getColumnIndex(FACE));
				info.uid = cursor.getString(cursor.getColumnIndex(UID));
				info.nickname = cursor.getString(cursor
						.getColumnIndex(NICKNAME));
				info.is_friend = cursor.getString(cursor
						.getColumnIndex(IS_FRIEND));
				info.tel = cursor.getString(cursor.getColumnIndex(TEL));
				info.title = cursor.getString(cursor.getColumnIndex(TITLE));
				info.telName = cursor.getString(cursor.getColumnIndex(TELNAME));

			}
		}
		return info;
	}

	/**
	 * 获取全部
	 * 
	 * @return
	 */
	synchronized public List<TelFriends> getAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<TelFriends> listInfo = new ArrayList<TelFriends>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLENAME + " ",
					null);
			while (cursor.moveToNext()) {
				TelFriends info = new TelFriends();
				info.myid = cursor.getLong(cursor.getColumnIndex(MYID));
				info.id = cursor.getString(cursor.getColumnIndex(ID));
				info.face = cursor.getString(cursor.getColumnIndex(FACE));
				info.uid = cursor.getString(cursor.getColumnIndex(UID));
				info.nickname = cursor.getString(cursor
						.getColumnIndex(NICKNAME));
				info.is_friend = cursor.getString(cursor
						.getColumnIndex(IS_FRIEND));
				info.tel = cursor.getString(cursor.getColumnIndex(TEL));
				info.title = cursor.getString(cursor.getColumnIndex(TITLE));
				info.telName = cursor.getString(cursor.getColumnIndex(TELNAME));

				listInfo.add(info);
			}
			cursor.close();
		}
		return listInfo;
	}
}
