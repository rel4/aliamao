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
package com.aliamauri.meat.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aliamauri.meat.db.dlapp.FindDao;
import com.aliamauri.meat.utils.UIUtils;

public class DbAppOpenHelper extends SQLiteOpenHelper {
	// private String CREATE_BOOK =
	// "create table play_cache.db(bookId integer primarykey,bookName text);";

	// private String CREATE_TEMP_BOOK = "alter table "
	// + CacheMessgeDao.TABLE_NAME + " rename to _temp_book";
	//
	// private String INSERT_DATA = "insert into " + CacheMessgeDao.TABLE_NAME
	// + " select *,'' from _temp_book";
	//
	// private String DROP_BOOK = "drop table _temp_book";
	private static final int DATABASE_VERSION = 6;
	private static DbAppOpenHelper instance;
	// private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
	// + UserDao.TABLE_NAME + " ("
	// + UserDao.COLUMN_NAME_NICK + " TEXT, "
	// + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
	// + UserDao.COLUMN_APP_ID + " TEXT, "
	// + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
	private static final String FINDINFO_TABLE_CREATE = "CREATE TABLE "
			+ FindDao.TABLENAME + " (" + FindDao.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + FindDao.APPID
			+ " TEXT, " + FindDao.APPNAME + " TEXT, " + FindDao.APPPACKAGE
			+ " TEXT, " + FindDao.STATE + " TEXT, " + FindDao.DOWNLOADURL
			+ " TEXT, " + FindDao.LOCALURL + " TEXT , " + FindDao.REMARK1
			+ " TEXT, " + FindDao.REMARK2 + " TEXT, " + FindDao.REMARK3
			+ " TEXT);";

	private DbAppOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
	}

	public static DbAppOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbAppOpenHelper(context.getApplicationContext());
		}
		return instance;
	}

	public static DbAppOpenHelper getInstance() {
		if (instance == null) {
			instance = new DbAppOpenHelper(UIUtils.getContext());
		}
		return instance;
	}

	private static String getUserDatabaseName() {
		return "app_cache.db";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(FINDINFO_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		switch (newVersion) {


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
