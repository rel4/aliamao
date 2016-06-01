package com.aliamauri.meat.activity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.model.CityModel;
import com.aliamauri.meat.bean.model.DistrictModel;
import com.aliamauri.meat.bean.model.ProvinceModel;
import com.aliamauri.meat.utils.XmlParserHandler;
import com.aliamauri.meat.weight.wheelview.ArrayWheelAdapter;
import com.aliamauri.meat.weight.wheelview.OnWheelChangedListener;
import com.aliamauri.meat.weight.wheelview.WheelView;
import com.umeng.analytics.MobclickAgent;

/*
 * 
 author 院彩华
 弹出城市选择菜单

 */
public class SelectPlandWindow extends Activity implements OnClickListener,
		OnWheelChangedListener {

	private TextView tv_pcd_success;
	private WheelView wv_pcd_left;
	private WheelView wv_pcd_center;
	private WheelView wv_pcd_right;
	private Intent plandIntent;

	/**
	 * 所有省
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区
	 */
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

	/**
	 * key - 区 values - 邮编
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

	/**
	 * 当前省的名称
	 */
	protected String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	protected String mCurrentDistrictName = "";
	/**
	 * 当前区的邮政编码
	 */
	protected String mCurrentZipCode = "";

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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.p_c_d_selector);
		plandIntent = getIntent();
		init();// 初始化控件
		setListener();// 设置监听器
		initProvinceDatas();
		setUpData();
	}

	private void setListener() {
		tv_pcd_success.setOnClickListener(this);
		// 添加按钮监听
	}

	private void init() {
		tv_pcd_success = (TextView) findViewById(R.id.tv_pcd_success);
		wv_pcd_left = (WheelView) findViewById(R.id.wv_pcd_left);
		wv_pcd_center = (WheelView) findViewById(R.id.wv_pcd_center);
		wv_pcd_right = (WheelView) findViewById(R.id.wv_pcd_right);
	}

	private String[] arryPland;
	private boolean firstTime = true;// 判断是不是第一次如果是刚进这个页面还是后面自己手动滑动

	private void setUpData() {
		initProvinceDatas();
		String pland = "";
		if (plandIntent.getExtras() != null) {
			pland += plandIntent.getExtras().get("editdata").toString().trim();
		}
		arryPland = pland.split("/");
		wv_pcd_left.setAdapter(new ArrayWheelAdapter<String>(mProvinceDatas,
				mProvinceDatas.length));
		int index = 0;
		if (arryPland.length > 0)
			for (int i = 0; i < mProvinceDatas.length; i++) {
				if (mProvinceDatas[i].equals(arryPland[0])) {
					index = i;
				}
			}
		wv_pcd_left.setCurrentItem(index);
		// 设置可见条目数量
		updateCities();
		updateAreas();
		setWVListener();
	}

	private void setWVListener() {
		wv_pcd_left.addChangingListener(this);
		wv_pcd_center.addChangingListener(this);
		wv_pcd_right.addChangingListener(this);
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = wv_pcd_left.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		wv_pcd_center.setAdapter(new ArrayWheelAdapter<String>(cities,
				cities.length));
		if (firstTime) {
			int index = 0;
			if (arryPland.length > 1)
				for (int i = 0; i < cities.length; i++) {
					if (cities[i].equals(arryPland[1])) {
						index = i;
					}
				}
			wv_pcd_center.setCurrentItem(index);
		} else {
			wv_pcd_center.setCurrentItem(0);
		}
		updateAreas();
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = wv_pcd_center.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		wv_pcd_right.setAdapter(new ArrayWheelAdapter<String>(areas,
				areas.length));
		if (firstTime) {
			int index = 0;
			if (arryPland.length > 2)
				for (int i = 0; i < areas.length; i++) {
					if (areas[i].equals(arryPland[2])) {
						index = i;
					}
				}
			wv_pcd_right.setCurrentItem(index);
		} else {
			wv_pcd_right.setCurrentItem(0);
		}
		mCurrentDistrictName = areas[0];
		mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);

	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	/**
	 * 解析省市区的XML数据
	 */

	protected void initProvinceDatas() {
		List<ProvinceModel> provinceList = null;
		AssetManager asset = SelectPlandWindow.this.getAssets();
		try {
			InputStream input = asset.open("province_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			// */ 初始化默认选中的省、市、区
			if (provinceList != null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList != null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0)
							.getDistrictList();
					mCurrentDistrictName = districtList.get(0).getName();
					mCurrentZipCode = districtList.get(0).getZipcode();
				}
			}
			// */
			mProvinceDatas = new String[provinceList.size()];
			for (int i = 0; i < provinceList.size(); i++) {
				// 遍历所有省的数据
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityModel> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					// 遍历省下面的所有市的数据
					cityNames[j] = cityList.get(j).getName();
					List<DistrictModel> districtList = cityList.get(j)
							.getDistrictList();
					String[] distrinctNameArray = new String[districtList
							.size()];
					DistrictModel[] distrinctArray = new DistrictModel[districtList
							.size()];
					for (int k = 0; k < districtList.size(); k++) {
						// 遍历市下面所有区/县的数据
						DistrictModel districtModel = new DistrictModel(
								districtList.get(k).getName(), districtList
										.get(k).getZipcode());
						// 区/县对于的邮编，保存到mZipcodeDatasMap
						mZipcodeDatasMap.put(districtList.get(k).getName(),
								districtList.get(k).getZipcode());
						distrinctArray[k] = districtModel;
						distrinctNameArray[k] = districtModel.getName();
					}
					// 市-区/县的数据，保存到mDistrictDatasMap
					mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
				}
				// 省-市的数据，保存到mCitisDatasMap
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == wv_pcd_left) {
			updateCities();
		} else if (wheel == wv_pcd_center) {
			updateAreas();
		} else if (wheel == wv_pcd_right) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	public void onClick(View v) {
		Intent i = new Intent();
		switch (v.getId()) {
		case R.id.tv_pcd_success:
			int province = wv_pcd_left.getCurrentItem();
			mCurrentProviceName = mProvinceDatas[province];
			int city = wv_pcd_center.getCurrentItem();
			mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[city];
			int area = wv_pcd_right.getCurrentItem();
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[area];
			i.putExtra("editdata", mCurrentProviceName + "/" + mCurrentCityName
					+ "/" + mCurrentDistrictName);
			setResult(6, i);
			finish();
			break;
		default:
			break;
		}

	}
}