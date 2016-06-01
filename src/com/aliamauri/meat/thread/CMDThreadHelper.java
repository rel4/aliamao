package com.aliamauri.meat.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.AppointmentManager;
import com.aliamauri.meat.Manager.BlackListManager;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.activity.IM.Constant;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.activity.IM.activity.ChatActivity;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.db.AppointmentInvitationDao;
import com.aliamauri.meat.activity.IM.db.InviteMessgeDao;
import com.aliamauri.meat.activity.IM.db.UserDao;
import com.aliamauri.meat.activity.IM.domain.AppointmentInvitMessage;
import com.aliamauri.meat.activity.IM.domain.InviteMessage;
import com.aliamauri.meat.activity.IM.domain.Message.MesageStatus;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.activity.IM.domain.User.ContactType;
import com.aliamauri.meat.activity.IM.utils.UserUtils;
import com.aliamauri.meat.bean.ContactInfoBean.ContactInfo;
import com.aliamauri.meat.bean.cmd.ContactInvitBean;
import com.aliamauri.meat.global.CmdActionGlobal;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.parse.JsonParse;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.MapUtils;
import com.aliamauri.meat.utils.MessageUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.utils.UUIDUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.HanziToPinyin;

public class CMDThreadHelper {

	private static CMDThreadHelper instance;
	ExecutorService executorService = Executors.newSingleThreadExecutor();
	// 邀请dao
	private InviteMessgeDao mInviteMessgeDao;
	private UserDao mUserDao;
	private AppointmentInvitationDao mAppointmentInvitationDao;
	private Handler mHandler;

	synchronized public static CMDThreadHelper getInstance() {
		if (instance == null) {
			instance = new CMDThreadHelper();
		}
		return instance;
	}

	// 任务列表
	private Map<String, CMDTask> queueMap = new HashMap<String, CMDTask>();

	// 没有任务
	// public final static int FLAG_NOT_TASK = -1;
	// 更新联系列表
	public final static int FLAG_PARSE_CONTACTLIST = 1;
	// 更新本地头像
	public final static int FLAG_UP_AVATAR_NATIVE = 2;
	// 联系人请求
	public final static int FLAG_CONTACT_INVITE = 3;
	// 联系人添加请求
	public final static int FLAG_CONTACT_ADD = 4;
	// 约会消息请求
	public final static int FLAG_APPOINTMENT_INVITATION_MSG = 5;
	// 联系人删除请求
	public final static int FLAG_CONTACT_DELETE = 6;
	// 联系人添加到黑名单
	public final static int FLAG_ADD_CONTACT_TO_BLACKLIST = 7;

	// 处理黑名单信息
	public final static int FLAG_DISPOSE_BLACK_LIST = 8;

	/**
	 * 添加任务
	 * 
	 * @param mTaskType
	 * @param obj
	 */
	public synchronized void addTask(int mTaskType, Object obj) {
		if (obj != null) {
			CMDTask cmdTask = new CMDTask();
			cmdTask.setObj(obj);
			cmdTask.setTaskFlag(mTaskType);
			queueMap.put(UUIDUtils.getUUID(), cmdTask);
		}
		if (!isHaveTask) {
			isHaveTask = true;
			runNextTask();
		}
	}

	private boolean isHaveTask = false;

	private synchronized void runNextTask() {
		if (queueMap.size() != 0) {
			runTask();
		} else {
			isHaveTask = false;
		}
	}

