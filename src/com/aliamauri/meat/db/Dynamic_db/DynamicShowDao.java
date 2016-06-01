package com.aliamauri.meat.db.Dynamic_db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aliamauri.meat.db.Dbutils;
import com.aliamauri.meat.db.Dynamic_db.model.Dynamic_model;
import com.aliamauri.meat.db.Dynamic_db.model.HomeTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.RelayDynamicTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;

/**
 * 动态部分--进行服务器与数据库之间的操作
 * 
 * @author limaokeji-windosc
 * 
 */
public class DynamicShowDao {

	private DynamicShowDao() {

	}

	private static DynamicShowDao instance = null;

	public static DynamicShowDao getInstance() {
		if (instance == null) {

			synchronized (DynamicShowDao.class) {
				if (instance == null) {
					instance = new DynamicShowDao();
				}
			}
		}
		return instance;
	}

	public static final String TABLE_NAME_HOME = "dynamic_show_cache"; // 动态主表
	public static final String TABLE_NAME_RELAY_DYNAMIC = "relay_dynamic_show_cache"; // 转发动态表
	public static final String TABLE_NAME_IMGS = "imgs_file_cache"; // 存储照片数据
	public static final String TABLE_NAME_VIDEOS = "videos_file_cache"; // 存储视频数据
	public static final String TABLE_NAME_VOICES = "voices_file_cache"; // 存储音频数据
	public static final String TABLE_NAME_INDEX = "index_table"; // 存储音频数据

	// ***************************键值名称************************************
	public static final String COLUMN_NAME_AUTOID = "auto_id"; // 主表中自增长id
	public static final String COLUMN_NAME_ID = "id"; // 语音/视频/图片的id
	public static final String COLUMN_NAME_NEW_DYNAMIC_ID = "new_dynamic_id"; // 该条最新动态的id
	public static final String COLUMN_NAME_HOT_DYNAMIC_ID = "hot_dynamic_id"; // 该条最热动态的id
	public static final String COLUMN_NAME_FRIEND_DYNAMIC_ID = "friend_dynamic_id"; // 该条朋友圈动态的id
	public static final String COLUMN_NAME_CREATETIME = "createtime"; // 动态的创键时间（网络数据库返回）
	public static final String COLUMN_NAME_DISTANCE = "distance"; // 动态的距离
	public static final String COLUMN_NAME_DZ = "dz"; // 点赞数
	public static final String COLUMN_NAME_FLID = "flid";
	public static final String COLUMN_NAME_FNICKNAME = "fnickname";
	public static final String COLUMN_NAME_FUID = "fuid";
	public static final String COLUMN_NAME_OLID = "olid";
	public static final String COLUMN_NAME_ONICKNAME = "onickname";
	public static final String COLUMN_NAME_OUID = "ouid";
	public static final String COLUMN_NAME_IMGS = "imgs"; // 用户长传的图片表
	public static final String COLUMN_NAME_VIDEOS = "videos"; // 用户上传的视频表
	public static final String COLUMN_NAME_VOICES = "voices"; // 用户上传的语音表
	public static final String COLUMN_NAME_ZFINFORX = "zfinfox"; // 当前用户转发动态表
	public static final String COLUMN_NAME_FACE = "face"; // 头像
	public static final String COLUMN_NAME_ISZAN = "iszan"; // 是否点赞标记
	public static final String COLUMN_NAME_INFOS = "infos"; // 文本信息
	public static final String COLUMN_NAME_ISNM = "isnm"; // 是否匿名
	public static final String COLUMN_NAME_ISOPEN = "isopen"; // 是否开放
	public static final String COLUMN_NAME_JD = "jd"; // 经度
	public static final String COLUMN_NAME_NICKNAME = "nickname"; // 该条动态的名称
	public static final String COLUMN_NAME_PJ = "pj"; // 评价数
	public static final String COLUMN_NAME_RELINFO = "relinfo"; // 转发的文本信息
	public static final String COLUMN_NAME_TAGS = "tags"; // 标签集合
	public static final String COLUMN_NAME_MMTYPE = "mmtype"; // 该动态类型
	public static final String COLUMN_NAME_TYPE = "type"; // 当前动态的类型
	public static final String COLUMN_NAME_UID = "uid"; // 该用户的uid
	public static final String COLUMN_NAME_WD = "wd"; // 伟度
	public static final String COLUMN_NAME_YD = "yd"; // 阅读数
	public static final String COLUMN_NAME_ZF = "zf"; // 转发数
	public static final String COLUMN_NAME_ZFINFO = "zfinfo";
	public static final String COLUMN_NAME_SRC = "src";// 语音，视频地址
	public static final String COLUMN_NAME_SRCBG = "srcbg"; // 语音视频/背景图
	public static final String COLUMN_NAME_SC = "sc"; // 语音视频时长
	public static final String COLUMN_NAME_IMG = "img"; // 小图片地址（本地）
	public static final String COLUMN_NAME_IMGORI = "imgori";// 大图地址url
	public static final String COLUMN_NAME_INFO = "info"; // 图片的描述文字
	public static final String COLUMN_NAME_FILESIZE = "filesize";// 视频语音的文件大小
	public static final String COLUMN_NAME_ZFINFOX = "zfinfox";// 转发用户的内容
	public static final String COLUMN_NAME_LOCALFACE = "localface"; // 本地路径的用户头像
	public static final String COLUMN_NAME_LOCALIMG = "localimg"; // 本地用户上传的图片的小图路径
	public static final String COLUMN_NAME_LOCALIMGORI = "localimgori"; // 本地用户上传的图片的大图路径
	public static final String COLUMN_NAME_LOCALSRC = "localsrc"; // 本地的语音或者视频文件
	public static final String COLUMN_NAME_LOCALSRCBG = "localsrcbg"; // 本地的语音或者视频文件的背景图
	public static final String COLUMN_NAME_VOICETEXT = "voiceText";// 语音转文字
	public static final String COLUMN_NAME_UPDATETYPE = "updatetype"; // 上传动态类型
																		// 1，资源未上传，2，未上传到服务器，3，上传成功
	public static final String COLUMN_NAME_SPARE1 = "spare1"; // 备用字段1
	public static final String COLUMN_NAME_SPARE2 = "spare2"; // 备用字段2
	public static final String COLUMN_NAME_SPARE3 = "spare3"; // 备用字段3

