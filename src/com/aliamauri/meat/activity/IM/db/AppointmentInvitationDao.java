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

import android.content.ContentValues;
import android.content.Context;

import com.aliamauri.meat.activity.IM.domain.AppointmentInvitMessage;

public class AppointmentInvitationDao {
	public static final String TABLE_NAME = "appointment_Invitation_msgs";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_TIME = "time";
	public static final String COLUMN_NAME_FROM_HXID = "from_hx_id";
	public static final String COLUMN_NAME_FROM_CONTACT_NIKE_NAME = "from_contact_nike_name";
	public static final String COLUMN_NAME_REASON = "reason";
	public static final String COLUMN_NAME_STATUS = "status";
	public static final String COLUMN_NAME_FROM_CONTACT_ID = "from_contact_id";
	public static final String COLUMN_NAME_LOG_ID = "logid";
	public static final String COLUMN_NAME_FROM_FACE = "from_face_path";
	public static final String COLUMN_NAME_IS_INVITE_FROM_ME = "is_invite_from_me";

	public AppointmentInvitationDao(Context context) {
		MyDBManager.getInstance().onInit(context);
	}

	/**
	 * 保存约会邀请message
	 * 
	 * @param message
	 * @return 返回这条messaged在db中的id
	 */
	public Integer saveMessage(AppointmentInvitMessage message) {
		return MyDBManager.getInstance().saveAppointmentInvitationMsg(message);
	}

	/**
	 * 更新约会邀请message
	 * 
	 * @param msgId
	 * @param values
	 */
	public void updateMessage(int msgId, ContentValues values) {
		MyDBManager.getInstance().updateAppointmentInvitationMessage(msgId,
				values);
	}

	/**
	 * 获取约会邀请messges +@return
	 */
	public List<AppointmentInvitMessage> getMessagesList() {
		return MyDBManager.getInstance().getAppointmentInvitList();
	}

	/**
	 * 删除约会邀请信息
	 * 
	 * @param hxid 环信ID
	 */
	public void deleteMessage(String hxid) {
		MyDBManager.getInstance().deleteAppointmentInvitMessage(hxid);
	}
}
