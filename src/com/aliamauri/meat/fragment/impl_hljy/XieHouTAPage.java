package com.aliamauri.meat.fragment.impl_hljy;

import java.util.ArrayList;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.activity.OtherDataActivity;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.bean.hljy.XH_taBean;
import com.aliamauri.meat.bean.hljy.XH_taBean.Cont;
import com.aliamauri.meat.fragment.BaseFragment_hljy;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.RefreshListView;
import com.aliamauri.meat.view.RefreshListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 发现 ---- 婚恋交友----邂逅Ta
 * 
 * @author limaokeji-windosc
 * 
 */
public class XieHouTAPage extends BaseFragment_hljy implements
		OnItemClickListener, OnRefreshListener, OnClickListener {

	private MyBaseAdapter_lv mAdapter;
	private HttpHelp mHttpHelp;
	private RefreshListView mLv_xie_hou_ta_content; // 展示内容的lv
	private int mLoadPage_xh; // 获取分页加载的页数
	private String mUser_Id;
	private ArrayList<String> mUIds;//该用户的id集合
	private FrameLayout mFl_loading;
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("XieHouTAPage");
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		 MobclickAgent.onPageEnd("XieHouTAPage"); 
	}

	@Override
	public View initChildView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		mHttpHelp = new HttpHelp();
		mUIds = new ArrayList<>();
		mUIds.clear();
		getUIds();
		mUser_Id = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_ID, "");
		
		 View view = inflater.inflate(R.layout.hljy_xiehou_ta, container, false);
		mLv_xie_hou_ta_content = $(view,R.id.lv_xie_hou_ta_content);
		mFl_loading = $(view,R.id.fl_loading);
		return view;
	}
	/**
	 *获取用户id集合
	 */
	private void getUIds() {
		Map<String, User> allUserMap = ContactManager.getInstance().getAllContactUidMap();
		for (Map.Entry<String, User> entry : allUserMap.entrySet()) {
			String key = entry.getKey();
			if(key !=null ){
				mUIds.add(key);
			}
	    }
	}

	@Override
	public void initChildDate() {
		mLoadPage_xh = 1;
		initNetwork(true);
	}

	

	private ArrayList<Cont> mCont;// 邂逅ta好友集合
	

	/**
	 * 设置网络请求数据
	 * 
	 * @param b
	 *            true 第一次请求网络，false 第二次请求网络
	 */

	private void initNetwork(final boolean b) {
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION, "0&&0").split("&&");
		mHttpHelp.sendGet(NetworkConfig.getXh_url(mLoadPage_xh, String.valueOf(location[location.length-1]), String.valueOf(location[0])),
				XH_taBean.class, new MyRequestCallBack<XH_taBean>() {

					@Override
					public void onSucceed(XH_taBean bean) {
						if(mFl_loading.getVisibility() != View.GONE){
							mFl_loading.setVisibility(View.GONE);
						}
						if (bean == null || bean.status == null
								|| bean.cont == null) {
							UIUtils.showToast(mActivity, "网络异常");
							return;
						}
						switch (bean.status) {
						case "1":
							if (b) {
								if (bean.cont.size() <= 0) {
									UIUtils.showToast(mActivity, "没有更多数据了~~");
									return;
								}
								mCont = bean.cont;
							
								mAdapter = new MyBaseAdapter_lv();
								mLv_xie_hou_ta_content.setAdapter(mAdapter);
								mLv_xie_hou_ta_content
										.setOnItemClickListener(XieHouTAPage.this);
								mLv_xie_hou_ta_content
										.setOnRefreshListener(XieHouTAPage.this);

								mLoadPage_xh++;

							} else {
								if (bean.cont.size() <= 0) {
									UIUtils.showToast(mActivity, "没有更多数据了~~");
									mLv_xie_hou_ta_content.onRefreashFinish();
									return;
								}
								mCont.addAll(bean.cont);
								Map<String, User> allUserMap = ContactManager
										.getInstance().getAllContactUidMap();
								for (Cont cont : mCont) {
									if (allUserMap.containsKey(cont.id)) {
										mCont.remove(cont);
									}
								}
								mAdapter.notifyDataSetChanged();
								mLv_xie_hou_ta_content.onRefreashFinish();
								mLoadPage_xh++;
							}

							break;

						default:
							UIUtils.showToast(mActivity, bean.msg);
							break;
						}

					}
				});

	}

	/**
	 * 加载更多操作
	 */
	@Override
	public void onLoadMore() {
		initNetwork(false);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	class MyBaseAdapter_lv extends BaseAdapter {

		@Override
		public int getCount() {
			return mCont.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder_rmgg holder = null;
			if (convertView == null) {
				holder = new ViewHolder_rmgg();
				convertView = View.inflate(mActivity, R.layout.item_xiehou_ta,
						null);
				holder.tv_hljy_yue_ta_sex = $(convertView,
						R.id.tv_hljy_yue_ta_sex);
				holder.tv_hljy_yue_ta_distance = $(convertView,
						R.id.tv_hljy_yue_ta_distance);
				holder.tv_hljy_yue_ta_username = $(convertView,
						R.id.tv_hljy_yue_ta_username);
				holder.tv_hljy_yue_ta_authenticate = $(convertView,
						R.id.tv_hljy_yue_ta_authenticate);
				holder.civ_hljy_yue_ta_icon = $(convertView,
						R.id.civ_hljy_yue_ta_icon);
				holder.tv_hljy_yue_ta_introduction = $(convertView,
						R.id.tv_hljy_yue_ta_introduction);
				holder.iv_btn_yue_ta = $(convertView, R.id.tv_btn_yue_ta);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder_rmgg) convertView.getTag();
			}

			mHttpHelp.showImage(holder.civ_hljy_yue_ta_icon,
					mCont.get(position).face + "##");
			holder.civ_hljy_yue_ta_icon.setTag(mCont.get(position).id);
			holder.civ_hljy_yue_ta_icon.setOnClickListener(XieHouTAPage.this);

			setUserSex(holder.tv_hljy_yue_ta_sex, position);
			isSmval(holder.tv_hljy_yue_ta_authenticate, position);
			holder.tv_hljy_yue_ta_distance
					.setText(mCont.get(position).distance);
			holder.tv_hljy_yue_ta_username
					.setText(mCont.get(position).nickname);
			holder.tv_hljy_yue_ta_introduction
					.setText(mCont.get(position).signature);
			holder.iv_btn_yue_ta.setTag(mCont.get(position).id);
			holder.iv_btn_yue_ta.setOnClickListener(XieHouTAPage.this);

			return convertView;
		}

		/**
		 * 判断当前用户是否验证
		 * 
		 * @param tv_hljy_yue_ta_sex
		 * @param position
		 */
		private void isSmval(TextView text, int position) {

			switch (mCont.get(position).issmval) {
			case "0": // 未验证
				text.setText("未验证");
				text.setSelected(false);
				break;
			case "1": // 验证
				text.setText("已验证");
				text.setSelected(true);

				break;

			default:
				text.setText("未验证");
				text.setSelected(false);
				break;
			}

		}

		/**
		 * 设置用户的性别
		 * 
		 * @param sex
		 * @param position
		 */
		private void setUserSex(TextView sex, int position) {
			switch (mCont.get(position).sex) {
			case "0": // 女
				sex.setText("女");
				sex.setSelected(true);
				break;
			case "1": // 男
				sex.setText("男");
				sex.setSelected(false);

				break;

			default:
				sex.setText("女");
				sex.setSelected(true);
				break;
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_btn_yue_ta:
			if(mUIds.contains((String)v.getTag())){
				UIUtils.showToast(mActivity,"已是好友~");
				break;
			}
			CmdManager.getInstance().sendAppointmentInvit(v.getTag().toString(), "", new CmdManagerCallBack() {
						@Override
						public void onState(boolean isSucceed) {
							UIUtils.showToast(mActivity,
									isSucceed == true ? "约会邀请已发出~~": "约会邀请发送失败~~");

						}
					});
			break;
		case R.id.civ_hljy_yue_ta_icon:
			getFrinedPage(v);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * 点击好友头像进入好友界面
	 * 
	 * @param v
	 * @param position
	 */
	public void getFrinedPage(View v) {
		String tag = (String) v.getTag();
		if (!(mUser_Id.equals(tag))) { // 当前是朋友发布的动态
			Intent intent = new Intent(mActivity, OtherDataActivity.class);
			intent.putExtra(GlobalConstant.COMMENT_ADD_FRIEND, tag);
			startActivity(intent);
		}
	}

	class ViewHolder_rmgg {

		public CircleImageView civ_hljy_yue_ta_icon;
		public TextView tv_hljy_yue_ta_authenticate;
		public TextView tv_hljy_yue_ta_distance;
		public TextView tv_hljy_yue_ta_sex;
		public TextView tv_hljy_yue_ta_username;
		public TextView tv_hljy_yue_ta_introduction;
		public TextView iv_btn_yue_ta;

	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	public <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

}
