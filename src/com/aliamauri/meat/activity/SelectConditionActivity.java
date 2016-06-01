package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.bean.Cont;
import com.aliamauri.meat.bean.FriendBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.ChangeUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.RefreshListView;
import com.aliamauri.meat.view.RefreshListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 条件搜索
 * 
 * @author ych
 * 
 */
public class SelectConditionActivity extends BaseActivity implements
		OnClickListener {
	private HttpHelp httpHelp;
	private List<Cont> cont;

	private TextView tv_title_right;
	private final int SEX = 1;
	private final int AGE = 2;
	private final int FAR = 3;
	private final String POPUPTYPE = "popuptype";

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

	private boolean check[];
	private RefreshListView lv_selectcondition_all;
	private ConditionAdapter conditionAdapter;

	private TextView tv_selectcondition_search;// 查找按钮

	private LinearLayout ll_select_condition;
	private TextView tv_selectcondition_result;
	private boolean clickRight = false;

	private String recommendUrl;
	private int page = 1;

	@Override
	protected View getRootView() {
		View view = View.inflate(SelectConditionActivity.this,
				R.layout.select_condition, null);
		httpHelp = new HttpHelp();
		cont = new ArrayList<Cont>();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "条件查找";
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
	
	@Override
	protected void initView() {
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("筛选");

		lv_selectcondition_all = (RefreshListView) findViewById(R.id.lv_selectcondition_all);
		conditionAdapter = new ConditionAdapter();
		lv_selectcondition_all.setAdapter(conditionAdapter);

		rl_selectcondition_sex = (RelativeLayout) findViewById(R.id.rl_selectcondition_sex);
		iv_selectcondition_sex = (ImageView) findViewById(R.id.iv_selectcondition_sex);
		tv_selectcondition_sex1 = (TextView) findViewById(R.id.tv_selectcondition_sex1);
		tv_selectcondition_sex2 = (TextView) findViewById(R.id.tv_selectcondition_sex2);
		tv_selectcondition_sex2.setText(PrefUtils.getString(
				GlobalConstant.CONDITION_SEX, "不限"));

		rl_condition_age = (RelativeLayout) findViewById(R.id.rl_condition_age);
		tv_condition_age1 = (TextView) findViewById(R.id.tv_condition_age1);
		tv_condition_age2 = (TextView) findViewById(R.id.tv_condition_age2);
		iv_condition_age = (ImageView) findViewById(R.id.iv_condition_age);
		tv_condition_age2.setText(PrefUtils.getString(
				GlobalConstant.CONDITION_AGE, "1-100岁"));

		rl_condition_far = (RelativeLayout) findViewById(R.id.rl_condition_far);
		tv_condition_far1 = (TextView) findViewById(R.id.tv_condition_far1);
		tv_condition_far2 = (TextView) findViewById(R.id.tv_condition_far2);
		iv_condition_far = (ImageView) findViewById(R.id.iv_condition_far);
		tv_condition_far2.setText(PrefUtils.getString(
				GlobalConstant.CONDITION_FAR, "1-5KM"));

		tv_selectcondition_search = (TextView) findViewById(R.id.tv_selectcondition_search);
		ll_select_condition = (LinearLayout) findViewById(R.id.ll_select_condition);
		tv_selectcondition_result = (TextView) findViewById(R.id.tv_selectcondition_result);

	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
		rl_selectcondition_sex.setOnClickListener(this);
		rl_condition_age.setOnClickListener(this);
		rl_condition_far.setOnClickListener(this);
		tv_selectcondition_search.setOnClickListener(this);
		lv_selectcondition_all.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onLoadMore() {
				searchNetWork();
			}
		});
	}

	@Override
	protected void initNet() {
		String sexData = tv_condition_age2.getText().toString().trim();
		sexData = sexData.substring(0, sexData.length() - 1);
		strIndex = sexData.split("-");
		select = true;
		searchNetWork();
		super.initNet();
	}

	class ConditionAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cont.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(SelectConditionActivity.this,
						R.layout.select_condition_item, null);
				viewHolder = new ViewHolder();
				viewHolder.ci_sci_icon = (CircleImageView) convertView
						.findViewById(R.id.ci_sci_icon);
				viewHolder.tv_sci_authenticate = (TextView) convertView
						.findViewById(R.id.tv_sci_authenticate);

				viewHolder.tv_sci_name = (TextView) convertView
						.findViewById(R.id.tv_sci_name);
				viewHolder.tv_sci_sex = (TextView) convertView
						.findViewById(R.id.tv_sci_sex);
				viewHolder.tv_sci_introduction = (TextView) convertView
						.findViewById(R.id.tv_sci_introduction);
				viewHolder.tv_sci_distance = (TextView) convertView
						.findViewById(R.id.tv_sci_distance);
				viewHolder.btn_ren_add = (Button) convertView
						.findViewById(R.id.btn_ren_add);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			httpHelp.showImage(viewHolder.ci_sci_icon, cont.get(position).face
					+ "##");
			viewHolder.tv_sci_authenticate.setText(cont.get(position).issmval);
			viewHolder.tv_sci_distance.setText(cont.get(position).distance);
			viewHolder.tv_sci_introduction
					.setText(cont.get(position).signature);
			viewHolder.tv_sci_name.setText(cont.get(position).nickname);
			if ("1".equals(cont.get(position).sex)) {
				viewHolder.tv_sci_sex.setSelected(false);
			} else if ("0".equals(cont.get(position).sex)) {
				viewHolder.tv_sci_sex.setSelected(true);
			}
			viewHolder.tv_sci_sex.setText(cont.get(position).age);
			viewHolder.btn_ren_add.setOnClickListener(new AddFriend(cont
					.get(position).id));
			return convertView;
		}
	}

	class ViewHolder {
		private CircleImageView ci_sci_icon;
		private TextView tv_sci_name;
		private TextView tv_sci_sex;
		private TextView tv_sci_authenticate;
		private TextView tv_sci_introduction;
		private TextView tv_sci_distance;
		private Button btn_ren_add;
	}

	class AddFriend implements OnClickListener {
		private String friednId;

		public AddFriend(String friednId) {
			this.friednId = friednId;
		}

		@Override
		public void onClick(View v) {
			CmdManager.getInstance().addContact(friednId, null,
					new CmdManagerCallBack() {

						@Override
						public void onState(boolean isSucceed) {
							if (isSucceed) {
								UIUtils.showToast(UIUtils.getContext(),
										"发送请求成功");
							} else {
								UIUtils.showToast(UIUtils.getContext(), "添加失败");
							}
						}
					});
		}
	}

	private String[] strIndex;
	private boolean select = false;// 判断是刷新的还是点了筛选按键的

	private void saveCondition() {
		PrefUtils.setString(GlobalConstant.CONDITION_SEX,
				tv_selectcondition_sex2.getText().toString().trim());
		PrefUtils.setString(GlobalConstant.CONDITION_AGE, tv_condition_age2
				.getText().toString().trim());
		PrefUtils.setString(GlobalConstant.CONDITION_FAR, tv_condition_far2
				.getText().toString().trim());
		page = 1;
		String sexData = tv_condition_age2.getText().toString().trim();
		sexData = sexData.substring(0, sexData.length() - 1);
		strIndex = sexData.split("-");
		select = true;
	}

	private void searchNetWork() {
		recommendUrl = NetworkConfig.getuserSearch(
				strIndex[0],
				strIndex[1],
				ChangeUtils.ChangeSexToNumber(tv_selectcondition_sex2.getText()
						.toString().trim()), "", page);
		httpHelp.sendGet(recommendUrl, FriendBean.class,
				new MyRequestCallBack<FriendBean>() {

					@Override
					public void onSucceed(FriendBean bean) {
						if (bean == null) {
							lv_selectcondition_all.onRefreashFinish();
							return;
						}
						if ("1".equals(bean.status)) {
							if (select) {
								select = false;
								cont.clear();
								conditionAdapter.notifyDataSetChanged();
							}
							if (bean.cont.size() == 0 || bean.cont == null) {
								UIUtils.showToast(UIUtils.getContext(), "没有更多了");
							} else {
								page++;
								cont.addAll(bean.cont);
								conditionAdapter.notifyDataSetChanged();
							}
						} else {
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
						}
						lv_selectcondition_all.onRefreashFinish();
					}
				});
	}

	@Override
	public void onClick(View v) {
		Intent condition;
		switch (v.getId()) {
		case R.id.tv_title_right:
			clickRight = !clickRight;
			if (clickRight) {
				tv_title_right.setText("收起");
				ll_select_condition.setVisibility(View.VISIBLE);
			} else {
				tv_title_right.setText("筛选");
				ll_select_condition.setVisibility(View.GONE);
			}
			break;
		case R.id.tv_selectcondition_search:
			tv_selectcondition_result.setVisibility(View.VISIBLE);
			saveCondition();
			searchNetWork();
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.rl_selectcondition_sex:
			tv_selectcondition_sex1.setSelected(true);
			tv_selectcondition_sex2.setSelected(true);
			iv_selectcondition_sex.setSelected(true);
			condition = new Intent(SelectConditionActivity.this,
					PopupwindowConditionActivity.class);
			condition.putExtra(POPUPTYPE, "sex");
			condition.putExtra("coditiondata", tv_selectcondition_sex2
					.getText().toString().trim());
			startActivityForResult(condition, SEX);
			// showPopUpWindow();
			break;
		case R.id.rl_condition_age:
			tv_condition_age1.setSelected(true);
			tv_condition_age2.setSelected(true);
			iv_condition_age.setSelected(true);
			condition = new Intent(SelectConditionActivity.this,
					PopupwindowConditionActivity.class);
			condition.putExtra(POPUPTYPE, "age");
			condition.putExtra("coditiondata", tv_condition_age2.getText()
					.toString().trim());
			startActivityForResult(condition, AGE);
			break;
		case R.id.rl_condition_far:
			tv_condition_far1.setSelected(true);
			tv_condition_far2.setSelected(true);
			iv_condition_far.setSelected(true);
			condition = new Intent(SelectConditionActivity.this,
					PopupwindowConditionActivity.class);
			condition.putExtra(POPUPTYPE, "far");
			condition.putExtra("coditiondata", tv_condition_far2.getText()
					.toString().trim());
			startActivityForResult(condition, FAR);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SEX) {
			if (data != null) {
				if (data.getExtras() != null) {
					tv_selectcondition_sex2.setText(ChangeUtils
							.ChangeNumberToSex(data.getExtras()
									.get("popCondition").toString().trim()));
				}
			}
			tv_selectcondition_sex1.setSelected(false);
			tv_selectcondition_sex2.setSelected(false);
			iv_selectcondition_sex.setSelected(false);
		} else if (requestCode == AGE) {
			if (data != null) {
				if (data.getExtras() != null) {
					if (data.getExtras().get("right") != null
							&& data.getExtras().get("left") != null) {
						String right = data.getExtras().get("right").toString()
								.trim();
						String left = data.getExtras().get("left").toString()
								.trim();
						tv_condition_age2.setText(left + "-" + right + "岁");
					}
				}
			}
			tv_condition_age1.setSelected(false);
			tv_condition_age2.setSelected(false);
			iv_condition_age.setSelected(false);
		} else if (requestCode == FAR) {
			if (data != null) {
				if (data.getExtras() != null) {
					if (data.getExtras().get("right") != null
							&& data.getExtras().get("left") != null) {
						String right = data.getExtras().get("right").toString()
								.trim();
						String left = data.getExtras().get("left").toString()
								.trim();
						tv_condition_far2.setText(left + "-" + right + "km");
					}
				}
			}
			tv_condition_far1.setSelected(false);
			tv_condition_far2.setSelected(false);
			iv_condition_far.setSelected(false);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