	private void runTask() {

		executorService.execute(new Runnable() {

			@Override
			public void run() {
				if (queueMap.size() != 0) {
					LogUtil.e(this, "线程池做任务前的任务数  ：" + queueMap.size() + "个");

					if (LogUtil.getDeBugState()) {
						long currentTime = System.currentTimeMillis();
						String firstOrKey = MapUtils.getFirstOrKey(queueMap);
						CMDTask cmdTask = queueMap.get(firstOrKey);
						disposeTask(cmdTask.getTaskFlag(), cmdTask.getObj());
						queueMap.remove(firstOrKey);
						LogUtil.e(
								this,
								"子线程处理任务花费的时间   ："
										+ (System.currentTimeMillis() - currentTime));
					} else {

						String firstOrKey = MapUtils.getFirstOrKey(queueMap);
						CMDTask cmdTask = queueMap.get(firstOrKey);
						disposeTask(cmdTask.getTaskFlag(), cmdTask.getObj());
						queueMap.remove(firstOrKey);
					}
					LogUtil.e(this, "线程池做任务后任务数  ：" + queueMap.size() + "个");
					runNextTask();
				}
			}
		});

	}

	/**
	 * 处理任务
	 * 
	 * @param taskType
	 * @param curentobj
	 */
	@SuppressWarnings("unchecked")
	synchronized private void disposeTask(int taskType, Object curentobj) {
		LogUtil.e("CMDThreadHelper",
				"run()是否子线程    =  "
						+ (Thread.currentThread() != Looper.getMainLooper()
								.getThread()));
		switch (taskType) {
		case FLAG_PARSE_CONTACTLIST:
			if (curentobj != null && curentobj instanceof List) {
				List<ContactInfo> infos = (List<ContactInfo>) curentobj;
				disposeAsyncFetchContacts(infos);
			}
			break;
		case FLAG_UP_AVATAR_NATIVE:
			if (curentobj != null && curentobj instanceof User) {
				User user = (User) curentobj;
				disposeUserUpavaterNative(user);
			}
			break;

		case FLAG_CONTACT_INVITE:
			if (curentobj != null && curentobj instanceof ContactInvitBean) {
				ContactInvitBean contactInvitBean = (ContactInvitBean) curentobj;
				disposeContactInvite(contactInvitBean);
			}
			break;
		case FLAG_CONTACT_ADD:
			if (curentobj != null && curentobj instanceof ContactInvitBean) {
				ContactInvitBean contactInvitBean = (ContactInvitBean) curentobj;
				disposeContactAdd(contactInvitBean);
			}
			break;
		case FLAG_APPOINTMENT_INVITATION_MSG:
			if (curentobj != null && curentobj instanceof ContactInvitBean) {
				ContactInvitBean contactInvitBean = (ContactInvitBean) curentobj;
				disposeAppointmentInvitationMsg(contactInvitBean);
			}
			break;
		case FLAG_CONTACT_DELETE:
			if (curentobj != null && curentobj instanceof ContactInvitBean) {
				ContactInvitBean contactInvitBean = (ContactInvitBean) curentobj;
				disposeContactDelete(contactInvitBean);
			}
			break;
		case FLAG_ADD_CONTACT_TO_BLACKLIST:
			if (curentobj != null && curentobj instanceof ContactInvitBean) {
				ContactInvitBean contactInvitBean = (ContactInvitBean) curentobj;
				disposeAddContactToBlackList(contactInvitBean);
			}
			break;
		case FLAG_DISPOSE_BLACK_LIST:
			if (curentobj != null && curentobj instanceof List) {
				List<ContactInfo> infos = (List<ContactInfo>) curentobj;
				disposeAsyncFetchBlackContacts(infos);
			}
			break;

		}

	}