	// ***************************键值名称************************************
	/**
	 * 向数据库中索引表中插入相应的索引值
	 * 
	 * @param columnName
	 *            列名称
	 * @param ids
	 *            列值
	 */

	public void listItemInsert_index(String columnName, ArrayList<String> ids) {
		/*
		 * 每次向索引表中插入数据(大于5条)的时候都将之前的索引置为null,然后重新覆盖数据
		 */
		if (ids.size() > 5) {
			ArrayList<String> indexs = DynamicShowDao.getInstance()
					.getItemList_index_desc(columnName);
			if (indexs.size() > 0) {
				DbManager_dynamic.getInstance().singleItemDelete_index(
						columnName, indexs);
			}
			DbManager_dynamic.getInstance().deleteMoreItem_homeTable(indexs);
		}

		for (int i = 0; i < ids.size(); i++) {
			if (!DbManager_dynamic.getInstance().singleItemUpdate_index(
					columnName, ids.get(i))) {
				DbManager_dynamic.getInstance().singleItemInsert_index(
						columnName, ids.get(i));
			}
		}

	}

	/**
	 * 获取数据库中索引表最新索引值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 有返回id值，没有返回null
	 */

	public String getRecentIndex(String colName) {
		return DbManager_dynamic.getInstance().getRecentIndex(colName);
	}

	/**
	 * 删除索引表中的id值
	 * 
	 * @param columnName
	 *            指定的列名
	 * @param indexs
	 *            指定的索引集合
	 * @return 影响的行数
	 */

	public int singleItemDelete_index(String columnName,
			ArrayList<String> indexs) {
		return DbManager_dynamic.getInstance().singleItemDelete_index(
				columnName, indexs);
	}

	/**
	 * 从索引表中获取数据
	 * 
	 * @param columnName
	 * @param columnValue
	 */

	public String getItem_index(String columnName, String columnValue) {
		return DbManager_dynamic.getInstance().getItem_index(null, columnName,
				columnValue);
	}

	/**
	 * 从索引表中获取指定条数数据
	 * 
	 * @param columnName
	 *            列名名称
	 * @param count
	 *            获取的条数
	 * @return
	 */
	public ArrayList<String> getItemList_index_desc(String columnName) {
		return DbManager_dynamic.getInstance().getItemList_index_desc(
				columnName);
	}

