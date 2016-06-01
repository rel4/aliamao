package com.aliamauri.meat.activity.search_activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.bean.CollectionBean;
import com.aliamauri.meat.bean.MyDataEditBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.play.PlayActivity;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.UIUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

/**
 * 左侧菜单-收藏功能页
 * 
 * @author Kevin
 * 
 */
public class CollectDetailActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {

	static PullToRefreshListView lv;

	private HttpHelp httpHelp;
	private CollectionBean collectionBean;
	private Integer collectpage = 2;

	private boolean clickEdit;// 判断是否点击了编辑按钮
	private MessgeAdapter mAdapter;
	

	private View ll_base_page_delete_or_cancel, ibtn_left_home_edit;

	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "收藏";
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
	protected View getRootView() {
		clickEdit = false;
		return UIUtils.inflate(R.layout.left_collect);
	}

	protected void initView() {
		findViewById(R.id.ibtn_left_home_edit).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.tv_title)).setText("收藏");
		lv = (PullToRefreshListView) findViewById(R.id.lv_left_collect);
		ll_base_page_delete_or_cancel = findViewById(R.id.ll_base_page_delete_or_cancel);
		findViewById(R.id.btn_base_page_cancel).setOnClickListener(this);
		findViewById(R.id.btn_base_page_cancel).setOnClickListener(this);
		findViewById(R.id.btn_base_page_all_delete).setOnClickListener(this);
		findViewById(R.id.ibtn_left_home_edit).setOnClickListener(this);
		findViewById(R.id.ibtn_left_home_back).setOnClickListener(this);
		ll_base_page_delete_or_cancel = findViewById(R.id.ll_base_page_delete_or_cancel);
		ibtn_left_home_edit = findViewById(R.id.ibtn_left_home_edit);
		ibtn_left_home_edit.setOnClickListener(this);
		ibtn_left_home_edit.setVisibility(View.VISIBLE);
		netWork();
	}

	private void netWork() {
		httpHelp = new HttpHelp();
		httpHelp.sendGet(NetworkConfig.getColection(1), CollectionBean.class,
				new MyRequestCallBack<CollectionBean>() {

					@Override
					public void onSucceed(CollectionBean bean) {
						if (bean == null) {
							return;
						}
						// LoadingSuccess();
						collectionBean = bean;
						setLVListener();// 设置监听器
						initListview();
					}
				});

	}

	private void setLVListener() {
		lv.setMode(Mode.PULL_FROM_END);

		// 设置下拉刷新监听器
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			// 下拉Pulling Down
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				new AsyncTask<Void, Void, Void>() {

					// 先运行这个
					@Override
					protected Void doInBackground(Void... arg0) {

						httpHelp.sendGet(
								NetworkConfig.getColection(collectpage),
								CollectionBean.class,
								new MyRequestCallBack<CollectionBean>() {

									@Override
									public void onSucceed(CollectionBean bean) {
										if (bean == null) {
											return;
										}
										if (bean.cont.size() < 2) {
											UIUtils.showToast(
													UIUtils.getContext(),
													"没有更多数据了");
										}
										collectionBean.cont.addAll(bean.cont);
										mAdapter.notifyDataSetChanged();

									}
								});

						return null;
					}

					// 最后运行
					@Override
					protected void onPostExecute(Void result) {
						lv.onRefreshComplete();

					};
				}.execute();
			}
		});

	}

	public void executeCancelOperate() {
		clickEdit = false;
		initCollectDetailView();
	}

	public void executeAllDeleteOperate() {

		if (collectionBean.cont.size() > 0) {
			httpHelp.sendGet(NetworkConfig.getColectionDel("deleteall", "all"),
					MyDataEditBean.class,
					new MyRequestCallBack<MyDataEditBean>() {

						@Override
						public void onSucceed(MyDataEditBean bean) {
							if (bean == null)
								return;

							if (Integer.parseInt(bean.status) == 1) {
								collectionBean.cont.clear();
								mAdapter.notifyDataSetChanged();
								UIUtils.showToast(UIUtils.getContext(), "删除成功");
							} else {
								UIUtils.showToast(UIUtils.getContext(),
										"请求网络失败，请再重新试一试");
							}
						}
					});
		} else {
			UIUtils.showToast(this, "没有记录了~~");
		}
	}

	public void executeEditOperate() {
		clickEdit = true;
		initCollectDetailView();
	}

	private void initListview() {
		mAdapter = new MessgeAdapter();
		lv.setAdapter(mAdapter);
		lv.setOnItemClickListener(this);
	}

	private class MessgeAdapter extends BaseAdapter {
		private ViewHolder holder;

		@Override
		public int getCount() {
			return collectionBean.cont.size();
		}

		@Override
		public Object getItem(int position) {
			return collectionBean.cont.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView != null) {
				holder = (ViewHolder) convertView.getTag();
			} else {

				holder = new ViewHolder();
				convertView = UIUtils.inflate(R.layout.item_record_state);
				holder.iv_cacheload_pic = (ImageView) convertView
						.findViewById(R.id.iv_item_record_icon);
				holder.tv_cacheload_name = (TextView) convertView
						.findViewById(R.id.tv_item_record_title);
				holder.tv_cacheload_time = (TextView) convertView
						.findViewById(R.id.tv_item_record_state);
				convertView.setTag(holder);
			}

			TextView tv_cacheload_redcircle = (TextView) convertView
					.findViewById(R.id.tv_record_redcircle);

			if (clickEdit) {
				tv_cacheload_redcircle.setVisibility(View.VISIBLE);
				deleteCollectItem(tv_cacheload_redcircle, position);
			} else {
				tv_cacheload_redcircle.setVisibility(View.GONE);
			}
			httpHelp.showImage(holder.iv_cacheload_pic,
					collectionBean.cont.get(position).pic);
			holder.tv_cacheload_name
					.setText(collectionBean.cont.get(position).film_name);
			holder.tv_cacheload_time
					.setText(collectionBean.cont.get(position).time);

			return convertView;
		}

		/**
		 * 在编辑的状态下删除收藏的条目
		 * 
		 * @param tv
		 * @param position
		 */
		public void deleteCollectItem(TextView tv, final int position) {
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String url1 = NetworkConfig.getColectionDel("one",
							collectionBean.cont.get(position).id);
					httpHelp.sendGet(url1, MyDataEditBean.class,
							new MyRequestCallBack<MyDataEditBean>() {

								@Override
								public void onSucceed(MyDataEditBean bean) {
									if (bean == null)
										return;

									if (Integer.parseInt(bean.status) == 1) {

										collectionBean.cont.remove(position);
										mAdapter.notifyDataSetChanged();
										UIUtils.showToast(
												CollectDetailActivity.this,
												"删除成功~");

									} else {
										UIUtils.showToast(UIUtils.getContext(),
												"请求网络失败，请再重新试一试");
									}
								}
							});
				}

			});
		}

	}

	static class ViewHolder {
		ImageView iv_cacheload_pic;
		TextView tv_cacheload_name;
		TextView tv_cacheload_time;
	}

	private void initCollectDetailView() {
		if (clickEdit) {
			lv.setOnItemClickListener(null);
		} else {
			lv.setOnItemClickListener(this);
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (collectionBean.cont.get(position - 1) != null) {
			Intent intent = new Intent(this, PlayActivity.class);
			intent.putExtra(GlobalConstant.TV_ID,
					collectionBean.cont.get(position - 1).film_id);
			startActivity(intent);
		} else {
			LogUtil.e("REEOR", "***********收藏界面获取视频id失败************");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_base_page_cancel:
			// 隐藏记录，缓存，收藏中的删除布局
			executeCancelOperate();
			ll_base_page_delete_or_cancel.setVisibility(View.GONE);
			ibtn_left_home_edit.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_base_page_all_delete:
			// 隐藏记录，缓存，收藏中的删除布局
			executeOperate();

			break;
		case R.id.ibtn_left_home_back:
			setExitSwichLayout();
			break;
		case R.id.ibtn_left_home_edit:
			// 显示记录，缓存，收藏中的删除布局
			executeEditOperate();
			ll_base_page_delete_or_cancel.setVisibility(View.VISIBLE);
			ibtn_left_home_edit.setVisibility(View.GONE);
			break;

		default:
			break;
		}

	}

	private void executeOperate() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("确定全部删除么？");
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 执行记录，缓存，收藏中的全部删除操作
				executeAllDeleteOperate();
				ibtn_left_home_edit.setVisibility(View.VISIBLE);
				ll_base_page_delete_or_cancel.setVisibility(View.GONE);
				dialog.cancel();
			}

		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();

	}

}
