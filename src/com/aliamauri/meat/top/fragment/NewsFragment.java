package com.aliamauri.meat.top.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.R.integer;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.aliamauri.meat.activity.DoyenDetailActvity;
import com.aliamauri.meat.activity.WebViewActivity;
import com.aliamauri.meat.bean.BaseTopBean;
import com.aliamauri.meat.bean.DTVListBaen.DTvCont;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.play.PlayActivity;
import com.aliamauri.meat.play.PlaySourceActivity;
import com.aliamauri.meat.top.adapter.ListBaseAdapter;
import com.aliamauri.meat.top.manager.ThreadManager;
import com.aliamauri.meat.top.protocol.NewsFragmentProtocol;
import com.aliamauri.meat.top.ui.BasePullToRefreshListView;
import com.aliamauri.meat.top.ui.LoadingPage.LoadResult;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.UIUtils;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class NewsFragment extends BaseFragment implements
		OnRefreshListener2<ListView> {
	public NewsFragment(int channelId) {
		super(channelId);
	}

	public NewsFragment() {
		super();
	}

	private List<BaseTopBean> datas;
	private List<Integer> topId = new ArrayList<Integer>();
	private BasePullToRefreshListView lv;
	private ListBaseAdapter listBaseAdapter;
	/**
	 * 去重数据 条目ID 和数组角标
	 */
	private LinkedHashMap<String, BaseTopBean> maps = new LinkedHashMap<String, BaseTopBean>();

	@Override
	protected View createSuccessView() {
		lv = new BasePullToRefreshListView(UIUtils.getContext());
		ILoadingLayout startLabels = lv.getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel("正在载入...");// 刷新时
		startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

		ILoadingLayout endLabels = lv.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
		endLabels.setRefreshingLabel("正在载入...");// 刷新时
		endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示
		reFreshUI();
		return lv;
	}

	public void reFreshUI() {

		if (listBaseAdapter == null) {
			listBaseAdapter = new ListBaseAdapter(datas);
			lv.setAdapter(listBaseAdapter);
			lv.setMode(Mode.BOTH);

			lv.setOnRefreshListener(this);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					DTvCont dTvCont = (DTvCont) datas.get(position - 1);
					if (dTvCont != null) {
						enterPage(dTvCont);
					}

				}

			});
		} else {
			listBaseAdapter.notifyDataSetChanged();
		}
	}

	private void enterPage(DTvCont dTvCont) {
		Intent intent;
		if (dTvCont.type == 1) {
			intent = new Intent(getActivity(), PlayActivity.class);
			intent.putExtra(GlobalConstant.PLAY_VIDEO_ID, dTvCont.id);
			getActivity().startActivity(intent);
		} else if (dTvCont.type == 2) {
			switch (dTvCont.action) {
			case "1":// webView
				// UIUtils.showToast(UIUtils.getContext(), "去浏览器网址："
				// + dTvCont.param);
				intent = new Intent(getActivity(), WebViewActivity.class);
				intent.putExtra(GlobalConstant.INTENT_DATA, dTvCont.param);
				getActivity().startActivity(intent);
				break;
			case "2":// 播放页面
				// UIUtils.showToast(UIUtils.getContext(), "播放页面 id："
				// + dTvCont.param);
				intent = new Intent(getActivity(), PlayActivity.class);
				intent.putExtra(GlobalConstant.PLAY_VIDEO_ID, dTvCont.id);
				getActivity().startActivity(intent);
				break;
			case "3":// 达人页面

				// intent = new Intent(getActivity(), DoyenDetailActvity.class);
				// intent.putExtra(GlobalConstant.INTENT_ID, dTvCont.uid);
				// getActivity().startActivity(intent);
				// // UIUtils.showToast(UIUtils.getContext(), "达人页面 id："
				// + dTvCont.param);
				break;
			case "4":
				intent = new Intent(getActivity(), PlayActivity.class);
				intent.putExtra(GlobalConstant.PLAY_VIDEO_ID, dTvCont.id);
				getActivity().startActivity(intent);
			default:
				break;
			}
		} else if (dTvCont.type == 4) {
			intent = new Intent(getActivity(), PlayActivity.class);
			intent.putExtra(GlobalConstant.PLAY_VIDEO_ID, dTvCont.id);
			getActivity().startActivity(intent);
		}

	}

	/*
	 * 请求服务器 服务器返回的状态
	 */
	@Override
	public LoadResult onLoad() {
		LogUtil.e(NewsFragment.this, "channelId: " + channelId);
		NewsFragmentProtocol protocol = new NewsFragmentProtocol();
		datas = protocol.load(channelId, page);
		if (datas != null) {
			for (BaseTopBean b : datas) {
				if (b != null) {
					maps.put(b.id, b);
				}
			}
		}
		sortDatas(datas);
		return checkDatas(datas);
	}

	@Override
	public void onStart() {
		super.onStart();
		show();// 解决了第一个界面不能切换状态的bug
	}

	private void loadMord() {
		ThreadManager.getInstance().executeLongTask(new Runnable() {

			@Override
			public void run() {
				NewsFragmentProtocol protocol = new NewsFragmentProtocol();
				int currentPage = page;
				if (isPullDownToRefresh) {
					currentPage = 1;
				} else {
					page++;
					currentPage = page;
				}
				List<BaseTopBean> loaddatas = protocol.load(channelId,
						currentPage);
				if (loaddatas == null || loaddatas.size() == 0) {
					UIUtils.runInMainThread(new Runnable() {

						@Override
						public void run() {
							if (!isPullDownToRefresh) {
								page--;
							}
							UIUtils.showToast(UIUtils.getContext(), "没有更多数据");
							if (lv != null) {
								lv.onRefreshComplete();
							}
						}
					});

					return;
				}

				if (isPullDownToRefresh) {
					for (int i = loaddatas.size() - 1; i >= 0; i--) {
						DTvCont dTvCont = (DTvCont) loaddatas.get(i);
						if (dTvCont != null && !maps.containsKey(dTvCont.id)) {
							datas.add(0, dTvCont);
							maps.put(dTvCont.id, dTvCont);
						} else if (dTvCont != null
								&& maps.containsKey(dTvCont.id)) {
							int indexOf = datas.indexOf(maps.get(dTvCont.id));
							boolean isrRemove = datas.remove(maps.get(dTvCont.id));
							maps.put(dTvCont.id, dTvCont);
							datas.add(indexOf, dTvCont);
						}
					}
				} else {
					for (BaseTopBean dTvCont : loaddatas) {
						if (dTvCont != null && !maps.containsKey(dTvCont.id)) {
							maps.put(dTvCont.id, dTvCont);
							datas.add(dTvCont);
						} else if (dTvCont != null
								&& maps.containsKey(dTvCont.id)) {
							int indexOf = datas.indexOf(maps.get(dTvCont.id));
							datas.remove(maps.get(dTvCont.id));
							maps.put(dTvCont.id, dTvCont);
							datas.add(indexOf,dTvCont);
						}
					}
				}
				sortDatas(datas);
				UIUtils.runInMainThread(new Runnable() {

					@Override
					public void run() {
						reFreshUI();
						if (lv != null) {
							lv.onRefreshComplete();
						}

					}
				});

			}
		});

	}

	/**
	 * 排序
	 * 
	 * @param datas
	 */
	private void sortDatas(List<BaseTopBean> sortDatas) {
		if (sortDatas == null || sortDatas.isEmpty()) {
			return;
		}
		ArrayList<BaseTopBean> tops = null;
		for (int i = 0; i < sortDatas.size(); i++) {
			BaseTopBean baseTopBean = sortDatas.get(i);
			if (baseTopBean != null && baseTopBean instanceof DTvCont) {
				DTvCont d = (DTvCont) baseTopBean;
				if (d != null && d.type == 2) {
					if (tops == null) {
						tops = new ArrayList<BaseTopBean>();
					}
					tops.add(d);
				}

			}
		}
		if (tops != null && !tops.isEmpty()) {
			for (int i = tops.size() - 1; i >= 0; i--) {
				BaseTopBean b = tops.get(i);
				sortDatas.remove(b);
				sortDatas.add(0, b);
				// BaseTopBean baseTopBean = sortDatas.get(index);
				// sortDatas.remove(index);
				// sortDatas.add(0, baseTopBean);
			}
			// for (int i = 0; i < tops.size(); i++) {
			// int index = tops.get(i).intValue();
			// BaseTopBean baseTopBean = sortDatas.get(index);
			// sortDatas.remove(index);
			// sortDatas.add(0, baseTopBean);
			// }
		}
	}

	private boolean isPullDownToRefresh = false;

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		isPullDownToRefresh = true;
		loadMord();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		isPullDownToRefresh = false;
		loadMord();

	}
}
