package com.aliamauri.meat.activity.search_activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.adapter.AppBaseAdapter;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.SearchRseBean;
import com.aliamauri.meat.bean.SearchRseBean.Cont.Cfilm_area;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.play.PlayActivity;
import com.aliamauri.meat.play.PlaySourceActivity;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringNumUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

/**
 * 收索结果页
 * 
 * @author jjm
 * 
 */
public class SearchResultPageActivity extends Activity implements
		OnFocusChangeListener, OnRefreshListener<ListView>,
		OnItemClickListener, OnClickListener {

	private PullToRefreshListView search_list_view;
	private List<Cfilm_area> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result_page_main);
		if (getIntent().getStringExtra(GlobalConstant.APP_SEARCH_KEY) != null) {
			app_search_key = getIntent().getStringExtra(
					GlobalConstant.APP_SEARCH_KEY);
		} else {
			app_search_key = "";
		}
		initView();
		initNet();

	}

	private void initNet() {
		if (mHelp == null) {
			mHelp = new HttpHelp();
		}
		PrefUtils.saveHotKey(app_search_key);
		mHelp.sendGet(NetworkConfig.getSearchResult2(app_search_key, 1),
				SearchRseBean.class, new MyRequestCallBack<SearchRseBean>() {
					@Override
					public void onSucceed(SearchRseBean bean) {
						if (bean != null) {
							setMainData(bean, false);
						}
					}
				});
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

	private void setMainData(SearchRseBean bean, boolean loadmore) {
		if (bean == null || bean.cont == null) {
			return;
		}
		if (mDatas == null) {
			mDatas = new ArrayList<Cfilm_area>();
		}
		if (!loadmore)
			mDatas.clear();
		List<Cfilm_area> listku = bean.cont.listku;
		if (listku != null && listku.size() != 0) {
			for (Cfilm_area cfilm_area : listku) {

				if ("t4".equals(cfilm_area.flagdd)) {
					mDatas.add(0, cfilm_area);
				} else {
					mDatas.add(cfilm_area);
				}

			}
		}
		if (bean.cont.cfilm_area2 != null) {
			mDatas.addAll(bean.cont.cfilm_area2);
		}
		if (bean.cont.cfilm_area3 != null) {
			mDatas.addAll(bean.cont.cfilm_area3);
		}
		if (bean.cont.cfilm_area5 != null) {
			mDatas.addAll(bean.cont.cfilm_area5);
		}
		if (mSearchAadater == null) {
			mSearchAadater = new SearchAadater();
			search_list_view.setAdapter(mSearchAadater);
		} else {
			mSearchAadater.notifyDataSetInvalidated();
		}
	}

	private int refreshPage = 2;

	private void initView() {
		et_channel_find = (EditText) findViewById(R.id.et_channel_find);
		if (app_search_key != null)
			et_channel_find.setHint(app_search_key);
		addTextChangedListener(et_channel_find);
		et_channel_find.setOnFocusChangeListener(this);
		ibtn_channel_find = (TextView) findViewById(R.id.ibtn_channel_find);
		ibtn_channel_find.setOnClickListener(this);
		search_list_view = (PullToRefreshListView) findViewById(R.id.search_list_view);
		search_list_view.setMode(Mode.PULL_FROM_END);
		search_list_view.setOnRefreshListener(this);
		search_list_view.setOnItemClickListener(this);
	}

	public static final int TYPE_TV = 0;// 视频类型
	public static final int TYPE_DETAILS = 1;// 介绍类型
	public static final int TYPE_TEXT = 2;// 文本类型
	public static final int TYPE_ERR = 3;// 没数据
	public static final int TYPE_SHORT_TV = 4;// 小视屏
	private String app_search_key;
	private HttpHelp mHelp;
	private SearchAadater mSearchAadater;

	private class SearchAadater extends BaseAdapter {
		@Override
		public int getItemViewType(int position) {
			int type = 0;
			switch (mDatas.get(position).flagdd) {
			case "t1":
				type = TYPE_TV;
				break;
			case "t2":
				type = TYPE_DETAILS;
				break;
			case "t3":
				type = TYPE_TEXT;
				break;
			case "t4":
				type = TYPE_ERR;

			case "t5":
				type = TYPE_SHORT_TV;
				break;
			}
			return type;
		}

		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 4;
		}

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			ViewholderOne viewholderOne = null;
			ViewholderTwo viewholderTwo = null;
			ViewholderThree viewholderThree = null;
			ViewholderFour viewholderFour = null;
			if (convertView == null) {
				switch (itemViewType) {
				case TYPE_ERR:
					convertView = View.inflate(UIUtils.getContext(),
							R.layout.item_err_msg, null);
					break;
				case TYPE_TV:
					viewholderOne = new ViewholderOne();
					convertView = View.inflate(UIUtils.getContext(),
							R.layout.item_search_tv, null);
					viewholderOne.setView(viewholderOne, convertView, position);

					break;
				case TYPE_DETAILS:
					viewholderTwo = new ViewholderTwo();
					convertView = View.inflate(UIUtils.getContext(),
							R.layout.item_type_details, null);
					viewholderTwo.setView(viewholderTwo, convertView);
					convertView.setTag(viewholderTwo);
					break;
				case TYPE_TEXT:
					convertView = View.inflate(UIUtils.getContext(),
							R.layout.item_type_text, null);
					viewholderThree = new ViewholderThree();
					viewholderThree.setView(viewholderThree, convertView);
					convertView.setTag(viewholderThree);
					break;
				case TYPE_SHORT_TV:
					viewholderFour = new ViewholderFour();
					convertView = View.inflate(UIUtils.getContext(),
							R.layout.item_search_short_tv, null);
					viewholderFour.setView(viewholderFour, convertView,
							position);

					break;
				}
			} else {
				switch (itemViewType) {
				case TYPE_ERR:
					break;
				case TYPE_TV:
					viewholderOne = (ViewholderOne) convertView.getTag();
					break;
				case TYPE_DETAILS:
					viewholderTwo = (ViewholderTwo) convertView.getTag();
					break;
				case TYPE_TEXT:
					viewholderThree = (ViewholderThree) convertView.getTag();
					break;
				case TYPE_SHORT_TV:
					viewholderFour = (ViewholderFour) convertView.getTag();
					break;

				}
			}

			switch (itemViewType) {
			case TYPE_ERR:
				break;
			case TYPE_TV:
				viewholderOne.setData(position);
				break;
			case TYPE_DETAILS:
				viewholderTwo.setData(position);
				break;
			case TYPE_TEXT:
				viewholderThree.setData(position);
				break;
			case TYPE_SHORT_TV:
				viewholderFour.setData(position);
				break;

			}

			return convertView;

		}
	}

	private class ViewholderFour {
		ImageView iv_tv_icon;
		TextView tv_tv_name;

		private void setView(ViewholderFour one, View view, int position) {
			iv_tv_icon = (ImageView) view.findViewById(R.id.iv_tv_icon);
			tv_tv_name = (TextView) view.findViewById(R.id.tv_tv_name);
			view.setTag(this);

		}

		public void setData(int position) {
			Cfilm_area cfilm_area = mDatas.get(position);
			if (cfilm_area == null) {
				return;
			}
			mHelp.showImage(iv_tv_icon, cfilm_area.pic);
			tv_tv_name.setText(cfilm_area.name);
		}
	}

	private class ViewholderOne {
		private GridView gv_tv_num;
		ImageView iv_tv_icon;
		View tv_up_layout;
		TextView tv_tv_name, tv_up_num, tv_tv_direct, tv_tv_lead_role,
				tvt_tv_type, tv_bt_play, tv_tv_details;

		private void setView(ViewholderOne one, View view, int position) {
			iv_tv_icon = (ImageView) view.findViewById(R.id.iv_tv_icon);
			tv_tv_name = (TextView) view.findViewById(R.id.tv_tv_name);
			tv_up_num = (TextView) view.findViewById(R.id.tv_up_num);
			tv_tv_direct = (TextView) view.findViewById(R.id.tv_tv_direct);
			tv_tv_lead_role = (TextView) view
					.findViewById(R.id.tv_tv_lead_role);
			tvt_tv_type = (TextView) view.findViewById(R.id.tvt_tv_type);
			tv_tv_details = (TextView) view.findViewById(R.id.tv_tv_details);
			tv_bt_play = (TextView) view.findViewById(R.id.tv_bt_play);

			tv_up_layout = view.findViewById(R.id.tv_up_layout);
			gv_tv_num = (GridView) view.findViewById(R.id.gv_tv_num);
			view.setTag(this);

		}

		public void setData(int position) {
			setOnClick(tv_bt_play, position);
			Cfilm_area cfilm_area = mDatas.get(position);
			if (cfilm_area == null) {
				return;
			}
			if ("0".equals(cfilm_area.jujinum)) {
				tv_up_layout.setVisibility(View.GONE);
				gv_tv_num.setVisibility(View.GONE);
			} else {
				gv_tv_num.setVisibility(View.VISIBLE);
				tv_up_layout.setVisibility(View.VISIBLE);
				tv_up_num.setText(cfilm_area.jujinum + "集");
				setGridAdapter(gv_tv_num, cfilm_area, position);
			}
			mHelp.showImage(iv_tv_icon, cfilm_area.pic);
			tv_tv_name.setText(cfilm_area.name);
			tv_tv_direct.setText("导演： " + cfilm_area.derector);
			tv_tv_lead_role.setText("主演： " + cfilm_area.actor);
			tvt_tv_type.setText("类型： " + cfilm_area.ctypename);

		}
	}

	private class ViewholderTwo {
		ImageView iv_bg_icon;

		TextView tv_main_content, tv_child_content;

		private void setView(ViewholderTwo one, View view) {
			iv_bg_icon = (ImageView) view.findViewById(R.id.iv_bg_icon);
			tv_main_content = (TextView) view
					.findViewById(R.id.tv_main_content);
			tv_child_content = (TextView) view
					.findViewById(R.id.tv_child_content);
		}

		public void setData(int position) {
			Cfilm_area cfilm_area = mDatas.get(position);
			mHelp.showImage(iv_bg_icon, cfilm_area.pic);
			tv_main_content.setText(cfilm_area.name);
		}
	}

	private class ViewholderThree {

		TextView tv_content;

		private void setView(ViewholderThree one, View view) {
			tv_content = (TextView) view.findViewById(R.id.tv_content);
		}

		public void setData(int position) {
			tv_content.setText(mDatas.get(position).name);

		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			// startActivity(new Intent(this, SearchActivity.class));
			// finish();
		}

	}

	/**
	 * 设置剧集
	 * 
	 * @param gv_tv_num
	 * @param cfilm_area
	 * @param position
	 */
	public void setGridAdapter(GridView view, Cfilm_area cfilm_area,
			int position) {
		int tvNeum = (int) StringNumUtils.string2Num(cfilm_area.jujinum);
		int cont = tvNeum > 8 ? 8 : tvNeum;
		view.setAdapter(new GridAdapter(cont, tvNeum));
		setOnItemClickListener(view, position, tvNeum);

	}

	/**
	 * 设置剧集点击事件
	 * 
	 * @param view
	 * @param tvNeum
	 * @param position
	 */
	private void setOnItemClickListener(GridView view,
			final int listViewPosition, final int tvNeum) {
		view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 7 && tvNeum > 8) {
					Intent intent = new Intent(SearchResultPageActivity.this,
							SelectPlayNumActivity.class);
					intent.putExtra(GlobalConstant.TV_ID_AND_TVNUM,
							mDatas.get(listViewPosition));
					startActivity(intent);
					return;
				}
				enterSourcePage(listViewPosition, position);

			}
		});

	}

	protected void enterSourcePage(final int listViewPosition,
			final int position) {
		mHelp.sendGet(NetworkConfig.getReqPlay(mDatas.get(listViewPosition).id,
				position + 1), BaseBaen.class,
				new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || "0".equals(bean.status)) {
							UIUtils.showToast(
									UIUtils.getContext(),
									UIUtils.getContext()
											.getResources()
											.getString(
													R.string.state_not_resource));
							return;
						}
						Intent intent = new Intent(
								SearchResultPageActivity.this,
								PlaySourceActivity.class);
						intent.putExtra(GlobalConstant.TV_ID,
								mDatas.get(listViewPosition).id
										+ GlobalConstant.FLAG_APP_SPLIT
										+ (position + 1));
						startActivity(intent);
					}
				});
	}

	private class GridAdapter extends AppBaseAdapter {
		private int tvNum;

		public GridAdapter(int count, int tvNum) {
			super(count);
			this.tvNum = tvNum;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GridViewHolder holder;
			if (convertView == null) {
				holder = new GridViewHolder();
				convertView = UIUtils.inflate(R.layout.item_scroll_view);
				holder.setView(convertView);
			} else {
				holder = (GridViewHolder) convertView.getTag();
			}
			holder.setData(position, tvNum);
			return convertView;
		}
	}

	private class GridViewHolder {
		TextView v_scroll;

		public void setView(View convertView) {
			v_scroll = (TextView) convertView.findViewById(R.id.tv_scroll);
			convertView.setTag(this);
		}

		public void setData(int position, int tvNum) {
			if (position == 7 && tvNum > 8) {
				v_scroll.setText("...");
			} else {
				v_scroll.setText((position + 1) + "");
			}

		}

	}

	/**
	 * 设置点击跳转事件播放
	 * 
	 * @param tv_bt_play
	 * @param position
	 */
	public void setOnClick(View v, final int position) {
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int i = position;
				final String filmid = mDatas.get(position).filmid;
				if ("0".equals(filmid)) {
					UIUtils.showToast(
							UIUtils.getContext(),
							UIUtils.getContext().getResources()
									.getString(R.string.state_not_resource));
					return;
				}
				if (mHelp == null) {
					mHelp = new HttpHelp();
				}
				mHelp.sendGet(
						NetworkConfig.getReqPlay(mDatas.get(position).id, 0),
						BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

							@Override
							public void onSucceed(BaseBaen bean) {
								if (bean == null || "0".equals(bean.status)) {
									UIUtils.getContext()
											.getResources()
											.getString(
													R.string.state_not_resource);
									return;
								}

								Intent intent = new Intent(
										UIUtils.getContext(),
										PlayActivity.class);
								UIUtils.showToast(UIUtils.getContext(),
										mDatas.get(position).name);
								intent.putExtra(GlobalConstant.TV_ID, filmid
										+ GlobalConstant.FLAG_APP_SPLIT
										+ (position + 1));
								startActivity(intent);
								// finish();
							}
						});

			}
		});
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {

		mHelp.sendGet(
				NetworkConfig.getSearchResult2(app_search_key, refreshPage),
				SearchRseBean.class, new MyRequestCallBack<SearchRseBean>() {

					@Override
					public void onSucceed(SearchRseBean bean) {
						if (bean != null) {
							refreshPage++;
							setMainData(bean, true);
						} else {
							UIUtils.showToast(UIUtils.getContext(),
									"没有更多数据了...");
						}
						search_list_view.onRefreshComplete();
					}
				});

	}

	/**
	 * 条目点击事件,跳转播放来源页
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cfilm_area cfilm_area = mDatas.get(position - 1);
		if (cfilm_area == null) {
			return;
		}
		if ("t4".equals(cfilm_area.flagdd)) {
			return;
		}
		if (cfilm_area.hasfilm == null || "0".equals(cfilm_area.hasfilm)) {

			UIUtils.showToast(UIUtils.getContext(), UIUtils.getContext()
					.getResources().getString(R.string.state_not_resource));
			return;
		}
		if ("t5".equals(cfilm_area.flagdd)) {
			Intent intent = new Intent(this,PlayActivity.class);
			intent.putExtra(GlobalConstant.PLAY_VIDEO_ID, cfilm_area.id);
			startActivity(intent);
			return;
		}
		Intent intent = new Intent(this, PlaySourceActivity.class);
		intent.putExtra(GlobalConstant.TV_ID, cfilm_area.id);
		startActivity(intent);

	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_channel_find:
			if (isSearch) {
				if (et_channel_find != null) {
					String searchKey = et_channel_find.getText().toString();
					if (!TextUtils.isEmpty(searchKey)) {
						app_search_key = searchKey;
						initNet();
					} else {
						finish();
					}

				}
			} else {
				finish();
			}

			break;

		}

	}

	/**
	 * 输入监听
	 * 
	 * @param et_search_pager_find2
	 */
	private boolean isSearch = false; // 是否搜索
	private EditText et_channel_find;
	private TextView ibtn_channel_find;

	private void addTextChangedListener(final EditText edittext) {
		edittext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String enter_key = edittext.getText().toString();
				if (enter_key.length() == 0) {
					isSearch = false;
					ibtn_channel_find.setText("取消");
				} else {
					ibtn_channel_find.setText("搜索");
					isSearch = true;
				}
			}
		});

	}

}
