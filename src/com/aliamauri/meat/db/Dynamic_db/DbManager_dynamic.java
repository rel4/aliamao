package com.aliamauri.meat.db.Dynamic_db;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aliamauri.meat.db.DbManager;
import com.aliamauri.meat.db.Dbutils;
import com.aliamauri.meat.db.Dynamic_db.model.HomeTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.RelayDynamicTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;

public class DbManager_dynamic {
	private static final String TAG = "DbManager_dynamic";
	private DbOpenHelper_dynamic dbHelper;

	private DbManager_dynamic() {
		dbHelper = DbOpenHelper_dynamic.getInstance();
	}

	public static DbManager_dynamic instance;

	public static DbManager_dynamic getInstance() {
		if (instance == null) {
			synchronized (DbManager_dynamic.class) {
				if (instance == null) {
					instance = new DbManager_dynamic();
				}
			}
		}
		return instance;
	}

	/**
	 * 将索引表中指定的id值更换为新的id值
	 * 
	 * @param pastid
	 *            旧的id值
	 * @param latestid
	 *            新的id值
	 * @param colName
	 *            列名
	 */
	public void updateIndexTable(String colName, String pastid, String latestid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(colName, latestid);
		db.update(DynamicShowDao.TABLE_NAME_INDEX, values, colName + " = ?",
				new String[] { pastid });
	}

