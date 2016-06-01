package com.aliamauri.meat.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FramgentUtils {
	/**
	 * 
	 * @param manager
	 * @param layout
	 * @param hide
	 * @param show
	 */
	public static void switchFragment(FragmentManager manager, int layout,
			Fragment hide, Fragment show) {
		if (hide == null || show == null || manager == null)
			return;
		FragmentTransaction transaction = manager.beginTransaction();
		// FragmentTransaction transaction =mFm. getSupportFragmentManager()
		// .beginTransaction().setCustomAnimations(R.anim.tran_pre_in,
		// R.anim.tran_pre_out);
		if (!show.isAdded()) {
			// 隐藏当前的fragment，add下一个到Activity中
			transaction.hide(hide).add(layout, show)
					.commit();
		} else {
			// 隐藏当前的fragment，显示下一个
			transaction.hide(hide).show(show).commit();
		}
	}
}
