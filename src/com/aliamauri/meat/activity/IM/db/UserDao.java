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

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.aliamauri.meat.activity.IM.domain.RobotUser;
import com.aliamauri.meat.activity.IM.domain.User;

public class UserDao {
	public static final String TABLE_NAME = "uers";
	/**
	 * 环信id
	 */
	public static final String COLUMN_NAME_USER_NAME = "user_name";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	public static final String COLUMN_NAME_CONTACT_TYPE = "contact_type";
	public static final String COLUMN_NAME_NATIVE_AVATAR = "native_avatar";
	public static final String COLUMN_NAME_FROM_COMTACT_ID = "from_contact_id";
	public static final String COLUMN_NAME_CONTACT_SEX = "contact_sex";
	public static final String COLUMN_NAME_CONTACT_JOB = "contact_job";
	public static final String COLUMN_NAME_CONTACT_AGE = "contact_age";
	public static final String COLUMN_NAME_CONTACT_ADDRESS = "contact_address";
	public static final String COLUMN_NAME_CONTACT_HOBBY = "contact_hobby";
	public static final String COLUMN_NAME_CONTACT_SHIELD_STATE = "contact_shield_state";

	public static final String PREF_TABLE_NAME = "pref";
	public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
	public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

	public static final String ROBOT_TABLE_NAME = "robots";
	public static final String ROBOT_COLUMN_NAME_ID = "user_name";
	public static final String ROBOT_COLUMN_NAME_NICK = "nick";
	public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";

	public UserDao(Context context) {
		MyDBManager.getInstance().onInit(context);
	}

	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<User> contactList) {
		MyDBManager.getInstance().saveContactList(contactList);
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {

		return MyDBManager.getInstance().getContactList();
	}

	/**
	 * 获取用户信息 hxid 环信ID
	 * 
	 * @return
	 */
	public User getContactInfo(String hxid) {

		return MyDBManager.getInstance().getContactInfo(hxid);
	}

	/**
	 * 删除一个联系人
	 * 
	 * @param username
	 */
	public void deleteContact(String username) {
		MyDBManager.getInstance().deleteContact(username);
	}

	/**
	 * 保存一个联系人
	 * 
	 * @param user
	 */
	public void saveContact(User user) {
		MyDBManager.getInstance().saveContact(user);
	}


	/**
	 * 更新一个联系人
	 * 
	 * @param user
	 */
	public void updateContact(User user) {
		MyDBManager.getInstance().updateContact(user);
	}

	public void setDisabledGroups(List<String> groups) {
		MyDBManager.getInstance().setDisabledGroups(groups);
	}

	public List<String> getDisabledGroups() {
		return MyDBManager.getInstance().getDisabledGroups();
	}

	public void setDisabledIds(List<String> ids) {
		MyDBManager.getInstance().setDisabledIds(ids);
	}

	public List<String> getDisabledIds() {
		return MyDBManager.getInstance().getDisabledIds();
	}

	public Map<String, RobotUser> getRobotUser() {
		return MyDBManager.getInstance().getRobotList();
	}

	public void saveRobotUser(List<RobotUser> robotList) {
		MyDBManager.getInstance().saveRobotList(robotList);
	}
}
