package com.aliamauri.meat.fragment.impl_supper;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.BlackListManager;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.activity.AttentionActivity;
import com.aliamauri.meat.activity.AuthenticateActivity;
import com.aliamauri.meat.activity.BlackListActivity;
import com.aliamauri.meat.activity.FriendActivity;
import com.aliamauri.meat.activity.MyAlbumActivity;
import com.aliamauri.meat.activity.MyDataActivity;
import com.aliamauri.meat.activity.MyDynamicActivity;
import com.aliamauri.meat.activity.MyMoviesActivity;
import com.aliamauri.meat.activity.SettingActivity;
import com.aliamauri.meat.activity.VipCenterActivity;
import com.aliamauri.meat.activity.search_activity.CollectDetailActivity;
import com.aliamauri.meat.activity.search_activity.PlayCacheAcivity;
import com.aliamauri.meat.activity.search_activity.RecordDetailActivity;
import com.aliamauri.meat.bean.UserInfoBean;
import com.aliamauri.meat.bean.UserReInfoBean;
import com.aliamauri.meat.eventBus.EventForNotice;
import com.aliamauri.meat.fragment.BaseFragment;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.ChangeUtils;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.HorizontalListView;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 首页——我的页面 第一级 mActivity 可直接获取Activity
 * 
 * @author limaokeji-windosc
 * 
 */
public class MyPage extends BaseFragment implements OnClickListener {
	private HttpHelp httpHelp;
	// private HttpHelp httpHelpRe;
	// private HttpHelp httpHelpInfo;
	// private HttpHelp httpHelpPic;

	private RelativeLayout rl_myspace_data;// 跳转到我的资料
	private TextView tv_myspace_data;// 跳转到我的资料
	private View MyPageView;
	private TextView tv_myspace_talk;
	private TextView tv_myspce_phone;
	private TextView tv_myspace_friend;
	private RelativeLayout rl_myspace_authenticate;
	private TextView tv_myspace_account;

	private HorizontalListView hlv_myspace_photos;
	private HorizontalListViewAdapter HLVAdapter;

	private RelativeLayout rl_myspace_mydynamic;// 跳转到我的动态
	private RelativeLayout rl_myspace_vipcenter;// 跳转到vipcenter
	private RelativeLayout rl_myspace_setting;// 跳转到设置页面
	private RelativeLayout rl_myspace_blacelist;// 跳转到黑名单
	private RelativeLayout rl_myspace_friend;// 跳转到好友列表
	private RelativeLayout rl_myspace_talk;// 跳转到话题

	private RelativeLayout rl_my_space_record;//
	private RelativeLayout rl_my_space_cache;
	private RelativeLayout rl_my_space_collect;

	private TextView tv_myspace_nickname;
	private CircleImageView ci_myspace_icon;

	private TextView tv_myspace_blackcount;

	private RelativeLayout rl_myspace_mydyvideo;
	private RelativeLayout rl_myspace_myattention;
	private RelativeLayout rl_myspace_attentionmine;

	private TextView tv_myspace_numberattentionmine;
	private TextView tv_myspace_numbermyattention;
	// private ImageView iv_myspace_jiantou1;
	// private TextView tv_myspace_xc;
	// private RelativeLayout rl_myspace_album;
	private ImageView iv_myspace_album;// 箭头跳转到个人相册

	@Override
	public void onResume() {
		initData();
		super.onResume();
		MobclickAgent.onPageStart("MyPage");

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MyPage");
	}

	@Override
	public void onStart() {
		// initData();
		// initHLVdata();
		super.onStart();
	}

	@Override
	public void initDate() {
		MyPageView = View.inflate(mActivity, R.layout.my_space, null);
		httpHelp = new HttpHelp();
		// httpHelpInfo = new HttpHelp();
		// obtain = Message.obtain();
		// if (PrefUtils.getBoolean(GlobalConstant.EDIT_USER_HEADICON, false)
		// || PrefUtils.getBoolean(GlobalConstant.EDIT_USER_INFO, false)) {
		// handler.sendEmptyMessage(1);
		// }
		// update();
		initView();
		setListener();
		setLVListener();
		netWorkReInfo();
		mFl_fragment_base_content.removeAllViews();
		mFl_fragment_base_content.addView(MyPageView);
	}

