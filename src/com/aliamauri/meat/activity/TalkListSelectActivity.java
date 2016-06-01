package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * 话题搜索
 * 
 * @author ych
 * 
 */
public class TalkListSelectActivity extends BaseActivity implements
		OnClickListener {
	private HttpHelp httpHelp;
	private TalkListBean tlBean;

	private RefreshListView rlv_talkselect_all;
	private TalkListAdapter talkListAdapter;
	private RelativeLayout rl_talklist_search;

	private EditText et_tls_search;
	private ImageView iv_tls_searchicon;
	private boolean FirstTime = true;

	@Override
	protected View getRootView() {
		View view = View.inflate(TalkListSelectActivity.this,
				R.layout.talk_list_select, null);
		httpHelp = new HttpHelp();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "话题";
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

		rlv_talkselect_all = (RefreshListView) findViewById(R.id.rlv_talkselect_all);
		rl_talklist_search = (RelativeLayout) findViewById(R.id.rl_talklist_search);
		rl_talklist_search.setVisibility(View.VISIBLE);

		et_tls_search = (EditText) findViewById(R.id.et_tls_search);
		iv_tls_searchicon = (ImageView) findViewById(R.id.iv_tls_searchicon);

	}

	@Override
	protected void setListener() {
		iv_tls_searchicon.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		newWork("");
	}

	private static int chatGroupPage = 2;
	private final static int Pagesize = 2;

	private void newWork(final String name) {
		httpHelp.sendGet(
				NetworkConfig.getChatGroupList(Pagesize, "1", 1, name),
				TalkListBean.class,

				new MyRequestCallBack<TalkListBean>() {

					@Override
					public void onSucceed(TalkListBean bean) {
						if (bean == null)
							return;
						if ("1".equals(bean.status)) {
							if (FirstTime) {
								tlBean = bean;
								FirstTime = false;
								initRLVData();
								setLVListener();

							} else {
								tlBean.cont.clear();
								tlBean.cont.addAll(bean.cont);
								talkListAdapter.notifyDataSetChanged();
							}
						} else {
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
						}
					}
				});

	}

	private void setLVListener() {
		rlv_talkselect_all.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(TalkListSelectActivity.this,
						TalkDataJoinActivity.class);
				i.putExtra("talkId", tlBean.cont.get(position).id);
				i.putExtra("IntentType", "2");
				startActivity(i);
			}
		});
		rlv_talkselect_all.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onLoadMore() {
				loadMore();
			}

		});
	}

	private void loadMore() {
		httpHelp.sendGet(NetworkConfig.getChatGroupList(Pagesize, "1",
				chatGroupPage, ""), TalkListBean.class,

		new MyRequestCallBack<TalkListBean>() {

			@Override
			public void onSucceed(TalkListBean bean) {
				if (bean == null) {
					rlv_talkselect_all.onRefreashFinish();
					return;
				}
				if ("1".equals(bean.status)) {
					if (bean.cont.size() > 0) {
						tlBean.cont.addAll(bean.cont);
						chatGroupPage++;
						talkListAdapter.notifyDataSetChanged();
					} else {
						UIUtils.showToast(UIUtils.getContext(), "没有更多了");
					}
				} else {
					UIUtils.showToast(UIUtils.getContext(), bean.msg);
				}
				rlv_talkselect_all.onRefreashFinish();
			}
		});
	}

	private void initRLVData() {
		talkListAdapter = new TalkListAdapter();
		rlv_talkselect_all.setAdapter(talkListAdapter);
	}

	class TalkListAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tlBean.cont.size();
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
				convertView = View.inflate(TalkListSelectActivity.this,
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
			httpHelp.showImage(viewHolder.ci_selectcondition_icon,
					tlBean.cont.get(position).pic + "##");
			viewHolder.tv_talklistitem_name
					.setText(tlBean.cont.get(position).name);
			viewHolder.tv_talklistitem_number.setText(""
					+ tlBean.cont.get(position).mnum + "/"
					+ tlBean.cont.get(position).mtotal);
			viewHolder.tv_talklistitem_introduction.setText(tlBean.cont
					.get(position).desc);
			return convertView;
		}

	}

	class ViewHolder {
		private CircleImageView ci_selectcondition_icon;
		private TextView tv_talklistitem_name;
		private TextView tv_talklistitem_number;
		private TextView tv_talklistitem_introduction;
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
		case R.id.iv_tls_searchicon:
			rlv_talkselect_all.onRefreashFinish();
			String name = et_tls_search.getText().toString().trim();
			newWork(name);
			break;
		default:
			break;
		}
	}

}
