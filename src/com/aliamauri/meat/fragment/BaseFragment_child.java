package com.aliamauri.meat.fragment;

import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author ych
 * 
 */
public abstract class BaseFragment_child extends Fragment {
	public Activity mActivity;

	public final static int INT_LEFT = 0;
	public final static int INT_CENTER = 1;
	public final static int INT_RIGHT = 2;

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
	 * 获取子类中的数据
	 */
	public abstract void initChildDate();

	/**
	 * 初始化子类中的view
	 * 
	 * @param savedInstanceState
	 * @param container
	 * @param inflater
	 */
	public abstract View initChildView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initChildDate();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return initChildView(inflater, container, savedInstanceState);
	}
}
