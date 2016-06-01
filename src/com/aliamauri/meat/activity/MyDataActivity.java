package com.aliamauri.meat.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.eventBus.EventForNotice;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.utils.CaculationUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageViewWhite;
import com.aliamauri.meat.view.HorizontalListView;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 我的资料
 * 
 * @author ych
 * 
 */
public class MyDataActivity extends Activity implements OnClickListener {
	private HttpHelp httpHelp;
	private HorizontalListView hlv_mydata_medals;
	private TextView tv_mydata_setdata;
	private ImageView iv_mydata_back;
	private TextView tv_mydata_authenticate;

	// 资料
	private CircleImageViewWhite ci_mydata_headicon;
	private TextView tv_mydata_nickname;
	private TextView tv_mydata_userid;
	private TextView tv_mydata_sex;
	private TextView tv_mydata_age;
	private TextView tv_mydata_signstyle;
	private TextView tv_mydata_pland;
	private TextView tv_mydata_job;
	private TextView tv_mydata_hobby;

	private ImageView iv_mydata_blur;// 高斯模糊图片
	private Bitmap bmp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);// 注册EVENTBUS
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_data);
		httpHelp = new HttpHelp();
		// update();
		initView();
		setListener();
		initData();
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

	public void onEventMainThread(EventForNotice event) {
		initData();
	}

	private void initData() {
		httpHelp.showImage(ci_mydata_headicon,
				PrefUtils.getString(GlobalConstant.USER_FACE, "") + "##");
		httpHelp.showImage(PrefUtils.getString(GlobalConstant.USER_FACE, ""),
				iv_mydata_blur);
		tv_mydata_nickname.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_NICKNAME, ""));
		tv_mydata_userid.setText("帐号:"
				+ PrefUtils.getString(UIUtils.getContext(),
						GlobalConstant.USER_ID, ""));
		tv_mydata_sex.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_SEX, ""));
		tv_mydata_age
				.setText(CaculationUtils.calculateDatePoor(PrefUtils.getString(
						UIUtils.getContext(), GlobalConstant.USER_BIRTH, "")));
		tv_mydata_signstyle.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_SIGNATURE, ""));
		tv_mydata_pland.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_PLAND, ""));
		tv_mydata_job.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_JOB, ""));
		tv_mydata_hobby.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_HOBBY, ""));
		if ("1".equals(PrefUtils.getString(GlobalConstant.ISSMVAL, ""))) {
			tv_mydata_authenticate.setText("已认证");
		} else {
			tv_mydata_authenticate.setText("去认证");
			tv_mydata_authenticate.setOnClickListener(this);
		}
	}

	private void setListener() {
		tv_mydata_setdata.setOnClickListener(this);
		iv_mydata_back.setOnClickListener(this);
		// tv_mydata_authenticate.setOnClickListener(this);
	}

	private void initView() {
		tv_mydata_hobby = (TextView) findViewById(R.id.tv_mydata_hobby);
		tv_mydata_job = (TextView) findViewById(R.id.tv_mydata_job);
		tv_mydata_pland = (TextView) findViewById(R.id.tv_mydata_pland);
		tv_mydata_signstyle = (TextView) findViewById(R.id.tv_mydata_signstyle);
		tv_mydata_age = (TextView) findViewById(R.id.tv_mydata_age);
		tv_mydata_sex = (TextView) findViewById(R.id.tv_mydata_sex);
		tv_mydata_userid = (TextView) findViewById(R.id.tv_mydata_userid);
		tv_mydata_nickname = (TextView) findViewById(R.id.tv_mydata_nickname);
		ci_mydata_headicon = (CircleImageViewWhite) findViewById(R.id.ci_mydata_headicon);
		hlv_mydata_medals = (HorizontalListView) findViewById(R.id.hlv_mydata_medals);
		hlv_mydata_medals.setAdapter(new HorizontalListViewAdapter());
		tv_mydata_setdata = (TextView) findViewById(R.id.tv_mydata_setdata);
		iv_mydata_back = (ImageView) findViewById(R.id.iv_mydata_back);
		tv_mydata_authenticate = (TextView) findViewById(R.id.tv_mydata_authenticate);

		iv_mydata_blur = (ImageView) findViewById(R.id.iv_mydata_blur);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// content.unregisterReceiver(receiver);
		// content = null;
		EventBus.getDefault().unregister(this);// 注册EVENTBUS
	}

	@Override
	protected void onStart() {
		initData();
		super.onStart();
	}

	// private Context content;
	// private Receiver receiver;
	//
	// private void update() {
	// if (content == null) {
	// content = UIUtils.getContext();
	// }
	// IntentFilter filter = new IntentFilter();
	// filter.addAction("updateUserInfo");
	// receiver = new Receiver();
	// content.registerReceiver(receiver, filter);
	// }
	//
	// class Receiver extends BroadcastReceiver {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// // 看本地有没有存有头像，
	// initData();
	// }
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_mydata_back:
			finish();
			break;
		case R.id.tv_mydata_setdata:
			startActivity(new Intent(MyDataActivity.this,
					EditDataActivity.class));
			break;
		case R.id.tv_mydata_authenticate:
			startActivity(new Intent(MyDataActivity.this,
					AuthenticateActivity.class));
			break;
		default:
			break;
		}
	}

	class HorizontalListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
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
				convertView = View.inflate(MyDataActivity.this,
						R.layout.hlv_mydata_item, null);
			}
			return convertView;
		}

	}

}