	/**
	 * 处理黑名单
	 * 
	 * @param infos
	 */
	private void disposeAsyncFetchBlackContacts(List<ContactInfo> infos) {
		Map<String, User> userlist = new HashMap<String, User>();
		for (int i = 0; i < infos.size(); i++) {
			ContactInfo info = infos.get(i);
			User user = new User();
			user.setNick(info.nickname);
			user.setUsername(info.useridhx);
			user.setAvatar(info.face);
			user.setUserId(info.uid);
			user.setContactType(ContactType.BLACK_TYPE);
			setUserHearder(info.nickname, user);
			userlist.put(info.useridhx, user);
			// UserInfoManager.getInstance().upAvatarNative(user);
		}

		BlackListManager.getInstance().saveBlackList(userlist);
		Map<String, User> localUsers = ((MySDKHelper) SDKHelper.getInstance())
				.getContactList();
		localUsers.putAll(userlist);
		UserDao dao = new UserDao(UIUtils.getContext());
		for (String key : userlist.keySet()) {
			dao.saveContact(userlist.get(key));
		}
		if (mHandler != null) {
			mHandler.sendEmptyMessage(MainActivity.MSG_NOTIFY_BLACKLIST_AND_BLACK);
		}
		if (SDKHelper.getInstance().isBlackListSyncedWithServer()) {
			SDKHelper.getInstance().notifyForRecevingEvents();
		}

	}

	/**
	 * 更新
	 * 
	 * @param user
	 */
	private void disposeUserUpavaterNative(User user) {
		UserDao mUserDao = new UserDao(UIUtils.getContext());
		mUserDao.updateContact(user);

	}

	private InviteMessgeDao getInviteMessgeDao() {
		if (mInviteMessgeDao == null) {
			mInviteMessgeDao = new InviteMessgeDao(UIUtils.getContext());
		}
		return mInviteMessgeDao;
	}

	private UserDao getUserDao() {
		if (mUserDao == null) {
			mUserDao = new UserDao(UIUtils.getContext());
		}
		return mUserDao;
	}

	private AppointmentInvitationDao getAppointmentInvitationDao() {
		if (mAppointmentInvitationDao == null) {
			mAppointmentInvitationDao = new AppointmentInvitationDao(
					UIUtils.getContext());

		}
		return mAppointmentInvitationDao;
	}

	/**
	 * 处理透传信息
	 * 
	 * @param action
	 */
	public void switchAction(String json) {

		ContactInvitBean mContactInvitBean = null;
		try {
			mContactInvitBean = (ContactInvitBean) JsonParse.parserJson(json,
					ContactInvitBean.class);
		} catch (JSONException e) {
			e.printStackTrace();
			if (LogUtil.getDeBugState()) {
				throw new RuntimeException();
			}
		}
		if (mContactInvitBean == null) {
			return;
		}
		LogUtil.e(this, "  action  =  " + mContactInvitBean.action);
		switch (mContactInvitBean.action) {

		case CmdActionGlobal.ACTION_CONTACT_INVIT:// 联系人好友请求
			Map<String, User> haveUidIdBlackList = BlackListManager
					.getInstance().getHaveUidIdBlackListMap();
			if (!haveUidIdBlackList.containsKey(mContactInvitBean.fromuid)) {
				showNotificationMsg(mContactInvitBean, "请求添加好友");
				addTask(FLAG_CONTACT_INVITE, mContactInvitBean);

			}
			break;
		case CmdActionGlobal.ACTION_CONTACT_ADD:// 添加联系人
			mContactInvitBean.contactType = ContactType.FRIEND_TYPE;
			showNotificationMsg(mContactInvitBean, "已添加您为好友");
			addTask(FLAG_CONTACT_ADD, mContactInvitBean);
			break;
		case CmdActionGlobal.ACTION_APPOINTMENT_INVIT:// 添加约会联系人
			Map<String, User> blackList = BlackListManager.getInstance()
					.getHaveUidIdBlackListMap();
			if (!blackList.containsKey(mContactInvitBean.fromuid)) {
				mContactInvitBean.contactType = ContactType.APPOINTMENT_TYPE;
				if ("0".equals(mContactInvitBean.isInviteFromMe)) {
					showNotificationMsg(mContactInvitBean, "约会邀请");
				}
				addTask(FLAG_APPOINTMENT_INVITATION_MSG, mContactInvitBean);
			}
			break;
		case CmdActionGlobal.ACTION_CONTACT_DELETED:// 联系人被删除
			addTask(FLAG_CONTACT_DELETE, mContactInvitBean);
			break;
		case CmdActionGlobal.ACTION_ADD_BLACK_LIST:// 添加黑名單列表
			addTask(FLAG_ADD_CONTACT_TO_BLACKLIST, mContactInvitBean);
			break;
		}

	}

