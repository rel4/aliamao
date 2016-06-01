package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.AttentionBean;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CaculationUtils;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class AttentionActivity extends BaseActivity implements OnClickListener {

	private HttpHelp httpHelp;

	private RelativeLayout rl_progress;
	private PullToRefreshListView ptr_attention_list;

	private AttentionBean aBean;

	private int type;
	private String id;
	private int page = 1;

	private AttentionAdapter attentionAdapter;

	@Override
	protected View getRootView() {
		type = getIntent().getIntExtra(GlobalConstant.INTENT_DATA, 0);
		id = getIntent().getStringExtra(GlobalConstant.INTENT_ID);
		return View.inflate(UIUtils.getContext(), R.layout.activity_attention,
				null);
	}

	@Override
	protected void initView() {
		rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
		ptr_attention_list = (PullToRefreshListView) findViewById(R.id.ptr_attention_list);
		rl_progress.setOnClickListener(this);
	}

	@Override
	protected String getCurrentTitle() {
		if (type == 0) {
			return "全部关注";
		} else {
			return "全部粉丝";
		}
	}

	@Override
	protected void onStart() {
		// initMyNet();
		super.onStart();
	}

	@Override
	protected void onResume() {
		page = 1;
		initMyNet();
		super.onResume();
	}

	private HttpHelp getHttpHelp() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		return httpHelp;
	}

	private void initMyNet() {
		if (page <= 1) {
			rl_progress.setVisibility(View.VISIBLE);
		}
		if (type == 0) {
			getHttpHelp().sendGet(NetworkConfig.getfollows(id, page),
					AttentionBean.class,
					new MyRequestCallBack<AttentionBean>() {

						@Override
						public void onSucceed(AttentionBean bean) {
							ptr_attention_list.onRefreshComplete();
							if (bean == null) {
								return;
							}
							if ("1".equals(bean.status)) {

								if (page > 1) {
									if (aBean == null || aBean.cont == null) {
										aBean = bean;
									} else {
										if (bean.cont.size() <= 0) {
											UIUtils.showToast(
													UIUtils.getContext(),
													"没有更多了");
											return;
										} else {
											aBean.cont.addAll(bean.cont);
										}
									}
								} else {
									rl_progress.setVisibility(View.GONE);
									aBean = bean;
								}
								initLVData();
								page++;
							} else {
								UIUtils.showToast(UIUtils.getContext(),
										bean.msg);
							}
						}
					});
		} else {

			getHttpHelp().sendGet(NetworkConfig.getfans(id, page),
					AttentionBean.class,
					new MyRequestCallBack<AttentionBean>() {

						@Override
						public void onSucceed(AttentionBean bean) {
							ptr_attention_list.onRefreshComplete();
							if (bean == null) {
								return;
							}
							if ("1".equals(bean.status)) {
								if (CheckUtils.getInstance().isNumber(bean.num)) {
									PrefUtils.setInt(
											GlobalConstant.USER_FANSCOUNT,
											Integer.parseInt(bean.num));
								}
								if (page > 1) {
									if (aBean == null || aBean.cont == null) {
										aBean = bean;
									} else {
										aBean.cont.addAll(aBean.cont);
									}
								} else {
									rl_progress.setVisibility(View.GONE);
									aBean = bean;
								}
								initLVData();
								page++;
							} else {
								UIUtils.showToast(UIUtils.getContext(),
										bean.msg);
							}
						}
					});
		}
	}

	protected void initLVData() {
		if (attentionAdapter == null) {
			attentionAdapter = new AttentionAdapter();
			ptr_attention_list.setAdapter(attentionAdapter);
			ptr_attention_list
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent i;
							if ("0".equals(aBean.cont.get(position - 1).darentype)) {
								i = new Intent(AttentionActivity.this,
										OtherDataActivity.class);
								i.putExtra(GlobalConstant.COMMENT_ADD_FRIEND,
										aBean.cont.get(position - 1).uid);
								startActivity(i);
							} else {
								i = new Intent(AttentionActivity.this,
										DoyenDetailActvity.class);
								i.putExtra(GlobalConstant.INTENT_ID,
										aBean.cont.get(position - 1).uid);
								startActivity(i);
							}
						}
					});
			ptr_attention_list.setMode(Mode.PULL_FROM_END);
			ptr_attention_list.setOnRefreshListener(new OnRefreshListener2() {

				@Override
				public void onPullDownToRefresh(PullToRefreshBase refreshView) {

				}

				@Override
				public void onPullUpToRefresh(PullToRefreshBase refreshView) {
					initMyNet();
				}
			});
		} else {
			attentionAdapter.notifyDataSetChanged();
		}
	}

	class AttentionAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			if (aBean == null || aBean.cont == null) {
				return 0;
			}
			return aBean.cont.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(UIUtils.getContext(),
						R.layout.item_attention, null);
				viewHolder = new ViewHolder();
				viewHolder.civ_iattention_headicon = (CircleImageView) convertView
						.findViewById(R.id.civ_iattention_headicon);

				viewHolder.tv_iattention_nickname = (TextView) convertView
						.findViewById(R.id.tv_iattention_nickname);
				viewHolder.tv_iattention_sexage = (TextView) convertView
						.findViewById(R.id.tv_iattention_sexage);
				viewHolder.tv_iattention_pland = (TextView) convertView
						.findViewById(R.id.tv_iattention_pland);
				viewHolder.tv_iattention_add = (TextView) convertView
						.findViewById(R.id.tv_iattention_add);
				viewHolder.ll_iattention_add = (LinearLayout) convertView
						.findViewById(R.id.ll_iattention_add);
				viewHolder.tv_iattention_intro = (TextView) convertView
						.findViewById(R.id.tv_iattention_intro);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			getHttpHelp().showImage(viewHolder.civ_iattention_headicon,
					aBean.cont.get(position).face);
			viewHolder.tv_iattention_nickname
					.setText(aBean.cont.get(position).nickname);
			if ("1".equals(aBean.cont.get(position).sex)) {
				viewHolder.tv_iattention_sexage.setSelected(true);
			} else {
				viewHolder.tv_iattention_sexage.setSelected(false);
			}
			if ("0".equals(CaculationUtils.calculateDatePoor(aBean.cont
					.get(position).birth))) {

			} else {
				viewHolder.tv_iattention_sexage.setText(CaculationUtils
						.calculateDatePoor(aBean.cont.get(position).birth));
			}
			viewHolder.tv_iattention_pland
					.setText(aBean.cont.get(position).province);
			viewHolder.tv_iattention_intro
					.setText(aBean.cont.get(position).signature);
			if ("1".equals(aBean.cont.get(position).isfollow)) {
				viewHolder.ll_iattention_add.setSelected(true);
				viewHolder.tv_iattention_add.setText("取消关注");
			} else {
				viewHolder.ll_iattention_add.setSelected(false);
				viewHolder.tv_iattention_add.setText("加关注");
			}
			viewHolder.ll_iattention_add.setOnClickListener(new MyOnclick(
					aBean.cont.get(position).uid, position));
			return convertView;
		}
	}

	class MyOnclick implements OnClickListener {
		private String id;
		private int position;

		public MyOnclick(String id, int position) {
			this.id = id;
			this.position = position;
		}

		@Override
		public void onClick(final View v) {
			if (v.isSelected()) {
				getHttpHelp().sendGet(NetworkConfig.getdelfollows(id),
						BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

							@Override
							public void onSucceed(BaseBaen bean) {
								if (bean == null) {
									return;
								}
								if ("1".equals(bean.status)) {
									if (type == 0) {
										PrefUtils
												.setInt(GlobalConstant.USER_FOLLOWSCOUNT,
														PrefUtils
																.getInt(GlobalConstant.USER_FOLLOWSCOUNT,
																		0) - 1);
										aBean.cont.remove(position);
										initLVData();
									} else {
										PrefUtils
												.setInt(GlobalConstant.USER_FOLLOWSCOUNT,
														PrefUtils
																.getInt(GlobalConstant.USER_FOLLOWSCOUNT,
																		0) - 1);
										v.setSelected(false);
										TextView tv = (TextView) v
												.findViewById(R.id.tv_iattention_add);
										tv.setText("加关注");
										aBean.cont.get(position).isfollow = "0";
									}
								}
								UIUtils.showToast(UIUtils.getContext(),
										bean.msg);
							}
						});

			} else {
				getHttpHelp().sendGet(NetworkConfig.getgoFollow(id),
						BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

							@Override
							public void onSucceed(BaseBaen bean) {
								if (bean == null) {
									return;
								}
								if ("1".equals(bean.status)) {
									PrefUtils
											.setInt(GlobalConstant.USER_FOLLOWSCOUNT,
													PrefUtils
															.getInt(GlobalConstant.USER_FOLLOWSCOUNT,
																	0) + 1);
									v.setSelected(true);
									TextView tv = (TextView) v
											.findViewById(R.id.tv_iattention_add);
									tv.setText("取消关注");
									aBean.cont.get(position).isfollow = "1";
								}
								UIUtils.showToast(UIUtils.getContext(),
										bean.msg);
							}
						});
			}
		}

	}

	class ViewHolder {
		public CircleImageView civ_iattention_headicon;
		public TextView tv_iattention_nickname;
		public TextView tv_iattention_sexage;
		public TextView tv_iattention_pland;
		public LinearLayout ll_iattention_add;
		public TextView tv_iattention_add;
		public TextView tv_iattention_intro;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

}
