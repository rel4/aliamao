package com.aliamauri.meat.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.model.ProvinceModel;
import com.aliamauri.meat.eventBus.GetDialogData;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.utils.XmlParserHandler;
import com.aliamauri.meat.weight.wheelview.NumericWheelAdapter;
import com.aliamauri.meat.weight.wheelview.OnWheelScrollListener;
import com.aliamauri.meat.weight.wheelview.WheelAdapter;
import com.aliamauri.meat.weight.wheelview.WheelView;

import de.greenrobot.event.EventBus;

/**
 * 弹出选择对话框的dialog类
 * 
 * @author limaokeji-windosc
 * 
 */
public class MySearchDialog implements OnClickListener {
	public final int TAG_SEX = 1; // 点击搜索按钮后 ，，，性别标记
	public final int TAG_AGE = 2; // 点击搜索按钮后。。。年龄标记
	public final int TAG_FAR = 3; // 点击搜索按钮后。。。 距离标记
	public final int TAG_CITY = 4; // 点击搜索按钮后。。。 距离标记
	private LayoutInflater li;
	private Activity ac;
	private int current_tag;
	private Dialog dialog;

	private TextView tv_pop_condition_title;
	private WheelView wv_condition_left;
	private WheelView wv_condition_right;
	private TextView tv_pop_condition_success;// 完成按钮

	public MySearchDialog(LayoutInflater inflater, Activity mActivity, int tag) {
		li = inflater;
		ac = mActivity;
		current_tag = tag;
		init();
	}

