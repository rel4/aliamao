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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.TalkDataBean;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.HorizontalListView;
import com.aliamauri.meat.view.MySwitch;
import com.umeng.analytics.MobclickAgent;

/**
 * 话题资料
 * 
 * @author ych
 * 
 */
public class TalkDataJoinActivity extends BaseActivity implements
		OnClickListener {
	private HttpHelp httpHelp;
	private TalkDataBean tdBean;

	private HorizontalListView hl_tdj_pic;
	private TextView tv_talkdata_btn;
	private Intent dataIntent;
	private HorizontalListViewAdapter hlvAdapter;

	private ImageView iv_tdj_talkmember1;
	private RelativeLayout rl_talkdata_groupmember;

	private List<String> listTags;
	// 资料
	private CircleImageView ci_tdj_headicon;
	private TextView tv_tdj_nickname;
	private GridView gv_tdj_tags;
	private TextView tv_tdj_number;
	private MySwitch ms_tdj_message;
	private TextView tv_tdj_indtroduction;

	@Override
	protected View getRootView() {
		View view = View.inflate(TalkDataJoinActivity.this,
				R.layout.talk_data_join, null);
		httpHelp = new HttpHelp();
		dataIntent = getIntent();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		return "话题名字";
	}

	@Override
	protected void initView() {
		hl_tdj_pic = (HorizontalListView) findViewById(R.id.hl_tdj_pic);
		tv_talkdata_btn = (TextView) findViewById(R.id.tv_talkdata_btn);
		iv_tdj_talkmember1 = (ImageView) findViewById(R.id.iv_tdj_talkmember1);
		rl_talkdata_groupmember = (RelativeLayout) findViewById(R.id.rl_talkdata_groupmember);

		ci_tdj_headicon = (CircleImageView) findViewById(R.id.ci_tdj_headicon);
		tv_tdj_nickname = (TextView) findViewById(R.id.tv_tdj_nickname);
		gv_tdj_tags = (GridView) findViewById(R.id.gv_tdj_tags);
		tv_tdj_number = (TextView) findViewById(R.id.tv_tdj_number);
		ms_tdj_message = (MySwitch) findViewById(R.id.ms_tdj_message);
		tv_tdj_indtroduction = (TextView) findViewById(R.id.tv_tdj_indtroduction);

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
	protected void setListener() {
		tv_talkdata_btn.setOnClickListener(this);
		iv_tdj_talkmember1.setOnClickListener(this);
		rl_talkdata_groupmember.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		netWork();
		setLVListener();
	}

	private void netWork() {
		if (dataIntent.getStringExtra("talkId") != null) {
			httpHelp.sendGet(NetworkConfig.getUserGroupInfo(dataIntent
					.getStringExtra("talkId")), TalkDataBean.class,
					new MyRequestCallBack<TalkDataBean>() {

						@Override
						public void onSucceed(TalkDataBean bean) {
							if (bean == null)
								return;
							tdBean = bean;
							initData();
							setLVData();
							setLVListener();

						}

					});
		}
	}

	private void initData() {
		httpHelp.showImage(ci_tdj_headicon, tdBean.cont.baseinfo.pic + "##");
		tv_tdj_nickname.setText(tdBean.cont.baseinfo.name);
		listTags = new ArrayList<String>();
		if (tdBean.cont.baseinfo.tags != null) {
			String[] tagsArray = tdBean.cont.baseinfo.tags.trim().split(
					"\\|\\|\\|");
			for (String s : tagsArray) {
				if (!TextUtils.isEmpty(s)) {
					listTags.add(s);
				}
			}
		}
		gv_tdj_tags.setAdapter(new GridViewAdapter());
		tv_tdj_number.setText(tdBean.cont.baseinfo.mnum + "/"
				+ tdBean.cont.baseinfo.mtotal);
		ms_tdj_message.setOpen(tdBean.cont.baseinfo.isopen);
		tv_tdj_indtroduction.setText(tdBean.cont.baseinfo.desc);
	}

	private void setLVData() {
		hlvAdapter = new HorizontalListViewAdapter();
		hl_tdj_pic.setAdapter(hlvAdapter);
	}

	private void setLVListener() {
		hl_tdj_pic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(TalkDataJoinActivity.this,
						TalkMemberActivity.class);
				i.putExtra("talkId", dataIntent.getStringExtra("talkId"));
				startActivity(i);
			}
		});
	}

	private CircleImageView ci_circlepic_icon;

	class HorizontalListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tdBean.cont.groupuserlist.size();
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
				convertView = View.inflate(TalkDataJoinActivity.this,
						R.layout.hlv_circlepic_item, null);
				ci_circlepic_icon = (CircleImageView) convertView
						.findViewById(R.id.ci_circlepic_icon);
				convertView.setTag(ci_circlepic_icon);
			} else {
				ci_circlepic_icon = (CircleImageView) convertView.getTag();
			}
			httpHelp.showImage(ci_circlepic_icon,
					tdBean.cont.groupuserlist.get(position).face + "##");
			return convertView;
		}

	}

	private TextView tv_tdcfi_tags;

	class GridViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listTags.size();
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
				convertView = View.inflate(UIUtils.getContext(),
						R.layout.talk_data_create_flowitem, null);
				tv_tdcfi_tags = (TextView) convertView
						.findViewById(R.id.tv_tdcfi_tags);
				convertView.setTag(tv_tdcfi_tags);
			} else {
				tv_tdcfi_tags = (TextView) convertView.getTag();
			}

			tv_tdcfi_tags.setText(listTags.get(position));
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.tv_title_right:
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.iv_tdj_talkmember1:
		case R.id.rl_talkdata_groupmember:
			// case R.id.iv_tdj_talkmember2:
			i = new Intent(TalkDataJoinActivity.this, TalkMemberActivity.class);
			i.putExtra("talkType", "join");
			i.putExtra("talkId", dataIntent.getStringExtra("talkId"));
			startActivity(i);
			break;
		case R.id.tv_talkdata_btn:
			break;
		default:
			break;
		}
	}
}
