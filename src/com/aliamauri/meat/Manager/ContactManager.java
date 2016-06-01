package com.aliamauri.meat.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.text.TextUtils;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.IM.Constant;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.db.InviteMessgeDao;
import com.aliamauri.meat.activity.IM.db.UserDao;
import com.aliamauri.meat.activity.IM.domain.AppointmentInvitMessage;
import com.aliamauri.meat.activity.IM.domain.InviteMessage;
import com.aliamauri.meat.activity.IM.domain.Message;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.activity.IM.domain.User.ContactType;
import com.aliamauri.meat.bean.ContactInfoBean;
import com.aliamauri.meat.bean.cmd.ContactInvitBean;
import com.aliamauri.meat.bean.cmd.ContactInvitsBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.util.HanziToPinyin;
import com.lidroid.xutils.http.RequestParams;

/**
 * 好友管理
 * 
 * @author jjm
 * 
 */
public class ContactManager {
	public ContactManagerCallBack mCallBack;
	public ContactManagerDataCallBack mDataCallBack;

	/**
	 * 联系人管理回调
	 * 
	 * @author jjm
	 */
	public interface ContactManagerCallBack {
		void onState(boolean isSucceed);
	}

	/**
	 * 携带数据回调
	 * 
	 * @author jjm
	 * 
	 * @param <T>
	 */
	public interface ContactManagerDataCallBack<T> {
		void onData(boolean isSucceed, T t);
	}

	private ContactManager() {
	}

	private final static ContactManager Manager = new ContactManager();
	private UserDao userDao;
	private HttpHelp mHelp;

	public static ContactManager getInstance() {
		return Manager;

	}

