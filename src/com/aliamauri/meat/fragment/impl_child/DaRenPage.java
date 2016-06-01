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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.fragment.BaseFragment_child;
import com.aliamauri.meat.fragment.impl_grandson_dr.FuJinPage;
import com.aliamauri.meat.fragment.impl_grandson_dr.RemenPage;
import com.aliamauri.meat.fragment.impl_grandson_dr.XinRuiPage;

public class DaRenPage extends BaseFragment_child implements OnClickListener {

	private TextView mRb_dr_fj, mRb_dr_xr, mRb_dr_rm;
	private FrameLayout mFl_dr_content;
	/**
	 * 顶部标签 ---附近 tag
	 */
	public static final int FJ_TAG = 1;
	/**
	 * 顶部标签 ---新锐 tag
	 */
	public static final int XR_TAG = 2;
	/**
	 * 顶部标签 ---热门 tag
	 */
	public static final int RM_TAG = 3;
	private FragmentManager mFm;
	/**
	 * 附近页面
	 */
	private FuJinPage fj_page;
	/**
	 * 新锐页面
	 */
	private XinRuiPage xr_page;
	/**
	 * 热门页面
	 */
	private RemenPage rm_page;

	@Override
	public View initChildView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_child_dr, container,
				false);
		init(view);
		return view;
	}

	@Override
	public void initChildDate() {

		setSelectState(FJ_TAG);
	}

	/**
	 * 切换标题按钮的时候改变相应的状态
	 * 
	 * @param tag
	 */
	private void setSelectState(int tag) {

		FragmentTransaction transaction = mFm.beginTransaction();

		switch (tag) {
		case FJ_TAG:

			if (fj_page == null) {
				fj_page = new FuJinPage();
			}
			if (!fj_page.isAdded())
				transaction.replace(R.id.fl_dr_content, fj_page);
			break;
		case XR_TAG:
			if (xr_page == null) {
				xr_page = new XinRuiPage();
			}
			if (!xr_page.isAdded())
				transaction.replace(R.id.fl_dr_content, xr_page);
			break;
		case RM_TAG:
			if (rm_page == null) {
				rm_page = new RemenPage();
			}
			if (!rm_page.isAdded())
				transaction.replace(R.id.fl_dr_content, rm_page);
			break;

		default:
			break;
		}
		transaction.commitAllowingStateLoss();
	}

	/**
	 * 初始化各种控件的查找
	 * 
	 * @param view
	 */
	private void init(View view) {
		mFm = getChildFragmentManager();
		mRb_dr_fj = $(view, R.id.rb_dr_fj);
		mRb_dr_xr = $(view, R.id.rb_dr_xr);
		mRb_dr_rm = $(view, R.id.rb_dr_rm);
		mFl_dr_content = $(view, R.id.fl_dr_content);
		mRb_dr_fj.setOnClickListener(this);
		mRb_dr_xr.setOnClickListener(this);
		mRb_dr_rm.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_dr_fj:
			setSelectState(FJ_TAG);
			break;
		case R.id.rb_dr_xr:
			setSelectState(XR_TAG);
			break;
		case R.id.rb_dr_rm:
			setSelectState(RM_TAG);
			break;
		}
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