	/**
	 * 数据库单个条目增加
	 * 
	 * @param home
	 *            主表数据模型
	 * @param img
	 *            图片数据模型
	 * @param relaydynamic
	 *            转发动态数据模型
	 * @param video
	 *            视频数据模型
	 * @param voice
	 *            音频数据模型
	 */
	public void singleItemInsert(HomeTable_model home, List<Imgs_model> imgs,
			RelayDynamicTable_model relaydynamic, List<Videos_model> videos,
			List<Voices_model> voices) {
		DbManager_dynamic.getInstance().singleItemInsert(home, imgs,
				relaydynamic, videos, voices);
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
		DbManager_dynamic.getInstance().updateIndexTable(colName, pastid,
				latestid);
	}

	/**
	 * 数据库批量条目增加
	 * 
	 * @param homes
	 *            //主表数据集合
	 * @param imgs
	 *            //相册表数据集合
	 * @param relaydynamics
	 *            //转发动态表数据集合
	 * @param videos
	 *            //视频表数据集合
	 * @param voices
	 *            //语音表数据集合
	 */

	public void ItemListInsert(List<HomeTable_model> homes,
			List<Imgs_model> imgs, List<RelayDynamicTable_model> relaydynamics,
			List<Videos_model> videos, List<Voices_model> voices) {
		DbManager_dynamic.getInstance().ItemListInsert(homes, imgs,
				relaydynamics, videos, voices);
	}

	/**
	 * 数据库删除操作在主表中(按照提交时间删除)
	 * 
	 * @param submitTime
	 *            提交时间
	 */

	public void singleItemDelete(String submitTime) {
		DbManager_dynamic.getInstance().singleItemDelete(submitTime);
	}

	/**
	 * 数据库删除操作 (按照id删除)
	 * 
	 * @param tableType
	 *            指定删除的表，在全局变量中 动态表： DYNAMIC_TABLE;转发动态表
	 *            DYNAMIC_RELAY_TABLE;语音表VOICE_TABLE
	 *            ;视频表VIDEO_TABLE;相册表IMAGE_TABLE;
	 * @param id
	 *            服务器返回的id
	 */

	public void singleItemDelete(int tableType, String id) {
		DbManager_dynamic.getInstance().singleItemDelete(tableType, id);
	}
	/**
	 * 删除数据库中所有的表数据
	 */
	public void deleteAllData(){
		DbManager_dynamic.getInstance().deleteAllData();
	}
	
	/**
	 * 数据库删除操作 (按照uid)
	 * 
	 * @param uid
	 *            服务器返回的uid
	 */

