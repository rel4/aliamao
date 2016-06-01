package com.aliamauri.meat.fragment.impl_child;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.top.ChannelActivity;
import com.aliamauri.meat.top.bean.ChannelItem;
import com.aliamauri.meat.top.bean.ChannelManage;
import com.aliamauri.meat.top.db.SQLHelper;
import com.aliamauri.meat.top.fragment.BaseFragment;
import com.aliamauri.meat.top.fragment.FragmentFactory;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.SystemUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.ColumnHorizontalScrollView;
import com.aliamauri.meat.weight.ViewPager;
import com.aliamauri.meat.weight.ViewPager.OnPageChangeListener;

public class HomeTVListFragment extends Fragment {
	private View mView;
	/** 自定义HorizontalScrollView */
	private ColumnHorizontalScrollView mColumnHorizontalScrollView;
	LinearLayout mRadioGroup_content;
	LinearLayout ll_more_columns;
	RelativeLayout rl_column;
	private ViewPager mViewPager;
	private ImageView button_more_columns;
	/** 用户选择的新闻分类列表 */
	private ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();
	/** 当前选中的栏目 */
	private int columnSelectIndex = 0;
	/** 左阴影部分 */
	public ImageView shade_left;
	/** 右阴影部分 */
	public ImageView shade_right;
	/** 屏幕宽度 */
	private int mScreenWidth = 0;
	/** Item宽度 */
	private int mItemWidth = 0;

