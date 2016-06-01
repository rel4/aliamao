package com.aliamauri.meat.activity.find_activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.hljy.Yjzq_fjBean;
import com.aliamauri.meat.bean.hljy.Yjzq_fjBean.Cont;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.LoadRequestCallBack;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.IconCompress;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.MyBDmapUtlis;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.umeng.analytics.MobclickAgent;

/**
 * 发现----一见钟情界面
 * 
 * 
 * @author limaokeji-windosc
 * 
 */
public class YjzqActivity extends Activity implements OnClickListener {

	private final int FBXX_PAGE = 0; // 发布消息 tag
	private final int FUJIN_PAGE = 1; // 附近 tag

	private final String TEXT_COLOR_NORMAL = "#525252"; // 按钮未点击的字体颜色
	private final String TEXT_COLOR_PRESSED = "#f95c71"; // 按钮点击的字体颜色

	private TextView mTv_find_hljy_rmgg_title; // 发布消息标题
	private View mV_find_hljy_rmgg_line; // 发布消息线
	private TextView mTv_find_hljy_xhta_title; // 附近标题
	private View mV_find_hljy_xhta_line; // 附近线
	private RelativeLayout mRl_find_hljy_rmgg; // 发布消息点击区域
	private RelativeLayout mRl_find_hljy_xhta; // 邂逅他点击区域
	private RelativeLayout mRl_show_frined_layout; // 显示推荐的好友布局
	private String mWd; // 伟度
	private String mJd; // 经度
	private EditText mEt_message_improt; // 用户发布信息
	private MapView mBmapView; // 百度地图控件
	private BaiduMap mBaiduMap; // 百度地图
	private RelativeLayout mRl_message_layout; // 聊天布局
	private boolean isSuccess; // 判断当前是否定位成功

	public int ZOOM_TAG = 14; // 设置默认比例尺的级别
	private int width; // 屏幕的宽度
	private int height; // 屏幕的高度
	private HttpHelp mHttpHelp;

	// *****************百度定位***************************
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = null;

	private double wd; // 伟度信息
	private double jd; // 经度信息

	// *****************百度定位***************************

	private CircleImageView mCiv_yjzq_friend_icon; // 显示推荐好友的头像
	private TextView mTv_yjzq_user_name; // 显示推荐好友的名字
	private TextView mTv_yjzq_user_location; // 显示推荐好友的位置名称
	private TextView mTv_yjzq_content; // 显示推荐好友的发表的内容
	private TextView mBtn_yjzq_left;// 显示推荐好友界面左按钮
	private TextView mBtn_yjzq_right; // 显示推荐好友界面右按钮

