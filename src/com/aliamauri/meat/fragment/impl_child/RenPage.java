package com.aliamauri.meat.fragment.impl_child;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.activity.OtherDataActivity;
import com.aliamauri.meat.bean.Cont;
import com.aliamauri.meat.bean.RenBean;
import com.aliamauri.meat.eventBus.GetDialogData;
import com.aliamauri.meat.eventBus.IsShowRenSelectLayout;
import com.aliamauri.meat.fragment.BaseFragment_child;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CaculationUtils;
import com.aliamauri.meat.utils.ChangeUtils;
import com.aliamauri.meat.utils.MyBDmapUtlis;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.MySearchDialog;
import com.aliamauri.meat.view.RefreshListView;
import com.aliamauri.meat.view.RefreshListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 附近标题下的 人 子类 第二级
 * 
 * @author limaokeji-windosc
 * 
 */
public class RenPage extends BaseFragment_child implements OnClickListener,
		OnRefreshListener {

	public final String SEARCH_STATE = "search"; // 查询状态下展示
	public final String NORMAL_STATE = "normal"; // 正常状态下展示
	public final int TAG_SEX = 1; // 点击搜索按钮后 ，，，性别标记
	public final int TAG_AGE = 2; // 点击搜索按钮后。。。年龄标记
	public final int TAG_FAR = 3; // 点击搜索按钮后。。。 距离标记
	public final int TAG_CITY = 4; // 点击搜索按钮后。。。城市标记

	public int mCurrent_tag;
	public String mCurrentState = null; // 当前状态
	private View view;
	private LinearLayout mLl_select_condition; // 筛选布局

	private HttpHelp httpHelp;

	private TextView tv_title_title;
	private TextView tv_title_right;
	private ImageView iv_title_backicon;

	private RelativeLayout rl_selectcondition_sex;
	private TextView tv_selectcondition_sex1;
	private TextView tv_selectcondition_sex2;
	private ImageView iv_selectcondition_sex;

	private RelativeLayout rl_condition_age;
	private TextView tv_condition_age1;
	private TextView tv_condition_age2;
	private ImageView iv_condition_age;

	private RelativeLayout rl_condition_far;
	private TextView tv_condition_far1;
	private TextView tv_condition_far2;
	private ImageView iv_condition_far;

	private RefreshListView lv_selectcondition_all;
	private ConditionAdapter conditionAdapter;
	private String mWd; // 伟度
	private String mJd; // 经度
	private TextView tv_selectcondition_search;// 查找按钮

	private LinearLayout ll_select_condition;
	private TextView tv_selectcondition_result;
	private int page_normal; // 附近用户页码
	private int page_search; // 查找用户页码
	private String url; // 地址url
	private List<Cont> mCont;
	private LayoutInflater inflater;
	private TextView mNearby_search_btn; // 附近页面的筛选按钮
	private ProgressBar mPb_load_more_progress;
	private String mUser_Id;
	private MyBDmapUtlis dbUtlis;
	private String[] strs;

	private RelativeLayout rl_condition_city;
	private TextView tv_condition_city;

	@Override
	public View initChildView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mUser_Id = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_ID, "");
		dbUtlis = new MyBDmapUtlis();
		strs = dbUtlis.splitCityNameArray(PrefUtils.getString(
				GlobalConstant.USER_LOCATION_MSG, null));
		this.inflater = inflater;
		view = inflater.inflate(R.layout.select_condition, container, false);
		$(view, R.id.layout_title).setVisibility(View.GONE);
		mLl_select_condition = $(view, R.id.ll_select_condition);
		mPb_load_more_progress = $(view, R.id.pb_load_more_progress);

		rl_condition_city = $(view, R.id.rl_condition_city);
		tv_condition_city = $(view, R.id.tv_condition_city);
		httpHelp = new HttpHelp();
		mCont = new ArrayList<Cont>();
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION,
				"0&&0").split("&&");
		mWd = String.valueOf(location[0]);
		mJd = String.valueOf(location[location.length - 1]);
		mCurrentState = NORMAL_STATE; // 设置当前的状态为正常状态
		page_normal = 1; // 初始化附近用户页码为1
		page_search = 1; // 初始化查找用户页码为1
		initView();
		setListener();

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("RenPage");

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("RenPage");
	}

	@Override
	public void initChildDate() {
		initNet();
	}

	@Override
	public void onDestroy() {
		if (httpHelp != null) {
			httpHelp.stopHttpNET();
		}
		super.onDestroy();
	}

	/**
	 * 初始化网络数据 以及多次加载数据
	 */
	private void initNet() {
		// if (strs != null && strs[0] != null && !"err".equals(strs[0])) { // 省
		// }
		// if (strs != null && strs[1] != null && !"err".equals(strs[1])) { // 市
		// }
		// if (strs != null && strs[2] != null && !"err".equals(strs[2])) { // 县
		// }
		switch (mCurrentState) {
		case NORMAL_STATE:
			url = NetworkConfig.getNearbyUser(page_normal, strs[0], strs[1]);
			break;
		case SEARCH_STATE:
			String sex = getUrlSex(); // 性别
			String[] Age = getUrlAge(); // 年龄
			String[] Far = getUrlFar(); // 距离
			url = NetworkConfig.getSearchUser(page_search, sex, Age[0], Age[1],
					Far[0], Far[1], tv_condition_city.getText().toString(), "");
			break;
		}

		httpHelp.sendGet(url, RenBean.class, new MyRequestCallBack<RenBean>() {

			@Override
			public void onSucceed(RenBean bean) {
				mPb_load_more_progress.setVisibility(View.GONE);
				if (bean == null || bean.cont == null || bean.status == null) {
					UIUtils.showToast(mActivity, "网络异常！");
					return;
				}
				if ("1".equals(bean.status)) {
					if (bean.cont.size() <= 0) {
						UIUtils.showToast(mActivity, mActivity.getResources()
								.getString(R.string.text_no_data));
						lv_selectcondition_all.onRefreashFinish();
						if (mCurrentState == SEARCH_STATE && page_search == 1
								&& conditionAdapter != null) {
							mCont.clear();
							conditionAdapter.notifyDataSetChanged();
						}
						return;

					}
					switch (mCurrentState) {
					case NORMAL_STATE: // 正常状态下的显示数据
						if (page_normal > 1) {
							mCont.addAll(bean.cont);
							conditionAdapter.notifyDataSetChanged();
							lv_selectcondition_all.onRefreashFinish();
						} else {
							mCont.clear();
							mCont = bean.cont;
							conditionAdapter = new ConditionAdapter();
							lv_selectcondition_all.setAdapter(conditionAdapter);
							lv_selectcondition_all
									.setOnRefreshListener(RenPage.this);
						}
						page_normal++;
						break;
					case SEARCH_STATE: // 查询状态下显示的数据
						if (page_search > 1) {
							mCont.addAll(bean.cont);
							conditionAdapter.notifyDataSetChanged();
							lv_selectcondition_all.onRefreashFinish();
						} else {
							mCont.clear();
							mCont = bean.cont;
							conditionAdapter.notifyDataSetChanged();
						}
						page_search++;
						break;
					}
				}
			}
		});

	}

	/**
	 * 获取筛选框中的距离数据
	 * 
	 * @return
	 */
	private String[] getUrlFar() {
		String far = tv_condition_far2.getText().toString().trim();
		String repFar = far.replaceAll("[a-zA-Z]", "");
		return repFar.split("-");
	}

	/**
	 * 获取筛选框中的年龄数据
	 * 
	 * @return
	 */
	private String[] getUrlAge() {
		String age = tv_condition_age2.getText().toString().trim();
		String repAge = age.replaceAll("[\u4e00-\u9fa5]+", "");
		return repAge.split("-");
	}

	/**
	 * 获取筛选框中的性别数据 0----女 1----男 -1----全部
	 * 
	 * @return
	 */
	private String getUrlSex() {
		String sex = tv_selectcondition_sex2.getText().toString().trim();
		switch (ChangeUtils.ChangeSexToNumber(sex)) {
		case "0":
			return "0";
		case "1":
			return "1";
		}
		return "-1";
	}

	private void setListener() {
		tv_title_right.setOnClickListener(this);
		iv_title_backicon.setOnClickListener(this);
		rl_selectcondition_sex.setOnClickListener(this);
		rl_condition_age.setOnClickListener(this);
		rl_condition_far.setOnClickListener(this);
		tv_selectcondition_search.setOnClickListener(this);
		rl_condition_city.setOnClickListener(this);
	}

	private void initView() {
		iv_title_backicon = (ImageView) $(view, R.id.iv_title_backicon);
		tv_title_title = (TextView) $(view, R.id.tv_title_title);
		tv_title_title.setText("条件查找");
		tv_title_right = (TextView) $(view, R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("筛选");

		lv_selectcondition_all = (RefreshListView) $(view,
				R.id.lv_selectcondition_all);

		rl_selectcondition_sex = (RelativeLayout) $(view,
				R.id.rl_selectcondition_sex);
		iv_selectcondition_sex = (ImageView) $(view,
				R.id.iv_selectcondition_sex);
		tv_selectcondition_sex1 = (TextView) $(view,
				R.id.tv_selectcondition_sex1);
		tv_selectcondition_sex2 = (TextView) $(view,
				R.id.tv_selectcondition_sex2);

		tv_selectcondition_sex2.setText(getUserSex());

		rl_condition_age = (RelativeLayout) $(view, R.id.rl_condition_age);
		tv_condition_age1 = (TextView) $(view, R.id.tv_condition_age1);
		tv_condition_age2 = (TextView) $(view, R.id.tv_condition_age2);
		iv_condition_age = (ImageView) $(view, R.id.iv_condition_age);
		tv_condition_age2.setText(getUserAge());

		rl_condition_far = (RelativeLayout) $(view, R.id.rl_condition_far);
		tv_condition_far1 = (TextView) $(view, R.id.tv_condition_far1);
		tv_condition_far2 = (TextView) $(view, R.id.tv_condition_far2);
		iv_condition_far = (ImageView) $(view, R.id.iv_condition_far);
		tv_condition_far2.setText("1-5KM");

		if (strs[0] != null && !"".equals(strs[0].trim())
				&& !"err".equals(strs[0].trim())) {
			tv_condition_city.setText(strs[0]);
		} else {
			tv_condition_city.setText("北京");
		}

		tv_selectcondition_search = (TextView) $(view,
				R.id.tv_selectcondition_search);
		ll_select_condition = (LinearLayout) $(view, R.id.ll_select_condition);
		tv_selectcondition_result = (TextView) $(view,
				R.id.tv_selectcondition_result);

	}

	/**
	 * 根据本机用户的年龄来确定返回的年龄段
	 * 
	 * @return
	 */
	private String getUserAge() {
		String birthday = PrefUtils.getString(GlobalConstant.USER_BIRTH, null);
		if (StringUtils.isEmpty(birthday)
				|| Integer
						.parseInt(CaculationUtils.calculateDatePoor(birthday)) < 18) {
			return "18-22岁";
		}
		String age = CaculationUtils.calculateDatePoor(birthday);
		if (Integer.parseInt(age) < 0) {
			return "18-22岁";
		} else if (Integer.parseInt(age) - 2 <= 0) {
			return 1 + "-" + (Integer.parseInt(age) + 2) + "岁";
		}
		return (Integer.parseInt(age) - 2) + "-" + (Integer.parseInt(age) + 2)
				+ "岁";
	}

	/**
	 * 根据本机用户的性别，来确定搜索框的默认性别
	 * 
	 * @return
	 */
	private String getUserSex() {
		String sex = PrefUtils.getString(GlobalConstant.USER_SEX, "不限");
		switch (ChangeUtils.ChangeSexToNumber(sex)) {
		case "0":// 女返回男
			return "男";
		case "1":// 男返回女
			return "女";
		case "2":
		case "1000":
			return "不限";
		}
		return "不限";
	}

	class ConditionAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			return mCont.size();
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				// convertView = View.inflate(mActivity,
				// R.layout.select_condition_item, null);
				convertView = View.inflate(mActivity,
						R.layout.item_page_miaoke, null);
				viewHolder = new ViewHolder();
				viewHolder.civ_ipmiaoke_headicon = (CircleImageView) convertView
						.findViewById(R.id.civ_ipmiaoke_headicon);
				viewHolder.tv_ipmiaoke_sexandage = (TextView) convertView
						.findViewById(R.id.tv_ipmiaoke_sexandage);
				viewHolder.tv_ipmiaoke_nickname = (TextView) convertView
						.findViewById(R.id.tv_ipmiaoke_nickname);
				viewHolder.tv_ipmiaoke_intro = (TextView) convertView
						.findViewById(R.id.tv_ipmiaoke_intro);
				viewHolder.ll_ipmiaoke_add = (LinearLayout) convertView
						.findViewById(R.id.ll_ipmiaoke_add);
				viewHolder.tv_ipmiaoke_province = (TextView) convertView
						.findViewById(R.id.tv_ipmiaoke_province);
				viewHolder.tv_ipm_add = (TextView) convertView
						.findViewById(R.id.tv_ipm_add);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (mCont.get(position).local_state == 1) {
				viewHolder.ll_ipmiaoke_add.setSelected(true);
				viewHolder.tv_ipm_add.setText("已发送");
			} else {
				viewHolder.ll_ipmiaoke_add.setSelected(false);
				viewHolder.tv_ipm_add.setText("加好友");
				myOnClick = new MyOnClick(position, viewHolder.ll_ipmiaoke_add,
						viewHolder.tv_ipm_add);
				viewHolder.ll_ipmiaoke_add.setOnClickListener(myOnClick);
			}

			if ("1".equals(mCont.get(position).sex)) {
				viewHolder.tv_ipmiaoke_sexandage.setSelected(true);
			} else {
				viewHolder.tv_ipmiaoke_sexandage.setSelected(false);
			}
			viewHolder.tv_ipmiaoke_sexandage.setText(mCont.get(position).age);

			httpHelp.showImage(viewHolder.civ_ipmiaoke_headicon,
					mCont.get(position).face + "##");
			viewHolder.civ_ipmiaoke_headicon.setTag(mCont.get(position).id);
			viewHolder.civ_ipmiaoke_headicon.setOnClickListener(RenPage.this);
			viewHolder.tv_ipmiaoke_intro.setText(mCont.get(position).signature);
			viewHolder.tv_ipmiaoke_nickname
					.setText(mCont.get(position).nickname.length() > 5 ? mCont
							.get(position).nickname.substring(0, 5) + "..."
							: mCont.get(position).nickname);

			viewHolder.tv_ipmiaoke_province
					.setText(mCont.get(position).province);
			return convertView;
		}
	}

	private MyOnClick myOnClick;

	class MyOnClick implements OnClickListener {

		private int position;
		private LinearLayout add_ll;
		private TextView add_tv;

		public MyOnClick(int position, LinearLayout add_ll, TextView add_tv) {
			this.position = position;
			this.add_ll = add_ll;
			this.add_tv = add_tv;
		}

		@Override
		public void onClick(final View v) {
			v.setClickable(false);
			String id = mCont.get(position).id;
			if (id != null) {
				if (!add_ll.isSelected()) {
					CmdManager.getInstance().addContact(id, "",
							new CmdManagerCallBack() {
								@Override
								public void onState(boolean isSucceed) {
									v.setClickable(true);
									if (isSucceed) {
										UIUtils.showToast(mActivity, "发送请求成功~~");
										add_ll.setSelected(true);
										add_tv.setText("已发送");
										mCont.get(position).local_state = 1;
									} else {
										UIUtils.showToast(mActivity, "发送请求失败~~");
									}
								}
							});
				}
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_selectcondition_search:
			mCurrentState = SEARCH_STATE; // 设置当前的状态为查询状态
			page_search = 1;// 每次点击筛选按钮的时候将初始页置为1
			mCont.clear();// 每次点击筛选按钮的时候将之前的数据置为空
			mNearby_search_btn.setClickable(true);
			mNearby_search_btn.setEnabled(true);
			mLl_select_condition.setVisibility(View.GONE);
			tv_selectcondition_result.setVisibility(View.VISIBLE);
			initNet();
			break;
		case R.id.rl_selectcondition_sex:
			mCurrent_tag = TAG_SEX;
			showSearchDialog(TAG_SEX);
			break;
		case R.id.rl_condition_age:
			mCurrent_tag = TAG_AGE;
			showSearchDialog(TAG_AGE);
			break;
		case R.id.rl_condition_far:
			showSearchDialog(TAG_FAR);
			mCurrent_tag = TAG_FAR;
			break;
		case R.id.civ_ipmiaoke_headicon:
			goFrinedPage(v);
			break;
		case R.id.rl_condition_city:
			showSearchDialog(TAG_CITY);
			mCurrent_tag = TAG_CITY;
			break;
		}
	}

	/**
	 * 点击好友头像进入好友界面
	 * 
	 * @param v
	 * @param position
	 */
	public void goFrinedPage(View v) {
		String tag = (String) v.getTag();
		if (tag != null && !(mUser_Id.equals(tag))) { // 当前是朋友发布的动态
			Intent intent = new Intent(mActivity, OtherDataActivity.class);
			intent.putExtra(GlobalConstant.COMMENT_ADD_FRIEND, tag);
			startActivity(intent);
		}
	}

	/**
	 * 根据当前的状况显示相应的dailog弹窗
	 * 
	 * @param tAG_FAR2
	 */
	private void showSearchDialog(int tag) {
		recoverDefouldState();

		switch (tag) {
		case TAG_SEX: // 选择性别
			tv_selectcondition_sex1.setSelected(true);
			tv_selectcondition_sex2.setSelected(true);
			iv_selectcondition_sex.setSelected(true);
			setMySearchDialog(TAG_SEX);
			break;
		case TAG_AGE: // 选择年龄段
			tv_condition_age1.setSelected(true);
			tv_condition_age2.setSelected(true);
			iv_condition_age.setSelected(true);
			setMySearchDialog(TAG_AGE);
			break;
		case TAG_FAR: // 选择距离范围
			tv_condition_far1.setSelected(true);
			tv_condition_far2.setSelected(true);
			iv_condition_far.setSelected(true);
			setMySearchDialog(TAG_FAR);
			break;
		case TAG_CITY:
			rl_condition_city.setSelected(true);
			setMySearchDialog(TAG_CITY);
			break;

		}
	}

	/**
	 * 设置筛选框的样式
	 * 
	 * @param li
	 * @param a
	 * @param tag
	 */
	private void setMySearchDialog(int tag) {
		new MySearchDialog(inflater, mActivity, tag);
	}

	/**
	 * 恢复默认状态 筛选状态栏
	 */
	private void recoverDefouldState() {
		tv_selectcondition_sex1.setSelected(false);
		tv_selectcondition_sex2.setSelected(false);
		iv_selectcondition_sex.setSelected(false);

		tv_condition_age1.setSelected(false);
		tv_condition_age2.setSelected(false);
		iv_condition_age.setSelected(false);

		tv_condition_far1.setSelected(false);
		tv_condition_far2.setSelected(false);
		iv_condition_far.setSelected(false);

		rl_condition_city.setSelected(false);

	}

	class ViewHolder {
		public LinearLayout ll_ipmiaoke_add;
		public CircleImageView civ_ipmiaoke_headicon;
		public TextView tv_ipmiaoke_nickname;
		public TextView tv_ipmiaoke_sexandage;
		public TextView tv_ipmiaoke_province;
		// public TextView tv_sci_sex;
		// public TextView tv_sci_authenticate;
		public TextView tv_ipmiaoke_intro;
		public TextView tv_ipm_add;
		// public TextView tv_sci_distance;
	}

	/**
	 * 显示筛选对话框的dialog
	 * 
	 * @param is
	 */
	public void onEventMainThread(IsShowRenSelectLayout is) {
		mNearby_search_btn = is.getTv();
		mNearby_search_btn.setText("筛选");
		mNearby_search_btn.setEnabled(false);
		mNearby_search_btn.setClickable(false);
		mLl_select_condition.setVisibility(View.VISIBLE);
	}

	/**
	 * 在dialog筛选 框中获得到数据后，在此方法中获取用户选择的数值
	 * 
	 * @param gdd
	 */
	public void onEventMainThread(GetDialogData gdd) {
		recoverDefouldState();
		int dialogLeftValue = gdd.getLeft_value();
		int dialogRightValue = gdd.getRight_value();
		switch (mCurrent_tag) {
		case TAG_SEX: // 性别
			setSEXState(dialogLeftValue);
			break;
		case TAG_AGE: // 年龄
			setAgeState(dialogLeftValue, dialogRightValue);
			break;
		case TAG_FAR: // 距离
			setFarState(dialogLeftValue, dialogRightValue);
			break;
		case TAG_CITY:
			setCityState(gdd.getCity());
		}
	}

	private void setCityState(String city) {
		tv_condition_city.setText(city);
	}

	/**
	 * 设置用户选择的距离数据
	 * 
	 * @param dialogLeftValue
	 * @param dialogRightValue
	 */
	private void setFarState(int dialogLeftValue, int dialogRightValue) {
		if (++dialogLeftValue == dialogRightValue) {
			tv_condition_far2.setText(--dialogLeftValue + "-"
					+ dialogRightValue + "KM");
		} else {
			tv_condition_far2.setText(dialogLeftValue + "-" + dialogRightValue
					+ "KM");
		}
	}

	/**
	 * 设置用户选择的年龄数据
	 * 
	 * @param dialogLeftValue
	 * @param dialogRightValue
	 */
	private void setAgeState(int dialogLeftValue, int dialogRightValue) {
		// if (dialogLeftValue + 18 < 18 || dialogRightValue < 18) {
		// tv_condition_age2.setText("18-19岁");
		// } else if (++dialogLeftValue == dialogRightValue) {
		// tv_condition_age2.setText(--dialogLeftValue + "-"
		// + dialogRightValue + "岁");
		// } else {
		// tv_condition_age2.setText(dialogLeftValue + "-" + dialogRightValue
		// + "岁");
		// }
		tv_condition_age2.setText(dialogLeftValue + 18 + "-" + dialogRightValue
				+ "岁");
	}

	/**
	 * 设置用户选择的性别数据
	 * 
	 * @param dialogLeftValue
	 */
	private void setSEXState(int dialogLeftValue) {
		tv_selectcondition_sex2.setText(ChangeUtils.ChangeNumberToSex(String
				.valueOf(dialogLeftValue)));
	}

	/**
	 * 加载更多操作
	 */
	@Override
	public void onLoadMore() {
		initNet();

	}

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
		if (mNearby_search_btn != null) {
			mNearby_search_btn.setClickable(true);
			mNearby_search_btn.setEnabled(true);
		}
	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

}
