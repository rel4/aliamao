package com.aliamauri.meat.fragment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.UploadDynamicData;
import com.aliamauri.meat.activity.FriendDataActivity;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.activity.OtherDataActivity;
import com.aliamauri.meat.activity.find_activity.TjyyActivity;
import com.aliamauri.meat.activity.nearby_activity.Details_DT;
import com.aliamauri.meat.activity.nearby_activity.Retrans_DT;
import com.aliamauri.meat.activity.nearby_activity.ShowDetailsImages;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.DtBean;
import com.aliamauri.meat.bean.DtBean.Cont.Recapp;
import com.aliamauri.meat.bean.DtBean.Cont.Recgroup;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist;
import com.aliamauri.meat.db.DbManager;
import com.aliamauri.meat.db.Dbutils;
import com.aliamauri.meat.db.Dynamic_db.DynamicShowDao;
import com.aliamauri.meat.db.Dynamic_db.model.Dynamic_model;
import com.aliamauri.meat.db.Dynamic_db.model.HomeTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.RelayDynamicTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.eventBus.BroadCast_OK;
import com.aliamauri.meat.eventBus.DynamicUpdateUi;
import com.aliamauri.meat.eventBus.RefurbishDTItem;
import com.aliamauri.meat.eventBus.UpdateDynamicLists;
import com.aliamauri.meat.eventBus.UpdateShowMsg;
import com.aliamauri.meat.eventBus.sendDownLoadedOrder;
import com.aliamauri.meat.fragment.impl_supper.NearbyPage;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.listener.VoicePlay;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.MyBDmapUtlis;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.MyGridView;
import com.aliamauri.meat.view.RefreshViewUpAndDonw;
import com.aliamauri.meat.view.RefreshViewUpAndDonw.OnRefreshListener;
import com.aliamauri.meat.weight.MyDialog;
import com.baidu.mapapi.model.LatLng;

import de.greenrobot.event.EventBus;

/**
 * 中间标题头-----3级fragment基类
 * 
 * @author limaokeji-windosc
 * 
 * 
 *         第三级fragment 最新动态
 * 
 * @author limaokeji-windosc
 * 
 *         动态详情展示分为3种状态，好友，自己，匿名的状态。
 * 
 *         三种状态 DT_FRIED_TAG 好友 DT_MY_TAG 自己 DT_ANONYMOUS_TAG 匿名
 * 
 *         注朋友圈没有匿名状态 `
 * 
 */