	/**
	 * 通过HX获取user信息
	 * 
	 * @param usernameList
	 * @return
	 */
	public void getHxToInfos(List<String> usernameList,
			final ContactManagerDataCallBack managerCallBack) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < usernameList.size(); i++) {
			sb.append(usernameList.get(i));
			if (i != usernameList.size() - 1) {
				sb.append("|||");
			}
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter("hxuids", sb.toString());
		getHttpHelp().sendPost(NetworkConfig.getHxToInfosUrl(), params,
				ContactInvitsBean.class,
				new MyRequestCallBack<ContactInvitsBean>() {

					@Override
					public void onSucceed(ContactInvitsBean bean) {
						if (bean == null || bean.cont == null
								|| bean.cont.size() == 0) {
							if (managerCallBack != null) {
								managerCallBack.onData(false, null);
							}
						} else {
							if (managerCallBack != null) {
								managerCallBack.onData(true, bean);
							}
						}
					}
				});
	}

	private HttpHelp getHttpHelp() {
		if (mHelp == null) {
			mHelp = new HttpHelp();
		}
		return mHelp;
	}

	/**
	 * 是否是有效用户
	 * 
	 * @param userid
	 * @return
	 */
	private boolean isInvalidUser(String userid) {
		boolean flag = false;
		if (TextUtils.isEmpty(userid)) {
			if (mCallBack != null) {
				mCallBack.onState(false);
			}
			UIUtils.showToast(UIUtils.getContext(), "用户名不能为空");
			return flag;
		}
		String localusr = PrefUtils.getString(GlobalConstant.USER_ID, null);
		if (!TextUtils.isEmpty(localusr) && localusr.equals(userid)) {
			UIUtils.showToast(UIUtils.getContext(), UIUtils.getContext()
					.getString(R.string.not_add_myself));
			return flag;
		}
		Map<String, User> contactList = ((MySDKHelper) SDKHelper.getInstance())
				.getContactList();
		for (User user : contactList.values()) {
			String appID = user.getUserId();
			if (!TextUtils.isEmpty(appID) && appID.equals(userid)) {
				UIUtils.showToast(UIUtils.getContext(), UIUtils.getContext()
						.getString(R.string.This_user_is_already_your_friend));
				return flag;
			}
		}
		return !flag;
	}

	/**
	 * 删除联系人
	 * 
	 * @param uid
	 */
	public void deleteContact(String uid) {
		// 被删除
		if (TextUtils.isEmpty(uid)) {
			return;
		}
		Map<String, User> localUsers = ((MySDKHelper) SDKHelper.getInstance())
				.getContactList();

		Map<String, User> allContactUidMap = getAllContactUidMap();
		if (!allContactUidMap.containsKey(uid)) {
			return;
		}
		String username = allContactUidMap.get(uid).getUsername();
		if (!TextUtils.isEmpty(username)) {
			new InviteMessgeDao(UIUtils.getContext()).deleteMessage(username);
			EMChatManager.getInstance().deleteConversation(username);
			localUsers.remove(username);
			if (userDao == null) {
				userDao = new UserDao(UIUtils.getContext());
			}
			userDao.deleteContact(username);
		}
		SDKHelper.getInstance().notifyContactsSyncListener(true);

	}

	/**
	 * 通过id获取信息
	 * 
	 * @param uid
	 * @param managerCallBack
	 */
	public void findContactbyId(String uid,
			final ContactManagerDataCallBack managerCallBack) {
		if (TextUtils.isEmpty(uid)) {
			return;
		}
		getHttpHelp().sendGet(NetworkConfig.getFindFriend(uid),
				ContactInfoBean.class,
				new MyRequestCallBack<ContactInfoBean>() {

					@Override
					public void onSucceed(ContactInfoBean bean) {
						if (bean == null || bean.cont == null
								|| bean.cont.size() == 0) {
							if (managerCallBack != null) {
								managerCallBack.onData(false, null);
							}
							return;
						}
						if (managerCallBack != null) {
							managerCallBack.onData(true, bean);
						}
					}
				});

	}

	/**
	 * 通过信息添加联系人
	 * 
	 * @param msg
	 */
	public void contactAdded(Message msg) {
		// 保存增加的联系人
		Map<String, User> localUsers = ((MySDKHelper) SDKHelper.getInstance())
				.getContactList();
		Map<String, User> toAddUsers = new HashMap<String, User>();
		User user = setUserHead(msg);
		// 添加好友时可能会回调added方法两次
		if (!localUsers.containsKey(msg.getFromContactHxid())) {
			if (userDao == null) {
				userDao = new UserDao(UIUtils.getContext());
			}
			userDao.saveContact(user);
		}
		toAddUsers.put(msg.getFromContactHxid(), user);
		localUsers.putAll(toAddUsers);
	}

	/**
	 * 消息-设置用户头信息
	 * 
	 * @param msg
	 * @return
	 */
	public User setUserHead(Message msg) {
		User user = new User();
		user.setUsername(msg.getFromContactHxid());
		if (msg.getClass().getName() == InviteMessage.class.getName()) {
			user.setContactType(ContactType.FRIEND_TYPE);
		} else if (msg.getClass().getName() == AppointmentInvitMessage.class
				.getName()) {
			user.setContactType(ContactType.FRIEND_TYPE);
		}
		String nickname = msg.getFromContactNick();
		if (nickname == null || "".equals(nickname)) {
			nickname = msg.getFromContactId();
		}
		user.setNick(nickname);
		user.setUserId(msg.getFromContactId());

		user.setAvatar(msg.getFromContactFacePath());
		if (TextUtils.isEmpty(user.getNativeAvatar())
				&& !TextUtils.isEmpty(user.getAvatar())) {
//			UserInfoManager.getInstance().upAvatarNative(user);
		}
		if (msg.getFromContactFacePath() != null
				&& !"".equals(msg.getFromContactFacePath())) {
			user.setAvatar(msg.getFromContactFacePath());
		}
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (Constant.NEW_FRIENDS_USERNAME.equals(msg.getFromContactNick())) {
			user.setHeader("");
		} else if (Constant.APPOINTMENT_INVITATION.equals(msg
				.getFromContactNick())) {
			user.setHeader("");
		} else if (Constant.GROUP_USERNAME.equals(msg.getFromContactNick())) {
			user.setHeader("");
		} else if (TextUtils.isEmpty(user.getUserId())) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
		return user;
	}

	/**
	 * 透传-设置用户头信息
	 * 
	 * @param contact
	 *            .nickname
	 * @return
	 */
	public User setUserHead(ContactInvitBean contact) {
		User user = new User();
		user.setUsername(contact.fromuidhx);
		user.setContactType(contact.contactType);
		user.setUserId(contact.fromuid);
		String nickname = contact.fromnickname;
		if (TextUtils.isEmpty(nickname)) {
			nickname = contact.fromuid;
		}
		user.setNick(nickname);
		user.setUserId(contact.fromuid);
		user.setAvatar(contact.fromface);
		/*********************************************/
		user.setAddress(contact.frompland);
		user.setAge(contact.frombirth);
		user.setHobby(contact.fromhobby);
		user.setJob(contact.fromjob);
		user.setSex(contact.fromsex);

		if (!TextUtils.isEmpty(user.getAvatar())
				&& TextUtils.isEmpty(user.getNativeAvatar())) {
//			UserInfoManager.getInstance().upAvatarNative(user);
		}
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUserId();
		}
		if (Constant.NEW_FRIENDS_USERNAME.equals(contact.fromnickname)) {
			user.setHeader("");
		} else if (Constant.APPOINTMENT_INVITATION.equals(contact.fromnickname)) {
			user.setHeader("");
		} else if (Constant.GROUP_USERNAME.equals(contact.fromnickname)) {
			user.setHeader("");
		} else if (TextUtils.isEmpty(user.getUserId())) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
		return user;
	}

	/**
	 * 获取带环信ID所有用户
	 * 
	 * @return
	 */
	public Map<String, User> getAllHXUsersMap() {
		return ((MySDKHelper) SDKHelper.getInstance()).getContactList();
	}

	/**
	 * 获取所有用户list集合
	 * 
	 * @return
	 */
	public List<User> getAllContactList() {
		ArrayList<User> arrayList = new ArrayList<User>();
		arrayList.addAll(getAllHXUsersMap().values());
		return arrayList;
	}

	/**
	 * 获取带uid的用户Map集合
	 * 
	 * @return <用户uid,user>
	 */
	public Map<String, User> getAllContactUidMap() {
		HashMap<String, User> userMap = new HashMap<String, User>();
		List<User> usersList = getAllContactList();
		for (User user : usersList) {
			userMap.put(user.getUserId(), user);
		}
		return userMap;
	}

	/**
	 * 获取好友列表
	 * 
	 * @return
	 */
	public List<User> getFriendContact() {
		List<User> contactList = new ArrayList<User>();
		Map<String, User> users = ((MySDKHelper) SDKHelper.getInstance())
				.getContactList();
		Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, User> entry = iterator.next();
			if (!Constant.NEW_FRIENDS_USERNAME.equals(entry.getKey())
					&& !Constant.GROUP_USERNAME.equals(entry.getKey())
					&& !Constant.CHAT_ROOM.equals(entry.getKey())
					&& !Constant.CHAT_ROBOT.equals(entry.getKey())
					&& !Constant.APPOINTMENT_INVITATION.equals(entry.getKey())
					&& !BlackListManager.getInstance().getBlackListHXid()
							.contains(entry.getKey())
					&& !AppointmentManager.getInstance()
							.getAppointmentsHXIDList().contains(entry.getKey()))
				contactList.add(entry.getValue());
		}

		return contactList;
	}

	/**
	 * 获取好友带UID map 集合
	 * 
	 * @return
	 */
	public Map<String, User> getFriendContactUidMap() {
		Map<String, User> contactList = new HashMap<String, User>();
		Map<String, User> users = ((MySDKHelper) SDKHelper.getInstance())
				.getContactList();
		for (String key : users.keySet()) {
			if (!Constant.NEW_FRIENDS_USERNAME.endsWith(key)
					&& !Constant.CHAT_ROOM.equals(key)
					&& !Constant.APPOINTMENT_INVITATION.equals(key)) {
				User user = users.get(key);
				contactList.put(user.getUserId(), user);
			}
		}

		// Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
		// while (iterator.hasNext()) {
		// Entry<String, User> entry = iterator.next();
		// if (!Constant.NEW_FRIENDS_USERNAME.equals(entry.getKey())
		// && !Constant.GROUP_USERNAME.equals(entry.getKey())
		// && !Constant.CHAT_ROOM.equals(entry.getKey())
		// && !Constant.CHAT_ROBOT.equals(entry.getKey())
		// && !Constant.APPOINTMENT_INVITATION.equals(entry.getKey())
		// && !BlackListManager.getInstance().getBlackListHXid()
		// .contains(entry.getKey())
		// && !AppointmentManager.getInstance()
		// .getAppointmentsHXIDList().contains(entry.getKey()))
		// contactList.put(entry.getKey(),entry.getValue());
		// }

		return contactList;
	}

	/**
	 * 获取好友带HXID map 集合
	 * 
	 * @return
	 */
	public Map<String, User> getFriendContactHXidMap() {
		Map<String, User> contactList = new HashMap<String, User>();
		Map<String, User> users = ((MySDKHelper) SDKHelper.getInstance())
				.getContactList();
		for (String key : users.keySet()) {
			if (!Constant.NEW_FRIENDS_USERNAME.endsWith(key)
					&& !Constant.CHAT_ROOM.equals(key)
					&& !Constant.APPOINTMENT_INVITATION.equals(key)) {
				User user = users.get(key);
				contactList.put(key, user);
			}
		}
		return contactList;
	}
	private  void setUserInfo(User newUser){
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((MySDKHelper) SDKHelper.getInstance()).saveContact(newUser);
	}
	/**
	 * 更新用户名字
	 * @param uid
	 */
	public boolean updateUserNike(String uid,String newName){
		boolean flag = false;
		User user = getAllContactUidMap().get(uid);
		if (user!=null) {
			user.setNick(newName);
			setUserInfo(user);
			flag =true;
		}
		return flag;
	}
 
}
