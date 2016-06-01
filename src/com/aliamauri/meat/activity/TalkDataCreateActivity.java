package com.aliamauri.meat.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.TalkDataBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.HorizontalListView;
import com.aliamauri.meat.view.MySwitch;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

/**
 * 话题资料
 * 
 * @author ych
 * 
 */
public class TalkDataCreateActivity extends BaseActivity implements
		OnClickListener {
	private HttpHelp httpHelp;
	private TalkDataBean tdBean;
	private TextView tv_title_right;

	private HorizontalListView hl_tdc_pic;
	private TextView tv_talkdata_btn;
	private Intent dataIntent;
	private HorizontalListViewAdapter hlvAdapter;

	private ImageView iv_tdc_talkmember1;
	private ImageView iv_tdc_talkmember2;
	// 话题资料
	private CircleImageView ci_tdc_headicon;
	private TextView tv_tdc_number;
	private MySwitch ms_tdc_message;
	private GridView gv_tdc_tags;

	private final static String EDITDATA = "editdata";
	private final static String TALKDATA = "talkdata";

	// 修改话题头像
	private RelativeLayout rl_tdc_headicon;
	private TextView tv_tdc_headicon;
	private final static int HEADICON = 1;
	// 修改话题名字
	private RelativeLayout rl_tdc_nickname;
	private TextView tv_tdc_nickname;
	private final static int NICKNAME = 2;
	// 修改话题介绍
	private LinearLayout ll_tdc_introduction;
	private TextView tv_tdc_introduction;
	private final static int INTRODUCTION = 5;
	// 修改话题标签
	private ImageView iv_tdc_tags;
	private final static int TAGS = 8;

	private GridViewAdapter GVAdapter;

	private boolean groupPic = false;// 判断是否修改了群头像
	private boolean groupData = false;// 判断是否修改了群资料

	@Override
	protected View getRootView() {
		View view = View.inflate(TalkDataCreateActivity.this,
				R.layout.talk_data_create, null);
		httpHelp = new HttpHelp();
		dataIntent = getIntent();
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
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "话题名字";
	}

	@Override
	protected void initView() {
		hl_tdc_pic = (HorizontalListView) findViewById(R.id.hl_tdc_pic);
		tv_talkdata_btn = (TextView) findViewById(R.id.tv_talkdata_btn);
		iv_tdc_talkmember1 = (ImageView) findViewById(R.id.iv_tdc_talkmember1);
		iv_tdc_talkmember2 = (ImageView) findViewById(R.id.iv_tdc_talkmember2);
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("保存");
		rl_tdc_nickname = (RelativeLayout) findViewById(R.id.rl_tdc_nickname);
		tv_tdc_nickname = (TextView) findViewById(R.id.tv_tdc_nickname);
		ll_tdc_introduction = (LinearLayout) findViewById(R.id.ll_tdc_introduction);
		tv_tdc_introduction = (TextView) findViewById(R.id.tv_tdc_introduction);
		ci_tdc_headicon = (CircleImageView) findViewById(R.id.ci_tdc_headicon);
		ms_tdc_message = (MySwitch) findViewById(R.id.ms_tdc_message);
		tv_tdc_number = (TextView) findViewById(R.id.tv_tdc_number);
		gv_tdc_tags = (GridView) findViewById(R.id.gv_tdc_tags);
		rl_tdc_headicon = (RelativeLayout) findViewById(R.id.rl_tdc_headicon);
		tv_tdc_headicon = (TextView) findViewById(R.id.tv_tdc_headicon);
		iv_tdc_tags = (ImageView) findViewById(R.id.iv_tdc_tags);

	}

	@Override
	protected void setListener() {
		tv_talkdata_btn.setOnClickListener(this);
		tv_title_right.setOnClickListener(this);
		iv_tdc_talkmember1.setOnClickListener(this);
		iv_tdc_talkmember2.setOnClickListener(this);
		rl_tdc_nickname.setOnClickListener(this);
		ll_tdc_introduction.setOnClickListener(this);
		rl_tdc_headicon.setOnClickListener(this);
		iv_tdc_tags.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		netWork();
	}

	private void netWork() {
		if (dataIntent.getStringExtra("talkId") != null) {
			httpHelp.sendGet(NetworkConfig.getUserGroupInfo(dataIntent
					.getStringExtra("talkId")), TalkDataBean.class,
					new MyRequestCallBack<TalkDataBean>() {

						@Override
						public void onSucceed(TalkDataBean bean) {
							if (bean == null)
								return;
							tdBean = bean;
							initData();
							setLVData();
							setLVListener();

						}

					});
		}
	}

	private List<String> tagsList;

	private void initData() {
		httpHelp.showImage(ci_tdc_headicon, tdBean.cont.baseinfo.pic + "##");
		tv_tdc_nickname.setText(tdBean.cont.baseinfo.name);
		tv_tdc_number.setText(tdBean.cont.baseinfo.mnum + "/"
				+ tdBean.cont.baseinfo.mtotal);
		ms_tdc_message.setOpen(tdBean.cont.baseinfo.isopen);

		tv_tdc_introduction.setText(tdBean.cont.baseinfo.desc);
		tagsList = new ArrayList<String>();
		if (tdBean.cont.baseinfo.tags != null) {
			String[] tagsArray = tdBean.cont.baseinfo.tags.trim().split(
					"\\|\\|\\|");
			for (String s : tagsArray) {
				if (!TextUtils.isEmpty(s)) {
					tagsList.add(s);
				}
			}
		}
		GVAdapter = new GridViewAdapter();
		gv_tdc_tags.setAdapter(GVAdapter);
		if ("1".equals(tdBean.cont.userjoin)) {
			tv_talkdata_btn.setText("退出话题");
		} else {
			tv_talkdata_btn.setText("加入话题");
		}
	}

	private void setLVData() {
		hlvAdapter = new HorizontalListViewAdapter();
		hl_tdc_pic.setAdapter(hlvAdapter);
	}

	private void setLVListener() {
		hl_tdc_pic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(TalkDataCreateActivity.this,
						TalkMemberActivity.class);
				i.putExtra("talkType", "create");
				startActivity(i);
			}
		});
	}

	private CircleImageView ci_circlepic_icon;

	class HorizontalListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tdBean.cont.groupuserlist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
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
				convertView = View.inflate(TalkDataCreateActivity.this,
						R.layout.hlv_circlepic_item, null);
				ci_circlepic_icon = (CircleImageView) convertView
						.findViewById(R.id.ci_circlepic_icon);
				convertView.setTag(ci_circlepic_icon);
			} else {
				ci_circlepic_icon = (CircleImageView) convertView.getTag();
			}
			httpHelp.showImage(ci_circlepic_icon,
					tdBean.cont.groupuserlist.get(position).face + "##");
			return convertView;
		}

	}

	private TextView tv_tdcfi_tags;

	class GridViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tagsList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
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
				convertView = View.inflate(UIUtils.getContext(),
						R.layout.talk_data_create_flowitem, null);
				tv_tdcfi_tags = (TextView) convertView
						.findViewById(R.id.tv_tdcfi_tags);
				convertView.setTag(tv_tdcfi_tags);
			} else {
				tv_tdcfi_tags = (TextView) convertView.getTag();
			}

			tv_tdcfi_tags.setText(tagsList.get(position));
			return convertView;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (CheckUtils.getInstance().isIcon(
				GlobalConstant.HEAD_ICON_PATH_BACKUP)) {
			File f = new File(GlobalConstant.HEAD_ICON_PATH_BACKUP);
			f.delete();
		}

	}

	private File filePic;

	private void uploadGroupFace() {
		RequestParams params = new RequestParams();
		filePic = new File(GlobalConstant.HEAD_ICON_PATH_BACKUP);
		params.addBodyParameter("uploadedfile", filePic);
		params.addBodyParameter("groupid", dataIntent.getStringExtra("talkId"));
		httpHelp.sendPost(NetworkConfig.uploadGroupFace(), params,
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {
					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null)
							return;
						UIUtils.showToast(UIUtils.getContext(), bean.msg);
					}
				});

	}

	private void editChatGroupBaseInfo() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("groupname", tv_tdc_nickname.getText()
				.toString().trim());
		params.addBodyParameter("desc", tv_tdc_introduction.getText()
				.toString().trim());
		String tags = "";
		for (String tag : tagsList) {
			if (!TextUtils.isEmpty(tag)) {
				tags += tag + "|||";
			}
		}
		params.addBodyParameter("tags", tags);
		params.addBodyParameter("groupid", dataIntent.getStringExtra("talkId"));
		httpHelp.sendPost(NetworkConfig.editChatGroupBaseInfo(), params,
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {
					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null)
							return;
						if ("1".equals(bean.status)) {
							UIUtils.showToast(UIUtils.getContext(), "成功");

						}
						UIUtils.showToast(UIUtils.getContext(), bean.msg);
					}
				});
	}

	@Override
	public void onClick(View v) {
		Intent talkDataIntent;
		switch (v.getId()) {
		case R.id.tv_title_right:
			if (groupData || groupPic) {
				if (groupData) {
					editChatGroupBaseInfo();
				}
				if (groupPic) {
					uploadGroupFace();
				}
			} else {
				UIUtils.showToast(UIUtils.getContext(), "您还没有修改群资料");
			}
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.iv_tdc_talkmember1:
		case R.id.iv_tdc_talkmember2:
			talkDataIntent = new Intent(TalkDataCreateActivity.this,
					TalkMemberActivity.class);
			talkDataIntent.putExtra("talkType", "create");
			startActivity(talkDataIntent);
			break;
		case R.id.tv_talkdata_btn:
			break;
		case R.id.rl_tdc_headicon:
			talkDataIntent = new Intent(TalkDataCreateActivity.this,
					SelectPicPopupWindow.class);
			talkDataIntent.putExtra(TALKDATA, "1");
			startActivityForResult(talkDataIntent, HEADICON);
			break;
		case R.id.rl_tdc_nickname:
			talkDataIntent = new Intent(TalkDataCreateActivity.this,
					NickNameActivity.class);
			talkDataIntent.putExtra(EDITDATA, tv_tdc_nickname.getText()
					.toString().trim());
			talkDataIntent.putExtra(TALKDATA, "1");
			startActivityForResult(talkDataIntent, NICKNAME);
			break;
		case R.id.ll_tdc_introduction:
			talkDataIntent = new Intent(TalkDataCreateActivity.this,
					SignSytleActivity.class);
			talkDataIntent.putExtra(EDITDATA, tv_tdc_introduction.getText()
					.toString().trim());
			startActivityForResult(talkDataIntent, INTRODUCTION);
			break;
		case R.id.iv_tdc_tags:
			String tags = "";
			if (tagsList.size() > 0) {
				for (String s : tagsList) {
					tags += s + "/";
				}
			}
			talkDataIntent = new Intent(TalkDataCreateActivity.this,
					HobbyActivity.class);
			talkDataIntent.putExtra(TALKDATA, "1");
			talkDataIntent.putExtra(EDITDATA, tags.trim());
			startActivityForResult(talkDataIntent, TAGS);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == HEADICON) {
			if (CheckUtils.getInstance().isIcon(
					GlobalConstant.HEAD_ICON_PATH_BACKUP)) {
				groupPic = true;
				httpHelp.showImage(ci_tdc_headicon,
						GlobalConstant.HEAD_ICON_PATH_BACKUP + "##");
				if (data != null)
					tv_tdc_headicon.setText(data.getStringExtra(EDITDATA) + "");
			}
		}
		if (requestCode == NICKNAME) {
			if (data != null) {
				groupData = true;
				tv_tdc_nickname.setText(data.getExtras().get(EDITDATA) + "");
			}
		}
		if (requestCode == INTRODUCTION) {
			if (data != null) {
				groupData = true;
				tv_tdc_introduction
						.setText(data.getExtras().get(EDITDATA) + "");
			}
		}
		if (requestCode == TAGS) {
			if (data != null) {
				groupData = true;
				String[] tags = data.getStringExtra(EDITDATA).split("/");
				tagsList.clear();
				for (String s : tags) {
					tagsList.add(s);
				}
				GVAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
