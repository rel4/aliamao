package com.aliamauri.meat.activity;

import java.util.List;
import java.util.UUID;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.Manager.ContactManager.ContactManagerDataCallBack;
import com.aliamauri.meat.Manager.SendFileManager;
import com.aliamauri.meat.activity.IM.Constant;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.activity.IM.activity.GroupsActivity;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.db.AppointmentInvitationDao;
import com.aliamauri.meat.activity.IM.db.InviteMessgeDao;
import com.aliamauri.meat.activity.IM.db.UserDao;
import com.aliamauri.meat.activity.IM.domain.AppointmentInvitMessage;
import com.aliamauri.meat.activity.IM.domain.InviteMessage;
import com.aliamauri.meat.activity.IM.domain.Message.MesageStatus;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.activity.IM.utils.CommonUtils;
import com.aliamauri.meat.activity.IM.utils.PreferenceUtils;
import com.aliamauri.meat.bean.AttentionNumberBean;
import com.aliamauri.meat.bean.ContactInfoBean;
import com.aliamauri.meat.bean.ContactInfoBean.ContactInfo;
import com.aliamauri.meat.db.TempFileUpBeanTask;
import com.aliamauri.meat.db.TempUploadFileDao;
import com.aliamauri.meat.fragment.impl_child.ChatAllHistoryFragment;
import com.aliamauri.meat.fragment.impl_child.ContactlistFragment;
import com.aliamauri.meat.fragment.impl_supper.AddressPage;
import com.aliamauri.meat.fragment.impl_supper.FindPage;
import com.aliamauri.meat.fragment.impl_supper.MyPage;
import com.aliamauri.meat.fragment.impl_supper.NearbyPage;
import com.aliamauri.meat.fragment.impl_supper.SearchPage;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.thread.CMDThreadHelper;
import com.aliamauri.meat.update.UpdateManager;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.MyBDmapUtlis;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.gif.GifFragment;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;