	/**
	 * 添加黑名單列表
	 * 
	 * @param mContactInvitBean
	 */
	private void disposeAddContactToBlackList(ContactInvitBean mContactInvitBean) {
		LogUtil.e(this, "CMDThreadHelper处理" + mContactInvitBean.fromnickname
				+ " 添加黑名的消息");
		if ("0".equals(mContactInvitBean.isInviteFromMe)) {
			// 正常添加
			ContactManager.getInstance().deleteContact(
					mContactInvitBean.fromuid);
		} else if ("1".equals(mContactInvitBean.isInviteFromMe)) {
			// 反馈信息
			BlackListManager.getInstance().addContacttTOBlackList(
					mContactInvitBean.fromuidhx);
		}
		if (mHandler != null) {
			mHandler.sendEmptyMessage(MainActivity.MSG_NOTIFY_BLACKLIST_AND_BLACK);
		}

	}

	/**
	 * 联系人被删除
	 * 
	 * @param mContactInvitBean
	 */
	private void disposeContactDelete(ContactInvitBean contactInvitBean) {
		LogUtil.e(this, "CMDThreadHelper处理" + contactInvitBean.fromnickname
				+ "被删除的消息");
		// 被删除
		final String fromuid = contactInvitBean.fromuid;
		if (TextUtils.isEmpty(fromuid)) {
			return;
		}
		ContactManager.getInstance().deleteContact(fromuid);

		String st10 = UIUtils.getContext().getResources()
				.getString(R.string.have_you_removed);
		if (ChatActivity.activityInstance != null
				&& contactInvitBean.fromuidhx
						.equals(ChatActivity.activityInstance
								.getToChatUsername())) {
			User userInfo = UserUtils.getUserInfo(contactInvitBean.fromuidhx);
			Toast.makeText(
					MyApplication.getMainActivity(),
					TextUtils.isEmpty(userInfo.getNick()) ? userInfo.getNick()
							: userInfo.getUserId() + st10, 1).show();
			ChatActivity.activityInstance.finish();
		}
		// updateUnreadLabel();
		if (mHandler != null) {
			mHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_CHATLIST_AND_CONTACTLIST);
		}

	}

	/**
	 * 添加约会联系人
	 * 
	 * @param mContactInvitBean
	 */
	private void disposeAppointmentInvitationMsg(
			ContactInvitBean contactInvitBean) {
		LogUtil.e(this, "CMDThreadHelper处理" + contactInvitBean.fromnickname
				+ "约会添加的消息");
		// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
		List<AppointmentInvitMessage> msgs = getAppointmentInvitationDao()
				.getMessagesList();
		for (AppointmentInvitMessage message : msgs) {
			if (message.getFromContactHxid().equals(contactInvitBean.fromuidhx)) {
				getAppointmentInvitationDao().deleteMessage(
						message.getFromContactHxid());
			}
		}
		AppointmentInvitMessage msg = new AppointmentInvitMessage();
		String nickname = contactInvitBean.fromnickname;
		if (null == nickname || "".equals(nickname)) {
			nickname = contactInvitBean.fromuid;
		}
		msg.setIsInviteFromMe(contactInvitBean.isInviteFromMe);
		msg.setFromContactUserNikeName(nickname);
		msg.setFromContactId(contactInvitBean.fromuid);
		msg.setFromContactHxid(contactInvitBean.fromuidhx);
		msg.setLogid(contactInvitBean.logid);
		msg.setTime(System.currentTimeMillis());
		msg.setFromContactFacePath(contactInvitBean.fromface);
		msg.setReason(contactInvitBean.msg);
		msg.setStatus(MesageStatus.BEINVITEED);
		notifyNewAppointmentInvitationMsg(msg);

	}

	/**
	 * 处理接到被添加的消息
	 * 
	 * @param mContactInvitBean
	 */
	private void disposeContactAdd(ContactInvitBean contactInvitBean) {
		LogUtil.e(this, "CMDThreadHelper处理" + contactInvitBean.fromnickname
				+ "添加的消息");
		// 保存增加的联系人
		Map<String, User> toAddUsers = new HashMap<String, User>();
		User user = ContactManager.getInstance().setUserHead(contactInvitBean);
		Map<String, AppointmentInvitMessage> appointmentHXidMap = AppointmentManager
				.getInstance().getAppointmentHXidMap();

		// 是否包含约会邀请信息
		if (appointmentHXidMap.containsKey(user.getUsername())) {
			AppointmentManager.getInstance().deletedAppointment(
					user.getUsername());
			SDKHelper.getInstance().notifyAppointmentSyncListener(true);
			user.setContactType(ContactType.FRIEND_TYPE);
			getUserDao().updateContact(user);
		}
		Map<String, User> localUsers = ((MySDKHelper) SDKHelper.getInstance())
				.getContactList();
		// 添加好友时可能会回调added方法两次
		if (!localUsers.containsKey(contactInvitBean.fromuidhx)) {
			getUserDao().saveContact(user);
		}
		// 是否包含要求消息
		Map<String, InviteMessage> msgs = getInviteMessgeDao()
				.getInviteMessageHXMap();
		if (msgs.containsKey(contactInvitBean.fromuidhx)) {
			InviteMessage message = msgs.get(contactInvitBean.fromuidhx);
			MesageStatus status = message.getStatus();
			if (status == MesageStatus.BEINVITEED) {
				ContentValues values = new ContentValues();
				values.put(InviteMessgeDao.COLUMN_NAME_STATUS,
						MesageStatus.BEAGREED.ordinal());
				getInviteMessgeDao().updateMessage(message.getId(), values);
				// inviteMessgeDao.deleteMessage(contactInvitBean.fromuidhx);
				// user.setAppointmentMsgCount(0);
			}
		}
		toAddUsers.put(contactInvitBean.fromuidhx, user);
		localUsers.putAll(toAddUsers);
		// 是否包含
		Map<String, InviteMessage> messagesList = getInviteMessgeDao()
				.getInviteMessageHXMap();
		if (messagesList.containsKey(contactInvitBean.fromuidhx)) {
			MessageUtils.sendMessage(contactInvitBean.fromuidhx, "开始聊天吧！");
		}

		if (mHandler == null) {
			mHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_CHATLIST_AND_CONTACTLIST);
		}
	}

	/**
	 * 处理接到邀请的消息
	 * 
	 * @param contactInvitBean
	 */
	private void disposeContactInvite(ContactInvitBean contactInvitBean) {
		// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
		LogUtil.e(this, "CMDThreadHelper处理" + contactInvitBean.fromnickname
				+ "接到邀请的消息");
		List<InviteMessage> msgs = getInviteMessgeDao().getMessagesList();
		boolean isDeleteMessage = false;
		for (InviteMessage inviteMessage : msgs) {
			if (inviteMessage.getGroupId() == null
					&& inviteMessage.getFromContactHxid().equals(
							contactInvitBean.fromuidhx)) {
				getInviteMessgeDao().deleteMessage(
						inviteMessage.getFromContactHxid());
				isDeleteMessage = true;
			}
		}
		InviteMessage msg = new InviteMessage();
		String nickname = contactInvitBean.fromnickname;
		if (TextUtils.isEmpty(nickname)) {
			nickname = contactInvitBean.fromuid;
		}
		msg.setIsInviteFromMe(contactInvitBean.isInviteFromMe);
		msg.setFromContactFacePath(contactInvitBean.fromface);
		msg.setFromContactHxid(contactInvitBean.fromuidhx);
		msg.setFromContactNick(nickname);
		msg.setFromContactId(contactInvitBean.fromuid);
		msg.setLogid(contactInvitBean.logid);
		msg.setTime(System.currentTimeMillis());
		msg.setReason(contactInvitBean.msg);
		msg.setStatus(MesageStatus.BEINVITEED);
		notifyNewIviteMessage(msg, isDeleteMessage);
	}

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 * @param isDeleteMessage
	 */
	private void notifyNewIviteMessage(InviteMessage msg,
			boolean isDeleteMessage) {
		saveInviteMsg(msg, isDeleteMessage);
		// 提示有新消息
		// SDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);

		if (mHandler != null) {
			mHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_CHATLIST);
		}
	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 * @param isDeleteMessage
	 */
	private void saveInviteMsg(InviteMessage msg, boolean isDeleteMessage) {
		// 保存msg
		getInviteMessgeDao().saveMessage(msg);
		// 未读数加1
		User user = ((MySDKHelper) SDKHelper.getInstance()).getContactList()
				.get(Constant.NEW_FRIENDS_USERNAME);
		// if (user.getUnreadMsgCount() == 0)
		if (!isDeleteMessage) {
			SDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
		}

	}

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewAppointmentInvitationMsg(AppointmentInvitMessage msg) {
		saveAppointmentInvitationMsg(msg);

		// // 刷新好友页面ui
		if (mHandler != null) {
			mHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_CHATLIST);
		}

	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveAppointmentInvitationMsg(AppointmentInvitMessage msg) {
		// 保存msg
		getAppointmentInvitationDao().saveMessage(msg);
		if ("1".equals(msg.getIsInviteFromMe())) {
			ContactManager.getInstance().contactAdded(msg);
		} else {
			SDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);
			// 刷新bottom bar消息未读数
			// updateUnreadAddressLable();
		}
		// 未读数加1
		// 更新约会联系人完毕
		SDKHelper.getInstance().notifyAppointmentSyncListener(true);
		User user = ((MySDKHelper) SDKHelper.getInstance()).getContactList()
				.get(Constant.APPOINTMENT_INVITATION);
		if (user.getAppointmentMsgCount() == 0)
			user.setAppointmentMsgCount(user.getUnreadMsgCount() + 1);

	}

	/**
	 * 状态栏提示信息
	 * 
	 * @param mContactInvitBean
	 */
	private void showNotificationMsg(ContactInvitBean mContactInvitBean,
			String msg) {
		if (mContactInvitBean == null) {
			return;
		}
		EMMessage messages = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
		String fnickname = mContactInvitBean.fromnickname;
		if (null == fnickname || "".equals(fnickname)) {
			fnickname = mContactInvitBean.fromuid;
		}
		messages.setFrom(fnickname);
		messages.setTo(mContactInvitBean.tonickname);
		messages.setMsgId(UUID.randomUUID().toString());
		messages.addBody(new TextMessageBody(msg));
		SDKHelper.getInstance().getNotifier().onNewMsg(messages);
	}

	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;

	}

	private void disposeAsyncFetchContacts(List<ContactInfo> infos) {
		LogUtil.e(this, "CMDThreadHelper从服务器取联系人数据数量： " + infos.size());
		LogUtil.e("CMDThreadHelper",
				"CMDThreadHelper是否在主线程    =  "
						+ (Thread.currentThread() != Looper.getMainLooper()
								.getThread()));
		LogUtil.e("CMDThreadHelper", "   从服务器取联系人  threadName = "
				+ Thread.currentThread().getName());

		Context context = SDKHelper.getInstance().getAppContext();
		Map<String, User> userlist = new HashMap<String, User>();
		if (infos == null || infos.isEmpty()) {
			ArrayList<String> arrayList = new ArrayList<String>();
			Hashtable<String, EMConversation> allConversations = EMChatManager
					.getInstance().getAllConversations();
			for (String key : allConversations.keySet()) {
				arrayList.add(key);
			}
			for (String al : arrayList) {
				EMChatManager.getInstance().clearConversation(al);
			}
			InviteMessgeDao inviteMessgeDao2 = new InviteMessgeDao(
					UIUtils.getContext());
			inviteMessgeDao2.deleteAllMessage();
		} else {
			for (int i = 0; i < infos.size(); i++) {
				ContactInfo info = infos.get(i);
				User user = new User();
				user.setNick(info.nickname);
				user.setUsername(info.useridhx);
				user.setAvatar(info.face);
				user.setUserId(info.uid);

				user.setAddress(info.pland);
				user.setAge(info.birth);
				user.setHobby(info.hobby);
				user.setJob(info.job);
				user.setSex(info.sex);
				user.setShieldState(info.shieldState);
				user.setContactType(ContactType.FRIEND_TYPE);
				setUserHearder(info.nickname, user);
				userlist.put(info.useridhx, user);
				// UserInfoManager.getInstance().upAvatarNative(user);
				// CMDThreadHelper.getInstance().addTask(
				// CMDThreadHelper.FLAG_UP_AVATAR_NATIVE, user);
			}
		}
		// 添加user"申请与通知"

		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		String strChat = context.getString(R.string.Application_and_notify);
		newFriends.setNick(strChat);
		newFriends.setHeader("");
		userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
		// 添加"群聊"
		// User groupUser = new User();
		// String strGroup = context
		// .getString(R.string.group_chat);
		// groupUser.setUsername(Constant.GROUP_USERNAME);
		// groupUser.setNick(strGroup);
		// groupUser.setHeader("");
		// userlist.put(Constant.GROUP_USERNAME, groupUser);

		// 添加"约会邀请"
		User engagementUser = new User();
		String strInvitation = context
				.getString(R.string.appointment_invitation);
		engagementUser.setUsername(Constant.APPOINTMENT_INVITATION);
		engagementUser.setNick(strInvitation);
		engagementUser.setHeader("");
		userlist.put(Constant.APPOINTMENT_INVITATION, engagementUser);
//		// 添加"官方"
//		User officcialUser = new User();
//		String official_user = context.getResources().getString(
//				R.string.official_user);
//		officcialUser.setUsername(Constant.OFFICIAL_USER);
//		officcialUser.setNick(official_user);
//		officcialUser.setHeader("");
//		userlist.put(Constant.OFFICIAL_USER, officcialUser);

		((MySDKHelper) SDKHelper.getInstance()).setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(context);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);

		SDKHelper.getInstance().notifyContactsSyncListener(true);
		// 更新约会联系人完毕
		SDKHelper.getInstance().notifyAppointmentSyncListener(true);

		if (mHandler != null) {
			mHandler.sendEmptyMessage(MainActivity.MSG_UPDATE_CONTACTLIST);
		}
		if (SDKHelper.getInstance().isContactsSyncedWithServer()) {
			SDKHelper.getInstance().notifyForRecevingEvents();
		}
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	private static void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUserId();
		}
		if (Constant.NEW_FRIENDS_USERNAME.equals(headerName)) {
			user.setHeader("");
		} else if (Constant.APPOINTMENT_INVITATION.equals(headerName)) {
			user.setHeader("");
		} else if (Constant.GROUP_USERNAME.equals(headerName)) {
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
	}

	private class CMDTask {
		private int taskFlag;
		private Object obj;

		public int getTaskFlag() {
			return taskFlag;
		}

		public void setTaskFlag(int taskFlag) {
			this.taskFlag = taskFlag;
		}

		public Object getObj() {
			return obj;
		}

		public void setObj(Object obj) {
			this.obj = obj;
		}

	}

}
