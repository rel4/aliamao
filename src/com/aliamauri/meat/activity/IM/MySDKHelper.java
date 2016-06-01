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
package com.aliamauri.meat.activity.IM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.activity.IM.activity.ChatActivity;
import com.aliamauri.meat.activity.IM.activity.VideoCallActivity;
import com.aliamauri.meat.activity.IM.activity.VoiceCallActivity;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.domain.RobotUser;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.activity.IM.model.Notifier;
import com.aliamauri.meat.activity.IM.model.Notifier.HXNotificationInfoProvider;
import com.aliamauri.meat.activity.IM.model.SDKModel;
import com.aliamauri.meat.activity.IM.receiver.CallReceiver;
import com.aliamauri.meat.activity.IM.utils.CommonUtils;
import com.aliamauri.meat.thread.CMDThreadHelper;
import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * 
 * @author easemob
 * 
 */
public class MySDKHelper extends SDKHelper {

	private static final String TAG = "DemoHXSDKHelper";

	/**
	 * EMEventListener
	 */
	protected EMEventListener eventListener = null;

	/**
	 * contact list in cache
	 */
	private Map<String, User> contactList;

	/**
	 * robot list in cache
	 */
	private Map<String, RobotUser> robotList;
	private CallReceiver callReceiver;

	private UserProfileManager userProManager;

	/**
	 * 用来记录foreground Activity
	 */
	private List<Activity> activityList = new ArrayList<Activity>();

	public void pushActivity(Activity activity) {
		if (!activityList.contains(activity)) {
			activityList.add(0, activity);
		}
	}

	public void popActivity(Activity activity) {
		if (activityList.contains(activity)) {
			activityList.remove(activity);
		}
	}

	@Override
	public synchronized boolean onInit(Context context) {
		if (super.onInit(context)) {
			getUserProfileManager().onInit(context);

			// if your app is supposed to user Google Push, please set project
			// number
			String projectNumber = "562451699741";
			EMChatManager.getInstance().setGCMProjectNumber(projectNumber);
			return true;
		}

		return false;
	}

	@Override
	protected void initHXOptions() {
		super.initHXOptions();

		// you can also get EMChatOptions to set related SDK options
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		options.allowChatroomOwnerLeave(getModel()
				.isChatroomOwnerLeaveAllowed());
	}

	@Override
	protected void initListener() {
		super.initListener();
		IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance()
				.getIncomingCallBroadcastAction());
		if (callReceiver == null) {
			callReceiver = new CallReceiver();
		}

