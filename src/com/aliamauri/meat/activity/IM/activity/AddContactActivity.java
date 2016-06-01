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
package com.aliamauri.meat.activity.IM.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.BlackListManager;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.bean.ContactInfoBean;
import com.aliamauri.meat.bean.ContactInfoBean.ContactInfo;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

public class AddContactActivity extends BaseActivity {
	private EditText editText;
	private Button searchBtn;
	private CircleImageView avatar;
	private InputMethodManager inputMethodManager;
	private String toAddUsername;
	private ProgressDialog progressDialog;
	private HttpHelp httpHelp;
	private ContactInfoBean mBean;
	private PullToRefreshListView ptr_adc_list;

	private ListAdapter listAdapter;
	private TextView add_list_friends;

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		ptr_adc_list = (PullToRefreshListView) findViewById(R.id.ptr_adc_list);
		editText = (EditText) findViewById(R.id.edit_note);
		String strAdd = getResources().getString(R.string.add_friend);

		searchBtn = (Button) findViewById(R.id.search);
		avatar = (CircleImageView) findViewById(R.id.avatar);
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		add_list_friends = (TextView) findViewById(R.id.add_list_friends);
		add_list_friends.setText("添加新好友");
		initListView();

	}

	private void initListView() {
		if (listAdapter == null) {
			listAdapter = new ListAdapter();
			ptr_adc_list.setAdapter(listAdapter);
		} else {
			listAdapter.notifyDataSetChanged();
		}
	}

	class ListAdapter extends BaseAdapter {

		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			if (mBean == null || mBean.cont == null) {
				return 0;
			}
			return mBean.cont.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup arg2) {
			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = View.inflate(UIUtils.getContext(),
						R.layout.activity_add_contact_item, null);
				viewHolder.civ_aaci_avatar = (CircleImageView) converView
						.findViewById(R.id.civ_aaci_avatar);
				viewHolder.tv_aaci_name = (TextView) converView
						.findViewById(R.id.tv_aaci_name);
				viewHolder.tv_aaci_indicator = (TextView) converView
						.findViewById(R.id.tv_aaci_indicator);
				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}
			viewHolder.tv_aaci_name.setText(mBean.cont.get(position).nickname);
			httpHelp.showImage(viewHolder.civ_aaci_avatar,
					mBean.cont.get(position).face + "##");
			viewHolder.tv_aaci_indicator.setOnClickListener(new MyOnclick(
					position));
			return converView;
		}

	}

	class ViewHolder {
		public CircleImageView civ_aaci_avatar;
		public TextView tv_aaci_name;
		public TextView tv_aaci_indicator;
	}

	/**
	 * 查找contact
	 * 
	 * @param v
	 */
	public void searchContact(View v) {
		final String name = editText.getText().toString();
		String saveText = searchBtn.getText().toString();

		if (getString(R.string.button_search).equals(saveText)) {
			toAddUsername = name;
			if (TextUtils.isEmpty(name)) {
				alertDialog("");
				return;
			}

			// TODO 从服务器获取此contact,如果不存在提示不存在此用户

			if (httpHelp == null) {
				httpHelp = new HttpHelp();
			}
			httpHelp.sendGet(NetworkConfig.getFindFriend(name),
					ContactInfoBean.class,
					new MyRequestCallBack<ContactInfoBean>() {

						@Override
						public void onSucceed(ContactInfoBean bean) {
							if (bean == null || bean.cont == null
									|| bean.cont.size() == 0) {
								alertDialog("用户不存在");
								return;
							}
							if ("1".equals(bean.status)) {
								mBean = bean;
								initListView();
							}
							// 服务器存在此用户，显示此用户和添加按钮
						}
					});

		}
	}

	private void alertDialog(String msg) {
		if ("".equals(msg)) {
			msg = getResources().getString(R.string.Please_enter_a_username);
		}
		startActivity(new Intent(this, AlertDialog.class).putExtra("msg", msg));
	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact(int position) {
		if (mBean == null || mBean.cont == null) {
			return;
		}
		final ContactInfo user = mBean.cont.get(position);
		if (MyApplication.getInstance().getUserName().equals(user.useridhx)) {
			String str = getString(R.string.not_add_myself);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
					str));
			return;
		}

		if (ContactManager.getInstance().getFriendContactHXidMap()
				.containsKey(user.useridhx)) {
			// 提示已在好友列表中，无需添加

			String strin = getString(R.string.This_user_is_already_your_friend);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
					strin));
			return;
		}
		if (BlackListManager.getInstance().getBlackListHXid()
				.contains(user.useridhx)) {
			LogUtil.e(this, BlackListManager.getInstance().getBlackListHXid()
					.toString());
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
					"此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可"));
			return;
		}
		progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {

				try {
					// demo写死了个reason，实际应该让用户手动填入
					String s = getResources().getString(R.string.Add_a_friend);
					CmdManager.getInstance().addContact(user.uid, s,
							new CmdManagerCallBack() {

								@Override
								public void onState(final boolean isSucceed) {
									runOnUiThread(new Runnable() {
										public void run() {
											progressDialog.dismiss();
											String s1 = getResources()
													.getString(
															R.string.send_successful);
											String s2 = getResources()
													.getString(
															R.string.Request_add_buddy_failure);
											Toast.makeText(
													getApplicationContext(),
													isSucceed ? s1 : s2, 1)
													.show();
										}
									});
								}
							});

				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(
									R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(),
									s2 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	class MyOnclick implements OnClickListener {
		private int position;

		public MyOnclick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			addContact(position);
		}

	}

	public void back(View v) {
		finish();
	}
}
