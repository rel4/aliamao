package com.aliamauri.meat.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliamauri.meat.activity.IM.db.AppointmentInvitationDao;
import com.aliamauri.meat.activity.IM.domain.AppointmentInvitMessage;
import com.aliamauri.meat.utils.UIUtils;

public class AppointmentManager {
	private final static AppointmentManager Manager = new AppointmentManager();
	private AppointmentInvitationDao invitationDao;

	public static AppointmentManager getInstance() {
		return Manager;

	}
	private AppointmentInvitationDao getAppointmentInvitationDao(){
		if (invitationDao==null) {
			invitationDao = new AppointmentInvitationDao(UIUtils.getContext());
		}
		return invitationDao;
	}

	/**
	 * 获取带环信id约会邀请信息Map
	 * 
	 * @return
	 */
	public Map<String, AppointmentInvitMessage> getAppointmentHXidMap() {
		HashMap<String, AppointmentInvitMessage> hashMap = new HashMap<String, AppointmentInvitMessage>();
		List<AppointmentInvitMessage> messagesList = getAppointmentInvitationDao()
				.getMessagesList();
		for (AppointmentInvitMessage appointmentInvitMessage : messagesList) {
			hashMap.put(appointmentInvitMessage.getFromContactHxid(),
					appointmentInvitMessage);
		}
		return hashMap;
	}

	/**
	 * 获取约会邀请信息HX ID List集合
	 * 
	 * @return
	 */
	public synchronized List<String> getAppointmentsHXIDList() {
		ArrayList<String> arrayList = new ArrayList<String>();
		List<AppointmentInvitMessage> messagesList = getAppointmentInvitationDao()
				.getMessagesList();
		for (AppointmentInvitMessage msg : messagesList) {
			arrayList.add(msg.getFromContactHxid());
		}
		return arrayList;
	}

	/**
	 * 获取带uid的约会邀请信息Map
	 * 
	 * @return
	 */
	public Map<String, AppointmentInvitMessage> getAppointmentsMap() {
		HashMap<String, AppointmentInvitMessage> hashMap = new HashMap<String, AppointmentInvitMessage>();
		List<AppointmentInvitMessage> messagesList = getAppointmentInvitationDao()
				.getMessagesList();
		for (AppointmentInvitMessage appointmentInvitMessage : messagesList) {
			hashMap.put(appointmentInvitMessage.getFromContactId(),
					appointmentInvitMessage);
		}
		return hashMap;
	}
	/**
	 * 删除约会邀请信息
	 * 
	 * @param hxid 环信ID
	 */
	public void deletedAppointment(String hxid){
		getAppointmentInvitationDao().deleteMessage(hxid);
		
	}

}
