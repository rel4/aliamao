package com.aliamauri.meat.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 群成员
 * 
 * @author ych
 * 
 */
public class DelTalkMemberActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_title_right;
	private ListView lv_groupmenber_all;
	private GroupMenberAdapter delMenberAdapter;
	private boolean check[];
	private int count = 5;
	RelativeLayout rl_groupmember_select;

	@Override
	protected View getRootView() {
		View view = View.inflate(DelTalkMemberActivity.this,
				R.layout.talk_member, null);
		check = new boolean[6];
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
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "移除成员";
	}

	@Override
	protected void initView() {
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("移除");

		lv_groupmenber_all = (ListView) findViewById(R.id.lv_groupmenber_all);
		rl_groupmember_select = (RelativeLayout) findViewById(R.id.rl_groupmember_select);
		rl_groupmember_select.setVisibility(View.VISIBLE);
	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		setAdapter();
		setLVListener();
	}

	private void setLVListener() {
		lv_groupmenber_all.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ImageView iv_groupmember_checkicon = (ImageView) view
						.findViewById(R.id.iv_talkmember_checkicon);
				iv_groupmember_checkicon.setSelected(check[position]);
				check[position] = !check[position];

			}
		});
	}

	private void setAdapter() {
		delMenberAdapter = new GroupMenberAdapter();
		lv_groupmenber_all.setAdapter(delMenberAdapter);
	}

	class GroupMenberAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
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
				convertView = View.inflate(DelTalkMemberActivity.this,
						R.layout.talk_member_item, null);

			}
			ImageView iv_groupmember_checkicon = (ImageView) convertView
					.findViewById(R.id.iv_talkmember_checkicon);
			iv_groupmember_checkicon.setVisibility(View.VISIBLE);
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

}
