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
package com.aliamauri.meat.activity.IM.domain;

import com.easemob.chat.EMContact;

public class User extends EMContact {
	/**
	 * 邀请信息数
	 */
	private int appointmentMsgCount;
	/**
	 * 信息数
	 */
	private int unreadMsgCount;
	/**
	 * 头标
	 */
	private String header;
	/**
	 * 头像路径
	 */
	private String avatar;
	/**
	 * 本地头像路径
	 */
	private String nativeAvatar;
	/**
	 * 用户id
	 */
	private String userId;
	/**
	 * 用户关系
	 */
	private ContactType contactType;
	
	
	private String sex;
	
	private String address;
	
	private String job;
	
	private String age;
	
	private String hobby;
	
	private String shieldState;

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String getShieldState() {
		return shieldState;
	}

	public void setShieldState(String shieldState) {
		this.shieldState = shieldState;
	}

	public ContactType getContactType() {
		return contactType;
	}

	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNativeAvatar() {
		return nativeAvatar;
	}

	public void setNativeAvatar(String nativeAvatar) {
		this.nativeAvatar = nativeAvatar;
	}

	public User() {
	}

	public User(String username) {
		this.username = username;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		return getUsername().equals(((User) o).getUsername());
	}

	@Override
	public String toString() {
		return nick == null ? username : nick;
	}

	/**
	 * 获取邀请信息数
	 * 
	 * @return
	 */
	public int getAppointmentMsgCount() {
		// TODO Auto-generated method stub
		return appointmentMsgCount;
	}

	/**
	 * 设置邀请信息数
	 * 
	 * @param appointmentMsgCount
	 */
	public void setAppointmentMsgCount(int appointmentMsgCount) {
		this.appointmentMsgCount = appointmentMsgCount;

	}

	/**
	 * 联系人关系
	 * 
	 * @author admin
	 * 
	 */
	public enum ContactType {
		/**
		 * 好友关系
		 */
		FRIEND_TYPE,
		/**
		 * 约会关系
		 */
		APPOINTMENT_TYPE,
		/**
		 * 黑名单关系
		 */
		BLACK_TYPE;
	}
}