	/**
	 * 向数据库中索引表中插入相应的索引值
	 * 
	 * @param columnName
	 *            列名称
	 * @param columnValue
	 *            列值
	 * @param isCloseDb
	 *            是否关闭数据库
	 */
	public void singleItemInsert_index(String columnName, String columnValue) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen() && columnValue != null
				&& getItem_index(db, columnName, columnValue) == null) {
			ContentValues values = new ContentValues();
			values.put(columnName, columnValue);
			long insert = db.insert(DynamicShowDao.TABLE_NAME_INDEX,
					columnName, values);
			values.clear();
			values = null;
			if (insert <= 0) {
				Log.e(TAG, "**********索引表插入动态数据失败********");
			}
		}
	}

	/**
	 * 将最热动态中保存的id清除
	 * 
	 * @param columnName
	 * @param indexs
	 */
	public int singleItemDelete_index(String columnName,
			ArrayList<String> indexs) {
		if (indexs == null || (indexs != null && indexs.size() <= 0)) {
			return 0;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.putNull(columnName);
			return db.update(DynamicShowDao.TABLE_NAME_INDEX, values,
					columnName + " in(" + getParamMark(indexs.size()) + ") ",
					indexs.toArray(new String[indexs.size()]));
		}
		return 0;

	}

	/**
	 * 根据传入的集合大小确定占位符
	 * 
	 * @param counts
	 * 
	 * @return
	 */
	private String getParamMark(int count) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; i++) {
			if (i == count - 1) {
				builder.append("?");
			} else {
				builder.append("?,");
			}
		}
		return builder.toString();
	}

	/**
	 * 获取数据库中索引表最新索引值
	 * 
	 * @param colName
	 *            列名字
	 * @return 有返回id值，没有返回null
	 */

	public String getRecentIndex(String colName) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = null;
			try {
				cursor = db.query(DynamicShowDao.TABLE_NAME_INDEX,
						new String[] { colName }, colName + " is not null",
						null, null, null, colName + " DESC");
				// cursor = db.rawQuery("select "+colName+" from "+
				// DynamicShowDao.TABLE_NAME_INDEX + " where "+ colName +
				// " is not null" , null); 暂时不用
				if (cursor.moveToFirst()) {
					if (!Pattern
							.compile("(?i)[a-z]")
							.matcher(
									cursor.getString(cursor
											.getColumnIndex(colName))).find()) {
						return cursor.getString(cursor.getColumnIndex(colName));
					} else {
						// 如果获取的id含有字母就获取上一个
						while (cursor.moveToNext()) {
							if (!Pattern
									.compile("(?i)[a-z]")
									.matcher(
											cursor.getString(cursor
													.getColumnIndex(colName)))
									.find()) {
								return cursor.getString(cursor
										.getColumnIndex(colName));
							}
						}
					}

				} else {
					return "0";
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}

		return "0";
	}

	/**
	 * 查找索引表中行为空的字段值，进行更新操作
	 * 
	 * @param columnName列名
	 * @param newId
	 *            //更新的id
	 * @return //是否更新成功
	 */
	public boolean singleItemUpdate_index(String columnName, String newValue) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String isNullIndex = getTableIsNull(columnName); // 获取为null的索引
		if (db.isOpen() && isNullIndex != null
				&& getItem_index(db, columnName, newValue) == null) {
			ContentValues values = new ContentValues();
			values.put(columnName, newValue);
			int update = db.update(DynamicShowDao.TABLE_NAME_INDEX, values,
					DynamicShowDao.COLUMN_NAME_AUTOID + " = ?",
					new String[] { isNullIndex });
			values.clear();
			values = null;
			if (update <= 0) {
				Log.e(TAG, "**********更新索引表id数据失败********");
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取指定列名为null的行号
	 * 
	 * @param columnName
	 */
	private String getTableIsNull(String columnName) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = null;
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_INDEX + " where "
						+ columnName + " is null", null);
				if (cursor.moveToNext()) {
					return cursor.getString(cursor
							.getColumnIndex(DynamicShowDao.COLUMN_NAME_AUTOID));
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
		return null;
	}

	/**
	 * 倒序从索引表中获取数据id
	 * 
	 * @param id
	 *            指定的列数u
	 * @return
	 */
	public ArrayList<String> getItemList_index_desc(String columnName) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		ArrayList<String> indexList = new ArrayList<>();
		if (db.isOpen()) {
			try {
				// cursor = db.rawQuery("select " + columnName + " from "+
				// DynamicShowDao.TABLE_NAME_INDEX, null);
				cursor = db.query(DynamicShowDao.TABLE_NAME_INDEX,
						new String[] { columnName }, null, null, null, null,
						columnName + " DESC");
				while (cursor.moveToNext()) {
					String value = cursor.getString(cursor
							.getColumnIndex(columnName));
					if (value != null) {
						indexList.add(value);
					}
				}
				return indexList;
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
		return null;

	}

	/**
	 * 从索引表中获取数据
	 * 
	 * @param db
	 * @param columnName
	 * @param columnValue
	 */

	public String getItem_index(SQLiteDatabase db, String columnName,
			String columnValue) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		Cursor cursor = null;
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select  " + columnName + " from "
						+ DynamicShowDao.TABLE_NAME_INDEX + " where "
						+ columnName + " =? ", new String[] { columnValue });
				if (cursor.moveToNext()) {
					return cursor.getString(cursor.getColumnIndex(columnName));
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}

			}
		}
		return null;

	}

	/**
	 * 数据库单个条目增加
	 * 
	 * @param voices
	 *            音频表数据模型集合
	 * @param videos
	 *            视频表数据模型集合
	 * @param relaydynamic
	 *            转发动态数据模型
	 * @param imgs
	 *            图片集数据模型集合
	 * @param home
	 *            主表数据模型
	 */
	public void singleItemInsert(HomeTable_model home, List<Imgs_model> imgs,
			RelayDynamicTable_model relaydynamic, List<Videos_model> videos,
			List<Voices_model> voices) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			InsertToDB_home(home, db, 1);
			InsertToDB_relaydynamic(relaydynamic, db);
			for (int i = 0; imgs != null && imgs.size() > 0 && i < imgs.size(); i++) {
				InsertToDB_imgs(imgs.get(i), db);
			}
			for (int i = 0; videos != null && videos.size() > 0
					&& i < videos.size(); i++) {
				InsertToDB_videos(videos.get(i), db);
			}
			for (int i = 0; voices != null && voices.size() > 0
					&& i < voices.size(); i++) {
				InsertToDB_voices(voices.get(i), db);
			}
		}

	}

	/**
	 * 更新动态表中的某个字段值的数据
	 * 
	 * @param columnNameDz
	 *            字段值名称
	 * @param sum
	 *            结果
	 * @param id
	 *            修改记录的id
	 */
	public void singleItemUpdate_home(String id, String columnNameDz, String sum) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("UPDATE " + DynamicShowDao.TABLE_NAME_HOME + " SET "
					+ columnNameDz + " ='" + sum + "' WHERE id=" + id);
		}
	}

	/**
	 * * 获取表中指定列名的所有指定数据
	 * 
	 * @param tableName
	 *            表名称
	 * @param where1
	 *            根据的列名
	 * @param value1
	 *            列名条件结果
	 * @param where2
	 *            想要获取列名
	 * @return 返回的结果集合
	 */

	public ArrayList<String> getListItem(String tableName, String where1,
			String[] value1, String where2) {
		ArrayList<String> list = new ArrayList<>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = null;
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("SELECT " + where2 + " FROM " + tableName
						+ " WHERE " + where1 + " =? ", value1);
				while (cursor.moveToNext()) {
					list.add(cursor.getString(cursor.getColumnIndex(where2)));
				}
				return list;
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
		return list;
	}

	/**
	 * 数据库批量条目增加
	 * 
	 * @param voices
	 *            音频表数据模型集合
	 * @param videos
	 *            视频表数据模型集合
	 * @param relaydynamics
	 *            转发动态数据模型集合
	 * @param imgs
	 *            图片集数据模型集合
	 * @param homes
	 *            主表数据模型集合
	 * 
	 * @param beans
	 */
	public void ItemListInsert(List<HomeTable_model> homes,
			List<Imgs_model> imgs, List<RelayDynamicTable_model> relaydynamics,
			List<Videos_model> videos, List<Voices_model> voices) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (int i = 0; homes != null && homes.size() > 0
					&& i < homes.size(); i++) {
				InsertToDB_home(homes.get(i), db, homes.size());
			}
			for (int i = 0; relaydynamics != null && relaydynamics.size() > 0
					&& i < relaydynamics.size(); i++) {
				InsertToDB_relaydynamic(relaydynamics.get(i), db);
			}
			for (int i = 0; imgs != null && imgs.size() > 0 && i < imgs.size(); i++) {
				InsertToDB_imgs(imgs.get(i), db);
			}
			for (int i = 0; videos != null && videos.size() > 0
					&& i < videos.size(); i++) {
				InsertToDB_videos(videos.get(i), db);
			}
			for (int i = 0; voices != null && voices.size() > 0
					&& i < voices.size(); i++) {
				InsertToDB_voices(voices.get(i), db);
			}
		}
	}

	/**
	 * 数据库删除操作在主表中(按照提交时间删除)
	 * 
	 * 思路：在作删除操作的时候，如果含有视频语音，图片时将分别获取出来相应的id。通过查询数据库的方法判断其他条目的数据，有没有使用
	 * 确认没有的话，在相应的表中进行删除
	 * 
	 * @param submitTime
	 *            提交时间
	 */
	public void singleItemDelete(String submitTime) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			// isRepeat(submitTime, null);

			int deleteLine = db.delete(DynamicShowDao.TABLE_NAME_HOME,
					DynamicShowDao.COLUMN_NAME_CREATETIME + " = ?",
					new String[] { submitTime });
			if (deleteLine <= 0) {
				Log.e(TAG, "**********主表按照时间删除数据失败********");
			}
		}

	}

	/**
	 * 数据库删除操作在主表中(按照uid删除)
	 * 
	 * @param uid
	 *            用户id
	 */
	public void singleItemDelete_uid(String uid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			int deleteLine = db.delete(DynamicShowDao.TABLE_NAME_HOME,
					DynamicShowDao.COLUMN_NAME_UID + " = ?",
					new String[] { uid });
			if (deleteLine <= 0) {
				Log.e(TAG, "**********主表按照时间删除数据失败***uid*****");
			}
		}
	}

	/**
	 * 删除的时候通过提交时间或者id判断该条目的动态（图片，视频，语音字段值）在数据库中其他的地方是否引用，若有则不删
	 * 
	 * @param submitTime
	 */
	private void isRepeat(String submitTime, String id) {
		HomeTable_model dynamics = null;
		if (submitTime != null) {
			dynamics = getSingleItem_dynamic(
					DynamicShowDao.COLUMN_NAME_CREATETIME,
					new String[] { submitTime });
		} else if (id != null) {
			dynamics = getSingleItem_dynamic(DynamicShowDao.COLUMN_NAME_ID,
					new String[] { id });
		}
		// 判断是否有照片数据 ，，在当前数据库查询 有：在对应的照片表中不删，否则将照片表中的数据删除
		if (dynamics != null
				&& dynamics.getImgs() != null
				&& getListItems_dynamics(DynamicShowDao.COLUMN_NAME_IMGS,
						new String[] { dynamics.getImgs() }).size() == 1) {
			List<String> imgIds = DbManager.getInstance().splitFileName_p(
					dynamics.getImgs());
			for (int i = 0; i < imgIds.size(); i++) { // 遍历循环每个照片的id
														// ,在每个照片表的数据库中找到该id的item.判端每个本地的url是否有相应的资源，有就进行删除
				Imgs_model imgmodel = getSingleItem_img(
						DynamicShowDao.COLUMN_NAME_ID,
						new String[] { imgIds.get(i) });
				// 判断小图的是否存在本地路径，路径下是否有照片数据，，有就进行删除操作
				if (imgmodel != null && imgmodel.getLocalImg() != null
						&& new File(imgmodel.getLocalImg()).exists()) {
					new File(imgmodel.getLocalImg()).delete();
				}
				if (imgmodel != null && imgmodel.getLocalImgOri() != null
						&& new File(imgmodel.getLocalImgOri()).exists()) {
					new File(imgmodel.getLocalImgOri()).delete();
				}
				singleItemDelete(GlobalConstant.IMAGE_TABLE, imgIds.get(i));
			}
		}

		// 判断是否有音频数据 ，，在当前数据库查询 有：在对应的音频表中不删，否则将音频表中的数据删除
		if (dynamics != null
				&& dynamics.getVoices() != null
				&& getListItems_dynamics(DynamicShowDao.COLUMN_NAME_VOICES,
						new String[] { dynamics.getVoices() }).size() == 1) {
			List<String> voiceIds = DbManager.getInstance().splitFileName_p(
					dynamics.getVoices());
			for (int i = 0; i < voiceIds.size(); i++) { // 遍历循环每个音频的id
														// ,在每个音频表的数据库中找到该id的item.判端每个本地的url是否有相应的资源，有就进行删除
				Voices_model voicemodel = getSingleItem_voice(
						DynamicShowDao.COLUMN_NAME_ID,
						new String[] { voiceIds.get(i) });
				// 判断的是否存在本地路径，路径下是否有语音数据，，有就进行删除操作
				if (voicemodel != null && voicemodel.getLocalSrc() != null
						&& new File(voicemodel.getLocalSrc()).exists()) {
					new File(voicemodel.getLocalSrc()).delete();
				}
				if (voicemodel != null && voicemodel.getLocalSrcbg() != null
						&& new File(voicemodel.getLocalSrcbg()).exists()) {
					new File(voicemodel.getLocalSrcbg()).delete();
				}
				singleItemDelete(GlobalConstant.VOICE_TABLE, voiceIds.get(i));
			}
		}

		// 判断是否有视频数据 ，，在当前数据库查询 有：在对应的视频表中不删，否则将视频表中的数据删除
		if (dynamics != null
				&& dynamics.getVideos() != null
				&& getListItems_dynamics(DynamicShowDao.COLUMN_NAME_VIDEOS,
						new String[] { dynamics.getVideos() }).size() == 1) {
			List<String> videoIds = DbManager.getInstance().splitFileName_p(
					dynamics.getVideos());
			for (int i = 0; i < videoIds.size(); i++) { // 遍历循环每个视频的id
														// ,在每个视频表的数据库中找到该id的item.判端每个本地的url是否有相应的资源，有就进行删除
				Videos_model videomodel = getSingleItem_video(
						DynamicShowDao.COLUMN_NAME_ID,
						new String[] { videoIds.get(i) });
				// 判断的是否存在本地路径，路径下是否有视频数据，，有就进行删除操作
				if (videomodel != null && videomodel.getLocalSrc() != null
						&& new File(videomodel.getLocalSrc()).exists()) {
					new File(videomodel.getLocalSrc()).delete();
				}
				if (videomodel != null && videomodel.getLocalSrcbg() != null
						&& new File(videomodel.getLocalSrcbg()).exists()) {
					new File(videomodel.getLocalSrcbg()).delete();
				}
				singleItemDelete(GlobalConstant.VIDEO_TABLE, videoIds.get(i));
			}
		}
	}

	/**
	 * 数据库删除操作 (按照id删除) 传入表的类型 ，要删除的表的id
	 * 
	 * @param tableType
	 *            指定删除的表，在全局变量中 动态表： DYNAMIC_TABLE;转发动态表
	 *            DYNAMIC_RELAY_TABLE;语音表VOICE_TABLE
	 *            ;视频表VIDEO_TABLE;相册表IMAGE_TABLE;
	 * @param Id
	 *            服务器返回的id
	 * @param isCloseDB
	 *            操作完成后是否自动关闭数据库
	 */
	public void singleItemDelete(int tableType, String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			switch (tableType) {
			case GlobalConstant.DYNAMIC_TABLE:
				// isRepeat(null, id);
				int dynamic = db.delete(DynamicShowDao.TABLE_NAME_HOME,
						DynamicShowDao.COLUMN_NAME_ID + " = ?",
						new String[] { id });
				if (dynamic <= 0) {
					Log.e(TAG, "**********dynamic按照id删除数据失败********");
				}
				break;
			case GlobalConstant.DYNAMIC_RELAY_TABLE:
				int dynamic_relay = db.delete(
						DynamicShowDao.TABLE_NAME_RELAY_DYNAMIC,
						DynamicShowDao.COLUMN_NAME_ID + " = ?",
						new String[] { id });
				if (dynamic_relay <= 0) {
					Log.e(TAG, "**********dynamic_relayTable按照id删除数据失败********");
				}
				break;
			case GlobalConstant.VOICE_TABLE:
				int voice = db.delete(DynamicShowDao.TABLE_NAME_VOICES,
						DynamicShowDao.COLUMN_NAME_ID + " = ?",
						new String[] { id });
				if (voice <= 0) {
					Log.e(TAG, "**********voiceTable按照id删除数据失败********");
				}

				break;
			case GlobalConstant.VIDEO_TABLE:
				int video = db.delete(DynamicShowDao.TABLE_NAME_VIDEOS,
						DynamicShowDao.COLUMN_NAME_ID + " = ?",
						new String[] { id });
				if (video <= 0) {
					Log.e(TAG, "**********videoTable按照id删除数据失败********");
				}
				break;
			case GlobalConstant.IMAGE_TABLE:
				int image = db.delete(DynamicShowDao.TABLE_NAME_IMGS,
						DynamicShowDao.COLUMN_NAME_ID + " = ?",
						new String[] { id });
				if (image <= 0) {
					Log.e(TAG, "**********imageTable按照id删除数据失败********");
				}
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 数据库更新操作 在tableType中传入相应的表tag，在要更新的类型变量中传入对象，其余传入null即可 在全局变量中 动态表：
	 * DYNAMIC_TABLE;转发动态表
	 * DYNAMIC_RELAY_TABLE;语音表VOICE_TABLE;视频表VIDEO_TABLE;相册表IMAGE_TABLE;
	 * 
	 * @param tableType
	 *            表关键tag
	 * @param id
	 *            根据id（服务器返回的动态id）来更新数据库的数据
	 * @param home
	 *            动态表模型
	 * @param img
	 *            相册表
	 * @param relaydynamic
	 *            转发表模型
	 * @param videos
	 *            视频表
	 * @param voices
	 *            语音表
	 * @param isDBclose
	 *            当前是否进行关闭数据库操纵
	 */
	public void singleItemUpdate(int tableType, String id,
			HomeTable_model home, Imgs_model img,
			RelayDynamicTable_model relaydynamic, Videos_model video,
			Voices_model voice) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			switch (tableType) {
			case GlobalConstant.DYNAMIC_TABLE:
				if (home != null) {
					UpdateToDynamic_Table(db, home, id);
				}
				break;
			case GlobalConstant.IMAGE_TABLE:
				if (img != null) {
					UpdateToImage_Table(db, img, id);
				}
				break;
			case GlobalConstant.DYNAMIC_RELAY_TABLE:
				if (relaydynamic != null) {
					UpdateToDynamic_relay_Table(db, relaydynamic, id);
				}
				break;
			case GlobalConstant.VIDEO_TABLE:
				if (video != null) {
					UpdateToVideo_Table(db, video, id);
				}
				break;
			case GlobalConstant.VOICE_TABLE:
				if (voice != null) {
					UpdateToVoice_Table(db, voice, id);
				}
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 更新数据到语音表中
	 * 
	 * @param db
	 * 
	 * @param voice
	 *            新的语音数据模型
	 * @param id
	 *            更新的条件
	 */

	private void UpdateToVoice_Table(SQLiteDatabase db, Voices_model voice,
			String id) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		if (voice.getId() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_ID, voice.getId()); // 该语音的id
		}
		if (voice.getSrc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SRC, voice.getSrc()); // 语音的路径
		}
		if (voice.getLocalSrc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALSRC, voice.getLocalSrc()); // 语音的路径（本地）
		}
		if (voice.getLocalSrcbg() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALSRCBG,
					voice.getLocalSrcbg()); // 语音预览图片的路径（本地）
		}
		if (voice.getVoiceText() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_VOICETEXT,
					voice.getVoiceText()); // 语音文本内容
		}
		if (voice.getSrcbg() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SRCBG, voice.getSrcbg()); // 语音的预览图片
		}
		if (voice.getFilesize() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_FILESIZE, voice.getFilesize()); // 语音的文件大小
		}
		if (voice.getSc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SC, voice.getSc()); // 语音的时长
		}
		int updateNum = db.update(DynamicShowDao.TABLE_NAME_VOICES, values,
				DynamicShowDao.COLUMN_NAME_ID + " = ?", new String[] { id });
		if (updateNum <= 0) {
			Log.e(TAG, "**********语音表更新动态数据失败********");
		}
		values.clear();
		values = null;
	}

	/**
	 * 更新数据到语音表中
	 * 
	 * @param db
	 * 
	 * @param voice
	 *            新的语音数据模型
	 * @param id
	 *            更新的条件
	 */

	public void UpdateToVoice_Table(Voices_model voice, String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (!db.isOpen()) {
			return;
		}
		ContentValues values = new ContentValues();
		if (voice.getId() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_ID, voice.getId()); // 该语音的id
		}
		if (voice.getSrc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SRC, voice.getSrc()); // 语音的路径
		}
		if (voice.getLocalSrc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALSRC, voice.getLocalSrc()); // 语音的路径（本地）
		}
		if (voice.getLocalSrcbg() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALSRCBG,
					voice.getLocalSrcbg()); // 语音预览图片的路径（本地）
		}
		if (voice.getVoiceText() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_VOICETEXT,
					voice.getVoiceText()); // 语音文本内容
		}
		if (voice.getSrcbg() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SRCBG, voice.getSrcbg()); // 语音的预览图片
		}
		if (voice.getFilesize() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_FILESIZE, voice.getFilesize()); // 语音的文件大小
		}
		if (voice.getSc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SC, voice.getSc()); // 语音的时长
		}
		int updateNum = db.update(DynamicShowDao.TABLE_NAME_VOICES, values,
				DynamicShowDao.COLUMN_NAME_ID + " = ?", new String[] { id });
		if (updateNum <= 0) {
			Log.e(TAG, "**********语音表更新动态数据失败********");
		}
		values.clear();
		values = null;
	}

	/**
	 * 更新数据到视频表中
	 * 
	 * @param db
	 * 
	 * @param video
	 *            新的视频数据模型
	 * @param id
	 *            更新的条件
	 */
	private void UpdateToVideo_Table(SQLiteDatabase db, Videos_model video,
			String id) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		if (video.getId() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_ID, video.getId()); // 该视频的id
		}
		if (video.getSrc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SRC, video.getSrc()); // 视频的路径
		}
		if (video.getLocalSrc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALSRC, video.getLocalSrc()); // 视频的路径（本地）
		}
		if (video.getLocalSrcbg() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALSRCBG,
					video.getLocalSrcbg()); // 视频预览图片的路径（本地）
		}
		if (video.getSrcbg() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SRCBG, video.getSrcbg()); // 视频的预览图片
		}
		if (video.getFilesize() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_FILESIZE, video.getFilesize()); // 视频的文件大小
		}
		if (video.getSc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SC, video.getSc()); // 视频的时长
		}
		int updateNum = db.update(DynamicShowDao.TABLE_NAME_VIDEOS, values,
				DynamicShowDao.COLUMN_NAME_ID + " = ?", new String[] { id });
		if (updateNum <= 0) {
			Log.e(TAG, "**********视频表更新动态数据失败********");
		}
		values.clear();
		values = null;

	}

	/**
	 * 更新数据到视频表中
	 * 
	 * @param db
	 * 
	 * @param video
	 *            新的视频数据模型
	 * @param localPath
	 *            本地路径
	 */
	public void UpdateToVideo_Table(Videos_model video, String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (!db.isOpen()) {
			return;
		}
		ContentValues values = new ContentValues();
		if (video.getId() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_ID, video.getId()); // 该视频的id
		}
		if (video.getSrc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SRC, video.getSrc()); // 视频的路径
		}
		if (video.getLocalSrc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALSRC, video.getLocalSrc()); // 视频的路径（本地）
		}
		if (video.getLocalSrcbg() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALSRCBG,
					video.getLocalSrcbg()); // 视频预览图片的路径（本地）
		}
		if (video.getSrcbg() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SRCBG, video.getSrcbg()); // 视频的预览图片
		}
		if (video.getFilesize() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_FILESIZE, video.getFilesize()); // 视频的文件大小
		}
		if (video.getSc() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_SC, video.getSc()); // 视频的时长
		}
		int updateNum = db.update(DynamicShowDao.TABLE_NAME_VIDEOS, values,
				DynamicShowDao.COLUMN_NAME_ID + " = ?", new String[] { id });
		if (updateNum <= 0) {
			Log.e(TAG, "**********视频表更新动态数据失败********");
		}
		values.clear();
		values = null;

	}

	/**
	 * 更新数据到转发动态表中
	 * 
	 * @param db
	 * 
	 * @param relaydynamic
	 * @param id
	 *            更新的条件
	 */
	private void UpdateToDynamic_relay_Table(SQLiteDatabase db,
			RelayDynamicTable_model relaydynamic, String id) {
		// 目前没有要更新的操作

	}

	/**
	 * 更新数据到照片表中
	 * 
	 * @param db
	 * @param img
	 *            照片表的模型
	 * @param id
	 *            更新的条件
	 */
	private void UpdateToImage_Table(SQLiteDatabase db, Imgs_model img,
			String id) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		if (img.getId() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_ID, img.getId()); // 该图片的id
		}
		if (img.getImg() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_IMG, img.getImg()); // 小图片的路径
		}
		if (img.getLocalImg() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALIMG, img.getLocalImg()); // 小图片的路径(本地)
		}
		if (img.getImgori() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_IMGORI, img.getImgori()); // 大图片的url
		}
		if (img.getLocalImgOri() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALIMGORI,
					img.getLocalImgOri()); // 大图片的url(本地)
		}
		if (img.getInfo() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_INFO, img.getInfo()); // img文字信息
		}
		int updateNum = db.update(DynamicShowDao.TABLE_NAME_IMGS, values,
				DynamicShowDao.COLUMN_NAME_ID + " = ?", new String[] { id });
		if (updateNum <= 0) {
			Log.e(TAG, "**********图片表更新动态数据失败********");
		}
		values.clear();
		values = null;

	}

	/**
	 * 更新数据到照片表中
	 * 
	 * @param img
	 *            、照片表的模型
	 * @param localPath
	 *            本地路径
	 */
	public void UpdateToImage_Table(Imgs_model img, String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			if (img.getId() != null) {
				values.put(DynamicShowDao.COLUMN_NAME_ID, img.getId()); // 该图片的id
			}
			if (img.getImg() != null) {
				values.put(DynamicShowDao.COLUMN_NAME_IMG, img.getImg()); // 小图片的路径
			}
			if (img.getLocalImg() != null) {
				values.put(DynamicShowDao.COLUMN_NAME_LOCALIMG,
						img.getLocalImg()); // 小图片的路径(本地)
			}
			if (img.getImgori() != null) {
				values.put(DynamicShowDao.COLUMN_NAME_IMGORI, img.getImgori()); // 大图片的url
			}
			if (img.getLocalImgOri() != null) {
				values.put(DynamicShowDao.COLUMN_NAME_LOCALIMGORI,
						img.getLocalImgOri()); // 大图片的url(本地)
			}
			if (img.getInfo() != null) {
				values.put(DynamicShowDao.COLUMN_NAME_INFO, img.getInfo()); // img文字信息
			}
			int updateNum = db
					.update(DynamicShowDao.TABLE_NAME_IMGS, values,
							DynamicShowDao.COLUMN_NAME_ID + " = ?",
							new String[] { id });
			if (updateNum <= 0) {
				Log.e(TAG, "**********图片表更新动态数据失败********");
			}
			values.clear();
			values = null;
		}
	}

	/**
	 * 更新数据到动态表中
	 * 
	 * @param db
	 *            数据库
	 * 
	 * @param home
	 *            动态表（主表）的数据模型
	 * @param id
	 *            更新的条件
	 */
	public void UpdateToDynamic_Table(SQLiteDatabase db, HomeTable_model home,
			String id) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		if (home.getCreatetime() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_CREATETIME,
					home.getCreatetime()); // 创建时间
		}
		if (home.getId() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_ID, home.getId()); // id

		}
		if (home.getImgs() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_IMGS, home.getImgs()); // 照片

		}
		if (home.getUpdate_type() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_UPDATETYPE,
					home.getUpdate_type()); // 上传状态码

		}
		if (home.getVoices() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_VOICES, home.getVoices()); // 语音

		}
		if (home.getVideos() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_VIDEOS, home.getVideos()); // 视频

		}
		if (home.getFace() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_FACE, home.getFace()); // 头像

		}
		if (home.getNickname() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_NICKNAME, home.getNickname()); // 网络名称

		}
		if (home.getZf() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_ZF, home.getZf()); // 转发数

		}
		if (home.getPj() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_PJ, home.getPj()); // 评价数

		}
		if (home.getYd() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_YD, home.getYd()); // 阅读数

		}
		if (home.getLocalFace() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_LOCALFACE,
					home.getLocalFace()); // 头像本地的路径

		}
		if (home.getDz() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_DZ, home.getDz()); // 点赞数
		}
		if (home.getNickname() != null) {
			values.put(DynamicShowDao.COLUMN_NAME_NICKNAME, home.getNickname()); // 修改昵称名字
		}

		int updateNum = db.update(DynamicShowDao.TABLE_NAME_HOME, values,
				DynamicShowDao.COLUMN_NAME_ID + " = ?", new String[] { id });
		if (updateNum <= 0) {
			Log.e(TAG, "**********动态表更新动态数据失败********");
		}
		values.clear();
		values = null;
	}

	/**
	 * 更新数据到动态表中(根据指定条件)
	 * 
	 * @param value
	 *            更新的结果
	 * @param condition  条件
	 * @param condition_value  条件值
	 */
	public void UpdateToDynamic_Table(String value, String condition,
			String condition_value) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(DynamicShowDao.COLUMN_NAME_NICKNAME, value);
			db.update(DynamicShowDao.TABLE_NAME_HOME,values, condition+ " = ?", new String[]{condition_value});
			values.clear();
			values = null;
		}
	}

	/**
	 * 语音数据插入操作
	 * 
	 * @param voices
	 * @param db
	 */
	private void InsertToDB_voices(Voices_model voices, SQLiteDatabase db) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		if (voices != null
				&& getSingleItem_voice(DynamicShowDao.COLUMN_NAME_ID,
						new String[] { voices.getId() }) == null) {
			ContentValues values_voices = new ContentValues();
			values_voices.put(DynamicShowDao.COLUMN_NAME_ID, voices.getId());
			values_voices.put(DynamicShowDao.COLUMN_NAME_VOICETEXT,
					voices.getVoiceText());
			values_voices.put(DynamicShowDao.COLUMN_NAME_SRC, voices.getSrc());
			values_voices.put(DynamicShowDao.COLUMN_NAME_SRCBG,
					voices.getSrcbg());
			values_voices.put(DynamicShowDao.COLUMN_NAME_LOCALSRC,
					voices.getLocalSrc());
			values_voices.put(DynamicShowDao.COLUMN_NAME_LOCALSRCBG,
					voices.getLocalSrcbg());
			values_voices.put(DynamicShowDao.COLUMN_NAME_SC, voices.getSc());
			values_voices.put(DynamicShowDao.COLUMN_NAME_FILESIZE,
					voices.getFilesize());
			long insert = db.insert(DynamicShowDao.TABLE_NAME_VOICES, null,
					values_voices);
			values_voices.clear();
			values_voices = null;
			if (insert <= 0) {
				Log.e(TAG, "**********音频表插入动态数据失败********");
			}
		}

	}

	/**
	 * 视频数据插入操作
	 * 
	 * @param videos
	 * @param db
	 */
	private void InsertToDB_videos(Videos_model videos, SQLiteDatabase db) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		if (videos != null
				&& getSingleItem_video(DynamicShowDao.COLUMN_NAME_ID,
						new String[] { videos.getId() }) == null) {
			ContentValues values_videos = new ContentValues();
			values_videos.put(DynamicShowDao.COLUMN_NAME_ID, videos.getId());
			values_videos.put(DynamicShowDao.COLUMN_NAME_SRC, videos.getSrc());
			values_videos.put(DynamicShowDao.COLUMN_NAME_SRCBG,
					videos.getSrcbg());
			values_videos.put(DynamicShowDao.COLUMN_NAME_SC, videos.getSc());
			values_videos.put(DynamicShowDao.COLUMN_NAME_FILESIZE,
					videos.getFilesize());
			values_videos.put(DynamicShowDao.COLUMN_NAME_LOCALSRC,
					videos.getLocalSrc());
			values_videos.put(DynamicShowDao.COLUMN_NAME_LOCALSRCBG,
					videos.getLocalSrcbg());
			long insert = db.insert(DynamicShowDao.TABLE_NAME_VIDEOS, null,
					values_videos);
			values_videos.clear();
			values_videos = null;
			if (insert <= 0) {
				Log.e(TAG, "**********视频表插入动态数据失败********");
			} else {
				if (videos.getSrcbg() != null) { // 将背景图下载到本地路径存入数据库
					String[] split = videos.getSrcbg().split("/");
					String targetPath = GlobalConstant.DYNAMIC_VIDEOIMGBG_SAVEPATH
							+ videos.getId() + "$$$" + split[split.length - 1];
					if (new File(targetPath).exists()) { // 如果插入的目的地路径存在就之间保存改路径
															// (当用户卸载软件后该文件还存在，再次安装可避免重复下载)
						Videos_model video = new Videos_model();
						video.setLocalSrc(targetPath);
						DynamicShowDao.getInstance().singleItemUpdate(
								GlobalConstant.VIDEO_TABLE, videos.getId(),
								null, null, null, videos, null);
					} else {
						downloadFile(videos.getSrcbg(), targetPath,
								videos.getId(), GlobalConstant.VIDEO_TABLE,
								null);
					}
				}
			}

		}

	}

	/**
	 * 转发动态数据插入操作
	 * 
	 * @param relaydynamic
	 * @param db2
	 */
	private void InsertToDB_relaydynamic(RelayDynamicTable_model relaydynamic,
			SQLiteDatabase db) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		if (relaydynamic != null
				&& getSingleItem_relayDynamic(DynamicShowDao.COLUMN_NAME_ID,
						new String[] { relaydynamic.getId() }) == null) {
			ContentValues values_zfinfox = new ContentValues();
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_ID,
					relaydynamic.getId());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_CREATETIME,
					relaydynamic.getCreatetime());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_FLID,
					relaydynamic.getFlid());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_FNICKNAME,
					relaydynamic.getFnickname());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_FUID,
					relaydynamic.getFuid());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_OLID,
					relaydynamic.getOlid());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_ONICKNAME,
					relaydynamic.getOnickname());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_OUID,
					relaydynamic.getOuid());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_DZ,
					relaydynamic.getDz());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_INFOS,
					relaydynamic.getInfos());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_ISNM,
					relaydynamic.getIsnm());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_ISOPEN,
					relaydynamic.getIsopen());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_JD,
					relaydynamic.getJd());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_PJ,
					relaydynamic.getPj());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_RELINFO,
					relaydynamic.getRelinfo());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_TAGS,
					relaydynamic.getTags());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_TYPE,
					relaydynamic.getType());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_UID,
					relaydynamic.getUid());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_WD,
					relaydynamic.getWd());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_YD,
					relaydynamic.getYd());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_ZF,
					relaydynamic.getZf());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_ZFINFO,
					relaydynamic.getZfinfo());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_IMGS,
					relaydynamic.getImgs());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_VIDEOS,
					relaydynamic.getVideos());
			values_zfinfox.put(DynamicShowDao.COLUMN_NAME_VOICES,
					relaydynamic.getVoices());
			long insert = db.insert(DynamicShowDao.TABLE_NAME_RELAY_DYNAMIC,
					null, values_zfinfox);
			values_zfinfox.clear();
			values_zfinfox = null;
			if (insert <= 0) {
				Log.e(TAG, "**********转发动态表插入动态数据失败********");
			}
		}

	}

	/**
	 * 相册数据插入操作
	 * 
	 * @param imgs
	 * @param db
	 */
	private void InsertToDB_imgs(Imgs_model imgs, SQLiteDatabase db) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		if (imgs != null
				&& getSingleItem_img(DynamicShowDao.COLUMN_NAME_ID,
						new String[] { imgs.getId() }) == null) {
			ContentValues values_imgs = new ContentValues();
			values_imgs.put(DynamicShowDao.COLUMN_NAME_ID, imgs.getId());
			values_imgs.put(DynamicShowDao.COLUMN_NAME_IMG, imgs.getImg());
			values_imgs.put(DynamicShowDao.COLUMN_NAME_LOCALIMG,
					imgs.getLocalImg());
			values_imgs.put(DynamicShowDao.COLUMN_NAME_LOCALIMGORI,
					imgs.getLocalImgOri());
			values_imgs
					.put(DynamicShowDao.COLUMN_NAME_IMGORI, imgs.getImgori());
			values_imgs.put(DynamicShowDao.COLUMN_NAME_INFO, imgs.getInfo());
			long insert = db.insert(DynamicShowDao.TABLE_NAME_IMGS, null,
					values_imgs);
			values_imgs.clear();
			values_imgs = null;
			if (insert <= 0) {
				Log.e(TAG, "**********图片表插入动态数据失败********");
			} else {
				if (imgs.getImg() != null) { // 将小图下载到本地路径存入数据库
					String[] split = imgs.getImg().split("/");
					String targetPath = GlobalConstant.DYNAMIC_SMALLIMAGE_SAVEPATH
							+ imgs.getId() + "$$$" + split[split.length - 1];
					if (new File(targetPath).exists()) { // 如果插入的目的地路径存在就之间保存改路径
															// (当用户卸载软件后该文件还存在，再次安装可避免重复下载)
						Imgs_model img = new Imgs_model();
						img.setLocalImg(targetPath);
						DynamicShowDao.getInstance().singleItemUpdate(
								GlobalConstant.IMAGE_TABLE, imgs.getId(), null,
								img, null, null, null);
					} else {
						downloadFile(imgs.getImg(), targetPath, imgs.getId(),
								GlobalConstant.IMAGE_TABLE, null);
					}
				}
			}
		}
	}

	/**
	 * 主表数据插入操作
	 * 
	 * @param home
	 *            主表
	 * @param db
	 *            数据库
	 * @param count
	 *            插入数据库的总数
	 */
	private void InsertToDB_home(HomeTable_model home, SQLiteDatabase db,
			int count) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		if (home != null
				&& getSingleItem_dynamic(DynamicShowDao.COLUMN_NAME_ID,
						new String[] { home.getId() }) == null) {

			ContentValues values_home = new ContentValues();
			values_home.put(DynamicShowDao.COLUMN_NAME_ID, home.getId());
			values_home.put(DynamicShowDao.COLUMN_NAME_UPDATETYPE,
					home.getUpdate_type());
			values_home.put(DynamicShowDao.COLUMN_NAME_CREATETIME,
					home.getCreatetime());
			values_home.put(DynamicShowDao.COLUMN_NAME_DISTANCE,
					home.getDistance());
			values_home.put(DynamicShowDao.COLUMN_NAME_DZ, home.getDz());
			values_home.put(DynamicShowDao.COLUMN_NAME_FACE, home.getFace());
			values_home.put(DynamicShowDao.COLUMN_NAME_LOCALFACE,
					home.getLocalFace());
			values_home.put(DynamicShowDao.COLUMN_NAME_ISZAN, home.getIszan());
			values_home.put(DynamicShowDao.COLUMN_NAME_INFOS, home.getInfos());
			values_home.put(DynamicShowDao.COLUMN_NAME_ISNM, home.getIsnm());
			values_home
					.put(DynamicShowDao.COLUMN_NAME_ISOPEN, home.getIsopen());
			values_home.put(DynamicShowDao.COLUMN_NAME_JD, home.getJd());
			values_home.put(DynamicShowDao.COLUMN_NAME_NICKNAME,
					home.getNickname());
			values_home.put(DynamicShowDao.COLUMN_NAME_PJ, home.getPj());
			values_home.put(DynamicShowDao.COLUMN_NAME_RELINFO,
					home.getRelinfo());
			values_home.put(DynamicShowDao.COLUMN_NAME_TAGS, home.getTags());
			values_home
					.put(DynamicShowDao.COLUMN_NAME_MMTYPE, home.getMmtype());
			values_home.put(DynamicShowDao.COLUMN_NAME_TYPE, home.getType());
			values_home.put(DynamicShowDao.COLUMN_NAME_UID, home.getUid());
			values_home.put(DynamicShowDao.COLUMN_NAME_WD, home.getWd());
			values_home.put(DynamicShowDao.COLUMN_NAME_YD, home.getYd());
			values_home.put(DynamicShowDao.COLUMN_NAME_ZF, home.getZf());
			values_home
					.put(DynamicShowDao.COLUMN_NAME_ZFINFO, home.getZfinfo());
			values_home.put(DynamicShowDao.COLUMN_NAME_IMGS, home.getImgs());
			values_home
					.put(DynamicShowDao.COLUMN_NAME_VIDEOS, home.getVideos());
			values_home
					.put(DynamicShowDao.COLUMN_NAME_VOICES, home.getVoices());
			values_home.put(DynamicShowDao.COLUMN_NAME_ZFINFOX,
					home.getZfinfox());
			long insert = db.insert(DynamicShowDao.TABLE_NAME_HOME, null,
					values_home);
			values_home.clear();
			values_home = null;

			if (insert <= 0) {
				Log.e(TAG, "**********主表插入动态数据失败********");
			} else { // 动态表插入成功将头像转为本地数据，路径写入数据库
				if (home.getFace() != null) {
					String targetPath = GlobalConstant.DYNAMIC_HEADIMAGE_SAVEPATH
							+ home.getId()
							+ "$$$"
							+ new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
									.format(new Date(System.currentTimeMillis()))
							+ ".jpg";
					if (new File(targetPath).exists()) {
						new File(targetPath).delete();
					}
					downloadFile(home.getFace(), targetPath, home.getId(),
							GlobalConstant.DYNAMIC_TABLE, null);
				}
			}
		}
	}

	/**
	 * 删除数据库中的所有数据
	 */
	public void deleteAllData() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(DynamicShowDao.TABLE_NAME_HOME, null, null);
			db.delete(DynamicShowDao.TABLE_NAME_IMGS, null, null);
			db.delete(DynamicShowDao.TABLE_NAME_INDEX, null, null);
			db.delete(DynamicShowDao.TABLE_NAME_RELAY_DYNAMIC, null, null);
			db.delete(DynamicShowDao.TABLE_NAME_VIDEOS, null, null);
			db.delete(DynamicShowDao.TABLE_NAME_VOICES, null, null);
		}
	}

	/**
	 * 删除数据库数据操作
	 * 
	 * @param indexs
	 *            删除动态表中的id集合
	 */

	public void deleteMoreItem_homeTable(ArrayList<String> indexs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ArrayList<String> ids_img = new ArrayList<>(); // 照片的id集合
			ArrayList<String> ids_voice = new ArrayList<>(); // 语音的id集合
			ArrayList<String> ids_video = new ArrayList<>(); // 视频的id集合
			ArrayList<String> ids_relDynamic = new ArrayList<>(); // 转发动态的id集合

			// 获取ids集合中id的条目对象
			List<HomeTable_model> models_homeTable = getListItems_dynamics(
					DynamicShowDao.COLUMN_NAME_ID,
					indexs.toArray(new String[indexs.size()]));
			// 将对象中的照片id，转发动态id，视频id,语音id，都存入到相应的集合中，头像本地照片删除
			for (int i = 0; i < models_homeTable.size(); i++) {
				HomeTable_model model = models_homeTable.get(i);
				// 本地头像数据删除
				if (!StringUtils.isEmpty(model.getLocalFace())
						&& new File(model.getLocalFace()).exists()
						&& !model.getUid().equals(
								PrefUtils.getString(GlobalConstant.USER_ID,
										null))) {

					new File(model.getLocalFace()).delete();
				}
				// 获取照片的id
				if (!StringUtils.isEmpty(model.getImgs())) {
					List<String> imgIds = Dbutils
							.splitFileName(model.getImgs());
					for (int j = 0; imgIds.size() > 0 && j < imgIds.size(); j++) {
						ids_img.add(imgIds.get(j));
					}
				}
				// 获取语音的id
				if (!StringUtils.isEmpty(model.getVoices())) {
					List<String> voicesIds = Dbutils.splitFileName(model
							.getVoices());
					for (int v = 0; voicesIds.size() > 0
							&& v < voicesIds.size(); v++) {
						ids_voice.add(voicesIds.get(v));
					}
				}
				// 获取视频的id
				if (!StringUtils.isEmpty(model.getVideos())) {
					List<String> videoIds = Dbutils.splitFileName(model
							.getVideos());
					for (int d = 0; videoIds.size() > 0 && d < videoIds.size(); d++) {
						ids_video.add(videoIds.get(d));
					}
				}
				// 获取转发动态的id
				if (!StringUtils.isEmpty(model.getZfinfox())) {
					ids_relDynamic.add(model.getZfinfox());
				}
			}
			// 获取到相应的表id，到表中删除该id的本地数据
			deleteMoreItem_imgTable(ids_img, db);
			deleteMoreItem_voiceTable(ids_voice, db);
			deleteMoreItem_videoTable(ids_video, db);
			deleteMoreItem_relDynamicTable(ids_relDynamic, db);
			db.delete(DynamicShowDao.TABLE_NAME_HOME,
					DynamicShowDao.COLUMN_NAME_ID + " in("
							+ getParamMark(indexs.size()) + ") ",
					indexs.toArray(new String[indexs.size()]));
		}
	}

	/**
	 * 删除转发表中的数据
	 * 
	 * @param ids
	 *            转发动态集合
	 * @param db
	 */
	private void deleteMoreItem_relDynamicTable(ArrayList<String> ids,
			SQLiteDatabase db) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		if (db.isOpen()) {
			db.delete(
					DynamicShowDao.TABLE_NAME_RELAY_DYNAMIC,
					DynamicShowDao.COLUMN_NAME_ID + " in("
							+ getParamMark(ids.size()) + ") ",
					ids.toArray(new String[ids.size()]));

		}
	}

	/**
	 * 删除视频表中的数据
	 * 
	 * @param ids
	 *            视频数据的id集合
	 * @param db
	 */
	private void deleteMoreItem_videoTable(ArrayList<String> ids,
			SQLiteDatabase db) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		if (db.isOpen()) {
			db.delete(
					DynamicShowDao.TABLE_NAME_VIDEOS,
					DynamicShowDao.COLUMN_NAME_ID + " in("
							+ getParamMark(ids.size()) + ") ",
					ids.toArray(new String[ids.size()]));
		}

	}

	/**
	 * 删除语音表中的数据
	 * 
	 * @param ids
	 *            语音数据id集合
	 * @param db
	 */
	private void deleteMoreItem_voiceTable(ArrayList<String> ids,
			SQLiteDatabase db) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		if (db.isOpen()) {
			db.delete(
					DynamicShowDao.TABLE_NAME_VOICES,
					DynamicShowDao.COLUMN_NAME_ID + " in("
							+ getParamMark(ids.size()) + ") ",
					ids.toArray(new String[ids.size()]));

		}

	}

	/**
	 * 删除照片表中的数据
	 * 
	 * @param ids
	 *            删除的照片数据
	 * @param db
	 */
	private void deleteMoreItem_imgTable(ArrayList<String> ids,
			SQLiteDatabase db) {
		if (db == null || (db != null && !db.isOpen())) {
			db = dbHelper.getWritableDatabase();
		}
		if (db.isOpen()) {
			db.delete(
					DynamicShowDao.TABLE_NAME_IMGS,
					DynamicShowDao.COLUMN_NAME_ID + " in("
							+ getParamMark(ids.size()) + ") ",
					ids.toArray(new String[ids.size()]));
		}

	}

	/**
	 * 获取单个条目 动态条目
	 * 
	 * @param selectionArgs
	 *            查询的arg
	 * @param selectionName
	 *            查询的字段名称
	 * 
	 * @return 单个动态条目的模型
	 */
	public HomeTable_model getSingleItem_dynamic(String selectionName,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_HOME + " where "
						+ selectionName + " =? ", selectionArgs);
				if (cursor.moveToNext()) {

					return getDynamicModel(cursor);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
		return null;
	}

	/**
	 * 获取批量条目 动态条目 (时间排序)
	 * 
	 * @param selectionArgs
	 *            查询的arg
	 * @param selectionName
	 *            查询的字段名称
	 * @param isCloseDb
	 *            是否自动关闭数据库
	 * @return 动态条目集合
	 */
	public List<HomeTable_model> getListItems_dynamics(String selectionName,
			String[] selectionArgs) {
		if (selectionArgs == null) {
			return null;
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		ArrayList<HomeTable_model> lists = new ArrayList<>();
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_HOME + " where "
						+ selectionName + " in("
						+ getParamMark(selectionArgs.length) + ") order by "
						+ DynamicShowDao.COLUMN_NAME_CREATETIME + " asc",
						selectionArgs);
				while (cursor.moveToNext()) {
					lists.add(getDynamicModel(cursor));

				}
				return lists;
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
		return null;
	}

	/**
	 * 获取传值后的动态对象
	 * 
	 * @param model_relay
	 * @param cursor
	 */
	private HomeTable_model getDynamicModel(Cursor cursor) {
		HomeTable_model model = new HomeTable_model();
		model.setId(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ID)));
		model.setUpdate_type(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_UPDATETYPE)));
		model.setCreatetime(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_CREATETIME)));
		model.setDistance(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_DISTANCE)));
		model.setDz(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_DZ)));
		model.setFace(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_FACE)));
		model.setLocalFace(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_LOCALFACE)));
		model.setIszan(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ISZAN)));
		model.setInfos(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_INFOS)));
		model.setIsnm(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ISNM)));
		model.setIsopen(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ISOPEN)));
		model.setJd(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_JD)));
		model.setNickname(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_NICKNAME)));
		model.setPj(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_PJ)));
		model.setRelinfo(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_RELINFO)));
		model.setTags(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_TAGS)));
		model.setMmtype(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_MMTYPE)));
		model.setType(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_TYPE)));
		model.setUid(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_UID)));
		model.setWd(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_WD)));
		model.setYd(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_YD)));
		model.setZf(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ZF)));
		model.setZfinfo(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ZFINFO)));
		model.setImgs(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_IMGS)));
		model.setVideos(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_VIDEOS)));
		model.setVoices(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_VOICES)));
		model.setZfinfox(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ZFINFOX)));
		return model;
	}

	/**
	 * 获取单个条目 转发动态条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * 
	 * 
	 * @return 转发动态条目的模型
	 */
	public RelayDynamicTable_model getSingleItem_relayDynamic(
			String selectionName, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		if (db.isOpen()) {
			try {

				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_RELAY_DYNAMIC + " where "
						+ selectionName + " =? ", selectionArgs);
				if (cursor.moveToNext()) {
					return getRelayDynamicModel(cursor);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
		return null;
	}

	/**
	 * 获取批量条目 转发动态条目组
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * @return 转发动态条目的模型集合
	 */
	public List<RelayDynamicTable_model> getListItems_relayDynamics(
			String selectionName, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		ArrayList<RelayDynamicTable_model> lists = new ArrayList<>();
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_RELAY_DYNAMIC + " where "
						+ selectionName + " =? ", selectionArgs);
				while (cursor.moveToNext()) {
					lists.add(getRelayDynamicModel(cursor));
				}
				return lists;
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
		return null;
	}

	/**
	 * 获取传值后的转发动态对象
	 * 
	 * @param model_relay
	 * @param cursor
	 */
	private RelayDynamicTable_model getRelayDynamicModel(Cursor cursor) {
		RelayDynamicTable_model model = new RelayDynamicTable_model();
		model.setId(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ID)));
		model.setCreatetime(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_CREATETIME)));
		model.setFlid(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_FLID)));
		model.setFnickname(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_FNICKNAME)));
		model.setFuid(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_FUID)));
		model.setOlid(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_OLID)));
		model.setOnickname(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ONICKNAME)));
		model.setOuid(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_OUID)));
		model.setDz(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_DZ)));
		model.setInfos(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_INFOS)));
		model.setIsnm(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ISNM)));
		model.setIsopen(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ISOPEN)));
		model.setJd(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_JD)));
		model.setPj(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_PJ)));
		model.setRelinfo(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_RELINFO)));
		model.setTags(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_TAGS)));
		model.setType(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_TYPE)));
		model.setUid(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_UID)));
		model.setWd(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_WD)));
		model.setYd(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_YD)));
		model.setZf(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ZF)));
		model.setZfinfo(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ZFINFO)));
		model.setImgs(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_IMGS)));
		model.setVideos(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_VIDEOS)));
		model.setVoices(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_VOICES)));
		return model;
	}

	/**
	 * 获取单个条目 相册条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * 
	 * 
	 * @return 单个相册条目的模型
	 */

	public Imgs_model getSingleItem_img(String selectionName,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_IMGS + " where "
						+ selectionName + " =? ", selectionArgs);
				if (cursor.moveToNext()) {
					return getImgsModel(cursor);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
		return null;
	}

	/**
	 * 获取批量条目 相册条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * @return 相册条目的模型集合
	 */
	public ArrayList<Imgs_model> getListItem_imgs(String selectionName,
			String[] selectionArgs) {
		if (selectionArgs == null) {
			return null;
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		ArrayList<Imgs_model> lists = new ArrayList<>();
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_IMGS + " where "
						+ selectionName + " in("
						+ getParamMark(selectionArgs.length) + ") ",
						selectionArgs);
				while (cursor.moveToNext()) {
					lists.add(getImgsModel(cursor));
				}
				return lists;
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}

			}
		}
		return null;
	}

	/**
	 * 获取传值后的相册对象
	 * 
	 * @param model_relay
	 * @param cursor
	 */
	private Imgs_model getImgsModel(Cursor cursor) {
		Imgs_model model = new Imgs_model();
		model.setId(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ID)));
		model.setAuto_id(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_AUTOID)));
		model.setLocalImg(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_LOCALIMG)));
		model.setImg(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_IMG)));

		model.setImgori(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_IMGORI)));
		model.setLocalImgOri(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_LOCALIMGORI)));
		String str = cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_INFO));
		if (str == null) {
			model.setInfo("");
		} else {
			model.setInfo(str);
		}
		return model;
	}

	/**
	 * 获取单个条目 语音条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * 
	 * @return 单个语音条目的模型
	 */

	public Voices_model getSingleItem_voice(String selectionName,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_VOICES + " where "
						+ selectionName + " =? ", selectionArgs);
				if (cursor.moveToNext()) {
					return getVoiceModel(cursor);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}

			}
		}
		return null;
	}

	/**
	 * 批量获取条目 语音条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * @return 语音条目集合
	 */
	public List<Voices_model> getListItem_voices(String selectionName,
			String[] selectionArgs) {
		if (selectionArgs == null) {
			return null;
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		ArrayList<Voices_model> lists = new ArrayList<>();
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_VOICES + " where "
						+ selectionName + " in("
						+ getParamMark(selectionArgs.length) + ") ",
						selectionArgs);
				while (cursor.moveToNext()) {
					lists.add(getVoiceModel(cursor));
				}
				return lists;
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}

			}
		}
		return null;
	}

	/**
	 * 获取传值后的语音对象
	 * 
	 * @param cursor
	 * @return
	 */
	private Voices_model getVoiceModel(Cursor cursor) {
		Voices_model model = new Voices_model();
		model.setId(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ID)));
		model.setSc(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_SC)));
		model.setSrc(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_SRC)));
		model.setLocalSrc(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_LOCALSRC)));

		model.setLocalSrcbg(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_LOCALSRCBG)));
		model.setSrcbg(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_SRCBG)));

		model.setFilesize(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_FILESIZE)));
		return model;
	}

	/**
	 * 获取单个条目 视频条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * @return 单个视频条目的模型
	 */
	public Videos_model getSingleItem_video(String selectionName,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_VIDEOS + " where "
						+ selectionName + " =? ", selectionArgs);
				if (cursor.moveToNext()) {
					return getVideoModel(cursor);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
		return null;
	}

	/**
	 * 获取批量条目 视频条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * @return 视频条目的集合
	 */
	public List<Videos_model> getListItem_videos(String selectionName,
			String[] selectionArgs) {
		if (selectionArgs == null) {
			return null;
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		ArrayList<Videos_model> lists = new ArrayList<>();
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select * from "
						+ DynamicShowDao.TABLE_NAME_VIDEOS + " where "
						+ selectionName + " in("
						+ getParamMark(selectionArgs.length) + ") ",
						selectionArgs);
				while (cursor.moveToNext()) {
					lists.add(getVideoModel(cursor));
				}
				return lists;
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}

			}
		}
		return null;
	}

	/**
	 * 获取传值后的视频对象
	 * 
	 * @param cursor
	 * @return
	 */
	private Videos_model getVideoModel(Cursor cursor) {
		Videos_model model = new Videos_model();
		model.setId(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_ID)));
		model.setSc(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_SC)));
		model.setSrc(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_SRC)));
		model.setSrcbg(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_SRCBG)));
		model.setLocalSrc(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_LOCALSRC)));

		model.setLocalSrcbg(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_LOCALSRCBG)));
		model.setFilesize(cursor.getString(cursor
				.getColumnIndex(DynamicShowDao.COLUMN_NAME_FILESIZE)));
		return model;
	}

	/**
	 * 下载照片视频或者语音的数据 ，获取本地路径存入数据库
	 * 
	 * @param url
	 * @param id
	 *            该条数据的id值（服务器返回的）
	 * @param type
	 *            通过该字段值来确定哪个类调用了该方法
	 * @param TableName
	 *            //表名称
	 */

	public void downloadFile(String url, String targetPath, String id,
			int TableTag, String type) {

		new HttpHelp().downLoad(url, targetPath, new DownloadFileTools(id,
				TableTag, type));
	}

}