	/** 请求CODE */
	public final static int CHANNELREQUEST = 1;
	/** 调整返回的RESULTCODE */
	public final static int CHANNELRESULT = 10;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// textData();
		mView = inflater.inflate(R.layout.fragment_home_tv_list, null);
		mScreenWidth = SystemUtils.getScreenWidth();
		mItemWidth = mScreenWidth / 7;// 一个Item宽度为屏幕的1/7
		initView();
		return mView;

	}

	private void initView() {
		mColumnHorizontalScrollView = (ColumnHorizontalScrollView) mView
				.findViewById(R.id.mColumnHorizontalScrollView);
		mRadioGroup_content = (LinearLayout) mView
				.findViewById(R.id.mRadioGroup_content);
		ll_more_columns = (LinearLayout) mView
				.findViewById(R.id.ll_more_columns);
		rl_column = (RelativeLayout) mView.findViewById(R.id.rl_column);
		button_more_columns = (ImageView) mView
				.findViewById(R.id.button_more_columns);
		mViewPager = (ViewPager) mView.findViewById(R.id.mViewPager);
		shade_left = (ImageView) mView.findViewById(R.id.shade_left);
		shade_right = (ImageView) mView.findViewById(R.id.shade_right);
		button_more_columns.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent_channel = new Intent(getApplicationContext(),
				// ChannelActivity.class);
				// startActivityForResult(intent_channel, CHANNELREQUEST);
				// overridePendingTransition(R.anim.slide_in_right,
				// R.anim.slide_out_left);
				// Intent intent = new Intent(getActivity(),
				// ChannelActivity.class);
				// startActivityForResult(intent, CHANNELREQUEST);
				Intent intent = new Intent();
				intent.setClass(getActivity(), ChannelActivity.class);
				// intent.putExtra("TAG", "TEST");
				getRootFragment()
						.startActivityForResult(intent, CHANNELREQUEST);
			}
		});

		setChangelView();
	}

	/**
	 * 得到根Fragment
	 * 
	 * @return
	 */
	private Fragment getRootFragment() {
		Fragment fragment = getParentFragment();
		while (fragment.getParentFragment() != null) {
			fragment = fragment.getParentFragment();
		}
		return fragment;

	}

	/**
	 * 当栏目项发生变化时候调用
	 * */
	private void setChangelView() {
		initColumnData();
		initTabColumn();
		initFragment();
	}

	/** 获取Column栏目 数据 */
	private void initColumnData() {
		SQLHelper sqlHelper = new SQLHelper(UIUtils.getContext());
		userChannelList = ((ArrayList<ChannelItem>) ChannelManage.getManage(

		sqlHelper).getUserChannel());
		if (userChannelList == null || userChannelList.size() == 0) {
			MainActivity activity = (MainActivity) MyApplication
					.getMainActivity();
			if (activity != null) {
				activity.showConflictDialog();
			}
		}
	}

	/**
	 * 初始化Column栏目项
	 * */
	private void initTabColumn() {
		mRadioGroup_content.removeAllViews();
		int count = userChannelList.size();
		if (columnSelectIndex>count) {
			columnSelectIndex=0;
		}
		mColumnHorizontalScrollView.setParam(getActivity(), mScreenWidth,
				mRadioGroup_content, shade_left, shade_right, ll_more_columns,
				rl_column);
		for (int i = 0; i < count; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					mItemWidth, LayoutParams.MATCH_PARENT);
			params.leftMargin = 5;
			params.rightMargin = 5;
			// TextView localTextView = (TextView)
			// mInflater.inflate(R.layout.column_radio_item, null);
			TextView columnTextView = new TextView(getActivity());
			columnTextView.setTextAppearance(getActivity(),
					R.style.top_category_scroll_view_item_text);
			// localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
			// columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
			columnTextView.setGravity(Gravity.CENTER);
			columnTextView.setPadding(5, 5, 5, 0);
			columnTextView.setId(i);
			Drawable drawable = getResources().getDrawable(R.drawable.dr_title_text_bottom_line_selector);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			columnTextView.setCompoundDrawables(null, null, null, drawable);
			columnTextView.setText(userChannelList.get(i).getName());
			columnTextView.setTextColor(getResources().getColorStateList(
					R.color.top_category_scroll_text_color_day));
			if (columnSelectIndex == i) {
				columnTextView.setSelected(true);
			}
			columnTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
						View localView = mRadioGroup_content.getChildAt(i);
						if (localView != v)
							localView.setSelected(false);
						else {
							localView.setSelected(true);
							if (mViewPager != null) {
								mViewPager.setCurrentItem(i);
							}
						}
					}
					// Toast.makeText(getApplicationContext(),
					// userChannelList.get(v.getId()).getName(),
					// Toast.LENGTH_SHORT).show();
				}
			});
			mRadioGroup_content.addView(columnTextView, i, params);
		}
	}

	/**
	 * 选择的Column里面的Tab
	 * */
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			// rg_nav_content.getParent()).smoothScrollTo(i2, 0);
			mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
			// mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
			// mItemWidth , 0);
		}
		// 判断是否选中
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}

	/**
	 * 初始化Fragment
	 * */
	private void initFragment() {
		FragmentFactory.cleanFragments();
		// fragments.clear();// 清空
		// int count = userChannelList.size();
		// for (int i = 0; i < count; i++) {
		// Bundle data = new Bundle();
		// // data.putString("text", userChannelList.get(i).getName());
		// // data.putInt("id", userChannelList.get(i).getId());
		// // TVListFragment newfragment = new TVListFragment();
		// NewsFragment newfragment = new NewsFragment();
		// newfragment.setArguments(data);
		// fragments.add(newfragment);
		// }
		// NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(
		// activity.getSupportFragmentManager(), fragments);
		LogUtil.e(this, "****************mViewPager初始化***************");
		// if (mainAdapter == null) {
		mainAdapter = new MainAdapter(getChildFragmentManager());
		selectTab(0);
		mViewPager.setAdapter(mainAdapter);
//		mViewPager.setOffscreenPageLimit(0);
		mViewPager.setOnPageChangeListener(pageListener);
		// } else {
		// mainAdapter.notifyDataSetChanged();
		// }

	}

	private class MainAdapter extends FragmentStatePagerAdapter {

		public MainAdapter(FragmentManager fm) {
			super(fm);
		}

		// 每个条目返回的Fragment 参数就是条目的位置
		@Override
		public Fragment getItem(int postion) {

			// 工厂设计模式
			ChannelItem channelItem = userChannelList.get(postion);
			return FragmentFactory.createFragment(postion, channelItem.getId());

		}

		// 条目的数量
		@Override
		public int getCount() {
			return userChannelList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			Object obj = super.instantiateItem(container, position);
			return obj;
		}

	}

	/**
	 * ViewPager切换监听方法
	 * */
	public OnPageChangeListener pageListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			LogUtil.e(HomeTVListFragment.this, "当前选择的页面： " + position);
			// TODO Auto-generated method stub
			ChannelItem channelItem = userChannelList.get(position);
			BaseFragment baseFragment = FragmentFactory.createFragment(
					position, channelItem.getId());
			baseFragment.show();
			// mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};
	private MainAdapter mainAdapter;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.e(this, "onActivityResult执行了。。。。。。。。。。");
		switch (requestCode) {
		case CHANNELREQUEST:
			if (resultCode == CHANNELRESULT) {
				setChangelView();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
