package com.aliamauri.meat.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.DLAppManager;
import com.aliamauri.meat.bean.FindDetailBean;
import com.aliamauri.meat.db.dlapp.FindDao;
import com.aliamauri.meat.db.dlapp.FindInfo;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.AppUtils;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.HorizontalListView;
import com.aliamauri.meat.view.MyScrollView;
import com.aliamauri.meat.view.RoundedImageView;
import com.umeng.analytics.MobclickAgent;

public class APPDetailActivity extends BaseActivity implements OnClickListener {

	private HttpHelp httpHelp;
	private Intent getId;
	private String DetailId;
	private int dlID;
	private FindDetailBean FDbean;
	private SimpleDateFormat simpleDF;

	private RoundedImageView riv_adetail_icon;
	private TextView tv_adetail_name;
	private TextView tv_adetail_dlnum;
	private TextView tv_adetail_intro;
	private TextView tv_adetail_dl;

	private HorizontalListView hlv_adetail_pic;

	private RatingBar ratingBar;
	private ProgressBar proress_adetail_dl;

	private DetailAdapter detailAdapter;
	private NotificationManager nm;
	private Notification notification;
	private FindDao findDao;
	private FindInfo info;
	private int state = 0;

	private MyScrollView sv_adetail;

	private RelativeLayout rl_adetail_content;
	private RelativeLayout rl_progress;

