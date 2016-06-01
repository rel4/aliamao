package com.aliamauri.meat.play;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.activity.search_activity.AlertDialogActivity;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.PlaySoursBase;
import com.aliamauri.meat.bean.PlaySoursBase.Cont.Cfilmlist;
import com.aliamauri.meat.bean.PlaySoursBase.Cont.Ckuinfo;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.StringNumUtils;
import com.aliamauri.meat.utils.SystemUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.TabPageIndicator;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.limaoso.phonevideo.db.CacheInfo;
import com.limaoso.phonevideo.db.CacheMessgeDao;
import com.limaoso.phonevideo.p2p.P2PManager;
import com.limaoso.phonevideo.utils.SDUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 播放来源页
 * 
 * @author jjm
 * 
 */
public class PlaySourceActivity extends BaseActivity implements
		OnClickListener, OnFocusChangeListener, OnItemClickListener,
		OnRefreshListener<ListView> {
	private String TAG = "PlaySourceActivity";
	private PullToRefreshListView mListView;
	private RelativeLayout scroll_view_layout;
	private PlaySourceAadapter mAadapter;
	private GridView gridView;
	private EditText et_channel_play_find;
	private PlaySoursBase mBean;
	private List<Cfilmlist> mCfilmlists;
	private int jujinum;// 剧集数

	private ImageView mIv_play_source_page_show; // 该条目的电影展示图片
	private ImageButton mIbtn_play_video_button; // 电影展示图片的播放按钮
	private View v_playsoruce_line;// 在播放来源页中显示，简介不显示
	private GridViewAdapter mGridViewAdapter;
	// 首先在您的Activity中添加如下成员变量
//	final UMSocialService mController = UMServiceFactory
//			.getUMSocialService("com.umeng.share");

	@Override
	protected void initView() {
		init();
		initIntent();
	}

	@Override
	protected View getRootView() {
		return UIUtils.inflate(R.layout.page_play_source_page);
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
	
	private void initIntent() {
		Intent intent = getIntent();
		Uri uri = intent.getData();
		if (uri == null) {
			if (intent != null) {
				String tv = intent.getStringExtra(GlobalConstant.TV_ID);
				String[] split = tv.split(GlobalConstant.FLAG_APP_SPLIT);
				if (split.length > 1) {
					jindex = (int) StringNumUtils.string2Num(split[1]);
					currentJuJiNum = jindex;
				}
				tv_id = split[0];
				initNet(split[0]);
			}
		} else {
			tv_id = uri.getQueryParameter("id");
			jindex = Integer.parseInt(uri.getQueryParameter("jindex"));
			currentJuJiNum = jindex;
			initNet(tv_id);

		}
	}

	/**
	 * 
	 */
	private int jindex = 1;// 默认集数
	private int currentJuJiNum = jindex;// 剧集数

	private void initNet(String tv_id) {
		if (mHelp == null) {
			mHelp = new HttpHelp();
		}
		mHelp.sendGet(NetworkConfig.getPlaySourceAll(tv_id, jindex),
				PlaySoursBase.class, new MyRequestCallBack<PlaySoursBase>() {
					@Override
					public void onSucceed(PlaySoursBase bean) {
						mBean = bean;
						if (bean == null || bean.cont.cfilmlist == null
								|| "".equals(bean.cont.cfilmlist)) {
							// UIUtils.showToast(UIUtils.getContext(),
							// "服务器忙~~");
							return;
						}
						mCfilmlists = mBean.cont.cfilmlist;
						initShare();
						initTVHead();
						isShowEpisode();// 是否带选集功能
						playSourceList();// 初始化来源列表
					}
				});
	}

	protected void initShare() {
//		Shareinfo shareinfo = mBean.cont.shareinfo;
//		if (shareinfo != null && mPlatforms == null) {
//			mPlatforms = new SharePlatforms(this, mController, shareinfo);
//
//		}

	}

	/**
	 * 初始化TV信息
	 */
	protected void initTVHead() {
		if (mCfilmlists == null || mCfilmlists.size() <= 0) {
			return;
		}
		Cfilmlist cfilmlist = mCfilmlists.get(PlaySourceItem);
		double rb = StringNumUtils.string2Num(cfilmlist.up)
				/ ((StringNumUtils.string2Num(cfilmlist.up) + StringNumUtils
						.string2Num(cfilmlist.down)));
		String name = cfilmlist.name;
		name = name.length() > 20 ? name.substring(0, 20) + "..." : name;
		tv_tv_name.setText(name);
		mHelp.showImage(mIv_play_source_page_show, TextUtils.isEmpty(cfilmlist.picdown)?cfilmlist.pic:cfilmlist.picdown); // 设置相对应条目的电影预览图
		// ratingBar.setRating((float) (rb * 5));
		ratingBar.setRating((float) (5));

		// Cfilmlist cfilmlist = mCfilmlists.get(PlaySourceItem);
		// double rb = StringNumUtils.string2Num(cfilmlist.up)
		// / ((StringNumUtils.string2Num(cfilmlist.up) + StringNumUtils
		// .string2Num(cfilmlist.down)));
		// String name = cfilmlist.name;
		// name = name.length() > 20 ? name.substring(0, 20) + "..." : name;
		// tv_tv_name.setText(name);
		//
		// mHelp.showImage(mIv_play_source_page_show, cfilmlist.pic); //
		// 设置相对应条目的电影预览图
		// ratingBar.setRating((float) (rb * 5));
		// ratingBar.setRating((float) (5));

	}

	/**
	 * 显示剧集
	 */
	private void isShowEpisode() {
		jujinum = (int) StringNumUtils.string2Num(mBean.cont.ckuinfo.jujinum);
		// jujinum = 200;
		if (mBean.cont.ckuinfo.jujinum == null
				|| "0".equals(mBean.cont.ckuinfo.jujinum)) {
			gridView.setVisibility(View.GONE);
		} else {
			getPageTitle();// 获取集数标头
			setGridView();// 横向集数列表
			gridView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化布局
	 */
	private void init() {
		v_playsoruce_line = findViewById(R.id.v_playsoruce_line);
		tv_tv_name = (TextView) findViewById(R.id.tv_tv_name);
		scroll_view_layout = (RelativeLayout) findViewById(R.id.scroll_view_layout);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		et_channel_play_find = (EditText) findViewById(R.id.et_channel_play_find);
		rg_play_page_title = (RadioGroup) findViewById(R.id.rg_play_page_title);
		mListView = (PullToRefreshListView) findViewById(R.id.play_source_list_view);
		gridView = (GridView) findViewById(R.id.grid);
		mIv_play_source_page_show = (ImageView) findViewById(R.id.iv_play_source_page_show);
		mIbtn_play_video_button = (ImageButton) findViewById(R.id.ibtn_play_video_button);
		mListView.setMode(Mode.PULL_FROM_END);
		mListView.setOnRefreshListener(this);
		rg_play_page_title.check(R.id.tv_play_source);
		findViewById(R.id.tv_play_source).setOnClickListener(this);
		findViewById(R.id.iv_down_task).setOnClickListener(this);
		findViewById(R.id.btn_search_pager_cancel).setOnClickListener(this);
		findViewById(R.id.tv_play_info).setOnClickListener(this);
		findViewById(R.id.rb_share).setOnClickListener(this);
		et_channel_play_find.setOnFocusChangeListener(this);
		mIbtn_play_video_button.setOnClickListener(this);
		initBroadcast();
	}

	/**
	 * 初始化关闭页面广播
	 */
	private void initBroadcast() {
		broadcastReceiver = new MyBroadcastReceiver();
		registerReceiver(broadcastReceiver, new IntentFilter(
				GlobalConstant.CLOSE_PLAY_SOURCE_ACTIVITY));

	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_play_source:// 播放来源列表
			v_playsoruce_line.setVisibility(View.VISIBLE);
			playSourceList();
			setGridView();
			break;
		case R.id.tv_play_info:// 播放简介
			v_playsoruce_line.setVisibility(View.GONE);
			playinfo();
			break;
		case R.id.ibtn_play_video_button:// 预览图片播放按钮
			if (mCfilmlists == null || mCfilmlists.size() <= 0) {
				return;
			}
			playItemPositionVideo(PlaySourceItem);
			break;
		case R.id.rb_share:
			v_playsoruce_line.setVisibility(View.GONE);
			sharePlatform();// 分享
			break;
		case R.id.popu_back:// 关闭
			showPopopWindow();
			break;
		case R.id.btn_search_pager_cancel:// 关闭
			finish();
			break;
		case R.id.iv_down_task:// 下载
			if (mCfilmlists == null || mCfilmlists.size() == 0) {
				return;
			}
			dowmItemPositionVideo(PlaySourceItem);
			break;

		}
	}

	/**
	 * 分享
	 */

	private void sharePlatform() {
//		if (mPlatforms != null) {
//
//			mPlatforms.initView();
//			mController.openShare(this, false);
//		}

	}

	/**
	 * 视频详情
	 */
	private void playinfo() {
		gridView.setVisibility(View.GONE);
		if (scroll_view_layout.getChildCount() > 0) {
			scroll_view_layout.removeViewAt(0);
		}

		initIntroduction();// 简介布局
	}

	/**
	 * 简介
	 */
	private void initIntroduction() {
		if (mBean == null || mBean.cont.ckuinfo == null) {
			return;
		}
		Ckuinfo ckuinfo = mBean.cont.ckuinfo;
		View player_introduction = UIUtils
				.inflate(R.layout.player_introduction);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		scroll_view_layout.addView(player_introduction, params);
		((TextView) player_introduction.findViewById(R.id.tv_play_num))
				.setText("播放次数:" + ckuinfo.bf_num + "");
		((TextView) player_introduction.findViewById(R.id.tv_up_time))
				.setText("更新时间:" + ckuinfo.updatetime + "");
		((TextView) player_introduction.findViewById(R.id.tv_derector))
				.setText("导演:"
						+ (ckuinfo.actor.length() > 6 ? ckuinfo.actor
								.substring(0, 5) + "..." : ckuinfo.actor));
		((TextView) player_introduction.findViewById(R.id.tv_type_tv))
				.setText("类型:" + ckuinfo.typename + "");
		((TextView) player_introduction.findViewById(R.id.tv_actor))
				.setText("主演:"
						+ (ckuinfo.derector.length() > 6 ? ckuinfo.derector
								.substring(0, 5) + "..." : ckuinfo.derector)
						+ "");
		((TextView) player_introduction.findViewById(R.id.tv_area))
				.setText("地区:" + ckuinfo.area + "");
		String desc = ckuinfo.desc.replace("\n", "");// \r\n
		desc = desc.replace("\r", "");
		desc = desc.replace("　", "");
		ckuinfo.desc.replace("\n", "");// \r\n
		((TextView) player_introduction
				.findViewById(R.id.tv_player_introduction_content))
				.setText(desc.trim());

	}

	/**
	 * 播放列表
	 */
	private void playSourceList() {
		gridView.setVisibility(View.VISIBLE);
		if (scroll_view_layout.getChildCount() > 0) {
			scroll_view_layout.removeViewAt(0);
		}
		scroll_view_layout.addView(mListView);
		setListAdapter();
	}

	private void setListAdapter() {
		if (mAadapter == null) {
			mAadapter = new PlaySourceAadapter();
			mListView.setAdapter(mAadapter);
			mListView.setOnItemClickListener(this);
		} else {
			mAadapter.notifyDataSetInvalidated();
		}
		initTVHead();
	}

	private int PlaySourceItem = 0;// 点击资源条目

	private class PlaySourceAadapter extends BaseAdapter {
		@Override
		public int getCount() {
			if (mCfilmlists == null) {
				return 0;
			}
			return mCfilmlists.size();
		}

		@Override
		public Object getItem(int position) {
			return mCfilmlists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			PlaySourceHolder sourceHolder;
			if (convertView == null) {
				sourceHolder = new PlaySourceHolder();
				convertView = View.inflate(PlaySourceActivity.this,
						R.layout.item_play_source_list, null);
				sourceHolder.setView(convertView);

				convertView.setTag(sourceHolder);
			} else {
				sourceHolder = (PlaySourceHolder) convertView.getTag();
			}
			/*
			 * 点击播放按钮后，进入播放界面
			 */
			setsetOnClickListener(sourceHolder.tv_play_tag, position);// 设置点击播放
			sourceHolder.setData(position);
			return convertView;
		}
	}

	public void setsetOnClickListener(View view, final int position) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PlaySourceItem = position;
				playItemPositionVideo(position);
			}

		});
	}

	/**
	 * 点击当前影片的播放按钮进行相应条目位置的播放
	 * 
	 * @param position
	 */
	private void playItemPositionVideo(final int position) {
		if (mAadapter != null) {
			mAadapter.notifyDataSetInvalidated();
		}
		Intent intent = new Intent(PlaySourceActivity.this, PlayActivity.class);
		
		intent.putExtra(GlobalConstant.TV_ID, mCfilmlists.get(position).id
				+ GlobalConstant.FLAG_APP_SPLIT + jindex);
		startActivity(intent);
	}

	/**
	 * 缓存当前影片的播放按钮进行相应条目位置的播放 type=1为 磁力链接 type=2 为狸猫链接
	 * 
	 * @param position
	 */
	private void dowmItemPositionVideo(final int position) {
		Cfilmlist cfilmlist = mCfilmlists.get(position);
		if (cfilmlist == null) {
			LogUtil.e(TAG, "下载信息为null");
			return;
		}
		//内存不足
		if (cfilmlist.filesize>(SDUtils.getCacheAvailableSize(getApplicationContext())-1024*1024*200)) {
			startActivity(new Intent(this, AlertDialogActivity.class));
			return;
		}
		String lmlinkurl = cfilmlist.lmlinkurl;
		if (!TextUtils.isEmpty(lmlinkurl)) {
			saveInfo(cfilmlist);
			P2PManager.getInstance(getApplicationContext()).cacheP2PFile(lmlinkurl);
			UIUtils.showToast(this, "已经开始下载");
		}
	}

	/**
	 * 保存下载信息
	 * 
	 * @param cfilmlist
	 */
	private void saveInfo(Cfilmlist cfilmlist) {
		CacheMessgeDao cacheMessgeDao = new CacheMessgeDao(this);
		CacheInfo info = new CacheInfo();
		String lmlinkurl = cfilmlist.lmlinkurl;
		info.setTvId(cfilmlist.id);
		info.setTvPlaynum(currentJuJiNum + "");
		info.setTvHash(lmlinkurl);
		info.setTvName(cfilmlist.name);
		info.setTvPicPath(cfilmlist.pic);
		info.setTvFileSize(cfilmlist.filesize);
		cacheMessgeDao.saveMessage(info);
	}

	/**
	 * 视频viewholder
	 * 
	 * @author jjm
	 * 
	 */
	private class PlaySourceHolder {
		TextView tv_play_tag, tv_title, tv_content;
		LinearLayout ll_play_item;
		View mView;

		public void setView(View convertView) {
			this.mView = convertView;
			tv_play_tag = (TextView) convertView.findViewById(R.id.tv_play_tag);
			tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			ll_play_item = (LinearLayout) convertView
					.findViewById(R.id.ll_play_item);
		}

		@SuppressWarnings("deprecation")
		public void setData(int position) {
			Cfilmlist cfilmlist = mCfilmlists.get(position);
			String name = cfilmlist.name;
			tv_title.setText(name);
			String surl = cfilmlist.surl;
			tv_content.setText(surl);
			if (position == PlaySourceItem) {
				mView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.bfly_djh_ht));
				tv_play_tag.setSelected(true);

			} else {
				mView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.bfly_djq_ht));
				tv_play_tag.setSelected(false);
			}
		}
	}

	/**
	 * 设置横向适配器
	 */
	private void setGridView() {
		if (mGridViewAdapter != null) {
			mGridViewAdapter.notifyDataSetInvalidated();
		} else {
			mGridViewAdapter = new GridViewAdapter(PlaySourceActivity.this);
			gridView.setAdapter(mGridViewAdapter);
			gridView.setOnItemClickListener(this);
		}
	}

	public class GridViewAdapter extends BaseAdapter {
		Context context;

		public GridViewAdapter(Context _context) {
			this.context = _context;
		}

		@Override
		public int getCount() {
			return jujinum >= 6 ? 6 : jujinum;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(PlaySourceActivity.this,
						R.layout.item_scroll_view, null);

				holder.setView(convertView);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == (jindex - currentJuJiNum)) {
				convertView.setBackgroundResource(R.drawable.shape_gray_circle);
			} else {
				convertView
						.setBackgroundResource(R.drawable.shape_white_circle);
			}
			holder.setData(position);
			return convertView;
		}
	}

	class ViewHolder {
		TextView tv_scroll;
		View mView;

		public void setView(View convertView) {
			tv_scroll = (TextView) convertView.findViewById(R.id.tv_scroll);
			convertView.setTag(this);
		}

		public void setData(int position) {
			if (gridClickItem == position) {
				tv_scroll.setTextColor(getResources()
						.getColor(R.color.bg_green));
			} else {
				tv_scroll.setTextColor(getResources()
						.getColor(R.color.bg_black));
			}
			if (position == 5 && jujinum > 6) {
				tv_scroll.setText("...");
			} else {

				tv_scroll.setText((position + currentJuJiNum) + "");
			}
		}

		public void setPopuData(int pagePosition, int position) {
			if ((position + 1) == jindex) {

				tv_scroll.setTextColor(getResources()
						.getColor(R.color.bg_green));
			} else {
				tv_scroll.setTextColor(getResources().getColor(
						R.color.bg_black));
			}
			tv_scroll.setText(pagePosition * 50 + position + 1 + "");
			setClickItem(tv_scroll);

		}
	}

	int moveLengh = 0;
	private RadioGroup rg_play_page_title;
	private HttpHelp mHelp;

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
//		if (hasFocus == true) {
//			et_channel_play_find.clearFocus();
//			PlaySourceActivity.this.startActivity(new Intent(
//					PlaySourceActivity.this, SearchActivity.class));
//		}

	}

	public void setClickItem(final TextView tv_scroll) {
		tv_scroll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TVListPage = 1;
				jindex = (int) StringNumUtils.string2Num(tv_scroll.getText()
						.toString());
				isPopuClick = true;
				responseClickIsPlay(jindex);

			}
		});

	}

	/**
	 * 点击响应点击事件是否播放
	 * 
	 * @param jindex
	 */
	private boolean isPopuClick = false;

	protected void responseClickIsPlay(final int num) {
		mHelp.sendGet(NetworkConfig.getReqPlay(tv_id, num), BaseBaen.class,
				new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || "0".equals(bean.status)) {
							UIUtils.getContext().getResources()
									.getString(R.string.state_not_resource);
							return;
						}
						if (isPopuClick) {

							currentJuJiNum = jindex;
							popuNet();

							if (!isOpenPopu && mPopupWindow != null) {
								mPopupWindow.dismiss();
								isOpenPopu = !isOpenPopu;
							}
						} else {

							gridClickItem = num - currentJuJiNum;
							jindex = num;
							isUp = false;
							TVListPage = 1;
							PlaySourceItem = 0;
							GridNotifyData();
							mGridViewAdapter.notifyDataSetInvalidated();
						}
					}
				});

	}

	/**
	 * 弹框请求网络
	 */
	protected void popuNet() {
		mHelp.sendGet(mBean.url + "?page=" + TVListPage + "&jindex=" + jindex,
				PlaySourceRefresh.class,
				new MyRequestCallBack<PlaySourceRefresh>() {

					@Override
					public void onSucceed(PlaySourceRefresh bean) {
						if (bean != null && bean.cont != null) {
							mCfilmlists.clear();
							mCfilmlists.addAll(bean.cont);
							setListAdapter();
							gridClickItem = 0;// 重置横向集数点击条目
							setGridView();//
							TVListPage++;
						}

					}
				});

	}

	private int gridClickItem = 0;// 横条grid
	private int gridPopuClickItem = 0;// 弹框grid
	private boolean isOpenPopu = true;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		switch (parent.getId()) {
		case R.id.item_tv_list:
			if (itemAdapter != null) {
				gridPopuClickItem = position;
				itemAdapter.notifyDataSetInvalidated();
			}
			break;
		case R.id.grid:
			gridItemClick(position);
			break;

		default:
			PlaySourceItem = position - 1;
			mAadapter.notifyDataSetInvalidated();
			initTVHead();
			break;
		}
	}

	/**
	 * 来源列表条目点击事件
	 * 
	 * @param position
	 */
	private void gridItemClick(int position) {
		if (position == 5 && jujinum > 6) {
			showPopopWindow();
			return;
		}
		int jin = position + currentJuJiNum;
		isPopuClick = false;
		responseClickIsPlay(jin);

	}

	private void showPopopWindow() {
		if (isOpenPopu) {
			if (popwindow_layout == null) {
				popwindow_layout = (LinearLayout) LayoutInflater.from(this)
						.inflate(R.layout.play_sourc_popwindow, null);
				ViewPager pager = (ViewPager) popwindow_layout
						.findViewById(R.id.pager);
				GoogleMusicAdapter googleMusicAdapter = new GoogleMusicAdapter();
				pager.setAdapter(googleMusicAdapter);
				TabPageIndicator indicator = (TabPageIndicator) popwindow_layout
						.findViewById(R.id.indicator);
				indicator.setVisibility(View.VISIBLE);
				indicator.setViewPager(pager);

				popwindow_layout.findViewById(R.id.popu_back)
						.setOnClickListener(this);
			}
			if (mPopupWindow == null) {
				int popuHeight = SystemUtils.getScreenHeight()
						- (int) PlaySourceActivity.this.getResources()
								.getDimension(R.dimen.dp250);
				mPopupWindow = new PopupWindow(popwindow_layout,
						LayoutParams.FILL_PARENT, popuHeight);
				mPopupWindow.setAnimationStyle(R.style.AnimCommentList);
			}
			mPopupWindow.showAtLocation(
					findViewById(R.id.play_source_list_view), Gravity.RIGHT
							| Gravity.BOTTOM, 0, 0);
		} else {
			popwindow_layout = null;
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
		isOpenPopu = !isOpenPopu;
	}

	private int gridPage;// 弹框列表页数
	private String[] titles;// 标头

	private void getPageTitle() {

		if (jujinum % 50 == 0) {
			gridPage = jujinum / 50;
		} else {
			gridPage = jujinum / 50 + 1;
		}
		titles = new String[gridPage];
		for (int i = 1; i <= gridPage; i++) {
			if ((jujinum - (i - 1) * 50) > 50) {
				titles[i - 1] = (i - 1) * 50 + 1 + "-" + i * 50;
			} else {
				titles[i - 1] = (i - 1) * 50 + 1 + "-" + (jujinum);
			}
		}
	}

	class GoogleMusicAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return gridPage;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = View.inflate(UIUtils.getContext(),
					R.layout.item_tv_list_tabpageindicator, null);
			GridView item_tv_list = (GridView) view
					.findViewById(R.id.item_tv_list);
			setItemAdapter(item_tv_list, position);
			container.addView(view);
			return view;
		}
	}

	private class ItemAdapter extends BaseAdapter {
		private int pagePosition;

		public ItemAdapter(int position) {
			this.pagePosition = position;
		}

		@Override
		public int getCount() {

			return jujinum - 50 * pagePosition > 50 ? 50 : jujinum - 50
					* pagePosition;
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
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(PlaySourceActivity.this,
						R.layout.item_scroll_view, null);
				holder.setView(convertView);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (jindex == (position + 1)) {
				convertView.setBackgroundResource(R.drawable.shape_gray_circle);
			} else {
				convertView
						.setBackgroundResource(R.drawable.shape_white_circle);
			}
			holder.setPopuData(pagePosition, position);
			return convertView;
			// return tv;
		}

	}

	/**
	 * 更新来源数据
	 */
	private void GridNotifyData() {
		if (mBean == null || mHelp == null) {
			return;
		}
		mHelp.sendGet(mBean.url + "?page=" + TVListPage + "&jindex=" + jindex,
				PlaySourceRefresh.class,
				new MyRequestCallBack<PlaySourceRefresh>() {

					@Override
					public void onSucceed(PlaySourceRefresh bean) {
						if (bean != null && bean.cont != null) {
							if (isUp) {
								mCfilmlists.addAll(bean.cont);
								setListAdapter();
								mListView.onRefreshComplete();
							} else {
								mCfilmlists.clear();
								mCfilmlists.addAll(bean.cont);

								setListAdapter();
							}
							TVListPage++;
						}

					}
				});

	}

	/**
	 * 弹框
	 * 
	 * @param position
	 * 
	 * @param item_tv_list
	 */
	public void setItemAdapter(GridView view, int position) {
		itemAdapter = new ItemAdapter(position);
		view.setAdapter(itemAdapter);
		view.setOnItemClickListener(this);
	}

	private int TVListPage = 2;// 来源页数
	private boolean isUp = true;
	private LinearLayout popwindow_layout;
	private PopupWindow mPopupWindow;
	private ItemAdapter itemAdapter;
	private RatingBar ratingBar;
	private TextView tv_tv_name;
	private String tv_id;
	private MyBroadcastReceiver broadcastReceiver;
