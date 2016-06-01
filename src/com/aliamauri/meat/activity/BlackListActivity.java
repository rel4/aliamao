package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.BlackListManager;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.controller.SDKHelper.HXSyncListener;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;

public class BlackListActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title_title;
	private ImageView iv_title_backicon;
	private List<User> mBlackList;
	// private List<WXMessage> data = new ArrayList<WXMessage>();
	private ListView mListView;
	private String TAG = "BlackListActivity";

	private List<Integer> choosePosition;// 选择的列表
	private HttpHelp mHttpHelp;
	private MyBaseAdapter mAdapter;

	@Override
	protected View getRootView() {
		mHttpHelp = new HttpHelp();
		getBlackUsers();
		View view = View.inflate(BlackListActivity.this, R.layout.black_list,
				null);
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
	
	/**
	 * 获取黑名单列表的用户
	 */
	private void getBlackUsers() {
		mBlackList = new ArrayList<>();
		mBlackList.addAll(BlackListManager.getInstance().getBlackListUser());

		SDKHelper.getInstance().addSyncBlackListListener(new HXSyncListener() {

			@Override
			public void onSyncSucess(boolean success) {
				mBlackList.clear();
				mBlackList.addAll(BlackListManager.getInstance()
						.getBlackListUser());

			}
		});
	}

	@Override
	protected void initView() {

		iv_title_backicon = (ImageView) findViewById(R.id.iv_title_backicon);
		tv_title_title = (TextView) findViewById(R.id.tv_title_title);
		tv_title_title.setText("黑名单");
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new MyBaseAdapter();
		mListView.setAdapter(mAdapter);

	}

	@Override
	protected void setListener() {
		iv_title_backicon.setOnClickListener(this);
	}

	class MyBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return mBlackList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mBlackList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.black_user_item, null);
				holder.civ_aaci_avatar = $(convertView, R.id.civ_aaci_avatar);
				holder.tv_aaci_name = $(convertView, R.id.tv_aaci_name);
				holder.tv_aaci_indicator = $(convertView, R.id.tv_aaci_indicator);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			mHttpHelp.showImage(holder.civ_aaci_avatar, mBlackList.get(position).getAvatar());
			holder.tv_aaci_name.setText(mBlackList.get(position).getNick());
			holder.tv_aaci_indicator.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CmdManager.getInstance().requstServicerRemoveBlackList(
							mBlackList.get(position).getUserId(), new CmdManagerCallBack() {

								@Override
								public void onState(boolean isSucceed) {
									if (isSucceed) {
										mBlackList.remove(position);
										mAdapter.notifyDataSetChanged();
									}

								}
							});
					
				}
			});
			return convertView;
		}

	}

	class ViewHolder {
		public CircleImageView civ_aaci_avatar;
		public TextView tv_aaci_name;
		public TextView tv_aaci_indicator;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.iv_title_backicon:
			finish();
			break;

		default:
			break;
		}
	}

}
