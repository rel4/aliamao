package com.aliamauri.meat.fragment.impl_grandson_dt;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.adapter.AppBaseAdapter;
import com.aliamauri.meat.bean.DTVListBaen;
import com.aliamauri.meat.bean.DTVListBaen.DTvCont;
import com.aliamauri.meat.bean.DTVListBaen.DTvCont.TwoDTVCont;
import com.aliamauri.meat.fragment.BaseFragment_child;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.play.PlaySourceActivity;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class TVListFragment extends BaseFragment_child implements
		OnItemClickListener, OnRefreshListener2<ListView> {

	private View mView;
	private PullToRefreshListView lv_content;
	private List<DTvCont> datas;

	@Override
	public View initChildView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_tv_list, null);
		return mView;
	}

	@Override
	public void initChildDate() {
		lv_content = (PullToRefreshListView) mView
				.findViewById(R.id.lv_content);
		netRequest();
		// reflashUI();
	}

	/**
	 * 网路请求
	 */
	private int page = 1;
	private TVlistAdapter mAdapter;
	private HttpHelp mHelp;

	private HttpHelp getHttpHelp() {
		if (mHelp == null) {
			mHelp = new HttpHelp();
		}
		return mHelp;
	}

	private void netRequest() {

		getHttpHelp().sendGet(NetworkConfig.getDTVList(page),
				DTVListBaen.class, new MyRequestCallBack<DTVListBaen>() {

					@Override
					public void onSucceed(DTVListBaen bean) {
						if (bean == null || bean.cont == null
								|| bean.cont.size() == 0) {
							lv_content.onRefreshComplete();
							return;
						}
						if (datas == null) {
							datas = new ArrayList<DTvCont>();
						}
						if (page == 1) {
							datas.clear();
						}
						page++;
						datas.addAll(bean.cont);

						reflashUI();
						lv_content.onRefreshComplete();
					}
				});
	}

	public void reflashUI() {
		if (mAdapter == null) {
			mAdapter = new TVlistAdapter(0);
			lv_content.setAdapter(mAdapter);
			lv_content.setOnRefreshListener(this);
			lv_content.setMode(Mode.BOTH);
			lv_content.setOnItemClickListener(this);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	private class TVlistAdapter extends AppBaseAdapter {

		public TVlistAdapter(int count) {
			super(count);

		}

		@Override
		public int getCount() {

			return datas == null ? 0 : datas.size();
		}

		@Override
		public int getItemViewType(int position) {
			return datas.get(position).type;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = holder.convertView(itemViewType);
			} else {
				holder = (ViewHolder) convertView.getTag();
				convertView = holder.getView(itemViewType);
			}
			DTvCont dTvCont = datas.get(position);
			holder.setData(dTvCont, itemViewType);
			return convertView;
		}
	}

	private class ViewHolder {
		// 第一类条目
		private View oneItemView;
		public final static int ONE_ITEM_VIEW = 1;
		// 第二类
		private View twoItemView;
		public final static int two_ITEM_VIEW = 2;
		/********************************/
		private CircleImageView iv_avater_one, iv_avater_two, iv_avater_three,
				iv_avater_four;
		private ImageView tv_bg;
		private TextView tv_film_dz, tv_film_name, tv_film_pl, tv_user_msg_one,
				tv_user_msg_two, tv_user_msg_three, tv_user_msg_four;

		/**
		 * 获取显示的view
		 * 
		 * @param convertView
		 * @param itemViewType
		 * @return
		 */
		public View convertView(int itemViewType) {
			View v = null;
			switch (itemViewType) {
			case ONE_ITEM_VIEW:
				v = UIUtils.inflate(R.layout.item_fragment_dt_tv_list);
				getViewId(v, itemViewType);
				oneItemView = v;
				break;
			case two_ITEM_VIEW:
				v = UIUtils.inflate(R.layout.item_err_msg);
				getViewId(v, itemViewType);
				twoItemView = v;
				break;

			}
			v.setTag(this);
			return v;

		}

		public void setData(DTvCont dTvCont, int itemViewType) {
			switch (itemViewType) {
			case ONE_ITEM_VIEW:
				getHttpHelp().showFitImage(tv_bg, dTvCont.pic);
				tv_film_dz.setText(dTvCont.dz + "");
				tv_film_pl.setText(dTvCont.bf + "");
				tv_film_name.setText("【" + dTvCont.name);
				tv_user_msg_one.setVisibility(View.GONE);
				tv_user_msg_two.setVisibility(View.GONE);
				tv_user_msg_three.setVisibility(View.GONE);
				tv_user_msg_four.setVisibility(View.GONE);
				iv_avater_one.setVisibility(View.GONE);
				iv_avater_two.setVisibility(View.GONE);
				iv_avater_three.setVisibility(View.GONE);
				iv_avater_four.setVisibility(View.GONE);
				List<TwoDTVCont> cont = dTvCont.cont;
				if (cont != null) {
					int size = cont.size();
					if (size >= 1) {
						TwoDTVCont twoDTVCont = cont.get(0);
						String msg1 = twoDTVCont.msg;
						if (TextUtils.isEmpty(msg1)) {

						} else {
							iv_avater_one.setVisibility(View.VISIBLE);
							tv_user_msg_one.setVisibility(View.VISIBLE);
							tv_user_msg_one.setText(msg1);
							getHttpHelp().showImage(iv_avater_one,
									twoDTVCont.face + "##");
						}

					}
					if (size >= 2) {
						TwoDTVCont twoDTVCont2 = cont.get(1);

						String msg2 = twoDTVCont2.msg;
						if (TextUtils.isEmpty(msg2)) {

						} else {
							iv_avater_two.setVisibility(View.VISIBLE);
							getHttpHelp().showImage(iv_avater_two,
									twoDTVCont2.face + "##");
							tv_user_msg_two.setVisibility(View.VISIBLE);
							tv_user_msg_two.setText(msg2);
						}
					}

					if (size >= 3) {
						TwoDTVCont twoDTVCont3 = cont.get(2);
						String msg3 = twoDTVCont3.msg;
						if (TextUtils.isEmpty(msg3)) {

						} else {
							iv_avater_three.setVisibility(View.VISIBLE);
							tv_user_msg_three.setVisibility(View.VISIBLE);
							tv_user_msg_three.setText(msg3);
							getHttpHelp().showImage(iv_avater_three,
									twoDTVCont3.face + "##");
						}

					}
					if (size >= 4) {
						TwoDTVCont twoDTVCont4 = cont.get(3);
						String msg4 = twoDTVCont4.msg;
						if (TextUtils.isEmpty(msg4)) {

						} else {
							iv_avater_four.setVisibility(View.VISIBLE);
							tv_user_msg_four.setVisibility(View.VISIBLE);
							tv_user_msg_four.setText(msg4);
							getHttpHelp().showImage(iv_avater_four,
									twoDTVCont4.face + "##");
						}

					}

				}
				break;

			}
		}

		private void getViewId(View v, int type) {
			switch (type) {
			case ONE_ITEM_VIEW:
				tv_bg = (ImageView) v.findViewById(R.id.tv_bg);
				tv_film_dz = (TextView) v.findViewById(R.id.tv_film_dz);
				tv_film_name = (TextView) v.findViewById(R.id.tv_film_name);
				tv_film_pl = (TextView) v.findViewById(R.id.tv_film_pl);
				tv_user_msg_one = (TextView) v
						.findViewById(R.id.tv_user_msg_one);
				tv_user_msg_two = (TextView) v
						.findViewById(R.id.tv_user_msg_two);
				tv_user_msg_three = (TextView) v
						.findViewById(R.id.tv_user_msg_three);
				tv_user_msg_four = (TextView) v
						.findViewById(R.id.tv_user_msg_four);
				iv_avater_one = (CircleImageView) v
						.findViewById(R.id.iv_avater_one);
				iv_avater_two = (CircleImageView) v
						.findViewById(R.id.iv_avater_two);
				iv_avater_three = (CircleImageView) v
						.findViewById(R.id.iv_avater_three);
				iv_avater_four = (CircleImageView) v
						.findViewById(R.id.iv_avater_four);
				break;

			}
		}

		public View getView(int type) {
			View v = null;
			switch (type) {
			case ONE_ITEM_VIEW:
				v = oneItemView;
				break;
			case two_ITEM_VIEW:
				v = twoItemView;
				break;

			}

			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (datas != null && datas.size() != 0) {
			DTvCont dTvCont = datas.get(position - 1);
			if (dTvCont != null) {
				Intent intent = new Intent(getActivity(),
						PlaySourceActivity.class);
				intent.putExtra(GlobalConstant.TV_ID, dTvCont.id);
				getActivity().startActivity(intent);
			}
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		page = 1;
		netRequest();

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		netRequest();

	}
	

}
