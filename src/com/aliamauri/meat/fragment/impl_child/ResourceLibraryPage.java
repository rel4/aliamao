package com.aliamauri.meat.fragment.impl_child;

import java.lang.reflect.Field;
import java.util.HashMap;

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
import com.aliamauri.meat.eventBus.ResourceUpdateUi;
import com.aliamauri.meat.fragment.BaseFragment_child;
import com.aliamauri.meat.fragment.impl_grandson_ys.HotVideoPage;
import com.aliamauri.meat.fragment.impl_grandson_ys.NewVideoPage;
import com.aliamauri.meat.fragment.impl_grandson_ys.TagsPage;
import com.aliamauri.meat.global.MyApplication;

import de.greenrobot.event.EventBus;

/**
 * 附近标题下的话题类 第二级
 * 
 * @author limaokeji-windosc
 * 
 */
public class ResourceLibraryPage extends BaseFragment_child implements
		OnClickListener {

	private static final String TITLE_NAME_ZXYS = "最新影视";
	private static final String TITLE_NAME_ZRYS = "最热影视";
	private static final String TITLE_NAME_BQ = "标签";

	private final int ZXYS_TAG = 0; // 顶部标签 ---最新影视 tag
	private final int ZRYS_TAG = 1; // 顶部标签 ---最热影视 tag
	private final int BQ_TAG = 2; // 顶部标签 ---标签 tag

	private final String CHILE_ZXYS_TAG = "zuixinyingshi"; // 附近——资源库--最新影视---fragment
	private final String CHILE_ZRYS_TAG = "zuireyingshi"; // 附近——资源库--最热影视--fragment
	private final String CHILE_BQ_TAG = "biaoqian"; // 附近——资源库--标签---fragment

	private FragmentManager mFm; // 子类的fragment管理器
	private NewVideoPage zxys_f; // 附近——资源库--最新影视
	private HotVideoPage zrys_f; // 附近——资源库--最热影视
	private TagsPage tag_f; // 附近——资源库--标签

	private TextView mTv_fragment_base_chile_title_zxys;
	private TextView mTv_fragment_base_chile_title_zrys;
	private TextView mTv_fragment_base_chile_title_bq;
	private ImageView mIv_fragment_base_chile_title_zxys;
	private ImageView mIv_fragment_base_chile_title_zrys;
	private ImageView mIv_fragment_base_chile_title_bq;

	@Override
	public void initChildDate() {
		mFm = getChildFragmentManager();
		// 初始化进入页面
		setCurrentchilePage_ys();
	}

	/**
	 * 显示当前默认页
	 */
	private void setCurrentchilePage_ys() {
		resBtn_dt();
		mTv_fragment_base_chile_title_zxys.setTextColor(getResources()
				.getColor(R.color.main_color));
		mIv_fragment_base_chile_title_zxys.setBackgroundColor(getResources()
				.getColor(R.color.main_color));
		setSelectState_ys(ZXYS_TAG);
	}
	
	public void onEventMainThread(ResourceUpdateUi ruu){
		zxys_f = null;
		initChildDate();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_fragment_base_chile_title_zxdt: // 标题栏的资源库按钮
			setSelectState_ys(ZXYS_TAG);
			break;
		case R.id.tv_fragment_base_chile_title_zrdt: // 标题栏的人按钮
			setSelectState_ys(ZRYS_TAG);
			break;
		case R.id.tv_fragment_base_chile_title_pyq: // 标题栏的群组按钮
			setSelectState_ys(BQ_TAG);
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
	private void setSelectState_ys(int tag) {

		resBtn_dt();
		FragmentTransaction transaction = mFm.beginTransaction();
		
		switch (tag) {
		case ZXYS_TAG:
			setCurrentState(ZXYS_TAG);

			if (zxys_f == null) {
				zxys_f = new NewVideoPage();
			}
			transaction.replace(R.id.fl_fragment_base_chile_title_content,
					zxys_f, CHILE_ZXYS_TAG);
			break;
		case ZRYS_TAG:
			setCurrentState(ZRYS_TAG);

			if (zrys_f == null) {
				zrys_f = new HotVideoPage();
			}
			transaction.replace(R.id.fl_fragment_base_chile_title_content,
					zrys_f, CHILE_ZRYS_TAG);
			break;
		case BQ_TAG:
			setCurrentState(BQ_TAG);

			if (tag_f == null) {
				tag_f = new TagsPage();
			}
			transaction.replace(R.id.fl_fragment_base_chile_title_content,
					tag_f, CHILE_BQ_TAG);
			break;

		default:
			break;
		}
		transaction.commitAllowingStateLoss();
	}

	/**
	 * 设置当前图标的状态
	 * 
	 * @param nearbyPage
	 */
	private void setCurrentState(int key) {
		switch (key) {
		case ZXYS_TAG:
			mTv_fragment_base_chile_title_zxys.setTextColor(getResources()
					.getColor(R.color.main_color));
			mIv_fragment_base_chile_title_zxys
					.setBackgroundColor(getResources().getColor(
							R.color.main_color));
			mTv_fragment_base_chile_title_zxys.setEnabled(false);
			mTv_fragment_base_chile_title_zrys.setEnabled(true);
			mTv_fragment_base_chile_title_bq.setEnabled(true);
			break;

		case ZRYS_TAG:
			mTv_fragment_base_chile_title_zrys.setTextColor(getResources()
					.getColor(R.color.main_color));
			mIv_fragment_base_chile_title_zrys
					.setBackgroundColor(getResources().getColor(
							R.color.main_color));
			mTv_fragment_base_chile_title_zxys.setEnabled(true);
			mTv_fragment_base_chile_title_zrys.setEnabled(false);
			mTv_fragment_base_chile_title_bq.setEnabled(true);
			break;
		case BQ_TAG:
			mTv_fragment_base_chile_title_bq.setTextColor(getResources()
					.getColor(R.color.main_color));
			mIv_fragment_base_chile_title_bq.setBackgroundColor(getResources()
					.getColor(R.color.main_color));
			mTv_fragment_base_chile_title_zxys.setEnabled(true);
			mTv_fragment_base_chile_title_zrys.setEnabled(true);
			mTv_fragment_base_chile_title_bq.setEnabled(false);
			break;

		default:
			break;
		}
	}

	/**
	 * 重置按钮的状态 字体颜色和背景图片
	 */
	private void resBtn_dt() {
		mTv_fragment_base_chile_title_zxys.setTextColor(getResources()
				.getColor(R.color.word_black));
		mIv_fragment_base_chile_title_zxys.setBackgroundColor(getResources()
				.getColor(R.color.bg_app_line));

		mTv_fragment_base_chile_title_zrys.setTextColor(getResources()
				.getColor(R.color.word_black));
		mIv_fragment_base_chile_title_zrys.setBackgroundColor(getResources()
				.getColor(R.color.bg_app_line));

		mTv_fragment_base_chile_title_bq.setTextColor(getResources().getColor(
				R.color.word_black));
		mIv_fragment_base_chile_title_bq.setBackgroundColor(getResources()
				.getColor(R.color.bg_app_line));
	}

	@Override
	public View initChildView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
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
		mTv_fragment_base_chile_title_zxys.setOnClickListener(this);
		mTv_fragment_base_chile_title_zrys.setOnClickListener(this);
		mTv_fragment_base_chile_title_bq.setOnClickListener(this);
	}

	/**
	 * 初始化各种控件的查找
	 * 
	 * @param view
	 */
	private void init(View view) {
		MyApplication.UpDowns = new HashMap<>(); // 创建存储顶踩条目的集合
		mTv_fragment_base_chile_title_zxys = $(view,
				R.id.tv_fragment_base_chile_title_zxdt);
		mTv_fragment_base_chile_title_zrys = $(view,
				R.id.tv_fragment_base_chile_title_zrdt);
		mTv_fragment_base_chile_title_bq = $(view,
				R.id.tv_fragment_base_chile_title_pyq);

		mTv_fragment_base_chile_title_zxys.setText(TITLE_NAME_ZXYS);
		mTv_fragment_base_chile_title_zrys.setText(TITLE_NAME_ZRYS);
		mTv_fragment_base_chile_title_bq.setText(TITLE_NAME_BQ);

		mIv_fragment_base_chile_title_zxys = $(view,
				R.id.iv_fragment_base_chile_title_zxdt);
		mIv_fragment_base_chile_title_zrys = $(view,
				R.id.iv_fragment_base_chile_title_zrdt);
		mIv_fragment_base_chile_title_bq = $(view,
				R.id.iv_fragment_base_chile_title_pyq);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		// 删除资源库中的存储点赞的条目id
		if (MyApplication.UpDowns != null) {
			MyApplication.UpDowns.clear();
			MyApplication.UpDowns = null;
		}
		super.onDestroy();
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
