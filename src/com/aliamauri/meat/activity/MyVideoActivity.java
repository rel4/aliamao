package com.aliamauri.meat.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.MyVideoBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.play.PlayActivity;
import com.aliamauri.meat.utils.UIUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

public class MyVideoActivity extends BaseActivity implements OnClickListener {

	private HttpHelp httpHelp;
	private MyVideoBean myVideoBean;

	private PullToRefreshGridView ptg_amymovies_list;
	private MyMoviesAdapter myMoviesAdapter;
	private RelativeLayout rl_progress;
	private String id = "";

	private int page = 0;

	@Override
	protected View getRootView() {
		id = getIntent().getStringExtra(GlobalConstant.INTENT_ID);
		return View.inflate(UIUtils.getContext(), R.layout.actvity_mymovies,
				null);
	}

	public HttpHelp getHttpHelp() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		return httpHelp;
	}

	@Override
	protected String getCurrentTitle() {
		return "我的作品";
	}

	public void delMyMovies(String id) {
		rl_progress.setVisibility(View.VISIBLE);
		getHttpHelp().sendGet(NetworkConfig.getUserVideoDel(id),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						rl_progress.setVisibility(View.GONE);
						if (bean == null) {
							return;
						}
						if ("1".equals(bean.status)) {
							initNet();
						} else {
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
						}
					}
				});

	}

	@Override
	protected void initNet() {
		rl_progress.setVisibility(View.VISIBLE);
		getHttpHelp().sendGet(NetworkConfig.getUserVideo(id, page),
				MyVideoBean.class, new MyRequestCallBack<MyVideoBean>() {

					@Override
					public void onSucceed(MyVideoBean bean) {
						rl_progress.setVisibility(View.GONE);
						if (bean == null) {
							return;
						}
						if ("1".equals(bean.status)) {
							myVideoBean = bean;
							initLVData();
							page++;
						} else {
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
						}
					}
				});
		super.initNet();
	}

	protected void initLVData() {
		if (myMoviesAdapter == null) {
			myMoviesAdapter = new MyMoviesAdapter();
			ptg_amymovies_list.setAdapter(myMoviesAdapter);
			ptg_amymovies_list
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, final int position, long id) {
							new AlertDialog.Builder(MyVideoActivity.this)
									.setMessage(
											"是否确定删除"
													+ myVideoBean.cont
															.get(position).name)
									.setPositiveButton(
											R.string.ok,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													arg0.dismiss();
													delMyMovies(myVideoBean.cont
															.get(position).id);
												}
											})
									.setNegativeButton(R.string.cancel, null)
									.setCancelable(false).show();
						}
					});
			ptg_amymovies_list.setOnRefreshListener(new OnRefreshListener2() {
				@Override
				public void onPullDownToRefresh(PullToRefreshBase refreshView) {

				}

				@Override
				public void onPullUpToRefresh(PullToRefreshBase refreshView) {
					initNet();
				}
			});
		} else {
			myMoviesAdapter.notifyDataSetInvalidated();
		}
	}

	@Override
	protected void initView() {
		ptg_amymovies_list = (PullToRefreshGridView) findViewById(R.id.ptg_amymovies_list);
		rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
	}

	@Override
	protected void setListener() {
		rl_progress.setOnClickListener(this);
	}

	class MyMoviesAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			if (myVideoBean == null || myVideoBean.cont == null) {
				return 0;
			}
			return myVideoBean.cont.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(UIUtils.getContext(),
						R.layout.item_mymovies_greywhite, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_imymovies_pic = (ImageView) convertView
						.findViewById(R.id.iv_imymovies_pic);
				viewHolder.tv_imymovies_title = (TextView) convertView
						.findViewById(R.id.tv_imymovies_title);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			getHttpHelp().showImage(viewHolder.iv_imymovies_pic,
					myVideoBean.cont.get(position).pic);
			viewHolder.tv_imymovies_title.setText(myVideoBean.cont
					.get(position).name);

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					new AlertDialog.Builder(MyVideoActivity.this)
							.setMessage(
									"是否确定删除"
											+ myVideoBean.cont.get(position).name)
							.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											arg0.dismiss();
											delMyMovies(myVideoBean.cont
													.get(position).id);
										}
									}).setNegativeButton(R.string.cancel, null)
							.setCancelable(false).show();
					return false;
				}
			});
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MyVideoActivity.this,
							PlayActivity.class);
					intent.putExtra(GlobalConstant.PLAY_VIDEO_ID,
							myVideoBean.cont.get(position).id);
					MyVideoActivity.this.startActivity(intent);
				}
			});
			return convertView;
		}

	}

	class ViewHolder {
		public ImageView iv_imymovies_pic;
		public TextView tv_imymovies_title;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}
}