	public void singleItemDelete_uid(String uid) {
		DbManager_dynamic.getInstance().singleItemDelete_uid(uid);
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
	 */

	public void singleItemUpdate(int tableType, String id,
			HomeTable_model home, Imgs_model img,
			RelayDynamicTable_model relaydynamic, Videos_model video,
			Voices_model voice) {
		DbManager_dynamic.getInstance().singleItemUpdate(tableType, id, home,
				img, relaydynamic, video, voice);
	}

	/**
	 * 获取单个条目 动态条目
	 * 
	 * @param sqlText
	 *            aql查询语句
	 * @return 单个动态条目的模型
	 */

	public HomeTable_model getSingleItem_dynamic(String selectionName,
			String[] selectionArgs) {
		return DbManager_dynamic.getInstance().getSingleItem_dynamic(
				selectionName, selectionArgs);
	}

	/**
	 * 获取批量条目 动态条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * @param isCloseDb
	 *            操作完成后是否关闭数据库
	 * 
	 * @return
	 */

	public List<HomeTable_model> getListItems_dynamics(String selectionName,
			String[] selectionArgs) {
		return DbManager_dynamic.getInstance().getListItems_dynamics(
				selectionName, selectionArgs);
	}

	/**
	 * 更新数据到动态表中(根据指定条件)
	 * @param value 更新后的值
	 * @param condition 条件字段值
	 * @param condition_value 条件值
	 */

	public void UpdateDynamicTable(String value, String condition,String condition_value) {
		DbManager_dynamic.getInstance().UpdateToDynamic_Table(value, condition, condition_value);
	}
	/**
	 * 更新动态表中的数据--照片
	 * 
	 * @param imgs
	 *            照片id集合
	 * @param id
	 *            更新条件值
	 * @return
	 */
	public void UpdateDynamicTable_img(List<String> imgs, String id) {
		
		HomeTable_model homeTable_model = new HomeTable_model();
		homeTable_model.setImgs(Dbutils.jionFileName(imgs));
		
		DbManager_dynamic.getInstance().UpdateToDynamic_Table(
				DbOpenHelper_dynamic.getInstance().getWritableDatabase(),
				homeTable_model, id);
	}

	/**
	 * 更新动态数据条目--语音
	 * 
	 * @param voices
	 *            语音id集合
	 * @param id
	 *            更新条件值
	 * @return
	 */
	public void UpdateDynamicTable_voice(List<String> voices, String id) {
		HomeTable_model homeTable_model = new HomeTable_model();
		homeTable_model.setVoices(Dbutils.jionFileName(voices));

		DbManager_dynamic.getInstance().UpdateToDynamic_Table(
				DbOpenHelper_dynamic.getInstance().getWritableDatabase(),
				homeTable_model, id);
	}

	/**
	 * 更新动态数据条目--视频
	 * 
	 * @param videos
	 *            视频id集合
	 * @param id
	 *            更新条件值
	 * @return
	 */
	public void UpdateDynamicTable_video(List<String> videos, String id) {
		HomeTable_model homeTable_model = new HomeTable_model();
		homeTable_model.setVideos(Dbutils.jionFileName(videos));

		DbManager_dynamic.getInstance().UpdateToDynamic_Table(
				DbOpenHelper_dynamic.getInstance().getWritableDatabase(),
				homeTable_model, id);
	}

	/**
	 * 获取单个条目 转发动态条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * @return 转发动态条目的模型
	 */

	public RelayDynamicTable_model getSingleItem_relayDynamic(
			String selectionName, String[] selectionArgs) {
		return DbManager_dynamic.getInstance().getSingleItem_relayDynamic(
				selectionName, selectionArgs);
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
		return DbManager_dynamic.getInstance().getListItems_relayDynamics(
				selectionName, selectionArgs);
	}

	/**
	 * 获取单个条目 相册条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * @return 单个相册条目的模型
	 */

	public Imgs_model getSingleItem_img(String selectionName,
			String[] selectionArgs) {
		return DbManager_dynamic.getInstance().getSingleItem_img(selectionName,
				selectionArgs);
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
	public ArrayList<Imgs_model> getListItems_imgs(String selectionName,
			String[] selectionArgs) {
		return DbManager_dynamic.getInstance().getListItem_imgs(selectionName,
				selectionArgs);
	}

	/**
	 * 获取单个条目 语音条目
	 * 
	 * @param selectionName
	 *            查询的字段名称
	 * @param selectionArgs
	 *            查询的arg
	 * @return 单个语音条目的模型
	 */

	public Voices_model getSingleItem_voice(String selectionName,
			String[] selectionArgs) {
		return DbManager_dynamic.getInstance().getSingleItem_voice(
				selectionName, selectionArgs);
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
		return DbManager_dynamic.getInstance().getListItem_voices(
				selectionName, selectionArgs);
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
		return DbManager_dynamic.getInstance().getSingleItem_video(
				selectionName, selectionArgs);
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
		return DbManager_dynamic.getInstance().getListItem_videos(
				selectionName, selectionArgs);
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
		return DbManager_dynamic.getInstance().getListItem(tableName, where1,
				value1, where2);
	}

	/**
	 * 获取整条动态数据
	 * 
	 * @return
	 */
	public Dynamic_model getDynamicData(String id) {
		Dynamic_model dynamic = new Dynamic_model();
		Dbutils dbutils = new Dbutils();
		HomeTable_model home = getSingleItem_dynamic(
				DynamicShowDao.COLUMN_NAME_ID, new String[] { id });
		dynamic.createtime = dbutils.format_date(Long.parseLong(home
				.getCreatetime()));
		dynamic.distance = home.getDistance();
		dynamic.dz = home.getDz();
		if (home.getLocalFace() != null
				&& new File(home.getLocalFace()).exists()) {// 判断当前是否含有本地的头像路径
			dynamic.face = home.getLocalFace();
		} else if (home.getFace() != null) {
			dynamic.face = home.getFace() + "##";
		}
		dynamic.iszan = home.getIszan();
		dynamic.id = home.getId();
		dynamic.update_type = home.getUpdate_type();
		dynamic.infos = home.getInfos();
		dynamic.isnm = home.getIsnm();
		dynamic.isopen = home.getIsopen();
		dynamic.jd = home.getJd();
		dynamic.nickname = home.getNickname();
		dynamic.pj = home.getPj();
		dynamic.relinfo = home.getRelinfo();
		dynamic.tags = home.getTags();
		dynamic.mmtype = home.getMmtype();
		dynamic.type = home.getType();
		dynamic.uid = home.getUid();
		dynamic.wd = home.getWd();
		dynamic.yd = home.getYd();
		dynamic.zf = home.getZf();
		dynamic.zfinfo = home.getZfinfo();
		// 判断是否有照片数据
		if (home.getImgs() != null) {
			dynamic.imgs = dbutils.getImgsDate_db(home.getImgs());
		}
		// 判断是否有语音数据
		if (home.getVoices() != null) {
			dynamic.voices = dbutils.getVoicesDate_db(home.getVoices());
		}
		// 判断是否有视频数据
		if (home.getVideos() != null) {
			dynamic.videos = dbutils.getVideosDate_db(home.getVideos());
		}
		// 判断当前是否有转发动态
		dynamic.zfinfox = home.getZfinfox();
		if (dynamic.zfinfox != null) {// 有转发动态
			RelayDynamicTable_model relayDynamic = DynamicShowDao.getInstance()
					.getSingleItem_relayDynamic(DynamicShowDao.COLUMN_NAME_ID,
							new String[] { home.getZfinfox() });
			dynamic.zf_createtime = relayDynamic.getCreatetime();
			dynamic.zf_dz = relayDynamic.getDz();
			dynamic.zf_flid = relayDynamic.getFlid();
			dynamic.zf_fnickname = relayDynamic.getFnickname();
			dynamic.zf_fuid = relayDynamic.getFuid();
			dynamic.zf_id = relayDynamic.getId();
			dynamic.zf_infos = relayDynamic.getInfos();
			dynamic.zf_isnm = relayDynamic.getIsnm();
			dynamic.zf_isopen = relayDynamic.getIsopen();
			dynamic.zf_jd = relayDynamic.getJd();
			dynamic.zf_olid = relayDynamic.getOlid();
			dynamic.zf_onickname = relayDynamic.getOnickname();
			dynamic.zf_ouid = relayDynamic.getOuid();
			dynamic.zf_pj = relayDynamic.getPj();
			dynamic.zf_relinfo = relayDynamic.getRelinfo();
			dynamic.zf_tags = relayDynamic.getTags();
			dynamic.zf_type = relayDynamic.getType();
			dynamic.zf_uid = relayDynamic.getUid();
			dynamic.zf_wd = relayDynamic.getWd();
			dynamic.zf_yd = relayDynamic.getYd();
			dynamic.zf_zf = relayDynamic.getZf();
			dynamic.zf_zfinfo = relayDynamic.getZfinfo();
			// 判断是否有转发照片数据
			if (relayDynamic.getImgs() != null) {
				dynamic.zf_imgs = dbutils
						.getImgsDate_db(relayDynamic.getImgs());
			}
			// 判断是否有转发语音数据
			if (home.getVoices() != null) {
				dynamic.zf_voices = dbutils.getVoicesDate_db(relayDynamic
						.getVoices());
			}
			// 判断是否有转发视频数据
			if (home.getVideos() != null) {
				dynamic.zf_videos = dbutils.getVideosDate_db(relayDynamic
						.getVideos());
			}
		}

		return dynamic;
	}

	// 获取自己上传的照片
	public void getUploadPic(String userid, List<String> local, List<String> net) {
		DbOpenHelper_dynamic dBHelper = DbOpenHelper_dynamic.getInstance();
		SQLiteDatabase db = dBHelper.getReadableDatabase();
		Cursor cursor = null;
		if (db.isOpen()) {
			try {
				cursor = db.rawQuery("select img,localimg from "
						+ TABLE_NAME_HOME + " d," + TABLE_NAME_IMGS
						+ " i where d.imgs=i.id and d.uid='" + userid + "'",
						null);
				// cursor = db.rawQuery("select * from "
				// + TempUploadFileDao.TABLE_NAME, null);
				// while (cursor.moveToNext()) {
				if (cursor.getColumnIndex("img") > 0) {
					net.addAll(cursor.getColumnIndex("img"), net);
				}
				if (cursor.getColumnIndex("localing") > 0) {
					local.addAll(cursor.getColumnIndex("localing"), local);
				}
				// }
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
				if (db != null) {
					db.close();
				}

				cursor = null;
				db = null;
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
	 */
	public void singleItemUpdate_home(String id, String columnNameDz, String sum) {
		DbManager_dynamic.getInstance().singleItemUpdate_home(id, columnNameDz,
				sum);
	}

}
