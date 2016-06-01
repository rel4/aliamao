package com.aliamauri.meat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.MyAlbumBean;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.UIUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.umeng.analytics.MobclickAgent;

/**
 * 个人相册
 * 
 * @author ych
 * 
 */
public class MyAlbumActivity extends BaseActivity implements OnClickListener {
	private HttpHelp httpHelp;
	private ImageView tv_title_righticon;
	private PullToRefreshGridView gv_myalbum_pic;
	private MyAlbumBean albumBean;
	private MyAlbumAdapter myAlbumAdapter;

	@Override
	protected View getRootView() {
		View view = View.inflate(MyAlbumActivity.this, R.layout.my_album, null);
		MyAlbumUpdata();
		httpHelp = new HttpHelp();

		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "个人相册";
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
		tv_title_righticon = (ImageView) findViewById(R.id.iv_title_righticon);
		tv_title_righticon.setVisibility(View.VISIBLE);
		gv_myalbum_pic = (PullToRefreshGridView) findViewById(R.id.gv_myalbum_pic);
		gv_myalbum_pic.getRefreshableView().setSelector(
				android.R.color.transparent);

	}

	@Override
	protected void setListener() {
		tv_title_righticon.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		setGVListener();
		NetworkConfig();
	}

	private void setGVListener() {
		gv_myalbum_pic.setMode(Mode.PULL_FROM_END);
		gv_myalbum_pic.setOnRefreshListener(new OnRefreshListener<GridView>() {

			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
				more();
			}
		});
	}

	private void more() {
		httpHelp.sendGet(NetworkConfig.getUserAlbum("20", albumPage),
				MyAlbumBean.class, new MyRequestCallBack<MyAlbumBean>() {

					@Override
					public void onSucceed(MyAlbumBean bean) {
						if (bean == null) {
							gv_myalbum_pic.onRefreshComplete();
							return;
						}
						if ("1".equals(bean.status)) {
							if (bean.cont.size() > 0) {
								albumPage++;
								albumBean.cont.addAll(0, bean.cont);
								myAlbumAdapter.notifyDataSetChanged();
							} else {
								UIUtils.showToast(UIUtils.getContext(), "没有更多了");
							}
						}
						gv_myalbum_pic.onRefreshComplete();
					}

				});
	}

	private int albumPage;

	private void NetworkConfig() {
		albumPage = 1;
		httpHelp.sendGet(NetworkConfig.getUserAlbum("20", albumPage),
				MyAlbumBean.class, new MyRequestCallBack<MyAlbumBean>() {

					@Override
					public void onSucceed(MyAlbumBean bean) {
						if (bean == null) {
							return;
						}
						if ("1".equals(bean.status)) {
							if (bean.cont.size() > 0) {
								albumPage++;
								albumBean = bean;
								initGVData();
							} else {
								UIUtils.showToast(UIUtils.getContext(),
										"还木有图片,上传一些");
							}
						}
						gv_myalbum_pic.onRefreshComplete();
					}

				});
	}

	private void initGVData() {
		myAlbumAdapter = new MyAlbumAdapter();
		gv_myalbum_pic.setAdapter(myAlbumAdapter);
	}

	private ImageView iv_myalbum_pic;

	class MyAlbumAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return albumBean.cont.size();
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
				convertView = View.inflate(MyAlbumActivity.this,
						R.layout.my_album_item, null);
				iv_myalbum_pic = (ImageView) convertView
						.findViewById(R.id.iv_myalbum_pic);
				convertView.setTag(iv_myalbum_pic);
			} else {
				iv_myalbum_pic = (ImageView) convertView.getTag();
			}
			httpHelp.showImage(iv_myalbum_pic,
					albumBean.cont.get(position).imgurl);
			return convertView;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		content.unregisterReceiver(receiver);
		content = null;
	}

	private Receiver receiver;
	private Context content;

	private void MyAlbumUpdata() {
		receiver = new Receiver();
		if (content == null) {
			content = UIUtils.getContext();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction("MyAlbumUpdata");

		content.registerReceiver(receiver, filter);
	}

	class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			NetworkConfig();

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_righticon:
			startActivity(new Intent(this, PhonePicActivity.class));
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

}
