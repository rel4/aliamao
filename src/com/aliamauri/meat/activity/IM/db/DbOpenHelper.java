/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aliamauri.meat.activity.IM.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aliamauri.meat.activity.IM.controller.SDKHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 4;
	private static DbOpenHelper instance;

	private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
			+ UserDao.TABLE_NAME + " (" + UserDao.COLUMN_NAME_NICK + " TEXT, "
			+ UserDao.COLUMN_NAME_AVATAR + " TEXT, "
			+ UserDao.COLUMN_NAME_CONTACT_TYPE + " INTEGER, "
			+ UserDao.COLUMN_NAME_NATIVE_AVATAR + " TEXT, "
			+ UserDao.COLUMN_NAME_FROM_COMTACT_ID + " TEXT, "
			+ UserDao.COLUMN_NAME_CONTACT_ADDRESS+ " TEXT, "
			+ UserDao.COLUMN_NAME_CONTACT_AGE + " TEXT, "
			+ UserDao.COLUMN_NAME_CONTACT_HOBBY + " TEXT, "
			+ UserDao.COLUMN_NAME_CONTACT_JOB + " TEXT, "
			+ UserDao.COLUMN_NAME_CONTACT_SEX + " TEXT, "
			+ UserDao.COLUMN_NAME_CONTACT_SHIELD_STATE + " TEXT, "
			+ UserDao.COLUMN_NAME_USER_NAME + " TEXT PRIMARY KEY);";

	private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
			+ InviteMessgeDao.TABLE_NAME + " ("
			+ InviteMessgeDao.COLUMN_NAME_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ InviteMessgeDao.COLUMN_NAME_FROM_NIKE + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_FROM_FACE + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_GROUP_Name + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_REASON + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_FROM_CONTACT_ID + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_LOG_ID + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_FROM_HXID + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_IS_INVITE_FROM_ME + " TEXT, "
			+ InviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER, "
			+ InviteMessgeDao.COLUMN_NAME_TIME + " TEXT); ";
	private static final String APPOINTMENT_INVITATION_CREATE = "CREATE TABLE "
			+ AppointmentInvitationDao.TABLE_NAME + " ("
			+ AppointmentInvitationDao.COLUMN_NAME_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ AppointmentInvitationDao.COLUMN_NAME_REASON + " TEXT, "
			+ AppointmentInvitationDao.COLUMN_NAME_FROM_CONTACT_NIKE_NAME
			+ " TEXT, " + AppointmentInvitationDao.COLUMN_NAME_FROM_CONTACT_ID
			+ " TEXT, " + AppointmentInvitationDao.COLUMN_NAME_LOG_ID
			+ " TEXT, " + AppointmentInvitationDao.COLUMN_NAME_FROM_HXID
			+ " TEXT, " + AppointmentInvitationDao.COLUMN_NAME_FROM_FACE
			+ " TEXT, " + AppointmentInvitationDao.COLUMN_NAME_STATUS
			+ " INTEGER, "
			+ AppointmentInvitationDao.COLUMN_NAME_IS_INVITE_FROM_ME
			+ " INTEGER, " + AppointmentInvitationDao.COLUMN_NAME_TIME
			+ " TEXT); ";

	private static final String ROBOT_TABLE_CREATE = "CREATE TABLE "
			+ UserDao.ROBOT_TABLE_NAME + " (" + UserDao.ROBOT_COLUMN_NAME_ID
			+ " TEXT PRIMARY KEY, " + UserDao.ROBOT_COLUMN_NAME_NICK
			+ " TEXT, " + UserDao.ROBOT_COLUMN_NAME_AVATAR + " TEXT);";

	private static final String CREATE_PREF_TABLE = "CREATE TABLE "
			+ UserDao.PREF_TABLE_NAME + " ("
			+ UserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
			+ UserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";

	private DbOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
	}

	public static DbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}

	private static String getUserDatabaseName() {
		return SDKHelper.getInstance().getHXId() + "_limao.db";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(USERNAME_TABLE_CREATE);
		db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
		db.execSQL(CREATE_PREF_TABLE);
		db.execSQL(ROBOT_TABLE_CREATE);
		db.execSQL(APPOINTMENT_INVITATION_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN "
					+ UserDao.COLUMN_NAME_AVATAR + " TEXT ;");
		}

		if (oldVersion < 3) {
			db.execSQL(CREATE_PREF_TABLE);
		}
		if (oldVersion < 4) {
			db.execSQL(ROBOT_TABLE_CREATE);
		}
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
