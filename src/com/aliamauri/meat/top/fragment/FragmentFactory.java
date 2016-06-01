package com.aliamauri.meat.top.fragment;

import android.util.SparseArray;

public class FragmentFactory {
	static SparseArray<BaseFragment> mFragments = new SparseArray<BaseFragment>();

	public static void cleanFragments() {
		if (mFragments.size() != 0) {
			mFragments.clear();
//			for (int i = 0; i < mFragments.size(); i++) {
//				BaseFragment baseFragment = mFragments.get(i);
//				mFragments.removeAt(i);
//				baseFragment = null;
//
//			}
		}
	}

	public static BaseFragment createFragment(int postion, int channelId) {
		BaseFragment baseFragment = mFragments.get(postion);
		if (baseFragment == null) {
			baseFragment = new NewsFragment(channelId);
			mFragments.put(postion, baseFragment);
		}
		return baseFragment;

		// if (baseFragment == null) {
		// switch (postion) {
		// case 0:
		// baseFragment = new NewsFragment();
		// break;
		// case 1:
		// baseFragment = new NewsFragment();
		// break;
		// case 2:
		// baseFragment = new NewsFragment();
		// break;
		// case 3:
		// baseFragment = new NewsFragment();
		// break;
		//
		// case 4:
		// baseFragment = new NewsFragment();
		// break;
		// case 5:
		// baseFragment = new NewsFragment();
		// break;
		// case 6:
		// baseFragment = new NewsFragment();
		// break;
		// case 7:
		// baseFragment = new NewsFragment();
		// break;
		//
		// }
		// mFragments.put(postion, baseFragment);
		// }
		// return baseFragment;
	}
}