	private void getUserInfo() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		httpHelp.sendGet(NetworkConfig.getUserInfo(), UserInfoBean.class,
				new MyRequestCallBack<UserInfoBean>() {

					@Override
					public void onSucceed(UserInfoBean bean) {
						if (bean == null)
							return;
						if ("1".equals(bean.status)) {
							EventForNotice efn = new EventForNotice();
							efn.setText("1");
							EventBus.getDefault().post(efn);
							httpHelp.showImage(ci_myspace_icon, bean.cont.face);
							DLHeadIcon(bean.cont.face);
							saveUserInfo(bean);
						}

					}

				});
	}

	private void saveUserInfo(UserInfoBean bean) {
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_NICKNAME,
				bean.cont.nickname);
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_ID,
				bean.cont.id);
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
		DLHeadIcon(bean.cont.face);
	}

	public void netWorkReInfo() {
		httpHelp.sendGet(NetworkConfig.getUserRelInfo(), UserReInfoBean.class,
				new MyRequestCallBack<UserReInfoBean>() {

					@Override
					public void onSucceed(UserReInfoBean bean) {
						if (bean == null)
							return;
						if ("1".equals(bean.status)) {
							reInfoBean = bean;
							if (bean.cont != null) {
								initHLVdata(bean);

							}
						}
					}
				});
	}

	private void DLHeadIcon(String url) {
		httpHelp.downLoad(url, GlobalConstant.HEAD_ICON_PATH, null);
		PrefUtils.setString(GlobalConstant.USER_FACE,
				GlobalConstant.HEAD_ICON_PATH);
	}

	protected void initData() {
		if (CheckUtils.getInstance().isIcon(
				PrefUtils.getString(GlobalConstant.USER_FACE, ""))) {
			httpHelp.showImage(ci_myspace_icon, GlobalConstant.HEAD_ICON_PATH
					+ "##");
		} else {
			getUserInfo();
		}
		tv_myspce_phone.setText(getPhotoNum());
		tv_myspace_nickname.setText(PrefUtils.getString(
				GlobalConstant.USER_NICKNAME, ""));
		tv_myspace_account.setText("帐号: "
				+ PrefUtils.getString(GlobalConstant.USER_ID, ""));
		tv_myspace_friend.setText("好友("
				+ ContactManager.getInstance().getFriendContact().size() + ")");
		tv_myspace_talk.setText("话题(0)");
		tv_myspace_blackcount.setText(""
				+ BlackListManager.getInstance().getHaveHxIdBlackListMap()
						.size());
		tv_myspace_numberattentionmine.setText(""
				+ PrefUtils.getInt(GlobalConstant.USER_FANSCOUNT, 0));
		tv_myspace_numbermyattention.setText(""
				+ PrefUtils.getInt(GlobalConstant.USER_FOLLOWSCOUNT, 0));
		if ("0".equals(PrefUtils.getString(GlobalConstant.USER_DOYENTYPE, "0"))) {
			rl_myspace_attentionmine.setVisibility(View.GONE);
		} else {
			rl_myspace_attentionmine.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden) {
			tv_myspace_blackcount.setText(""
					+ BlackListManager.getInstance().getHaveHxIdBlackListMap()
							.size());

			netWorkReInfo();
		}
		super.onHiddenChanged(hidden);
	}

	private void setLVListener() {
		hlv_myspace_photos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (myAlbumList != null) {
					myAlbumIntent.putStringArrayListExtra("myPic",
							(ArrayList<String>) myAlbumList);
				}
				startActivity(myAlbumIntent);
			}
		});
	}

	private void setListener() {
		tv_myspace_data.setOnClickListener(this);
		rl_myspace_talk.setOnClickListener(this);
		rl_myspace_friend.setOnClickListener(this);
		rl_myspace_authenticate.setOnClickListener(this);
		rl_myspace_mydynamic.setOnClickListener(this);
		rl_myspace_vipcenter.setOnClickListener(this);
		rl_myspace_setting.setOnClickListener(this);
		rl_myspace_blacelist.setOnClickListener(this);
		// iv_myspace_jiantou1.setOnClickListener(this);
		rl_myspace_data.setOnClickListener(this);
		// tv_myspace_xc.setOnClickListener(this);
		// rl_myspace_album.setOnClickListener(this);
		iv_myspace_album.setOnClickListener(this);
		rl_my_space_record.setOnClickListener(this);
		rl_my_space_cache.setOnClickListener(this);
		rl_my_space_collect.setOnClickListener(this);
		rl_myspace_mydyvideo.setOnClickListener(this);
		rl_myspace_myattention.setOnClickListener(this);
		rl_myspace_attentionmine.setOnClickListener(this);
	}

	private void initView() {
		ci_myspace_icon = (CircleImageView) MyPageView
				.findViewById(R.id.ci_myspace_icon);
		if (CheckUtils.getInstance().isIcon(GlobalConstant.HEAD_ICON_PATH)) {
			httpHelp.showImage(ci_myspace_icon, GlobalConstant.HEAD_ICON_PATH
					+ "##");
		}
		tv_myspace_nickname = (TextView) MyPageView
				.findViewById(R.id.tv_myspace_nickname);
		tv_myspce_phone = (TextView) MyPageView
				.findViewById(R.id.tv_myspace_phone);

		tv_myspace_data = (TextView) MyPageView
				.findViewById(R.id.tv_myspace_data);
		tv_myspace_talk = (TextView) MyPageView
				.findViewById(R.id.tv_myspace_talk);
		tv_myspace_friend = (TextView) MyPageView
				.findViewById(R.id.tv_myspace_friend);
		rl_myspace_authenticate = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_authenticate);
		rl_myspace_mydynamic = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_mydynamic);
		rl_myspace_vipcenter = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_vipcenter);
		rl_myspace_setting = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_setting);
		rl_myspace_blacelist = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_blacelist);
		hlv_myspace_photos = (HorizontalListView) MyPageView
				.findViewById(R.id.hlv_myspace_photos);
		tv_myspace_blackcount = (TextView) MyPageView
				.findViewById(R.id.tv_myspace_blackcount);
		tv_myspace_account = (TextView) MyPageView
				.findViewById(R.id.tv_myspace_account);
		// iv_myspace_jiantou1 = (ImageView) MyPageView
		// .findViewById(R.id.iv_myspace_jiantou1);
		rl_myspace_data = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_data);
		rl_myspace_friend = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_friend);
		rl_myspace_talk = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_talk);
		// rl_myspace_album = (RelativeLayout) MyPageView
		// .findViewById(R.id.rl_myspace_album);
		// tv_myspace_xc = (TextView)
		// MyPageView.findViewById(R.id.tv_myspace_xc);
		myAlbumIntent = new Intent(mActivity, MyAlbumActivity.class);
		iv_myspace_album = (ImageView) MyPageView
				.findViewById(R.id.iv_myspace_album);

		rl_my_space_record = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_my_space_record);
		rl_my_space_cache = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_my_space_cache);
		rl_my_space_collect = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_my_space_collect);
		rl_myspace_mydyvideo = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_mydyvideo);
		rl_myspace_myattention = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_myattention);
		rl_myspace_attentionmine = (RelativeLayout) MyPageView
				.findViewById(R.id.rl_myspace_attentionmine);
		tv_myspace_numberattentionmine = (TextView) MyPageView
				.findViewById(R.id.tv_myspace_numberattentionmine);
		tv_myspace_numbermyattention = (TextView) MyPageView
				.findViewById(R.id.tv_myspace_numbermyattention);

		rl_my_space_cache.setVisibility(View.GONE);
	}

	private CharSequence getPhotoNum() {
		String str = PrefUtils.getString(GlobalConstant.USER_TEL, "");
		if (StringUtils.isEmpty(str) || "0".equals(str)) {
			return "未绑定手机";
		} else {
			return "已绑定手机:" + str;
		}
	}

	private Intent myAlbumIntent;
	private List<String> myAlbumList;
	private List<String> netList;

	private void initHLVdata(UserReInfoBean bean) {
		myAlbumList = new ArrayList<String>();
		netList = new ArrayList<String>();
		// DynamicShowDao.getInstance().getUploadPic(
		// PrefUtils.getString(GlobalConstant.USER_ID, ""), myAlbumList,
		// netList);`
		if (reInfoBean.cont.photolist != null) {
			for (int i = 0; i < reInfoBean.cont.photolist.size(); i++) {
				myAlbumList.add(reInfoBean.cont.photolist.get(i).imgurl);
			}
		}
		if (HLVAdapter == null) {
			HLVAdapter = new HorizontalListViewAdapter();
			hlv_myspace_photos.setAdapter(HLVAdapter);
		} else {
			HLVAdapter.notifyDataSetChanged();
		}
	}

	private ImageView iv_hlvmyspaceitem_icon;
	private UserReInfoBean reInfoBean;

	class HorizontalListViewAdapter extends BaseAdapter {

		public HorizontalListViewAdapter() {
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myAlbumList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mActivity,
						R.layout.hlv_myspace_item, null);
				iv_hlvmyspaceitem_icon = (ImageView) convertView
						.findViewById(R.id.iv_hlvmyspaceitem_icon);
				convertView.setTag(iv_hlvmyspaceitem_icon);
			} else {
				iv_hlvmyspaceitem_icon = (ImageView) convertView.getTag();
			}
			httpHelp.showImage(iv_hlvmyspaceitem_icon,
					myAlbumList.get(position));
			return convertView;
		}

	}

	@Override
	public int getchildFragmentTag() {
		return GlobalConstant.MY_PAGE;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("----MyPage----->", "--------走啦-----------");
		super.onActivityResult(requestCode, resultCode, data);
	}

	// private Context content;
	// private Receiver receiver;
	//
	// private void update() {
	// if (content == null) {
	// content = UIUtils.getContext();
	// }
	// IntentFilter filter = new IntentFilter();
	// filter.addAction("updateUserInfo");
	// filter.addAction("MyAlbumUpdata");
	// receiver = new Receiver();
	// content.registerReceiver(receiver, filter);
	// }
	//
	// class Receiver extends BroadcastReceiver {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// // // 看本地有没有存有头像，
	// initData();
	// netWorkReInfo();
	// }
	// }

	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.rl_myspace_data:
		case R.id.tv_myspace_data:
			startActivity(new Intent(mActivity, MyDataActivity.class));
			// startActivity(new Intent(mActivity, OtherDataActivity.class));
			break;
		case R.id.rl_myspace_talk:
			UIUtils.showToast(UIUtils.getContext(), "敬请期待");
			// startActivity(new Intent(mActivity, TalkListActivity.class));
			break;
		case R.id.rl_myspace_friend:
			startActivity(new Intent(mActivity, FriendActivity.class));
			break;
		case R.id.rl_myspace_authenticate:
			startActivity(new Intent(mActivity, AuthenticateActivity.class));
			break;
		case R.id.rl_myspace_mydynamic: // 根据本机用户进入动态页面
			String uid = PrefUtils.getString(UIUtils.getContext(),
					GlobalConstant.USER_ID, "");
			if (!StringUtils.isEmpty(uid)) {
				Intent intent = new Intent(mActivity, MyDynamicActivity.class);
				intent.putExtra(GlobalConstant.DYNAMIC_TAG, uid);
				startActivity(intent);
			}
			break;
		case R.id.rl_myspace_vipcenter:
			startActivity(new Intent(mActivity, VipCenterActivity.class));
			break;
		case R.id.rl_myspace_setting:
			startActivity(new Intent(mActivity, SettingActivity.class));
			break;
		case R.id.rl_myspace_blacelist:
			startActivity(new Intent(mActivity, BlackListActivity.class));
			break;
		case R.id.iv_myspace_album:
			// case R.id.rl_myspace_album:
			// case R.id.tv_myspace_xc:
			// case R.id.iv_myspace_jiantou1:
			if (myAlbumList != null) {
				myAlbumIntent.putStringArrayListExtra("myPic",
						(ArrayList<String>) myAlbumList);
			}
			startActivity(myAlbumIntent);
			break;
		case R.id.rl_myspace_mydyvideo:
			i = new Intent(MyApplication.getMainActivity(),
					MyMoviesActivity.class);
			// Intent i = new Intent(MyApplication.getMainActivity(),
			// DoyenDetailActvity.class);
			i.putExtra(GlobalConstant.INTENT_ID,
					PrefUtils.getString(GlobalConstant.USER_ID, ""));
			MyApplication.getMainActivity().startActivity(i);
			break;
		case R.id.rl_my_space_record:
			MyApplication.getMainActivity().startActivity(
					new Intent(MyApplication.getMainActivity(),
							RecordDetailActivity.class));
			break;
		case R.id.rl_my_space_cache:
			MyApplication.getMainActivity().startActivity(
					new Intent(MyApplication.getMainActivity(),
							PlayCacheAcivity.class));
			break;
		case R.id.rl_my_space_collect:
			MyApplication.getMainActivity().startActivity(
					new Intent(MyApplication.getMainActivity(),
							CollectDetailActivity.class));
			break;
		case R.id.rl_myspace_myattention:
			i = new Intent(MyApplication.getMainActivity(),
					AttentionActivity.class);
			i.putExtra(GlobalConstant.INTENT_DATA, 0);
			i.putExtra(GlobalConstant.INTENT_ID,
					PrefUtils.getString(GlobalConstant.USER_ID, ""));
			MyApplication.getMainActivity().startActivity(i);
			break;
		case R.id.rl_myspace_attentionmine:
			i = new Intent(MyApplication.getMainActivity(),
					AttentionActivity.class);
			i.putExtra(GlobalConstant.INTENT_DATA, 1);
			i.putExtra(GlobalConstant.INTENT_ID,
					PrefUtils.getString(GlobalConstant.USER_ID, ""));
			MyApplication.getMainActivity().startActivity(i);
			break;
		default:
			break;
		}

	}
}
