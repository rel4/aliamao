package com.aliamauri.meat.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.DoyenDetailActvity;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.adapter.DRContentAdapter;
import com.aliamauri.meat.bean.DrBean;
import com.aliamauri.meat.bean.DrBean.Cont;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.play.PlayActivity;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

/**
 * 资源库-基类
 * 
 * @author limaokeji-windosc
 * 
 */
public abstract class BaseFragment_grandson_dr extends Fragment implements
		OnItemClickListener, OnRefreshListener2<GridView> {
	public Activity mActivity;
	private String mUser_Id; // 本机用户的uID
	private HttpHelp mHttp;
	private int mCurrentPage = 1; // 默认页为1
	private int mChildType; // 获取当前子类类型的type
	private PullToRefreshGridView mGv_dr_content;
	private DRContentAdapter mAdapter;

	private ArrayList<Cont> mUsers;

	/**
	 * 获取子类的url
	 * 
	 * @return
	 */
	public abstract String getUrl(int page);

	/**
	 * 获取每个子类的类型
	 * 
	 * @return
	 */
	public abstract int getType();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mChildType = getType();
		mUsers = new ArrayList<>();
		mUser_Id = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_ID, "");
		mHttp = new HttpHelp();
		mCurrentPage = 1; // 默认页为1
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dr_content, container,
				false);
		mGv_dr_content = $(view, R.id.prgv_dr_content);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mGv_dr_content.setOnRefreshListener(this);
		mGv_dr_content.setOnItemClickListener(this);
		initNet();

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {

		mCurrentPage = 1;
		initNet();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
		initNet();
	}

	@Override
	public void onDestroy() {
		// 取消当前的网络请求操作
		if (mHttp != null) {
			mHttp.stopHttpNET();
		}
		super.onDestroy();
	}

	/**
	 * 初始化网络请求
	 * 
	 * @param url
	 */
	private void initNet() {

		String url = getUrl(mCurrentPage);
		mHttp.sendGet(url, DrBean.class, new MyRequestCallBack<DrBean>() {

			@Override
			public void onSucceed(DrBean bean) {
				mGv_dr_content.onRefreshComplete();
				if (bean == null || bean.cont == null || bean.status == null) {
					UIUtils.showToast(mActivity, "网络异常");
					return;
				}
				switch (bean.status) {
				case "1":
					if (bean.cont.size() <= 0) {
						UIUtils.showToast(mActivity, mActivity.getResources()
								.getString(R.string.text_no_data));
						return;
					}
					if (mCurrentPage <= 1) {
						mUsers.clear();
						mUsers = (ArrayList<Cont>) bean.cont;
						mAdapter = new DRContentAdapter(mActivity, mUsers,
								R.layout.item_dr_content) {

							@Override
							public void goToUserBaseDate(int position) {
								if (mUser_Id.equals(mUsers.get(position).uid)) {
									((MainActivity) mActivity).getMyPage();
								} else {
									startActivity(new Intent(mActivity,
											DoyenDetailActvity.class).putExtra(
											GlobalConstant.INTENT_ID,
											mUsers.get(position).uid));
								}
							}
						};
						mGv_dr_content.setAdapter(mAdapter);
					} else {
						mUsers.addAll(bean.cont);
						mAdapter.notifyDataSetChanged();
					}

					mCurrentPage++;

					break;

				default:
					UIUtils.showToast(getActivity(), bean.msg);
					break;
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		startActivity(new Intent(mActivity, PlayActivity.class).putExtra(
				GlobalConstant.PLAY_VIDEO_ID, mUsers.get(position).id));

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
