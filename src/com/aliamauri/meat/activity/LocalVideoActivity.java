package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.db.localvideo.LocalVideo;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.utils.VideoUtils;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

public class LocalVideoActivity extends BaseActivity {
	public final static String TAG = "LocalVideoActivity";

	private PullToRefreshGridView ptr_alocalvideo_list;
	List<LocalVideo> LVBean;
	private VideoAdapter videoAdapter;

	private HttpHelp httpHelp;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				initLVData();
				break;

			default:
				break;
			}

		};
	};

	protected String getCurrentTitle() {
		return "本地视频";
	};

	@Override
	protected View getRootView() {
		return View.inflate(UIUtils.getContext(), R.layout.activity_localvideo,
				null);
	}

	@Override
	protected void initView() {
		ptr_alocalvideo_list = (PullToRefreshGridView) findViewById(R.id.ptr_alocalvideo_list);
		Thread daemon = new Thread(new VideoThread());
		daemon.setDaemon(true);
		daemon.start();
	}

	private HttpHelp getHttpHelp() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		return httpHelp;
	}

	private void initLVData() {
		if (videoAdapter == null) {
			videoAdapter = new VideoAdapter();
			ptr_alocalvideo_list.setAdapter(videoAdapter);
			ptr_alocalvideo_list
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							if (!((LVBean.get(position).size / 1024) / 1024 > 10)) {
								Intent intent = new Intent(
										LocalVideoActivity.this,
										UploadPhoneVideoActivity.class);
								Bundle bundle = new Bundle();
								bundle.putSerializable(
										GlobalConstant.INTENT_BUNDLE,
										LVBean.get(position));
								intent.putExtras(bundle);
								startActivity(intent);
							} else {
								UIUtils.showToast(UIUtils.getContext(),
										"上传文件不能大于10M");
							}
						}
					});
			// ptr_alocalvideo_list.setMode(Mode.DISABLED);
		} else {
			videoAdapter.notifyDataSetChanged();
		}
	}

	class VideoAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			if (LVBean == null) {
				LVBean = new ArrayList<LocalVideo>();
			}
			return LVBean.size();
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
						R.layout.item_activity_localvideo, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_ial_pic = (ImageView) convertView
						.findViewById(R.id.iv_ial_pic);
				viewHolder.tv_ial_time = (TextView) convertView
						.findViewById(R.id.tv_ial_time);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			getHttpHelp().showImage(viewHolder.iv_ial_pic,
					LVBean.get(position).imgPath);
			viewHolder.tv_ial_time.setText(LVBean.get(position).duration + "");
			return convertView;
		}
	}

	class ViewHolder {
		public ImageView iv_ial_pic;
		public TextView tv_ial_time;
	}

	class VideoThread implements Runnable {

		@Override
		public void run() {
			try {
				if (LVBean == null) {
					LVBean = new ArrayList<LocalVideo>();
				}
				LVBean.addAll(VideoUtils.getInstance().getList(
						UIUtils.getContext()));
				handler.sendEmptyMessage(1);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("sleep() interrupted");
			}
		}
	}

}
