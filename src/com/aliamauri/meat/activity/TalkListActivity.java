package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.TalkListBean;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.RefreshListView;
import com.aliamauri.meat.view.RefreshListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 话题列表我加入的和我创建的
 * 
 * @author ych
 * 
 */
public class TalkListActivity extends BaseActivity implements OnClickListener {
	private HttpHelp httpHelp;
	private TalkListBean myBean;
	private TalkListBean joinBean;
	private TextView tv_title_title;
	private RefreshListView rlv_talk_all;
	private TalkListAdapter talkListAdapter;
	private LinearLayout ll_title_talk;

	private TextView tv_talk_add;// 标头的图标
	private TextView tv_talk_create;// 标头的图标

	private LinearLayout rl_talklist_nolist;

	private int IntentType = 1;// 调转到群资料 1：加入的群------ 2：是自己创建的群

	@Override
	protected View getRootView() {
		View view = View.inflate(TalkListActivity.this, R.layout.talk_list,
				null);
		httpHelp = new HttpHelp();
		return view;
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
	protected void initView() {
		tv_title_title = (TextView) findViewById(R.id.tv_title_title);
		tv_title_title.setVisibility(View.GONE);
		ll_title_talk = (LinearLayout) findViewById(R.id.ll_title_talk);
		ll_title_talk.setVisibility(View.VISIBLE);

		rl_talklist_nolist = (LinearLayout) findViewById(R.id.rl_talklist_nolist);
		tv_talk_add = (TextView) findViewById(R.id.tv_talk_add);
		tv_talk_create = (TextView) findViewById(R.id.tv_talk_create);

		rlv_talk_all = (RefreshListView) findViewById(R.id.rlv_talk_all);

		tv_talk_add.setSelected(true);
		setSelect(1);

	}

	@Override
	protected void setListener() {
		tv_talk_add.setOnClickListener(this);
		tv_talk_create.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		netWork();
		setRLVListener();
	}

	private void netWork() {
		netWorkjoinBean();
	}

	private int createPage = 1;

	private void netWorkmyBean() {
		httpHelp.sendGet(
				NetworkConfig.getMyChatGroupCreateList("5", createPage),
				TalkListBean.class, new MyRequestCallBack<TalkListBean>() {

					@Override
					public void onSucceed(TalkListBean bean) {
						if (bean == null)
							return;
						myBean = bean;
						createPage++;
					}
				});
	}

	private int joinPage = 1;

	private void netWorkjoinBean() {
		httpHelp.sendGet(NetworkConfig.getMyChatGroupJoinList("5", joinPage),
				TalkListBean.class, new MyRequestCallBack<TalkListBean>() {

					@Override
					public void onSucceed(TalkListBean bean) {
						if (bean == null)
							return;
						joinBean = bean;
						joinPage++;
						initRLVData();
						netWorkmyBean();
					}
				});
	}

	private String getTalkId(int position) {
		if (IntentType == 1) {
			return joinBean.cont.get(position).id;
		} else {
			return myBean.cont.get(position).id;
		}
	}

	private void setRLVListener() {
		rlv_talk_all.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(TalkListActivity.this, TalkActivity.class);
				i.putExtra("IntentType", IntentType + "");
				i.putExtra("talkId", getTalkId(position));
				startActivity(i);
			}

		});
		rlv_talk_all.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onLoadMore() {
				loadMore(IntentType);
			}
		});
	}

	private void loadMore(int i) {
		String url = "";
		if (i == 1) {
			url += NetworkConfig.getMyChatGroupJoinList("5", joinPage);
		} else if (i == 2) {
			url += NetworkConfig.getMyChatGroupCreateList("5", createPage);
		}
		httpHelp.sendGet(url, TalkListBean.class,
				new MyRequestCallBack<TalkListBean>() {

					@Override
					public void onSucceed(TalkListBean bean) {
						if (bean == null) {
							rlv_talk_all.onRefreashFinish();
							return;
						}
						if (bean.cont.size() > 0) {
							if (IntentType == 1) {
								joinPage++;
								joinBean.cont.addAll(bean.cont);

							} else if (IntentType == 2) {
								createPage++;
								myBean.cont.addAll(bean.cont);
							}
							talkListAdapter.notifyDataSetChanged();
						} else {
							rlv_talk_all.onRefreashFinish();
							UIUtils.showToast(UIUtils.getContext(), "没有更多的了");
						}
					}
				});

	}

	private void initRLVData() {
		talkListAdapter = new TalkListAdapter();
		rlv_talk_all.setAdapter(talkListAdapter);
		setAdapterData(joinBean);
	}

	// private static int ADAPTERTYPE = 1;

	class TalkListAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			if (IntentType == 1) {
				if (joinBean == null) {
					return 0;
				}
				return joinBean.cont.size();
			} else if (IntentType == 2) {
				if (myBean == null) {
					return 0;
				}
				return myBean.cont.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(TalkListActivity.this,
						R.layout.talk_list_item, null);
				viewHolder = new ViewHolder();

				viewHolder.ci_selectcondition_icon = (CircleImageView) convertView
						.findViewById(R.id.ci_selectcondition_icon);
				viewHolder.tv_talklistitem_introduction = (TextView) convertView
						.findViewById(R.id.tv_talklistitem_introduction);
				viewHolder.tv_talklistitem_number = (TextView) convertView
						.findViewById(R.id.tv_talklistitem_number);
				viewHolder.tv_talklistitem_name = (TextView) convertView
						.findViewById(R.id.tv_talklistitem_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			switch (IntentType) {
			case 1:
				initMyJoin(position);
				break;
			case 2:
				initMyCreate(position);
				break;
			default:
				break;
			}
			return convertView;
		}

		private void initMyJoin(int position) {
			httpHelp.showImage(viewHolder.ci_selectcondition_icon,
					joinBean.cont.get(position).pic + "##");
			viewHolder.tv_talklistitem_name
					.setText(joinBean.cont.get(position).name);
			viewHolder.tv_talklistitem_number.setText(""
					+ joinBean.cont.get(position).mnum + "/"
					+ joinBean.cont.get(position).mtotal);
			viewHolder.tv_talklistitem_introduction.setText(joinBean.cont
					.get(position).desc);
		}

		private void initMyCreate(int position) {
			httpHelp.showImage(viewHolder.ci_selectcondition_icon,
					myBean.cont.get(position).pic + "##");
			viewHolder.tv_talklistitem_name
					.setText(myBean.cont.get(position).name);
			viewHolder.tv_talklistitem_number.setText(""
					+ myBean.cont.get(position).mnum + "/"
					+ myBean.cont.get(position).mtotal);
			viewHolder.tv_talklistitem_introduction.setText(myBean.cont
					.get(position).desc);
		}

	}

	class ViewHolder {
		private CircleImageView ci_selectcondition_icon;
		private TextView tv_talklistitem_name;
		private TextView tv_talklistitem_number;
		private TextView tv_talklistitem_introduction;
	}

	private void setAdapterData(TalkListBean bean) {
		if (talkListAdapter != null) {
			if (bean.cont.size() > 0) {
				rlv_talk_all.setVisibility(View.VISIBLE);
				rl_talklist_nolist.setVisibility(View.GONE);
				talkListAdapter.notifyDataSetChanged();
			} else {
				rl_talklist_nolist.setVisibility(View.VISIBLE);
				rlv_talk_all.setVisibility(View.GONE);
			}
		}
	}

	private void setSelect(int i) {

		if (i == 1) {
			tv_talk_add.setSelected(true);
			tv_talk_create.setSelected(false);
		} else if (i == 2) {
			tv_talk_add.setSelected(false);
			tv_talk_create.setSelected(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.tv_talk_add:
			IntentType = 1;
			setSelect(1);
			if (joinBean != null)
				setAdapterData(joinBean);
			break;
		case R.id.tv_talk_create:
			IntentType = 2;
			setSelect(2);
			if (myBean != null)
				setAdapterData(myBean);
			break;
		default:
			break;
		}
	}

}
