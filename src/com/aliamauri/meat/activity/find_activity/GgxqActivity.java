package com.aliamauri.meat.activity.find_activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.OtherDataActivity;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.hljy.Ggxq_detailBean;
import com.aliamauri.meat.bean.hljy.Ggxq_detailBean.Cont.Replylist;
import com.aliamauri.meat.bean.hljy.Ggxq_detail_childBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.RefreshListView;
import com.aliamauri.meat.view.RefreshListView.OnRefreshListener;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

/**
 * 发现----公告详情界面
 * 
 * @author limaokeji-windosc
 * 
 */
public class GgxqActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnRefreshListener {
	private HttpHelp mHttpHelp;
	private int mLoadingPage;
	private TextView mTv_ggxq_time; // 发表时间
	private TextView mTv_add_friend_content; // 发表内 容
	private TextView mTv_ggxq_hy; // 回应数
	private RefreshListView mRlv_ggxq_show_friend_content; // 显示好友列表
	private FrameLayout mFl_loading;
	private CircleImageView mCiv_ggxq_head_icon;  //用户头像
	private TextView mTv_hljy_hot_affiche_username;  //用户昵称
	private TextView mTv_ggxq_add_friend;
	private EditText mEt_message_import; // 发表内容
	private String mTag;
	private String mId;
	private RelativeLayout mRl_message_layout; // 聊天布局
	private int width; // 屏幕的宽度
	private int height; // 屏幕的高度
	private String mUser_Id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_ggxq);
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mUser_Id = PrefUtils.getString(UIUtils.getContext(),GlobalConstant.USER_ID, "");
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		mTag = getIntent().getExtras().getString(GlobalConstant.GO_GGXQ_TAG);
		mId = getIntent().getExtras().getString(GlobalConstant.GO_GGXQ_ID);
		mHttpHelp = new HttpHelp();
		mLoadingPage = 2;

		initView();
		getCurrentLocation();
		initNet(true);
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
	
	
	private String mWd;   //伟度
	private String mJd;   //经度
	/**
	 * 获取相应的地理位置坐标
	 */
	private void getCurrentLocation() {
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION, "0&&0").split("&&");
		mWd = String.valueOf(location[0]);
		mJd = String.valueOf(location[location.length-1]);
	}

	@Override
	public void onLoadMore() {
		mHttpHelp.sendGet(
				mReplylist_url + "?ucode=" + NetworkConfig.getUcode()
						+ NetworkConfig.FMT + "&id=" + mId + "&page="
						+ String.valueOf(mLoadingPage),
				Ggxq_detail_childBean.class,
				new MyRequestCallBack<Ggxq_detail_childBean>() {

					@Override
					public void onSucceed(Ggxq_detail_childBean bean) {

						if (bean == null || bean.status == null
								|| bean.cont == null) {
							UIUtils.showToast(GgxqActivity.this, "没有找到数据~~");
							mRlv_ggxq_show_friend_content.onRefreashFinish();
							return;
						}

						switch (bean.status) {
						case "1":
							if (bean.cont.size() <= 0) {
								UIUtils.showToast(GgxqActivity.this,"没有更多数据了~~");
								mRlv_ggxq_show_friend_content.onRefreashFinish();
								return;
							}
							mReplylist.addAll(bean.cont);
							mAdapter.notifyDataSetChanged();
							mRlv_ggxq_show_friend_content.onRefreashFinish();
							mLoadingPage++;
							break;
						case "2":
							UIUtils.showToast(GgxqActivity.this,
									"你还没有登陆~~~");
							break;

						default:

							UIUtils.showToast(GgxqActivity.this, "没有找到数据~~");
							break;
						}

					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	private ArrayList<Replylist> mReplylist;
	private MyBaseAdapter mAdapter;
	private String mReplylist_url;
	
	/**

	 * 点击好友头像进入好友界面
	 * @param v 
	 * @param position 
	 */
	public void getFrinedPage(View v) {
		String tag = (String) v.getTag();
		if(!(mUser_Id.equals(tag))){  //当前是朋友发布的动态
			Intent intent = new Intent(this, OtherDataActivity.class);
			intent.putExtra(GlobalConstant.COMMENT_ADD_FRIEND, tag);
			startActivity(intent);
		}
	}
	
	private int mHy_num; //存储该条评论的回应数

	/**
	 * 初始化网络数据
	 * @param b 
	 */
	private void initNet(final boolean b) {
		String url = null;
		switch (mTag) {
		case GlobalConstant.RMGG_TAG: // 从热门公告传过来的数据
			url = NetworkConfig.get_rmgg_detail_url(mId);
			break;
		case GlobalConstant.SPYH_TAG: // 从速配约会传过来的数据
			url = NetworkConfig.get_spyh_detail_url(mId);
			break;
		}
		mHttpHelp.sendGet(url, Ggxq_detailBean.class,
				new MyRequestCallBack<Ggxq_detailBean>() {
					@Override
					public void onSucceed(Ggxq_detailBean bean) {
						if(mFl_loading.getVisibility() != View.GONE){
							mFl_loading.setVisibility(View.GONE);
						}
						if (bean == null || bean.status == null
								|| bean.cont == null || bean.url == null
								|| bean.url.replylist == null
								|| bean.cont.baseinfo == null) {
							UIUtils.showToast(GgxqActivity.this,
									"网络异常");
							return;
						}

						switch (bean.status) {
						case "1":
							if (b) {
								mReplylist_url = bean.url.replylist;
								mTv_ggxq_time.setText(bean.cont.baseinfo.time);
								mTv_add_friend_content.setText(bean.cont.baseinfo.name);
								mHy_num = Integer.valueOf(bean.cont.baseinfo.hy_num);
								mTv_ggxq_hy.setText("回应("+ mHy_num + ")");
								mTv_ggxq_time.setText(bean.cont.baseinfo.time);
								mTv_hljy_hot_affiche_username.setText(bean.cont.baseinfo.nickname);
								mHttpHelp.showImage(mCiv_ggxq_head_icon, bean.cont.baseinfo.face+"##");
								mCiv_ggxq_head_icon.setTag(bean.cont.baseinfo.uid);
								mCiv_ggxq_head_icon.setOnClickListener(GgxqActivity.this);
								if (bean.cont.replylist.size() <= 0) {
									UIUtils.showToast(GgxqActivity.this,"没有更多数据了~~");
									return;
								}
								mReplylist = bean.cont.replylist;
								
								mAdapter = new MyBaseAdapter();
								mRlv_ggxq_show_friend_content.setAdapter(mAdapter);
								mRlv_ggxq_show_friend_content.setOnItemClickListener(GgxqActivity.this);
								mRlv_ggxq_show_friend_content.setOnRefreshListener(GgxqActivity.this);
							}else{
								if (bean.cont.replylist.size() <= 0) {
									UIUtils.showToast(GgxqActivity.this,
											"没有更多数据了~~");
									return;
								}
								mReplylist = bean.cont.replylist;
								if(mAdapter != null ){
									mAdapter.notifyDataSetChanged();
									mRlv_ggxq_show_friend_content.smoothScrollToPosition(0);
								}else{
									mAdapter = new MyBaseAdapter();
									mRlv_ggxq_show_friend_content.setAdapter(mAdapter);
									mRlv_ggxq_show_friend_content.setOnRefreshListener(GgxqActivity.this);
								}
							}
							break;
						case "2":
							UIUtils.showToast(GgxqActivity.this,
									"你还没有登陆~~~");
							break;

						default:
							break;
						}
					}
				});
	}

	class MyBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mReplylist.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder_rmgg holder = null;
			if (convertView == null) {
				holder = new ViewHolder_rmgg();
				convertView = View.inflate(GgxqActivity.this,
						R.layout.ggxg_details_item_content, null);
				holder.tv_hljy_hot_affiche_username = $(convertView,
						R.id.tv_hljy_hot_affiche_username);
				holder.tv_hljy_hot_affiche_time = $(convertView,
						R.id.tv_hljy_hot_affiche_time);
				holder.tv_hljy_hot_affiche_content = $(convertView,
						R.id.tv_hljy_hot_affiche_content);
				holder.civ_hljy_hot_affiche_icon = $(convertView,
						R.id.civ_hljy_hot_affiche_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder_rmgg) convertView.getTag();
			}

			mHttpHelp.showImage(holder.civ_hljy_hot_affiche_icon,mReplylist.get(position).face + "##");
			holder.civ_hljy_hot_affiche_icon.setTag(mReplylist.get(position).uid);
			holder.civ_hljy_hot_affiche_icon.setOnClickListener(GgxqActivity.this);
			holder.tv_hljy_hot_affiche_content.setText(mReplylist.get(position).name);
			holder.tv_hljy_hot_affiche_time.setText(mReplylist.get(position).time);
			holder.tv_hljy_hot_affiche_username.setText(mReplylist.get(position).nickname);

			return convertView;
		}

	}

	class ViewHolder_rmgg {

		public CircleImageView civ_hljy_hot_affiche_icon;
		public TextView tv_hljy_hot_affiche_content;
		public TextView tv_hljy_hot_affiche_time;
		public TextView tv_hljy_hot_affiche_username;

	}

	private void initView() {
		$(R.id.tv_btn_ok).setOnClickListener(this);
		((TextView) $(R.id.tv_title_title)).setText("公告详情");
		$(R.id.ll_title_talk).setVisibility(View.GONE);
		$(R.id.tv_title_right).setVisibility(View.GONE);
		$(R.id.iv_title_backicon).setOnClickListener(this);

		mTv_ggxq_time = $(R.id.tv_ggxq_time);
		mTv_ggxq_time = $(R.id.tv_ggxq_time);
		mFl_loading = $(R.id.fl_loading);
		mTv_add_friend_content = $(R.id.tv_add_friend_content);
		mTv_ggxq_hy = $(R.id.tv_ggxq_hy);
		
//		mTv_ggxq_add_friend = $(R.id.tv_ggxq_add_friend);
//		mTv_ggxq_add_friend.setOnClickListener(this);
		mRlv_ggxq_show_friend_content = $(R.id.rlv_ggxq_show_friend_content);
		mEt_message_import = $(R.id.et_message_import);
		mRl_message_layout = $(R.id.rl_message_layout);
		mCiv_ggxq_head_icon = $(R.id.civ_ggxq_head_icon);
		mTv_hljy_hot_affiche_username = $(R.id.tv_hljy_hot_affiche_username);
		$(R.id.tv_btn_ok).setOnClickListener(this);
		mEt_message_import.addTextChangedListener(new MyTextWatcher());

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
		public void afterTextChanged(Editable s) {

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_backicon: // 返回按钮
			KeyBoardUtils.closeKeybord(mEt_message_import,getApplicationContext());
			Intent data = new Intent();
			data.putExtra(GlobalConstant.DATA_TAG, mHy_num);
			setResult(20, data);
			finish();
			break;
//		case R.id.tv_ggxq_add_friend: // 添加好友按钮
//			break;
		case R.id.tv_btn_ok: // 发表按钮
			getImportText();
			break;
		case R.id.civ_hljy_hot_affiche_icon: //评论列表好友头像点击后进入好友界面
			getFrinedPage(v);
			break;
		case R.id.civ_ggxq_head_icon: //该条评论的头像
			getFrinedPage(v);
			break;

		default:
			break;
		}

	}

	/**
	 * 获取输入框的文字信息内容
	 */
	private void getImportText() {

		isSeted = true;
		String user_text = mEt_message_import.getText().toString().trim();
		if (StringUtils.isEmpty(user_text)) {
			UIUtils.showToast(getApplicationContext(), "输入的内容不能为空~~");
			return;
		}
		if (user_text.length() > 30) {
			UIUtils.showToast(getApplicationContext(), "输入的内容不能超过30个字~~");
			return;
		}

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, height * 98 / 1280);
		params.gravity = Gravity.BOTTOM;
		mRl_message_layout.setLayoutParams(params);
		KeyBoardUtils.closeKeybord(mEt_message_import, getApplicationContext());

		publishMessage(user_text);
	}

	/**
	 * 发布消息功能
	 * 
	 * @param user_text
	 */
	private void publishMessage(final String user_text) {
		String url = null;
		RequestParams params = new RequestParams();
		params.addBodyParameter("name", user_text);
		
		params.addBodyParameter("id", mId);
		params.addBodyParameter("jd", mJd);
		params.addBodyParameter("wd", mWd);
		switch (mTag) {
		case GlobalConstant.RMGG_TAG://对热门公告页面的动态详情的数据回复
			url = NetworkConfig.getGgxq_rmgg_url();
			break;
		case GlobalConstant.SPYH_TAG://对速配约会页面的动态详情的数据回复
			url = NetworkConfig.getGgxq_spyh_url();
			break;

		default:
			break;
		}
		mHttpHelp.sendPost(url, params, BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

			@Override
			public void onSucceed(BaseBaen bean) {
				if(bean == null || bean.status == null){
					return ;
				}
				switch (bean.status) {
				case  "1":
					++mHy_num;
					UIUtils.showToast(getApplicationContext(), "信息发布成功啦~~");
					mEt_message_import.setText("");
					mTv_ggxq_hy.setText("回应("+ mHy_num + ")");
					initNet(false);//发表成功请求网络刷新数据
					break;
				case  "2":
					UIUtils.showToast(getApplicationContext(), "呀~你还没有登陆~~");
					break;

				default:
					UIUtils.showToast(getApplicationContext(), "信息发布失败~~");
					break;
				}
				
			}
		});
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
