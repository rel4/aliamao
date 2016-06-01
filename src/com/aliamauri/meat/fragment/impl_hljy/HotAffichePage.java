package com.aliamauri.meat.fragment.impl_hljy;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.OtherDataActivity;
import com.aliamauri.meat.activity.find_activity.GgxqActivity;
import com.aliamauri.meat.bean.hljy.RmggBean;
import com.aliamauri.meat.bean.hljy.RmggBean.Cont.Gonggao;
import com.aliamauri.meat.bean.hljy.Rmgg_childBean;
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
 * 发现 ---- 婚恋交友----热门公告
 * 
 * @author limaokeji-windosc
 * 
 */
public class HotAffichePage extends BaseFragment_hljy implements
		OnItemClickListener, OnRefreshListener, OnClickListener {

	private RefreshListView mLv_hljy_hot_afftich_content; // 展示内容的lv
	private MyBaseAdapter_lv mAdapter;
	private HttpHelp mHttpHelp;
	private int width;
	private int height;
	private String mUser_Id;
	private FrameLayout mFl_loading;
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("HotAffichePage");
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		 MobclickAgent.onPageEnd("HotAffichePage"); 
	}

	@Override
	public View initChildView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mHttpHelp = new HttpHelp();
		mUser_Id = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_ID, "");
		View view = inflater.inflate(R.layout.hljy_xiehou_ta, container, false);
		mLv_hljy_hot_afftich_content = $(view, R.id.lv_xie_hou_ta_content);
		mFl_loading = $(view, R.id.fl_loading);

		return view;
	}

	@Override
	public void initChildDate() {
		WindowManager systemService = (WindowManager) mActivity
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		systemService.getDefaultDisplay().getMetrics(metrics);
		width = metrics.widthPixels;
		height = metrics.heightPixels;

		mLoadPage_rmgg = 2;
		initNetwork();
	}

	private ArrayList<Gonggao> mGongGaos; // 展示发布公告的集合
	private String mLoadUrl_rmgg; // 公告二次加载url

	/**
	 * 设置网络请求数据
	 * 
	 * @param b
	 */
	private void initNetwork() {

		mHttpHelp.sendGet(NetworkConfig.getHLJY_url(), RmggBean.class,
				new MyRequestCallBack<RmggBean>() {

					@Override
					public void onSucceed(RmggBean bean) {
						if (mFl_loading.getVisibility() != View.GONE) {
							mFl_loading.setVisibility(View.GONE);
						}
						if (bean == null || bean.status == null
								|| bean.url == null
								|| bean.cont.gonggao == null
								|| bean.cont.xingqu == null) {
							UIUtils.showToast(mActivity, "没有找到数据~~");
							return;
						}

						switch (bean.status) {
						case "1":
							mGongGaos = bean.cont.gonggao;
							mLoadUrl_rmgg = bean.url.gonggao;
							mAdapter = new MyBaseAdapter_lv();
							mLv_hljy_hot_afftich_content.setAdapter(mAdapter);
							mLv_hljy_hot_afftich_content
									.setOnItemClickListener(HotAffichePage.this);
							mLv_hljy_hot_afftich_content
									.setOnRefreshListener(HotAffichePage.this);

							break;
						case "2":
							UIUtils.showToast(mActivity, "你还没有登陆~~~");
							break;

						default:
							break;
						}

					}
				});

	}

	private int mCurrentPosition; // 当前点击的位置

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mCurrentPosition = position;
		Intent intent = new Intent(mActivity, GgxqActivity.class);
		intent.putExtra(GlobalConstant.GO_GGXQ_TAG, GlobalConstant.RMGG_TAG);
		intent.putExtra(GlobalConstant.GO_GGXQ_ID, mGongGaos.get(position).id);
		startActivityForResult(intent, 20);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			int hy_num = data.getIntExtra(GlobalConstant.DATA_TAG, -1);
			if (hy_num != -1
					&& mAdapter != null
					&& hy_num != Integer.valueOf(mGongGaos
							.get(mCurrentPosition).hy_num)) {
				mGongGaos.get(mCurrentPosition).hy_num = String.valueOf(hy_num);
				mAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public int mLoadPage_rmgg = 2;

	@Override
	public void onLoadMore() {
		mHttpHelp.sendGet(
				mLoadUrl_rmgg + "?ucode=" + NetworkConfig.getUcode()
						+ NetworkConfig.FMT + "&page="
						+ String.valueOf(mLoadPage_rmgg), Rmgg_childBean.class,
				new MyRequestCallBack<Rmgg_childBean>() {

					@Override
					public void onSucceed(Rmgg_childBean bean) {

						if (bean == null || bean.status == null
								|| bean.cont == null) {
							UIUtils.showToast(mActivity, "没有找到数据~~");
							mLv_hljy_hot_afftich_content.onRefreashFinish();
							return;
						}

						switch (bean.status) {
						case "1":
							if (bean.cont.size() <= 0) {
								UIUtils.showToast(mActivity, "没有更多数据了~~");
								mLv_hljy_hot_afftich_content.onRefreashFinish();
								return;
							}
							mGongGaos.addAll(bean.cont);
							mAdapter.notifyDataSetChanged();
							mLv_hljy_hot_afftich_content.onRefreashFinish();
							mLoadPage_rmgg++;
							break;
						case "2":
							UIUtils.showToast(mActivity, "你还没有登陆~~~");
							break;

						default:

							UIUtils.showToast(mActivity, "没有找到数据~~");
							break;
						}

					}
				});
	}

	class MyBaseAdapter_lv extends BaseAdapter {

		@Override
		public int getCount() {
			return mGongGaos.size();
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
				convertView = View.inflate(mActivity,
						R.layout.item_hot_affiche, null);
				holder.tv_hljy_hot_affiche_username = $(convertView,
						R.id.tv_hljy_hot_affiche_username);
				holder.tv_hljy_hot_affiche_time = $(convertView,
						R.id.tv_hljy_hot_affiche_time);
				holder.tv_hljy_hot_affiche_echo = $(convertView,
						R.id.tv_hljy_hot_affiche_echo);
				holder.tv_hljy_hot_affiche_content = $(convertView,
						R.id.tv_hljy_hot_affiche_content);
				holder.civ_hljy_hot_affiche_icon = $(convertView,
						R.id.civ_hljy_hot_affiche_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder_rmgg) convertView.getTag();
			}

			mHttpHelp.showImage(holder.civ_hljy_hot_affiche_icon,
					mGongGaos.get(position).face + "##");
			holder.civ_hljy_hot_affiche_icon
					.setTag(mGongGaos.get(position).uid);
			holder.civ_hljy_hot_affiche_icon
					.setOnClickListener(HotAffichePage.this);
			holder.tv_hljy_hot_affiche_content
					.setText(mGongGaos.get(position).name);
			holder.tv_hljy_hot_affiche_time
					.setText(mGongGaos.get(position).time);
			holder.tv_hljy_hot_affiche_username
					.setText(mGongGaos.get(position).nickname);
			holder.tv_hljy_hot_affiche_echo.setText("回应("
					+ mGongGaos.get(position).hy_num + ")");

			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.civ_hljy_hot_affiche_icon:
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

		public CircleImageView civ_hljy_hot_affiche_icon;
		public TextView tv_hljy_hot_affiche_content;
		public TextView tv_hljy_hot_affiche_echo;
		public TextView tv_hljy_hot_affiche_time;
		public TextView tv_hljy_hot_affiche_username;

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
