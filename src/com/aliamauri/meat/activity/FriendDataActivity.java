package com.aliamauri.meat.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.db.Dynamic_db.DynamicShowDao;
import com.aliamauri.meat.eventBus.FriendData_bus;
import com.aliamauri.meat.eventBus.RefurbishDTItem;
import com.aliamauri.meat.eventBus.UpdateShowMsg;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.MySwitch;
import com.aliamauri.meat.view.MySwitch.OnCheckedChangeListener;
import com.aliamauri.meat.weight.DeleteFriend_dialog;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class FriendDataActivity extends BaseActivity implements OnClickListener {

	private final int MORE_STATE = 1;
	private final int EDIT_STATE = 2;
	public final static String TAG = "FriendDataActivity";
	public final static String UPDATE_TAG = "update";
	public final static String UPDATE_USERNAME_TAG = "UPDATE_USERNAME_TAG";// 更新用户名操作
	public final static String CLOSE_TAG = "close";
	private int current_state;
	private String mId;
	private String mNickName;
	private String mIsShiele;// 是否屏蔽该好友
	private TextView mLeft_text;

	private MySwitch mMs_switch;
	private EditText mEt_real_name;
	private RelativeLayout mRl_edit_name, mRl_layout;
	private HttpHelp mHttp;

	@Override
	protected View getRootView() {
		return View.inflate(getApplicationContext(),
				R.layout.activity_friend_data, null);
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
	
	@Override
	protected void initView() {
		current_state = MORE_STATE;
		mHttp = new HttpHelp();
		mId = baseIntent.getStringExtra(GlobalConstant.FRIEND_ID);
		mNickName = baseIntent.getStringExtra(GlobalConstant.FRIEND_NICKNAME);
		mIsShiele = baseIntent.getStringExtra(GlobalConstant.FRIEND_IS_SHIELD);
		mLeft_text = $(R.id.tv_left_text);
		mMs_switch = $(R.id.ms_switch);
		mMs_switch.setOpen(mIsShiele);
		mMs_switch.setOnChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MySwitch mySwitch, boolean isOpen) {
				if (!isOpen) { // 关注操作
					setFriendAction(NetworkConfig.SHIELDNO_TAG, isOpen);
				} else { // 屏蔽操作
					setFriendAction(NetworkConfig.SHIELDYES_TAG, isOpen);
				}
			}
		});
		mRl_edit_name = $(R.id.rl_edit_name);
		mRl_layout = $(R.id.rl_layout);
		$(R.id.tv_ok).setOnClickListener(this);
		$(R.id.delete_friend).setOnClickListener(this);
		$(R.id.iv_title_backicon).setOnClickListener(this);
		mEt_real_name = $(R.id.et_real_name);
		((TextView) $(R.id.tv_title_title)).setText("更多");
		mLeft_text.setText(mNickName);
		$(R.id.rl_layout1).setOnClickListener(this);
	}

	/**
	 * 发送屏蔽ta操作
	 * 
	 * @param isOpen
	 */
	private void setFriendAction(final String action, final boolean isOpen) {
		mHttp.sendGet(NetworkConfig.getShieldFriend(mId, action),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || bean.status == null
								|| bean.msg == null) {
							UIUtils.showToast(getApplicationContext(), "修改失败!");
							return;
						}

						if ("1".equals(bean.status)) {
							if (isOpen) { // 关注操作
								UIUtils.showToast(getApplicationContext(),
										bean.msg);
							} else { // 屏蔽操作
								UIUtils.showToast(getApplicationContext(),
										bean.msg);
								EventBus.getDefault().post(
										new RefurbishDTItem(mId,
												GlobalConstant.SHIELD_TAG)); // 该uid的动态本地全部删除
							}
						} else {
							UIUtils.showToast(getApplicationContext(), bean.msg);
						}
					}
				});

	}

	@Override
	protected void onDestroy() {
		if (mHttp != null) {
			mHttp.stopHttpNET();
		}
		super.onDestroy();
	}

	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_backicon:
			if (current_state == MORE_STATE) {
				setExitSwichLayout();
			} else {
				mRl_edit_name.setVisibility(View.GONE);
				mRl_layout.setVisibility(View.VISIBLE);
				current_state = MORE_STATE;
			}
			break;
		case R.id.rl_layout1:
			current_state = EDIT_STATE;
			mRl_edit_name.setVisibility(View.VISIBLE);
			mRl_layout.setVisibility(View.GONE);
			break;
		case R.id.delete_friend:
			
			new DeleteFriend_dialog(this, TAG) {
				
				@Override
				public void setButton_ok() {
					deleteFriendWork();
				}
				@Override
				public String getTitleText() {
					
					return "删除该好友？";
				}
			};
			
			break;
		case R.id.tv_ok:
			String str = mEt_real_name.getText().toString().trim();
			if (!StringUtils.isEmpty(str)) {
				postDataToNet(str);
			} else {
				UIUtils.showToast(getApplicationContext(), "请输入文字~");
			}
			break;
		}
	}
	/**
	 * 删除好友操作
	 */
	private void deleteFriendWork() {
		CmdManager.getInstance().deleteContact(mId,
				new CmdManagerCallBack() {

					@Override
					public void onState(boolean isSucceed) {
						// if(isSucceed){
						// MainActivity activity = (MainActivity)
						// MyApplication.getMainActivity();
						// if(activity !=null){
						// activity.refreshUI();
						// }
//						 setExitSwichLayout();
						UIUtils.showToast(getApplicationContext(), "删除成功");
						 EventBus.getDefault().post(new
						 FriendData_bus(CLOSE_TAG, null));
						// }
					}
				});
	}
	/**
	 * 将修改的备注提交到网上
	 * 
	 * @param str
	 */
	private void postDataToNet(final String str) {
		KeyBoardUtils.closeKeybord(getApplicationContext(), mEt_real_name);
		RequestParams params = new RequestParams();
		params.addBodyParameter("touid", mId);
		params.addBodyParameter("remark", str);
		mHttp.sendPost(NetworkConfig.setRemark_url(), params, BaseBaen.class,
				new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean != null) {
							if ("1".equals(bean.status)) {
								DynamicShowDao.getInstance()
										.UpdateDynamicTable(str,
												DynamicShowDao.COLUMN_NAME_UID,
												mId);
								current_state = MORE_STATE;
								mRl_edit_name.setVisibility(View.GONE);
								mRl_layout.setVisibility(View.VISIBLE);
								mLeft_text.setText(str);
								ContactManager.getInstance().updateUserNike(
										mId, str);
								EventBus.getDefault().post(
										new FriendData_bus(UPDATE_TAG, str));
								EventBus.getDefault().post(
										new UpdateShowMsg(UPDATE_USERNAME_TAG)
												.setmData(str).setId(mId));
								UIUtils.showToast(getApplicationContext(),
										bean.msg);

							} else {
								UIUtils.showToast(getApplicationContext(),
										bean.msg);
							}
						} else {
							UIUtils.showToast(getApplicationContext(), "提交失败");
						}
					}

				});
	}

}