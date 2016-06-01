package com.aliamauri.meat.parse;

import java.util.UUID;

import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.bean.cmd.ContactInvitBean;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

public class CmdAtionParseHelper {

	private CmdAtionParseHelper() {
	}

	private static CmdAtionParseHelper instance = new CmdAtionParseHelper();

	public static CmdAtionParseHelper getInstance() {
		return instance;
	}

	/**
	 * 处理透传信息
	 * 
	 * @param action
	 */
//	public void switchAction(String json) {
//		ContactInvitBean mContactInvitBean = null;
//		try {
//			mContactInvitBean = (ContactInvitBean) JsonParse.parserJson(json,
//					ContactInvitBean.class);
//		} catch (JSONException e) {
//			e.printStackTrace();
//			if (LogUtil.getDeBugState()) {
//				throw new RuntimeException();
//			}
//		}
//		if (mContactInvitBean == null) {
//			return;
//		}
//		switch (mContactInvitBean.action) {
//		case CmdActionGlobal.ACTION_CONTACT_INVIT:// 联系人好友请求
//			if (mCmdContactListener != null) {
//				Map<String, User> haveUidIdBlackList = BlackListManager
//						.getInstance().getHaveUidIdBlackListMap();
//				if (!haveUidIdBlackList.containsKey(mContactInvitBean.fromuid)) {
//					showNotificationMsg(mContactInvitBean, "请求添加好友");
//					mCmdContactListener.onContactInvited(mContactInvitBean);
//				}
//			}
//			break;
//		case CmdActionGlobal.ACTION_CONTACT_ADD:// 添加联系人
//			if (mCmdContactListener != null) {
//				mContactInvitBean.contactType = ContactType.FRIEND_TYPE;
//				showNotificationMsg(mContactInvitBean, "已添加您为好友");
//				mCmdContactListener.onContactAdded(mContactInvitBean);
//			}
//			break;
//		case CmdActionGlobal.ACTION_APPOINTMENT_INVIT:// 添加约会联系人
//			if (mCmdContactListener != null) {
//				Map<String, User> haveUidIdBlackList = BlackListManager
//						.getInstance().getHaveUidIdBlackListMap();
//				if (!haveUidIdBlackList.containsKey(mContactInvitBean.fromuid)) {
//					mContactInvitBean.contactType = ContactType.APPOINTMENT_TYPE;
//					if ("0".equals(mContactInvitBean.isInviteFromMe)) {
//						showNotificationMsg(mContactInvitBean, "约会邀请");
//					}
//					mCmdContactListener
//							.onAppointmentInvitationMsg(mContactInvitBean);
//				}
//			}
//			break;
//		case CmdActionGlobal.ACTION_CONTACT_DELETED:// 联系人被删除
//			if (mCmdContactListener != null) {
//				mCmdContactListener.onContactDeleted(mContactInvitBean);
//			}
//			break;
//		case CmdActionGlobal.ACTION_ADD_BLACK_LIST:// 添加黑名單列表
//			if (mCmdContactListener != null) {
//				mCmdContactListener.onAddContactToBlackList(mContactInvitBean);
//			}
//			break;
//		}
//
//	}

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

}
