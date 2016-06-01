package com.aliamauri.meat.db.localvideo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aliamauri.meat.app.db.DbAppOpenHelper;
import com.limaoso.phonevideo.db.DbOpenHelper;

public class LocalVideoDao {
	public static final String TABLENAME = "db_localvideo";
	public static final String ID = "id";
	public static final String VIDEOID = "videoId";
	public static final String TITLE = "title";
	public static final String ALBUM = "album";
	public static final String ARTIST = "artist";
	public static final String DISPLAYNAME = "displayName";
	public static final String MIMETYPE = "mimeType";
	public static final String PATH = "path";
	public static final String SIZE = "size";
	public static final String DURATION = "duration";
	public static final String IMGPATH = "imgPath";

	public static final String REMARK1 = "remark1";
	public static final String REMARK2 = "remark2";
	public static final String REMARK3 = "remark3";
	private DbAppOpenHelper dbHelper;
	public static LocalVideoDao telsFriend;

	public static synchronized LocalVideoDao getInstance() {
		if (telsFriend == null) {
			telsFriend = new LocalVideoDao();
		}
		return telsFriend;
	}

	private LocalVideoDao() {
		dbHelper = DbAppOpenHelper.getInstance();
	}

	synchronized public long SaveDL(LocalVideo info) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = -1;
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(VIDEOID, info.videoId);
			values.put(TITLE, info.title);
			values.put(ALBUM, info.album);
			values.put(ARTIST, info.artist);
			values.put(DISPLAYNAME, info.displayName);
			values.put(TITLE, info.title);
			values.put(MIMETYPE, info.mimeType);
			values.put(PATH, info.path);
			values.put(SIZE, info.size);
			values.put(DURATION, info.duration);
			values.put(IMGPATH, info.imgPath);
			id = SelectVidoId(info.videoId);
			if (id >= 0) {
				// values.put(ID, id);
				// id = db.replace(TABLENAME, null, values);
			} else {
				id = db.insert(TABLENAME, null, values);
			}
		}
		return id;
	}

	synchronized public long SelectVidoId(int videoId) {
		long idpos = -1;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLENAME
					+ " where " + VIDEOID + "= " + videoId, null);
			while (cursor.moveToNext()) {
				idpos = cursor.getLong(cursor.getColumnIndex(ID));
			}
		}
		return idpos;
	}

	synchronized public LocalVideo SelectInfoByVideoId(int VideoId) {
		LocalVideo info = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLENAME
					+ " where " + VIDEOID + " = " + VideoId, null);
			while (cursor.moveToNext()) {
				info = new LocalVideo();
				info.id = cursor.getLong(cursor.getColumnIndex(ID));
				info.videoId = cursor.getInt(cursor.getColumnIndex(VIDEOID));
				info.title = cursor.getString(cursor.getColumnIndex(TITLE));
				info.album = cursor.getString(cursor.getColumnIndex(ALBUM));
				info.artist = cursor.getString(cursor.getColumnIndex(ARTIST));
				info.displayName = cursor.getString(cursor
						.getColumnIndex(DISPLAYNAME));
				info.mimeType = cursor.getString(cursor
						.getColumnIndex(MIMETYPE));
				info.path = cursor.getString(cursor.getColumnIndex(PATH));
				info.imgPath = cursor.getString(cursor.getColumnIndex(IMGPATH));
				info.size = cursor.getLong(cursor.getColumnIndex(SIZE));
				info.duration = cursor.getString(cursor
						.getColumnIndex(DURATION));
			}
		}
		return info;
	}

	/**
	 * 获取全部
	 * 
	 * @return
	 */
	synchronized public List<LocalVideo> getAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<LocalVideo> listInfo = new ArrayList<LocalVideo>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLENAME + " ",
					null);
			while (cursor.moveToNext()) {
				LocalVideo info = new LocalVideo();
				info.id = cursor.getLong(cursor.getColumnIndex(ID));
				info.videoId = cursor.getInt(cursor.getColumnIndex(VIDEOID));
				info.title = cursor.getString(cursor.getColumnIndex(TITLE));
				info.album = cursor.getString(cursor.getColumnIndex(ALBUM));
				info.artist = cursor.getString(cursor.getColumnIndex(ARTIST));
				info.displayName = cursor.getString(cursor
						.getColumnIndex(DISPLAYNAME));
				info.mimeType = cursor.getString(cursor
						.getColumnIndex(MIMETYPE));
				info.path = cursor.getString(cursor.getColumnIndex(PATH));
				info.imgPath = cursor.getString(cursor.getColumnIndex(IMGPATH));
				info.size = cursor.getLong(cursor.getColumnIndex(SIZE));
				info.duration = cursor.getString(cursor
						.getColumnIndex(DURATION));

				listInfo.add(info);
			}
			cursor.close();
		}
		return listInfo;
	}
}
