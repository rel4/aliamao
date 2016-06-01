package com.aliamauri.meat.activity.search_activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.adapter.AppBaseAdapter;
import com.aliamauri.meat.bean.NetworkMenuDetailPagerBase;
import com.aliamauri.meat.bean.NetworkMenuDetailPagerBase.Cont.Recdata;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.UIUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

/**
 * 菜单详情页-必看推荐页
 * 
 * @author Kevin
 * 
 */
public class RecommendMenuDetailActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener<ListView>, OnItemClickListener {

	private HttpHelp mHttpHelp; // 网络请求工具类
	private NetworkMenuDetailPagerBase mBean; // 推荐页的javabean
	private PullToRefreshListView lv_recommend_show_app;
	private List<Recdata> recdatas;
	private RecommendAadpter recommendAadpter;

	@Override
	protected View getRootView() {
		return UIUtils.inflate(R.layout.left_recommend);
	}

	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}
	@Override
	protected void onResume() {
		super.onResume();
		  MobclickAgent.onResume(this);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		 MobclickAgent.onPause(this);
	}
	
	@Override
	public void initView() {
		lv_recommend_show_app = (PullToRefreshListView) rootView
				.findViewById(R.id.lv_recommend_show_app);
		lv_recommend_show_app.setMode(Mode.PULL_FROM_END);
		lv_recommend_show_app.setOnRefreshListener(this);
		lv_recommend_show_app.setOnItemClickListener(this);
		mHttpHelp = new HttpHelp();
	}

	/**
	 * 初始化网络请求
	 */
	protected void initNet() {
		mHttpHelp.sendGet(NetworkConfig.getRecommendIndex(),
				NetworkMenuDetailPagerBase.class,
				new MyRequestCallBack<NetworkMenuDetailPagerBase>() {

					@Override
					public void onSucceed(NetworkMenuDetailPagerBase bean) {
						if (bean == null || bean.cont.list == null
								|| bean.cont.list.size() == 0) {
							return;
						}
						if (recdatas == null) {
							recdatas = new ArrayList<Recdata>();
						} else {
							recdatas.clear();
						}

						mBean = bean;
						recdatas.addAll(bean.cont.list);
						setadpter();

					}

				});
	}

	protected void setadpter() {
		recommendAadpter = new RecommendAadpter(recdatas.size());
		lv_recommend_show_app.setAdapter(recommendAadpter);

	}

	public static final int HEAD_VIEW = 0;
	public static final int DUFULT_VIEW = 1;

	private class RecommendAadpter extends AppBaseAdapter {

		public RecommendAadpter(int count) {
			super(count);
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return HEAD_VIEW;
			}
			return DUFULT_VIEW;

		}

		@Override
		public int getCount() {
			return recdatas.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			ViewHeadHolder headHolder = null;
			DufultViewHolder dufultHolder = null;
			if (convertView == null) {
				switch (itemViewType) {
				case HEAD_VIEW:
					convertView = UIUtils.inflate(R.layout.item_recommend_head);
					headHolder = new ViewHeadHolder();
					headHolder.setView(convertView);
					break;
				case DUFULT_VIEW:
					convertView = UIUtils.inflate(R.layout.item_recommend);
					dufultHolder = new DufultViewHolder();
					dufultHolder.setView(convertView);
					break;
				}
			} else {
				switch (itemViewType) {
				case HEAD_VIEW:
					headHolder = (ViewHeadHolder) convertView.getTag();
					break;
				case DUFULT_VIEW:
					dufultHolder = (DufultViewHolder) convertView.getTag();
					break;

				}

			}
			switch (itemViewType) {
			case HEAD_VIEW:
				headHolder.setDate();
				break;
			case DUFULT_VIEW:
				dufultHolder.setDate(position - 1);
				break;

			}

			return convertView;
		}
	}

	private class ViewHeadHolder {

		private ImageView lv_head_iocn;
		private TextView tv_head_content, tv_reader_num, tv_head_time;

		public void setView(View convertView) {
			lv_head_iocn = (ImageView) convertView
					.findViewById(R.id.lv_head_iocn);
			tv_head_content = (TextView) convertView
					.findViewById(R.id.tv_head_content);
			tv_reader_num = (TextView) convertView
					.findViewById(R.id.tv_reader_num);
			tv_head_time = (TextView) convertView
					.findViewById(R.id.tv_head_time);
			convertView.setTag(this);
		}

		public void setDate() {
			Recdata recdata = mBean.cont.recdata;
			if (mHttpHelp != null) {
				mHttpHelp.showImage(lv_head_iocn, recdata.pic1);
			}
			tv_head_content.setText(recdata.name + "——" + recdata.shortname);
			tv_reader_num.setText(recdata.click + "阅读");
			tv_head_time.setText(recdata.updatetime);
		}

	}

	private class DufultViewHolder {

		private TextView tv_content, tv_update_time, tv_reader_num;
		private ImageView lv_pic_1, lv_pic_2, lv_pic_3;

		public void setView(View convertView) {
			tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			tv_reader_num = (TextView) convertView
					.findViewById(R.id.tv_reader_num);
			tv_update_time = (TextView) convertView
					.findViewById(R.id.tv_update_time);
			lv_pic_1 = (ImageView) convertView.findViewById(R.id.lv_pic_1);
			lv_pic_2 = (ImageView) convertView.findViewById(R.id.lv_pic_2);
			lv_pic_3 = (ImageView) convertView.findViewById(R.id.lv_pic_3);
			convertView.setTag(this);

		}

		public void setDate(int position) {
			Recdata recdata = recdatas.get(position);
			tv_content.setText(recdata.name + "——" + recdata.shortname);
			tv_update_time.setText(recdata.updatetime);
			tv_reader_num.setText(recdata.click + "阅读");
			if (mHttpHelp != null) {
				mHttpHelp.showImage(lv_pic_1, recdata.pic1);
				mHttpHelp.showImage(lv_pic_2, recdata.pic2);
				mHttpHelp.showImage(lv_pic_3, recdata.pic3);
			}
		}

	}

	@Override
	public String getCurrentTitle() {
		return "必看推荐";
	}

	private int page = 2;

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		mHttpHelp.sendGet(NetworkConfig.getRecommendIndex() + "&page=" + page,
				NetworkMenuDetailPagerBase.class,
				new MyRequestCallBack<NetworkMenuDetailPagerBase>() {

					@Override
					public void onSucceed(NetworkMenuDetailPagerBase bean) {
						if (bean == null || bean.cont.list == null
								|| bean.cont.list.size() == 0) {
							lv_recommend_show_app.onRefreshComplete();
							UIUtils.showToast(UIUtils.getContext(), "没有更多数据了");
							return;
						}

						if (recdatas != null) {
							recdatas.addAll(bean.cont.list);
							if (recommendAadpter != null) {
								recommendAadpter.notifyDataSetInvalidated();
							}
						}
						lv_recommend_show_app.onRefreshComplete();
						page++;
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String recId = "";
		String name = "";
		if (position == 1) {
			recId = mBean.cont.recdata.id;
			name = mBean.cont.recdata.name;
		} else {
			recId = recdatas.get(position - 2).id;
			name = recdatas.get(position - 2).name;
		}
		Intent intent = new Intent(mActivity, RecommendDetailActivity.class);
		intent.putExtra(GlobalConstant.INTENT_ID, recId);
		intent.putExtra(GlobalConstant.INTENT_NAME, name);
		mActivity.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