//	private SharePlatforms mPlatforms;

	// private ConnectionChangeReceiver myReceiver;

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		isUp = true;
		GridNotifyData();
	}

	public class PlaySourceRefresh extends BaseBaen {
		List<Cfilmlist> cont;
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!isOpenPopu && mPopupWindow != null) {
			mPopupWindow.dismiss();
			isOpenPopu = !isOpenPopu;
		}
	}

	@Override
	protected void onDestroy() {
		if (!isOpenPopu && mPopupWindow != null) {
			mPopupWindow.dismiss();
			mPopupWindow = null;
			isOpenPopu = !isOpenPopu;
		}
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
		}
		// unregisterReceiver();
		super.onDestroy();
	}

	// public class ConnectionChangeReceiver extends BroadcastReceiver {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// ConnectivityManager connectivityManager = (ConnectivityManager) context
	// .getSystemService(Context.CONNECTIVITY_SERVICE);
	// NetworkInfo mobNetInfo = connectivityManager
	// .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	// NetworkInfo wifiNetInfo = connectivityManager
	// .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	//
	// if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
	// UIUtils.showToast(context, "网络不可以用");
	// // 改变背景或者 处理网络的全局变量
	// } else if (mobNetInfo.isConnected()) {
	// UIUtils.showToast(context, "网络 : 手机网络");
	//
	// } else if (wifiNetInfo.isConnected()) {
	// UIUtils.showToast(context, "网络 : wifi ："+wifiNetInfo.getSubtypeName());
	// }
	//
	// }
	// }

	@Override
	protected void onStart() {
		// registerReceiver();
		super.onStart();
	}

	// private void registerReceiver() {
	// IntentFilter filter = new IntentFilter(
	// ConnectivityManager.CONNECTIVITY_ACTION);
	// myReceiver = new ConnectionChangeReceiver();
	// this.registerReceiver(myReceiver, filter);
	// }
	//
	// private void unregisterReceiver() {
	// if (myReceiver != null) {
	//
	// this.unregisterReceiver(myReceiver);
	// }
	// }

}
