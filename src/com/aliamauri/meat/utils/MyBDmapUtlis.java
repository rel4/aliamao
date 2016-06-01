package com.aliamauri.meat.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.aliamauri.meat.global.GlobalConstant;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;

public class MyBDmapUtlis {

	private Activity mActivity;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = null;
	private double mJd; // 经度
	private double mWd; // 伟度
	private boolean mIsSuccess;
	private String mLocationMsg;

	public MyBDmapUtlis() {
		super();
	}

	public MyBDmapUtlis(Activity mActivity) {
		this.mActivity = mActivity;
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

	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			StringBuffer sb = new StringBuffer(256);

			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				mWd = location.getLatitude();
				mJd = location.getLongitude();
				mLocationMsg = jionFileName(location.getProvince(),location.getDistrict(),location.getStreet());
				mIsSuccess = true;
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				mWd = location.getLatitude();
				mJd = location.getLongitude();
				mLocationMsg = jionFileName(location.getProvince(),location.getDistrict(),location.getStreet());
				mIsSuccess = true;
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				mWd = location.getLatitude();
				mJd = location.getLongitude();
				mLocationMsg = jionFileName(location.getProvince(),location.getDistrict(),location.getStreet());
				mIsSuccess = true;
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
				UIUtils.showToast(mActivity.getApplicationContext(), "找不到位置了~~");
				mWd = 0;
				mJd = 0;
				mIsSuccess = false;
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
				UIUtils.showToast(mActivity.getApplicationContext(), "请检查网络");
				mWd = 0;
				mJd = 0;
				mIsSuccess = false;
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				UIUtils.showToast(mActivity.getApplicationContext(), "请检查网络");
				mWd = 0;
				mJd = 0;
				mIsSuccess = false;
			}
			mLocationClient.stop();

			if (mIsSuccess) {
				PrefUtils.setString(GlobalConstant.USER_LOCATION, mWd + "&&"
						+ mJd);
				PrefUtils.setString(GlobalConstant.USER_LOCATION_MSG,
						mLocationMsg);
			} else {
				PrefUtils.setString(GlobalConstant.USER_LOCATION, "0&&0");
				PrefUtils.setString(GlobalConstant.USER_LOCATION_MSG, "");
			}

			Log.e("BaiduLocationApiDem", sb.toString());
		}
	}

	/**
	 * 获取当前经纬度，以及位置信息 存到sp当中
	 */
	public void getCurrentLocation() {
		myListener = new MyLocationListener();
		mLocationClient = new LocationClient(mActivity.getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initLocation();
		mLocationClient.start();
	}

	/**
	 * 获取两个坐标点之间的位置
	 * 
	 * @param p1
	 * @param p2
	 * @return km
	 */
	public String getCurrentDistance(LatLng start, LatLng end) {
		double lat1 = (Math.PI / 180) * start.latitude;
		double lat2 = (Math.PI / 180) * end.latitude;

		double lon1 = (Math.PI / 180) * start.longitude;
		double lon2 = (Math.PI / 180) * end.longitude;
		if(lat1 ==0 || lat2 ==0 || lon1 == 0 || lon2 == 0){
			return "3km以内";
		}
		// 地球半径
		double R = 6371;

		// 两点间距离 m
		double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.cos(lon2 - lon1))
				* R * 1000;
		if (d > 3000) {
			return String.valueOf((int) (d / 1000)) + "km";
		} else {
				return "3km以内";
		}
	}

	/**
	 * 切分文件名称
	 * 
	 * @param fileTypeVideo
	 * @return
	 */
	public String[] splitCityNameArray(String fileNames) {

		if (TextUtils.isEmpty(fileNames)) {
			return null;
		}
		return fileNames.split("\\|");

	}

	/**
	 * 拼接文件名称
	 * 
	 * @param pro
	 *            省
	 * @param dis
	 *            市
	 * @param str
	 *            区
	 * @return
	 */

	private String jionFileName(String pro, String dis, String str) {
		if(StringUtils.isEmpty(pro)){
			pro = "err";
		}
		if(StringUtils.isEmpty(dis)){
			dis = "err";
		}
		if(StringUtils.isEmpty(str)){
			str = "err";
		}
		return new StringBuilder().append(pro).append("|").append(dis).append("|").append(str).toString();
	}

}
