package com.aliamauri.meat.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.IM.Constant;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.db.UserDao;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.bean.ChannelBean;
import com.aliamauri.meat.bean.ChannelBean.ChannelCont.Channel;
import com.aliamauri.meat.bean.LoginBean;
import com.aliamauri.meat.db.Dynamic_db.DynamicShowDao;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.top.bean.ChannelItem;
import com.aliamauri.meat.top.bean.ChannelManage;
import com.aliamauri.meat.top.db.SQLHelper;
import com.aliamauri.meat.utils.ChangeUtils;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.ShowAlertDialog;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.umeng.analytics.MobclickAgent;

/**
 * 登录
 * 
 * @author ych
 * 
 */
public class LoginActivity extends Activity implements OnClickListener {
	private EditText et_login_username;
	private EditText et_login_userpwd;
	private TextView tv_login_login;
	private HttpHelp httpHelp;
	private Intent loginIntent;
	private TextView tv_login_retrievepwd;
	private TextView tv_login_gotoregister;

	private View v_login;

	// private RelativeLayout rl_login_bottom;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		// PrefUtils.clearSP(UIUtils.getContext());
		loginIntent = getIntent();
		httpHelp = new HttpHelp();
		initView();
		setListener();
	}

	private void setListener() {
		tv_login_login.setOnClickListener(this);
		tv_login_retrievepwd.setOnClickListener(this);
		tv_login_gotoregister.setOnClickListener(this);
		v_login.setOnClickListener(this);
		// rl_login_bottom.setOnClickListener(this);
	}

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

	private void initView() {
		et_login_username = (EditText) findViewById(R.id.et_login_username);
		if (loginIntent.getStringExtra("splash") != null) {
			et_login_username.setText(loginIntent.getStringExtra("splash"));
		} else {
			et_login_username.setText(PrefUtils.getString(
					GlobalConstant.USER_ID, ""));
		}
		v_login = findViewById(R.id.v_login);
		tv_login_gotoregister = (TextView) findViewById(R.id.tv_login_gotoregister);
		et_login_username.setSelection(et_login_username.getText().toString()
				.length());
		et_login_userpwd = (EditText) findViewById(R.id.et_login_userpwd);

		tv_login_login = (TextView) findViewById(R.id.tv_login_login);

		tv_login_retrievepwd = (TextView) findViewById(R.id.tv_login_retrievepwd);
		// rl_login_bottom = (RelativeLayout)
		// findViewById(R.id.rl_login_bottom);
	}

	private void DLHeadIcon(String url) {
		if (CheckUtils.getInstance().isIcon(GlobalConstant.HEAD_ICON_PATH)) {
			File f = new File(GlobalConstant.HEAD_ICON_PATH);
			f.delete();
		}
		httpHelp.downLoad(url, GlobalConstant.HEAD_ICON_PATH, null);
		PrefUtils.setString(GlobalConstant.USER_FACE,
				GlobalConstant.HEAD_ICON_PATH);
	}

	private ProgressDialog pd;

	private void initProgress() {
		progressShow = true;
		pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();
	}

	private boolean progressShow;

	public void Login(final String currentUsername, final String currentPassword) {
		EMChatManager.getInstance().login(currentUsername, currentPassword,
				new EMCallBack() {

					@Override
					public void onSuccess() {
						if (!progressShow) {
							return;
						}
						LogUtil.e(LoginActivity.this,
								"**************登陆成功*************");
						SDKHelper.getInstance().reset();
						// 登陆成功，保存用户名密码
						MyApplication.getInstance()
								.setUserName(currentUsername);
						MyApplication.getInstance()
								.setPassword(currentPassword);

						try {
							// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
							// ** manually load all local groups and
							EMGroupManager.getInstance().loadAllGroups();
							EMChatManager.getInstance().loadAllConversations();
							// 处理好友和群组
							initializeContacts();
						} catch (Exception e) {
							e.printStackTrace();
							// 取好友或者群聊失败，不让进入主页面
							runOnUiThread(new Runnable() {
								public void run() {
									pd.dismiss();
									MySDKHelper.getInstance()
											.logout(true, null);
									Toast.makeText(getApplicationContext(),
											R.string.login_failure_failed, 1)
											.show();
								}
							});
							// pd.dismiss();
							// PrefUtils.setBoolean(UIUtils.getContext(),
							// GlobalConstant.IS_LOGINED, true);
							// Intent intent = new Intent(LoginActivity.this,
							// MainActivity.class);
							// startActivity(intent);
							return;
						}
						// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
						// boolean updatenick = EMChatManager.getInstance()
						// .updateCurrentUserNick(
						// MyApplication.currentUserNick.trim());
						// if (!updatenick) {
						// Log.e("LoginActivity_1",
						// "update current user nick fail");
						// }
						getUserIntrest();

					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(final int code, final String message) {
						if (!progressShow) {
							return;
						}
						runOnUiThread(new Runnable() {
							public void run() {
								pd.dismiss();
								Toast.makeText(
										getApplicationContext(),
										getString(R.string.Login_failed)
												+ message, Toast.LENGTH_SHORT)
										.show();
							}
						});
					}
				});

	}

	/**
	 * 获取栏目信息
	 */
	private void getUserIntrest() {
		httpHelp.sendGet(NetworkConfig.getUserIntrest(), ChannelBean.class,
				new MyRequestCallBack<ChannelBean>() {

					@Override
					public void onSucceed(ChannelBean bean) {
						ChannelManage manage = ChannelManage
								.getManage(new SQLHelper(
										getApplicationContext()));
						if (manage == null) {
							if (LogUtil.getDeBugState()) {
								throw new RuntimeException();
							} else {
								return;
							}
						}
						manage.deleteAllChannel();
						ChannelItem homechannelItem = new ChannelItem(-1, "精选",
								0, 1);
						manage.addCache(homechannelItem);
						if (bean != null && bean.cont != null) {
							List<Channel> list = bean.cont.list;
							ArrayList<String> ids = new ArrayList<String>();
							for (Channel channel : list) {
								if (channel != null) {
									ids.add(channel.typeid + "");
								}
							}

							List<Channel> listall = bean.cont.listall;
							int UserCount = 1;
							int otherCount = 0;
							for (int i = 0; i < listall.size(); i++) {
								Channel channel = listall.get(i);
								if (ids.contains(channel.typeid + "")) {
									ChannelItem channelItem = new ChannelItem(
											channel.typeid, channel.typename,
											UserCount, 1);
									manage.addCache(channelItem);
									UserCount++;
								} else {
									ChannelItem channelItem = new ChannelItem(
											channel.typeid, channel.typename,
											otherCount, 0);
									manage.addCache(channelItem);
									otherCount++;
								}
							}

						}

						enterMain();

					}
				});
	}

	/**
	 * 进入
	 */
	private void enterMain() {
		if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
			pd.dismiss();
		}
		// 进入主页面
		PrefUtils.setBoolean(UIUtils.getContext(), GlobalConstant.IS_LOGINED,
				true);

		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void saveUserInfo(LoginBean bean) {

		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_ID,
				bean.cont.uid);
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.UCODE,
				bean.cont.ucode);
		Log.e("UCODE", bean.cont.ucode);
		PrefUtils.setString(UIUtils.getContext(),
				GlobalConstant.IS_INFOCOMPLETE, bean.cont.isinfocomplete);

		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_NICKNAME,
				bean.cont.nickname);
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_SEX,
				ChangeUtils.ChangeNumberToSex(bean.cont.sex));
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_BIRTH,
				bean.cont.birth);
		PrefUtils.setString(UIUtils.getContext(),
				GlobalConstant.USER_SIGNATURE, bean.cont.signature);
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_PLAND,
				bean.cont.pland);
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_JOB,
				bean.cont.job);
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_HOBBY,
				bean.cont.hobby);
		PrefUtils.setString(GlobalConstant.USER_TEL, bean.cont.tel);
		PrefUtils.setString(GlobalConstant.USER_TEL_VERIFY,
				bean.cont.tel_verify);
		PrefUtils.setString(GlobalConstant.USER_EMAIL, bean.cont.email);
		PrefUtils.setString(GlobalConstant.USER_EMAIL_VERIFY,
				bean.cont.email_verify);
		PrefUtils.setString(GlobalConstant.USER_ISEDITPWD, bean.cont.iseditpwd);

		PrefUtils.setString(GlobalConstant.USER_HXUID, bean.cont.txinfo.hxuid);
		PrefUtils.setString(GlobalConstant.USER_HXPWD, bean.cont.txinfo.hxpwd);

		PrefUtils.setString(GlobalConstant.USER_DOYENTYPE, bean.cont.darentype);
		DLHeadIcon(bean.cont.face);
	}

	private void initializeContacts() {
		Map<String, User> userlist = new HashMap<String, User>();
		// 添加user"申请与通知"
		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		String strChat = getResources().getString(
				R.string.Application_and_notify);
		newFriends.setNick(strChat);
		newFriends.setHeader("");
		userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
		// 添加"群聊"
		// User groupUser = new User();
		// String strGroup = getResources().getString(R.string.group_chat);
		// groupUser.setUsername(Constant.GROUP_USERNAME);
		// groupUser.setNick(strGroup);
		// groupUser.setHeader("");
		// userlist.put(Constant.GROUP_USERNAME, groupUser);

		// 添加"约会邀请"
		User engagementUser = new User();
		String strInvitation = getResources().getString(
				R.string.appointment_invitation);
		engagementUser.setUsername(Constant.APPOINTMENT_INVITATION);
		engagementUser.setNick(strInvitation);
		engagementUser.setHeader("");
		userlist.put(Constant.APPOINTMENT_INVITATION, engagementUser);
//		// 添加"官方"
//		User officcialUser = new User();
//
//		String fu = getResources().getString(R.string.official_user);
//		officcialUser.setUsername(Constant.OFFICIAL_USER);
//		officcialUser.setNick(fu);
//		officcialUser.setHeader("");
//		userlist.put(Constant.OFFICIAL_USER, officcialUser);
		// 添加"Robot"
		// User robotUser = new User();
		// String strRobot = getResources().getString(R.string.robot_chat);
		// robotUser.setUsername(Constant.CHAT_ROBOT);
		// robotUser.setNick(strRobot);
		// robotUser.setHeader("");
		// userlist.put(Constant.CHAT_ROBOT, robotUser);

		// 存入内存
		((MySDKHelper) SDKHelper.getInstance()).setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(LoginActivity.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}

	@Override
	public void onClick(View v) {
		Intent loginIntent;
		switch (v.getId()) {
		case R.id.tv_login_login:
			String uid = et_login_username.getText().toString().trim();
			String pwd = et_login_userpwd.getText().toString().trim();
			if ("".equals(uid) || "".equals(pwd)) {
				UIUtils.showToast(UIUtils.getContext(), "帐号或密码不能为空");
			} else {
				initProgress();
				httpHelp.sendGet(NetworkConfig.getLogin(uid, pwd),
						LoginBean.class, new MyRequestCallBack<LoginBean>() {

							@Override
							public void onSucceed(LoginBean bean) {

								if (bean == null) {
									pd.dismiss();
									return;
								}

								if ("1".equals(bean.status)) {
									saveUserInfo(bean);
									if ("0".equals(bean.cont.shejiaoactive)) {
										ShowAlertDialog s = new ShowAlertDialog(
												LoginActivity.this);
									} else {
										UIUtils.showToast(UIUtils.getContext(),
												bean.msg);
										if (bean.cont.txinfo.hxuid != null
												&& bean.cont.txinfo.hxpwd != null) {
											DynamicShowDao.getInstance()
													.deleteAllData();
											UIUtils.showToast(
													UIUtils.getContext(),
													bean.msg);
											Login(bean.cont.txinfo.hxuid,
													bean.cont.txinfo.hxpwd);
										}
									}
								} else {
									UIUtils.showToast(UIUtils.getContext(),
											bean.msg);
									pd.dismiss();
								}
							}

						});
			}

			break;
		case R.id.tv_login_gotoregister:
			loginIntent = new Intent(LoginActivity.this, RegisterActivity.class);
			loginIntent.putExtra("fromLogin", true);
			startActivityForResult(loginIntent, 1);
			break;
		case R.id.tv_login_retrievepwd:
			loginIntent = new Intent(LoginActivity.this,
					RetrievePasswordActivity.class);
			startActivityForResult(loginIntent, 2);
			break;
		case R.id.v_login:
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1) {
			if (data.getStringExtra("register") != null) {
				et_login_username.setText(data.getStringExtra("register")
						.toString());
				et_login_username.setSelection(et_login_username.getText()
						.toString().length());
			}
		}
		if (resultCode == 2) {
			if (data.getStringExtra("tel") != null) {
				et_login_username
						.setText(data.getStringExtra("tel").toString());
				et_login_username.setSelection(et_login_username.getText()
						.toString().length());
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
