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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;

import com.aliamauri.meat.activity.IM.domain.InviteMessage;

public class InviteMessgeDao {
	public static final String TABLE_NAME = "new_friends_msgs";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_FROM_NIKE = "from_nike_name";
	public static final String COLUMN_NAME_FROM_FACE = "from_face";
	public static final String COLUMN_NAME_GROUP_ID = "group_id";
	public static final String COLUMN_NAME_GROUP_Name = "group_name";
	
	public static final String COLUMN_NAME_TIME = "time";
	public static final String COLUMN_NAME_FROM_HXID = "from_hxid";
	public static final String COLUMN_NAME_REASON = "reason";
	public static final String COLUMN_NAME_STATUS = "status";
	public static final String COLUMN_NAME_FROM_CONTACT_ID = "from_contact_id";
	public static final String COLUMN_NAME_LOG_ID = "logid";
	public static final String COLUMN_NAME_IS_INVITE_FROM_ME = "is_invite_from_me";
	public InviteMessgeDao(Context context){
	    MyDBManager.getInstance().onInit(context);
	}
	
	/**
	 * 保存message
	 * @param messaged
	 * @return  返回这条messaged在db中的id
	 */
	public Integer saveMessage(InviteMessage message){
		return MyDBManager.getInstance().saveMessage(message);
	}
	
	/**
	 * 更新message
	 * @param msgId
	 * @param values
	 */
	public void updateMessage(int msgId,ContentValues values){
	    MyDBManager.getInstance().updateMessage(msgId, values);
	}
	
	/**
	 * 获取messges
	 * @return
	 */
	public List<InviteMessage> getMessagesList(){
		return MyDBManager.getInstance().getMessagesList();
	}
	/**
	 * 删除消息
	 * @param from
	 */
	public void deleteMessage(String from){
	    MyDBManager.getInstance().deleteMessage(from);
	}
	/**
	 * 删除所有的消息
	 * @param from
	 */
	public void deleteAllMessage(){
	    MyDBManager.getInstance().deleteAllMessage();
	}
	/**
	 * 获取带环信ID的map
	 * @return
	 */
	public Map<String, InviteMessage> getInviteMessageHXMap(){
		Map<String, InviteMessage> hashMap = new HashMap<String, InviteMessage>();
		List<InviteMessage> messagesList = getMessagesList();
		for (InviteMessage inviteMessage : messagesList) {
			hashMap.put(inviteMessage.getFromContactHxid(), inviteMessage);
		}
		return hashMap;
	}
}