public abstract class BaseFragment_grandson_dt extends Fragment implements
		OnRefreshListener, OnItemClickListener {
	private final String TAG = "BaseFragment_grandson_dt";
	private final String DOWN_STATUS = "down_status"; // 向下拉动
	private final String UP_STATUS = "up_status"; // 向上拉动
	private String mCurrent_status; // 当前的状态
	public Activity mActivity;
	private String mWd; // 伟度
	private String mJd; // 经度
	private final int TYPE_DT = 0; // 动态类型
	private final int TYPE_HT = 1; // 话题类型
	private final int TYPE_YY = 2; // 应用类型
	private final String OPEN_TAG = "open";
	private final String CLOSE_TAG = "close";
	private final String NOTIFY_TAG = "notify";
	private String DYNAMIC_NEW = "new"; // 获取最新动态字段值
	private String DYNAMIC_OLD = "old"; // 获取最以前动态字段值
	private final String USER_DYNAMIC = "1"; // 用户发布的动态
	private final String USER_TRANSMIT = "2"; // 用户转发的动态
	private final String USER_VIDEO = "3"; // 用户对视频评论的动态（预留）
	private final String USER_RECORD = "4"; // 用户的观看记录 （预留）
	private final int VIEW_TYPE_COUNT = 3; // 条目类型总数
	private RefreshViewUpAndDonw mLv; // 展示内容的listview控件
	private MyBaseAdapter mAdapter;
	private TextView mBtn_popup_delete; // 动态条目弹窗 popup删除
	private TextView mBtn_popup_dz; // 动态条目弹窗 popup点赞
	private TextView mBtn_popup_message; // 动态条目弹窗 popup信息
	private TextView mBtn_popup_retrans; // 动态条目弹窗 popup转发
	private FrameLayout mFl_loading;
	private PopupWindow mPopupWindow;
	private HttpHelp mHttp;
	private int height; // 屏幕的高度
	private int width; // 屏幕的宽度
	private String mUser_Id; // 本机用户的uID
	private String mUser_nickname; // 本机用户的网名
	private int mChildType; // 获取当前子类类型的type
	private List<Dynamic_model> mAllDynamics = null; // 存储从数据库获取的用户动态对象
	private ArrayList<String> mAllDynamicIds = null; // 数据库索引表中获取所有id的值

	/**
	 * 获取子类的url
	 * 
	 * @return
	 */
	public abstract String getUrl(String action, String curid);

	/**
	 * 获取每个子类的类型
	 * 
	 * @return
	 */
	public abstract int getType();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION,
				"0&&0").split("&&");
		mWd = String.valueOf(location[0]);
		mJd = String.valueOf(location[location.length - 1]);
		EventBus.getDefault().register(this);
		WindowManager ss = (WindowManager) mActivity
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		ss.getDefaultDisplay().getMetrics(dm);
		height = dm.heightPixels;
		width = dm.widthPixels;
		mHttp = new HttpHelp();
		mCurrent_status = UP_STATUS;
		mOffSet = 1;
		mVoicePlay = new VoicePlay();
		mIsplay = true;
		mCurrentRequestNet = true;
		isNewDynamic = false;
	}

	@Override
	public void onDestroy() {

		EventBus.getDefault().unregister(this);
		// 取消当前的网络请求操作
		if (mHttp != null) {
			mHttp.stopHttpNET();
		}
		if (mAllDynamicIds != null) {
			mAllDynamicIds.clear();
			mAllDynamicIds = null;
		}
		if (mAllDynamics != null) {
			mAllDynamics.clear();
			mAllDynamics = null;
		}
		if (mAdapter != null) {
			mAllDynamics = null;
		}
		super.onDestroy();
	}

	/**
	 * 删除(屏蔽)动态通知页面更新
	 * 
	 * @param event
	 */
	public void onEventMainThread(RefurbishDTItem event) {
		if (event == null) {
			return;
		}
		switch (event.getTag()) {
		case GlobalConstant.SHIELD_TAG: // 屏蔽动态
			Log.e(TAG, "********event********屏蔽动态***************");
			if (deleteDynamicAccordingAsUid(event.getId())) {
				Iterator<Dynamic_model> dynamics = mAllDynamics.iterator();
				while (dynamics.hasNext()) {
					Dynamic_model dynamic = (Dynamic_model) dynamics.next();
					if (event.getId().equals(dynamic.uid)) {
						dynamics.remove();
					}
				}
				mAdapter.notifyDataSetChanged();
			}
			break;
		case GlobalConstant.DELETE_TAG: // 删除动态
			Log.e(TAG, "********event********删除动态***************");
			deleteDynamicAccordingAsId(event.getId());
			Iterator<Dynamic_model> iterator = mAllDynamics.iterator();
			while (iterator.hasNext()) {
				Dynamic_model tlist = iterator.next();
				if (event.getId().equals(tlist.id)) {
					iterator.remove();
					mAdapter.notifyDataSetChanged();
					return;
				}
			}

			break;
		default:
			Log.d(TAG, "********event********删除(屏蔽)动态没有获取到tag***************");
			break;
		}
	}

	/**
	 * 删除本地数据库动态数据根据当前用户的uid
	 */
	private boolean deleteDynamicAccordingAsUid(String uid) {
		// 先查询主表中是否含有该uid的id数据，
		ArrayList<String> ids = DynamicShowDao.getInstance().getListItem(
				DynamicShowDao.TABLE_NAME_HOME, DynamicShowDao.COLUMN_NAME_UID,
				new String[] { uid }, DynamicShowDao.COLUMN_NAME_ID);
		if (ids != null && ids.size() > 0) {
			// 将索引表中的指定的id删除(更新为null)----最新动态列
			DynamicShowDao.getInstance().singleItemDelete_index(
					DynamicShowDao.COLUMN_NAME_NEW_DYNAMIC_ID, ids);
			// 将索引表中的指定的id删除(更新为null)----朋友圈列
			DynamicShowDao.getInstance().singleItemDelete_index(
					DynamicShowDao.COLUMN_NAME_FRIEND_DYNAMIC_ID, ids);
			// 将索引表中的指定的id删除(更新为null)----最热动态列
			DynamicShowDao.getInstance().singleItemDelete_index(
					DynamicShowDao.COLUMN_NAME_HOT_DYNAMIC_ID, ids);
			DynamicShowDao.getInstance().singleItemDelete_uid(uid);
		}
		if (ids != null && ids.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置动态页面上的 转发，评价，点赞 ，阅读 的显示数字 以及修改用户名字
	 */
	public void onEventMainThread(UpdateShowMsg event) {		
		if (event == null) {
			return;
		}
		if(FriendDataActivity.UPDATE_USERNAME_TAG.equals(event.getmTag())){
			for(int i=0 ; i<mAllDynamics.size() ; i++){
				if(event.getId().equals(mAllDynamics.get(i).uid)){
					mAllDynamics.get(i).nickname = event.getmData();
				}
			}
			mAdapter.notifyDataSetChanged();
			return;
		}
		if (GlobalConstant.UPDATEUSERNAME.equals(event.getId())) { // 更新用户的名字

			mUser_nickname = PrefUtils.getString(GlobalConstant.USER_NICKNAME,"");

			mAdapter.notifyDataSetChanged();
			return;
		}
		if (event.getDz() != null) {
			if (mTempHolder != null) {
				mTempHolder.tv_dynamic_praise.setText(String.valueOf(event
						.getDz() + getTitleName(R.string.text_dynamic_praise)));
				mTempHolder = null;
			}
			for (int i = 0; i < mAllDynamics.size(); i++) {
				if (event.getId().equals(mAllDynamics.get(i).id)) {
					mAllDynamics.get(i).dz = event.getDz();
					mAllDynamics.get(i).iszan = "1";// 标记已经点过赞了
					if (mTempHolder == null) {
						mAdapter.notifyDataSetChanged();
					}
					break;
				}
			}
			if (mAllDynamicIds.contains(event.getId())) {
				DynamicShowDao.getInstance().singleItemUpdate_home(
						event.getId(), DynamicShowDao.COLUMN_NAME_DZ,
						event.getDz());
				DynamicShowDao.getInstance().singleItemUpdate_home(
						event.getId(), DynamicShowDao.COLUMN_NAME_ISZAN, "1");
			}
		}
		if (event.getPj() != null) {

			for (int i = 0; i < mAllDynamics.size(); i++) {
				if (event.getId().equals(mAllDynamics.get(i).id)) {
					mAllDynamics.get(i).pj = event.getPj();
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
			if (mAllDynamicIds.contains(event.getId())) {
				DynamicShowDao.getInstance().singleItemUpdate_home(
						event.getId(), DynamicShowDao.COLUMN_NAME_PJ,
						event.getPj());
			}
		}
		if (event.getYd() != null) {

			for (int i = 0; i < mAllDynamics.size(); i++) {
				if (event.getId().equals(mAllDynamics.get(i).id)) {
					mAllDynamics.get(i).yd = event.getYd();
					mAdapter.notifyDataSetChanged();
					break;
				}
			}

			if (mAllDynamicIds.contains(event.getId())) {
				DynamicShowDao.getInstance().singleItemUpdate_home(
						event.getId(), DynamicShowDao.COLUMN_NAME_YD,
						event.getYd());
			}
		}
		if (event.getZf() != null) {
			String zf_num = null;
			for (int i = 0; i < mAllDynamics.size(); i++) {
				if (event.getId().equals(mAllDynamics.get(i).id)) {
					int zf = Integer.parseInt(mAllDynamics.get(i).zf);
					int Addzf = Integer.parseInt(event.getZf());
					zf_num = String.valueOf(zf + Addzf);
					mAllDynamics.get(i).zf = zf_num;
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
			if (mAllDynamicIds.contains(event.getId())) {
				DynamicShowDao.getInstance().singleItemUpdate_home(
						event.getId(), DynamicShowDao.COLUMN_NAME_ZF, zf_num);
			}
		}
	}

	/**
	 * 根据当前状态进行界面更新操作
	 * 
	 * @param event
	 */
	public void onEventMainThread(DynamicUpdateUi event) {
		if (event == null) {
			return;
		}
		switch (event.getTag()) {
		case OPEN_TAG:
			mLv.currentRefersh();// 显示请求网络的布局
			break;
		case CLOSE_TAG:
			mLv.onRefreshComplete();
			break;
		case NOTIFY_TAG:
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
				if (isNewDynamic) {// 当为用户发布状态的时候，让lv显示最上端
					isNewDynamic = false;
					mLv.setSelection(0);
				}
			} else {
				mAdapter = new MyBaseAdapter();
				mLv.setAdapter(mAdapter);
			}
			/*
			 * 当前本地数据库中有数据的时候，显示完毕后就在次请求网络请求新的数据
			 */
			if (mAllDynamics.size() > 0 && mCurrentRequestNet) {
				mCurrentRequestNet = false;
				RefurbishData();
			} else {
				mLv.onRefreshComplete();
			}
			break;
		}
	}

	private boolean isNewDynamic = false; // 当前用户发布动态的状态

	/**
	 * 发布新动态通知页面更新
	 */
	public void onEventMainThread(BroadCast_OK event) {
		if (event == null) {
			return;
		}
		isNewDynamic = true;
		mOffSet = 1; // 重置取值范围
		mCurrent_status = DOWN_STATUS; // 将状态改为下拉刷新状态
		new Thread(new Runnable() {
			@Override
			public void run() {
				getLvDateFromDb();
			}
		}).start();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mUser_Id = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_ID, "");
		mUser_nickname = PrefUtils.getString(GlobalConstant.USER_NICKNAME, "");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mAdapter = null;
		mAllDynamics = new ArrayList<Dynamic_model>();
		mAllDynamics.clear();
		View view = inflater.inflate(R.layout.fragment_grandson_zxdt,
				container, false);
		mChildType = getType();
		mLv = $(view, R.id.lv_zxdt_content);
		mFl_loading = $(view, R.id.fl_loading);
		mAdapter = new MyBaseAdapter();
		mLv.setAdapter(mAdapter);
		mLv.setOnItemClickListener(this);
		mLv.setOnRefreshListener(BaseFragment_grandson_dt.this);
		// 通过子线程获取数据库的数据

		new Thread(new Runnable() {
			@Override
			public void run() {
				getLvDateFromDb();
			}
		}).start();
		return view;
	}

	/**
	 * 从数据库中获取到listview的数据 有数据--读取---显示（同时：请求网络---存储数据库--添加集合--刷新界面）
	 * 没有数据--请求网络--存储数据库--添加集合---刷新界面
	 */
	private void getLvDateFromDb() {
		// 根据索引表来获取相应类型的数据
		switch (mChildType) {
		case GlobalConstant.TYPE_ZXDT:
			formDdGetDate_dt_down(DynamicShowDao.COLUMN_NAME_NEW_DYNAMIC_ID);
			break;
		case GlobalConstant.TYPE_ZRDT:
			formDdGetDate_dt_down(DynamicShowDao.COLUMN_NAME_HOT_DYNAMIC_ID);
			break;
		case GlobalConstant.TYPE_PYQ:
			formDdGetDate_dt_down(DynamicShowDao.COLUMN_NAME_FRIEND_DYNAMIC_ID);
			break;
		}

	}

	private List<Dynamic_model> mDynamics = null; // 临时存储从数据库获取的用户动态对象
	private int mOffSet; // 记录每次从数据库中获取的位置值（用于上拉加载更多）
	private boolean mCurrentRequestNet;// 用户第一次安装以及每次打开关于动态页面的时候请求网络刷新数据

	/**
	 * 从数据库中获取动态数据 有数据添加到集合中，，没有进行网络请求
	 * 
	 * @param size
	 *            显示的条数（请求网络的时候）
	 * @param colName
	 *            所查的列数名
	 */
	private void formDdGetDate_dt_down(String colName) {
		mAllDynamicIds = DynamicShowDao.getInstance().getItemList_index_desc(
				colName);// 存储从数据库中获取的用户动态索引
		// 每次打开关于动态页面的时候请求网络刷新数据
		if (mAllDynamicIds != null && mAllDynamicIds.size() > 0) { // 数据库中有本地的数据
			int i = 0;
			if (mAllDynamicIds.size() >= 10) { // 如果数据库中的数据有10，，每次显示10条
				i = 10;
			} else {
				i = mAllDynamicIds.size(); // 不满10条按最大数显示
			}
			if (mOffSet * i > mAllDynamicIds.size()) {
				if ((mOffSet - 1) * i < mAllDynamicIds.size()) {
					addDynamic_lists(mAllDynamicIds.subList((mOffSet - 1) * i,
							mAllDynamicIds.size()));
				} else { // 索引表中没有索引了
					switch (mChildType) {
					case GlobalConstant.TYPE_ZXDT:
						initNet_loadMore(GlobalConstant.TYPE_ZXDT);
						break;
					case GlobalConstant.TYPE_ZRDT:
						EventBus.getDefault().post(
								new DynamicUpdateUi(CLOSE_TAG));
						break;
					case GlobalConstant.TYPE_PYQ:
						initNet_loadMore(GlobalConstant.TYPE_PYQ);
						break;
					}
				}
			} else {
				List<String> subList = mAllDynamicIds.subList(
						(mOffSet - 1) * i, mOffSet * i);
				addDynamic_lists(subList);
			}
		} else if (mCurrentRequestNet) {
			mCurrentRequestNet = false;
			// 数据库中没有动态缓存刷新数据
			RefurbishData();
		}
	}

	/**
	 * 显示刷新布局刷新数据
	 */
	private void RefurbishData() {

		EventBus.getDefault().post(new DynamicUpdateUi(OPEN_TAG));
	}

	/**
	 * 根据索引表的获取的id,在主表中找到对应的id值对应的动态条目添加到tlistBean中
	 * 
	 * @param indexs
	 */
	private void addDynamic_lists(List<String> indexs) {
		Dbutils dbutils = new Dbutils();
		Dynamic_model dynamic = null; // 存储数据库中单条数据的记录
		// 根据索引表的获取的id,在主表中找到对应的id值对应的动态条目添加到tlistBean中
		List<HomeTable_model> homeModels = DynamicShowDao.getInstance()
				.getListItems_dynamics(DynamicShowDao.COLUMN_NAME_ID,
						indexs.toArray(new String[indexs.size()]));
		mDynamics = new ArrayList<>();

		for (int i = 0; homeModels != null && i < homeModels.size(); i++) {
			dynamic = new Dynamic_model();
			dynamic.createtime = dbutils.format_date(Long.parseLong(homeModels
					.get(i).getCreatetime()));
			dynamic.distance = homeModels.get(i).getDistance();
			dynamic.dz = homeModels.get(i).getDz();
			if (homeModels.get(i).getLocalFace() != null
					&& new File(homeModels.get(i).getLocalFace()).exists()) {// 判断当前是否含有本地的头像路径
				dynamic.face = homeModels.get(i).getLocalFace();
			} else if (homeModels.get(i).getFace() != null) {
				dynamic.face = homeModels.get(i).getFace() + "##";
			}
			dynamic.iszan = homeModels.get(i).getIszan();
			dynamic.id = homeModels.get(i).getId();
			dynamic.infos = homeModels.get(i).getInfos();
			dynamic.update_type = homeModels.get(i).getUpdate_type();
			dynamic.isnm = homeModels.get(i).getIsnm();
			dynamic.isopen = homeModels.get(i).getIsopen();
			dynamic.jd = homeModels.get(i).getJd();
			dynamic.nickname = homeModels.get(i).getNickname();
			dynamic.pj = homeModels.get(i).getPj();
			dynamic.relinfo = homeModels.get(i).getRelinfo();
			dynamic.tags = homeModels.get(i).getTags();
			dynamic.mmtype = homeModels.get(i).getMmtype();
			dynamic.type = homeModels.get(i).getType();
			dynamic.uid = homeModels.get(i).getUid();
			dynamic.wd = homeModels.get(i).getWd();
			dynamic.yd = homeModels.get(i).getYd();
			dynamic.zf = homeModels.get(i).getZf();
			dynamic.zfinfo = homeModels.get(i).getZfinfo();
			// 判断是否有照片数据
			if (homeModels.get(i).getImgs() != null) {
				dynamic.imgs = dbutils.getImgsDate_db(homeModels.get(i)
						.getImgs());
			}
			// 判断是否有语音数据
			if (homeModels.get(i).getVoices() != null) {
				dynamic.voices = dbutils.getVoicesDate_db(homeModels.get(i)
						.getVoices());
			}
			// 判断是否有视频数据
			if (homeModels.get(i).getVideos() != null) {
				dynamic.videos = dbutils.getVideosDate_db(homeModels.get(i)
						.getVideos());
			}
			// 判断当前是否有转发动态
			dynamic.zfinfox = homeModels.get(i).getZfinfox();
			if (dynamic.zfinfox != null) {// 有转发动态
				RelayDynamicTable_model relayDynamic = DynamicShowDao
						.getInstance()
						.getSingleItem_relayDynamic(
								DynamicShowDao.COLUMN_NAME_ID,
								new String[] { homeModels.get(i).getZfinfox() });
				if (relayDynamic != null) {
					dynamic.zf_createtime = relayDynamic.getCreatetime();
					dynamic.zf_dz = relayDynamic.getDz();
					dynamic.zf_flid = relayDynamic.getFlid();
					dynamic.zf_fnickname = relayDynamic.getFnickname();
					dynamic.zf_fuid = relayDynamic.getFuid();
					dynamic.zf_id = relayDynamic.getId();
					dynamic.zf_infos = relayDynamic.getInfos();
					dynamic.zf_isnm = relayDynamic.getIsnm();
					dynamic.zf_isopen = relayDynamic.getIsopen();
					dynamic.zf_jd = relayDynamic.getJd();
					dynamic.zf_olid = relayDynamic.getOlid();
					dynamic.zf_onickname = relayDynamic.getOnickname();
					dynamic.zf_ouid = relayDynamic.getOuid();
					dynamic.zf_pj = relayDynamic.getPj();
					dynamic.zf_relinfo = relayDynamic.getRelinfo();
					dynamic.zf_tags = relayDynamic.getTags();
					dynamic.zf_type = relayDynamic.getType();
					dynamic.zf_uid = relayDynamic.getUid();
					dynamic.zf_wd = relayDynamic.getWd();
					dynamic.zf_yd = relayDynamic.getYd();
					dynamic.zf_zf = relayDynamic.getZf();
					dynamic.zf_zfinfo = relayDynamic.getZfinfo();
					// 判断是否有转发照片数据
					if (relayDynamic.getImgs() != null) {
						dynamic.zf_imgs = dbutils.getImgsDate_db(relayDynamic
								.getImgs());
					}
					// 判断是否有转发语音数据
					if (relayDynamic.getVoices() != null) {
						dynamic.zf_voices = dbutils
								.getVoicesDate_db(relayDynamic.getVoices());
					}
					// 判断是否有转发视频数据
					if (relayDynamic.getVideos() != null) {
						dynamic.zf_videos = dbutils
								.getVideosDate_db(relayDynamic.getVideos());
					}
				}
			}

			mDynamics.add(0, dynamic);
		}
		/*
		 * 根据当前状态添加集合数据
		 * 
		 * 下拉刷新 ： 添加到集合的最前面 加载更多：添加到集合的最后面
		 */
		UIUtils.runInMainThread(new Runnable() {

			@Override
			public void run() {
				switch (mCurrent_status) {
				case DOWN_STATUS:// 下拉刷新
					if (mAllDynamics == null) {
						return;
					}
					mAllDynamics.clear();
					mAllDynamics.addAll(0, mDynamics);
					break;
				case UP_STATUS:// 加载更多
					if (mAllDynamics == null) {
						return;
					}
					mAllDynamics.addAll(mDynamics);
					break;
				}
			}
		});

		EventBus.getDefault().post(new DynamicUpdateUi(NOTIFY_TAG));

	}

	private boolean mIsplay = true; // 播放或停止，
	private VoicePlay mVoicePlay = null; // 播放语音设备
	private TextView mVoiceBtn;// 播放语音按钮

	/**
	 * 点击播放语音，在次点击停止播放
	 * 
	 * @param voices_model
	 * @param dt
	 */
	public void playVideo(Voices_model voices_model) {

		if (mIsplay) {
			mIsplay = false;
			DynamicUtlis.getInstance().playVoice(mVoicePlay, voices_model,
					mVoiceBtn, TAG);
		} else {
			mIsplay = true;
			DynamicUtlis.getInstance().stopPlayVoice(mVoiceBtn, voices_model,
					mVoicePlay, TAG);
		}
	}

	/**
	 * 将网络语音下载到本地后通过该方法获取下载完成指令 进行下载完成后的播放
	 * 
	 * @param event
	 */
	public void onEventMainThread(sendDownLoadedOrder event) {
		if (!TAG.equals(event.getType())) {
			return;
		}
		if (event.getTag() == GlobalConstant.VOICE_TABLE) {
			if (mVoiceBtn != null && !mVoiceBtn.isClickable()) {
				mVoiceBtn.setFocusable(true);
				mVoiceBtn.setClickable(true);
			}
			if (!"Failure".equals(event.getId())) {
				Voices_model voices_model = DynamicShowDao.getInstance()
						.getSingleItem_voice(DynamicShowDao.COLUMN_NAME_ID,
								new String[] { event.getId() });
				/*
				 * 如果读取语音表中没有只有id和本地路径就应该在此处补全模型插入数据库
				 */
				if (voices_model.getSrc() == null
						&& voices_model.getSc() == null) {
					for (int i = 0; i < mAllDynamics.size(); i++) {
						ArrayList<Voices_model> voices = mAllDynamics.get(i).voices;
						if (voices != null
								&& voices.size() > 0
								&& voices_model.getId().equals(
										voices.get(0).getId())) {
							voices_model.setSc(voices.get(0).getSc());
							voices_model.setFilesize(voices.get(0)
									.getFilesize());
							voices_model.setSrc(voices.get(0).getSrc());
							DynamicShowDao.getInstance().singleItemUpdate(
									GlobalConstant.VOICE_TABLE,
									voices_model.getId(), null, null, null,
									null, voices_model);
							break;
						}
					}
				}
				DynamicUtlis.getInstance().playVoice(mVoicePlay, voices_model,
						mVoiceBtn, TAG);
			} else {
				mIsplay = true;
			}
		} else {
			mIsplay = true;
		}
	}

	@Override
	public void onPause() {
		if (!mIsplay) { // 界面消失的时候判断是否正在播放语音
			DynamicUtlis.getInstance().stopPlayVoice(mVoiceBtn, null,
					mVoicePlay, TAG);
		}
		super.onPause();
	}

	/**
	 * 每个条目中popup弹窗回复信息按钮
	 * 
	 * @param position
	 * @param dt
	 */
	private void popup_button_message(int position, ViewHolder_DT dt) {

		goDetailsActivity(position, true);
	}

	/**
	 * 话题条目中第一个加入按钮
	 */
	private void ht_item_add_btn_1() {
		UIUtils.showToast(mActivity, "我是话题按钮1");
	}

	/**
	 * 话题条目中第二个加入按钮
	 */
	private void ht_item_add_btn_2() {
		UIUtils.showToast(mActivity, "我是话题按钮2");
	}

	/**
	 * 将存储的动态数据，更新临时id
	 */
	public void onEventMainThread(UpdateDynamicLists event) {
		String latestId = event.getLatestId();
		String pastId = event.getPastId();
		Collections.replaceAll(mAllDynamicIds, pastId, latestId);
		for (Dynamic_model model : mAllDynamics) {
			if (pastId.equals(model.id)) {
				model.id = latestId;
				break;
			}
		}
	}

	/**
	 * 每个条目中dialog弹窗删除按钮
	 * 
	 * @param position
	 */
	private void dialog_button_delete(final int position) {
		String id_tag = mAllDynamics.get(position).id;
		if (Pattern.compile("(?i)[a-z]").matcher(id_tag).find()) {
			UIUtils.showToast(UIUtils.getContext(), "正在后台上传中，请稍后操作!");
			return;
		}

		mHttp.sendGet(NetworkConfig.getDeleta_DtUrl(id_tag), BaseBaen.class,
				new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean != null && bean.status != null
								&& "1".equals(bean.status)) {
							String id = mAllDynamics.get(position).id;
							deleteDynamicAccordingAsId(id);
							mAllDynamics.remove(position);
							mAdapter.notifyDataSetChanged();
							UIUtils.showToast(mActivity, "删除成功！");
						} else if (bean != null && bean.msg != null) {
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
						}
					}
				});
	}

	/**
	 * 从本地数据库删除我的动态 根据当前的动态id
	 * 
	 * @param position
	 */
	private void deleteDynamicAccordingAsId(String id) {
		ArrayList<String> lists = new ArrayList<>();
		lists.add(id);
		// 将索引表中的指定的id删除(更新为null)----最新动态列
		DynamicShowDao.getInstance().singleItemDelete_index(
				DynamicShowDao.COLUMN_NAME_NEW_DYNAMIC_ID, lists);
		// 将索引表中的指定的id删除(更新为null)----朋友圈列
		DynamicShowDao.getInstance().singleItemDelete_index(
				DynamicShowDao.COLUMN_NAME_FRIEND_DYNAMIC_ID, lists);
		// 将索引表中的指定的id删除(更新为null)----最热动态列
		DynamicShowDao.getInstance().singleItemDelete_index(
				DynamicShowDao.COLUMN_NAME_HOT_DYNAMIC_ID, lists);
		DynamicShowDao.getInstance().singleItemDelete(
				GlobalConstant.DYNAMIC_TABLE, id);
	}

	/**
	 * 每个条目中popup弹窗转发按钮
	 * 
	 * @param dt
	 * 
	 * @param position4
	 */
	private void popup_button_retrans(final int position, ViewHolder_DT dt) {
		mFl_loading.setVisibility(View.VISIBLE);
		mHttp.sendGet(NetworkConfig.getIsExist(mAllDynamics.get(position).id),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						mFl_loading.setVisibility(View.GONE);
						if (bean == null || bean.status == null
								|| bean.msg == null) {
							UIUtils.showToast(mActivity, "网络异常");
							return;
						}
						switch (bean.status) {
						case "1":
							UIUtils.showToast(mActivity, "该条动态已被删除~");
							EventBus.getDefault().post(
									new RefurbishDTItem(mAllDynamics
											.get(position).id,
											GlobalConstant.DELETE_TAG));
							break;
						case "2":
							Intent intent = new Intent(mActivity,
									Retrans_DT.class);
							intent.putExtra(GlobalConstant.RETRANS_USER_TAG,
									mAllDynamics.get(position));
							mActivity.startActivity(intent);
							break;

						}

					}
				});
	}

	ViewHolder_DT mTempHolder = null;

	/**
	 * 每个条目中popup弹窗点赞按钮 服务端添加字段
	 * 
	 * @param dt
	 */
	private void popup_button_dianZan(final int position, final ViewHolder_DT dt) {
		if (mAllDynamics == null) {
			return;
		}
		/**
		 * 通过判断iszan字段值，1为已经点过该动态，0为未点过该条动态
		 */
		if ("1".equals(mAllDynamics.get(position).iszan)) {
			UIUtils.showToast(mActivity, "已经点过赞了~~");
		} else {
			if (Pattern.compile("(?i)[a-z]")
					.matcher(mAllDynamics.get(position).id).find()) {
				UIUtils.showToast(UIUtils.getContext(), "正在后台上传中，请稍后操作!");
				return;
			}
			mHttp.sendGet(
					NetworkConfig.getDianZanUrl(mAllDynamics.get(position).id),
					BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

						@Override
						public void onSucceed(BaseBaen bean) {
							if (bean == null || bean.status == null
									|| bean.msg == null) {
								UIUtils.showToast(mActivity, "网络异常！");
								return;
							}
							if ("3".equals(bean.status)) {
								UIUtils.showToast(mActivity, "该条动态已被删除~");
								EventBus.getDefault().post(
										new RefurbishDTItem(mAllDynamics
												.get(position).id,
												GlobalConstant.DELETE_TAG));
								return;
							}
							if ("1".equals(bean.status)) {
								UIUtils.showToast(mActivity, "点赞成功！");
								mTempHolder = dt;
								String dz_sum = String.valueOf(Integer
										.parseInt(mAllDynamics.get(position).dz) + 1);
								String id = mAllDynamics.get(position).id;
								EventBus.getDefault().post(
										new UpdateShowMsg(id, null, null,
												dz_sum, null));

							} else {
								UIUtils.showToast(mActivity, bean.msg);
							}
						}
					});
		}

	}

	/**
	 * 点击listview条目监听
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if ((mAllDynamics != null && mAllDynamics.size() == 0)
				|| mAllDynamics == null) {// 防止用户点击头布局时引发的异常
			return;
		}
		// if (mAllDynamics != null && mAllDynamics.size() > 0&& mChildType ==
		// GlobalConstant.TYPE_ZXDT && position >= 1
		// && position < 8) {
		// position -= 1;
		// } else
		if (mAllDynamics != null && mAllDynamics.size() > 0
				&& mChildType == GlobalConstant.TYPE_ZXDT && position >= 8) {
			position -= 1;
			// position -= 2;
		}
		goDetailsActivity(position, false);
	}

	/**
	 * 推荐应用栏目 更多按钮
	 */
	public void app_item_add_btn() {
		startActivity(new Intent(mActivity, TjyyActivity.class));
	}

	/**
	 * 进入动态详情页面,传入该条动态的id值
	 * 
	 * @param position
	 * @param b
	 *            true:说明当前点击的是整个条目 false：说明当前是点击的评论条目按钮，直接进入详情后弹出键盘布局
	 * @param view
	 */
	private void goDetailsActivity(int position, boolean b) {
		if (position == -1) {
			return;
		}
		String id_tag = mAllDynamics.get(position).id;
		if (Pattern.compile("(?i)[a-z]").matcher(id_tag).find()) {
			UIUtils.showToast(UIUtils.getContext(), "正在后台上传中，请稍后操作!");
			if (GlobalConstant.UPLOADTYPE_2
					.equals(mAllDynamics.get(position).update_type)) {
				UploadDynamicData uploadDynamic = new UploadDynamicData();
				String uploadId = uploadDynamic.getUploadId();
				if (StringUtils.isEmpty(uploadId)) {
					uploadDynamic.uploadDataToNet(uploadId);
				}
			}
		} else {

			Intent intent = new Intent(mActivity, Details_DT.class);
			intent.putExtra(GlobalConstant.DT_CURRENT_DATA_NAME,
					mAllDynamics.get(position).id);
			intent.putExtra(GlobalConstant.DT_CURRENT_DATA_UID,
					mAllDynamics.get(position).uid);
			intent.putExtra(GlobalConstant.DT_ISOPEN_COMMENT_LAYOUT, b);
			startActivity(intent);
		}
	}


	private DtBean mbean;

	/**
	 * 请求网络数据 刷新数据
	 * 
	 * @param id
	 *            所查的列数名
	 * 
	 */
	private void initNet_refurbish(final String id) {
		if (mHttp != null) {
			mHttp.stopHttpNET();
		}
		/*
		 * 当有正在上传的网络任务的时候就停止请求网络，防止将上传的id覆盖掉
		 */
		for (int i = 0; mAllDynamicIds != null && i < mAllDynamicIds.size(); i++) {
			if (Pattern.compile("(?i)[a-z]").matcher(mAllDynamicIds.get(i))
					.find()) {
				mLv.onRefreshComplete();
				return;
			}
		}
		String url = getUrl(DYNAMIC_NEW, "0");
		mHttp.sendGet(url, DtBean.class, new MyRequestCallBack<DtBean>() {

			@Override
			public void onSucceed(final DtBean bean) {
				if (bean == null || bean.cont == null
						|| bean.cont.tlist == null) {
					UIUtils.showToast(mActivity, "网络异常！");
					mLv.onRefreshComplete();
					return;
				}
				if (bean.cont.recapp != null) {
					mbean = bean;
				}
				if (bean.cont.tlist.size() <= 0) {
					UIUtils.showToast(mActivity, "没有更新数据了！");
					mLv.onRefreshComplete();
					return;
				}
				mOffSet = 1;
				final ArrayList<Tlist> tlist;
				tlist = bean.cont.tlist;
				Collections.reverse(tlist);
				/**
				 * 开启线程将网络中的数据存入数据库中
				 */
				new Thread(new Runnable() {
					@Override
					public void run() {
						init_db(tlist, id);
					}
				}).start();

			}
		});
	}

	/**
	 * 请求网络操作加载更多
	 * 
	 * @param typePyq
	 * 
	 */
	private void initNet_loadMore(int type) {
		Dynamic_model model = mAllDynamics.get(mAllDynamics.size() - 1);

		String url = getUrl(DYNAMIC_OLD, model.id);
		mHttp.sendGet(url, DtBean.class, new MyRequestCallBack<DtBean>() {

			@Override
			public void onSucceed(final DtBean bean) {
				if (bean == null || bean.cont == null
						|| bean.cont.tlist == null) {
					UIUtils.showToast(mActivity, "网络异常！");
					mLv.onRefreshComplete();
					return;
				}

				if ("1".equals(bean.status)) {
					ArrayList<Tlist> tlists = bean.cont.tlist;
					ArrayList<Dynamic_model> dynamicModels = new ArrayList<>();

					if (tlists.size() <= 0) {
						UIUtils.showToast(mActivity, "没有数据了！");
						mLv.onRefreshComplete();
						return;
					}
					for (int i = 0; i < tlists.size(); i++) {
						Dynamic_model dynamicModel = DynamicUtlis.getInstance()
								.transferFormat(tlists.get(i));
						dynamicModels.add(dynamicModel);
					}
					mAllDynamics.addAll(dynamicModels);
					mAdapter.notifyDataSetChanged();
				} else {
					UIUtils.showToast(mActivity, bean.msg);
				}
				mLv.onRefreshComplete();
			}
		});
	}

	// /**
	// * 进行最新动态，朋友圈查询的时候传入的id参数 查询索引数据库获取当前最新的id值，进行网络请求 没有最新的id的时候就传入为零
	// *
	// * @return
	// */
	// private String getCurrentId() {
	// String id = "0";
	// switch (mChildType) {
	// case GlobalConstant.TYPE_ZXDT:
	// id = DynamicShowDao.getInstance().getRecentIndex(
	// DynamicShowDao.COLUMN_NAME_NEW_DYNAMIC_ID);
	// break;
	// case GlobalConstant.TYPE_PYQ:
	// id = DynamicShowDao.getInstance().getRecentIndex(
	// DynamicShowDao.COLUMN_NAME_FRIEND_DYNAMIC_ID);
	// break;
	// default:
	// id = "0";
	// break;
	// }
	// return id;
	//
	// }

	@Override
	public void onRefresh() {
		mCurrent_status = DOWN_STATUS;

		switch (mChildType) {
		case GlobalConstant.TYPE_ZXDT:
			initNet_refurbish(DynamicShowDao.COLUMN_NAME_NEW_DYNAMIC_ID);

			break;
		case GlobalConstant.TYPE_ZRDT:
			initNet_refurbish(DynamicShowDao.COLUMN_NAME_HOT_DYNAMIC_ID);

			break;
		case GlobalConstant.TYPE_PYQ:
			initNet_refurbish(DynamicShowDao.COLUMN_NAME_FRIEND_DYNAMIC_ID);

			break;
		}
	}

	@Override
	public void onLoadMore() {
		mCurrent_status = UP_STATUS;
		mOffSet++;
		// 根据索引表来获取相应类型的数据
		switch (mChildType) {
		case GlobalConstant.TYPE_ZXDT:
			formDdGetDate_dt_down(DynamicShowDao.COLUMN_NAME_NEW_DYNAMIC_ID);
			break;
		case GlobalConstant.TYPE_ZRDT:
			formDdGetDate_dt_down(DynamicShowDao.COLUMN_NAME_HOT_DYNAMIC_ID);
			break;
		case GlobalConstant.TYPE_PYQ:
			formDdGetDate_dt_down(DynamicShowDao.COLUMN_NAME_FRIEND_DYNAMIC_ID);
			break;
		}

	}

	/**
	 * 数据库的插入
	 * 
	 * 将访问 网络或取得json数据存入数据库（插入前做去重操作） 存入规则： 先判断该条动态中是否含有相册，语音，视频数据，
	 * 若有可将三种数据分别放入相应的相册表，语音表视频表/ 若有三种数据，则记录id,(多条采用id拼串形式)存放到主表的关键字段中（相册
	 * ：imgs，视频:Videos,语音：voices） 关于转发动态：
	 * 在主表中首先根据zfinfox中判断是否含有转发动态，相册视频语音，插入规则相同
	 * 
	 * @param bean
	 * @param endIndex
	 *            开始索引
	 * @param startindex
	 *            结束索引
	 * @param id
	 *            所查的列名
	 */
	protected void init_db(ArrayList<Tlist> tlist, String id) {
		HomeTable_model mode = null; // 动态数据模型
		RelayDynamicTable_model rd_model = null; // 转发动态数据模型
		ArrayList<HomeTable_model> homeModels = new ArrayList<>(); // 动态模型集合
		ArrayList<Imgs_model> imgsModels = new ArrayList<>(); // 相册模型集合
		ArrayList<String> imgIds = null; // 相册索引值集合
		ArrayList<Voices_model> voicesModels = new ArrayList<>(); // 语音模型集合
		ArrayList<String> voicesIds = null; // 语音索引值集合
		ArrayList<Videos_model> videoModels = new ArrayList<>();// 视频模型集合
		ArrayList<String> videoIds = null; // 视频索引值集合
		ArrayList<RelayDynamicTable_model> rd_models = null; // 转发动态的数据模型集合
		ArrayList<String> ids = new ArrayList<>(); // 网络请求的数据id集合

		for (int i = 0; i < tlist.size(); i++) {
			mode = new HomeTable_model();
			/**
			 * 判断当前动态中是否含有相册数据
			 */

			if (tlist.get(i).imgs != null && tlist.get(i).imgs.size() > 0) {
				Imgs_model imgModel = null;
				imgIds = new ArrayList<>();
				imgIds.clear();
				// 读取数据进行相册表插入
				for (int j = 0; j < tlist.get(i).imgs.size(); j++) {
					imgModel = new Imgs_model();
					imgModel.setId(tlist.get(i).imgs.get(j).id);
					imgModel.setImg(tlist.get(i).imgs.get(j).img);
					imgModel.setImgori(tlist.get(i).imgs.get(j).imgori);
					imgModel.setInfo(tlist.get(i).imgs.get(j).info);
					imgIds.add(tlist.get(i).imgs.get(j).id);
					imgsModels.add(imgModel);
				}

			}

			/**
			 * 判断当前动态中是否含有语音数据
			 */
			if (tlist.get(i).voices != null && tlist.get(i).voices.size() > 0) {
				Voices_model voiceModel = null;
				voicesIds = new ArrayList<>();
				voicesIds.clear();
				// 读取数据进行语音表插入
				for (int z = 0; z < tlist.get(i).voices.size(); z++) {
					voiceModel = new Voices_model();
					voiceModel.setId(tlist.get(i).voices.get(z).id);
					voiceModel.setSc(tlist.get(i).voices.get(z).sc);
					voiceModel.setSrc(tlist.get(i).voices.get(z).src);
					voiceModel.setSrcbg(tlist.get(i).voices.get(z).srcbg);
					voiceModel.setFilesize(tlist.get(i).voices.get(z).filesize);
					voicesIds.add(tlist.get(i).voices.get(z).id);
					voicesModels.add(voiceModel);
				}

			}

			/**
			 * 判断当前动态中是否含有视频数据
			 */
			if (tlist.get(i).videos != null && tlist.get(i).videos.size() > 0) {

				Videos_model videoModel = null;
				videoIds = new ArrayList<>();
				videoIds.clear();
				// 读取数据进行视频表插入
				for (int n = 0; n < tlist.get(i).videos.size(); n++) {
					videoModel = new Videos_model();
					videoModel.setId(tlist.get(i).videos.get(n).id);
					videoModel.setSc(tlist.get(i).videos.get(n).sc);
					videoModel.setSrc(tlist.get(i).videos.get(n).src);
					videoModel.setSrcbg(tlist.get(i).videos.get(n).srcbg);
					videoModel.setFilesize(tlist.get(i).videos.get(n).filesize);
					videoIds.add(tlist.get(i).videos.get(n).id);
					videoModels.add(videoModel);
				}

			}
			/**
			 * 判断当前动态中是否含有转发数据
			 */
			if (tlist.get(i).zfinfox != null) {
				ArrayList<String> imgIds_rd = null; // 转发相册索引值集合
				ArrayList<String> voicesIds_rd = null; // 转发语音索引值集合
				ArrayList<String> videoIds_rd = null; // 转发视频索引值集合
				if (rd_models == null) {
					rd_models = new ArrayList<>();
				}
				/**
				 * 判断当前转发动态中是否含有相册数据
				 */
				if (tlist.get(i).zfinfox.imgs != null
						&& tlist.get(i).zfinfox.imgs.size() > 0) {
					Imgs_model imgModel = null;
					imgIds_rd = new ArrayList<>();
					imgIds_rd.clear();
					// 读取数据进行相册表插入
					for (int j = 0; j < tlist.get(i).zfinfox.imgs.size(); j++) {
						imgModel = new Imgs_model();
						imgModel.setId(tlist.get(i).zfinfox.imgs.get(j).id);
						imgModel.setImg(tlist.get(i).zfinfox.imgs.get(j).img);
						imgModel.setImgori(tlist.get(i).zfinfox.imgs.get(j).imgori);
						imgModel.setInfo(tlist.get(i).zfinfox.imgs.get(j).info);
						imgIds_rd.add(tlist.get(i).zfinfox.imgs.get(j).id);
						imgsModels.add(imgModel);
					}

				}
				/**
				 * 判断当前转发动态中是否含有语音数据
				 */
				if (tlist.get(i).zfinfox.voices != null
						&& tlist.get(i).zfinfox.voices.size() > 0) {

					Voices_model voiceModel = null;
					voicesIds_rd = new ArrayList<>();
					voicesIds_rd.clear();
					// 读取数据进行语音表插入
					for (int z = 0; z < tlist.get(i).zfinfox.voices.size(); z++) {
						voiceModel = new Voices_model();
						voiceModel.setId(tlist.get(i).zfinfox.voices.get(z).id);
						voiceModel.setSc(tlist.get(i).zfinfox.voices.get(z).sc);
						voiceModel
								.setSrc(tlist.get(i).zfinfox.voices.get(z).src);
						voiceModel
								.setSrcbg(tlist.get(i).zfinfox.voices.get(z).srcbg);
						voiceModel.setFilesize(tlist.get(i).zfinfox.voices
								.get(z).filesize);
						voicesIds_rd.add(tlist.get(i).zfinfox.voices.get(z).id);
						voicesModels.add(voiceModel);
					}

				}
				/**
				 * 判断当前转发动态中是否含有视频数据
				 */
				if (tlist.get(i).zfinfox.videos != null
						&& tlist.get(i).zfinfox.videos.size() > 0) {

					Videos_model videoModel = null;
					videoIds_rd = new ArrayList<>();
					videoIds_rd.clear();
					// 读取数据进行视频表插入
					for (int n = 0; n < tlist.get(i).zfinfox.videos.size(); n++) {
						videoModel = new Videos_model();
						videoModel.setId(tlist.get(i).zfinfox.videos.get(n).id);
						videoModel.setSc(tlist.get(i).zfinfox.videos.get(n).sc);
						videoModel
								.setSrc(tlist.get(i).zfinfox.videos.get(n).src);
						videoModel
								.setSrcbg(tlist.get(i).zfinfox.videos.get(n).srcbg);
						videoModel.setFilesize(tlist.get(i).zfinfox.videos
								.get(n).filesize);
						videoIds_rd.add(tlist.get(i).zfinfox.videos.get(n).id);
						videoModels.add(videoModel);
					}

				}

				rd_model = new RelayDynamicTable_model();
				rd_model.setId(tlist.get(i).zfinfox.id);
				if (!StringUtils
						.isEmpty(switchCreateTime(tlist.get(i).zfinfox.createtime))) {
					rd_model.setCreatetime(switchCreateTime(tlist.get(i).zfinfox.createtime));
				}
				rd_model.setFlid(tlist.get(i).zfinfox.flid);
				rd_model.setFnickname(tlist.get(i).zfinfox.fnickname);
				rd_model.setFuid(tlist.get(i).zfinfox.fuid);
				rd_model.setOlid(tlist.get(i).zfinfox.olid);
				rd_model.setOnickname(tlist.get(i).zfinfox.onickname);
				rd_model.setOuid(tlist.get(i).zfinfox.ouid);
				rd_model.setDz(tlist.get(i).zfinfox.dz);
				rd_model.setInfos(tlist.get(i).zfinfox.infos);
				rd_model.setIsnm(tlist.get(i).zfinfox.isnm);
				rd_model.setIsopen(tlist.get(i).zfinfox.isopen);
				rd_model.setJd(tlist.get(i).zfinfox.jd);
				rd_model.setPj(tlist.get(i).zfinfox.pj);
				rd_model.setRelinfo(tlist.get(i).zfinfox.relinfo);
				rd_model.setTags(tlist.get(i).zfinfox.tags);
				rd_model.setType(tlist.get(i).zfinfox.type);
				rd_model.setUid(tlist.get(i).zfinfox.uid);
				rd_model.setWd(tlist.get(i).zfinfox.wd);
				rd_model.setYd(tlist.get(i).zfinfox.yd);
				rd_model.setZf(tlist.get(i).zfinfox.zf);
				rd_model.setZfinfo(tlist.get(i).zfinfox.zfinfo);
				rd_model.setImgs(DbManager.getInstance().jionFileName_p(
						imgIds_rd));
				rd_model.setVideos(DbManager.getInstance().jionFileName_p(
						videoIds_rd));
				rd_model.setVoices(DbManager.getInstance().jionFileName_p(
						voicesIds_rd));
				rd_models.add(rd_model);

				if (imgIds_rd != null) {
					imgIds_rd.clear();
					imgIds_rd = null;
				}
				if (voicesIds_rd != null) {
					voicesIds_rd.clear();
					voicesIds_rd = null;
				}
				if (videoIds_rd != null) {
					videoIds_rd.clear();
					videoIds_rd = null;
				}

			}

			mode.setId(tlist.get(i).id);
			if (!StringUtils.isEmpty(switchCreateTime(tlist.get(i).createtime))) {
				mode.setCreatetime(switchCreateTime(tlist.get(i).createtime));
			}
			mode.setDistance(tlist.get(i).distance);
			mode.setDz(tlist.get(i).dz);
			if (!StringUtils.isEmpty(mUser_Id)) {
				if (mUser_Id.equals(tlist.get(i).uid)) {
					mode.setFace(PrefUtils.getString(GlobalConstant.USER_FACE, null));
				} else {
					mode.setFace(tlist.get(i).face);
				}
			}

			mode.setIszan(tlist.get(i).iszan);
			mode.setInfos(tlist.get(i).infos);
			mode.setIsnm(tlist.get(i).isnm);
			mode.setIsopen(tlist.get(i).isopen);
			mode.setJd(tlist.get(i).jd);
			mode.setNickname(tlist.get(i).nickname);
			mode.setPj(tlist.get(i).pj);
			mode.setRelinfo(tlist.get(i).relinfo);
			mode.setTags(tlist.get(i).tags);
			mode.setMmtype(tlist.get(i).mmtype);
			mode.setType(tlist.get(i).type);
			mode.setUid(tlist.get(i).uid);
			mode.setWd(tlist.get(i).wd);
			mode.setYd(tlist.get(i).yd);
			mode.setZf(tlist.get(i).zf);
			mode.setZfinfo(tlist.get(i).zfinfo);
			mode.setImgs(DbManager.getInstance().jionFileName_p(imgIds));
			mode.setVoices(DbManager.getInstance().jionFileName_p(voicesIds));
			mode.setVideos(DbManager.getInstance().jionFileName_p(videoIds));
			if (tlist.get(i).zfinfox != null) {
				mode.setZfinfox(tlist.get(i).zfinfox.id);
			}
			if (imgIds != null) {
				imgIds.clear();
				imgIds = null;
			}
			if (voicesIds != null) {
				voicesIds.clear();
				voicesIds = null;
			}
			if (videoIds != null) {
				videoIds.clear();
				videoIds = null;
			}
			homeModels.add(mode);
			ids.add(tlist.get(i).id);
		}

		switch (mChildType) {
		case GlobalConstant.TYPE_ZXDT: // 最新动态页面
			DynamicShowDao.getInstance().listItemInsert_index(
					DynamicShowDao.COLUMN_NAME_NEW_DYNAMIC_ID, ids);
			break;
		case GlobalConstant.TYPE_ZRDT: // 最热动态页面
			DynamicShowDao.getInstance().listItemInsert_index(
					DynamicShowDao.COLUMN_NAME_HOT_DYNAMIC_ID, ids);
			break;
		case GlobalConstant.TYPE_PYQ: // 朋友圈页面
			DynamicShowDao.getInstance().listItemInsert_index(
					DynamicShowDao.COLUMN_NAME_FRIEND_DYNAMIC_ID, ids);
			break;
		default:
			break;
		}

		// 将请求的网络数据存入数据库中
		DynamicShowDao.getInstance().ItemListInsert(homeModels, imgsModels,
				rd_models, videoModels, voicesModels);

		formDdGetDate_dt_down(id);
	}

	/**
	 * 将时间转换为时间戳
	 * 
	 * @param time
	 * @return
	 */
	private String switchCreateTime(String time) {
		String valueOf = null;
		try {
			valueOf = String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm")
					.parse(time).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return valueOf;
	}

	class MyBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			int count = 0;
			switch (mChildType) {

			case GlobalConstant.TYPE_ZXDT: // 返回最新动态数据
				if (mAllDynamics != null && mAllDynamics.size() > 0) {
					count = mAllDynamics.size() + 1;
				} else {
					count = mAllDynamics.size();
				}
				break;
			case GlobalConstant.TYPE_ZRDT: // 返回最热动态数据
			case GlobalConstant.TYPE_PYQ: // 返回朋友圈数据
				count = mAllDynamics.size();
				break;
			}
			return count;

			// return mAllDynamics.size();
		}

		@Override
		public int getItemViewType(int position) {
			// if (mChildType == GlobalConstant.TYPE_ZXDT&& (position ==
			// getPosition_ht())) {
			// return TYPE_HT;
			// } else

			if (mChildType == GlobalConstant.TYPE_ZXDT
					&& position == getPosition_yy(7)) {
				return TYPE_YY;
			} else {
				return TYPE_DT;
			}
			// return TYPE_DT;
		}

		/**
		 * 根据当前集合的变化来确定推荐应用的位置
		 * 
		 * @param i
		 * @return
		 */
		private int getPosition_yy(int i) {
			if (mAllDynamics.size() > i) {
				return i;
			} else {
				return mAllDynamics.size();
			}
		}

		/**
		 * 根据当前集合的变化来确定推荐话题的位置
		 * 
		 * @param i
		 * @return
		 */
		private int getPosition_ht() {
			if (mAllDynamics.size() == 0) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getViewTypeCount() {
			return VIEW_TYPE_COUNT;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder_DT holder_dt = null;// 动态类型
			ViewHolder_HT holder_ht = null;
			ViewHolder_YY holder_yy = null;
			int type = getItemViewType(position);

			if (convertView == null) {
				switch (type) {
				case TYPE_DT:
					holder_dt = new ViewHolder_DT();
					convertView = View.inflate(mActivity,
							R.layout.my_dynamic_item, null);
					holder_dt.im_mydynamic_show_more = $(convertView,
							R.id.im_mydynamic_show_more);
					holder_dt.ci_dynamicitem_headicon = $(convertView,
							R.id.ci_dynamicitem_headicon);
					holder_dt.tv_mydynamic_username = $(convertView,
							R.id.tv_mydynamic_username);
					holder_dt.tv_dynamic_item_introduce = $(convertView,
							R.id.tv_dynamic_item_introduce);
					holder_dt.tv_dynamic_current_time = $(convertView,
							R.id.tv_dynamic_current_time);
					holder_dt.iv_dynamic_video_icon = $(convertView,
							R.id.iv_dynamic_video_icon);
					holder_dt.iv_dynamic_video_play_icon = $(convertView,
							R.id.iv_dynamic_video_play_icon);
					holder_dt.tv_dynamic_distance = $(convertView,
							R.id.tv_dynamic_distance);
					holder_dt.tv_dynamic_retransmission = $(convertView,
							R.id.tv_dynamic_retransmission);
					holder_dt.tv_dynamic_appraise = $(convertView,
							R.id.tv_dynamic_appraise);
					holder_dt.tv_dynamic_praise = $(convertView,
							R.id.tv_dynamic_praise);
					holder_dt.tv_dynamic_read = $(convertView,
							R.id.tv_dynamic_read);
					holder_dt.fl_content_layout = $(convertView,
							R.id.fl_content_layout);
					holder_dt.tv_dynamic_item_voices = $(convertView,
							R.id.tv_dynamic_item_video);
					holder_dt.gv_photo_album = $(convertView,
							R.id.gv_photo_album);
					holder_dt.rl_item_video = $(convertView, R.id.rl_item_video);
					holder_dt.ll_transmit_layout = $(convertView,
							R.id.ll_transmit_layout);
					holder_dt.tv_transmit_item_introduce = $(convertView,
							R.id.tv_transmit_item_introduce);
					holder_dt.tv_transmit_item_voices = $(convertView,
							R.id.tv_transmit_item_video);
					holder_dt.ll_transmit_title_layout = $(convertView,
							R.id.ll_transmit_title_layout);
					holder_dt.tv_transmit_name = $(convertView,
							R.id.tv_transmit_name);
					convertView.setTag(holder_dt);
					break;
				case TYPE_HT:
					holder_ht = new ViewHolder_HT();
					convertView = View.inflate(mActivity,
							R.layout.my_topic_item, null);
					holder_ht.mTv_more_go_ht_page = $(convertView,
							R.id.tv_more_go_ht_page);
					holder_ht.mBtn_add_group_1 = $(convertView,
							R.id.btn_add_group_1);
					holder_ht.mBtn_add_group_2 = $(convertView,
							R.id.btn_add_group_2);

					holder_ht.civ_symposium_icon_1 = $(convertView,
							R.id.civ_symposium_icon_1);
					holder_ht.tv_ht_title_1 = $(convertView, R.id.tv_ht_title_1);
					holder_ht.tv_ht_introduce_1 = $(convertView,
							R.id.tv_ht_introduce_1);
					holder_ht.civ_symposium_icon_2 = $(convertView,
							R.id.civ_symposium_icon_2);
					holder_ht.tv_ht_title_2 = $(convertView, R.id.tv_ht_title_2);
					holder_ht.tv_ht_introduce_2 = $(convertView,
							R.id.tv_ht_introduce_2);

					convertView.setTag(holder_ht);
					break;
				case TYPE_YY:
					holder_yy = new ViewHolder_YY();
					convertView = View.inflate(mActivity, R.layout.my_app_item,
							null);
					holder_yy.btn_add = $(convertView, R.id.tv_more_go_yy_page);
					holder_yy.gv_app_show = $(convertView, R.id.gv_yy_page_app);

					convertView.setTag(holder_yy);
					break;
				}
			} else {
				switch (type) {
				case TYPE_DT:
					holder_dt = (ViewHolder_DT) convertView.getTag();
					break;
				case TYPE_HT:
					holder_ht = (ViewHolder_HT) convertView.getTag();
					break;
				case TYPE_YY:
					holder_yy = (ViewHolder_YY) convertView.getTag();
					break;
				}
			}

			switch (type) {
			case TYPE_DT:
				setItem_dt(position, holder_dt, mAllDynamics);
				break;
			case TYPE_HT:
				// if (mbean != null && mbean.cont != null
				// && mbean.cont.recgroup != null) {
				// setItem_ht(position, holder_ht, mbean.cont.recgroup);
				// }
				break;
			case TYPE_YY:
				if (mbean != null && mbean.cont != null
						&& mbean.cont.recapp != null) {
					setItem_yy(position, holder_yy, mbean.cont.recapp);
				}
				break;

			}

			return convertView;
		}
	}

	/**
	 * viewHolder_dt -----动态
	 * 
	 * @author limaokeji-windosc
	 * 
	 */

	public class ViewHolder_DT {
		public TextView tv_transmit_name; // 转发状态转发人的名字
		public LinearLayout ll_transmit_layout; // 转发状态背景布局
		public TextView tv_transmit_item_voices; // 转发状态------ 语音
		public TextView tv_transmit_item_introduce; // 转发状态----- 文字
		public LinearLayout ll_transmit_title_layout; // 转发状态的时候我的语音或者文字布局
		public RelativeLayout rl_item_video; // 视频的布局
		public TextView tv_dynamic_read; // 阅读
		public TextView tv_dynamic_praise; // 点赞
		public TextView tv_dynamic_appraise; // 评价
		public TextView tv_dynamic_retransmission; // 转发
		public TextView tv_dynamic_distance; // 距离s
		public ImageView iv_dynamic_video_icon; // 视频预览图
		public ImageView iv_dynamic_video_play_icon; // 视频播放按钮
		public TextView tv_dynamic_current_time; // 当前时间
		public TextView tv_dynamic_item_introduce; // 该条动态的文字状态
		public TextView tv_mydynamic_username; // 用户名
		public CircleImageView ci_dynamicitem_headicon;// 用户名头像
		public ImageView im_mydynamic_show_more; // 按钮----更多操作
		public GridView gv_photo_album;// 展示照片
		public FrameLayout fl_content_layout; // 用户上传视频布局
		public TextView tv_dynamic_item_voices; // 用户发送的语音时常

	}

	/**
	 * viewHolder_ht -----话题
	 * 
	 * @author limaokeji-windosc
	 * 
	 */

	class ViewHolder_HT {
		public TextView tv_ht_introduce_2;
		public TextView tv_ht_title_2;
		public CircleImageView civ_symposium_icon_2;
		public TextView tv_ht_introduce_1;
		public TextView tv_ht_title_1;
		public CircleImageView civ_symposium_icon_1;
		public TextView mTv_more_go_ht_page; // 按钮----更多（进入话题页面）
		public Button mBtn_add_group_1; // 按钮----更多（进入话题页面）
		public Button mBtn_add_group_2; // 按钮----更多（进入话题页面）
	}

	/**
	 * viewHolder_yy -----推荐应用
	 * 
	 * @author limaokeji-windosc
	 * 
	 */

	class ViewHolder_YY {
		public MyGridView gv_app_show;// 展示推荐的应用
		public TextView btn_add;// 按钮----更多（进入话题页面）

	}

	/**
	 * 设置动态每个条目的点击事件
	 * 
	 * @param position
	 * @param tlist
	 * @param cont
	 * @param mHolder_dt
	 * 
	 */
	public void setItem_dt(int position, final ViewHolder_DT dt,
			List<Dynamic_model> t_list) {
		if ((mAllDynamics != null && mAllDynamics.size() == 0)
				|| mAllDynamics == null) {
			return;
		}

		if (mAllDynamics != null && mAllDynamics.size() > 0
				&& mChildType == GlobalConstant.TYPE_ZXDT) {
			// if (position > 1 && position < 8) {
			// position = position - 1;
			// } else
			if (position > 7) {
				position -= 1;
			}
		}

		Dynamic_model tlist = t_list.get(position);
		dt.im_mydynamic_show_more.setOnClickListener(new MyItemClickListener(
				position, dt));
		
		dt.ci_dynamicitem_headicon.setOnClickListener(new MyItemClickListener(
				position, dt));

		dt.tv_dynamic_read.setText(tlist.yd
				+ getTitleName(R.string.text_dynamic_read));
		dt.tv_dynamic_retransmission.setText(tlist.zf
				+ getTitleName(R.string.text_dynamic_retransmission));
		dt.tv_dynamic_appraise.setText(tlist.pj
				+ getTitleName(R.string.text_dynamic_appraise));
		dt.tv_dynamic_praise.setText(tlist.dz
				+ getTitleName(R.string.text_dynamic_praise));
		if (mUser_Id.equals(tlist.uid) && !StringUtils.isEmpty(mUser_nickname)) {
			if ("1".equals(tlist.isnm)) {
				dt.tv_mydynamic_username.setText("匿名");
			} else {
				dt.tv_mydynamic_username.setText(mUser_nickname);
			}
		} else {
			if ("1".equals(tlist.isnm)) {
				dt.tv_mydynamic_username.setText("匿名");
			} else {
				dt.tv_mydynamic_username.setText(tlist.nickname);
			}

		}
		dt.tv_dynamic_current_time.setText(tlist.createtime);
		if (tlist.face != null) {
			mHttp.showImage(dt.ci_dynamicitem_headicon, tlist.face);
		} else {

			dt.ci_dynamicitem_headicon
					.setImageResource(R.drawable.head_default_icon);

		}
		dt.tv_dynamic_distance.setText(new MyBDmapUtlis().getCurrentDistance(
						new LatLng(Double.valueOf(tlist.wd), Double.valueOf(tlist.jd)),
						new LatLng(Double.valueOf(mWd), Double.valueOf(mJd))));
		switch (tlist.type) {
		case USER_DYNAMIC:
			setUser_dynamic(position, dt, tlist);
			break;
		case USER_TRANSMIT:
			setuser_transmit(position, dt, tlist);
			break;
		case USER_VIDEO:
			setUser_dynamic(position, dt, tlist);
			break;
		case USER_RECORD:
			/* 用户观看记录 ，预留 */
			break;

		default:
			dt.ll_transmit_title_layout.setVisibility(View.GONE);
			dt.tv_dynamic_item_introduce.setVisibility(View.GONE);
			dt.tv_dynamic_item_voices.setVisibility(View.GONE);
			dt.rl_item_video.setVisibility(View.GONE);
			dt.gv_photo_album.setVisibility(View.GONE);
			break;
		}

	}

	/**
	 * 设置用户转发动态的样式
	 * 
	 * @param position
	 * @param dt
	 * @param dynamic
	 */
	private void setuser_transmit(int position, ViewHolder_DT dt,
			Dynamic_model dynamic) {
		if (dynamic.zfinfox == null) {
			return;
		}
		dt.ll_transmit_title_layout.setVisibility(View.VISIBLE);
		showTextOrVoice(position, dt.tv_transmit_item_introduce,
				dt.tv_transmit_item_voices, dt, dynamic.voices, dynamic.infos);
		dt.ll_transmit_layout.setBackgroundColor(mActivity.getResources()
				.getColor(R.color.bg_greywhite));
		dt.ll_transmit_layout.setPadding(width * 20 / 720, height * 10 / 1280,
				width * 20 / 720, height * 20 / 1280);
		dt.tv_transmit_name.setVisibility(View.VISIBLE);

		dt.tv_transmit_name.setText(Html
				.fromHtml("转发: <font color=\"#f95c71\">" + dynamic.zf_fnickname
						+ "</font> 的动态"));

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		if (dynamic.zf_voices != null && dynamic.zf_voices.size() > 0) {
			lp.setMargins(0, 0, 0, height * 10 / 1280);
			dt.tv_transmit_name.setLayoutParams(lp);
		} else {
			lp.setMargins(0, 0, 0, height * 1 / 1280);
			dt.tv_transmit_name.setLayoutParams(lp);
		}

		showTextOrVoice(position, dt.tv_dynamic_item_introduce,
				dt.tv_dynamic_item_voices, dt, dynamic.zf_voices,
				dynamic.zf_infos);
		showPhotoOrVideo(position, dt, dynamic.zf_videos, dynamic.zf_imgs);
	}

	/**
	 * 设置用户的动态样式
	 * 
	 * @param position
	 * @param dt
	 * @param tlist
	 */
	private void setUser_dynamic(final int position, final ViewHolder_DT dt,
			Dynamic_model tlist) {
		dt.ll_transmit_title_layout.setVisibility(View.GONE);
		showTextOrVoice(position, dt.tv_dynamic_item_introduce,
				dt.tv_dynamic_item_voices, dt, tlist.voices, tlist.infos);
		showPhotoOrVideo(position, dt, tlist.videos, tlist.imgs);
		dt.tv_transmit_name.setVisibility(View.GONE);
		dt.ll_transmit_layout.setBackgroundColor(mActivity.getResources()
				.getColor(R.color.bg_white));
		dt.ll_transmit_layout.setPadding(0, 0, 0, 0);
	}

	/**
	 * 根据当前情况控制显示 图片或者视频动态 若图片和视频动态同时存在，显示视频状态为主，在动态详情页在全部展示出来
	 * 
	 * @param position
	 * @param dt
	 * @param imgs
	 * @param videos
	 * @param tlist
	 */
	private void showPhotoOrVideo(int position, ViewHolder_DT dt,
			ArrayList<Videos_model> videos, ArrayList<Imgs_model> imgs) {
		
		if (videos != null && videos.size() > 0) { // 情况1：图片和视频动态同时存在显示视频动态，或者只有视频的情况下,显示视频数据
			dt.rl_item_video.setVisibility(View.VISIBLE);
			dt.gv_photo_album.setVisibility(View.GONE);
			mHttp.showImage(dt.iv_dynamic_video_icon, GetVideoBg(videos.get(0)));
		} else if (imgs != null && imgs.size() > 0) { // 情况2：没有视频只有图片的情况下
														// ，显示图片数据
			dt.rl_item_video.setVisibility(View.GONE);
			dt.gv_photo_album.setVisibility(View.VISIBLE);
			dt.gv_photo_album.setClickable(false);
			dt.gv_photo_album.setPressed(false);
			dt.gv_photo_album.setEnabled(false);
			dt.gv_photo_album.setAdapter(new MyImgsAdapter(imgs));
		} else { // 情况3：视频和图片都没有的情况下， 都不显示
			dt.rl_item_video.setVisibility(View.GONE);
			dt.gv_photo_album.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取视频背景的图片
	 * 
	 * @param videos_model
	 */
	private String GetVideoBg(Videos_model model) {
		if (model == null) {
			return null;
		}
		if (!TextUtils.isEmpty(model.getLocalSrcbg())
				&& new File(model.getLocalSrcbg()).exists()) {
			return model.getLocalSrcbg();
		}
		return model.getSrcbg();
	}

	/**
	 * 设置相册中的图片展示
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyImgsAdapter extends BaseAdapter {

		private ArrayList<Imgs_model> imgs;

		public MyImgsAdapter(ArrayList<Imgs_model> imgs) {
			this.imgs = imgs;
		}

		@Override
		public int getCount() {
			if (imgs.size() >= 3) {
				return 3;
			} else {
				return imgs.size();
			}

		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = View.inflate(mActivity, R.layout.item_imgs_gv, null);
			ImageView iv = $(view, R.id.iv_dt_img);
			mHttp.showImage(iv, GetSmallImg(imgs.get(position)));
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ArrayList<String> imgoris = new ArrayList<>();
					imgoris.clear();
					for (int i = 0; i < imgs.size(); i++) {
						String img = GetBigImg(imgs.get(i));
						if (img != null) {
							imgoris.add(img);
						}
					}
					if (imgoris != null && imgoris.size() > 0) {
						Intent intent = new Intent(mActivity,
								ShowDetailsImages.class);
						intent.putStringArrayListExtra(
								GlobalConstant.SHOW_DETAILS_IMAGES, imgoris);
						intent.putExtra(GlobalConstant.SHOW_IMAGE_POSITION,
								position);
						startActivity(intent);
					}
				}
			});
			return view;
		}

	}

	/**
	 * 获取发布的小图片
	 * 
	 * @param model
	 * @param videos_model
	 */
	private String GetSmallImg(Imgs_model model) {
		if (!StringUtils.isEmpty(model.getLocalImg())
				&& new File(model.getLocalImg()).exists()) {
			return model.getLocalImg();
		}
		return model.getImg();
	}

	/**
	 * 获取发布的大图片图片
	 * 
	 * @param model
	 * @param videos_model
	 */
	private String GetBigImg(Imgs_model model) {
		if (!StringUtils.isEmpty(model.getLocalImgOri())
				&& new File(model.getLocalImgOri()).exists()) {
			return model.getLocalImgOri();
		}
		return model.getImgori();
	}

	/**
	 * 根据当前情况控制显示 文字或者语音动态 若语音和文字动态同时存在，显示语音状态为主，在动态详情页在全部展示出来
	 * 
	 * @param position
	 * @param introduce
	 * @param voice
	 * @param dt
	 * @param voices
	 * @param infos
	 */

	private void showTextOrVoice(final int position, TextView introduce,
			TextView voice, ViewHolder_DT dt, ArrayList<Voices_model> voices,
			String infos) {

		if (voices != null && voices.size() > 0) { // 情况1：语音和文字动态同时存在显示语音动态，或者只有语音的情况下
													// 显示语音数据
			introduce.setVisibility(View.GONE);
			voice.setVisibility(View.VISIBLE);
			voice.setText(StringUtils.secToTime((int) Float.parseFloat(voices
					.get(0).getSc())));
			// 设置音频条的长度
			UIUtils.setVideoShape(voice, voices.get(0).getSc(), mActivity);
			voice.setOnClickListener(new MyItemClickListener(position, dt));

		} else if (!StringUtils.isEmpty(infos)) { // 情况2：没有语音只有文字的情况下显示文字数据
			introduce.setVisibility(View.VISIBLE);
			voice.setVisibility(View.GONE);
			introduce.setText(infos);
		} else { // 情况3：语音和文字都没有的情况下， 都不显示
			introduce.setVisibility(View.GONE);
			voice.setVisibility(View.GONE);
		}
	}

	/**
	 * 根据id获取相应的系统资源
	 * 
	 * @param id
	 * @return
	 */
	private String getTitleName(int id) {
		return mActivity.getResources().getString(id);
	}

	/**
	 * 设置推荐应用的点击事件
	 * 
	 * @param position
	 * @param yy
	 * @param recapp
	 */
	public void setItem_yy(int position, ViewHolder_YY yy,
			ArrayList<Recapp> recapp) {
		yy.btn_add.setOnClickListener(new MyItemClickListener(position, null));
		yy.gv_app_show.setAdapter(new MyAppAdapter(recapp));
	}

	/**
	 * 设置话题每个条目的点击事件
	 * 
	 * @param position
	 * @param recgroup
	 * @param mHolder_dt
	 */
	public void setItem_ht(int position, ViewHolder_HT ht,
			ArrayList<Recgroup> recgroup) {

		// mHttp.showImage(ht.ci_dynamicitem_headicon, recgroup.get(position).);
		mHttp.showImage(ht.civ_symposium_icon_1, recgroup.get(0).pic + "##");
		mHttp.showImage(ht.civ_symposium_icon_2, recgroup.get(1).pic + "##");

		ht.tv_ht_title_1.setText(recgroup.get(0).name);
		ht.tv_ht_title_2.setText(recgroup.get(1).name);

		ht.tv_ht_introduce_1.setText(recgroup.get(0).desc);
		ht.tv_ht_introduce_2.setText(recgroup.get(1).desc);

		// 更多按钮
		ht.mTv_more_go_ht_page.setOnClickListener(new MyItemClickListener(
				position, null));
		// 群组1 加入
		ht.mBtn_add_group_1
				.setOnClickListener(new MyItemClickListener(0, null));
		// 群组2 加入
		ht.mBtn_add_group_2
				.setOnClickListener(new MyItemClickListener(1, null));
	}

	/**
	 * 条目中按钮点击监听实现类
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyItemClickListener implements OnClickListener {

		private int position; // 当前的位置
		private ViewHolder_DT dt; // 当前动态条目的Viewholder

		public MyItemClickListener(int position, ViewHolder_DT dt) {
			this.position = position;
			this.dt = dt;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.im_mydynamic_show_more: // 动态条目的popup弹窗按钮
				initPopupWindow(dt, position);
				break;
			case R.id.tv_more_go_ht_page: // 话题条目的更多按钮
				gotoHuatiPage();
				break;
		
			case R.id.btn_add_group_1: // 话题条目的加入按钮1
				ht_item_add_btn_1();
				break;
			case R.id.btn_add_group_2: // 话题条目的加入按钮2
				ht_item_add_btn_2();
				break;
			case R.id.tv_more_go_yy_page: // 进入更多的推荐应用
				app_item_add_btn();
				break;
			case R.id.tv_dynamic_item_video: // 点击播放语音功能
				switch (mAllDynamics.get(position).type) {
				case USER_DYNAMIC:
					mVoiceBtn = dt.tv_dynamic_item_voices;
					playVideo(mAllDynamics.get(position).voices.get(0));
					break;
				case USER_TRANSMIT:
					if (mAllDynamics.get(position).voices != null
							&& mAllDynamics.get(position).voices.size() > 0) {
						stopRelativeVoice(
								mAllDynamics.get(position).voices.get(0),
								dt.tv_dynamic_item_voices);
					}
					mVoiceBtn = dt.tv_dynamic_item_voices;
					playVideo(mAllDynamics.get(position).zf_voices.get(0));
					break;
				}
				break;
			case R.id.tv_transmit_item_video: // 点击播放语音功能,转发状态下
				if (USER_TRANSMIT.equals(mAllDynamics.get(position).type)) {
					if (mAllDynamics.get(position).zf_voices != null
							&& mAllDynamics.get(position).zf_voices.size() > 0) {
						stopRelativeVoice(
								mAllDynamics.get(position).zf_voices.get(0),
								dt.tv_transmit_item_voices);
					}
					mVoiceBtn = dt.tv_transmit_item_voices;
					playVideo(mAllDynamics.get(position).voices.get(0));
				}
				break;
			case R.id.ci_dynamicitem_headicon: // 点击好友头像跳到好友的界面
				if ("1".equals(mAllDynamics.get(position).isnm)) {
					UIUtils.showToast(mActivity, "当前用户是匿名状态！");
				} else {
					goFrinedPage(position);
				}
				break;
			default:
				break;
			}
		}

		/**
		 * 当该条动态是转发动态的时候 防止发布语音和转发语音同时播放冲突
		 * 
		 * @param model
		 * @param tv
		 */
		private void stopRelativeVoice(Voices_model model, TextView tv) {
			if (!mIsplay && mVoiceBtn != null && !mVoiceBtn.equals(tv)
					&& model != null) {
				mIsplay = true;
				DynamicUtlis.getInstance().stopPlayVoice(mVoiceBtn, model,
						mVoicePlay, TAG);
			}
		}
	}

	/**
	 * 点击好友头像进入好友界面
	 * 
	 * @param position
	 */
	public void goFrinedPage(int position) {
		if (!(mUser_Id.equals(mAllDynamics.get(position).uid))) { // 当前是朋友发布的动态
			Intent intent = new Intent(mActivity, OtherDataActivity.class);
			intent.putExtra(GlobalConstant.COMMENT_ADD_FRIEND,
					mAllDynamics.get(position).uid);
			startActivity(intent);
		}
	}

	/**
	 * 展示应用的适配器
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyAppAdapter extends BaseAdapter {

		private ArrayList<Recapp> recapp;

		public MyAppAdapter(ArrayList<Recapp> recapp) {
			this.recapp = recapp;
		}

		@Override
		public int getCount() {
			return recapp.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View
					.inflate(mActivity, R.layout.list_item_search, null);

			mHttp.showImage((ImageView) $(view, R.id.iv_home),
					recapp.get(position).pic);
			TextView tv_home = $(view, R.id.tv_home);
			tv_home.setText(recapp.get(position).name);

			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	}

	/**
	 * 初始化popupwindow操作
	 * 
	 * @param dt
	 * @param position
	 */
	@SuppressWarnings("deprecation")
	private void initPopupWindow(ViewHolder_DT dt, int position) {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			dt.im_mydynamic_show_more.setEnabled(false);
		} else {
			dt.im_mydynamic_show_more.setEnabled(true);
		}
		View popupView = UIUtils.inflate(R.layout.popupwindow);
		initPopup(dt, popupView, position);
		int width = mActivity.getWindowManager().getDefaultDisplay().getWidth();
		int height = mActivity.getWindowManager().getDefaultDisplay()
				.getHeight();

		mPopupWindow = new PopupWindow(popupView, width * 460 / 720,
				height * 85 / 1280);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));
		mPopupWindow.setOutsideTouchable(true);
		popupView.setOnTouchListener(new MyPopup(dt, position));
		int[] location = new int[2];
		dt.im_mydynamic_show_more.getLocationOnScreen(location);
		mPopupWindow.showAtLocation(dt.im_mydynamic_show_more,
				Gravity.NO_GRAVITY, location[0] - mPopupWindow.getWidth(),
				location[1]);
	}

	/**
	 * PopupWindow 对话框中自选项的点击事件
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyPopup implements OnClickListener, OnTouchListener {

		private ViewHolder_DT dt; // 动态显示viewholder
		private int position; // 当前的条目位置

		public MyPopup(ViewHolder_DT dt, int position) {
			this.dt = dt;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_popup_delete: // 删除
				LayoutInflater lf = LayoutInflater.from(mActivity);
				RelativeLayout layout = (RelativeLayout) lf.inflate(
						R.layout.dialog_layout, null);
				showDialog(position, layout);
				popupWindowHite(dt);
				break;
			case R.id.btn_popup_retrans: // 转发、
				popup_button_retrans(position, dt);
				popupWindowHite(dt);
				break;
			case R.id.btn_popup_dz: // 点赞

				popup_button_dianZan(position, dt);
				popupWindowHite(dt);
				break;
			case R.id.btn_popup_message: // 信息
				popup_button_message(position, dt);
				popupWindowHite(dt);
				break;

			default:
				break;
			}

		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				dt.im_mydynamic_show_more.setEnabled(true);
			}
			return false;
		}

	}

	/**
	 * 初始化popupwindow控件
	 * 
	 * @param dta
	 * @param popupView
	 * @param position
	 */
	private void initPopup(ViewHolder_DT dt, View popupView, int position) {
		mBtn_popup_delete = $(popupView, R.id.btn_popup_delete);
		mBtn_popup_dz = $(popupView, R.id.btn_popup_dz);
		mBtn_popup_message = $(popupView, R.id.btn_popup_message);
		mBtn_popup_retrans = $(popupView, R.id.btn_popup_retrans);
		setPopup(position);

		mBtn_popup_dz.setText(getResources().getString(
				R.string.text_dynamic_praise));
		mBtn_popup_delete.setOnClickListener(new MyPopup(dt, position));
		mBtn_popup_dz.setOnClickListener(new MyPopup(dt, position));
		mBtn_popup_message.setOnClickListener(new MyPopup(dt, position));
		mBtn_popup_retrans.setOnClickListener(new MyPopup(dt, position));
	}

	/**
	 * 点击条目中删除选项弹出对话框
	 * 
	 * @param layout
	 */
	private void showDialog(int position, RelativeLayout layout) {
		new MyDialog(mActivity, position, layout) {

			@Override
			public String getTitleText() {

				return mActivity.getResources().getString(
						R.string.text_is_delete);
			}

			@Override
			public void setButton_ok(int position) {
				dialog_button_delete(position);
			}
		};
	}

	/**
	 * 隐藏popupWindow对话框
	 * 
	 * @param dt
	 */
	private void popupWindowHite(ViewHolder_DT dt) {
		mPopupWindow.dismiss();
		mPopupWindow = null;
		dt.im_mydynamic_show_more.setEnabled(true);
	}

	/**
	 * 设置当前位置 状态 判断是否是自己发布的动态
	 * 
	 * @param position
	 */
	private void setPopup(int position) {

		if (mAllDynamics.get(position).uid != null
				&& mUser_Id.equals(mAllDynamics.get(position).uid)) {
			mBtn_popup_delete.setVisibility(View.VISIBLE);
			mBtn_popup_retrans.setVisibility(View.GONE);
		} else {
			mBtn_popup_delete.setVisibility(View.GONE);
			mBtn_popup_retrans.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 跳转到话题页面
	 */
	private void gotoHuatiPage() {
		NearbyPage np = ((MainActivity) mActivity).getNearbyPage();
		if (np != null) {
			np.setHuaTiPage();
		}
	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

}
