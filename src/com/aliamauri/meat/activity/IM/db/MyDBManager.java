package com.aliamauri.meat.activity.IM.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.aliamauri.meat.Manager.UserInfoManager;
import com.aliamauri.meat.activity.IM.Constant;
import com.aliamauri.meat.activity.IM.domain.AppointmentInvitMessage;
import com.aliamauri.meat.activity.IM.domain.InviteMessage;
import com.aliamauri.meat.activity.IM.domain.Message.MesageStatus;
import com.aliamauri.meat.activity.IM.domain.RobotUser;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.activity.IM.domain.User.ContactType;
import com.easemob.util.HanziToPinyin;

public class MyDBManager {
	static private MyDBManager dbMgr = new MyDBManager();
	private DbOpenHelper dbHelper;

	void onInit(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	public static synchronized MyDBManager getInstance() {
		return dbMgr;
	}

	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	synchronized public void saveContactList(List<User> contactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			// Map<String, User> uidMap = ContactManager.getInstance()
			// .getAllContactUidMap();
			// for (String key : uidMap.keySet()) {
			// UserInfoManager.getInstance().deleteNativeAvatar(key);
			// }
			db.delete(UserDao.TABLE_NAME, null, null);
			for (User user : contactList) {
				if (user == null || TextUtils.isEmpty(user.getUsername())) {
					continue;
				}
				ContentValues values = new ContentValues();
				if (user.getUserId() != null) {
					values.put(UserDao.COLUMN_NAME_FROM_COMTACT_ID,
							user.getUserId());
				}
				if (user.getNick() != null)
					values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
				if (user.getAvatar() != null)
					values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
				if (user.getNativeAvatar() != null)
					values.put(UserDao.COLUMN_NAME_NATIVE_AVATAR,
							user.getNativeAvatar());

				if (user.getUsername() != null) {
					values.put(UserDao.COLUMN_NAME_USER_NAME,
							user.getUsername());
				}
				if (user.getSex() != null) {
					values.put(UserDao.COLUMN_NAME_CONTACT_SEX, user.getSex());
				}
				if (user.getAge() != null) {
					values.put(UserDao.COLUMN_NAME_CONTACT_AGE, user.getAge());
				}
				if (user.getHobby() != null) {
					values.put(UserDao.COLUMN_NAME_CONTACT_HOBBY,
							user.getHobby());
				}
				if (user.getJob() != null) {
					values.put(UserDao.COLUMN_NAME_CONTACT_JOB, user.getJob());
				}
				if (user.getShieldState() != null) {
					values.put(UserDao.COLUMN_NAME_CONTACT_SHIELD_STATE,
							user.getShieldState());
				}
				if (user.getAddress() != null) {
					values.put(UserDao.COLUMN_NAME_CONTACT_ADDRESS,
							user.getAddress());
				}

				if (user.getContactType() != null) {
					values.put(UserDao.COLUMN_NAME_CONTACT_TYPE, user
							.getContactType().ordinal());
				}
				db.insert(UserDao.TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	synchronized public Map<String, User> getContactList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, User> users = new HashMap<String, User>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String address = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_ADDRESS));
				String hobby = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_HOBBY));
				String job = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_JOB));

				String sex = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_SEX));
				String shieldState = cursor
						.getString(cursor
								.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_SHIELD_STATE));
				String age = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_AGE));

				String username = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_USER_NAME));
				String nick = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
				String nativeAvatar = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_NATIVE_AVATAR));
				String fromComtactId = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_FROM_COMTACT_ID));
				int contactType = cursor.getInt(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_TYPE));
				User user = new User();
				if (contactType == ContactType.APPOINTMENT_TYPE.ordinal()) {
					user.setContactType(ContactType.APPOINTMENT_TYPE);
				} else if (contactType == ContactType.FRIEND_TYPE.ordinal()) {
					user.setContactType(ContactType.FRIEND_TYPE);
				}
				user.setUsername(username);
				user.setNick(nick);
				user.setUserId(fromComtactId);
				user.setAvatar(avatar);
				user.setNativeAvatar(nativeAvatar);
				user.setAddress(address);
				user.setAge(age);
				user.setHobby(hobby);
				user.setJob(job);
				user.setSex(sex);
				user.setShieldState(shieldState);

				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}

				if (Constant.NEW_FRIENDS_USERNAME.equals(username)
						|| Constant.GROUP_USERNAME.equals(username)
						|| Constant.CHAT_ROOM.equals(username)
						|| Constant.APPOINTMENT_INVITATION.equals(username)
						|| Constant.OFFICIAL_USER.equals(username)
						|| Constant.CHAT_ROBOT.equals(username)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance()
							.get(headerName.substring(0, 1)).get(0).target
							.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}

	/**
	 * 通过hxID获取用户信息
	 * 
	 * @return
	 */
	synchronized public User getContactInfo(String hxid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		User user = null;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME
					+ " where " + UserDao.COLUMN_NAME_USER_NAME + "=?",
					new String[] { hxid });
			while (cursor.moveToNext()) {
				user = new User();
				String address = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_ADDRESS));
				String job = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_JOB));
				String hobby = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_HOBBY));
				String sex = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_SEX));
				String shieldState = cursor
						.getString(cursor
								.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_SHIELD_STATE));
				String age = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_AGE));

				String username = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_USER_NAME));
				String nick = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
				String nativeAvatar = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_NATIVE_AVATAR));
				String userId = cursor.getString(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_FROM_COMTACT_ID));
				int contactType = cursor.getInt(cursor
						.getColumnIndex(UserDao.COLUMN_NAME_CONTACT_TYPE));
				if (contactType == ContactType.APPOINTMENT_TYPE.ordinal()) {
					user.setContactType(ContactType.APPOINTMENT_TYPE);
				} else if (contactType == ContactType.FRIEND_TYPE.ordinal()) {
					user.setContactType(ContactType.FRIEND_TYPE);
				}
				user.setUsername(username);
				user.setNick(nick);
				user.setUserId(userId);
				user.setNativeAvatar(nativeAvatar);
				user.setAvatar(avatar);
				user.setAddress(address);
				user.setAge(age);
				user.setHobby(hobby);
				user.setJob(job);
				user.setSex(sex);
				user.setShieldState(shieldState);
				String headerName;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}

				if (username.equals(Constant.NEW_FRIENDS_USERNAME)
						|| username.equals(Constant.GROUP_USERNAME)
						|| username.equals(Constant.CHAT_ROOM)
						|| username.equals(Constant.APPOINTMENT_INVITATION)
						|| username.equals(Constant.CHAT_ROBOT)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance()
							.get(headerName.substring(0, 1)).get(0).target
							.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				cursor.close();
			}
		}
		return user;
	}

	/**
	 * 删除一个联系人
	 * 
	 * @param username
	 */
	synchronized public void deleteContact(String username) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_USER_NAME
					+ " = ?", new String[] { username });
		}
		UserInfoManager.getInstance().deleteNativeAvatar(username);
	}

	/**
	 * 保存一个联系人
	 * 
	 * @param user
	 */
	synchronized public void saveContact(User user) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(UserDao.COLUMN_NAME_USER_NAME, user.getUsername());
		if (user.getNick() != null)
			values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
		if (user.getAvatar() != null)
			values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
		if (user.getNativeAvatar() != null)
			values.put(UserDao.COLUMN_NAME_NATIVE_AVATAR,
					user.getNativeAvatar());
		if (user.getUserId() != null)
			values.put(UserDao.COLUMN_NAME_FROM_COMTACT_ID, user.getUserId());
		if (user.getContactType() != null)
			values.put(UserDao.COLUMN_NAME_CONTACT_TYPE, user.getContactType()
					.ordinal());

		if (user.getAddress() != null) {
			values.put(UserDao.COLUMN_NAME_CONTACT_ADDRESS, user.getAddress());
		}
		if (user.getUsername() != null) {
			values.put(UserDao.COLUMN_NAME_USER_NAME, user.getUsername());
		}
		if (user.getSex() != null) {
			values.put(UserDao.COLUMN_NAME_CONTACT_SEX, user.getSex());
		}
		if (user.getAge() != null) {
			values.put(UserDao.COLUMN_NAME_CONTACT_AGE, user.getAge());
		}
		if (user.getHobby() != null) {
			values.put(UserDao.COLUMN_NAME_CONTACT_HOBBY, user.getHobby());
		}
		if (user.getJob() != null) {
			values.put(UserDao.COLUMN_NAME_CONTACT_JOB, user.getJob());
		}
		if (user.getShieldState() != null) {
			values.put(UserDao.COLUMN_NAME_CONTACT_SHIELD_STATE,
					user.getShieldState());
		}

		if (db.isOpen()) {
			db.replace(UserDao.TABLE_NAME, null, values);
		}
	}

	/**
	 * 更新联系人
	 * 
	 * @param msgId
	 * @param values
	 */
	synchronized public void updateContact(User user) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(UserDao.COLUMN_NAME_USER_NAME, user.getUsername());
			if (user.getNick() != null)
				values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
			if (user.getAvatar() != null)
				values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
			if (user.getAvatar() != null)
				values.put(UserDao.COLUMN_NAME_NATIVE_AVATAR,
						user.getNativeAvatar());
			if (user.getUserId() != null)
				values.put(UserDao.COLUMN_NAME_FROM_COMTACT_ID,
						user.getUserId());
			if (user.getContactType() != null)
				values.put(UserDao.COLUMN_NAME_CONTACT_TYPE, user
						.getContactType().ordinal());

			if (user.getUsername() != null) {
				values.put(UserDao.COLUMN_NAME_USER_NAME, user.getUsername());
			}
			if (user.getSex() != null) {
				values.put(UserDao.COLUMN_NAME_CONTACT_SEX, user.getSex());
			}
			if (user.getAge() != null) {
				values.put(UserDao.COLUMN_NAME_CONTACT_AGE, user.getAge());
			}
			if (user.getHobby() != null) {
				values.put(UserDao.COLUMN_NAME_CONTACT_HOBBY, user.getHobby());
			}
			if (user.getJob() != null) {
				values.put(UserDao.COLUMN_NAME_CONTACT_JOB, user.getJob());
			}
			if (user.getShieldState() != null) {
				values.put(UserDao.COLUMN_NAME_CONTACT_SHIELD_STATE,
						user.getShieldState());
			}

			db.update(UserDao.TABLE_NAME, values, UserDao.COLUMN_NAME_USER_NAME
					+ " = ?",
					new String[] { String.valueOf(user.getUsername()) });
		}
	}

	public void setDisabledGroups(List<String> groups) {
		setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
	}

	public List<String> getDisabledGroups() {
		return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
	}

	public void setDisabledIds(List<String> ids) {
		setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
	}

	public List<String> getDisabledIds() {
		return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
	}

	synchronized private void setList(String column, List<String> strList) {
		StringBuilder strBuilder = new StringBuilder();

		for (String hxid : strList) {
			strBuilder.append(hxid).append("$");
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(column, strBuilder.toString());

			db.update(UserDao.PREF_TABLE_NAME, values, null, null);
		}
	}

	synchronized private List<String> getList(String column) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select " + column + " from "
				+ UserDao.PREF_TABLE_NAME, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}

		String strVal = cursor.getString(0);
		if (strVal == null || strVal.equals("")) {
			return null;
		}

		cursor.close();

		String[] array = strVal.split("$");

		if (array != null && array.length > 0) {
			List<String> list = new ArrayList<String>();
			for (String str : array) {
				list.add(str);
			}

			return list;
		}

		return null;
	}

	/**
	 * 保存邀请信息message
	 * 
	 * @param message
	 * @return 返回这条messaged在db中的id
	 */
	public synchronized Integer saveAppointmentInvitationMsg(
			AppointmentInvitMessage message) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int id = -1;
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(AppointmentInvitationDao.COLUMN_NAME_FROM_HXID,
					message.getFromContactHxid());
			values.put(
					AppointmentInvitationDao.COLUMN_NAME_FROM_CONTACT_NIKE_NAME,
					message.getFromContactUserNikeName());
			values.put(AppointmentInvitationDao.COLUMN_NAME_REASON,
					message.getReason());
			values.put(AppointmentInvitationDao.COLUMN_NAME_FROM_FACE,
					message.getFromContactFacePath());
			values.put(AppointmentInvitationDao.COLUMN_NAME_TIME,
					message.getTime());
			values.put(AppointmentInvitationDao.COLUMN_NAME_IS_INVITE_FROM_ME,
					message.getIsInviteFromMe());
			values.put(AppointmentInvitationDao.COLUMN_NAME_LOG_ID,
					message.getLogid());
			values.put(AppointmentInvitationDao.COLUMN_NAME_FROM_CONTACT_ID,
					message.getFromContactId());
			values.put(AppointmentInvitationDao.COLUMN_NAME_STATUS, message
					.getStatus().ordinal());
			db.insert(AppointmentInvitationDao.TABLE_NAME, null, values);

			Cursor cursor = db.rawQuery("select last_insert_rowid() from "
					+ AppointmentInvitationDao.TABLE_NAME, null);
			if (cursor.moveToFirst()) {
				id = cursor.getInt(0);
			}

			cursor.close();
		}
		return id;
	}

	/**
	 * 保存message
	 * 
	 * @param message
	 * @return 返回这条messaged在db中的id
	 */
	public synchronized Integer saveMessage(InviteMessage message) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int id = -1;
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(InviteMessgeDao.COLUMN_NAME_FROM_NIKE,
					message.getFromContactNick());
			values.put(InviteMessgeDao.COLUMN_NAME_FROM_FACE,
					message.getFromContactFacePath());
			values.put(InviteMessgeDao.COLUMN_NAME_GROUP_ID,
					message.getGroupId());
			values.put(InviteMessgeDao.COLUMN_NAME_GROUP_Name,
					message.getGroupName());
			values.put(InviteMessgeDao.COLUMN_NAME_FROM_HXID,
					message.getFromContactHxid());
			values.put(AppointmentInvitationDao.COLUMN_NAME_IS_INVITE_FROM_ME,
					message.getIsInviteFromMe());
			values.put(InviteMessgeDao.COLUMN_NAME_REASON, message.getReason());
			values.put(InviteMessgeDao.COLUMN_NAME_TIME, message.getTime());
			values.put(InviteMessgeDao.COLUMN_NAME_LOG_ID, message.getLogid());
			values.put(InviteMessgeDao.COLUMN_NAME_FROM_CONTACT_ID,
					message.getFromContactId());
			values.put(InviteMessgeDao.COLUMN_NAME_STATUS, message.getStatus()
					.ordinal());
			db.insert(InviteMessgeDao.TABLE_NAME, null, values);

			Cursor cursor = db.rawQuery("select last_insert_rowid() from "
					+ InviteMessgeDao.TABLE_NAME, null);
			if (cursor.moveToFirst()) {
				id = cursor.getInt(0);
			}

			cursor.close();
		}
		return id;
	}

	/**
	 * 更新message
	 * 
	 * @param msgId
	 * @param values
	 */
	synchronized public void updateMessage(int msgId, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.update(InviteMessgeDao.TABLE_NAME, values,
					InviteMessgeDao.COLUMN_NAME_ID + " = ?",
					new String[] { String.valueOf(msgId) });
		}
	}

	/**
	 * 获取messges
	 * 
	 * @return
	 */
	synchronized public List<InviteMessage> getMessagesList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<InviteMessage> msgs = new ArrayList<InviteMessage>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from "
					+ InviteMessgeDao.TABLE_NAME + " desc", null);
			while (cursor.moveToNext()) {
				InviteMessage msg = new InviteMessage();
				int id = cursor.getInt(cursor
						.getColumnIndex(InviteMessgeDao.COLUMN_NAME_ID));
				String fromNike = cursor.getString(cursor
						.getColumnIndex(InviteMessgeDao.COLUMN_NAME_FROM_NIKE));
				String friendid = cursor
						.getString(cursor
								.getColumnIndex(InviteMessgeDao.COLUMN_NAME_FROM_CONTACT_ID));
				String groupid = cursor.getString(cursor
						.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_ID));
				String logid = cursor.getString(cursor
						.getColumnIndex(InviteMessgeDao.COLUMN_NAME_LOG_ID));
				String hxid = cursor.getString(cursor
						.getColumnIndex(InviteMessgeDao.COLUMN_NAME_FROM_HXID));
				String fromface = cursor.getString(cursor
						.getColumnIndex(InviteMessgeDao.COLUMN_NAME_FROM_FACE));
				String groupname = cursor
						.getString(cursor
								.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_Name));
				String reason = cursor.getString(cursor
						.getColumnIndex(InviteMessgeDao.COLUMN_NAME_REASON));
				long time = cursor.getLong(cursor
						.getColumnIndex(InviteMessgeDao.COLUMN_NAME_TIME));
				int status = cursor.getInt(cursor
						.getColumnIndex(InviteMessgeDao.COLUMN_NAME_STATUS));
				msg.setFromContactHxid(hxid);
				msg.setId(id);
				msg.setFromContactFacePath(fromface);
				msg.setFromContactId(friendid);
				msg.setFromContactNick(fromNike);
				msg.setLogid(logid);
				msg.setGroupId(groupid);
				msg.setGroupName(groupname);
				msg.setReason(reason);
				msg.setTime(time);
				// 信息状态类型
				if (status == MesageStatus.BEINVITEED.ordinal())
					msg.setStatus(MesageStatus.BEINVITEED);
				else if (status == MesageStatus.BEAGREED.ordinal())
					msg.setStatus(MesageStatus.BEAGREED);
				else if (status == MesageStatus.BEREFUSED.ordinal())
					msg.setStatus(MesageStatus.BEREFUSED);
				else if (status == MesageStatus.AGREED.ordinal())
					msg.setStatus(MesageStatus.AGREED);
				else if (status == MesageStatus.REFUSED.ordinal())
					msg.setStatus(MesageStatus.REFUSED);
				else if (status == MesageStatus.BEAPPLYED.ordinal()) {
					msg.setStatus(MesageStatus.BEAPPLYED);
				}
				msgs.add(msg);
			}
			cursor.close();
		}
		return msgs;
	}

	/**
	 * 获取邀请messges
	 * 
	 * @return
	 */
	synchronized public List<AppointmentInvitMessage> getAppointmentInvitList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<AppointmentInvitMessage> msgs = new ArrayList<AppointmentInvitMessage>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from "
					+ AppointmentInvitationDao.TABLE_NAME + " desc", null);
			while (cursor.moveToNext()) {
				AppointmentInvitMessage msg = new AppointmentInvitMessage();
				int id = cursor
						.getInt(cursor
								.getColumnIndex(AppointmentInvitationDao.COLUMN_NAME_ID));
				String facepath = cursor
						.getString(cursor
								.getColumnIndex(AppointmentInvitationDao.COLUMN_NAME_FROM_FACE));
				String formNikeName = cursor
						.getString(cursor
								.getColumnIndex(AppointmentInvitationDao.COLUMN_NAME_FROM_CONTACT_NIKE_NAME));
				String friendid = cursor
						.getString(cursor
								.getColumnIndex(AppointmentInvitationDao.COLUMN_NAME_FROM_CONTACT_ID));
				String logid = cursor
						.getString(cursor
								.getColumnIndex(AppointmentInvitationDao.COLUMN_NAME_LOG_ID));
				String hxid = cursor
						.getString(cursor
								.getColumnIndex(AppointmentInvitationDao.COLUMN_NAME_FROM_HXID));
				String reason = cursor
						.getString(cursor
								.getColumnIndex(AppointmentInvitationDao.COLUMN_NAME_REASON));
				String isFromMe = cursor
						.getString(cursor
								.getColumnIndex(AppointmentInvitationDao.COLUMN_NAME_IS_INVITE_FROM_ME));

				long time = cursor
						.getLong(cursor
								.getColumnIndex(AppointmentInvitationDao.COLUMN_NAME_TIME));
				int status = cursor
						.getInt(cursor
								.getColumnIndex(AppointmentInvitationDao.COLUMN_NAME_STATUS));
				msg.setFromContactHxid(hxid);
				;
				msg.setId(id);
				msg.setIsInviteFromMe(isFromMe);
				msg.setFromContactFacePath(facepath);
				msg.setFromContactId(friendid);
				msg.setLogid(logid);
				msg.setReason(reason);
				msg.setTime(time);
				msg.setFromContactNick(formNikeName);
				if (status == MesageStatus.BEINVITEED.ordinal())
					msg.setStatus(MesageStatus.BEINVITEED);
				else if (status == MesageStatus.BEAGREED.ordinal())
					msg.setStatus(MesageStatus.BEAGREED);
				else if (status == MesageStatus.BEREFUSED.ordinal())
					msg.setStatus(MesageStatus.BEREFUSED);
				else if (status == MesageStatus.AGREED.ordinal())
					msg.setStatus(MesageStatus.AGREED);
				else if (status == MesageStatus.REFUSED.ordinal())
					msg.setStatus(MesageStatus.REFUSED);
				else if (status == MesageStatus.BEAPPLYED.ordinal()) {
					msg.setStatus(MesageStatus.BEAPPLYED);
				}
				msgs.add(msg);
			}
			cursor.close();
		}
		return msgs;
	}

	synchronized public void deleteMessage(String from) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(InviteMessgeDao.TABLE_NAME,
					InviteMessgeDao.COLUMN_NAME_FROM_HXID + " = ?",
					new String[] { from });
		}
	}

	synchronized public void deleteAllMessage() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(InviteMessgeDao.TABLE_NAME, null, null);
		}
	}

	synchronized public void closeDB() {
		if (dbHelper != null) {
			dbHelper.closeDB();
		}
	}

	/**
	 * Save Robot list
	 */
	synchronized public void saveRobotList(List<RobotUser> robotList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(UserDao.ROBOT_TABLE_NAME, null, null);
			for (RobotUser item : robotList) {
				ContentValues values = new ContentValues();
				values.put(UserDao.ROBOT_COLUMN_NAME_ID, item.getUsername());
				if (item.getNick() != null)
					values.put(UserDao.ROBOT_COLUMN_NAME_NICK, item.getNick());
				if (item.getAvatar() != null)
					values.put(UserDao.ROBOT_COLUMN_NAME_AVATAR,
							item.getAvatar());
				db.replace(UserDao.ROBOT_TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * load robot list
	 */
	synchronized public Map<String, RobotUser> getRobotList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, RobotUser> users = null;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from "
					+ UserDao.ROBOT_TABLE_NAME, null);
			if (cursor.getCount() > 0) {
				users = new HashMap<String, RobotUser>();
			}
			;
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor
						.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_ID));
				String nick = cursor.getString(cursor
						.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor
						.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_AVATAR));
				RobotUser user = new RobotUser();
				user.setUsername(username);
				user.setNick(nick);
				user.setAvatar(avatar);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}
				if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance()
							.get(headerName.substring(0, 1)).get(0).target
							.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}

				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}

	/**
	 * 更新约会邀请信息
	 * 
	 * @param msgId
	 * @param values
	 */
	synchronized public void updateAppointmentInvitationMessage(int msgId,
			ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.update(AppointmentInvitationDao.TABLE_NAME, values,
					AppointmentInvitationDao.COLUMN_NAME_ID + " = ?",
					new String[] { String.valueOf(msgId) });
		}

	}

	/**
	 * 删除约会要请信息
	 * 
	 * @param from
	 */
	synchronized public void deleteAppointmentInvitMessage(String from) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(AppointmentInvitationDao.TABLE_NAME,
					AppointmentInvitationDao.COLUMN_NAME_FROM_HXID + " = ?",
					new String[] { from });
		}
	}

}