	private void init() {
		RelativeLayout rl = (RelativeLayout) li.inflate(
				R.layout.dialog_search_layout, null);
		dialog = new AlertDialog.Builder(ac).create();
		dialog.show();
		dialog.setContentView(rl);
		initView(rl);
		getContentView();
		setListener();
		rl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog.dismiss();
				return false;
			}
		});
	}

	private void setListener() {
		tv_pop_condition_success.setOnClickListener(this);
	}

	private void getContentView() {
		switch (current_tag) {
		case TAG_SEX:
			initSexView(); // 性别
			break;
		case TAG_AGE:
			initAgeView(); // 年龄
			break;
		case TAG_FAR:
			initFarView(); // 距离
			break;
		case TAG_CITY:
			initCityView(); // 性别

		}
	}

	private static NumericWheelAdapter ageRightAdapter;

	private void initFarView() {

		tv_pop_condition_title.setText("距离");
		wv_condition_left.setLabel("km");
		wv_condition_left.setAdapter(new NumericWheelAdapter(1, 100));
		wv_condition_right.setLabel("km");
		wv_condition_left.setCurrentItem(0);
		leftPosition = wv_condition_left.getCurrentItem();
		ageRightAdapter = new NumericWheelAdapter(leftPosition + 0, 100);
		wv_condition_right.setAdapter(ageRightAdapter);
		wv_condition_right.setCurrentItem(0);

		wv_condition_left.addScrollingListener(sexLeftListener);

	}

	private void initAgeView() {

		tv_pop_condition_title.setText("年龄");
		wv_condition_left.SET_TEXT_SIZE = true;
		wv_condition_left.CUSTOMER_TEXT_SIZE = 42;
		wv_condition_left.setAdapter(new NumericWheelAdapter(18, 99));
		wv_condition_right.SET_TEXT_SIZE = true;
		wv_condition_right.CUSTOMER_TEXT_SIZE = 42;

		wv_condition_left.setCurrentItem(0);

		leftPosition = wv_condition_left.getCurrentItem();
		ageRightAdapter = new NumericWheelAdapter(
				wv_condition_left.getCurrentItem() + 19, 100);
		wv_condition_right.setAdapter(ageRightAdapter);
		wv_condition_right.setCurrentItem(0);
		wv_condition_left.addScrollingListener(sexLeftListener);
	}

	private static int leftPosition = 0;

	private static int rightPosition = 0;
	private static int rightFirst = 0;
	OnWheelScrollListener sexLeftListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			leftPosition = wheel.getCurrentItem();
			rightPosition = wv_condition_right.getCurrentItem();
			rightFirst = Integer.parseInt(ageRightAdapter.getItem(0));
			ageRightAdapter = new NumericWheelAdapter(leftPosition + 19, 100);
			wv_condition_right.setAdapter(ageRightAdapter);
			if (rightFirst + rightPosition > leftPosition + 19) {
				wv_condition_right
						.setCurrentItem(rightPosition
								- (Integer.parseInt(ageRightAdapter.getItem(0)) - rightFirst));
			} else {
				wv_condition_right.setCurrentItem(0);
			}
		}
	};

	private void initCityView() {
		int sexIndex = 0;
		getProvince();
		tv_pop_condition_title.setText("城市");
		wv_condition_right.setVisibility(View.GONE);
		wv_condition_left.SET_TEXT_SIZE = true;
		wv_condition_left.CUSTOMER_TEXT_SIZE = 42;
		wv_condition_left.setAdapter(new WheelAdapter() {

			@Override
			public int getMaximumLength() {
				if (mDistrictDatasMap == null) {
					return 0;
				}
				return mDistrictDatasMap.size();
			}

			@Override
			public int getItemsCount() {
				return mDistrictDatasMap.size();
			}

			@Override
			public String getItem(int index) {
				return mDistrictDatasMap.get(index);
			}
		});
		wv_condition_left.setCurrentItem(sexIndex);
	}

	/**
	 * key - 市 values - 区
	 */
	protected List<String> mDistrictDatasMap;

	private void getProvince() {
		mDistrictDatasMap = new ArrayList<String>();
		List<ProvinceModel> provinceList = null;
		AssetManager asset = MyApplication.getMainActivity().getAssets();
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
			for (int i = 0; i < provinceList.size(); i++) {
				mDistrictDatasMap.add(provinceList.get(i).getName());
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

	private void initSexView() {
		int sexIndex = 0;

		tv_pop_condition_title.setText("性别");
		wv_condition_right.setVisibility(View.GONE);
		wv_condition_left.SET_TEXT_SIZE = true;
		wv_condition_left.CUSTOMER_TEXT_SIZE = 42;
		wv_condition_left.setAdapter(new WheelAdapter() {
			String sex[] = { "女", "男", "不限" };

			@Override
			public int getMaximumLength() {
				// TODO Auto-generated method stub
				return sex.length;
			}

			@Override
			public int getItemsCount() {
				// TODO Auto-generated method stub
				return sex.length;
			}

			@Override
			public String getItem(int index) {
				// TODO Auto-generated method stub
				return sex[index];
			}
		});
		wv_condition_left.setCurrentItem(sexIndex);
	}

	private void initView(RelativeLayout rl) {
		wv_condition_left = (WheelView) rl.findViewById(R.id.wv_condition_left);
		wv_condition_right = (WheelView) rl
				.findViewById(R.id.wv_condition_right);
		tv_pop_condition_title = (TextView) rl
				.findViewById(R.id.tv_pop_condition_title);
		tv_pop_condition_success = (TextView) rl
				.findViewById(R.id.tv_pop_condition_success);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_pop_condition_success:
			int right_value = 0;
			int left_value = wv_condition_left.getCurrentItem();
			if (ageRightAdapter != null) {
				right_value = wv_condition_right.getCurrentItem()
						+ Integer.parseInt(ageRightAdapter.getItem(0));
			}
			dialog.dismiss();
			GetDialogData gd = new GetDialogData(left_value, right_value);
			if (mDistrictDatasMap != null
					&& mDistrictDatasMap.size() > left_value) {
				gd.setCity(mDistrictDatasMap.get(left_value));
			}
			EventBus.getDefault().post(gd);
			break;

		default:
			break;
		}

	}

}
