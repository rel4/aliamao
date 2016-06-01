package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.GroupUserBean;
import com.aliamauri.meat.bean.cont.GroupUserCont;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;

/**
 * 群成员
 * 
 * @author ych
 * 
 */
public class TalkMemberActivity extends BaseActivity implements OnClickListener {
	private HttpHelp httpHelp;
	private GroupUserBean GUBean;
	private TextView tv_title_right;
	private ListView lv_groupmenber_all;
	private GroupMenberAdapter GroupMenberAdapter;
	RelativeLayout rl_groupmember_select;
	private Intent dataIntent;
	private EditText et_tm_find;
	private ImageView iv_tm_find;

	@Override
	protected View getRootView() {
		View view = View.inflate(TalkMemberActivity.this, R.layout.talk_member,
				null);
		dataIntent = getIntent();
		httpHelp = new HttpHelp();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "群成员";
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
		if ("create".equals(getIntent().getStringExtra("talkType"))) {
			tv_title_right = (TextView) findViewById(R.id.tv_title_right);
			tv_title_right.setVisibility(View.VISIBLE);
			tv_title_right.setText("编辑");
			tv_title_right.setOnClickListener(this);
		}

		lv_groupmenber_all = (ListView) findViewById(R.id.lv_groupmenber_all);
		rl_groupmember_select = (RelativeLayout) findViewById(R.id.rl_groupmember_select);
		et_tm_find = (EditText) findViewById(R.id.et_tm_find);
		iv_tm_find = (ImageView) findViewById(R.id.iv_tm_find);

	}

	@Override
	protected void setListener() {
		iv_tm_find.setOnClickListener(this);
		netWork();
	}

	private void netWork() {
		if (dataIntent.getStringExtra("talkId") != null) {
			httpHelp.sendGet(NetworkConfig.groupUserlist(dataIntent
					.getStringExtra("talkId")), GroupUserBean.class,
					new MyRequestCallBack<GroupUserBean>() {

						@Override
						public void onSucceed(GroupUserBean bean) {
							if (bean == null)
								return;
							GUBean = bean;
							setAdapter(bean.cont);
							setLVListener();
						}

					});
		}
	}

	private void setLVListener() {
		lv_groupmenber_all.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
	}

	private void setAdapter(List<GroupUserCont> cont) {
		GroupMenberAdapter = new GroupMenberAdapter(cont);
		lv_groupmenber_all.setAdapter(GroupMenberAdapter);
	}

	class GroupMenberAdapter extends BaseAdapter {
		private ViewHolder viewHolder;
		private List<GroupUserCont> cont = new ArrayList<GroupUserCont>();

		public GroupMenberAdapter(List<GroupUserCont> cont) {
			this.cont.addAll(cont);

		}

		public void SetCont(List<GroupUserCont> cont) {
			this.cont.clear();
			this.cont.addAll(cont);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cont.size();
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
				convertView = View.inflate(TalkMemberActivity.this,
						R.layout.talk_member_item, null);
				viewHolder = new ViewHolder();
				viewHolder.ci_talkmember_icon = (CircleImageView) convertView
						.findViewById(R.id.ci_talkmember_icon);
				viewHolder.tv_tmi_nickname = (TextView) convertView
						.findViewById(R.id.tv_tmi_nickname);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			httpHelp.showImage(viewHolder.ci_talkmember_icon,
					cont.get(position).face + "##");
			viewHolder.tv_tmi_nickname.setText(cont.get(position).nickname);
			return convertView;
		}
	}

	class ViewHolder {
		public CircleImageView ci_talkmember_icon;
		public TextView tv_tmi_nickname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	private void findUser() {
		String find = et_tm_find.getText().toString().trim();
		List<GroupUserCont> cont = new ArrayList<GroupUserCont>();
		if (TextUtils.isEmpty(find)) {
			cont = GUBean.cont;
		} else {
			cont.clear();
			for (GroupUserCont c : GUBean.cont) {
				if (c.id.contains(find) || c.nickname.contains(find)) {
					cont.add(c);
				}
			}
		}
		GroupMenberAdapter.SetCont(cont);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			Intent i = new Intent(TalkMemberActivity.this,
					PopupwindowEditMemberActivity.class);
			startActivity(i);
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.iv_tm_find:
			findUser();
			break;
		default:
			break;
		}
	}

}
