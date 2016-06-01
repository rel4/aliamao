package com.aliamauri.meat.activity.search_activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.RecordPageBean;
import com.aliamauri.meat.bean.RecordPageBean.List_all;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.holder.AllViewholder;
import com.aliamauri.meat.holder.RecordTitleViewHolder;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.play.PlayActivity;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.RefreshListView;
import com.aliamauri.meat.view.RefreshListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 左侧菜单-记录功能页
 * 
 * @author Kevin
 * 
 */
@SuppressLint("CutPasteId")
public class RecordDetailActivity extends BaseActivity implements
		OnRefreshListener, OnItemClickListener, OnClickListener {

	private RefreshListView lv_left_record; // 显示播放记录的listview

	private boolean clickEdit;

	public static final int RECORD_TITLE = 0;// 标头类型
	public static final int RECORD_CONTENT = 2;// 内容类型

	private HttpHelp mHttpHelp;
	private ArrayList<String> recordTodayIcon; // 记录今天图片集合
	private ArrayList<String> recordWeekIcon; // 记录一周图片集合
	private ArrayList<String> recordOldIcon; // 记录更早图片集合

	private ArrayList<String> recordTodayNames; // 记录今天名字集合
	private ArrayList<String> recordWeekNames; // 记录一周名字集合
	private ArrayList<String> recordOldNames; // 记录更早名字集合

	private ArrayList<String> recordTodayTime; // 记录今天状态集合
	private ArrayList<String> recordWeekTime; // 记录一周状态集合
	private ArrayList<String> recordOldTime; // 记录更早状态集合

	private ArrayList<String> recordTodayFileID; // 记录今天视频id集合
	private ArrayList<String> recordWeekFileID; // 记录一周视频id集合
	private ArrayList<String> recordOldFileID; // 记录更早视频id集合

	private ArrayList<String> recordTodayID; // 记录今天id集合
	private ArrayList<String> recordWeekID; // 记录一周id集合
	private ArrayList<String> recordOldID; // 记录更早id集合

	private RecordAadater recordAdapter;
	private List<List_all> todayBeans; // 今天的记录bean
	private List<List_all> weekBeans; // 一周的记录bean
	private List<List_all> oldBeans; // 之前的记录bean
	private RecordPageBean mbean; //
	private View ll_base_page_delete_or_cancel, ibtn_left_home_edit;

	@Override
	protected View getRootView() {
		return UIUtils.inflate(R.layout.left_record);
	}

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

	protected void initView() {
		mHttpHelp = new HttpHelp();
		clickEdit = false;
		((TextView) findViewById(R.id.tv_title)).setText("记录");
		findViewById(R.id.btn_base_page_cancel).setOnClickListener(this);
		findViewById(R.id.btn_base_page_all_delete).setOnClickListener(this);
		lv_left_record = (RefreshListView) findViewById(R.id.lv_left_record);
		lv_left_record.setOnItemClickListener(this);
		ll_base_page_delete_or_cancel = findViewById(R.id.ll_base_page_delete_or_cancel);
		ibtn_left_home_edit = findViewById(R.id.ibtn_left_home_edit);
		ibtn_left_home_edit.setOnClickListener(this);
		ibtn_left_home_edit.setVisibility(View.VISIBLE);
		initPageData();
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "记录";
	}

	/**
	 * 第一次请求网络页面初始化
	 * 
	 * @param u
	 */
	private void initPageData() {

		mHttpHelp.sendGet(NetworkConfig.getRecordPage(loadMoviePage),
				RecordPageBean.class, new MyRequestCallBack<RecordPageBean>() {
					@Override
					public void onSucceed(RecordPageBean bean) {
						if (bean == null) {
							return;
						}
						initNetworkData(true, bean);
					}
				});

	}

	/**
	 * 加载网络数据
	 * 
	 * @param b
	 * @param bean
	 */
	private void initNetworkData(boolean b, RecordPageBean bean) {
		mbean = bean;
		if (b) {
			oldBeans = bean.cont.list_old;
		} else {
			oldBeans = bean.cont.list_old;
			if (oldBeans == null) {
				UIUtils.showToast(UIUtils.getContext(), "没有更多记录");
				return;
			}
			loadMoviePage++;
		}

		if (b) {
			todayBeans = bean.cont.list_today;
			weekBeans = bean.cont.list_week;
			// 记录图片集合
			recordTodayIcon = new ArrayList<String>();
			recordWeekIcon = new ArrayList<String>();
			recordOldIcon = new ArrayList<String>();
			// 记录名字集合
			recordTodayNames = new ArrayList<String>();
			recordWeekNames = new ArrayList<String>();
			recordOldNames = new ArrayList<String>();
			// 记录时间集合
			recordTodayTime = new ArrayList<String>();
			recordWeekTime = new ArrayList<String>();
			recordOldTime = new ArrayList<String>();

			// 记录视频id的集合
			recordTodayFileID = new ArrayList<String>();
			recordWeekFileID = new ArrayList<String>();
			recordOldFileID = new ArrayList<String>();

			// 记录id的集合
			recordTodayID = new ArrayList<String>();
			recordWeekID = new ArrayList<String>();
			recordOldID = new ArrayList<String>();

			recordTodayID.clear();
			recordWeekID.clear();
			recordOldID.clear();

			recordTodayFileID.clear();
			recordWeekFileID.clear();
			recordOldFileID.clear();

			recordTodayIcon.clear();
			recordWeekIcon.clear();
			recordOldIcon.clear();

			recordTodayNames.clear();
			recordWeekNames.clear();
			recordOldNames.clear();

			recordTodayTime.clear();
			recordWeekTime.clear();
			recordOldTime.clear();

			// 今天
			for (int i = 0; i < todayBeans.size(); i++) {
				recordTodayNames.add(todayBeans.get(i).film_name);
				recordTodayIcon.add(todayBeans.get(i).pic);
				recordTodayTime.add(todayBeans.get(i).time);
				recordTodayFileID.add(todayBeans.get(i).film_id);
				recordTodayID.add(todayBeans.get(i).id);
			}
			// 一周内的
			for (int i = 0; i < weekBeans.size(); i++) {
				recordWeekNames.add(weekBeans.get(i).film_name);
				recordWeekIcon.add(weekBeans.get(i).pic);
				recordWeekTime.add(weekBeans.get(i).time);
				recordWeekFileID.add(weekBeans.get(i).film_id);
				recordWeekID.add(weekBeans.get(i).id);
			}
		}
		if (oldBeans != null) {
			// 更早的
			for (int i = 0; i < oldBeans.size(); i++) {
				recordOldNames.add(oldBeans.get(i).film_name);
				recordOldIcon.add(oldBeans.get(i).pic);
				recordOldTime.add(oldBeans.get(i).time);
				recordOldFileID.add(oldBeans.get(i).film_id);
				recordOldID.add(oldBeans.get(i).id);
			}
		}

		setViewAdapter();

	}

	/**
	 * 设置展示的listview适配器
	 */
	private void setViewAdapter() {
		if (recordAdapter == null) {
			recordAdapter = new RecordAadater();
			lv_left_record.setAdapter(recordAdapter);
			lv_left_record.setOnRefreshListener(this);
		} else {
			recordAdapter.notifyDataSetChanged();
		}
	}

	private class RecordAadater extends BaseAdapter {
		AllViewholder viewholderContent;
		RecordTitleViewHolder viewholderTitle;

		@Override
		public int getItemViewType(final int position) {
			// if (position == 0
			// || position == recordTodayNames.size() + 1
			// || position == recordTodayNames.size()
			// + recordWeekNames.size() + 2) {
			// return RECORD_TITLE;
			// }
			return RECORD_CONTENT;

		}

		// @Override
		// public boolean isEnabled(int position) {
		// if (position == 0
		// || position == recordTodayNames.size() + 1
		// || position == recordTodayNames.size()
		// + recordWeekNames.size() + 2) {
		//
		// return false;
		// } else {
		// return true;
		// }
		// }

		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount();
		}

		@Override
		public int getCount() {
			return recordOldID == null ? 0 : recordOldID.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			if (convertView == null) {
				switch (itemViewType) {
				case RECORD_TITLE:
					viewholderTitle = new RecordTitleViewHolder();
					convertView = UIUtils.inflate(R.layout.item_record_head);

					viewholderTitle.recordTitleIcon = (TextView) convertView
							.findViewById(R.id.rl_itemrecord_circle);
					viewholderTitle.recordTitletext = (TextView) convertView
							.findViewById(R.id.tv_itemrecord_showtext);

					isShowTitleFrom(position, viewholderTitle.recordTitleIcon,
							viewholderTitle.recordTitletext);

					convertView.setTag(viewholderTitle);
					break;
				case RECORD_CONTENT:
					viewholderContent = new AllViewholder();
					convertView = UIUtils.inflate(R.layout.item_record_state);
					viewholderContent.recordLine = convertView
							.findViewById(R.id.v_item_record_line);
					viewholderContent.recordDeletedIcon = (TextView) convertView
							.findViewById(R.id.tv_record_redcircle);
					viewholderContent.recordIcon = (ImageView) convertView
							.findViewById(R.id.iv_item_record_icon);
					viewholderContent.recordTitle = (TextView) convertView
							.findViewById(R.id.tv_item_record_title);
					viewholderContent.recordTime = (TextView) convertView
							.findViewById(R.id.tv_item_record_state);

					isShowContentFrom(position, viewholderContent);

					convertView.setTag(viewholderContent);
					break;
				}
			} else {

				switch (itemViewType) {
				case RECORD_TITLE:
					viewholderTitle = (RecordTitleViewHolder) convertView
							.getTag();
					isShowTitleFrom(position, viewholderTitle.recordTitleIcon,
							viewholderTitle.recordTitletext);
					break;

				case RECORD_CONTENT:
					viewholderContent = (AllViewholder) convertView.getTag();
					isShowContentFrom(position, viewholderContent);
					break;
				}

			}

			return convertView;

		}

		/**
		 * 显示内容体的格式
		 * 
		 * @param position
		 * @param vhc
		 * @param line
		 * @param deleteIcon
		 * @param clear
		 */
		private void isShowContentFrom(int position, AllViewholder vhc) {

			if (position == recordTodayNames.size()
					|| position == recordTodayNames.size()
							+ recordWeekNames.size() + 1
					|| position == recordTodayNames.size()
							+ recordWeekNames.size() + recordOldNames.size()
							+ 2) {
				vhc.recordLine.setVisibility(View.GONE);
			} else {
				vhc.recordLine.setVisibility(View.VISIBLE);
			}

			// int position_t = position - 1; // 位置 --- 今天
			// int position_w = position - 2 - recordTodayNames.size(); // 位置
			// ---
			// // 一周内
			// int position_o = position - 3 - recordTodayNames.size()
			// - recordWeekNames.size(); // 位置 --- 更早
			//
			// LogUtil.e("record", "position_t=" + position_t + "//position_w"
			// + position_w + "//position_o=" + position_o);
			/*
			 * 判断当前位置，分情况显示条目内容
			 * 
			 * 此处判断当前position的范围，3种情况
			 * 
			 * 1-- 0 > position < recordTodayNames.size()+1 2--
			 * recordTodayNames.size() + 2 >= position <
			 * recordTodayNames.size()+ recordWeekNames.size() + 2 3--
			 * recordTodayNames.size()+ recordWeekNames.size() + 3 >= position <
			 * recordTodayNames.size()+ recordTodayNames.size()+
			 * recordWeekNames.size() + 3
			 */
			if (position > 0 && position < recordTodayNames.size()) {

				vhc.recordTitle.setText(recordTodayNames.get(position));
				vhc.recordTime.setText(recordTodayTime.get(position));
				mHttpHelp.showImage(vhc.recordIcon,
						recordTodayIcon.get(position));

			} else if (position >= recordTodayNames.size()
					&& position < recordTodayNames.size()
							+ recordWeekNames.size()) {

				vhc.recordTitle.setText(recordWeekNames.get(position));
				vhc.recordTime.setText(recordWeekTime.get(position));
				mHttpHelp.showImage(vhc.recordIcon,
						recordWeekIcon.get(position));

			} else if (position >= recordTodayNames.size()
					+ recordWeekNames.size()
					&& position < recordTodayNames.size()
							+ recordOldNames.size() + recordWeekNames.size()) {
				vhc.recordTitle.setText(recordOldNames.get(position));
				vhc.recordTime.setText(recordOldTime.get(position));
				mHttpHelp
						.showImage(vhc.recordIcon, recordOldIcon.get(position));
			}

			if (clickEdit) {
				vhc.recordDeletedIcon.setVisibility(View.VISIBLE);

			} else {
				vhc.recordDeletedIcon.setVisibility(View.GONE);
			}
		}

	}

	/**
	 * 显示标题的格式
	 * 
	 * @param position
	 * @param icon
	 * @param text
	 */
	private void isShowTitleFrom(final int position, TextView icon,
			TextView text) {
		if (position == 0) {
			icon.setBackgroundResource(R.drawable.circle_green);
			text.setText("今天");

		} else if (position == recordTodayNames.size() + 1) {
			icon.setBackgroundResource(R.drawable.circle_red);
			text.setText("一周");
		} else if (position == recordTodayNames.size() + recordWeekNames.size()
				+ 2) {
			icon.setBackgroundResource(R.drawable.circle_yellow);
			text.setText("更早");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!clickEdit) {
			if (recordOldFileID != null) {
				String fileID = recordOldFileID.get(position);
				if (!TextUtils.isEmpty(fileID)) {
					Intent intent = new Intent(this, PlayActivity.class);
					intent.putExtra(GlobalConstant.PLAY_VIDEO_ID, fileID);
					startActivity(intent);
				}
			}
		} else {
			if (recordOldID != null) {
				recordOldNames.remove(position);
				recordOldIcon.remove(position);
				recordOldFileID.remove(position);

				recordOldTime.remove(position);
				if (recordAdapter != null) {
					recordAdapter.notifyDataSetInvalidated();
					mHttpHelp.sendGet(NetworkConfig
							.getDelVideoHistory(recordOldID.get(position)),
							BaseBaen.class, null);
				}
				recordOldID.remove(position);
			}

		}
		// 条目所处的3处位置
		// int todayScope = 1 + recordTodayID.size();// 今天的范围内
		// int weekScope = 1 + recordTodayID.size() + 1 + recordWeekID.size();//
		// 一周内的范围内
		// int oldScope = 1 + recordTodayID.size() + 1 + recordWeekID.size() + 1
		// + recordOldID.size();// 更早的范围内
		//
		// if (!clickEdit) {
		// String value = null;
		// // 默认请款下点击条目进入播放界面
		// if (position > 0 && position < todayScope) {
		// value = recordTodayFileID.get(position - 1);
		// } else if (position > todayScope && position < weekScope) {
		// value = recordWeekFileID.get(position - todayScope - 1);
		// } else if (position > weekScope && position < oldScope) {
		// value = recordOldFileID.get(position - weekScope - 1);
		// }
		// Intent intent = new Intent(this, PlayActivity.class);
		// intent.putExtra(GlobalConstant.TV_ID, value);
		// startActivity(intent);
		//
		// } else {
		// if (UIUtils.isRunInMainThread()) {
		// // 点击编辑按钮后进入再次点击条目，进入删除状态
		// if (position > 0 && position < todayScope) {
		// // 删除今天的数据内容
		// deleteRecordDate(recordTodayID.get(position - 1));
		// recordTodayNames.remove(position - 1);
		// recordTodayIcon.remove(position - 1);
		// recordTodayFileID.remove(position - 1);
		// recordTodayTime.remove(position - 1);
		// recordTodayID.remove(position - 1);
		// recordAdapter.notifyDataSetChanged();
		// } else if (position > todayScope && position < weekScope) {
		// deleteRecordDate(recordWeekID
		// .get(position - todayScope - 1));
		// recordWeekNames.remove(position - todayScope - 1);
		// recordWeekIcon.remove(position - todayScope - 1);
		// recordWeekFileID.remove(position - todayScope - 1);
		// recordWeekID.remove(position - todayScope - 1);
		// recordWeekTime.remove(position - todayScope - 1);
		// recordAdapter.notifyDataSetChanged();
		// } else if (position > weekScope && position < oldScope) {
		// deleteRecordDate(recordOldID.get(position - weekScope - 1));
		// recordOldNames.remove(position - weekScope - 1);
		// recordOldIcon.remove(position - weekScope - 1);
		// recordOldFileID.remove(position - weekScope - 1);
		// recordOldID.remove(position - weekScope - 1);
		// recordOldTime.remove(position - weekScope - 1);
		// recordAdapter.notifyDataSetChanged();
		// }
		// } else {
		// LogUtil.e("ERROR", "**********此线程为后台线程*********");
		// }
		//
		// }

	}

	public void executeCancelOperate() {
		clickEdit = false;
		recordAdapter.notifyDataSetChanged();
	}

	public void executeAllDeleteOperate() {
		if (recordOldID != null) {

			deleteRecordDate(null);

			recordTodayIcon.clear();
			recordWeekIcon.clear();
			recordOldIcon.clear();
			recordTodayNames.clear();
			recordWeekNames.clear();
			recordOldNames.clear();

			recordTodayTime.clear();
			recordWeekTime.clear();
			recordOldTime.clear();

			recordTodayFileID.clear();
			recordWeekFileID.clear();
			recordOldFileID.clear();
			recordOldID.clear();
			if (recordAdapter != null) {
				recordAdapter.notifyDataSetChanged();
			}

		} else {

			UIUtils.showToast(this, "没有记录了~~");
		}

	}

	public void executeEditOperate() {
		clickEdit = true;
		if (recordAdapter != null) {
			recordAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 删除用户数据 两种状况 不为null单选 null全部
	 * 
	 * @param b
	 */
	String singleUrl = null;

	private void deleteRecordDate(String str) {
		if (str != null) {
			singleUrl = NetworkConfig.getRecordDelPage() + "&ids=" + str;
			deleteRecordDateFormNetwork(singleUrl);
		} else {
			singleUrl = NetworkConfig.getRecordDelPage() + "&action=deleteall";
			deleteRecordDateFormNetwork(singleUrl);
		}
	}

	/**
	 * 请求网洛删除网络端数据
	 */
	private void deleteRecordDateFormNetwork(String url) {
		mHttpHelp.sendGet(url, RecordPageBean.class,
				new MyRequestCallBack<RecordPageBean>() {

					@Override
					public void onSucceed(RecordPageBean bean) {
						if (bean != null) {

							if ("1".equals(bean.status)) {
								UIUtils.showToast(RecordDetailActivity.this,
										"删除成功~");
							}
						}

					}
				});
	}

	boolean isInitLoad = true;
	int loadMoviePage = 1;

	@Override
	public void onLoadMore() {
		mHttpHelp.sendGet(NetworkConfig.getRecordPage(loadMoviePage),
				RecordPageBean.class, new MyRequestCallBack<RecordPageBean>() {

					@Override
					public void onSucceed(RecordPageBean bean) {
						if (bean == null || bean.cont == null
								|| bean.cont.list_old == null
								|| bean.cont.list_old.size() == 0) {
							if (lv_left_record != null) {
								lv_left_record.onRefreashFinish();
							}
							return;
						}
						loadMoviePage++;
						initNetworkData(false, bean);
						if (lv_left_record != null) {
							lv_left_record.onRefreashFinish();
						}
					}
				});
		// String lUrl = "";
		//
		// if (isInitLoad) {
		// lUrl =
		// "http://mmmliaotian2.limaoso.com/msearch.php/Userrel/getVideoHistory"
		// + "&ucode=" + NetworkConfig.getUcode() + "";
		// tempUrl = lUrl;
		// isInitLoad = false;
		// } else {
		// lUrl = tempUrl + mHttpHelp.FLAG_PAGE + loadMoviePage;
		// lUrl = tempUrl + loadMoviePage;
		// }
		// lUrl = tempUrl + mHttpHelp.FLAG_PAGE + loadMoviePage;
		// mHttpHelp.sendGet(lUrl, RecordPageBean.class,
		// new MyRequestCallBack<RecordPageBean>() {
		//
		// @Override
		// public void onSucceed(RecordPageBean bean) {
		// lv_left_record.onRefreashFinish();
		// if (bean == null) {
		// return;
		// }
		// ; // 上拉加载的时候再次设置网络数据
		//
		// }
		// });

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_base_page_cancel:
			// 隐藏记录，缓存，收藏中的删除布局
			executeCancelOperate();
			ll_base_page_delete_or_cancel.setVisibility(View.GONE);
			ibtn_left_home_edit.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_base_page_all_delete:
			// 隐藏记录，缓存，收藏中的删除布局
			executeOperate();

			break;
		case R.id.ibtn_left_home_edit:
			// 显示记录，缓存，收藏中的删除布局
			executeEditOperate();
			ll_base_page_delete_or_cancel.setVisibility(View.VISIBLE);
			ibtn_left_home_edit.setVisibility(View.GONE);
			break;

		default:
			break;
		}

	}

	/**
	 * 弹出确认删除按钮
	 */
	private void executeOperate() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("确定全部删除么？");
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 执行记录，缓存，收藏中的全部删除操作
				executeAllDeleteOperate();
				ibtn_left_home_edit.setVisibility(View.VISIBLE);
				ll_base_page_delete_or_cancel.setVisibility(View.GONE);
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

}
