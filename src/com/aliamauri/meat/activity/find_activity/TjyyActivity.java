package com.aliamauri.meat.activity.find_activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.DLAppManager;
import com.aliamauri.meat.activity.APPDetailActivity;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.bean.FindBean;
import com.aliamauri.meat.bean.cont.AppCont;
import com.aliamauri.meat.db.dlapp.FindDao;
import com.aliamauri.meat.db.dlapp.FindInfo;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.AppUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.RoundedImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

/**
 * 发现----推荐应用界面
 * 
 * @author limaokeji-windosc
 * 
 */
public class TjyyActivity extends Activity implements OnClickListener {
	private HttpHelp mHttpHelp;
	private FindBean findBean;
	private PullToRefreshListView lv;
	private int findPage = 2;
	private Adapter findAdapter;
	SimpleDateFormat simpleDF;

	List<FindInfo> listInfo;
	private int listPosition;// 查询listinfo中的数据，看看有没有想要的
	private FindDao findDao;
	private NotificationManager nm;
	private Notification notification;
	private Intent iDetail;

	private ImageView iv_title_backicon;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_tjyy);
		simpleDF = new SimpleDateFormat("yyyyMMddHHmmss");
		findDao = new FindDao();
		mHttpHelp = new HttpHelp();
		listInfo = new ArrayList<FindInfo>();
		iDetail = new Intent(TjyyActivity.this, APPDetailActivity.class);
		DLAppManager.getInstance().setTjyy(this);
		initDB();
		initView();
		// inLoading();
		// isOtherPager(1);
		// setSlidingMenuEnable(true);
		initNet();
	}

	private void initView() {
		((TextView) findViewById(R.id.tv_title_title)).setText("应用推荐");
		iv_title_backicon = (ImageView) findViewById(R.id.iv_title_backicon);
		lv = (PullToRefreshListView) findViewById(R.id.rlv_find_tjyy);
		iv_title_backicon.setOnClickListener(this);
	}

	public void initDB() {
		if (listInfo != null) {
			listInfo.clear();
		} else {
			listInfo = new ArrayList<FindInfo>();
		}
		listInfo.addAll(findDao.getAll());
		if (findAdapter != null) {
			findAdapter.notifyDataSetChanged();
		}
	}

	private void initNet() {
		mHttpHelp.sendGet(NetworkConfig.getFind_tjyy(1), FindBean.class,
				new MyRequestCallBack<FindBean>() {

					@Override
					public void onSucceed(FindBean bean) {
						if (bean == null) {
							return;
						}
						findBean = bean;
						// LoadingSuccess();
						initLV();// 初始化
						setListener();// 设置监听器
						// 去掉之前的View;
						// flContent.removeAllViews();
						// 向FrameLayout中动态添加布局
						// flContent.addView(lv);
					}
				});
	}

	private void initLV() {
		// lv = new com.handmark.pulltorefresh.library.PullToRefreshListView(
		// MyApplication.getContext(), Mode.PULL_FROM_END);
		if (findAdapter == null) {
			findAdapter = new Adapter(MyApplication.getContext());
			lv.setAdapter(findAdapter);
		} else {
			findAdapter.notifyDataSetChanged();
		}

	}

	private void setListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				iDetail.putExtra(GlobalConstant.INTENT_ID,
						findBean.cont.get(position - 1).id);
				iDetail.putExtra(GlobalConstant.INTENT_NAME,
						findBean.cont.get(position - 1).name);
				startActivity(iDetail);
			}

		});
		lv.setMode(Mode.PULL_FROM_END);
		// 设置下拉刷新监听器
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			// 下拉Pulling Down
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				// TODO Auto-generated method stub
				new AsyncTask<Void, Void, Void>() {

					// 先运行这个
					@Override
					protected Void doInBackground(Void... arg0) {
						// TODO Auto-generated method stub

						mHttpHelp.sendGet(NetworkConfig.getFind_tjyy(findPage),
								FindBean.class,
								new MyRequestCallBack<FindBean>() {

									@Override
									public void onSucceed(FindBean bean) {

										if (bean == null) {
											UIUtils.showToast(
													UIUtils.getContext(),
													"没有更多数据");
											return;
										}
										if (bean.cont != null) {
											findPage++;
											findBean.cont.addAll(bean.cont);
											findAdapter.notifyDataSetChanged();
										} else {
											UIUtils.showToast(
													UIUtils.getContext(),
													"没有更多数据");
											return;
										}
									}
								});

						return null;
					}

					// 最后运行
					@Override
					protected void onPostExecute(Void result) {
						// TODO Auto-generated method stub\
						lv.onRefreshComplete();

					};
				}.execute();
			}
		});
	}

	class Adapter extends BaseAdapter {
		private Context context;
		private ViewHolder holder;

		// private List<DiscoverItem> data;

		public Context getContext() {
			return context;
		}

		/*
		 * public void addDiscoverItem(DiscoverItem item){ data.add(item);
		 * notifyDataSetChanged(); }
		 */
		public Adapter(Context context) {
			this.context = context;
			// data = new ArrayList<DiscoverItem>();
		}

		@Override
		public int getCount() {
			if (findBean == null || findBean.cont == null) {
				return 0;
			}
			return findBean.cont.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			// ViewHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getContext(), R.layout.discoverlist_item,
						null);
				holder = new ViewHolder();

				holder.iv_discoverlist_icon = (RoundedImageView) view
						.findViewById(R.id.iv_discoverlist_icon);
				holder.tv_discoverlist_name = (TextView) view
						.findViewById(R.id.tv_discoverlist_name);
				// holder.tv_discoverlist_introduce = (TextView) view
				// .findViewById(R.id.tv_discoverlist_introduce);
				holder.tv_discoverlist_number = (TextView) view
						.findViewById(R.id.tv_discoverlist_number);
				holder.tv_discoverlist_try = (TextView) view
						.findViewById(R.id.tv_discoverlist_try);
				view.setTag(holder);
			}

			holder.tv_discoverlist_name
					.setText(findBean.cont.get(position).name);
			// holder.tv_discoverlist_introduce.setText(findBean.cont
			// .get(position).mdesc);
			holder.tv_discoverlist_number
					.setText(findBean.cont.get(position).num);
			mHttpHelp.showImage(holder.iv_discoverlist_icon,
					findBean.cont.get(position).pic);

			listPosition = AppUtils.IsDownload(findBean.cont.get(position).id,
					listInfo);

			if (listPosition >= 0 && listInfo.get(listPosition).getState() == 2) {
				holder.tv_discoverlist_try.setClickable(true);
				holder.tv_discoverlist_try.setSelected(false);
				holder.tv_discoverlist_try.setText("打开");
				holder.tv_discoverlist_try.setOnClickListener(new MyOnclick(
						findBean.cont.get(position),
						holder.tv_discoverlist_try, 2, listInfo
								.get(listPosition)));

			} else if (AppUtils.IsDownload(findBean.cont.get(position).id,
					DLAppManager.getInstance().getFindInfo()) >= 0) {
				holder.tv_discoverlist_try.setClickable(false);
				holder.tv_discoverlist_try.setText("正在下载");
				holder.tv_discoverlist_try.setSelected(true);
			} else {
				holder.tv_discoverlist_try.setClickable(true);
				holder.tv_discoverlist_try.setSelected(false);
				holder.tv_discoverlist_try.setText("下载");
				holder.tv_discoverlist_try.setOnClickListener(new MyOnclick(
						findBean.cont.get(position),
						holder.tv_discoverlist_try, 0, null));
			}

			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		/*
		 * 显示在ListView中的控件
		 */

	}

	static class ViewHolder {
		RoundedImageView iv_discoverlist_icon;
		TextView tv_discoverlist_name;
		TextView tv_discoverlist_number;
		TextView tv_discoverlist_try;
		// TextView tv_discoverlist_introduce;
	}

	public void ShowDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(TjyyActivity.this);
		builder.setMessage("确定下载该应用么?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	class MyOnclick implements OnClickListener {
		private int state;
		private String localUrl = "";
		private long name;
		private TextView textView;
		private FindInfo fInfo;
		private AppCont cont;

		public MyOnclick(AppCont cont, TextView textView, int state,
				FindInfo fInfo) {
			this.state = state;
			this.textView = textView;
			this.cont = cont;
			this.fInfo = fInfo;
		}

		@Override
		public void onClick(View v) {
			if (state == 2) {
				AppUtils.startAPP(fInfo);
			} else if (state == 1) {
			} else {
				DLAPP();
			}
		}

		private void DLAPP() {
			name = Long.parseLong((simpleDF.format(new Date()) + cont.id));
			localUrl = GlobalConstant.DL_APK_PATH + name + ".apk";
			fInfo = new FindInfo();
			fInfo.setAppid(cont.id);
			fInfo.setAppname(cont.name);
			fInfo.setApppackage(cont.pname);
			fInfo.setDownloadurl(cont.downloadurl);
			fInfo.setLocalurl(localUrl);
			fInfo.setState(1);
			listInfo.add(fInfo);
			state = 1;
			findDao.SaveDL(fInfo);
			DLAppManager.getInstance().addFindInfo(fInfo);
			if (!textView.isSelected()) {
				textView.setSelected(true);
				textView.setText("正在下载");
			}
		}

	}

	public void showNotification(int id) {
		nm.notify(id, notification);
	}

	private void showNotifi(String name) {
		nm = (NotificationManager) UIUtils.getContext().getSystemService(
				Context.NOTIFICATION_SERVICE);
		notification = new Notification(R.drawable.ic_launch, name + "正在下载",
				System.currentTimeMillis());
		notification.contentView = new RemoteViews(UIUtils.getContext()
				.getPackageName(), R.layout.up_notification);
		notification.flags = Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_AUTO_CANCEL;
		notification.icon = android.R.drawable.stat_sys_download;
		Intent notificationIntent = new Intent(UIUtils.getContext(),
				MainActivity.class);
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
