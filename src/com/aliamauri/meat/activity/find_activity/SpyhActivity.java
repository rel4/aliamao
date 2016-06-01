package com.aliamauri.meat.activity.find_activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.OtherDataActivity;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.hljy.RmggBean.Cont.Gonggao;
import com.aliamauri.meat.bean.hljy.Rmgg_childBean;
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
 * 婚恋交友---速配约会
 * @author limaokeji-windosc
 *
 */
public class SpyhActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnRefreshListener {

	private EditText mEt_spyh_impot_content; // 输入的内容
	private RefreshListView mLv_show_item_content; // 展示周围用户发布的公告
	private HttpHelp mHttpHelp;
	
	protected FrameLayout mFl_loading; // 经度
	
	private String mUser_Id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_spyh);
		mUser_Id = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_ID, "");
		mHttpHelp = new HttpHelp();
		mLoadPage = 1;
		initView();
		initNetwork(true);
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
	private int mCurrentPosition;  //当前点击的位置
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mCurrentPosition = position;
		Intent intent = new Intent(this, GgxqActivity.class);
		intent.putExtra(GlobalConstant.GO_GGXQ_TAG, GlobalConstant.SPYH_TAG);
		intent.putExtra(GlobalConstant.GO_GGXQ_ID, mCont.get(position).id);
		startActivityForResult(intent, 20);

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
			int hy_num= data.getIntExtra(GlobalConstant.DATA_TAG, -1);
			if(hy_num != -1 && mAdapter != null && hy_num != Integer.valueOf(mCont.get(mCurrentPosition).hy_num)){
				mCont.get(mCurrentPosition).hy_num = String.valueOf(hy_num);
				mAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onLoadMore() {
		initNetwork(false);
	}

	private int mLoadPage; // 当前页数
	private ArrayList<Gonggao> mCont; // 公告展示
	private MyBaseAdapter mAdapter;

	/**
	 * 设置网络请求操作
	 * 
	 * @param b
	 */
	private void initNetwork(final boolean b) {
		mHttpHelp.sendGet(NetworkConfig.getSPYH_item_url(mLoadPage),
				Rmgg_childBean.class, new MyRequestCallBack<Rmgg_childBean>() {

					@Override
					public void onSucceed(Rmgg_childBean bean) {
						if(mFl_loading.getVisibility() != View.GONE){
							mFl_loading.setVisibility(View.GONE);
						}
						if (bean == null || bean.status == null
								|| bean.cont == null) {
							UIUtils.showToast(SpyhActivity.this,
									"网络异常~~");
							return;
						}
						switch (bean.status) {
						case "1":

							if (b) {
								if (bean.cont.size() <= 0) {
									UIUtils.showToast(SpyhActivity.this,
											"没有更多数据了~~");
									return;
								}
								mCont = bean.cont;
								mAdapter = new MyBaseAdapter();
								mLv_show_item_content.setAdapter(mAdapter);
								mLv_show_item_content
										.setOnItemClickListener(SpyhActivity.this);
								mLv_show_item_content
										.setOnRefreshListener(SpyhActivity.this);

								mLoadPage++;

							} else {
								if (bean.cont.size() <= 0) {
									UIUtils.showToast(SpyhActivity.this,
											"没有更多数据了~~");
									mLv_show_item_content.onRefreashFinish();
									return;
								}
								mCont.addAll(bean.cont);
								mAdapter.notifyDataSetChanged();
								mLv_show_item_content.onRefreashFinish();
								mLoadPage++;
							}

							break;
						case "2":
							UIUtils.showToast(SpyhActivity.this,
									"你还没有登陆~~~");
							break;

						default:
							break;
						}

					}
				});
	}

	/**
	 * 初始化布局
	 */
	private void initView() {
		$(R.id.iv_title_backicon).setOnClickListener(this);
		((TextView) $(R.id.tv_title_title)).setText("速配约会");
		mEt_spyh_impot_content = $(R.id.et_spyh_impot_content);
		mFl_loading = $(R.id.fl_loading);
		$(R.id.btn_spyh_ok).setOnClickListener(this);
		mLv_show_item_content = $(R.id.lv_show_item_content);
	}

	class MyBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCont.size();
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
				convertView = View.inflate(SpyhActivity.this,
						R.layout.item_hot_affiche, null);
				holder.tv_hljy_hot_affiche_username = $(convertView,
						R.id.tv_hljy_hot_affiche_username);
				holder.tv_hljy_hot_affiche_time = $(convertView,
						R.id.tv_hljy_hot_affiche_time);
				holder.tv_hljy_hot_affiche_echo = $(convertView,
						R.id.tv_hljy_hot_affiche_echo);
				holder.tv_hljy_hot_affiche_content = $(convertView,
						R.id.tv_hljy_hot_affiche_content);
				holder.civ_hljy_hot_affiche_icon = $(convertView, 
						R.id.civ_hljy_hot_affiche_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder_rmgg) convertView.getTag();
			}

			mHttpHelp.showImage(holder.civ_hljy_hot_affiche_icon,
					mCont.get(position).face + "##");
			holder.civ_hljy_hot_affiche_icon.setTag(mCont.get(position).uid);
			holder.civ_hljy_hot_affiche_icon.setOnClickListener(SpyhActivity.this);
			holder.tv_hljy_hot_affiche_content
					.setText(mCont.get(position).name);
			holder.tv_hljy_hot_affiche_time.setText(mCont.get(position).time);
			holder.tv_hljy_hot_affiche_username
					.setText(mCont.get(position).nickname);
			holder.tv_hljy_hot_affiche_echo.setText("回应("
					+ mCont.get(position).hy_num + ")");
			return convertView;
		}

	}

	class ViewHolder_rmgg {

		public CircleImageView civ_hljy_hot_affiche_icon;
		public TextView tv_hljy_hot_affiche_content;
		public TextView tv_hljy_hot_affiche_echo;
		public TextView tv_hljy_hot_affiche_time;
		public TextView tv_hljy_hot_affiche_username;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_backicon:
			finish();
			KeyBoardUtils.closeKeybord(mEt_spyh_impot_content,
					getApplicationContext());
			break;
		case R.id.btn_spyh_ok:
			getImportText();
			break;
		case R.id.civ_hljy_hot_affiche_icon:
			getFrinedPage(v);
			break;

		default:
			break;
		}

	}
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

	/**
	 * 获取输入框的文字信息内容
	 */
	private void getImportText() {
		String user_text = mEt_spyh_impot_content.getText().toString().trim();
		if (StringUtils.isEmpty(user_text)) {
			UIUtils.showToast(getApplicationContext(), "输入的内容不能为空~~");
			return;
		}
		if (user_text.length() > 30) {
			UIUtils.showToast(getApplicationContext(), "输入的内容不能超过30个字~~");
			return;
		}

		KeyBoardUtils.closeKeybord(mEt_spyh_impot_content,
				getApplicationContext());

		publishMessage(user_text);
	}

	/**
	 * 发布消息功能
	 * 
	 * @param user_text
	 */
	private void publishMessage(final String user_text) {
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION, "0&&0").split("&&");
		RequestParams params = new RequestParams();
		params.addBodyParameter("name", user_text);
		params.addBodyParameter("jd",  String.valueOf(location[location.length-1]));
		params.addBodyParameter("wd", String.valueOf(location[0]));
		mHttpHelp.sendPost(NetworkConfig.get_add_spyh_url(), params,
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || bean.status == null) {
							return;
						}
						switch (bean.status) {
						case "1":
							UIUtils.showToast(getApplicationContext(),
									"信息发布成功啦~~");
							mEt_spyh_impot_content.setText("");
							mLoadPage = 1;
							mHttpHelp.sendGet(NetworkConfig.getSPYH_item_url(mLoadPage),
									Rmgg_childBean.class,
									new MyRequestCallBack<Rmgg_childBean>() {

										@Override
										public void onSucceed(	Rmgg_childBean bean) {
											if (bean == null|| bean.status == null|| bean.cont == null) {
												UIUtils.showToast(SpyhActivity.this,	"没有找到数据~~");
												return;
											}
											switch (bean.status) {
											case "1":

												if (bean.cont.size() <= 0) {
													UIUtils.showToast(SpyhActivity.this,
															"没有更多数据了~~");
													return;
												}
												mCont = bean.cont;
												if(mAdapter != null){
													mAdapter.notifyDataSetChanged();
													mLv_show_item_content.smoothScrollToPosition(0);
													mLoadPage++;
												}else{
												mAdapter = new MyBaseAdapter();
												mLv_show_item_content.setAdapter(mAdapter);
												mLv_show_item_content
														.setOnItemClickListener(SpyhActivity.this);
												mLv_show_item_content
														.setOnRefreshListener(SpyhActivity.this);
												mLoadPage++;
												}
												
												break;
											}
										}
									});
							break;
						case "2":
							UIUtils.showToast(getApplicationContext(),
									"呀~你还没有登陆~~");
							break;

						default:
							UIUtils.showToast(SpyhActivity.this,
									"发布失败~~~");
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
