package com.aliamauri.meat.activity.search_activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.adapter.AppBaseAdapter;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.play.PlayActivity;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.NetWorkUtil;
import com.aliamauri.meat.utils.UIUtils;
import com.limaoso.phonevideo.db.CacheInfo;
import com.limaoso.phonevideo.db.CacheMessgeDao;
import com.limaoso.phonevideo.download.DownServerControl;
import com.limaoso.phonevideo.download.DownloadFileInfo;
import com.limaoso.phonevideo.p2p.P2PManager;
import com.limaoso.phonevideo.utils.SDUtils;
import com.umeng.analytics.MobclickAgent;

public class PlayCacheAcivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private ProgressBar pb;
	private ListView lv;
	private CacheAdapter mAdapter;
	private TextView ibtn_left_home_edit,tv_cache_size;
	private boolean isDeleteItem = false;// 是否是删除状态
	// private CacheMessgeDao messgeDao;
	public final static int VIEW_UPDATA = 1;
	public final static int VIEW_NOT_DATA_UPDATA = 2;
	public final static int VIEW_DETETE_UPDATA = 3;
	private final static int VIEW_DETETE_PROGRESS=4;
	private String TAG = "PlayCacheAcivity";
	private String deleteHash;
	public static PlayCacheAcivity instance;
	/*
	 * 获取下载数据
	 */
	private Map<String, DownloadFileInfo> allFilesInfos;
	private List<String> indexData;
	private DownServerControl mControl;
	private Runnable allDataTask;

	@Override
	protected int setActivityAnimaMode() {
		return 4;
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
	
	private Handler mHandler = new Handler() {
		

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VIEW_UPDATA:
				notifiChange();
				break;
			case VIEW_NOT_DATA_UPDATA:
				if (tv_not_data != null) {
					tv_not_data.setVisibility(View.VISIBLE);
				}
				break;

			case VIEW_DETETE_UPDATA:
				P2PManager.getInstance(getApplicationContext()).removeTask(deleteTask);
				if (pd!=null&&pd.isShowing()) {
					pd.cancel();
				}
				break;
			case VIEW_DETETE_PROGRESS:
				if (pd!=null&&pd.isShowing()) {
					pd.cancel();
				}
				break;
			}
		}
	};

	@Override
	protected void initView() {
		ibtn_left_home_edit = (TextView) findViewById(R.id.ibtn_left_home_edit);
		tv_cache_size = (TextView) findViewById(R.id.tv_cache_size);
		if (tv_cache_size!=null) {
			tv_cache_size.setText("   手机存储: 总空间"+NetWorkUtil.FormetFileSize(SDUtils.getCacheTotalAvailableSize(getApplicationContext()))+" / " +"剩余" +NetWorkUtil.FormetFileSize(SDUtils.getCacheAvailableSize(getApplicationContext())));
		}
		tv_not_data = findViewById(R.id.tv_not_data);
		ibtn_left_home_edit.setVisibility(View.VISIBLE);
		ibtn_left_home_edit.setOnClickListener(this);
		pb = (ProgressBar) rootView.findViewById(R.id.pb);
		if (pb!=null) {
			pb.setProgress((int) ((SDUtils.getCacheTotalAvailableSize(getApplicationContext())-SDUtils.getCacheAvailableSize(getApplicationContext()))*100/SDUtils.getCacheTotalAvailableSize(getApplicationContext())));
		}
		lv = (ListView) rootView.findViewById(R.id.lv);
		lv.setOnItemClickListener(this);
		messgeDao = new CacheMessgeDao(this);
		mControl = new DownServerControl();
		
		initTask();
		
		

	}
	@Override
	protected void onStart() {
		super.onStart();
		getAllData();
	}

	private void getDBData() {
		List<CacheInfo> cacheInfoList = messgeDao.getCacheInfoList();

		for (CacheInfo cacheInfo : cacheInfoList) {
			String tvHash = cacheInfo.getTvHash();
			if (TextUtils.isEmpty(tvHash) || allFilesInfos.containsKey(tvHash)) {
				continue;
			}
			DownloadFileInfo downloadFileInfo = new DownloadFileInfo();
			downloadFileInfo.fileSize = cacheInfo.getTvFileSize();
			LogUtil.e(TAG, "数据库数据  : " + tvHash);
			downloadFileInfo.fileSHA1 = tvHash;
			allFilesInfos.put(tvHash, downloadFileInfo);
			if (!indexData.contains(tvHash)) {
				indexData.add(tvHash);
			}

		}

	}

	/**
	 * 初始化对象
	 */
	private void initTask() {
		allDataTask = new Runnable() {
			@Override
			public void run() {
				try {

					if (allFilesInfos == null) {
						allFilesInfos = new HashMap<String, DownloadFileInfo>();
					}
					allFilesInfos.clear();
					if (indexData == null) {
						indexData = new ArrayList<String>();
					}
					indexData.clear();

					ArrayList<DownloadFileInfo> allFilesInfo = mControl
							.getAllFilesInfo();
					querCount++;
					if (allFilesInfo != null) {
						for (DownloadFileInfo downloadFileInfo : allFilesInfo) {
							if (TextUtils.isEmpty(downloadFileInfo.fileSHA1)) {
								continue;
							}
							LogUtil.e(TAG, "查询数据  : "
									+ downloadFileInfo.fileSHA1);
							// if (allFilesInfos
							// .containsKey(downloadFileInfo.fileSHA1)) {
							allFilesInfos.put(downloadFileInfo.fileSHA1,
									downloadFileInfo);
							if (!indexData.contains(downloadFileInfo.fileSHA1)) {
								indexData.add(downloadFileInfo.fileSHA1);

							}

							// }
							text(downloadFileInfo);

						}
					}

					if (allFilesInfos.size() != 0) {
						getDBData();
						getSingleData();
						mHandler.sendEmptyMessage(VIEW_DETETE_PROGRESS);
						return;
					}
					//
					if (querCount > 3) {
						getDBData();
						if (allFilesInfos.size() != 0) {
							getSingleData();
						} else {
							LogUtil.i(TAG, "查询三次后，没有数据，移除任务");
							P2PManager.getInstance(getApplicationContext()).removeTask(allDataTask);
							mHandler.sendEmptyMessage(VIEW_NOT_DATA_UPDATA);
						}
						mHandler.sendEmptyMessage(VIEW_DETETE_PROGRESS);
					}
				} catch (Exception e) {
					LogUtil.i(TAG, "allDataTask 崩溃 " + e.toString());
				}
			}
		};

		/**********************************************************/
		singleDataTask = new Runnable() {

			@Override
			public void run() {
				try {
					
					String taskName = P2PManager.getInstance(getApplicationContext()).
							getCurrentTaskName();
					if (TextUtils.isEmpty(taskName)) {
						P2PManager.getInstance(getApplicationContext()).removeTask(singleDataTask);
						mHandler.sendEmptyMessage(VIEW_UPDATA);
						return;
					}

					DownloadFileInfo filesInfo = mControl
							.getFilesInfo(taskName);
					filesInfo.fileSHA1 = taskName;
					LogUtil.i(TAG, "开始查询单个任务");
					// if (allFilesInfos.containsKey(filesInfo.fileHash)) {
					allFilesInfos.put(taskName, filesInfo);
					if (!indexData.contains(taskName)
							&& !TextUtils.isEmpty(taskName)) {
						indexData.add(taskName);
					}
					// }
					text(filesInfo);
					if (allFilesInfos.size() != 0) {
						mHandler.sendEmptyMessage(VIEW_UPDATA);
					}
				} catch (Exception e) {
					LogUtil.i(TAG, "singleDataTask 崩溃 " + e.toString());
				}

			}
		};
		deleteTask = new Runnable() {
			
			@Override
			public void run() {
				if (TextUtils.isEmpty(deleteHash)) {
					mHandler.sendEmptyMessage(VIEW_UPDATA);
					mHandler.sendEmptyMessage(VIEW_DETETE_UPDATA);
					LogUtil.e(TAG, "deleteHash为null");
					return;
				}
				DownloadFileInfo filesInfo = mControl.getFilesInfo(deleteHash);
				if (TextUtils.isEmpty(filesInfo.fileSHA1)&&TextUtils.isEmpty(filesInfo.fileMD4)&&TextUtils.isEmpty(filesInfo.filePath)) {
					messgeDao.deleteMessage(deleteHash);
					if (indexData.contains(deleteHash)) {
						indexData.remove(deleteHash);
					}
					if (allFilesInfos.containsKey(deleteHash)) {
						allFilesInfos.remove(deleteHash);
					}
					mHandler.sendEmptyMessage(VIEW_UPDATA);
					deleteHash=null;
					mHandler.sendEmptyMessage(VIEW_DETETE_UPDATA);
					LogUtil.e(TAG, "deleteHash完成");
					return;
				}
				querDeleteCount++;
				if (querDeleteCount>6) {
					messgeDao.deleteMessage(deleteHash);
					if (indexData.contains(deleteHash)) {
						indexData.remove(deleteHash);
					}
					if (allFilesInfos.containsKey(deleteHash)) {
						allFilesInfos.remove(deleteHash);
					}
					mHandler.sendEmptyMessage(VIEW_UPDATA);
					deleteHash=null;
					mHandler.sendEmptyMessage(VIEW_DETETE_UPDATA);
				
					LogUtil.e(TAG, "querDeleteCount完成");
					return;
				}
			}
		};
	}
	private int querDeleteCount=0;//删除此次

	private int querCount = 0;// 查询次数
	private Runnable singleDataTask;
	private ImageView iv_play_dowm;
	private CacheMessgeDao messgeDao;
	private View tv_not_data;
	private Runnable deleteTask;

	private void getAllData() {
		initProgress("正在加载中");
		if (singleDataTask != null) {
			P2PManager.getInstance(getApplicationContext()).removeTask(singleDataTask);
		}

		P2PManager.getInstance(getApplicationContext()).addQureyTask(allDataTask);
	}

	private void text(DownloadFileInfo downloadFileInfo) {
		if (downloadFileInfo == null) {
			return;
		}
		LogUtil.i(TAG, "********************start************************** ");
		LogUtil.i(TAG, "fileSHA1 : " + downloadFileInfo.fileSHA1);
		LogUtil.i(TAG, "filePath : " + downloadFileInfo.filePath);
		LogUtil.i(TAG, "fileMD4 : " + downloadFileInfo.fileMD4);
		LogUtil.i(TAG, "fileSize : " + downloadFileInfo.fileSize);
		LogUtil.i(TAG, "dlSize : " + downloadFileInfo.dlSize);
		LogUtil.i(TAG, "dlSpeed : " + downloadFileInfo.dlSpeed);
		LogUtil.i(TAG, "***********************end*********************** ");
	}

	private void getSingleData() {
		if (allDataTask != null) {
			P2PManager.getInstance(getApplicationContext()).removeTask(allDataTask);
		}
		P2PManager.getInstance(getApplicationContext()).addQureyTask(singleDataTask);
	}

	@Override
	protected View getRootView() {
		instance=this;
		return UIUtils.inflate(R.layout.play_cache_activity);
	}

	@Override
	protected String getCurrentTitle() {
		return "缓存";
	}

	public void notifiChange() {
		if (mAdapter == null) {
			mAdapter = new CacheAdapter();
			lv.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
		if (tv_cache_size!=null) {
			tv_cache_size.setText("   手机存储: 总空间"+NetWorkUtil.FormetFileSize(SDUtils.getCacheTotalAvailableSize(getApplicationContext()))+" / " +"剩余" +NetWorkUtil.FormetFileSize(SDUtils.getCacheAvailableSize(getApplicationContext())));
		}
		if (pb!=null) {
			pb.setProgress((int) ((SDUtils.getCacheTotalAvailableSize(getApplicationContext()) - SDUtils
					.getCacheAvailableSize(getApplicationContext())) * 100 / SDUtils.getCacheTotalAvailableSize(getApplicationContext())));
		}
	}

	private class CacheAdapter extends AppBaseAdapter {

		public CacheAdapter() {
			super(0);
		}

		@Override
		public int getCount() {
			return indexData == null ? 0 : indexData.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_cache_load, null);
				holder.setView(convertView);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (allFilesInfos != null) {
				String index  = indexData.get(position);
				if (allFilesInfos.containsKey(index)) {
					holder.setData(allFilesInfos.get(index));
				}
			}
			return convertView;
		}

	}

	private class ViewHolder implements OnClickListener {
		TextView tv_cacheload_name, tv_cacheload_progress, tv_cacheload_speed;
		TextView tv_cacheload_time, tv_cacheload_redcircle;
		ProgressBar pb_show_progress;
		private ImageView iv_play_dowm, iv_cacheload_pic;
		private HttpHelp mHttpHelp;

		public void setView(View convertView) {
			tv_cacheload_name = (TextView) convertView
					.findViewById(R.id.tv_cacheload_name);
			tv_cacheload_redcircle = (TextView) convertView
					.findViewById(R.id.tv_cacheload_redcircle);
			tv_cacheload_progress = (TextView) convertView
					.findViewById(R.id.tv_cacheload_progress);
			tv_cacheload_speed = (TextView) convertView
					.findViewById(R.id.tv_cacheload_speed);
			tv_cacheload_time = (TextView) convertView
					.findViewById(R.id.tv_cacheload_time);
			pb_show_progress = (ProgressBar) convertView
					.findViewById(R.id.pb_show_progress);
			iv_cacheload_pic = (ImageView) convertView
					.findViewById(R.id.iv_cacheload_pic);
			iv_play_dowm = (ImageView) convertView
					.findViewById(R.id.iv_play_dowm);
			iv_play_dowm.setOnClickListener(this);
			convertView.setTag(this);
		}

		public void setData(DownloadFileInfo downloadFileInfo) {
			tv_cacheload_speed.setText("速度 ： 0 KB/S");
			if (tv_cacheload_redcircle != null) {
				if (isDeleteItem) {
					tv_cacheload_redcircle.setVisibility(View.VISIBLE);
				} else {
					tv_cacheload_redcircle.setVisibility(View.GONE);
				}
			}
			if (downloadFileInfo == null  ) {
				return;
			}
			if (TextUtils.isEmpty(downloadFileInfo.fileSHA1)&&downloadFileInfo.fileSize <= 0) {
				return;
			}

			String fileHash = downloadFileInfo.fileSHA1;
			iv_play_dowm.setTag(fileHash);
			if (!TextUtils.isEmpty(fileHash)) {
				String taskName = P2PManager.getInstance(getApplicationContext()).getCurrentTaskName();
				if (fileHash.equals(taskName)) {
					iv_play_dowm.setBackgroundResource(R.drawable.cahe_pause);
				} else {
					iv_play_dowm.setBackgroundResource(R.drawable.cahe_down);
				}

			} else {
				iv_play_dowm.setBackgroundResource(R.drawable.cahe_down);
			}

			if (downloadFileInfo != null) {
				iv_play_dowm.setTag(downloadFileInfo.fileSHA1);
				if (downloadFileInfo.fileSize!=0) {
					tv_cacheload_time.setText(NetWorkUtil.FormetFileSize(downloadFileInfo.fileSize));
				}
				
				int progress;
				if (downloadFileInfo.fileSize == 0) {
					progress = 0;
				} else {
					progress = (int) (downloadFileInfo.dlSize * 100 / (downloadFileInfo.fileSize == 0 ? 1
							: downloadFileInfo.fileSize));
				}
				pb_show_progress.setProgress(progress);
				if (progress == 100) {
					iv_play_dowm.setVisibility(View.GONE);
				} else {
					iv_play_dowm.setVisibility(View.VISIBLE);
				}
				tv_cacheload_progress.setText(NetWorkUtil
						.FormetFileSize(downloadFileInfo.dlSize));
				tv_cacheload_speed.setText("速度 ： " + downloadFileInfo.dlSpeed
						+ " KB/S");
				CacheInfo cacheInfo = messgeDao.getCacheInfo(fileHash);
				if (cacheInfo != null) {
					showImage(iv_cacheload_pic, cacheInfo.getTvPicPath());
					tv_cacheload_name.setText(cacheInfo.getTvName());
				} else {
					String filePath = downloadFileInfo.filePath;
					tv_cacheload_name
							.setText(filePath.contains(File.separator) ? filePath
									.substring(filePath
											.lastIndexOf(File.separator) + 1)
									: filePath);
				}
			}

		}

		private void showImage(ImageView imageView, String picPath) {
			if (TextUtils.isEmpty(picPath)) {
				imageView.setImageDrawable(UIUtils.getContext()
						.getResources().getDrawable(R.drawable.default_logo));
				return;
			}
			if (mHttpHelp == null) {
				mHttpHelp = new HttpHelp();
			}
			mHttpHelp.showImage(imageView, picPath);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_play_dowm:
				if (iv_play_dowm == null) {
					return;
				}
				Object tag2 = iv_play_dowm.getTag();
				if (tag2 instanceof String) {
					String fileHash = (String) tag2;
					if (!TextUtils.isEmpty(fileHash)
							&& fileHash.equals(P2PManager.getInstance(getApplicationContext()).
									getCurrentTaskName())) {
						P2PManager.getInstance(getApplicationContext()).stopCachePageFile(fileHash);
						iv_play_dowm
								.setBackgroundResource(R.drawable.cahe_down);
						tv_cacheload_speed.setText("速度 ： 0 KB/S");
						LogUtil.i(TAG, "停止下载 ： " + fileHash);
					} else {
						P2PManager.getInstance(getApplicationContext()).startCachePageFile(fileHash);
						iv_play_dowm
								.setBackgroundResource(R.drawable.cahe_pause);
						LogUtil.i(TAG, "开始缓存 ： " + fileHash);
						getSingleData();
					}
					//
				}
				break;

			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left_home_edit:
			if (ibtn_left_home_edit != null) {
				isDeleteItem = !isDeleteItem;
				if (isDeleteItem) {
					if (ibtn_left_home_edit != null) {
						ibtn_left_home_edit.setText("取消");
					}
				} else {
					if (ibtn_left_home_edit != null) {
						ibtn_left_home_edit.setText("编辑");
					}
				}

				mHandler.sendEmptyMessage(VIEW_UPDATA);

			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		if (singleDataTask != null) {
			P2PManager.getInstance(getApplicationContext()).removeTask(singleDataTask);
		}
		if (allDataTask != null) {
			P2PManager.getInstance(getApplicationContext()).removeTask(allDataTask);
		}
		instance=null;
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String hash = indexData.get(position);
		if (TextUtils.isEmpty(hash)) {
			return;
		}
		if (isDeleteItem) {
			if (progressShow) {
				UIUtils.showToast(UIUtils.getContext(), "正在删除中...");
			}else {
				initProgress("正在删除中...");
				deleteHash =hash;
				P2PManager.getInstance(getApplicationContext()).deleteFile(hash);
				querDeleteCount=0;
				boolean isAddQureyTask = P2PManager.getInstance(getApplicationContext()).addQureyTask(deleteTask);
				LogUtil.e(TAG, "删除 ： " + indexData.get(position) + "   第 "
						+ position + " 个");
				if (isAddQureyTask) {
					LogUtil.e(TAG, "添加删除任务成功");
				}else {
					LogUtil.e(TAG, "添加删除任务失败");
				}
			}
			
		}else {
			if (allFilesInfos.containsKey(hash)) {
					CacheInfo cacheInfo = messgeDao.getCacheInfo(hash);
					if (cacheInfo==null) {
						DownloadFileInfo downloadFileInfo = allFilesInfos.get(hash);
						String fileSHA1 = downloadFileInfo.fileSHA1;
						if (TextUtils.isEmpty(fileSHA1)) {
							return;
						}
						CacheInfo saveinfo =new CacheInfo();
						saveinfo.setTvHash(fileSHA1);
						String filePath = downloadFileInfo.filePath;
						saveinfo.setTvName(filePath.contains(File.separator) ? filePath.substring(filePath.lastIndexOf(File.separator) + 1): filePath);
						saveinfo.setTvDownFileSize(downloadFileInfo.fileSize);
						messgeDao.saveMessage(saveinfo);
					}
					Intent intent = new Intent(this, PlayActivity.class);
					intent.putExtra(GlobalConstant.FLAG_PALY_CACHE_HASH, hash);
					startActivity(intent);
			}
		}

	}
	private ProgressDialog pd;
	private boolean progressShow =false;
	private void initProgress(String des) {
		if (lv!=null) {
			lv.setClickable(false);
			lv.setFocusable(false);
		}
		progressShow=true;
		
		pd = new ProgressDialog(PlayCacheAcivity.this);
		pd.setCanceledOnTouchOutside(false);
		
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow=false;
				if (lv!=null) {
					lv.setClickable(true);
					lv.setFocusable(true);
				}
			}
		});
		pd.setMessage(des);
		pd.show();
	}
	

	

}
