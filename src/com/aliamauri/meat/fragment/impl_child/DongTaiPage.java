package com.aliamauri.meat.fragment.impl_child;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.fragment.BaseFragment_child;
import com.aliamauri.meat.fragment.impl_grandson_dt.FriendsPage;
import com.aliamauri.meat.fragment.impl_grandson_dt.HotDynamicPage;
import com.aliamauri.meat.fragment.impl_grandson_dt.NewDynamicPage;
import com.aliamauri.meat.fragment.impl_grandson_dt.TVListFragment;

/**
 * 附近标题下的动态类 第二级
 * 
 * @author limaokeji-windosc
 * 
 */

public class DongTaiPage extends BaseFragment_child implements OnClickListener {

	private final int ZXDT_TAG = 0; // 顶部标签 ---最新动态 tag
	private final int ZRDT_TAG = 1; // 顶部标签 ---最热动态 tag
	private final int PYQ_TAG = 2; // 顶部标签 ---朋友圈 tag

	private final String CHILE_ZXDT_TAG = "zuixindongtai"; // 附近——动态
															// -最新动态---fragment
	private final String CHILE_ZRDT_TAG = "zuiredongtai"; // 附近——动态--最热动态--fragment
	private final String CHILE_PYQ_TAG = "pengyouquan"; // 附近——动态--朋友圈---fragment

	private FragmentManager mFm; // 子类的fragment管理器
	private NewDynamicPage zxdt_f; // 附近——动态--最新动态
	private HotDynamicPage zrdt_f; // 附近——动态--最热动态
	private FriendsPage pyq_f; // 附近——动态--朋友圈

	private TextView mTv_fragment_base_chile_title_zxdt;
	private TextView mTv_fragment_base_chile_title_zrdt;
	private TextView mTv_fragment_base_chile_title_pyq;
	private ImageView mIv_fragment_base_chile_title_zxdt;
	private ImageView mIv_fragment_base_chile_title_zrdt;
	private ImageView mIv_fragment_base_chile_title_pyq;

	@Override
	public void initChildDate() {
		mFm = getChildFragmentManager();
		// 初始化进入页面
		setCurrentchilePage_dt();

	}

