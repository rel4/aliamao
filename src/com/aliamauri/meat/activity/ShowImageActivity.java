package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.weight.phonephotos.ChildAdapter;
import com.umeng.analytics.MobclickAgent;

public class ShowImageActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title_right;

	private GridView mGridView;
	private List<String> list;
	private ArrayList<String> select_list;
	private ChildAdapter adapter;
	private CheckBox mCheckBox;
	private String mBraodcast_tag; // 发布动态页面的tag
	private Intent showPicIntent;

	@Override
	protected View getRootView() {
		View view = View.inflate(ShowImageActivity.this,
				R.layout.show_image_activity, null);

		showPicIntent = getIntent();
		select_list = new ArrayList<String>();
		ActivityFinish();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "所有图片";
	}

	@Override
	protected void initView() {
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("下一步");
		mGridView = (GridView) findViewById(R.id.child_grid);
	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
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
		list = showPicIntent.getStringArrayListExtra("data");
		mBraodcast_tag = showPicIntent.getStringExtra(GlobalConstant.DT_TAG);
		if (mBraodcast_tag != null) {
			tv_title_right.setText("完成");
		}
		adapter = new ChildAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mCheckBox = (CheckBox) view.findViewById(R.id.child_checkbox);
				mCheckBox.setChecked(!mCheckBox.isChecked());
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		context.unregisterReceiver(receiver);
		context = null;
	}

	@Override
	public void onBackPressed() {
		Toast.makeText(this,
				"бЁжа " + adapter.getSelectItems().size() + " item",
				Toast.LENGTH_LONG).show();
		super.onBackPressed();
	}

	private void ActivityFinish() {
		if (context == null) {
			context = UIUtils.getContext();
		}
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("ActivityFinish");
		context.registerReceiver(receiver, filter);
	}

	private Context context;
	private Receiver receiver;

	class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			finish();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			for (int i = 0; i < adapter.getSelectItems().size(); i++) {
				select_list.add(list.get(adapter.getSelectItems().get(i)));
			}
			if (select_list.size() > 9) {
				UIUtils.showToast(UIUtils.getContext(), "一次最多只能选择9张图片");
				select_list.clear();
			} else {
				Intent i;
				if (mBraodcast_tag != null
						&& GlobalConstant.DT_TAG.equals(mBraodcast_tag)) {
					// 将获取的照片结果添加到全局变量中
					MyApplication.setAlbumLists(select_list);
					// 通过广播销毁ac
					sendBroadCastFinish();

				} else {
					i = new Intent(this, UploadPhonePicActivity.class);
					i.putStringArrayListExtra("data",
							(ArrayList<String>) select_list);
					startActivity(i);
					select_list.clear();
				}
			}
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 通过广播销毁之前的activity
	 */
	private void sendBroadCastFinish() {
		Intent i = new Intent();
		i.setAction("ActivityFinish");
		ShowImageActivity.this.sendBroadcast(i);
	}

}
