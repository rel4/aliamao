package com.aliamauri.meat.top.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliamauri.meat.top.ui.LoadingPage;
import com.aliamauri.meat.top.ui.LoadingPage.LoadResult;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.utils.ViewUtils;

public abstract class BaseFragment extends Fragment {

	private LoadingPage mFrameLayout;
	protected int channelId;
	protected int page = 1;

	public BaseFragment(int channelId) {
		this.channelId = channelId;
	}

	public BaseFragment() {
	}

	public int getChannelId() {
		return channelId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (mFrameLayout == null) {
			mFrameLayout = new LoadingPage(UIUtils.getContext()) {

				@Override
				protected View createSuccessView() {
					return BaseFragment.this.createSuccessView();
				}

				@Override
				public LoadResult onLoad() {
					return BaseFragment.this.onLoad();
				}
			};
		} else {
			// 移除之前的布局
			ViewUtils.removeParent(mFrameLayout);
		}
		// show(); 创建界面 直接请求服务器 改成看到界面请求服务器
		// 当前帧布局就会挂载到其中的一个界面上
		return mFrameLayout;
	}

	public void show() {
		if (mFrameLayout!=null) {
			mFrameLayout.show();
		}
	}

	public LoadResult checkDatas(List datas) {
		if (datas == null) {
			return LoadResult.error;
		} else {
			if (datas.size() == 0) {
				return LoadResult.empty;
			} else {
				return LoadResult.success;
			}
		}
	}

	/**
	 * 创建成功界面
	 * 
	 * @return
	 */
	protected abstract View createSuccessView();

	/**
	 * 请求服务器
	 * 
	 * @return
	 */
	public abstract LoadResult onLoad();
}