	/**
	 * 显示当前默认页
	 */
	private void setCurrentchilePage_dt() {
		resBtn_dt();

		mTv_fragment_base_chile_title_zxdt.setTextColor(getResources()
				.getColor(R.color.main_color));
		mIv_fragment_base_chile_title_zxdt.setBackgroundColor(getResources()
				.getColor(R.color.main_color));

		setSelectState_dt(ZRDT_TAG);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_fragment_base_chile_title_zxdt: // 标题栏的最新动态按钮
			setSelectState_dt(ZXDT_TAG);
			break;
		case R.id.tv_fragment_base_chile_title_zrdt: // 标题栏的最热动态按钮
			setSelectState_dt(ZRDT_TAG);
			break;
		case R.id.tv_fragment_base_chile_title_pyq: // 标题栏的朋友圈按钮
			setSelectState_dt(PYQ_TAG);
			break;

		default:
			break;
		}
	}

	/**
	 * 切换标题按钮的时候改变相应的状态
	 * 
	 * @param tag
	 */
	private void setSelectState_dt(int tag) {

		resBtn_dt();
		FragmentTransaction transaction = mFm.beginTransaction();

		switch (tag) {
		case ZXDT_TAG:
			setCurrentState(ZXDT_TAG);

			if (zxdt_f == null) {
				zxdt_f = new NewDynamicPage();
			}
			transaction.replace(R.id.fl_fragment_base_chile_title_content,
					zxdt_f, CHILE_ZXDT_TAG);
			break;
		case ZRDT_TAG:
			setCurrentState(ZRDT_TAG);

//			if (zrdt_f == null) {
//				zrdt_f = new HotDynamicPage();
//			}
			TVListFragment tvListFragment = new TVListFragment();
			transaction.replace(R.id.fl_fragment_base_chile_title_content,
					tvListFragment, CHILE_ZRDT_TAG);
			break;
		case PYQ_TAG:
			setCurrentState(PYQ_TAG);

			if (pyq_f == null) {
				pyq_f = new FriendsPage();
			}
			transaction.replace(R.id.fl_fragment_base_chile_title_content,
					pyq_f, CHILE_PYQ_TAG);
			break;

		default:
			break;
		}
		transaction.commit();
	}

	/**
	 * 设置当前图标的状态
	 * 
	 * @param nearbyPage
	 */
	private void setCurrentState(int key) {
		switch (key) {
		case ZXDT_TAG:
			mTv_fragment_base_chile_title_zxdt.setTextColor(getResources()
					.getColor(R.color.main_color));
			mIv_fragment_base_chile_title_zxdt
					.setBackgroundColor(getResources().getColor(
							R.color.main_color));
			mTv_fragment_base_chile_title_zxdt.setEnabled(false);
			mTv_fragment_base_chile_title_zrdt.setEnabled(true);
			mTv_fragment_base_chile_title_pyq.setEnabled(true);
			break;

		case ZRDT_TAG:
			mTv_fragment_base_chile_title_zrdt.setTextColor(getResources()
					.getColor(R.color.main_color));
			mIv_fragment_base_chile_title_zrdt
					.setBackgroundColor(getResources().getColor(
							R.color.main_color));
			mTv_fragment_base_chile_title_zxdt.setEnabled(true);
			mTv_fragment_base_chile_title_zrdt.setEnabled(false);
			mTv_fragment_base_chile_title_pyq.setEnabled(true);
			break;
		case PYQ_TAG:
			mTv_fragment_base_chile_title_pyq.setTextColor(getResources()
					.getColor(R.color.main_color));
			mIv_fragment_base_chile_title_pyq.setBackgroundColor(getResources()
					.getColor(R.color.main_color));
			mTv_fragment_base_chile_title_zxdt.setEnabled(true);
			mTv_fragment_base_chile_title_zrdt.setEnabled(true);
			mTv_fragment_base_chile_title_pyq.setEnabled(false);
			break;

		default:
			break;
		}
	}

	/**
	 * 重置按钮的状态 字体颜色和背景图片
	 */
	private void resBtn_dt() {
		mTv_fragment_base_chile_title_zxdt.setTextColor(getResources()
				.getColor(R.color.word_black));
		mIv_fragment_base_chile_title_zxdt.setBackgroundColor(getResources()
				.getColor(R.color.bg_app_line));

		mTv_fragment_base_chile_title_zrdt.setTextColor(getResources()
				.getColor(R.color.word_black));
		mIv_fragment_base_chile_title_zrdt.setBackgroundColor(getResources()
				.getColor(R.color.bg_app_line));

		mTv_fragment_base_chile_title_pyq.setTextColor(getResources().getColor(
				R.color.word_black));
		mIv_fragment_base_chile_title_pyq.setBackgroundColor(getResources()
				.getColor(R.color.bg_app_line));
	}

	@Override
	public View initChildView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_child_dt, container,
				false);
		init(view);
		initClicks();
		return view;
	}

	/**
	 * 此方法可解决 noActivity异常
	 * 
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 初始化点击事件
	 */
	private void initClicks() {
		mTv_fragment_base_chile_title_zxdt.setOnClickListener(this);
		mTv_fragment_base_chile_title_zrdt.setOnClickListener(this);
		mTv_fragment_base_chile_title_pyq.setOnClickListener(this);
	}

	/**
	 * 初始化各种控件的查找
	 * 
	 * @param view
	 */
	private void init(View view) {
		mTv_fragment_base_chile_title_zxdt = $(view,
				R.id.tv_fragment_base_chile_title_zxdt);
		mTv_fragment_base_chile_title_zrdt = $(view,
				R.id.tv_fragment_base_chile_title_zrdt);
		mTv_fragment_base_chile_title_pyq = $(view,
				R.id.tv_fragment_base_chile_title_pyq);
		mIv_fragment_base_chile_title_zxdt = $(view,
				R.id.iv_fragment_base_chile_title_zxdt);
		mIv_fragment_base_chile_title_zrdt = $(view,
				R.id.iv_fragment_base_chile_title_zrdt);
		mIv_fragment_base_chile_title_pyq = $(view,
				R.id.iv_fragment_base_chile_title_pyq);
	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

}