public class MainActivity extends FragmentActivity implements OnClickListener,
		EMEventListener {
	private static String TAG = "MainActivity";
	private HttpHelp httpHelp;
	// 未读消息textview
	private TextView unreadLabel;
	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	// 当前fragment的index 0会话 1代表通讯录
	private InviteMessgeDao inviteMessgeDao;
	/**
	 * 约会邀请
	 */
	public AppointmentInvitationDao appointmentInvitationDao;
	private UserDao userDao;
	// 当前fragment的index 0会话 1代表通讯录
	public static ContactlistFragment contactListFragment;
	private static ChatAllHistoryFragment chatHistoryFragment;
	private boolean isCurrentAccountRemoved = false;
	private FrameLayout mFl_home_content;
	private LinearLayout mLl_home_title_nearby, mLl_home_title_addr,
			mLl_home_title_find, mLl_home_title_my;
	private ImageView mIv_home_title_search_icon;
	private FragmentManager fm; // 获取fragment管理器

	private NearbyPage np; // 附近fragment
	private AddressPage ap; // 通讯录fragment
	private SearchPage sp; // 查找 fragment
	private FindPage fp; // 发现fragment
	// private Fragment fp;
	public MyPage mp; // 我的fragment
	private MyConnectionListener connectionListener = null;
	private MyGroupChangeListener groupChangeListener = null;
	/********************** 标示 ******************************/
	public final static int MSG_UPDATE_CHATLIST_AND_CONTACTLIST = 1;
	public final static int MSG_UPDATE_CHATLIST = 2;
	public final static int MSG_UPDATE_CONTACTLIST = 3;
	public final static int MSG_NOTIFY_BLACKLIST = 4;
	public final static int MSG_NOTIFY_BLACKLIST_AND_BLACK = 5;
	private android.app.Fragment newHandFragment;

	/**
	 * 更新界面
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		FragmentManager fm = getSupportFragmentManager();
		int index = requestCode >> 16;
		if (index != 0) {
			index--;
			if (fm.getFragments() == null || index < 0
					|| index >= fm.getFragments().size()) {
				LogUtil.e(TAG,
						"Activity result fragment index out of range: 0x"
								+ Integer.toHexString(requestCode));
				return;
			}
			Fragment frag = fm.getFragments().get(index);
			if (frag == null) {
				LogUtil.e(TAG,
						"Activity result no fragment exists for index: 0x"
								+ Integer.toHexString(requestCode));
			} else {
				handleResult(frag, requestCode, resultCode, data);
			}
			return;
		}
	}

	/**
	 * 递归调用，对所有子Fragement生效
	 * 
	 * @param frag
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	private void handleResult(Fragment frag, int requestCode, int resultCode,
			Intent data) {
		frag.onActivityResult(requestCode & 0xffff, resultCode, data);
		List<Fragment> frags = frag.getChildFragmentManager().getFragments();
		if (frags != null) {
			for (Fragment f : frags) {
				if (f != null)
					handleResult(f, requestCode, resultCode, data);
			}
		}
	}

	public static Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			LogUtil.e(this, "*************完成msg.what***********:" + msg.what);
			switch (msg.what) {

			case MSG_UPDATE_CHATLIST_AND_CONTACTLIST:// 更新联系人和会话列表
				if (contactListFragment != null) {
					contactListFragment.refresh();
				}
				if (chatHistoryFragment != null) {
					chatHistoryFragment.refresh();
				}
				break;
			case MSG_UPDATE_CHATLIST:// 更新会话列表
				if (chatHistoryFragment != null) {
					chatHistoryFragment.refresh();
				}
				break;
			case MSG_UPDATE_CONTACTLIST:// 更新联系人列表
				if (contactListFragment != null) {
					contactListFragment.refresh();
				}
				break;
			case MSG_NOTIFY_BLACKLIST:// 更新联系人列表
				SDKHelper.getInstance().notifyContactsSyncListener(true);
				break;
			case MSG_NOTIFY_BLACKLIST_AND_BLACK:// 更新联系人列表
				// SDKHelper.getInstance().notifyContactsSyncListener(true);
				SDKHelper.getInstance().notifyBlackListSyncListener(true);

				break;
			}
			LogUtil.e(this, "*************完成msg.what***********:" + msg.what);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.setMainActivity(this);
		CMDThreadHelper.getInstance().setHandler(mHandler);
		if (inviteMessgeDao == null) {

			inviteMessgeDao = new InviteMessgeDao(this);
		}
		if (appointmentInvitationDao == null) {

			appointmentInvitationDao = new AppointmentInvitationDao(this);
		}
		if (userDao == null) {
			userDao = new UserDao(this);
		}
		if (contactListFragment == null) {
			contactListFragment = new ContactlistFragment();
		}
		if (chatHistoryFragment == null) {
			chatHistoryFragment = new ChatAllHistoryFragment();

		}
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED,
						false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			MySDKHelper.getInstance().logout(true, null);

			PrefUtils.setBoolean(UIUtils.getContext(),
					GlobalConstant.IS_LOGINED, false);
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return;
		} else if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理

			PrefUtils.setBoolean(UIUtils.getContext(),
					GlobalConstant.IS_LOGINED, false);
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return;
		}
		setContentView(R.layout.activity_main);

		saveUserLocation();
		initView();
		setClicked();
		fm = getSupportFragmentManager();
		setPageSelection(GlobalConstant.NEARBY_PAGE);// 设置默认选中页面
		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}

		initAll();

		// 异步获取当前用户的昵称和头像
		// ((MySDKHelper) SDKHelper.getInstance()).getUserProfileManager()
		// .asyncGetCurrentUserInfo();
		// startActivity(new Intent(MainActivity.this,
		// CompleteDataActivity.class));
		// ShowAlertDialog s = new ShowAlertDialog(MainActivity.this);
		initNETData();
		if (!PrefUtils
				.getBoolean(GlobalConstant.NOT_NEWHAND_GOTOSETTING, false)) {
			PrefUtils.setBoolean(GlobalConstant.NOT_NEWHAND_GOTOSETTING, true);
			showView(0);
		}
		// showAnimate();
	}

	public void showView(int type) {
		newHandFragment = new GifFragment(MainActivity.this, type,
				R.raw.gif_finger);
		getFragmentManager().beginTransaction()
				.replace(R.id.rl_amain, newHandFragment).commit();
	}

	private HttpHelp getHttpHelp() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		return httpHelp;
	}

	public void initNETData() {
		getHttpHelp().sendGet(NetworkConfig.getfofans(),
				AttentionNumberBean.class,
				new MyRequestCallBack<AttentionNumberBean>() {

					@Override
					public void onSucceed(AttentionNumberBean bean) {
						if (bean == null) {
							return;
						}
						if ("1".equals(bean.status)) {
							if (CheckUtils.getInstance().isNumber(
									bean.cont.followscount)) {
								PrefUtils
										.setInt(GlobalConstant.USER_FOLLOWSCOUNT,
												Integer.parseInt(bean.cont.followscount));
							} else {
								PrefUtils.setInt(
										GlobalConstant.USER_FOLLOWSCOUNT, 0);
							}

							if (CheckUtils.getInstance().isNumber(
									bean.cont.fanscount)) {
								PrefUtils.setInt(GlobalConstant.USER_FANSCOUNT,
										Integer.parseInt(bean.cont.fanscount));
							} else {
								PrefUtils.setInt(GlobalConstant.USER_FANSCOUNT,
										0);
							}

						}

					}
				});
	}

	/**
	 * 获取用户的经纬度
	 * 
	 * @return
	 */
	private void saveUserLocation() {
		MyBDmapUtlis myBDmapUtlis = new MyBDmapUtlis(MainActivity.this);
		myBDmapUtlis.getCurrentLocation();
	}

	/**
	 * 获取消息列表
	 * 
	 * @return
	 */
	public ChatAllHistoryFragment getChatAllHistoryFragment() {

		return chatHistoryFragment;
	}

	public ContactlistFragment getContactlistFragment() {

		return contactListFragment;
	}

	/**
	 * 设置点击事件
	 */
	private void setClicked() {
		mLl_home_title_nearby.setOnClickListener(this);
		mLl_home_title_addr.setOnClickListener(this);
		mIv_home_title_search_icon.setOnClickListener(this);
		mLl_home_title_find.setOnClickListener(this);
		mLl_home_title_my.setOnClickListener(this);

	}

	/**
	 * 初始化控件变量
	 */
	private void initView() {
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		mFl_home_content = $(R.id.fl_home_content);
		mLl_home_title_nearby = $(R.id.ll_home_title_nearby);
		mLl_home_title_addr = $(R.id.ll_home_title_addr);
		mIv_home_title_search_icon = $(R.id.iv_home_title_search_icon);
		mLl_home_title_find = $(R.id.ll_home_title_find);
		mLl_home_title_my = $(R.id.ll_home_title_my);
	}

	/**
	 * 外界通过此方法来获取 话题页面
	 * 
	 * @return
	 */
	public NearbyPage getNearbyPage() {

		FragmentManager sfm = getSupportFragmentManager();

		NearbyPage nearby_fm = (NearbyPage) sfm
				.findFragmentByTag(GlobalConstant.F_NEARBY_TAG);

		return nearby_fm;
	}

	/**
	 * 外界通过此方法来获取 我的页面
	 * 
	 * @return
	 */
	public void getMyPage() {

		setPageSelection(GlobalConstant.MY_PAGE);
	}

	/**
	 * 根据id查找控件
	 * 
	 * @param id
	 * @return
	 */
	public <T extends View> T $(int id) {
		return (T) findViewById(id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_home_title_nearby: // 附近页面
			setPageSelection(GlobalConstant.NEARBY_PAGE);
			break;
		case R.id.ll_home_title_addr: // 通讯录页面
			deleteDianCais();
			setPageSelection(GlobalConstant.ADDR_PAGE);
			if (unreadLabel != null) {
				unreadLabel.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.iv_home_title_search_icon: // 查找页面
			deleteDianCais();
			setPageSelection(GlobalConstant.SEARCH_PAGE);
			break;
		case R.id.ll_home_title_find: // 发现页面
			deleteDianCais();
			setPageSelection(GlobalConstant.FIND_PAGE);
			break;
		case R.id.ll_home_title_my: // 我的页面
			deleteDianCais();
			setPageSelection(GlobalConstant.MY_PAGE);
			break;

		}
	}

	/**
	 * 删除全局中的顶踩集合中的数据
	 */
	private void deleteDianCais() {
		// 删除资源库中的存储点赞的条目id
		if (MyApplication.UpDowns != null) {
			MyApplication.UpDowns.clear();
			MyApplication.UpDowns = null;
		}

	}

	/**
	 * 获取点击后的页面改变
	 * 
	 * @param index
	 */
	private void setPageSelection(int index) {
		FragmentTransaction transaction = fm.beginTransaction();
		hideFragments(transaction);
		setCurrentState(index);
		switch (index) {
		case GlobalConstant.NEARBY_PAGE:

			if (np == null) {
				np = new NearbyPage();
				transaction.add(R.id.fl_home_content, np,
						GlobalConstant.F_NEARBY_TAG);
			} else {
				transaction.show(np);
			}

			break;
		case GlobalConstant.ADDR_PAGE:

			if (ap == null) {
				ap = new AddressPage();
				transaction.add(R.id.fl_home_content, ap,
						GlobalConstant.F_ADDRESS_TAG);
			} else {
				transaction.show(ap);
			}
			break;
		case GlobalConstant.SEARCH_PAGE:

			if (sp == null) {
				sp = new SearchPage();
				transaction.add(R.id.fl_home_content, sp,
						GlobalConstant.F_SEARCH_TAG);
			} else {
				transaction.show(sp);
			}
			break;
		case GlobalConstant.FIND_PAGE:

			if (fp == null) {
				fp = new FindPage();
				transaction.add(R.id.fl_home_content, fp,
						GlobalConstant.F_FIND_TAG);
			} else {
				transaction.show(fp);
			}
			break;
		case GlobalConstant.MY_PAGE:

			if (mp == null) {
				mp = new MyPage();
				transaction.add(R.id.fl_home_content, mp,
						GlobalConstant.F_MY_TAG);
			} else {
				transaction.show(mp);
			}
			break;
		}

		transaction.commit();
	}

	/**
	 * 设置当前图标的状态
	 * 
	 * @param nearbyPage
	 */
	private void setCurrentState(int num) {// 点击标签的时候改变图标和文字的颜色,状态
		if (num == GlobalConstant.NEARBY_PAGE) {
			mLl_home_title_nearby.setSelected(true);
		} else {
			mLl_home_title_nearby.setSelected(false);
		}

		if (num == GlobalConstant.ADDR_PAGE) {
			mLl_home_title_addr.setSelected(true);
		} else {
			mLl_home_title_addr.setSelected(false);
		}

		if (num == GlobalConstant.FIND_PAGE) {
			mLl_home_title_find.setSelected(true);
		} else {
			mLl_home_title_find.setSelected(false);
		}
		if (num == GlobalConstant.MY_PAGE) {
			mLl_home_title_my.setSelected(true);
		} else {
			mLl_home_title_my.setSelected(false);
		}

	}

	/**
	 * 在选择之前隐藏所有的fragment
	 * 
	 * @param transaction
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (ap != null) {
			transaction.hide(ap);
		}
		if (fp != null) {
			transaction.hide(fp);
		}
		if (mp != null) {
			transaction.hide(mp);
		}
		if (np != null) {
			transaction.hide(np);
		}
		if (sp != null) {
			transaction.hide(sp);
		}

	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (unreadLabel != null) {
			unreadLabel.setText(String.valueOf(count));
		}
		if (count > 0) {
			if (ap == null) {
				unreadLabel.setVisibility(View.VISIBLE);
			} else if (ap != null && !ap.isVisible()) {
				unreadLabel.setVisibility(View.VISIBLE);
			} else {
				unreadLabel.setVisibility(View.INVISIBLE);
			}

		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for (EMConversation conversation : EMChatManager.getInstance()
				.getAllConversations().values()) {
			if (conversation.getType() == EMConversationType.ChatRoom)
				chatroomUnreadMsgCount = chatroomUnreadMsgCount
						+ conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal - chatroomUnreadMsgCount;
	}

	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;

	/**
	 * 显示帐号在别处登录dialog
	 */
	public void showConflictDialog() {

		isConflictDialogShow = true;
		MySDKHelper.getInstance().logout(false, null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								conflictBuilder = null;
								finish();
								PrefUtils.setBoolean(UIUtils.getContext(),
										GlobalConstant.IS_LOGINED, false);
								startActivity(new Intent(MainActivity.this,
										LoginActivity.class));
							}
						});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				// EMLog.e(TAG, "---------color conflictBuilder error" +
				// e.getMessage());
				LogUtil.e(this, "退出异常 ： " + e.getMessage());
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		MySDKHelper.getInstance().logout(true, null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								accountRemovedBuilder = null;
								finish();
								PrefUtils.setBoolean(UIUtils.getContext(),
										GlobalConstant.IS_LOGINED, false);
								startActivity(new Intent(MainActivity.this,
										LoginActivity.class));
							}
						});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				// EMLog.e(TAG, "---------color userRemovedBuilder error" +
				// e.getMessage());
			}

		}

	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// moveTaskToBack(false);
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	//
	/**
	 * 初始化所有
	 */
	private void initAll() {
		// 初始监听器
		initListener();
		// initReceiver();
		// 内部测试方法，请忽略
		// registerInternalDebugReceiver();

		initCurrentUser();
		new UpdateManager(this).checkUpdate();
		// 唤醒上传服务
		awakenTaskService();
	}

	/**
	 * 初始化当前用户信息
	 */
	private void initCurrentUser() {

		String currentUserNick = PreferenceUtils.getInstance()
				.getCurrentUserNick();
		if (!TextUtils.isEmpty(currentUserNick)) {
			return;
		}
		String currentUserId = PrefUtils
				.getString(GlobalConstant.USER_ID, null);
		if (TextUtils.isEmpty(currentUserId)) {
			return;
		}
		ContactManager.getInstance().findContactbyId(currentUserId,
				new ContactManagerDataCallBack<ContactInfoBean>() {

					@Override
					public void onData(boolean isSucceed, ContactInfoBean t) {
						if (isSucceed) {
							saveCurrenUserInfo(t);
						}

					}
				});

	}

	/**
	 * 唤醒上传服务
	 */
	private void awakenTaskService() {
		Log.e("SendFileManager", "开始上传任务服务");
		boolean isHaveTask = SendFileManager.getInstance().isHaveTask();
		if (isHaveTask) {
			return;
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				List<TempFileUpBeanTask> tempFileUpTask = new TempUploadFileDao()
						.getTempFileUpTask();
				Log.e("SendFileManager",
						"开始上传任务，任务数为---->" + tempFileUpTask.size());
				if (tempFileUpTask != null && !tempFileUpTask.isEmpty()) {
					SendFileManager.getInstance().awakenTask();
				}
			}
		}, 60000);
	}

	/**
	 * 保存当前用户信息
	 * 
	 * @param t
	 */
	private void saveCurrenUserInfo(ContactInfoBean infos) {
		ContactInfo contact = infos.cont.get(0);
		if (contact == null) {
			return;
		}
		PreferenceUtils instance = PreferenceUtils.getInstance();
		instance.setCurrentUserNick(contact.nickname);
		instance.setCurrentUserAvatar(contact.face);
	}

	/**
	 * 初始化环信监听
	 */
	private void initListener() {

		connectionListener = new MyConnectionListener();
		EMChatManager.getInstance().addConnectionListener(connectionListener);

		groupChangeListener = new MyGroupChangeListener();
		// 注册群聊相关的listener
		EMGroupManager.getInstance()
				.addGroupChangeListener(groupChangeListener);

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			if (!isConflict && !isCurrentAccountRemoved) {
				updateUnreadLabel();
				EMChatManager.getInstance().activityResumed();
			}
			MySDKHelper sdkHelper = (MySDKHelper) MySDKHelper.getInstance();
			sdkHelper.pushActivity(this);
			EMChatManager
					.getInstance()
					.registerEventListener(
							this,
							new EMNotifierEvent.Event[] {
									EMNotifierEvent.Event.EventNewMessage,
									EMNotifierEvent.Event.EventOfflineMessage,
									EMNotifierEvent.Event.EventConversationListChanged });
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void onStop() {
		try {
			super.onStop();
			if (this != null) {
				EMChatManager.getInstance().unregisterEventListener(this);
				MySDKHelper sdkHelper = (MySDKHelper) MySDKHelper.getInstance();
				sdkHelper.popActivity(this);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	/**
	 * 连接监听listener
	 * 
	 */
	public class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			boolean groupSynced = SDKHelper.getInstance()
					.isGroupsSyncedWithServer();
			boolean contactSynced = SDKHelper.getInstance()
					.isContactsSyncedWithServer();
			boolean blackContactsSynced = SDKHelper.getInstance()
					.isBlackListSyncedWithServer();

			// in case group and contact were already synced, we supposed to
			// notify sdk we are ready to receive the events
			if (groupSynced && contactSynced && blackContactsSynced) {
				new Thread() {
					@Override
					public void run() {
						SDKHelper.getInstance().notifyForRecevingEvents();
					}
				}.start();
			} else {
				if (!groupSynced) {
					// 初始化群
					asyncFetchGroupsFromServer();
				}

				if (!contactSynced) {
					// 初始化联系人
					asyncFetchContactsFromServer();
				}
				// 初始化黑名单
				if (!blackContactsSynced) {
					asyncFetchBlackListFromServer();
				}
			}

			runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (chatHistoryFragment != null
							&& chatHistoryFragment.errorItem != null) {
						chatHistoryFragment.errorItem.setVisibility(View.GONE);
					}
				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			final String st1 = getResources().getString(
					R.string.can_not_connect_chat_server_connection);
			final String st2 = getResources().getString(
					R.string.the_current_network);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						showAccountRemovedDialog();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						showConflictDialog();
					} else {
						if (chatHistoryFragment.errorItem != null) {

							chatHistoryFragment.errorItem
									.setVisibility(View.VISIBLE);
							if (NetUtils.hasNetwork(MainActivity.this))
								chatHistoryFragment.errorText.setText(st1);
							else
								chatHistoryFragment.errorText.setText(st2);

						}
					}
				}

			});
		}
	}

	/**
	 * MyGroupChangeListener
	 */
	public class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName,
				String inviter, String reason) {

			boolean hasGroup = false;
			for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					break;
				}
			}
			if (!hasGroup)
				return;

			// 被邀请
			String st3 = getResources().getString(
					R.string.Invite_you_to_join_a_group_chat);
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(inviter + " " + st3));
			// 保存邀请消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			SDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (chatHistoryFragment.isVisible())
						chatHistoryFragment.refresh();
					if (CommonUtils.getTopActivity(MainActivity.this).equals(
							GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter,
				String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee,
				String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {

			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						if (chatHistoryFragment.isVisible())
							chatHistoryFragment.refresh();
						if (CommonUtils.getTopActivity(MainActivity.this)
								.equals(GroupsActivity.class.getName())) {
							GroupsActivity.instance.onResume();
						}
					} catch (Exception e) {
						// EMLog.e(TAG, "refresh exception " + e.getMessage());
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {

			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					if (chatHistoryFragment.isVisible())
						chatHistoryFragment.refresh();
					if (CommonUtils.getTopActivity(MainActivity.this).equals(
							GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName,
				String applyer, String reason) {

			// 用户申请加入群聊
			InviteMessage msg = new InviteMessage();
			msg.setFromContactHxid(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			// Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
			msg.setStatus(MesageStatus.BEAPPLYED);
			notifyNewIviteMessage(msg, isAccountRemovedDialogShow);
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName,
				String accepter) {

			String st4 = getResources().getString(
					R.string.Agreed_to_your_group_chat_application);
			// 加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(accepter + " " + st4));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			SDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (chatHistoryFragment.isVisible())
						chatHistoryFragment.refresh();
					if (CommonUtils.getTopActivity(MainActivity.this).equals(
							GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName,
				String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}
	}

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	public void notifyNewAppointmentInvitationMsg(AppointmentInvitMessage msg) {
		saveAppointmentInvitationMsg(msg);

		// 刷新好友页面ui
		if (contactListFragment.isVisible())
			contactListFragment.refresh();

	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveAppointmentInvitationMsg(AppointmentInvitMessage msg) {
		// 保存msg
		appointmentInvitationDao.saveMessage(msg);
		if ("1".equals(msg.getIsInviteFromMe())) {
			ContactManager.getInstance().contactAdded(msg);
		} else {
			SDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);
			// 刷新bottom bar消息未读数
			updateUnreadAddressLable();
		}
		// 未读数加1
		// 更新约会联系人完毕
		SDKHelper.getInstance().notifyAppointmentSyncListener(true);
		User user = ((MySDKHelper) SDKHelper.getInstance()).getContactList()
				.get(Constant.APPOINTMENT_INVITATION);
		if (user.getAppointmentMsgCount() == 0)
			user.setAppointmentMsgCount(user.getUnreadMsgCount() + 1);

	}

	static void asyncFetchContactsFromServer() {
		LogUtil.e(TAG, "***********开始从服务器取联系人数据**********");
		SDKHelper.getInstance().asyncFetchContactsFromServer(
				new EMValueCallBack<List<ContactInfo>>() {

					@Override
					public void onSuccess(List<ContactInfo> infos) {
						if (infos != null) {
							LogUtil.e(TAG, "获取到联系人数据数量： " + infos.size());
						}
						CMDThreadHelper.getInstance().addTask(
								CMDThreadHelper.FLAG_PARSE_CONTACTLIST, infos);

					}

					@Override
					public void onError(int error, String errorMsg) {
						SDKHelper.getInstance().notifyContactsSyncListener(
								false);
						// 更新约会好友完毕
						SDKHelper.getInstance().notifyAppointmentSyncListener(
								false);
					}

				});

	}

	static void asyncFetchBlackListFromServer() {
		SDKHelper.getInstance().asyncFetchBlackListFromServer(
				new EMValueCallBack<List<ContactInfo>>() {

					@Override
					public void onSuccess(List<ContactInfo> infos) {
						if (infos == null) {
							return;
						}

						CMDThreadHelper.getInstance().addTask(
								CMDThreadHelper.FLAG_DISPOSE_BLACK_LIST, infos);

					}

					@Override
					public void onError(int error, String errorMsg) {
						SDKHelper.getInstance().notifyBlackListSyncListener(
								false);
					}

				});
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

		// 刷新好友页面ui
		if (contactListFragment != null)
			contactListFragment.refresh();
	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 * @param isDeleteMessage
	 */
	private void saveInviteMsg(InviteMessage msg, boolean isDeleteMessage) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
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
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				// if (count > 0) {
				// // unreadAddressLable.setText(String.valueOf(count));
				// unreadAddressLable.setVisibility(View.VISIBLE);
				// } else {
				// unreadAddressLable.setVisibility(View.INVISIBLE);
				// }
			}
		});

	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		if (((MySDKHelper) SDKHelper.getInstance()).getContactList().get(
				Constant.NEW_FRIENDS_USERNAME) != null)
			unreadAddressCountTotal = ((MySDKHelper) SDKHelper.getInstance())
					.getContactList().get(Constant.NEW_FRIENDS_USERNAME)
					.getUnreadMsgCount();
		return unreadAddressCountTotal;
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

	public static void asyncFetchGroupsFromServer() {

		SDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack() {

			@Override
			public void onSuccess() {
				// SDKHelper.getInstance().noitifyGroupSyncListeners(true);

				if (SDKHelper.getInstance().isContactsSyncedWithServer()) {
					SDKHelper.getInstance().notifyForRecevingEvents();
				}
			}

			@Override
			public void onError(int code, String message) {
				SDKHelper.getInstance().noitifyGroupSyncListeners(false);
			}

			@Override
			public void onProgress(int progress, String status) {

			}

		});
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();
			// UserDao userDao = new UserDao(UIUtils.getContext());
			// User contactInfo = userDao.getContactInfo(message.getFrom());
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
			// 提示新消息
			MySDKHelper.getInstance().getNotifier().onNewMsg(message);

			refreshUI();
			break;
		}

		case EventOfflineMessage: {
			refreshUI();
			break;
		}

		case EventConversationListChanged: {
			refreshUI();
			break;
		}

		default:
			break;
		}

	}

	private void refreshUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
				if (chatHistoryFragment != null) {
					// 当前页面如果为聊天历史页面，刷新此页面
					if (chatHistoryFragment != null) {
						chatHistoryFragment.refresh();
					}
				}
			}
		});
	}

}