package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.ListContBean;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.FlowLayout;
import com.umeng.analytics.MobclickAgent;

/**
 * 爱好
 * 
 * @author ych
 * 
 */
public class HobbyActivity extends BaseActivity implements OnClickListener {
	private HttpHelp httpHelp;
	private TextView tv_title_title;
	private TextView tv_title_right;

	private FlowLayout fl_hobby_all;
	private FlowLayout fl_hobby_choice;
	// private String[] hobbyAll;

	private List<String> hobbyAll;

	private Intent hobbyIntent;
	private List<String> hobbyList;
	private LayoutInflater mInflater;
	private static int position;

	private TextView tv_hobby_addlabel;
	private EditText et_hobby_add;

	private TextView tv_hobby_moretags;// 换一换
	private boolean moreFinish = false;// 判断换一换是否更新完了

	@Override
	protected View getRootView() {
		View view = View.inflate(HobbyActivity.this, R.layout.hobby, null);
		httpHelp = new HttpHelp();
		hobbyIntent = getIntent();
		hobbyList = new ArrayList<String>();
		String hobby = (String) hobbyIntent.getExtras().get("editdata");
		String[] hobbyArry;
		if ("".equals(hobby)) {
			hobbyArry = new String[0];
		} else {
			hobbyArry = hobby.split("/");
			for (int i = 0; i < hobbyArry.length; i++) {
				hobbyList.add(hobbyArry[i]);
			}
		}
		hobbyAll = new ArrayList<String>();
		return view;
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
		tv_title_title = (TextView) findViewById(R.id.tv_title_title);
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("保存");

		fl_hobby_all = (FlowLayout) findViewById(R.id.fl_hobby_all);
		fl_hobby_choice = (FlowLayout) findViewById(R.id.fl_hobby_choice);
		tv_hobby_addlabel = (TextView) findViewById(R.id.tv_hobby_addlabel);
		et_hobby_add = (EditText) findViewById(R.id.et_hobby_add);
		mInflater = LayoutInflater.from(this);
		tv_hobby_moretags = (TextView) findViewById(R.id.tv_hobby_moretags);

		if (hobbyIntent.getStringExtra("talkdata") != null) {
			url = NetworkConfig.getChatGroupTagsr(5, hobbyPage);
			tv_title_title.setText("话题标签");
		} else {
			url = NetworkConfig.getUserTags(5, hobbyPage);
			tv_title_title.setText("爱好");
		}
	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
		tv_hobby_addlabel.setOnClickListener(this);
		tv_hobby_moretags.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		updata();
		netWork();
	}

	private int hobbyPage = 1;
	private String url;

	private void netWork() {
		hobbyAll.clear();
		httpHelp.sendGet(url, ListContBean.class,
				new MyRequestCallBack<ListContBean>() {

					@Override
					public void onSucceed(ListContBean bean) {
						if (bean == null) {
							moreFinish = true;
							return;
						}
						if ("1".equals(bean.status)) {
							hobbyAll.addAll(bean.cont);
							hobbyPage++;
							setFlowData();
						}
						moreFinish = true;
					}
				});
	}

	private void setFlowData() {
		fl_hobby_all.removeAllViews();
		for (int i = 0; i < hobbyAll.size(); i++) {
			final int t = i;
			tv = (TextView) mInflater.inflate(R.layout.fl_text, fl_hobby_all,
					false);
			tv.setTag(i);
			tv.setText(hobbyAll.get(i));
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Choice(t);
				}

			});
			fl_hobby_all.addView(tv);

		}
	}

	TextView tv;
	private FrameLayout view;

	private void updata() {
		fl_hobby_choice.removeAllViews();
		for (int t = 0; t < hobbyList.size(); t++) {
			view = (FrameLayout) mInflater.inflate(R.layout.add_tag_layout,
					fl_hobby_choice, false);
			// tv = (TextView) mInflater.inflate(R.layout.hobby_item,
			// fl_hobby_choice, false);
			tv = (TextView) view.findViewById(R.id.tv_retrans_dt_tag);
			tv.setText(hobbyList.get(t));
			tv.setTag(t);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// deleteChoice(pos);
					int tag = (int) v.getTag();
					hobbyList.remove(tag);
					updata();
				}

			});
			fl_hobby_choice.addView(view);
		}
	}

	private void delectHobbyArry(int i) {
		hobbyList.remove(i);
	}

	private void Choice(int i) {
		position = hobbyList.size();
		if (!isChoose(hobbyAll.get(i))) {
			hobbyList.add(hobbyAll.get(i));
			updata();
		}
	}

	private boolean isChoose(String tags) {
		for (int t = 0; t < hobbyList.size(); t++) {
			if (tags.equals(hobbyList.get(t))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			if(hobbyList==null || (hobbyList !=null && hobbyList.size()<=0)){
				UIUtils.showToast(getApplicationContext(), "请添加内容");
				return;
			}
				String hobby = "";
				for (String s : hobbyList) {
					hobby += s + "/";
				}
				hobby = hobby.substring(0, hobby.length() - 1);
				hobbyIntent.putExtra("editdata", hobby);
				setResult(8, hobbyIntent);
			
			finish();
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.tv_hobby_addlabel:
			String label = et_hobby_add.getText().toString().trim();
			if ("".equals(label)) {
				UIUtils.showToast(UIUtils.getContext(), "您还没有输入内容");
			} else if (isChoose(label)) {
				UIUtils.showToast(UIUtils.getContext(), "已有该爱好");
			} else {
				hobbyList.add(label);
				updata();
				et_hobby_add.setText("");
			}
			break;
		case R.id.tv_hobby_moretags:
			if (moreFinish) {
				moreFinish = false;
				netWork();
			}
			break;
		default:
			break;
		}
	}

}
