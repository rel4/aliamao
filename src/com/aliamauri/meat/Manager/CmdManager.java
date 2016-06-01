package com.aliamauri.meat.Manager;

import java.util.Map;

import android.text.TextUtils;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.db.InviteMessgeDao;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.activity.IM.domain.User.ContactType;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;

/**
 * 透传信息管理类
 * 
 * @author admin
 * 
 */
public class CmdManager {

	private final static CmdManager Manager = new CmdManager();

	public static CmdManager getInstance() {
		return Manager;

	}

	public CmdManagerCallBack mCallBack;
	private HttpHelp httpHelp;

	// public ContactManagerDataCallBack mDataCallBack;
	/**
	 * 消息管理回调
	 * 
	 * @author jjm
	 */
	public interface CmdManagerCallBack {
		void onState(boolean isSucceed);
	}

	private HttpHelp getHttpHelp() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		return httpHelp;
	}

	/**
	 * 约会邀请---（须透传）
	 * 
	 * @param useridhx
	 * @param s
	 */
	public void sendAppointmentInvit(String userid, String s,
			CmdManagerCallBack cmdCallBack) {
		this.mCallBack = cmdCallBack;
		if (!isInvalidUser(userid)) {
			if (mCallBack != null) {
				mCallBack.onState(false);
			}
			return;
		}
		getHttpHelp().sendGet(NetworkConfig.getappointmentInvitUrl(userid, s),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {
					@Override
					public void onSucceed(BaseBaen bean) {
						if (mCallBack != null && bean != null
								&& "1".equals(bean.status)) {
							mCallBack.onState(true);
							SDKHelper.getInstance().notifyContactsSyncListener(true);
						} else {
							mCallBack.onState(false);
						}
					}
				});
	}

	/**
	 * 添加好友---（须透传）
	 * 
	 * @param useridhx
	 * @param s
	 */
	public void addContact(String userid, String s,
			CmdManagerCallBack managerCallBack) {
		this.mCallBack = managerCallBack;
		if (!isInvalidUser(userid)) {
			if (mCallBack != null) {
				mCallBack.onState(false);
			}
			return;
		}
		getHttpHelp().sendGet(NetworkConfig.getAddContactUrl(userid, s),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {
					@Override
					public void onSucceed(BaseBaen bean) {
						if (mCallBack != null && bean != null
								&& "1".equals(bean.status)) {
							mCallBack.onState(true);
							SDKHelper.getInstance().notifyContactsSyncListener(true);
						} else {
							mCallBack.onState(false);
						}
					}
				});
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
			ContactType type = user.getContactType();

			if (!TextUtils.isEmpty(appID) && appID.equals(userid)) {
				if (type == ContactType.APPOINTMENT_TYPE) {

				} else if (type == ContactType.FRIEND_TYPE) {
					UIUtils.showToast(
							UIUtils.getContext(),
							UIUtils.getContext().getString(
									R.string.This_user_is_already_your_friend));
					return flag;
				}
			}
		}
		return !flag;
	}

	/**
	 * 删除好友---（须透传）
	 * 
	 * @param uid
	 */
	public void deleteContact(final String uid,
			CmdManagerCallBack managerCallBack) {
		this.mCallBack = managerCallBack;
		getHttpHelp().sendGet(NetworkConfig.getDeleteContactUrl(uid),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {
					@Override
					public void onSucceed(BaseBaen bean) {
						if (mCallBack != null && bean != null
								&& "1".equals(bean.status)) {
							// contactdeleted(uid);
							Map<String, User> allContactUidMap = ContactManager.getInstance().getAllContactUidMap();
							if (allContactUidMap.containsKey(uid)) {
								User user = allContactUidMap.get(uid);
								ContactManager.getInstance().deleteContact(uid);
								InviteMessgeDao dao = new InviteMessgeDao(UIUtils.getContext());
								dao.deleteMessage(user.getUsername());
							}
							mCallBack.onState(true);
							SDKHelper.getInstance().notifyContactsSyncListener(true);
						} else {
							mCallBack.onState(false);
						}
					}
				});
	}

	/**
	 * 接收好友的请求---（须透传）
	 * 
	 * @param fUid
	 *            好友ID
	 * @param action
	 *            是否同意
	 * @param logid
	 *            标示
	 */
	public void acceptInvitation(String logid, String fUid, String action,
			final CmdManagerCallBack managerCallBack) {
		getHttpHelp().sendGet(
				NetworkConfig.getAcceptInvitation(fUid, action, logid),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (managerCallBack != null) {
							if (bean != null && "1".equals(bean.status)) {
								managerCallBack.onState(true);
							} else {
								managerCallBack.onState(false);
							}
						}

					}
				});

	}

	/**
	 * 请求服务器加入黑名单
	 * 
	 * @param uid
	 */
	public void requsetServiceToBlackList(final String uid,
			final CmdManagerCallBack callBack) {
		getHttpHelp().sendGet(NetworkConfig.getRequsetServiceToBlackList(uid),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean != null || "1".equals(bean.status)) {
							UIUtils.showToast(UIUtils.getContext(),
									"添加黑名单成功");
							if (callBack != null) {
//								BlackListManager.getInstance().addContacttTOBlackList(uid);
								
								callBack.onState(true);
							}
							
						} else {
							UIUtils.showToast(UIUtils.getContext(),
									"添加黑名单失败");
							if (callBack != null) {
								
								callBack.onState(false);
							
							}
						}

					}
				});
	}

	/**
	 * 请求服务器移除黑名单---（不须透传）
	 * 
	 * @param uid
	 * @param callBack
	 */
	public void requstServicerRemoveBlackList(final String uid,
			final CmdManagerCallBack callBack) {
		getHttpHelp().sendGet(
				NetworkConfig.getRequstServicerRemoveBlackList(uid),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {
					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || !"1".equals(bean.status)) {
							if (callBack != null) {
								callBack.onState(false);
							}
							UIUtils.showToast(UIUtils.getContext(),
									"解除黑名单失败");
						} else {
							Map<String, User> haveUidIdBlackListMap = BlackListManager
									.getInstance().getHaveUidIdBlackListMap();
							if (haveUidIdBlackListMap.containsKey(uid)) {
								BlackListManager.getInstance()
										.deleteConttactBlackLsit(
												haveUidIdBlackListMap.get(uid)
														.getUsername());
								if (callBack != null) {
									callBack.onState(true);
								}
								SDKHelper.getInstance()
										.notifyBlackListSyncListener(true);
								SDKHelper.getInstance()
										.notifyContactsSyncListener(true);
								UIUtils.showToast(UIUtils.getContext(),
										"解除黑名单成功");
							}

						}

					}
				});
	}
}