	private ImageButton ibtn_left_home_back;
	private TextView tv_title;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 处理消息
			switch (msg.what) {
			case 1:
				// rl_adetail_content
				proress_adetail_dl.setProgress(msg.arg1);
				if (msg.arg1 >= 100) {
					proress_adetail_dl.setVisibility(View.GONE);
					state = 2;
					tv_adetail_dl.setSelected(false);
					tv_adetail_dl.setText("打开");
				} else {
					if (!tv_adetail_dl.isSelected()) {
						tv_adetail_dl.setSelected(true);
						tv_adetail_dl.setText("正在下载...");
					}
					proress_adetail_dl.setVisibility(View.VISIBLE);
				}
				// 设置滚动条和text的值
				break;
			default:
				break;
			}
		}
	};

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
	protected View getRootView() {
		DLAppManager.getInstance().setAppHandler(mHandler);
		return View.inflate(UIUtils.getContext(), R.layout.app_detail, null);
	}

	@Override
	protected void onDestroy() {
		DLAppManager.getInstance().setAppHandler(null);
		super.onDestroy();
	}

	@Override
	protected void initView() {
		// isLoading();
		tv_title = (TextView) findViewById(R.id.tv_title);
		getId = getIntent();
		findDao = new FindDao();
		simpleDF = new SimpleDateFormat("yyyyMMdd");
		if (getId.getStringExtra(GlobalConstant.INTENT_ID) != null) {
			DetailId = getId.getStringExtra(GlobalConstant.INTENT_ID);
			DLAppManager.getInstance().setAppid(DetailId);
			tv_title.setText(getId.getStringExtra(GlobalConstant.INTENT_NAME));
		}
		riv_adetail_icon = (RoundedImageView) findViewById(R.id.riv_adetail_icon);
		tv_adetail_dlnum = (TextView) findViewById(R.id.tv_adetail_dlnum);
		tv_adetail_name = (TextView) findViewById(R.id.tv_adetail_name);
		tv_adetail_intro = (TextView) findViewById(R.id.tv_adetail_intro);
		proress_adetail_dl = (ProgressBar) findViewById(R.id.proress_adetail_dl);

		tv_adetail_dl = (TextView) findViewById(R.id.tv_adetail_dl);
		hlv_adetail_pic = (HorizontalListView) findViewById(R.id.hlv_adetail_pic);

		rl_adetail_content = (RelativeLayout) findViewById(R.id.rl_adetail_content);
		rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);

		sv_adetail = (MyScrollView) findViewById(R.id.sv_adetail);
		ibtn_left_home_back = (ImageButton) findViewById(R.id.ibtn_left_home_back);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		if (CheckUtils.getInstance().isNumber(DetailId)) {
			info = findDao.SelectInfoByAppid(DetailId);
		}
		state = (info != null && info.getState() == 2) ? 2 : 0;

		if (AppUtils.IsDownload(DetailId, DLAppManager.getInstance()
				.getFindInfo()) >= 0) {
			state = 1;
			tv_adetail_dl.setSelected(true);
			tv_adetail_dl.setText("正在下载");
		}

	}

	@Override
	protected void initNet() {
		isLoading();
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		httpHelp.sendGet(NetworkConfig.getFindDetail(DetailId),
				FindDetailBean.class, new MyRequestCallBack<FindDetailBean>() {

					@Override
					public void onSucceed(FindDetailBean bean) {
						if (bean == null) {
							return;
						}
						if ("1".equals(bean.status)) {
							loadingSuss();
							FDbean = bean;
							initLV();
							initData();
							setViewListener();
						} else {
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
						}
					}
				});
	}

	private void isLoading() {
		rl_adetail_content.setVisibility(View.GONE);
		rl_progress.setVisibility(View.VISIBLE);
	}

	private void loadingSuss() {
		rl_adetail_content.setVisibility(View.VISIBLE);
		rl_progress.setVisibility(View.GONE);
	}

	private void initLV() {
		if (detailAdapter == null) {
			detailAdapter = new DetailAdapter();
			hlv_adetail_pic.setAdapter(detailAdapter);
		} else {
			detailAdapter.notifyDataSetChanged();
		}
	}

	private void initData() {
		if (FDbean == null) {
			return;
		}
		httpHelp.showImage(riv_adetail_icon, FDbean.cont.pic);
		tv_adetail_name.setText(FDbean.cont.name);
		tv_adetail_dlnum.setText(FDbean.cont.num);
		tv_adetail_intro.setText(FDbean.cont.desc);
		// ratingBar.setRating((float) 4.5);
		ratingBar.setRating(CheckUtils.getInstance()
				.isNumber(FDbean.cont.stars) ? Float
				.parseFloat(FDbean.cont.stars) : 0);

	}

	private void setViewListener() {
		if (state == 1) {

		} else {
			if (state == 2) {
				tv_adetail_dl.setText("打开");
				// tv_adetail_dl.setSelected(true);
			}
			tv_adetail_dl.setOnClickListener(this);
		}
		ibtn_left_home_back.setOnClickListener(this);
	}

	class DetailAdapter extends BaseAdapter {

		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			if (FDbean == null || FDbean.cont == null
					|| FDbean.cont.piclist == null) {
				return 0;

			}
			return FDbean.cont.piclist.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(UIUtils.getContext(),
						R.layout.app_detail_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_adi_pic = (ImageView) convertView
						.findViewById(R.id.iv_adi_pic);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			httpHelp.showImage(viewHolder.iv_adi_pic,
					FDbean.cont.piclist.get(position));
			return convertView;
		}

	}

	class ViewHolder {
		public ImageView iv_adi_pic;
	}

	private void DLAPP() {
		if (!tv_adetail_dl.isSelected()) {
			tv_adetail_dl.setSelected(true);
			tv_adetail_dl.setText("正在下载");
		}
		long name = Long.parseLong((simpleDF.format(new Date()) + DetailId));
		String localUrl = GlobalConstant.DL_APK_PATH + name + ".apk";
		info = new FindInfo();
		info.setAppid(DetailId);
		info.setAppname(FDbean.cont.name);
		info.setApppackage(FDbean.cont.pname);
		info.setDownloadurl(FDbean.cont.downloadurl);
		info.setLocalurl(localUrl);
		info.setState(1);
		findDao.SaveDL(info);
		DLAppManager.getInstance().addFindInfo(info);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_adetail_dl:
			if (state == 2) {
				AppUtils.startAPP(info);
			} else if (state == 1) {

			} else {
				state = 1;
				DLAPP();
			}
			break;
		case R.id.ibtn_left_home_back:
			finish();
			break;
		default:
			break;
		}
	}
}
