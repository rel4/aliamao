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
package com.aliamauri.meat.activity.IM.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.activity.IM.db.InviteMessgeDao;
import com.aliamauri.meat.activity.IM.domain.InviteMessage;
import com.aliamauri.meat.activity.IM.domain.Message.MesageStatus;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.view.CircleImageView;
import com.easemob.chat.EMGroupManager;

public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage> {
	private HttpHelp mHelp;
	private Context context;
	private InviteMessgeDao messgeDao;
	private String ACCEPT_INVITATION = "agree";// 同意

	public NewFriendsMsgAdapter(Context context, int textViewResourceId,
			List<InviteMessage> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		messgeDao = new InviteMessgeDao(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.row_invite_msg, null);
			holder.avator = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			holder.reason = (TextView) convertView.findViewById(R.id.message);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.status = (Button) convertView.findViewById(R.id.user_state);
			holder.groupContainer = (LinearLayout) convertView
					.findViewById(R.id.ll_group);
			holder.groupname = (TextView) convertView
					.findViewById(R.id.tv_groupName);
			// holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String str1 = context.getResources().getString(
				R.string.Has_agreed_to_your_friend_request);
		String str2 = context.getResources().getString(R.string.agree);

		String str3 = context.getResources().getString(
				R.string.Request_to_add_you_as_a_friend);
		String str4 = context.getResources().getString(
				R.string.Apply_to_the_group_of);
		String str5 = context.getResources().getString(R.string.Has_agreed_to);
		String str6 = context.getResources().getString(R.string.Has_refused_to);
		final InviteMessage msg = getItem(position);
		if (msg != null) {
			if (msg.getGroupId() != null) { // 显示群聊提示
				holder.groupContainer.setVisibility(View.VISIBLE);
				holder.groupname.setText(msg.getGroupName());
			} else {
				holder.groupContainer.setVisibility(View.GONE);
			}
			if (mHelp == null) {
				mHelp = new HttpHelp();
			}
			mHelp.showImage(holder.avator, msg.getFromContactFacePath() + "##");
			holder.reason.setText(msg.getFromContactId() + "");
			holder.name.setText(msg.getFromContactNick());
			// holder.time.setText(DateUtils.getTimestampString(new
			// Date(msg.getTime())));
			if (msg.getStatus() == MesageStatus.BEAGREED) {
				holder.status.setVisibility(View.INVISIBLE);
				holder.reason.setText(str1);
			} else if (msg.getStatus() == MesageStatus.BEINVITEED
					|| msg.getStatus() == MesageStatus.BEAPPLYED) {
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setEnabled(true);
				holder.status
						.setBackgroundResource(android.R.drawable.btn_default);
				holder.status.setText(str2);
				if (msg.getStatus() == MesageStatus.BEINVITEED) {
					if (msg.getReason() == null) {
						// 如果没写理由
						holder.reason.setText(str3);
					}
				} else { // 入群申请
					if (TextUtils.isEmpty(msg.getReason())) {
						holder.reason.setText(str4 + msg.getGroupName());
					}
				}
				// 设置点击事件
				holder.status.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 同意别人发的好友请求
						acceptInvitation(holder.status, msg, ACCEPT_INVITATION);
					}
				});
			} else if (msg.getStatus() == MesageStatus.AGREED) {
				holder.status.setText(str5);
				holder.status.setBackgroundDrawable(null);
				holder.status.setEnabled(false);
			} else if (msg.getStatus() == MesageStatus.REFUSED) {
				holder.status.setText(str6);
				holder.status.setBackgroundDrawable(null);
				holder.status.setEnabled(false);
			}

			// 设置用户头像
		}

		return convertView;
	}

	/**
	 * 同意好友请求或者群申请
	 * 
	 * @param button
	 * @param username
	 */
	private void acceptInvitation(final Button button, final InviteMessage msg,
			final String action) {
		final ProgressDialog pd = new ProgressDialog(context);
		String str1 = context.getResources().getString(R.string.Are_agree_with);
		pd.setMessage(str1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		// 调用sdk的同意方法
		try {
			if (msg.getGroupId() == null) { // 同意好友请求
				CmdManager.getInstance().acceptInvitation(msg.getLogid(),
						msg.getFromContactId(), action,
						new CmdManagerCallBack() {

							@Override
							public void onState(boolean isSucceed) {
								showPd(isSucceed, pd, button, msg);
							}
						});
			} else {
				// 同意加群申请
				EMGroupManager.getInstance().acceptApplication(
						msg.getFromContactNick(), msg.getGroupId());
				showPd(true, pd, button, msg);
			}
		} catch (final Exception e) {
			showPd(true, pd, button, msg);
		}

	}

	/**
	 * 显示提示信息
	 * 
	 * @param msg
	 * @param button
	 * @param pd
	 * @param isSucceed
	 * 
	 * @param isSucceed
	 * @param msg
	 * @param button
	 * @param pd
	 */
	private void showPd(boolean isSucceed, ProgressDialog pd, Button button,
			InviteMessage msg) {
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
		if (isSucceed) {

			if (msg != null) {
				msg.setStatus(MesageStatus.AGREED);
				ContentValues values = new ContentValues();
				values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus()
						.ordinal());
				// 更新db
				messgeDao.updateMessage(msg.getId(), values);
			}
			if (button != null) {

				button.setText(context.getResources().getString(
						R.string.Has_agreed_to));
				button.setBackgroundDrawable(null);
				button.setEnabled(false);
			}
			ContactManager.getInstance().contactAdded(msg);
		} else {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.Agree_with_failure), 1).show();
		}

	}

	private static class ViewHolder {
		CircleImageView avator;
		TextView name;
		TextView reason;
		Button status;
		LinearLayout groupContainer;
		TextView groupname;
		// TextView time;
	}

}
