package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.umeng.analytics.MobclickAgent;

/**
 * ID搜索
 * 
 * @author ych
 * 
 */
public class TalkActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title_right;
	private ListView lv_talk_content;
	private Intent talkType_intent;
	private static String talkType_Str;

	@Override
	protected View getRootView() {
		View view = View.inflate(TalkActivity.this, R.layout.talk, null);
		talkType_intent = getIntent();
		if (talkType_intent.getStringExtra("IntentType") != null) {
			talkType_Str = talkType_intent.getStringExtra("IntentType")
					.toString();
		}
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "话题标题";
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
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("资料");// 还没给

		lv_talk_content = (ListView) findViewById(R.id.lv_talk_content);
		lv_talk_content.setAdapter(new TalkAdapter());
	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
	}

	class TalkAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 4;
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
			if (position == 0) {
				convertView = View.inflate(TalkActivity.this,
						R.layout.talk_item_my, null);
				TextView tv_talkitem_speechtime = (TextView) convertView
						.findViewById(R.id.tv_talkitem_speechtime);
				tv_talkitem_speechtime.setVisibility(View.VISIBLE);

				TextView tv_talkitem_content = (TextView) convertView
						.findViewById(R.id.tv_talkitem_content);
				tv_talkitem_content.setText("               ");
				// tv_talkitem_content.setWidth(R.dimen.x50);
				convertView.findViewById(R.id.iv_talkitem_speechicon)
						.setVisibility(View.VISIBLE);
			} else if (position == 1) {
				convertView = View.inflate(TalkActivity.this,
						R.layout.talk_item_my, null);
				TextView tv_talkitem_content = (TextView) convertView
						.findViewById(R.id.tv_talkitem_content);
				tv_talkitem_content
						.setText("sfsfsdfdsfdfadsfdsfdsfdsfdsfsfsfsfdsfdsfdsfsfsfsfdsfsfdsfdsfsdf");
			} else if (position == 2) {
				convertView = View.inflate(TalkActivity.this,
						R.layout.talk_item_other, null);
				TextView tv_talk_other_content = (TextView) convertView
						.findViewById(R.id.tv_talk_other_content);
				convertView.findViewById(R.id.iv_talk_other_speechicon)
						.setVisibility(View.VISIBLE);
				convertView.findViewById(R.id.tv_talk_other_speechtime)
						.setVisibility(View.VISIBLE);
				convertView.findViewById(R.id.iv_talk_other_unread)
						.setVisibility(View.VISIBLE);
			} else if (position == 3) {
				convertView = View.inflate(TalkActivity.this,
						R.layout.talk_item_other, null);
				TextView tv_talk_other_content = (TextView) convertView
						.findViewById(R.id.tv_talk_other_content);
				tv_talk_other_content
						.setText("sfsfsdfdsfdfadsfdsfdsfdsfdsfsfsfsfdsfdsfdsfsfsfsfdsfsfdsfdsfsdf");
			}

			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.tv_title_right:

			if ("1".equals(talkType_Str)) {
				i = new Intent(this, TalkDataJoinActivity.class);
				i.putExtra("talkId", talkType_intent.getStringExtra("talkId"));
				startActivity(i);
			} else {
				i = new Intent(this, TalkDataCreateActivity.class);
				i.putExtra("talkId", talkType_intent.getStringExtra("talkId"));
				startActivity(i);
			}
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

}
