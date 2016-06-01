package com.aliamauri.meat.utils;

import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.global.MyApplication;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

public class MessageUtils {
	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 * @param isResend
	 *            boolean resend
	 */
	public static EMMessage createText(String toChatUsername, String content) {

		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// 如果是群聊，设置chattype,默认是单聊
		// if (chatType == CHATTYPE_GROUP) {
		// message.setChatType(ChatType.GroupChat);
		// } else if (chatType == CHATTYPE_CHATROOM) {
		// message.setChatType(ChatType.ChatRoom);
		// }
		// if (isRobot) {
		// message.setAttribute("em_robot_message", true);
		// }
		TextMessageBody txtBody = new TextMessageBody(content);
		// 设置消息body
		message.addBody(txtBody);
		// 设置要发给谁,用户username或者群聊groupid
		// message.status =EMMessage.Status.SUCCESS;
		message.setFrom(MySDKHelper.getInstance().getHXId());
		message.setTo(toChatUsername);
		// 把messgage加到conversation中
		// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
		return message;

	}

	public static void sendMessage(String username, String content) {
		// 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
		EMConversation conversation = EMChatManager.getInstance()
				.getConversation(username);
		// 创建一条文本消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// 如果是群聊，设置chattype,默认是单聊
		// message.setChatType(ChatType.GroupChat);
		// 设置消息body
		TextMessageBody txtBody = new TextMessageBody(content);
		message.addBody(txtBody);
		// 设置接收人
		message.setReceipt(username);
		// 把消息加入到此会话对象中
		conversation.addMessage(message);
		// 发送消息
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub

			}
		});
	}

	public static void sendMyMessage(String form, String to, String content) {
		// 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
//		EMConversation conversation = EMChatManager.getInstance()
//				.getConversation(form);
//		// 创建一条文本消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// 如果是群聊，设置chattype,默认是单聊
		// message.setChatType(ChatType.GroupChat);
		// 设置消息body
		TextMessageBody txtBody = new TextMessageBody(content);

		message.addBody(txtBody);
		// 设置接收人
		message.setReceipt(form);
		// message.setTo(to);
		message.setFrom(to);
//		// 把消息加入到此会话对象中
//		conversation.addMessage(message);
		// 发送消息
		EMChatManager.getInstance().saveMessage(message,true);
		((MainActivity) MyApplication.getInstance().getMainActivity()).mHandler
				.sendEmptyMessage(MainActivity.MSG_UPDATE_CHATLIST);
		// EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
		//
		// @Override
		// public void onSuccess() {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onProgress(int arg0, String arg1) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onError(int arg0, String arg1) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
	}

}