		// 注册通话广播接收者
		appContext.registerReceiver(callReceiver, callFilter);
		// 注册消息事件监听
		initEventListener();
	}

	/**
	 * 全局事件监听 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理 activityList.size()
	 * <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
	 */
	protected void initEventListener() {
		eventListener = new EMEventListener() {
			private BroadcastReceiver broadCastReceiver = null;

			@Override
			public void onEvent(EMNotifierEvent event) {
				EMMessage message = null;
				if (event.getData() instanceof EMMessage) {
					message = (EMMessage) event.getData();
					EMLog.d(TAG, "receive the event : " + event.getEvent()
							+ ",id : " + message.getMsgId());
				}

				switch (event.getEvent()) {
				case EventNewMessage:
					// 应用在后台，不需要刷新UI,通知栏提示新消息
					if (activityList.size() <= 0) {
						// UserDao userDao = new UserDao(UIUtils.getContext());
						// User contactInfo = userDao.getContactInfo(message
						// .getFrom());
						// if (contactInfo != null) {
						// String nick = contactInfo.getNick();
						// if (TextUtils.isEmpty(nick)) {
						//
						// if (!TextUtils.isEmpty(contactInfo.getUserId())) {
						// message.setFrom(contactInfo.getUserId());
						// }
						// } else {
						// message.setFrom(nick);
						// }
						// }
						SDKHelper.getInstance().getNotifier().onNewMsg(message);
					}
					break;
				case EventOfflineMessage:
					if (activityList.size() <= 0) {
						EMLog.d(TAG, "received offline messages");
						List<EMMessage> messages = (List<EMMessage>) event
								.getData();
						SDKHelper.getInstance().getNotifier()
								.onNewMesg(messages);
					}
					break;
				// below is just giving a example to show a cmd toast, the app
				// should not follow this
				// so be careful of this
				case EventNewCMDMessage: {
					CmdMessageBody cmdMsgBody = (CmdMessageBody) message
							.getBody();
					String action = cmdMsgBody.action;// 获取自定义action
					CMDThreadHelper.getInstance().switchAction(action);
					// CmdAtionParseHelper.getInstance().switchAction(action,
					// mCmdContactListener);
				}
				case EventDeliveryAck:
					message.setDelivered(true);
					break;
				case EventReadAck:
					message.setAcked(true);
					break;
				// add other events in case you are interested in
				default:
					break;
				}

			}
		};

		EMChatManager.getInstance().registerEventListener(eventListener);

		EMChatManager.getInstance().addChatRoomChangeListener(
				new EMChatRoomChangeListener() {
					private final static String ROOM_CHANGE_BROADCAST = "easemob.demo.chatroom.changeevent.toast";
					private final IntentFilter filter = new IntentFilter(
							ROOM_CHANGE_BROADCAST);
					private boolean registered = false;

					private void showToast(String value) {
						if (!registered) {
							// 注册广播接收者
							appContext.registerReceiver(
									new BroadcastReceiver() {

										@Override
										public void onReceive(Context context,
												Intent intent) {
											Toast.makeText(
													appContext,
													intent.getStringExtra("value"),
													Toast.LENGTH_SHORT).show();
										}

									}, filter);

							registered = true;
						}

						Intent broadcastIntent = new Intent(
								ROOM_CHANGE_BROADCAST);
						broadcastIntent.putExtra("value", value);
						appContext.sendBroadcast(broadcastIntent, null);
					}

					@Override
					public void onChatRoomDestroyed(String roomId,
							String roomName) {
						showToast(" room : " + roomId + " with room name : "
								+ roomName + " was destroyed");
						Log.i("info", "onChatRoomDestroyed=" + roomName);
					}

					@Override
					public void onMemberJoined(String roomId, String participant) {
						showToast("member : " + participant
								+ " join the room : " + roomId);
						Log.i("info", "onmemberjoined=" + participant);

					}

					@Override
					public void onMemberExited(String roomId, String roomName,
							String participant) {
						showToast("member : " + participant
								+ " leave the room : " + roomId
								+ " room name : " + roomName);
						Log.i("info", "onMemberExited=" + participant);

					}

					@Override
					public void onMemberKicked(String roomId, String roomName,
							String participant) {
						showToast("member : " + participant
								+ " was kicked from the room : " + roomId
								+ " room name : " + roomName);
						Log.i("info", "onMemberKicked=" + participant);

					}

				});
	}

	//
	// /**
	// * 状态栏提示信息
	// *
	// * @param mContactInvitBean
	// */
	// private void showNotificationMsg(ContactInvitBean mContactInvitBean,
	// String msg) {
	// if (mContactInvitBean == null) {
	// return;
	// }
	// EMMessage messages = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
	// String fnickname = mContactInvitBean.fromnickname;
	// if (null == fnickname || "".equals(fnickname)) {
	// fnickname = mContactInvitBean.fromuid;
	// }
	// messages.setFrom(fnickname);
	// messages.setTo(mContactInvitBean.tonickname);
	// messages.setMsgId(UUID.randomUUID().toString());
	// messages.addBody(new TextMessageBody(msg));
	// SDKHelper.getInstance().getNotifier().onNewMsg(messages);
	// }

	/**
	 * 自定义通知栏提示内容
	 * 
	 * @return
	 */
	@Override
	protected HXNotificationInfoProvider getNotificationListener() {
		// 可以覆盖默认的设置
		return new HXNotificationInfoProvider() {

			@Override
			public String getTitle(EMMessage message) {
				// 修改标题,这里使用默认
				return null;
			}

			@Override
			public int getSmallIcon(EMMessage message) {
				// 设置小图标，这里为默认
				return 0;
			}

			@Override
			public String getDisplayedText(EMMessage message) {
				// 设置状态栏的消息提示，可以根据message的类型做相应提示
				String ticker = CommonUtils.getMessageDigest(message,
						appContext);
				if (message.getType() == Type.TXT) {
					ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
				}
				Map<String, RobotUser> robotMap = ((MySDKHelper) SDKHelper
						.getInstance()).getRobotList();
				if (robotMap != null && robotMap.containsKey(message.getFrom())) {
					String nick = robotMap.get(message.getFrom()).getNick();
					if (!TextUtils.isEmpty(nick)) {
						return nick + ": " + ticker;
					} else {
						return message.getFrom() + ": " + ticker;
					}
				} else {
					String from = message.getFrom();
					Map<String, User> hxUsersMap = ContactManager.getInstance()
							.getAllHXUsersMap();
					if (hxUsersMap.containsKey(from)) {
						User user = hxUsersMap.get(from);
						String nick = user.getNick();
						if (!TextUtils.isEmpty(nick)) {
							from = nick;
						}
					}
					return from + ": " + ticker;
				}
			}

			@Override
			public String getLatestText(EMMessage message, int fromUsersNum,
					int messageNum) {
				return null;
				// return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
			}

			@Override
			public Intent getLaunchIntent(EMMessage message) {
				// 设置点击通知栏跳转事件
				Intent intent = new Intent(appContext, ChatActivity.class);
				// 有电话时优先跳转到通话页面
				if (isVideoCalling) {
					intent = new Intent(appContext, VideoCallActivity.class);
				} else if (isVoiceCalling) {
					intent = new Intent(appContext, VoiceCallActivity.class);
				} else {
					ChatType chatType = message.getChatType();
					if (chatType == ChatType.Chat) { // 单聊信息
						intent.putExtra("userId", message.getFrom());
						intent.putExtra("chatType",
								ChatActivity.CHATTYPE_SINGLE);
					} else { // 群聊信息
						// message.getTo()为群聊id
						intent.putExtra("groupId", message.getTo());
						if (chatType == ChatType.GroupChat) {
							intent.putExtra("chatType",
									ChatActivity.CHATTYPE_GROUP);
						} else {
							intent.putExtra("chatType",
									ChatActivity.CHATTYPE_CHATROOM);
						}

					}
				}
				return intent;
			}
		};
	}

	@Override
	protected void onConnectionConflict() {
		Intent intent = new Intent(appContext, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("conflict", true);
		appContext.startActivity(intent);
	}

	@Override
	protected void onCurrentAccountRemoved() {
		Intent intent = new Intent(appContext, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Constant.ACCOUNT_REMOVED, true);
		appContext.startActivity(intent);
	}

	@Override
	protected SDKModel createModel() {
		return new MySDKModel(appContext);
	}

	@Override
	public Notifier createNotifier() {
		return new Notifier() {
			public synchronized void onNewMsg(final EMMessage message) {
				if (EMChatManager.getInstance().isSlientMessage(message)) {
					return;
				}

				String chatUsename = null;
				List<String> notNotifyIds = null;
				// 获取设置的不提示新消息的用户或者群组ids
				if (message.getChatType() == ChatType.Chat) {
					chatUsename = message.getFrom();
					notNotifyIds = ((MySDKModel) hxModel).getDisabledGroups();
				} else {
					chatUsename = message.getTo();
					notNotifyIds = ((MySDKModel) hxModel).getDisabledIds();
				}

				if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
					// 判断app是否在后台
					if (!EasyUtils.isAppRunningForeground(appContext)) {
						EMLog.d(TAG, "app is running in backgroud");
						sendNotification(message, false);
					} else {
						sendNotification(message, true);

					}

					viberateAndPlayTone(message);
				}
			}
		};
	}

	/**
	 * get demo HX SDK Model
	 */
	public MySDKModel getModel() {
		return (MySDKModel) hxModel;
	}

	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		if (getHXId() != null && contactList == null) {
			contactList = ((MySDKModel) getModel()).getContactList();
		}

		return contactList;
	}

	public Map<String, RobotUser> getRobotList() {
		if (getHXId() != null && robotList == null) {
			robotList = ((MySDKModel) getModel()).getRobotList();
		}
		return robotList;
	}

	public boolean isRobotMenuMessage(EMMessage message) {

		try {
			JSONObject jsonObj = message
					.getJSONObjectAttribute(Constant.MESSAGE_ATTR_ROBOT_MSGTYPE);
			if (jsonObj.has("choice")) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public String getRobotMenuMessageDigest(EMMessage message) {
		String title = "";
		try {
			JSONObject jsonObj = message
					.getJSONObjectAttribute(Constant.MESSAGE_ATTR_ROBOT_MSGTYPE);
			if (jsonObj.has("choice")) {
				JSONObject jsonChoice = jsonObj.getJSONObject("choice");
				title = jsonChoice.getString("title");
			}
		} catch (Exception e) {
		}
		return title;
	}

	public void setRobotList(Map<String, RobotUser> robotList) {
		this.robotList = robotList;
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		this.contactList = contactList;
	}

	/**
	 * 保存单个user
	 */
	public void saveContact(User user) {
		contactList.put(user.getUsername(), user);
		((MySDKModel) getModel()).saveContact(user);
	}

	@Override
	public void logout(final boolean unbindDeviceToken,
			final EMCallBack callback) {
		endCall();
		super.logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				setContactList(null);
				setRobotList(null);
				getUserProfileManager().reset();
				getModel().closeDB();
				if (callback != null) {
					callback.onSuccess();
				}
			}

			@Override
			public void onError(int code, String message) {
				// TODO Auto-generated method stub
				if (callback != null) {
					callback.onError(code, message);
				}
			}

			@Override
			public void onProgress(int progress, String status) {
				// TODO Auto-generated method stub
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

		});
	}

	void endCall() {
		try {
			EMChatManager.getInstance().endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * update User cach And db
	 * 
	 * @param contactList
	 */
	public void updateContactList(List<User> contactInfoList) {
		for (User u : contactInfoList) {
			contactList.put(u.getUsername(), u);
		}
		ArrayList<User> mList = new ArrayList<User>();
		mList.addAll(contactList.values());
		((MySDKModel) getModel()).saveContactList(mList);
	}

	public UserProfileManager getUserProfileManager() {
		if (userProManager == null) {
			userProManager = new UserProfileManager();
		}
		return userProManager;
	}

}