	private int mLoadPage; // 当前页数
	private ArrayList<Marker> mMyMarkers;// 存储好友的marker图标
	private ArrayList<String> mFrindUrl;// 存储下载好的好友头像路径

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ***************百度基础地图**********************
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		// ***************百度基础地图**********************
		setContentView(R.layout.activity_find_yjzq);
		isSuccess = false;// 默认没有找到当前位置
		mHttpHelp = new HttpHelp();
		mMyMarkers = new ArrayList<Marker>();
		mMyMarkers.clear();
		mFrindUrl = new ArrayList<String>();
		mFrindUrl.clear();
		mLoadPage = 1;
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		initView();
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION,
				"0&&0").split("&&");
		mWd = String.valueOf(location[0]);
		mJd = String.valueOf(location[location.length - 1]);
		mBaiduMap = mBmapView.getMap();
		setPageSelection(FBXX_PAGE);// 设置默认选中页面
		hideMapChildView();

		// *****************百度定位***************************
		myListener = new MyLocationListener();
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		// *****************百度定位***************************
		initLocation();
		mLocationClient.start();

	}

	ArrayList<Cont> mCont; // 获取所有推荐好友的信息

	/**
	 * 初始化推荐好友的网络数据
	 * 
	 * @param b
	 *            true :初次请求网络 false :多次请求网络
	 */
	private void initSearchFrindNet(final boolean b) {
		if (!isSuccess) {// 如果定位失败 经度 = 0 ； 伟度 = 0；
			jd = 0;
			wd = 0;
		}
		mHttpHelp.sendGet(NetworkConfig.getFj_friend_url(mLoadPage, wd, jd),
				Yjzq_fjBean.class, new MyRequestCallBack<Yjzq_fjBean>() {

					@Override
					public void onSucceed(Yjzq_fjBean bean) {
						if (bean == null || bean.status == null
								|| bean.cont == null || bean.distance == null) {
							UIUtils.showToast(YjzqActivity.this,
									"没有找到数据~~");
							return;
						}
						switch (bean.status) {
						case "1":
							if (b) {
								if (bean.cont.size() <= 0) {
									UIUtils.showToast(YjzqActivity.this,
											"没有更多数据了~~");
									return;
								}
								mCont = bean.cont;
								goCurrentLocation(bean.distance);
								showFrindLocation();
								setMarkerClick();
								mLoadPage++;
			  				} else {
								if (bean.cont.size() <= 0) {
									UIUtils.showToast(YjzqActivity.this,
											"没有更多数据了~~");
									return;
								}
								mCont.addAll(bean.cont);
								goCurrentLocation(bean.distance);
								showFrindLocation();
								setMarkerClick();
								mLoadPage++;
							}

							break;
						case "2":
							UIUtils.showToast(YjzqActivity.this,
									"你还没有登陆~~~");
							break;

						default:
							UIUtils.showToast(YjzqActivity.this,
									"数据获取失败~~~");
							break;
						}

					}
				});
	}

	/**
	 * 初始化定位信息
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设
															// 备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	/**
	 * 删除百度地图的子图标
	 */
	private void hideMapChildView() {
		int childCount = mBmapView.getChildCount();

		View zoom = null;
		for (int i = 0; i < childCount; i++) {

			View child = mBmapView.getChildAt(i);

			if (child instanceof ZoomControls) {
				zoom = child;
				break;
			}
		}

		zoom.setVisibility(View.GONE);

		// 隐藏比例尺控件

		int count = mBmapView.getChildCount();
		View scale = null;
		for (int i = 0; i < count; i++) {
			View child = mBmapView.getChildAt(i);
			if (child instanceof ZoomControls) {
				scale = child;
				break;
			}
		}

		scale.setVisibility(View.GONE);

		// 隐藏指南针

		UiSettings uiSettings = mBaiduMap.getUiSettings();
		uiSettings.setCompassEnabled(true);

		// 删除百度地图logo
		mBmapView.removeViewAt(1);
	}

	private void initView() {
		$(R.id.tv_btn_ok).setOnClickListener(this);
		((TextView) $(R.id.tv_title_title)).setText("一键钟情");
		$(R.id.ll_title_talk).setVisibility(View.GONE);
		$(R.id.tv_title_right).setVisibility(View.GONE);	
		$(R.id.iv_title_backicon).setOnClickListener(this);
		mRl_find_hljy_rmgg = $(R.id.rl_find_hljy_rmgg);
		mRl_find_hljy_xhta = $(R.id.rl_find_hljy_xhta);
		mRl_find_hljy_rmgg.setOnClickListener(this);
		mRl_find_hljy_xhta.setOnClickListener(this);
		mTv_find_hljy_rmgg_title = $(R.id.tv_find_hljy_rmgg_title);
		mV_find_hljy_rmgg_line = $(R.id.v_find_hljy_rmgg_line);
		mTv_find_hljy_xhta_title = $(R.id.tv_find_hljy_xhta_title);
		mV_find_hljy_xhta_line = $(R.id.v_find_hljy_xhta_line);

		mRl_show_frined_layout = $(R.id.rl_show_frined_layout);
		mCiv_yjzq_friend_icon = $(R.id.civ_yjzq_friend_icon);
		mTv_yjzq_user_name = $(R.id.tv_yjzq_user_name);
		mTv_yjzq_user_location = $(R.id.tv_yjzq_user_location);
		mTv_yjzq_content = $(R.id.tv_yjzq_content);

		mBtn_yjzq_left = $(R.id.btn_yjzq_left);
		mBtn_yjzq_left.setOnClickListener(this);
		mBtn_yjzq_right = $(R.id.btn_yjzq_right);
		mBtn_yjzq_right.setOnClickListener(this);

		mRl_show_frined_layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		mBmapView = $(R.id.bmapView);
		mEt_message_improt = $(R.id.et_message_import);
		mRl_message_layout = $(R.id.rl_message_layout);

		mEt_message_improt.addTextChangedListener(new MyTextWatcher());
	}

	private boolean isSeted = true;

	/**
	 * 监听输入的文字的个数动态改变输入框布局的高度
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyTextWatcher implements TextWatcher {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (isSeted && start + count > 14) {
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, height * 196 / 1280);
				params.gravity = Gravity.BOTTOM;
				mRl_message_layout.setLayoutParams(params);
				isSeted = false;
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable editable) {
			//屏蔽第三方输入法中表情图标
			 int index = mEt_message_improt.getSelectionStart() - 1;
	         if (index > 0) {
	              if (isEmojiCharacter(editable.charAt(index))) {
	                  Editable edit = mEt_message_improt.getText();
	                  edit.delete(index, index + 1);
	             }
	         }
		}
	}
	/**
	 * 判断字符是否为表情
	 */
	 private  boolean isEmojiCharacter(char codePoint) {
	       return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||(codePoint == 0xD) || (codePoint >= 0x20 && codePoint <= 0xD7FF))|| 
	    		   ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	  }
	/**
	 * 获取点击后的页面改变
	 * 
	 * @param index
	 */
	private void setPageSelection(int index) {
		resetBtn();

		switch (index) {
		case FBXX_PAGE:
			setCurrentState(FBXX_PAGE);

			break;
		case FUJIN_PAGE:
			setCurrentState(FUJIN_PAGE);

			break;

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mBmapView.onDestroy();
		/*
		 * 将下载的用户头像进行删除
		 */
		if (mFrindUrl != null) {
			for (int i = 0; i < mFrindUrl.size(); i++) {
				File file = new File(mFrindUrl.get(i));
				if (file.exists())
					file.delete();
			}
		}
		mFrindUrl = null;
	}

	@Override
	public void onResume() {
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mBmapView.onResume();
		super.onResume();
		MobclickAgent.onResume(this);

	}

	@Override
	public void onPause() {
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mBmapView.onPause();
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_backicon: // 返回按钮
			KeyBoardUtils.closeKeybord(mEt_message_improt,
					getApplicationContext());
			finish();
			break;
		case R.id.rl_find_hljy_rmgg: // 发布消息点击区域
			setPageSelection(FBXX_PAGE);
			break;
		case R.id.rl_find_hljy_xhta: // 附近点击区域
			mLoadPage = 1; // 每次点击附近按钮就初始化页数为1
			KeyBoardUtils.closeKeybord(mEt_message_improt,
					getApplicationContext());
			if (mCont != null) {
				mCont.clear();
			}
			if (mMyMarkers != null) {
				mMyMarkers.clear();
			}

			if (mTitle_tag != null) {
				mTitle_tag = null;
			}

			setPageSelection(FUJIN_PAGE);
			break;
		case R.id.tv_btn_ok: // 确认发布消息按钮
			getImportText();
			break;
		case R.id.btn_yjzq_left: // 显示推荐好友界面的左侧按钮
			mRl_show_frined_layout.setVisibility(View.GONE);
			mRl_show_frined_layout.setAnimation(AnimationUtils.loadAnimation(
					YjzqActivity.this, R.anim.view_close));
			mBtn_yjzq_left.setEnabled(false);
			if (mTitle_tag == null) {
				UIUtils.showToast(getApplicationContext(), "没有找到要回应的好友~~");
				return;
			}

			// 通过遍历好友集合通过点击时候的gettitle获取的id值找到忽略好友的id值，进行好友资料的删除
			for (int i = 0; i < mCont.size(); i++) {
				if (mTitle_tag.equals(mCont.get(i).id)) {
					Intent intent = new Intent(getApplicationContext(),
							Hfpy_activity.class);
					intent.putExtra(GlobalConstant.HYPY_TAG, mCont.get(i));
					startActivity(intent);
					break;
				}
			}

			break;
		case R.id.btn_yjzq_right: // 显示推荐界面的右侧按钮
			boolean marker_b = false;
			boolean cont_b = false;
			if (mCont != null && mCont.size() < 3) {// 当忽略的好友少于3个的时候就请求网络加载新的好友
				initSearchFrindNet(false);
			}
			mRl_show_frined_layout.setVisibility(View.GONE);
			mRl_show_frined_layout.setAnimation(AnimationUtils.loadAnimation(
					YjzqActivity.this, R.anim.view_close));
			mBtn_yjzq_right.setEnabled(false);
			if (mTitle_tag == null) {
				UIUtils.showToast(getApplicationContext(), "没有找到要忽略的好友~~");
				marker_b = true;
				return;
			}
			// 通过遍历marker集合通过点击时候的gettitle获取的id值找到忽略好友的marker,进行地图图标的删除
			for (int i = 0; i < mMyMarkers.size(); i++) {
				if (mTitle_tag.equals(mMyMarkers.get(i).getTitle())) {
					mMyMarkers.get(0).setToTop();
					mMyMarkers.get(i).remove();

					cont_b = true;
					break;
				}
			}

			// 通过遍历好友集合通过点击时候的gettitle获取的id值找到忽略好友的id值，进行好友资料的删除
			for (int i = 0; i < mCont.size(); i++) {
				if (mTitle_tag.equals(mCont.get(i).id)) {
					// 在集合中删除该朋友的信息
					mCont.remove(i);
					break;
				}
			}
			if (marker_b == false && cont_b == false) {
				UIUtils.showToast(getApplicationContext(), "忽略好友失败~~");
			}
			break;

		default:
			break;
		}
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (KeyEvent.KEYCODE_BACK == keyCode) {
			// 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出地图",
						Toast.LENGTH_SHORT).show();
				// 将系统当前的时间赋值给exitTime
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	/**
	 * 获取输入框的文字信息内容
	 */
	private void getImportText() {
		if (isSuccess) {
			isSeted = true;
			String user_text = mEt_message_improt.getText().toString().trim();
			if (StringUtils.isEmpty(user_text)) {
				UIUtils.showToast(getApplicationContext(), "输入的内容不能为空~~");
				return;
			}
			if (user_text.length() > 30) {
				UIUtils.showToast(getApplicationContext(),
						"输入的内容不能超过30个字~~");
				return;
			}

			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, height * 98 / 1280);
			params.gravity = Gravity.BOTTOM;
			mRl_message_layout.setLayoutParams(params);
			KeyBoardUtils.closeKeybord(mEt_message_improt,
					getApplicationContext());

			publishMessage(user_text);

		} else {
			UIUtils.showToast(getApplicationContext(), "现在还在定位中~~稍等一会~");
		}

	}

	/**
	 * 发布消息功能
	 * 
	 * @param user_text
	 */
	private void publishMessage(final String user_text) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("name", user_text);
		params.addBodyParameter("jd", String.valueOf(jd));
		params.addBodyParameter("wd", String.valueOf(wd));
		mHttpHelp.sendPost(NetworkConfig.getFBXX_url(), params, BaseBaen.class,
				new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || bean.status == null) {
							return;
						}
						switch (bean.status) {
						case "1":
							UIUtils.showToast(getApplicationContext(),
									"信息发布成功啦~~");
							goCurrentLocation(null);
							setInfoWindowText(user_text);
							mEt_message_improt.setText("");
							break;
						case "2":
							UIUtils.showToast(getApplicationContext(),
									"呀~你还没有登陆~~");
							break;

						default:
							break;
						}

					}
				});
	}

	private InfoWindow mInfoWindow;

	/**
	 * 设置地图上弹窗的文字样式
	 * 
	 * @param user_text
	 */
	private void setInfoWindowText(String user_text) {
		TextView popup_show_text = (TextView) View.inflate(
				getApplicationContext(), R.layout.popup_text_layout, null);
		/**
		 * 如果当前的用户输入的文字长度大于13就固定发表框的宽度
		 */
		if (getTextLength(user_text) > 13) {
			popup_show_text.setWidth(height * 400 / 1280);
		}
		popup_show_text.setText(user_text);
		// 将marker所在的经纬度的信息转化成屏幕上的坐标
		LatLng point = new LatLng(wd, jd);
		// 创建InfoWindow,传入view，地理坐标，y 轴偏移量
		mInfoWindow = new InfoWindow(popup_show_text, point, height * (-50)
				/ 1280);
		// 显示InfoWindow
		mBaiduMap.showInfoWindow(mInfoWindow);
	}

	/**
	 * 判断用户输入的文字的长度 若为英文小写字母则两个小写字母为一个汉字的宽度 标点符号为默认为一个汉字的宽度 两个空格默认为一个汉子宽度
	 * 
	 * @param user_text
	 * @return
	 */
	private int getTextLength(String user_text) {
		float abccount = 0; // 小写英文字母个数
		float numcount = 0; // 数字个数
		float spacecount = 0; // 空格个数
		int othercount = 0; // 其他字符个数
		char[] b = user_text.toCharArray();
		for (int i = 0; i < b.length; i++) {
			if (b[i] >= 'a' && b[i] <= 'z') {
				abccount++;
			} else if (b[i] >= '0' && b[i] <= '9') {
				numcount++;
			} else if (b[i] == ' ') {
				spacecount++;
			} else {
				othercount++;
			}
		}
		int abc = (int) (abccount / 2 + 0.5);
		int num = (int) (numcount / 2 + 0.5);
		int space = (int) (spacecount / 2 + 0.5);
		return abc + num + space + othercount;
	}

	/**
	 * 设置当前图标的状态
	 * 
	 * @param nearbyPage
	 */
	private void setCurrentState(int num) {// 点击标签的时候改变图标和文字的颜色,状态
		switch (num) {
		case FBXX_PAGE: // 发布消息页面
			setBtnStyle_fbxx();
			if (isSuccess) {
				if (mRl_show_frined_layout.getVisibility() == View.VISIBLE) {
					mRl_show_frined_layout.setVisibility(View.GONE);
					mRl_show_frined_layout
							.setAnimation(AnimationUtils.loadAnimation(
									YjzqActivity.this, R.anim.view_close));
				}
				mBaiduMap.clear();
				goCurrentLocation(null);
				setFirendLocaltion(wd, jd, true, 0);
				showInfoPopup();
			}
			break;
		case FUJIN_PAGE: // 附近页面
			setBtnStyle_fx();
			if (isSuccess) {
				initSearchFrindNet(true);

				hideInfoPopup();
			}
			break;

		default:
			break;
		}

	}

	private String mTitle_tag; // 获取当前点击的头像的id

	/**
	 * 设置marker点击事件 通过判断点击的marker图标的来获取当前的marker的经纬度
	 * 在存储marker经纬度的集合中进行匹配找出该地理位置的好友
	 */
	private void setMarkerClick() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				mBtn_yjzq_right.setEnabled(true);
				mBtn_yjzq_left.setEnabled(true);
				mTitle_tag = marker.getTitle();
				for (int i = 0; i < mCont.size(); i++) {
					if (mTitle_tag.equals(mCont.get(i).id)) {
						mRl_show_frined_layout.setVisibility(View.VISIBLE);
						mRl_show_frined_layout.setAnimation(AnimationUtils
								.loadAnimation(YjzqActivity.this,
										R.anim.view_open));
						mHttpHelp.showImage_headIcon(mCiv_yjzq_friend_icon,
								mCont.get(i).face);
						mTv_yjzq_user_name.setText(mCont.get(i).nickname);
						mTv_yjzq_user_location.setText(new MyBDmapUtlis().getCurrentDistance(
								new LatLng(Double.valueOf(mCont.get(i).wd), Double.valueOf(mCont.get(i).jd)),
								new LatLng(Double.valueOf(mWd), Double.valueOf(mJd))));
						mTv_yjzq_content.setText(mCont.get(i).name);
						break;
					}
				}

				return true;
			}
		});

	}

	/**
	 * 如果用户发布过消息再次点击的时候就显示出来
	 */
	private void showInfoPopup() {
		if (mInfoWindow != null) {
			mBaiduMap.showInfoWindow(mInfoWindow);
		}

	}

	/**
	 * 隐藏发布消息的消息框
	 */
	private void hideInfoPopup() {
		if (mInfoWindow != null) {
			mBaiduMap.hideInfoWindow();
		}

	}

	/**
	 * 设置当前的位置在地图中心
	 * 
	 * @param distance
	 * @param b
	 *            distance == null :进入地图和在发布消息页面 distance !=null 附近页面
	 *            distance:使用固定比例尺，或动态比例尺
	 */
	private void goCurrentLocation(String distance) {
		int scaleSize = 0;
		if (isSuccess) {
			if (distance == null) {
				scaleSize = ZOOM_TAG;
			} else {
				scaleSize = getScaleSize(distance);
			}
			MapStatus mMapStatus = new MapStatus.Builder()
					.target(new LatLng(wd, jd)).zoom(scaleSize).build();
			// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
			MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
					.newMapStatus(mMapStatus);
			// 改变地图状态
			mBaiduMap.animateMapStatus(mMapStatusUpdate);
		}
	}

	/**
	 * 根据网络动态获取比例尺大小
	 * 
	 * @param scaleSize
	 * @return
	 */
	private int getScaleSize(String distance) {
		switch (distance) {
		case "0":
			return 15;
		case "1":
			return 14;
		case "2":
			return 13;
		case "3":
			return 12;
		case "4":
			return 11;
		case "5":
			return 10;
		case "6":
			return 9;
		case "7":
			return 8;
		case "8":
			return 7;
		default:
			return 12;
		}
	}

	/**
	 * 设置附近按钮的样式
	 */
	private void setBtnStyle_fx() {
		mTv_find_hljy_xhta_title.setTextColor(Color
				.parseColor(TEXT_COLOR_PRESSED));
		mV_find_hljy_xhta_line.setVisibility(View.VISIBLE);
		mRl_find_hljy_rmgg.setEnabled(true);
		mRl_find_hljy_xhta.setEnabled(false);
		mRl_message_layout.setVisibility(View.GONE);
	}

	/**
	 * 设置发布信息按钮的样式
	 */
	private void setBtnStyle_fbxx() {
		mTv_find_hljy_rmgg_title.setTextColor(Color
				.parseColor(TEXT_COLOR_PRESSED));
		mV_find_hljy_rmgg_line.setVisibility(View.VISIBLE);
		mRl_find_hljy_rmgg.setEnabled(false);
		mRl_find_hljy_xhta.setEnabled(true);
		mRl_message_layout.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示好友的位置
	 * 
	 * 根据数据库返回的头像地址集合，下载到本地后，然后再显示到地图上
	 * 
	 */
	private void showFrindLocation() {
		if (isSuccess) {
			for (int i = 0; i < mCont.size(); i++) {
				setFirendLocaltion(Double.parseDouble(mCont.get(i).wd),
						Double.parseDouble(mCont.get(i).jd), false, i);
			}

		} else {
			UIUtils.showToast(this, "呀~我迷路了，找不到我自己了~~");
		}

	}

	/**
	 * 下载朋友头像的类
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyloadRequestCallBack implements LoadRequestCallBack<File> {

		private String localURL;
		private CircleImageView civ_friend;
		private int index;
		private LatLng point;

		public MyloadRequestCallBack(String localURL, CircleImageView civ_friend, int index, LatLng point) {
			this.localURL = localURL;
			this.civ_friend = civ_friend;
			this.index = index;
			this.point = point;
			
		}

		

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
		}

		@Override
		public void onSucceed(ResponseInfo<File> file) {
			mFrindUrl.add(localURL);
			Bitmap decodeFile = BitmapFactory.decodeFile(localURL);
			setMapIconBitmap(civ_friend, decodeFile,index,point);
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.head_default_icon);
			setMapIconBitmap(civ_friend, decodeResource,index,point);
			
		}

	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			StringBuffer sb = new StringBuffer(256);

			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				isSuccess = true;
				getCurrentLocation(location);
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				isSuccess = true;
				getCurrentLocation(location);
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				isSuccess = true;
				getCurrentLocation(location);
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				isSuccess = false;
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
				UIUtils.showToast(getApplicationContext(), "找不到位置了~~");
				mLocationClient.stop();
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				isSuccess = false;
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
				UIUtils.showToast(getApplicationContext(),
						"找不到位置了~~网网断了么？");
				mLocationClient.stop();
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				isSuccess = false;
				UIUtils.showToast(getApplicationContext(),
						"找不到位置了~~我在飞行模式么？");
				mLocationClient.stop();
			}

			Log.e("BaiduLocationApiDem", sb.toString());
		}

		/**
		 * 获取当前的位置信息
		 * 
		 * @param location
		 * @param sb
		 */
		private void getCurrentLocation(BDLocation location) {

			wd = location.getLatitude();
			jd = location.getLongitude();
			String locationMsg = location.getLocationDescribe();// 大体位置信息
			mLocationClient.stop();
			// 定义Maker坐标点
			LatLng point = new LatLng(wd, jd);
			// 设置默认比例尺范围（3-19），数字越大比例越小
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(point,
					ZOOM_TAG);
			if (mBaiduMap == null) {
				mBaiduMap = mBmapView.getMap();
			}
			mBaiduMap.setMapStatus(msu);

			setFirendLocaltion(wd, jd, true, 0);

		}
	}

	/**
	 * 设置朋友的位置,或着自己的位置
	 * 
	 * @param wd
	 * @param jd
	 * @param b
	 *            true:显示自己的位置图标 false:显示朋友的位置图标
	 * @param index
	 */

	public void setFirendLocaltion(double wd, double jd, boolean b, int index) {
		// 定义Maker坐标点
		LatLng point = new LatLng(wd, jd);
		BitmapDescriptor bitmap = null;
		if (b) {
			// 构建Marker图标 (自己位置的图标)
			bitmap = BitmapDescriptorFactory.fromResource(R.drawable.map_icon);
			// 在地图上找到自己的位置
			OverlayOptions option = new MarkerOptions().position(point).icon(
					bitmap);
			// 在地图上添加Marker，并显示
			mBaiduMap.addOverlay(option);

		} else {
			// 构建Marker图标 (好友位置的图标)
			View view = View.inflate(getApplicationContext(),
					R.layout.civ_friend_icon, null);
			CircleImageView civ_friend = $(view, R.id.civ_friend);
			if (mCont.get(index).face == null|| StringUtils.isEmpty(mCont.get(index).face)) {
				Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.head_default_icon);
				setMapIconBitmap(civ_friend, decodeResource,index,point);
			} else {
				/*
				 * 通过头像地址将好友的头像下载到本地，然后显示到地图上。
				 */
				if (mHttpHelp == null) {
					mHttpHelp = new HttpHelp();
				}
				String localURL = GlobalConstant.YJZQ_HEAD_ICON_SAVEPATH+ String.valueOf(System.currentTimeMillis())+ index + ".jpg";
				mHttpHelp.downLoad(mCont.get(index).face, localURL,new MyloadRequestCallBack(localURL,civ_friend,index,point));
			}
			
		}

	}

	/**
	 * 在地图上设置好友的头像
	 * 
	 * @param civ_friend
	 * @param decodeResource
	 * @param index 
	 * @param point 
	 */
	private void setMapIconBitmap(CircleImageView civ_friend,Bitmap decodeResource, int index, LatLng point) {
		Bitmap zoomBitmap = IconCompress.zoomImage(decodeResource,width * 100 / 720, height * 100 / 1280);
		civ_friend.setImageBitmap(zoomBitmap);
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(civ_friend);
		// 在地图上找到自己的位置
		OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
		// 在地图上添加Marker，并显示
		Marker marker = (Marker) mBaiduMap.addOverlay(option);
		marker.setTitle(mCont.get(index).id);

		mMyMarkers.add(marker);
	}

	/**
	 * 选择界面的时候重置所有的按钮，图片，文字的颜色
	 */
	private void resetBtn() {

		mTv_find_hljy_rmgg_title.setTextColor(Color
				.parseColor(TEXT_COLOR_NORMAL));
		mV_find_hljy_rmgg_line.setVisibility(View.GONE);
		mTv_find_hljy_xhta_title.setTextColor(Color
				.parseColor(TEXT_COLOR_NORMAL));
		mV_find_hljy_xhta_line.setVisibility(View.GONE);

	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	public <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

	/**
	 * 根据id查找控件
	 * 
	 * @param id
	 * @return
	 */
	public <T extends View> T $(int id) {
		return (T) findViewById(id);
	}
}
