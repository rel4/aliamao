package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.activity.IM.activity.ChatActivity;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.activity.nearby_activity.ShowDetailsImages;
import com.aliamauri.meat.bean.OtherDataBean;
import com.aliamauri.meat.bean.OtherDataBean.Cont.Photolist;
import com.aliamauri.meat.eventBus.FriendData_bus;
import com.aliamauri.meat.eventBus.PlayVideo_event;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.holder.OtherDataViewHolder;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.play.PlayActivity;
import com.aliamauri.meat.utils.CaculationUtils;
import com.aliamauri.meat.utils.ChangeUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageViewWhite;
import com.aliamauri.meat.view.HorizontalListView;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class OtherDataActivity extends BaseActivity implements OnClickListener {

	private CircleImageViewWhite ci_od_icon;
	private ImageView iv_od_backicon;
	private ImageView iv_od_blur;// 高斯模糊图片

	/* 资料 */
	private TextView tv_od_autheicate;
	private TextView tv_od_nickname;
	private TextView tv_od_otherid;
	private TextView tv_od_sex;
	private TextView tv_od_age;
	private HorizontalListView hlv_od_photos;
	private TextView tv_od_job;
	private TextView tv_od_pland;
	private TextView tv_od_hobbit;
	private TextView mTv_other_data_add_friend;
	private String mFrined_ID;// 添加好友的ID
	private String mIsShield;// 是否屏蔽该好友
	private HttpHelp mHttpHelp;
	private boolean isFriend = false;

	private User user;

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
	protected View getRootView() {
		EventBus.getDefault().register(this);
		View view = View.inflate(OtherDataActivity.this, R.layout.other_data,
				null);
		return view;
	}

	@Override
	protected void initView() {
		mFrined_ID = baseIntent
				.getStringExtra(GlobalConstant.COMMENT_ADD_FRIEND);
		mHttpHelp = new HttpHelp();
		iv_od_blur = (ImageView) findViewById(R.id.iv_od_blur);
		findViewById(R.id.rl_myspace_dt).setOnClickListener(this);
		ci_od_icon = (CircleImageViewWhite) findViewById(R.id.ci_od_icon);
		iv_od_backicon = (ImageView) findViewById(R.id.iv_od_backicon);
		tv_od_autheicate = (TextView) findViewById(R.id.tv_od_autheicate);
		tv_od_sex = (TextView) findViewById(R.id.tv_od_sex);
		tv_od_nickname = (TextView) findViewById(R.id.tv_od_nickname);
		tv_od_otherid = (TextView) findViewById(R.id.tv_od_otherid);
		tv_od_age = (TextView) findViewById(R.id.tv_od_age);
		TextView tv_od_more = (TextView) findViewById(R.id.tv_od_more);
		tv_od_more.setOnClickListener(this);
		tv_od_job = (TextView) findViewById(R.id.tv_od_job);
		tv_od_pland = (TextView) findViewById(R.id.tv_od_pland);
		tv_od_hobbit = (TextView) findViewById(R.id.tv_od_hobbit);
		hlv_od_photos = (HorizontalListView) findViewById(R.id.hlv_od_photos);
		mTv_other_data_add_friend = (TextView) findViewById(R.id.tv_other_data_add_friend);
		mFl_add_frined_btn = (FrameLayout) findViewById(R.id.fl_add_frined_btn);
		// 本地判断好友方法
		Map<String, User> allUid = ContactManager.getInstance()
				.getFriendContactUidMap();
		if (allUid.containsKey(mFrined_ID)) {
			mTv_other_data_add_friend.setText("发消息");
			isFriend = true;
			user = allUid.get(mFrined_ID);

			tv_od_more.setVisibility(View.VISIBLE);

			// NetData(); //删除这句话
			// **************小将哥**************************
			nativeData(user);
			mHttpHelp.sendGet(NetworkConfig.getUserInforPics(mFrined_ID),
					OtherDataBean.class,
					new MyRequestCallBack<OtherDataBean>() {

						@Override
						public void onSucceed(OtherDataBean bean) {
							if (bean == null || bean.cont == null
									&& bean.cont.photolist == null
									&& bean.cont.photolist.size() == 0) {
								return;
							}
							mIsShield = bean.cont.is_shield;
							hlv_od_photos.setAdapter(new AlbumAdapter(
									bean.cont.photolist));
						}
					});
			// **************小将哥**************************
		} else {
			tv_od_more.setVisibility(View.GONE);
			mTv_other_data_add_friend.setText("加为好友");
			isFriend = false;
			NetData();
		}

	}

	private void nativeData(User user) {

		tv_od_nickname.setText(TextUtils.isEmpty(user.getNick()) ? "暂无" : user
				.getNick());
		tv_od_otherid.setText(TextUtils.isEmpty(user.getUserId()) ? "暂无" : user
				.getUserId());
		tv_od_sex.setText(TextUtils.isEmpty(ChangeUtils.ChangeNumberToSex(user
				.getSex())) ? "暂无" : "  "
				+ ChangeUtils.ChangeNumberToSex(user.getSex()));
		tv_od_age.setText(TextUtils.isEmpty(user.getAge()) ? "暂无"
				: CaculationUtils.calculateDatePoor(user.getAge()) + "岁");
		tv_od_job.setText(TextUtils.isEmpty(user.getJob()) ? "暂无" : user
				.getJob());
		tv_od_pland.setText(TextUtils.isEmpty(user.getAddress()) ? "暂无" : user
				.getAddress());
		tv_od_hobbit.setText(TextUtils.isEmpty(user.getHobby()) ? "暂无" : user
				.getHobby());
		String nativeAvatar = user.getNativeAvatar();
		mHttpHelp.showImage(ci_od_icon,
				TextUtils.isEmpty(nativeAvatar) ? user.getAvatar()
						: nativeAvatar);
		mHttpHelp.showImage(TextUtils.isEmpty(nativeAvatar) ? user.getAvatar()
				: nativeAvatar, iv_od_blur);
	}

	/**
	 * 从网络获取
	 */
	protected void NetData() {
		mHttpHelp.sendGet(NetworkConfig.getUserInfor(mFrined_ID),
				OtherDataBean.class, new MyRequestCallBack<OtherDataBean>() {

					@Override
					public void onSucceed(OtherDataBean bean) {
						if (bean == null || bean.status == null
								|| bean.msg == null) {
							UIUtils.showToast(getApplicationContext(), "网络异常");
							return;
						}
						switch (bean.status) {
						case "1":
							if (bean.cont == null) {
								return;

							}
							// 网络判断好友方法
							// if(bean.cont.friends_i !=null &&
							// "0".equals(bean.cont.friends_i)){//"0"为非好友的关系
							// mFl_add_frined_btn.setVisibility(View.VISIBLE);
							// //显示添加好友布局
							// }
							if ("1".equals(bean.cont.detail.issmval)) {
								tv_od_autheicate.setText("已验证");
								tv_od_autheicate.setSelected(true);
							} else {
								tv_od_autheicate.setText("未验证");
								tv_od_autheicate.setSelected(false);
							}

							tv_od_nickname.setText(bean.cont.detail.nickname);
							tv_od_otherid.setText(bean.cont.detail.id);
							tv_od_sex.setText(ChangeUtils
									.ChangeNumberToSex(bean.cont.detail.sex));
							tv_od_age.setText(bean.cont.detail.age);
							tv_od_job.setText(bean.cont.detail.job);
							tv_od_pland.setText(bean.cont.detail.pland);
							tv_od_hobbit.setText(bean.cont.detail.hobby);
							mHttpHelp.showImage(ci_od_icon,
									bean.cont.detail.face);
							mHttpHelp.showImage(bean.cont.detail.face,
									iv_od_blur);
							hlv_od_photos.setAdapter(new AlbumAdapter(
									bean.cont.photolist));

							break;
						default:
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
							break;
						}

					}
				});
	}

	private ImageView iv_hlvmyspaceitem_icon;
	private FrameLayout mFl_add_frined_btn;

	class AlbumAdapter extends BaseAdapter {

		private List<Photolist> photolist;

		public AlbumAdapter(List<Photolist> photolist) {
			this.photolist = photolist;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return photolist.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			OtherDataViewHolder holder = new OtherDataViewHolder();
			if (convertView == null) {
				convertView = View.inflate(mActivity,
						R.layout.hlv_myspace_item, null);
				holder.iv_hlvmyspaceitem_icon = (ImageView) convertView
						.findViewById(R.id.iv_hlvmyspaceitem_icon);
				convertView.setTag(iv_hlvmyspaceitem_icon);
			} else {
				holder.iv_hlvmyspaceitem_icon = (ImageView) convertView
						.getTag();
			}

			mHttpHelp.showImage(holder.iv_hlvmyspaceitem_icon,
					photolist.get(position).imgurl);
			holder.iv_hlvmyspaceitem_icon
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							ArrayList<String> imgoris = new ArrayList<>();
							imgoris.clear();
							for (int i = 0; i < photolist.size(); i++) {
								imgoris.add(photolist.get(i).imgurlori);
							}
							Intent intent = new Intent(mActivity,
									ShowDetailsImages.class);
							intent.putStringArrayListExtra(
									GlobalConstant.SHOW_DETAILS_IMAGES, imgoris);
							intent.putExtra(GlobalConstant.SHOW_IMAGE_POSITION,
									position);

							startActivity(intent);
						}
					});

			return convertView;
		}

	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		if (mHttpHelp != null) {
			mHttpHelp.stopHttpNET();
		}
		super.onDestroy();
	}
	  @Override  
      public boolean onKeyDown(int keyCode, KeyEvent event) {  
          if ((keyCode == KeyEvent.KEYCODE_BACK)) {  
        	  setExitSwichLayout();
  			EventBus.getDefault().post(
  					new PlayVideo_event(PlayActivity.PLAY_TAG, null));
               return false;  
          }else {  
              return super.onKeyDown(keyCode, event);  
          }  
            
      }  
	@Override
	protected void setListener() {
		iv_od_backicon.setOnClickListener(this);
		mTv_other_data_add_friend.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_od_backicon:
			setExitSwichLayout();
			EventBus.getDefault().post(
					new PlayVideo_event(PlayActivity.PLAY_TAG, null));
			break;
		case R.id.tv_od_more:
			Intent intent2 = new Intent(this, FriendDataActivity.class);
			intent2.putExtra(GlobalConstant.FRIEND_ID, mFrined_ID);
			intent2.putExtra(GlobalConstant.FRIEND_NICKNAME, tv_od_nickname
					.getText().toString());
			intent2.putExtra(GlobalConstant.FRIEND_IS_SHIELD, mIsShield);
			startActivity(intent2);
			break;
		case R.id.rl_myspace_dt:
			if (!StringUtils.isEmpty(mFrined_ID)) {
				Intent intent = new Intent(mActivity, MyDynamicActivity.class);
				intent.putExtra(GlobalConstant.DYNAMIC_TAG, mFrined_ID);
				startActivity(intent);
			}
			break;
		case R.id.tv_other_data_add_friend:
			mTv_other_data_add_friend.setClickable(false);
			if (mFrined_ID != null) {
				if (isFriend) {
					if (user != null) {
						startActivity(new Intent(this, ChatActivity.class)
								.putExtra(GlobalConstant.FLAG_CHAT_HX_USER_ID,
										user.getUsername())
								.putExtra(GlobalConstant.FLAG_CHAT_USER_NIKE,
										user.getNick())
								.putExtra(GlobalConstant.FLAG_CHAT_TYPE,
										ChatActivity.CHATTYPE_SINGLE));
						OtherDataActivity.this.finish();
					}
				} else {
					CmdManager.getInstance().addContact(mFrined_ID, "",
							new CmdManagerCallBack() {
								@Override
								public void onState(boolean isSucceed) {

									if (isSucceed) {
										UIUtils.showToast(
												getApplicationContext(),
												"发送请求成功~~");

									} else {
										mTv_other_data_add_friend
												.setClickable(true);
										UIUtils.showToast(
												getApplicationContext(),
												"请求失败~~");
									}

								}
							});
				}
			}
			break;

		default:
			break;
		}
	}

	public void onEventMainThread(FriendData_bus bus) {
		switch (bus.getmTag()) {
		case FriendDataActivity.CLOSE_TAG: // 关闭activity
			setExitSwichLayout();
			break;
		case FriendDataActivity.UPDATE_TAG:
			tv_od_nickname.setText(bus.getNikeName());
			break;
		}
	}

	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}

}
