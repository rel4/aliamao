package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.DoyenBean;
import com.aliamauri.meat.bean.MyVideoBean;
import com.aliamauri.meat.bean.cont.MyVideoCont;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.play.PlayActivity;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageViewWhite;
import com.aliamauri.meat.view.HorizontalListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class DoyenDetailActvity extends BaseActivity implements OnClickListener {
	private HttpHelp httpHelp;
	private DoyenBean dBean;

	private PullToRefreshListView ptr_add_list;
	private RelativeLayout rl_progress;
	private CircleImageViewWhite ci_add_icon;
	private TextView tv_add_nickname;
	private TextView tv_add_agesex;
	private TextView tv_add_pland;
	private ImageView iv_od_blur;
	private ImageView iv_od_backicon;

	private List<MyVideoCont> historyList;
	private List<MyVideoCont> myVideoList;
	private HistoryAdapter historyAdapter;
	private ListAdapter listAdapter;

	private LinearLayout ll_add_buttom;
	private RelativeLayout rl_add_addattention;
	private RelativeLayout rl_add_addfriend;
	private TextView tv_add_addattention;
	private TextView tv_add_addfriend;

	private String id = "";
	private int page = 2;

	@Override
	protected View getRootView() {
		id = getIntent().getStringExtra(GlobalConstant.INTENT_ID);
		return View.inflate(UIUtils.getContext(),
				R.layout.activity_doyen_detail, null);
	}

	@Override
	protected void initView() {
		ptr_add_list = (PullToRefreshListView) findViewById(R.id.ptr_add_list);

		rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
		ci_add_icon = (CircleImageViewWhite) findViewById(R.id.ci_add_icon);
		tv_add_nickname = (TextView) findViewById(R.id.tv_add_nickname);
		tv_add_agesex = (TextView) findViewById(R.id.tv_add_agesex);
		tv_add_pland = (TextView) findViewById(R.id.tv_add_pland);
		iv_od_blur = (ImageView) findViewById(R.id.iv_od_blur);
		iv_od_backicon = (ImageView) findViewById(R.id.iv_od_backicon);
		rl_add_addattention = (RelativeLayout) findViewById(R.id.rl_add_addattention);
		rl_add_addfriend = (RelativeLayout) findViewById(R.id.rl_add_addfriend);
		tv_add_addattention = (TextView) findViewById(R.id.tv_add_addattention);
		tv_add_addfriend = (TextView) findViewById(R.id.tv_add_addfriend);

		ll_add_buttom = (LinearLayout) findViewById(R.id.ll_add_buttom);
		rl_add_addattention.setOnClickListener(this);
		rl_add_addfriend.setOnClickListener(this);
		rl_progress.setOnClickListener(this);
		iv_od_backicon.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		rl_progress.setVisibility(View.VISIBLE);
		getHttpHelp().sendGet(NetworkConfig.getDarenDeital(id),
				DoyenBean.class, new MyRequestCallBack<DoyenBean>() {

					@Override
					public void onSucceed(DoyenBean bean) {
						if (bean == null || bean.cont == null) {
							return;
						}
						if ("1".equals(bean.status)) {
							dBean = bean;
							initData(bean);
							initLVData();
							rl_progress.setVisibility(View.GONE);
						} else {
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
						}
					}
				});
	}

	protected void initData(DoyenBean bean) {
		if (PrefUtils.getString(GlobalConstant.USER_ID, "").equals(id)) {
			ll_add_buttom.setVisibility(View.GONE);
		} else {
			ll_add_buttom.setVisibility(View.VISIBLE);
		}
		if ("1".equals(bean.cont.isCare)) {
			rl_add_addattention.setSelected(true);
			tv_add_addattention.setText("取消关注");
		} else if ("0".equals(bean.cont.isCare)) {
			rl_add_addattention.setSelected(false);
			tv_add_addattention.setText("加关注");
		}
		if ("1".equals(bean.cont.isFriend)) {
			rl_add_addfriend.setSelected(true);
			tv_add_addfriend.setText("已是好友");
		} else if ("0".equals(bean.cont.isFriend)) {
			rl_add_addfriend.setSelected(false);
			tv_add_addfriend.setText("加好友");
		}
		if (bean.cont.face != null
				&& !"null".equals(bean.cont.face.toLowerCase())) {
			getHttpHelp().showImage(ci_add_icon, bean.cont.face + "##");
			getHttpHelp().showImage(bean.cont.face + "##", iv_od_blur);
		} else {
			getHttpHelp().showImage(ci_add_icon, "##");
			getHttpHelp().showImage("##", iv_od_blur);
			// iv_od_blur.setImageDrawable(IconStyle.BoxBlurFilter(BitmapFactory
			// .decodeResource(getResources(), R.drawable.default_logo)));
		}
		tv_add_agesex.setVisibility(View.VISIBLE);
		tv_add_nickname.setText(bean.cont.nickname != null ? bean.cont.nickname
				: "");
		if ("1".equals(bean.cont.sex)) {
			tv_add_agesex.setSelected(true);
		} else {
			tv_add_agesex.setSelected(false);
		}
		if (bean.cont.age != null && !"0".equals(bean.cont.age)
				&& !"".equals(bean.cont.age.trim())) {
			tv_add_agesex.setText(bean.cont.age + "岁");
		} else {
			tv_add_agesex.setText("保密");
		}
		if (bean.cont.pland != null) {
			tv_add_pland.setText(bean.cont.pland);
		}
		if (bean.cont.newestList != null) {
			if (historyList == null) {
				historyList = new ArrayList<MyVideoCont>();
			}
			historyList.addAll(bean.cont.newestList);
		}
		if (bean.cont.hotList != null) {
			if (myVideoList == null) {
				myVideoList = new ArrayList<MyVideoCont>();
			}
			myVideoList.addAll(bean.cont.hotList);
		}
	}

	private void initLVData() {
		if (listAdapter == null) {
			listAdapter = new ListAdapter();
			ptr_add_list.setAdapter(new ListAdapter());
			ptr_add_list.setMode(Mode.PULL_UP_TO_REFRESH);
			ptr_add_list.setOnRefreshListener(new OnRefreshListener2() {
				@Override
				public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				}

				@Override
				public void onPullUpToRefresh(PullToRefreshBase refreshView) {
					loadMode();
				}
			});
		} else {
			listAdapter.notifyDataSetChanged();
		}
	}

	private void loadMode() {
		getHttpHelp().sendGet(NetworkConfig.getUserHotVideo(id, page),
				MyVideoBean.class, new MyRequestCallBack<MyVideoBean>() {
					@Override
					public void onSucceed(MyVideoBean bean) {
						ptr_add_list.onRefreshComplete();
						if (bean == null) {
							UIUtils.showToast(UIUtils.getContext(), "没有更多了");
							return;
						}
						if ("1".equals(bean.status)) {
							if (bean.cont == null || bean.cont.size() <= 0) {
								UIUtils.showToast(UIUtils.getContext(), "没有更多了");
							} else {
								if (myVideoList == null) {
									myVideoList = new ArrayList<MyVideoCont>();
								}
								myVideoList.addAll(bean.cont);
								page++;
								initLVData();
							}
						} else {
							UIUtils.showToast(UIUtils.getContext(), "没有更多了");
						}
					}
				});
	}

	class HistoryAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			if (historyList == null) {
				return 0;
			}
			return historyList.size();
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
						R.layout.item_doyen_history_pic, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_imymovies_pic = (ImageView) convertView
						.findViewById(R.id.iv_imymovies_pic);
				viewHolder.tv_imymovies_title = (TextView) convertView
						.findViewById(R.id.tv_imymovies_title);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (historyList.get(position).pic != null) {
				getHttpHelp().showImage(viewHolder.iv_imymovies_pic,
						historyList.get(position).pic);
			}
			viewHolder.tv_imymovies_title
					.setText(historyList.get(position).name != null ? historyList
							.get(position).name : "");
			return convertView;
		}
	}

	class ListAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			if (myVideoList == null) {
				return 0;
			}
			int num = 0;
			if (myVideoList.size() % 2 == 0) {
				num = myVideoList.size() / 2;
			} else {
				num = myVideoList.size() / 2 + 1;
			}
			return num + 1;
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
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				if (position == 0) {
					convertView = View.inflate(UIUtils.getContext(),
							R.layout.item_doyen_myvideo_title, null);
					viewHolder = new ViewHolder();
					viewHolder.hlv_add_history = (HorizontalListView) convertView
							.findViewById(R.id.hlv_add_history);
					viewHolder.tv_idmt_myfansnum = (TextView) convertView
							.findViewById(R.id.tv_idmt_myfansnum);
					viewHolder.tv_idmt_attentionminum = (TextView) convertView
							.findViewById(R.id.tv_idmt_attentionminum);
					viewHolder.ll_idmt_myfansnum = (LinearLayout) convertView
							.findViewById(R.id.ll_idmt_myfansnum);
					viewHolder.ll_idmt_attentionminum = (LinearLayout) convertView
							.findViewById(R.id.ll_idmt_attentionminum);
					convertView.setTag(viewHolder);
				} else {
					convertView = View.inflate(UIUtils.getContext(),
							R.layout.item_doyen_myvideo_body, null);
					viewHolder = new ViewHolder();

					viewHolder.ll_idmb_image1 = (LinearLayout) convertView
							.findViewById(R.id.ll_idmb_image1);
					viewHolder.iv_idmb_image1 = (ImageView) convertView
							.findViewById(R.id.iv_idmb_image1);
					viewHolder.tv_idmb_image1 = (TextView) convertView
							.findViewById(R.id.tv_idmb_image1);

					viewHolder.ll_idmb_image2 = (LinearLayout) convertView
							.findViewById(R.id.ll_idmb_image2);
					viewHolder.iv_idmb_image2 = (ImageView) convertView
							.findViewById(R.id.iv_idmb_image2);
					viewHolder.tv_idmb_image2 = (TextView) convertView
							.findViewById(R.id.tv_idmb_image2);

					convertView.setTag(viewHolder);
				}
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				if (historyAdapter == null) {
					historyAdapter = new HistoryAdapter();
					viewHolder.hlv_add_history.setAdapter(historyAdapter);
					viewHolder.hlv_add_history
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									startActivity(new Intent(mActivity,
											PlayActivity.class).putExtra(
											GlobalConstant.PLAY_VIDEO_ID,
											historyList.get(position).id));

								}
							});

					viewHolder.tv_idmt_myfansnum
							.setText(dBean.cont.fansnum != null ? dBean.cont.fansnum
									: "0");
					viewHolder.tv_idmt_attentionminum
							.setText(dBean.cont.followsnum != null ? dBean.cont.followsnum
									: "0");
					viewHolder.ll_idmt_myfansnum
							.setOnClickListener(new MyOnclick(id, 1));
					viewHolder.ll_idmt_attentionminum
							.setOnClickListener(new MyOnclick(id, 0));
				} else {
					historyAdapter.notifyDataSetChanged();
				}
			} else {
				if (myVideoList.size() % 2 == 0) {
					getHttpHelp().showImage(viewHolder.iv_idmb_image1,
							myVideoList.get((position - 1) * 2).pic);
					viewHolder.tv_idmb_image1.setText(myVideoList
							.get((position - 1) * 2).name);

					getHttpHelp().showImage(viewHolder.iv_idmb_image2,
							myVideoList.get((position - 1) * 2 + 1).pic);
					viewHolder.tv_idmb_image2.setText(myVideoList
							.get((position - 1) * 2 + 1).name);

					viewHolder.ll_idmb_image1.setOnClickListener(new MyOnclick(
							myVideoList.get((position - 1) * 2).id, 2));
					viewHolder.ll_idmb_image2.setOnClickListener(new MyOnclick(
							myVideoList.get((position - 1) * 2 + 1).id, 2));
				} else {
					if (position == (getCount() - 1)) {
						getHttpHelp().showImage(viewHolder.iv_idmb_image1,
								myVideoList.get((position - 1) * 2).pic);
						viewHolder.tv_idmb_image1.setText(myVideoList
								.get((position - 1) * 2).name);

						viewHolder.ll_idmb_image1
								.setOnClickListener(new MyOnclick(myVideoList
										.get((position - 1) * 2).id, 2));

						viewHolder.ll_idmb_image2.setVisibility(View.GONE);
					} else {
						viewHolder.ll_idmb_image2.setVisibility(View.VISIBLE);
						getHttpHelp().showImage(viewHolder.iv_idmb_image1,
								myVideoList.get((position - 1) * 2).pic);
						viewHolder.tv_idmb_image1.setText(myVideoList
								.get((position - 1) * 2).name);

						getHttpHelp().showImage(viewHolder.iv_idmb_image2,
								myVideoList.get((position - 1) * 2 + 1).pic);
						viewHolder.tv_idmb_image2.setText(myVideoList
								.get((position - 1) * 2 + 1).name);

						viewHolder.ll_idmb_image1
								.setOnClickListener(new MyOnclick(myVideoList
										.get((position - 1) * 2).id, 2));
						viewHolder.ll_idmb_image2
								.setOnClickListener(new MyOnclick(myVideoList
										.get((position - 1) * 2 + 1).id, 2));
					}
				}
			}

			return convertView;
		}

	}

	class ViewHolder {
		public HorizontalListView hlv_add_history;

		public LinearLayout ll_idmb_image1;
		public TextView tv_idmb_image1;
		public ImageView iv_idmb_image1;

		public LinearLayout ll_idmb_image2;
		public TextView tv_idmb_image2;
		public ImageView iv_idmb_image2;

		public LinearLayout ll_idmt_attentionminum;
		public LinearLayout ll_idmt_myfansnum;
		public ImageView iv_imymovies_pic;
		public TextView tv_imymovies_title;
		public TextView tv_idmt_myfansnum;
		public TextView tv_idmt_attentionminum;
	}

	class MyOnclick implements OnClickListener {
		private String id;
		private int type;

		public MyOnclick(String id, int type) {
			this.id = id;
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			Intent i;
			if (type == 2) {
				i = new Intent(DoyenDetailActvity.this, PlayActivity.class);
				i.putExtra(GlobalConstant.PLAY_VIDEO_ID, id);
				startActivity(i);
			} else {
				i = new Intent(DoyenDetailActvity.this, AttentionActivity.class);
				i.putExtra(GlobalConstant.INTENT_DATA, type);
				i.putExtra(GlobalConstant.INTENT_ID, id);
				startActivity(i);
			}
		}
	}

	public HttpHelp getHttpHelp() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		return httpHelp;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_od_backicon:
			finish();
			break;
		case R.id.rl_add_addfriend:
			if (!rl_add_addfriend.isSelected()) {
				CmdManager.getInstance().addContact(id, "",
						new CmdManagerCallBack() {
							@Override
							public void onState(boolean isSucceed) {

								if (isSucceed) {
									UIUtils.showToast(getApplicationContext(),
											"发送请求成功~~");
									rl_add_addfriend.setSelected(true);
									tv_add_addfriend.setText("已发送");
								} else {
									UIUtils.showToast(getApplicationContext(),
											"请求失败~~");
								}

							}
						});
			}
			break;
		case R.id.rl_add_addattention:
			if (!rl_add_addattention.isSelected()) {
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
									rl_add_addattention.setSelected(true);
									tv_add_addattention.setText("取消关注");
								}
								UIUtils.showToast(UIUtils.getContext(),
										bean.msg);
							}
						});
			} else {
				getHttpHelp().sendGet(NetworkConfig.getdelfollows(id),
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
																	0) - 1);
									rl_add_addattention.setSelected(false);
									tv_add_addattention.setText("加关注");
								}
								UIUtils.showToast(UIUtils.getContext(),
										bean.msg);
							}
						});
			}
			break;
		default:
			break;
		}
	}
}
