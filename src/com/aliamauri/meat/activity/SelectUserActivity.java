package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.TalkListBean;
import com.aliamauri.meat.bean.TelsFriendBean;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.RefreshListView;
import com.aliamauri.meat.view.RefreshListView.OnRefreshListener;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

/**
 * 查找用户
 * 
 * @author ych
 * 
 */
public class SelectUserActivity extends BaseActivity implements OnClickListener {
	private HttpHelp httpHelp;

	private TextView tv_selectuser_condition;
	private TextView tv_selectuser_group;
	private RefreshListView rlv_selectuser_all;
	private AllAdapter allAdapter;

	private RelativeLayout rl_su_recommend;// 通讯录的标头

	// private RelativeLayout rl_selectuseritem_showall;
	// private int addressCount = 2;

	@Override
	protected View getRootView() {
		View view = View.inflate(SelectUserActivity.this, R.layout.select_user,
				null);
		httpHelp = new HttpHelp();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "查找用户";
	}

	@Override
	protected void initView() {
		tv_selectuser_condition = (TextView) findViewById(R.id.tv_selectuser_condition);
		tv_selectuser_group = (TextView) findViewById(R.id.tv_selectuser_group);

		rlv_selectuser_all = (RefreshListView) findViewById(R.id.rlv_selectuser_all);

		rl_su_recommend = (RelativeLayout) findViewById(R.id.rl_su_recommend);
		// rl_selectuseritem_showall = (RelativeLayout)
		// findViewById(R.id.rl_selectuseritem_showall);
		setAddressAdapter();
	}

