package com.aliamauri.meat.fragment.impl_supper;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.APPDetailActivity;
import com.aliamauri.meat.activity.search_activity.SearchResultPageActivity;
import com.aliamauri.meat.adapter.MyGridAndListViewBaseAdapter;
import com.aliamauri.meat.bean.SearchBaen;
import com.aliamauri.meat.bean.TV;
import com.aliamauri.meat.bean.TVbean;
import com.aliamauri.meat.fragment.BaseFragment;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.AnimUtil;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.FlowLayout;
import com.aliamauri.meat.view.RoundedImageView;
import com.umeng.analytics.MobclickAgent;

/**
 * 首页——搜索 第一级 mActivity 可直接获取Activity
 * 
 * @author limaokeji-windosc
 * 
 */
public class SearchPage extends BaseFragment implements OnClickListener,
		OnItemClickListener {
	public static final int FLAG_HOTKEY = 1;
	public static final int FLAG_APP = 2;
	private TextView tv_home_hot_1;
	private TextView tv_home_hot_2;
	private TextView tv_home_hot_3;
	private TextView tv_home_hot_4;
	private TextView tv_home_hot_5;
	private TextView tv_home_hot_6;
	private View show_del_layout;
	private FlowLayout fl_erach_cache;
	private View iv_app_refrsuh;
	private GridView gv_search_app;
	private View iv_home_1;
	private AnimUtil animUtil;
	private MyGridAndListViewBaseAdapter mAdapter;
	private SearchBaen mBean;// 搜索数据
	private HttpHelp mHelp;

	@Override
	public void onResume() {
		super.onResume();
		setSearchData();
		MobclickAgent.onPageStart("SearchPage");

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SearchPage");
	}

	//
	@Override
	public void initDate() {
		View view = View.inflate(mActivity, R.layout.fragment_search, null);
		init(view);
		initNet();

		mFl_fragment_base_content.removeAllViews();
		mFl_fragment_base_content.addView(view);
	}

	/**
	 * 初始化控件
	 */
	private void init(View view) {
		mBtn_title_search_confirm.setOnClickListener(this);
		// btn_search_pager_cancel = (Button)
		// findViewById(R.id.btn_search_pager_cancel);

		gv_search_app = $(view, R.id.gv_search_app);
		tv_del_all = $(view, R.id.tv_del_all);
		tv_home_hot_1 = $(view, R.id.tv_home_hot_1);
		tv_home_hot_2 = $(view, R.id.tv_home_hot_2);
		tv_home_hot_3 = $(view, R.id.tv_home_hot_3);
		tv_home_hot_4 = $(view, R.id.tv_home_hot_4);
		tv_home_hot_5 = $(view, R.id.tv_home_hot_5);
		tv_home_hot_6 = $(view, R.id.tv_home_hot_6);
		show_del_layout = $(view, R.id.show_del_layout);
		iv_home_1 = $(view, R.id.iv_home_1);
		fl_erach_cache = $(view, R.id.fl_erach_cache);
		iv_app_refrsuh = $(view, R.id.iv_app_refrsuh);
		// gv_search_app = (GridView) findViewById(R.id.gv_search_app);
		// tv_del_all = (TextView) findViewById(R.id.tv_del_all);
		// tv_home_hot_1 = (TextView) findViewById(R.id.tv_home_hot_1);
		// tv_home_hot_2 = (TextView) findViewById(R.id.tv_home_hot_2);
		// tv_home_hot_3 = (TextView) findViewById(R.id.tv_home_hot_3);
		// tv_home_hot_4 = (TextView) findViewById(R.id.tv_home_hot_4);
		// tv_home_hot_5 = (TextView) findViewById(R.id.tv_home_hot_5);
		// tv_home_hot_6 = (TextView) findViewById(R.id.tv_home_hot_6);
		// show_del_layout = findViewById(R.id.show_del_layout);
		// iv_home_1 = findViewById(R.id.iv_home_1);
		// fl_erach_cache = (FlowLayout) findViewById(R.id.fl_erach_cache);
		// iv_app_refrsuh = findViewById(R.id.iv_app_refrsuh);
		tv_del_all.setOnClickListener(this);
		setAnim(iv_home_1, FLAG_HOTKEY, null);
	}

	/**
	 * 请求网络
	 */
	private void initNet() {
		mHelp = new HttpHelp();
		mHelp.sendGet(NetworkConfig.getSearchAll2(), SearchBaen.class,
				new MyRequestCallBack<SearchBaen>() {

					@Override
					public void onSucceed(SearchBaen bean) {
						if (bean == null) {
							return;
						}
						mBean = bean;
						setAdapter();
						initHotkeys();
					}
				});
	}

	@Override
	public int getchildFragmentTag() {
		return GlobalConstant.SEARCH_PAGE;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_search_confirm:
			String string = mEt_title_search_edit.getText().toString();
			if (!StringUtils.isEmpty(string)) {
				Intent intent = new Intent(mActivity,
						SearchResultPageActivity.class);
				intent.putExtra(GlobalConstant.APP_SEARCH_KEY, string);
				startActivity(intent);
			} else {
				UIUtils.showToast(mActivity.getApplicationContext(), "请输入内容");
			}
			break;
		case R.id.tv_del_all:// 清除记录
			PrefUtils.setString(UIUtils.getContext(),
					GlobalConstant.SERACH_CACHE_KEY, "");
			setSearchData();
			break;
		default:
			break;
		}
	}

	private void setAdapter() {

		if (mAdapter == null) {

			mAdapter = new MyGridAndListViewBaseAdapter(mBean.cont.faxian,
					false) {

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					ViewHolder holder;
					if (convertView == null) {
						holder = new ViewHolder();
						convertView = View.inflate(mActivity,
								R.layout.list_item_search, null);
						holder.ivHome = (RoundedImageView) convertView
								.findViewById(R.id.iv_home);
						holder.tvHome = (TextView) convertView
								.findViewById(R.id.tv_home);
						convertView.setTag(holder);
					} else {
						holder = (ViewHolder) convertView.getTag();
					}
					TV tv = mBean.cont.faxian.get(position);
					holder.tvHome.setText(tv.name);
					mHelp.showImage(holder.ivHome, tv.pic);
					return convertView;
				}
			};
			setAnim(iv_app_refrsuh, FLAG_APP, mAdapter);
			gv_search_app.setAdapter(mAdapter);
			gv_search_app.setOnItemClickListener(this);
			// 去除gv点击的背景色
			gv_search_app.setSelector(new ColorDrawable(Color.TRANSPARENT));
		} else {
			mAdapter.notifyDataSetInvalidated();
		}
	}

	/**
	 * 设置动画
	 * 
	 * @param view
	 * @param flag
	 * @param adapter
	 */
	private void setAnim(final View view, final int flag,
			final BaseAdapter adapter) {

		if (animUtil == null) {
			animUtil = new AnimUtil();
		}
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				view.setFocusable(false);
				view.setClickable(false);
				animUtil.startRotateAnimation(view);

				String url = getUrl(flag);
				if (url == null) {
					if (LogUtil.getDeBugState()) {
						UIUtils.showToast(UIUtils.getContext(), "路径错误");
					}
					view.clearAnimation();
					return;
				}
				mHelp.sendGet(url, TVbean.class,
						new MyRequestCallBack<TVbean>() {

							@Override
							public void onSucceed(TVbean bean) {
								view.setFocusable(true);
								view.setClickable(true);
								view.clearAnimation();
								if (bean == null) {
									return;
								}
								getBeanTypeAndUp(flag, bean.cont, adapter);
							}
						});

			}
		});
	}

	private void setSearchData() {
		String key = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.SERACH_CACHE_KEY, "");
		if (key == null || key == "") {
			show_del_layout.setVisibility(View.GONE);
			return;
		}
		show_del_layout.setVisibility(View.VISIBLE);
		mSearchKeys = key.split(GlobalConstant.FLAG_APP_SPLIT);
		fl_erach_cache.removeAllViews();
		LayoutInflater mInflater = LayoutInflater.from(mActivity);
		for (int i = 0; i < mSearchKeys.length; i++) {
			final int pos = i;
			TextView tv = (TextView) mInflater.inflate(
					R.layout.activity_search_tv, fl_erach_cache, false);
			tv.setText(mSearchKeys[i]);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goSearchResultPage(pos);
				}

			});
			fl_erach_cache.addView(tv);
		}
	}

	private class ViewHolder {
		RoundedImageView ivHome;
		TextView tvHome;
	}

	/**
	 * 判断类别和界面更新
	 * 
	 * @param flag
	 * @return
	 */
	private void getBeanTypeAndUp(int flag, List<TV> list, BaseAdapter adapter) {
		if (list == null || list.size() < 6) {
			// UIUtils.showToast(UIUtils.getContext(), "没有更多数据");
			switch (flag) {
			case FLAG_HOTKEY:
				if (hotKeysPage > 1) {
					hotKeysPage = 1;
				}
				break;
			case FLAG_APP:
				if (faxianPage > 1) {
					faxianPage = 1;
				}
				break;
			}
			return;
		}
		switch (flag) {
		case FLAG_HOTKEY:
			hotKeysPage++;
			mBean.cont.hotkeys = list;
			break;

		case FLAG_APP:
			faxianPage++;
			mBean.cont.faxian = list;
			break;
		}
		if (adapter == null) {
			initHotkeys();
		} else {

			adapter.notifyDataSetInvalidated();
		}
	}

	/**
	 * 点击标签跳到相应的视频处
	 * 
	 * @param i
	 */
	private void goSearchResultPage(int i) {
		Intent intent = new Intent(mActivity, SearchResultPageActivity.class);
		intent.putExtra(GlobalConstant.APP_SEARCH_KEY, mSearchKeys[i]);
		startActivity(intent);
	}

	/**
	 * 初始化关键字
	 */
	protected void initHotkeys() {

		List<TV> tv = mBean.cont.hotkeys;
		if (tv == null) {
			return;
		}
		tv_home_hot_1.setText(tv.get(0).name);
		tv_home_hot_2.setText(tv.get(1).name);
		tv_home_hot_3.setText(tv.get(2).name);
		tv_home_hot_4.setText(tv.get(3).name);
		tv_home_hot_5.setText(tv.get(4).name);
		tv_home_hot_6.setText(tv.get(5).name);
		setHomeOnClickListener(tv_home_hot_1, tv.get(0).name);
		setHomeOnClickListener(tv_home_hot_2, tv.get(1).name);
		setHomeOnClickListener(tv_home_hot_3, tv.get(2).name);
		setHomeOnClickListener(tv_home_hot_4, tv.get(3).name);
		setHomeOnClickListener(tv_home_hot_5, tv.get(4).name);
		setHomeOnClickListener(tv_home_hot_6, tv.get(5).name);
	}

	/**
	 * 点击事件
	 * 
	 * @param view
	 * @param key
	 */
	private void setHomeOnClickListener(View view, final String key) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UIUtils.showToast(UIUtils.getContext(), key);
				Intent intent = new Intent(mActivity,
						SearchResultPageActivity.class);
				intent.putExtra(GlobalConstant.APP_SEARCH_KEY, key);
				startActivity(intent);
				// PrefUtils.saveHotKey(key);
			}
		});
	}

	/**
	 * 获取URL
	 * 
	 * @param flag
	 * @return
	 */
	private int hotKeysPage = 2;
	private int faxianPage = 2;
	private String[] mSearchKeys;
	private TextView tv_del_all;

	protected String getUrl(int flag) {
		if (mBean == null || mBean.url == null) {
			return null;
		}
		String url = null;
		switch (flag) {
		case FLAG_HOTKEY:
			url = mBean.url.hotkeys + "&page=" + hotKeysPage;
			break;

		case FLAG_APP:
			url = mBean.url.faxian + "&page=" + faxianPage;
			break;

		}
		return url;
	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.gv_search_app:
			Intent i = new Intent(mActivity, APPDetailActivity.class);
			i.putExtra(GlobalConstant.INTENT_ID,
					mBean.cont.faxian.get(position).id);
			i.putExtra(GlobalConstant.INTENT_NAME,
					mBean.cont.faxian.get(position).name);
			MyApplication.getMainActivity().startActivity(i);
			// UIUtils.showToast(UIUtils.getContext(), "正在下载中……"
			// + mBean.cont.faxian.get(position).name);
			break;
		}
	}
}
