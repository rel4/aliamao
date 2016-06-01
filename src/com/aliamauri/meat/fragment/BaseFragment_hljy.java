package com.aliamauri.meat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment_hljy extends Fragment {

	
	public FragmentActivity mActivity;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initChildDate();
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mActivity = getActivity();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return initChildView(inflater,container,savedInstanceState);
	}

	/**
	 * 初始化数据
	 */
	public abstract void initChildDate();
	/**
	 * 初始化试图
	 * @return 
	 */
	public abstract View initChildView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);

}