	@Override
	protected void setListener() {
		tv_selectuser_condition.setOnClickListener(this);
		tv_selectuser_group.setOnClickListener(this);
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
	protected void initNet() {
		netWork();
	}

	private int chatGroupPage = 2;
	private TelsFriendBean telsFriendBean;
	// private TalkListBean tlBean;
	private final static int Pagesize = 5;

	private void netWork() {
		RequestParams params = new RequestParams();
		String tels = "17488881001,17488881002,17488881003,17488881004,17488881005,17488881006";
		// try {
		// tels = PhoneInfoUtils.getPhone(getContentResolver());
		// } catch (Exception e) {
		// UIUtils.showToast(UIUtils.getContext(), "没有获取到权限");
		// e.printStackTrace();
		// }
		// System.out.println("tesl=" + tels);
		params.addBodyParameter("tels", tels);
		httpHelp.sendPost(NetworkConfig.getMyfriendsByTXL(), params,
				TelsFriendBean.class,

				new MyRequestCallBack<TelsFriendBean>() {

					@Override
					public void onSucceed(TelsFriendBean bean) {
						if (bean == null)
							return;
						if ("1".equals(bean.status)) {
							telsFriendBean = bean;
							setAddressAdapter();
						} else {
							UIUtils.showToast(UIUtils.getContext(), "网络请求失败");
						}

					}
				});

	}

	private void setAddressAdapter() {
		if (allAdapter == null) {
			allAdapter = new AllAdapter();
			rlv_selectuser_all.setAdapter(allAdapter);
			rlv_selectuser_all.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onLoadMore() {
					loadMoreGroup();
				}

			});
		} else {
			allAdapter.notifyDataSetChanged();
		}
	}

	private void loadMoreGroup() {
		httpHelp.sendGet(NetworkConfig.getChatGroupList(Pagesize, "1",
				chatGroupPage, ""), TalkListBean.class,

		new MyRequestCallBack<TalkListBean>() {

			@Override
			public void onSucceed(TalkListBean bean) {
				if (bean == null) {
					rlv_selectuser_all.onRefreashFinish();
					return;
				}
				if (bean.cont.size() > 0) {
					rlv_selectuser_all.onRefreashFinish();
					if (telsFriendBean.cont.chatgroup == null) {
						telsFriendBean.cont.chatgroup = bean.cont;
					} else {
						telsFriendBean.cont.chatgroup.addAll(bean.cont);
					}
					chatGroupPage++;
					allAdapter.notifyDataSetChanged();
				} else {
					rlv_selectuser_all.onRefreashFinish();
					UIUtils.showToast(UIUtils.getContext(), "没有更多了");
				}
			}
		});
	}

	private final static int ADDRESS = 1;
	private final static int CENTER = 2;
	private final static int GROUP = 3;

	private int groupsize = 0;// 判断有没有推荐的群，来决定是否显示标头
	private int friendsize = 0;// 判断是否有好友，来决定是否显示中间标头

	private boolean showAll = false;// 是否显示全部通讯录好友

	class AllAdapter extends BaseAdapter {
		private AllHolder allHolder;

		@Override
		public int getCount() {
			// tfbean 电话好友。。tlbean话题列表
			if (telsFriendBean != null && telsFriendBean.cont != null
					&& telsFriendBean.cont.tongxunlu != null
					&& telsFriendBean.cont.tongxunlu.size() > 0) {
				if (telsFriendBean.cont.tongxunlu.size() > 5) {
					if (showAll) {
						friendsize = telsFriendBean.cont.tongxunlu.size();
					} else {
						friendsize = 5;
					}
				} else {
					friendsize = telsFriendBean.cont.tongxunlu.size();
				}
			} else {
				friendsize = 0;
			}

			if (telsFriendBean != null && telsFriendBean.cont != null
					&& telsFriendBean.cont.chatgroup != null
					&& telsFriendBean.cont.chatgroup.size() > 0) {
				groupsize = telsFriendBean.cont.chatgroup.size();
			} else {
				groupsize = 0;
			}
			if (friendsize + groupsize <= 0) {
				return 0;
			} else {
				return friendsize + groupsize + 1;
			}
		}

		@Override
		public int getItemViewType(int position) {
			if (position < friendsize) {
				return ADDRESS;
			} else if (position == friendsize) {
				return CENTER;
			} else {
				return GROUP;
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			long id = 0;
			switch (getItemViewType(position)) {
			case ADDRESS:
				id = position;
				break;
			case GROUP:
				id = position - friendsize;
				break;
			default:
				break;
			}
			return id;
		}

		public int getMyId(int position) {
			int id = 0;
			switch (getItemViewType(position)) {
			case ADDRESS:
				id = position;
				break;
			case GROUP:
				id = position - friendsize - 1;
				break;
			default:
				break;
			}
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (friendsize <= 0) {
				rl_su_recommend.setVisibility(View.GONE);
			} else {
				rl_su_recommend.setVisibility(View.VISIBLE);
			}
			// if (convertView == null) {
			switch (getItemViewType(position)) {
			case ADDRESS:
				convertView = initAdapterView(convertView);
				initAddressData(position);
				break;
			case CENTER:
				convertView = initCenterView(convertView);
				break;
			case GROUP:
				convertView = initAdapterView(convertView);
				initGroupData(position);
				break;
			default:
				break;
			}
			// convertView.setTag(allHolder);
			// }
			// else {
			// allHolder = (AllHolder) convertView.getTag();
			// }
			// switch (getItemViewType(position)) {
			// case ADDRESS:
			// initAddressData(position);
			// break;
			// case GROUP:
			// initGroupData(position);
			// break;
			// default:
			// break;
			// }

			return convertView;
		}

		private void initGroupData(int position) {
			httpHelp.showImage(allHolder.ci_selectuseritem_icon,
					telsFriendBean.cont.chatgroup.get(getMyId(position)).pic
							+ "##");
			allHolder.tv_selectuseritem_name
					.setText(telsFriendBean.cont.chatgroup
							.get(getMyId(position)).name);
			allHolder.tv_selectuseritem_introduction
					.setVisibility(View.VISIBLE);
			allHolder.tv_selectuseritem_introduction
					.setText(telsFriendBean.cont.chatgroup
							.get(getMyId(position)).desc);

		}

		private void initAddressData(int position) {
			httpHelp.showImage(allHolder.ci_selectuseritem_icon,
					telsFriendBean.cont.tongxunlu.get(getMyId(position)).face
							+ "##");
			allHolder.tv_selectuseritem_name
					.setText(telsFriendBean.cont.tongxunlu
							.get(getMyId(position)).nickname);
		}

		private View initCenterView(View convertView) {
			convertView = View.inflate(SelectUserActivity.this,
					R.layout.select_user_item_center, null);
			allHolder = new AllHolder();
			allHolder.rl_sui_recommend = (RelativeLayout) convertView
					.findViewById(R.id.rl_sui_recommend);
			allHolder.rl_selectuseritem_showall = (RelativeLayout) convertView
					.findViewById(R.id.rl_selectuseritem_showall);
			allHolder.tv_sui_title = (TextView) convertView
					.findViewById(R.id.tv_sui_title);
			allHolder.tv_sui_title.setText("推荐话题");

			if (friendsize <= 0) {
				allHolder.rl_selectuseritem_showall.setVisibility(View.GONE);
			} else {
				if (showAll) {
					allHolder.rl_selectuseritem_showall
							.setVisibility(View.GONE);
				} else {
					allHolder.rl_selectuseritem_showall
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									showAll = true;
									allAdapter.notifyDataSetChanged();
								}
							});
				}
			}

			if (groupsize <= 0) {
				allHolder.rl_sui_recommend.setVisibility(View.GONE);
			} else {
				allHolder.rl_sui_recommend.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		private View initAdapterView(View convertView) {
			convertView = View.inflate(SelectUserActivity.this,
					R.layout.select_user_item, null);
			allHolder = new AllHolder();
			allHolder.ci_selectuseritem_icon = (CircleImageView) convertView
					.findViewById(R.id.ci_selectuseritem_icon);
			allHolder.tv_selectuseritem_name = (TextView) convertView
					.findViewById(R.id.tv_selectuseritem_name);
			allHolder.tv_selectuseritem_introduction = (TextView) convertView
					.findViewById(R.id.tv_selectuseritem_introduction);
			convertView.setTag(allHolder);
			return convertView;
		}
	}

	class AllHolder {
		private CircleImageView ci_selectuseritem_icon;
		private TextView tv_selectuseritem_name;
		private TextView tv_selectuseritem_introduction;
		private RelativeLayout rl_sui_recommend;
		private RelativeLayout rl_selectuseritem_showall;
		private TextView tv_sui_title;
	}

	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.tv_selectuser_condition:
			startActivity(new Intent(SelectUserActivity.this,
					SelectConditionActivity.class));
			break;
		case R.id.tv_selectuser_group:
			UIUtils.showToast(UIUtils.getContext(), "敬请期待");
			// i = new Intent(SelectUserActivity.this,
			// TalkListSelectActivity.class);
			//
			// startActivity(i);
			break;
		case R.id.rl_selectuseritem_showall:
			showAll = true;
			allAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

}
