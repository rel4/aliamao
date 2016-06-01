package com.aliamauri.meat.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.nearby_activity.ResourceSearch_page;
import com.aliamauri.meat.bean.ResourceLibraryBean;
import com.aliamauri.meat.bean.ResourceLibraryBean.Cont;
import com.aliamauri.meat.bean.ResourceLibraryTagBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.holder.ViewHolder_YS;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.ClickZanBtn_utils;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.FlowLayout_tag;
import com.aliamauri.meat.view.RefreshListView;
import com.aliamauri.meat.view.RefreshListView.OnRefreshListener;

/**
 * 资源库-基类
 * 
 * @author limaokeji-windosc
 * 
 */
public abstract class BaseFragment_grandson_ys extends Fragment implements
		OnItemClickListener, OnRefreshListener, OnClickListener {
	public Activity mActivity;
	private RefreshListView mLv; // 展示内容的listview控件
	private MyBaseAdapter adapter;

	private HttpHelp mHttp;
	private int mCurrentPage = 1; // 默认页为1
	private int mChildType; // 获取当前子类类型的type
	private ArrayList<String> mId_sum; // 记录所有的被点过赞的动态id
	private EditText mEt_search_content; // 编辑框
	private TextView mTv_btn_ok; // 确定按钮
	private LinearLayout mLl_tag_lists; // 展示标签布局
	private String mCurrentType;
	private FrameLayout mLoading_tag;
	private ProgressBar mLoading_ys;

	private int height;
	private int width;

	/**
	 * 获取子类的url
	 * 
	 * @return
	 */
	public abstract String getUrl(int page);

	/**
	 * 获取每个子类的类型
	 * 
	 * @return
	 */
	public abstract int getType();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mChildType = getType();
		mHttp = new HttpHelp();

		WindowManager systemService = (WindowManager) mActivity
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		systemService.getDefaultDisplay().getMetrics(metrics);
		height = metrics.heightPixels;
		width = metrics.widthPixels;
		mCurrentPage = 1; // 默认页为1
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null;
		int layout_ys = R.layout.fragment_grandson_zyk;
		int layout_tag = R.layout.fragment_grandson_ys_tag;

		switch (mChildType) {
		case GlobalConstant.TYPE_ZRYS:
		case GlobalConstant.TYPE_ZXYS:
			view = set_yingshi(inflater, container, layout_ys);
			break;
		case GlobalConstant.TYPE_TAG:
			view = set_tag(inflater, container, layout_tag);
			break;

		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mId_sum = new ArrayList<String>();
		mId_sum.clear();

		switch (mChildType) {
		case GlobalConstant.TYPE_ZRYS:
		case GlobalConstant.TYPE_ZXYS:
			initNet();
			break;
		case GlobalConstant.TYPE_TAG:
			initNet_tag();

			break;
		}

	}
	
	private ArrayList<String> mCont_tag; // 标签数据组
	/**
	 * 获取标签集合
	 */
	private void initNet_tag() {
		String url = getUrl(mCurrentPage);
		mHttp.sendGet(url, ResourceLibraryTagBean.class,
				new MyRequestCallBack<ResourceLibraryTagBean>() {

					@Override
					public void onSucceed(ResourceLibraryTagBean bean) {
						mLoading_tag.setVisibility(View.GONE);
						if (bean == null || bean.cont == null) {
							UIUtils.showToast(mActivity, "网络异常");
							return;
						}
						switch (bean.status) {
						case "1":
							if (bean.cont.size() <= 0) {
								UIUtils.showToast(mActivity,mActivity.getResources().getString(R.string.text_no_data));
								return;
							}
							if (mChildType == GlobalConstant.TYPE_TAG) {
								mCont_tag = bean.cont;
								getEditDate();
								setFlowLayout();

							}

							break;
						default:
							UIUtils.showToast(getActivity(), bean.msg);
							break;
						}
					}
				});

	}

	/**
	 * 设置标签布局
	 * 
	 * @return
	 */
	private View set_tag(LayoutInflater inflater, ViewGroup container,
			int layout) {
		View view = inflater.inflate(layout, container, false);
		mEt_search_content = $(view, R.id.et_search_content);
		mTv_btn_ok = $(view, R.id.tv_btn_ok);
		mTv_btn_ok.setOnClickListener(this);
		mLl_tag_lists = $(view, R.id.rl_tag_lists);
		mLoading_tag = $(view, R.id.fl_loading);
		return view;
	}

	/**
	 * 设置最新影视，最热影视布局
	 * 
	 * @return
	 */
	private View set_yingshi(LayoutInflater inflater, ViewGroup container,int layout) {
		View view = inflater.inflate(layout, container, false);
		mLv = $(view, R.id.lv_zyk_content);
		mLoading_ys = $(view, R.id.pb_progress);

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_btn_ok:
			getImportText();
			break;

		default:
			break;
		}

	}

	/**
	 * 设置编辑框的操作
	 */
	private void getEditDate() {
		mEt_search_content.addTextChangedListener(new MyTextWatcher());
	}

	/**
	 * 获取输入框的文字信息内容
	 */
	private void getImportText() {
		String user_text = mEt_search_content.getText().toString().trim();
		if (StringUtils.isEmpty(user_text)) {
			UIUtils.showToast(mActivity.getApplicationContext(),
					"输入的内容不能为空~~");
			return;
		}
		if (user_text.length() > 18) {
			UIUtils.showToast(mActivity.getApplicationContext(),
					"输入的内容不能超过16个字~~");
			return;
		}
		goSearchPage(user_text);
	}

	/**
	 * 监听输入的文字，输入完毕后自动消除键盘
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyTextWatcher implements TextWatcher {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			if (after != 0) {
				KeyBoardUtils.closeKeybord(mEt_search_content, mActivity);
				mTv_btn_ok.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}

	@Override
	public void onDestroyView() {
		if (mEt_search_content != null) {
			KeyBoardUtils.closeKeybord(mEt_search_content,
					mActivity.getApplicationContext());
		}
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		if (mEt_search_content != null) {
			mEt_search_content.setText("");
			mTv_btn_ok.setVisibility(View.GONE);
		}
		super.onPause();
	}

	/**
	 * 设置流性布局中的数据
	 */
	private void setFlowLayout() {
		FlowLayout_tag flowLayout = new FlowLayout_tag(UIUtils.getContext());

		for (int i = 0; i < mCont_tag.size(); i++) {
			final TextView textView = new TextView(UIUtils.getContext());
			textView.setTextColor(getResources().getColor(
					R.color.time_word_black));
			textView.setGravity(Gravity.CENTER);

			textView.setPadding(720 * 20 / width, 1280 * 20 / height,
					720 * 20 / width, 1280 * 20 / height);
			textView.setText(mCont_tag.get(i));
			textView.setBackgroundResource(R.drawable.search_catch_tv_bg);

			textView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					goSearchPage(textView.getText().toString().trim());
				}
			});

			flowLayout.addView(textView);
		}
		mLl_tag_lists.addView(flowLayout);
	}

	/**
	 * 进入搜索结果页面
	 * 
	 * @param str
	 */
	protected void goSearchPage(String str) {
		Intent intent = new Intent(mActivity.getApplicationContext(),ResourceSearch_page.class);
		intent.putExtra(GlobalConstant.RESOURCESEARCH_TAG, str);
		startActivity(intent);

	}

	@Override
	public void onDestroy() {
		// 取消当前的网络请求操作
		if (mHttp != null) {
			mHttp.stopHttpNET();
		}
		super.onDestroy();
	}

	/**
	 * 初始化网络请求
	 * 
	 * @param url
	 */
	private ArrayList<Cont> mCont; // 影视资料数据

	private void initNet() {
		if (mHttp != null) {
			mHttp.stopHttpNET();
		}
		String url = getUrl(mCurrentPage);
		mHttp.sendGet(url, ResourceLibraryBean.class,
				new MyRequestCallBack<ResourceLibraryBean>() {

					@Override
					public void onSucceed(ResourceLibraryBean bean) {
						mLoading_ys.setVisibility(View.GONE);
						if (bean == null || bean.cont == null || bean.status==null) {
							UIUtils.showToast(mActivity, "网络异常");
							return;
						}
						switch (bean.status) {
						case "1":
							if (bean.cont.size() <= 0) {
								UIUtils.showToast(
										mActivity,
										mActivity.getResources().getString(
												R.string.text_no_data));
								switch (mChildType) {
								case GlobalConstant.TYPE_ZRYS:
								case GlobalConstant.TYPE_ZXYS:
									mLv.onRefreashFinish();
									break;
								}

								return;
							}

							switch (mChildType) {
							case GlobalConstant.TYPE_ZRYS:
							case GlobalConstant.TYPE_ZXYS:
								if (mCurrentPage <= 1) {
									mCont = bean.cont;
									adapter = new MyBaseAdapter();
									mLv.setAdapter(adapter);
									mLv.setOnItemClickListener(BaseFragment_grandson_ys.this);
									mLv.setOnRefreshListener(BaseFragment_grandson_ys.this);

								} else {
									mCont.addAll(bean.cont);
									adapter.notifyDataSetChanged();
									mLv.onRefreashFinish();
								}
								mCurrentPage++;
								break;

							}

							break;

						default:
							UIUtils.showToast(getActivity(), bean.msg);
							break;
						}
					}
				});
	}

	/**
	 * 下拉加载更多
	 */
	@Override
	public void onLoadMore() {
		initNet();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder_YS holder_ys = null;

			if (convertView == null) {

				holder_ys = new ViewHolder_YS();
				convertView = View.inflate(mActivity, R.layout.my_video_item,
						null);
				holder_ys.ivc_voide_item_icon = $(convertView,R.id.ivc_voide_item_icon);
				holder_ys.tv_voide_item_time = $(convertView,R.id.tv_voide_item_time);
				holder_ys.tv_voide_item_video_title = $(convertView,R.id.tv_voide_item_video_title);
				holder_ys.tv_video_url = $(convertView, R.id.tv_video_url);
				holder_ys.tv_voide_item_ding = $(convertView,R.id.tv_voide_item_ding);
				holder_ys.tv_voide_item_cai = $(convertView,R.id.tv_voide_item_cai);
				holder_ys.iv_voide_item_ding = $(convertView,R.id.iv_voide_item_ding);
				holder_ys.iv_voide_item_cai = $(convertView,R.id.iv_voide_item_cai);
				holder_ys.tv_voide_item_username = $(convertView,R.id.tv_voide_item_username);
				holder_ys.ll_ding = $(convertView,R.id.ll_ding);
				holder_ys.ll_cai = $(convertView,R.id.ll_cai);

				convertView.setTag(holder_ys);

			} else {

				holder_ys = (ViewHolder_YS) convertView.getTag();
			}

			setItem_content(position, holder_ys, mCont);
			return convertView;
		}
	}

	
	/**
	 * 设置动态每个条目的点击事件
	 * 
	 * @param position
	 * @param tlist
	 * @param cont
	 * @param ys
	 * 
	 */
	public void setItem_content(int position, ViewHolder_YS ys,ArrayList<Cont> conts) {
		ys.tv_video_url.setText(conts.get(position).content);
		ys.tv_voide_item_cai.setText(conts.get(position).down);
		ys.tv_voide_item_ding.setText(conts.get(position).up);
		ys.tv_voide_item_time.setText(conts.get(position).createtime);
		ys.tv_voide_item_video_title.setText(conts.get(position).name);
		ys.tv_voide_item_username.setText(conts.get(position).nickname);
		mHttp.showImage(ys.ivc_voide_item_icon, conts.get(position).face);
		
		ClickZanBtn_utils btn_utils = new ClickZanBtn_utils(ys, conts, position);
		ys.ll_cai.setTag("2");//踩标记
		ys.ll_cai.setOnClickListener(btn_utils);
		ys.ll_ding.setTag("1");//顶标记
		ys.ll_ding.setOnClickListener(btn_utils);
	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

}
