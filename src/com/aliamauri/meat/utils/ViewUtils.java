package com.aliamauri.meat.utils;

import android.view.ViewGroup;
import android.view.ViewParent;

import com.aliamauri.meat.top.ui.LoadingPage;

public class ViewUtils {
	public static void removeParent(LoadingPage mFrameLayout) {
		// 爹移除孩子 // 首先获取到孩子的爹
		ViewParent parent = mFrameLayout.getParent();
		ViewGroup group = (ViewGroup) parent;
		group.removeView(mFrameLayout);

	}
}
