package com.aliamauri.meat.activity.nearby_activity;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.bean.ResourceLibraryBean;
import com.aliamauri.meat.bean.ResourceLibraryBean.Cont;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.holder.ViewHolder_YS;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.ClickZanBtn_utils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.RefreshListView;
import com.aliamauri.meat.view.RefreshListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

public class ResourceSearch_page extends BaseActivity implements
		OnItemClickListener, OnRefreshListener {

	private RefreshListView mLv_rs_content;
	private MyBaseAdapter adapter;
	private String mTagName; // 资源页面传过来的标记
	private HttpHelp mHttpHelp;
	private int mCurrentPage; // 当前页数
	private FrameLayout mLoading; 

	@Override
	protected View getRootView() {
		return View.inflate(mActivity, R.layout.activity_resource_search, null);
	}

	@Override
	protected void initView() {
		mCurrentPage = 1;
		mHttpHelp = new HttpHelp();
		mTagName = baseIntent.getStringExtra(GlobalConstant.RESOURCESEARCH_TAG);
		mLv_rs_content = $(R.id.lv_rs_content);
		mLoading = $( R.id.fl_loading);
	}

	@Override
	protected void setListener() {

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
	

	private ArrayList<Cont> mCont;   //获取服务器返回的结果

	@Override
	protected void initNet() {
		if (mHttpHelp != null) {
			mHttpHelp.stopHttpNET();
		}
		mHttpHelp.sendGet(
				NetworkConfig.getResorceSearchUrl(mTagName, mCurrentPage),
				ResourceLibraryBean.class,
				new MyRequestCallBack<ResourceLibraryBean>() {

					@Override
					public void onSucceed(ResourceLibraryBean bean) {
						mLoading.setVisibility(View.GONE);
						if (bean == null || bean.cont == null
								|| bean.status == null) {
							UIUtils.showToast(mActivity,"网络异常");
							return;
						}
						switch (bean.status) {
						case "1":
							if (bean.cont.size() <= 0) {
								UIUtils.showToast(
										mActivity,
										mActivity.getResources().getString(
												R.string.text_no_data));
								mLv_rs_content.onRefreashFinish();
								return;
							}

							if (mCurrentPage <= 1) {
								mCont = bean.cont;
								adapter = new MyBaseAdapter();
								mLv_rs_content.setAdapter(adapter);
								mLv_rs_content.setOnItemClickListener(ResourceSearch_page.this);
								mLv_rs_content.setOnRefreshListener(ResourceSearch_page.this);

							} else {
								mCont.addAll(bean.cont);
								adapter.notifyDataSetChanged();
								mLv_rs_content.onRefreashFinish();
							}
							mCurrentPage++;
							break;

						default:
							UIUtils.showToast(ResourceSearch_page.this,
									bean.msg);
							break;
						}
					}
				});

	}

	@Override
	protected int setActivityAnimaMode() {

		return 4;
	}

	@Override
	protected String getCurrentTitle() {
		return "影视";
	}

	class MyBaseAdapter extends BaseAdapter {

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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder_YS holder_ys = null;

			if (convertView == null) {

				holder_ys = new ViewHolder_YS();
				convertView = View.inflate(mActivity, R.layout.my_video_item,null);
				holder_ys.ivc_voide_item_icon = $(convertView,R.id.ivc_voide_item_icon);
				holder_ys.tv_voide_item_time = $(convertView,R.id.tv_voide_item_time);
				holder_ys.tv_voide_item_video_title = $(convertView,R.id.tv_voide_item_video_title);
				holder_ys.tv_video_url = $(convertView, R.id.tv_video_url);
				holder_ys.tv_voide_item_ding = $(convertView,R.id.tv_voide_item_ding);
				holder_ys.tv_voide_item_cai = $(convertView,R.id.tv_voide_item_cai);
				holder_ys.iv_voide_item_ding = $(convertView,R.id.iv_voide_item_ding);
				holder_ys.iv_voide_item_cai = $(convertView,R.id.iv_voide_item_cai);
				holder_ys.tv_voide_item_username = $(convertView,R.id.tv_voide_item_username);
				holder_ys.ll_ding = $(convertView,R.id.ll_ding);
				holder_ys.ll_cai = $(convertView,R.id.ll_cai);
				
				convertView.setTag(holder_ys);

			} else {

				holder_ys = (ViewHolder_YS) convertView.getTag();
			}

			setItem_content(position, holder_ys, mCont);
			return convertView;
		}
	}


	/**
	 * 设置动态每个条目的点击事件
	 * 
	 * @param position
	 * @param tlist
	 * @param cont
	 * @param ys
	 * 
	 */
	public void setItem_content(int position, ViewHolder_YS ys,ArrayList<Cont> conts) {
		ys.tv_video_url.setText(conts.get(position).content);
		ys.tv_voide_item_cai.setText(conts.get(position).down);
		ys.tv_voide_item_ding.setText(conts.get(position).up);
		ys.tv_voide_item_time.setText(conts.get(position).createtime);
		ys.tv_voide_item_video_title.setText(conts.get(position).name);
		ys.tv_voide_item_username.setText(conts.get(position).nickname);
		mHttpHelp.showImage(ys.ivc_voide_item_icon, conts.get(position).face);
		
		ClickZanBtn_utils btn_utils = new ClickZanBtn_utils(ys, conts, position);
		ys.ll_cai.setTag("2");//踩标记
		ys.ll_cai.setOnClickListener(btn_utils);
		ys.ll_ding.setTag("1");//顶标记
		ys.ll_ding.setOnClickListener(btn_utils);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		initNet();
	}

}
