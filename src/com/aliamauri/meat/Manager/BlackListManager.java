package com.aliamauri.meat.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.db.HXDao;
import com.aliamauri.meat.activity.IM.db.UserDao;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.activity.IM.domain.User.ContactType;
import com.aliamauri.meat.utils.UIUtils;

public class BlackListManager {
	private final static BlackListManager Manager = new BlackListManager();

	public static BlackListManager getInstance() {
		return Manager;

	}

	/**
	 * 添加单个联系人黑名单
	 */
	public void addContacttTOBlackList(String hxID) {
		if (TextUtils.isEmpty(hxID)) {
			return;
		}
		HXDao.getInstance().saveBlackListContact(hxID);
		Map<String, User> allHXUsersMap = ContactManager.getInstance()
				.getAllHXUsersMap();
		if (allHXUsersMap.containsKey(hxID)) {
			User user = allHXUsersMap.get(hxID);
			user.setContactType(ContactType.BLACK_TYPE);
			UserInfoManager.getInstance().setUserInfo(user);
			
		}
		
	}

	/**
	 * 保存集合联系人黑名单
	 * 
	 * @param userlist
	 */
	public void saveBlackList(Map<String, User> userlist) {
		ArrayList<String> hxids = new ArrayList<String>();
		ArrayList<User> users = new ArrayList<User>();
		for (String key : userlist.keySet()) {
			users.add(userlist.get(key));
			hxids.add(key);
		}
		HXDao.getInstance().saveBlackListContactList(hxids);
		UserDao dao = new UserDao(UIUtils.getContext());
		for (User user : users) {
			dao.saveContact(user);
		}
		SDKHelper.getInstance().notifyBlackListSyncListener(true);
	}

	/**
	 * 从本地数据删除黑名单关系
	 * 
	 * @param uid
	 */
	public void deleteConttactBlackLsit(String hxid) {
		ContactManager.getInstance().deleteContact(hxid);
		HXDao.getInstance().deleteBlackListContact(hxid);
		Map<String, User> localUsers = ((MySDKHelper) SDKHelper.getInstance())
				.getContactList();
		if (localUsers.containsKey(hxid)) {
			localUsers.remove(hxid);
			new UserDao(UIUtils.getContext()).deleteContact(hxid);
		}
	}

	/**
	 * 获取带环信ID的黑名单列表
	 */
	public Map<String, User> getHaveHxIdBlackListMap() {
		Map<String, User> hashMap = new HashMap<String, User>();

		Map<String, User> allHXUsersMap = ContactManager.getInstance()
				.getAllHXUsersMap();
		for (String hxid : getBlackListHXid()) {
			if (allHXUsersMap.containsKey(hxid)) {
				hashMap.put(hxid, allHXUsersMap.get(hxid));
			}
		}
		return hashMap;
	}

	/**
	 * 获取带uid的黑名单列表
	 */
	public Map<String, User> getHaveUidIdBlackListMap() {
		Map<String, User> hashMap = new HashMap<String, User>();

		Map<String, User> allHXUsersMap = ContactManager.getInstance()
				.getAllHXUsersMap();
		for (String hxid : getBlackListHXid()) {
			if (allHXUsersMap.containsKey(hxid)) {
				User user = allHXUsersMap.get(hxid);
				hashMap.put(user.getUserId(), user);
			}
		}
		return hashMap;
	}

	/**
	 * 获取黑名单列表
	 * 
	 * @return
	 */
	public List<User> getBlackListUser() {
		ArrayList<User> arrayList = new ArrayList<User>(getHaveHxIdBlackListMap().values());
		return arrayList;
	}

	/**
	 * 获取黑名单的环信ID列表
	 * 
	 * @return
	 */
	public List<String> getBlackListHXid() {
		return HXDao.getInstance().getBlackListContactList();
	}

}
