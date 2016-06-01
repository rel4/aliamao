package com.aliamauri.meat.play;

import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.Lmp2pMediaPlayer;
import tv.danmaku.ijk.media.sample.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.sample.widget.media.MediaController;
import tv.danmaku.ijk.media.sample.widget.media.MediaController.OnHiddenListener;
import tv.danmaku.ijk.media.sample.widget.media.MediaController.OnShownListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.activity.DoyenDetailActvity;
import com.aliamauri.meat.activity.OtherDataActivity;
import com.aliamauri.meat.adapter.CommentContentAdapter;
import com.aliamauri.meat.adapter.VideoCommendAdapter;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.Comlist;
import com.aliamauri.meat.bean.PlayBaen;
import com.aliamauri.meat.bean.PlayBaen.Cont;
import com.aliamauri.meat.bean.PlayBaen.Cont.Shareinfo;
import com.aliamauri.meat.bean.PlayBaen.Cont.Userinfo;
import com.aliamauri.meat.bean.PlayBaen.Cont.Videoother;
import com.aliamauri.meat.bean.PlayBaen.Cont.Vinfo;
import com.aliamauri.meat.eventBus.PlayVideo_event;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.NetWorkUtil;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.SharePlatforms;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.gif.GifFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.limaoso.phonevideo.broadcastreceiver.ScreenObserver;
import com.limaoso.phonevideo.broadcastreceiver.ScreenObserver.ScreenStateListener;
import com.limaoso.phonevideo.db.CacheMessgeDao;
import com.limaoso.phonevideo.p2p.P2PManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

import de.greenrobot.event.EventBus;

/**
 * 播放页面
 * 
 * @author jiang
 * 
 */

public class PlayActivity extends BaseActivity implements OnClickListener,
		OnHiddenListener, OnShownListener, OnScrollListener,
		OnItemClickListener, TextWatcher {
	public static final String PLAY_TAG = "play_tag"; // 播放标记
	private PullToRefreshListView play_process_listview;
	private HttpHelp mHttpHelp;
	private IjkVideoView vv_player;
	private String TAG = "PlayActivity";
	// 来自缓存页面
	// private boolean isComeCachePage = false;
	private TextView mTv_step_up, mTv_step_down, tv_Play_number, tv_play_speed,
			tv_play_name, tv_play_speed_porgress, tv_down_speed;
	private EditText mEtComment;
	private RelativeLayout mRl_input_layout;
	private TextView mTv_user_name, mTv_video_desc;
	private TextView mTv_user_fans_num;
	private TextView mTv_gz_btn;
	private View mLine_desc;
	private RecyclerView mRlv_video;
	public static PlayActivity instace;
	/** 当前视频的id */
	private String mVideoID;

	// 首先在您的Activity中添加如下成员变量
	// 分享
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");
	private SharePlatforms mPlatforms;

	protected View getRootView() {
		instace = this;
		P2PManager.getInstance(getApplicationContext()).stopTask();
		mHttpHelp = new HttpHelp();
		return View.inflate(UIUtils.getContext(), R.layout.page_play_process, null);
	}

	private android.app.Fragment newHandFragment;

	private void showView(int type) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		if (newHandFragment == null) {
			newHandFragment = new GifFragment(PlayActivity.this, type,
					R.raw.gif_play_top, R.raw.gif_play_buttom);
		}
		getFragmentManager().beginTransaction()
				.replace(R.id.fl_playactivity, newHandFragment).commit();
	}

	@Override
	protected void initView() {
		// TODO
		EventBus.getDefault().register(this);
		// mPage = 1;
		mVideoID = baseIntent.getStringExtra(GlobalConstant.PLAY_VIDEO_ID);
		play_show_name_layout = findViewById(R.id.play_show_name_layout);
		buffering_indicator = findViewById(R.id.buffering_indicator);
		mEtComment = (EditText) findViewById(R.id.etComment);
		mEtComment.addTextChangedListener(this);
		findViewById(R.id.btnSendComment).setOnClickListener(this);
		mRl_input_layout = (RelativeLayout) findViewById(R.id.rl_input_layout);
		play_show_name_layout.setOnClickListener(this);
		tv_play_speed = (TextView) findViewById(R.id.tv_play_speed);
		tv_down_speed = (TextView) findViewById(R.id.tv_down_speed);
		tv_play_speed_porgress = (TextView) findViewById(R.id.tv_play_speed_porgress);
		tv_play_name = (TextView) findViewById(R.id.tv_play_name);
		vv_player = (IjkVideoView) findViewById(R.id.video_view);
		vv_player.setActivity(this);
		mTv_step_down = (TextView) findViewById(R.id.tv_step_down);
		play_process_listview = (PullToRefreshListView) findViewById(R.id.play_process_listview);
		play_process_listview.setMode(Mode.PULL_FROM_END);
		initIjkVideoView();
		initHeadView();
		initDate();
	}

	private void initDate() {
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.WRAP_CONTENT);
		mHeadView.setLayoutParams(layoutParams);
		ListView lv = play_process_listview.getRefreshableView();
		lv.addHeaderView(mHeadView);
		play_process_listview.setOnItemClickListener(this);

	}

	public View mHeadView;

	/**
	 * 初始化头布局
	 */
	private void initHeadView() {
		// TODO
		mHeadView = View.inflate(mActivity, R.layout.comlv_header, null);
		mCiv_user_icon = $(mHeadView, R.id.civ_user_icon);
		mTv_user_name = $(mHeadView, R.id.tv_user_name);
		mLine_desc = $(mHeadView, R.id.line_desc);
		mTv_video_desc = $(mHeadView, R.id.tv_video_desc);

		tv_Play_number = $(mHeadView, R.id.tv_Play_number);
		mTv_gz_btn = $(mHeadView, R.id.tv_gz_btn);
		mRlv_video = $(mHeadView, R.id.rlv_video);
		mTv_step_down = $(mHeadView, R.id.tv_step_down);
		mTv_step_up = $(mHeadView, R.id.tv_step_up);
		$(mHeadView, R.id.iv_share).setOnClickListener(this);
		$(mHeadView, R.id.iv_collect).setOnClickListener(this);
		mTv_add_pl_btn = $(mHeadView, R.id.tv_add_pl_btn);
		mTv_add_pl_btn.setOnClickListener(this);
		mTv_user_fans_num = $(mHeadView, R.id.tv_user_fans_num);
		mTv_gz_btn.setOnClickListener(this);
		videoCommend_setting();
	}

	/**
	 * 设置推荐电影初始化
	 */
	private void videoCommend_setting() {
		mRlv_video.setHasFixedSize(true);
		mRlv_video.addItemDecoration(new SpaceItemDecoration(getResources()
				.getDimensionPixelSize(R.dimen.x30)));
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mRlv_video.setLayoutManager(mLayoutManager);
	}

	/**
	 * 设置RecyclerView的间距
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

		private int space;

		public SpaceItemDecoration(int space) {
			this.space = space;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view,
				RecyclerView parent, RecyclerView.State state) {

			outRect.right = space;
		}
	}

	/**
	 * 播放页面进入请求网络
	 */
	@Override
	protected void initNet() {

		if (StringUtils.isEmpty(mVideoID)) {
			return;
		}
		initNetwork(mVideoID);
		// 有用
		// mHttpHelp.sendGet(NetworkConfig.getPlayCommentUrl(mVideoID,mPage),
		// PlayCommentBaen.class, new MyRequestCallBack<PlayCommentBaen>() {
		// @Override
		// public void onSucceed(PlayCommentBaen bean) {
		// if (bean == null || bean.cont == null
		// || bean.status == null) {
		// UIUtils.showToast(mActivity, "网络异常");
		// return;
		// }
		// if ("1".equals(bean.status)) {
		// setCommentInfo(bean.cont);
		// } else {
		// UIUtils.showToast(mActivity, "网络异常");
		// }
		// }
		// });
	}

	private void initNetwork(String str) {

		mHttpHelp.sendGet(NetworkConfig.getPlayActivityUrl(str),
				PlayBaen.class, new MyRequestCallBack<PlayBaen>() {
					@Override
					public void onSucceed(PlayBaen bean) {
						if (bean == null || bean.cont == null
								|| bean.status == null) {
							UIUtils.showToast(mActivity, "网络异常");
							return;
						}
						if ("1".equals(bean.status)) {
							setUserInfo(bean.cont);
							// setPlayInfo(bean.cont);
							initShare(bean.cont.shareinfo);
							if (!PrefUtils.getBoolean(
									GlobalConstant.NOT_NEWHAND_PLAY, false)) {
								PrefUtils.setBoolean(
										GlobalConstant.NOT_NEWHAND_PLAY, true);
								showView(4);
							}
						} else {
							UIUtils.showToast(mActivity, "网络异常");
						}
					}
				});
	}

	/**
	 * 设置播放页面的一级评论
	 * 
	 * @param cont
	 */
	protected void setCommentInfo(
			List<com.aliamauri.meat.bean.PlayCommentBaen.Cont> cont) {

	}

	/** 当前是否顶踩过标记 */
	private String mIsupdown;
	/** 用户的基本信息 */
	Userinfo mUserinfo;
	/** 评论条目 */
	private List<Comlist> mComlist;

	/**
	 * 设置用户的基本信息
	 * 
	 * @param cont
	 */
	protected void setUserInfo(Cont cont) {
		if (cont == null || cont.vinfo == null) {
			return;
		}
		if ("0".equals(cont.userinfo.isguanzhu)) {
			mTv_gz_btn.setVisibility(View.VISIBLE);
			mTv_gz_btn.setSelected(true);
			mTv_gz_btn.setText("关注发布者");
		} else if ("1".equals(cont.userinfo.isguanzhu)) {
			mTv_gz_btn.setVisibility(View.VISIBLE);
			mTv_gz_btn.setSelected(false);
			mTv_gz_btn.setText("取消关注");
		} else if ("10".equals(cont.userinfo.isguanzhu)) {
			// 是自己
			mTv_gz_btn.setVisibility(View.GONE);
		} else {
			mTv_gz_btn.setVisibility(View.GONE);
		}

		mIsupdown = cont.vinfo.isupdown;
		mUserinfo = cont.userinfo;
		mTv_step_down.setText(cont.vinfo.down);
		mTv_add_pl_btn.setText("当前" + cont.vinfo.pl_num + "条评论，快来发表评论吧！");
		mTv_step_up.setText(cont.vinfo.up);
		tv_Play_number.setText(setText_color(cont.vinfo.bf_num));
		mTv_step_down.setOnClickListener(this);
		mTv_step_up.setOnClickListener(this);
		mHttpHelp.showImage(mCiv_user_icon, cont.userinfo.face);
		mCiv_user_icon.setOnClickListener(this);
		mTv_user_name.setText(cont.userinfo.nickname);
		setDestStyle(UIUtils.ToDBC(cont.vinfo.desc));
		mTv_user_fans_num.setText(getResources().getString(
				R.string.textfans_num, cont.userinfo.fansnum));
		if ("1".equals(mIsupdown)) {
			mTv_step_up.setSelected(true);
			mTv_step_down.setSelected(false);
		} else if ("2".equals(mIsupdown)) {
			mTv_step_down.setSelected(true);
			mTv_step_up.setSelected(false);
		} else {
			mTv_step_down.setSelected(false);
			mTv_step_up.setSelected(false);
		}
		mVideos = cont.videoother;
		/*
		 * 设置推荐影片的数据
		 */
		mRlv_video.setAdapter(new VideoCommendAdapter(cont.videoother) {

			@Override
			public void setClick(int position) {
				mVideoID = mVideos.get(position).id;
				initNetwork(mVideos.get(position).id);
			}

		});
		mComlist = cont.comlist;
		/*
		 * 设置评论数据
		 */
		mCommentAdapter = new CommentContentAdapter(mActivity, cont.comlist,
				R.layout.item_comment_layout);
		play_process_listview.setAdapter(mCommentAdapter);
		netStatue(cont);
	}

	/**
	 * 设置简介内容的样式
	 * 
	 * @param desc
	 */
	private void setDestStyle(String desc) {
		if (StringUtils.isEmpty(desc)) {
			mTv_video_desc.setVisibility(View.GONE);
			mLine_desc.setVisibility(View.GONE);

		} else {
			mTv_video_desc.setVisibility(View.VISIBLE);
			mLine_desc.setVisibility(View.VISIBLE);

			if (desc.length() > 50) {
				mTv_video_desc.setTag(desc);
				mTv_video_desc.setOnClickListener(this);
				mTv_video_desc.setText(Html
						.fromHtml("<font color=#464545>简介 :</font>"
								+ desc.substring(0, 35)
								+ "<font color=#30A8EC> <u>查看全部</u></font>"));
			} else {
				mTv_video_desc.setText(Html
						.fromHtml("<font color=#464545>简介 :</font>" + desc));
			}
		}
	}

	/**
	 * 没有网络 进行网络设置
	 */
	private void alertDialogNotNet() {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog alertDialog = builder.create();
		View inflate = UIUtils.inflate(R.layout.dialog_alert_net);
		alertDialog.setView(inflate);
		alertDialog.show();

	}

	/**
	 * 弹窗提示使用手机网络
	 * 
	 * @param vinfo
	 * 
	 */
	private void alertDialogPhoneNet(final Vinfo vinfo) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog alertDialog = builder.create();
		View inflate = UIUtils.inflate(R.layout.dialog_alert_net);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
		alertDialog.getWindow().setContentView(inflate);
		// 继续播放
		inflate.findViewById(R.id.tv_dialog_continue).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDialog.cancel();
						if (vinfo != null) {
							if (vinfo != null) {
								P2PManager.getInstance(getApplicationContext())
										.notifyWifi(1);

								// if (cacheInfo == null
								// || TextUtils.isEmpty(cacheInfo
								// .getTvId())) {
								// saveInfo(vinfo);
								// }
								startIjkVideoView(vinfo);
							}

						}

					}
				});
		// 取消播放
		inflate.findViewById(R.id.tv_dialog_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDialog.cancel();

					}
				});
	}

	protected void initShare(Shareinfo share) {
		Shareinfo shareinfo = share;
		if (shareinfo != null && mPlatforms == null) {
			mController.getConfig().closeToast();
			mPlatforms = new SharePlatforms(this, mController, shareinfo);

		}

	}

	/**
	 * 分享
	 */

	private void sharePlatform() {
		if (vv_player != null && vv_player.isPlaying()) {
			vv_player.pause();
		}
		if (mPlatforms != null) {
			mPlatforms.initView();
			mController.openShare(this, false);
		}

	}

	/**
	 * 关闭键盘
	 */
	private void closeSoftInput() {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
			// 隐藏软键盘
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}

	}

	/**
	 * 关闭评论输入框
	 */
	private void closeEditText() {
		mRl_input_layout.setVisibility(View.GONE);
	}

	/**
	 * 初始化播放信息
	 * 
	 * @param cont
	 */
	protected void netStatue(Cont cont) {

		switch (NetWorkUtil.getNetworkClass(PlayActivity.this)) {
		case NetWorkUtil.NETWORK_CLASS_UNAVAILABLE:
			// alertDialogNotNet();
			break;
		case NetWorkUtil.NETWORK_CLASS_2_G:
			alertDialogPhoneNet(cont.vinfo);
			break;
		case NetWorkUtil.NETWORK_CLASS_3_G:
			alertDialogPhoneNet(cont.vinfo);
			break;
		case NetWorkUtil.NETWORK_CLASS_4_G:
			alertDialogPhoneNet(cont.vinfo);
			break;
		case NetWorkUtil.NETWORK_CLASS_WIFI:
			// hash = Vinfo.lmlink;
			// CacheInfo cacheInfo = mDao.getCacheInfo(hash);
			// if (cacheInfo == null || TextUtils.isEmpty(cacheInfo.getTvId()))
			// {
			// saveInfo(vinfo);
			// }
			startIjkVideoView(cont.vinfo);
			break;
		case NetWorkUtil.NETWORK_CLASS_UNKNOWN:
			// alertDialogNotNet();
			break;

		}

	}

	/**
	 * 设置字体颜色
	 * 
	 * @param length
	 * 
	 * @param bf_num
	 * @return
	 */
	private String setText_color(String text) {
		return Html.fromHtml(
				UIUtils.ToDBC(changeNumFormat(text))
						+ "<font color=#999999> 播放</font>").toString();
	}

	/**
	 * 设置数字样式
	 * 
	 * 大于1000 直接转换”万“
	 * 
	 * @param num
	 * @return
	 */
	private String changeNumFormat(String num) {
		int nu_m = 0;
		try {
			nu_m = Integer.parseInt(num);
		} catch (Exception e) {
			nu_m = 0;
		}
		if (nu_m > 9999) {
			return nu_m / 10000 + "万";
		} else {
			return String.valueOf(nu_m);
		}
	}

	/** 当前踩操作 */
	private String down_tag = "2";
	/** 当前顶操作 */
	private String up_tag = "1";
	private boolean desc_tag = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_collect:// 转发
			gotoZF();
			break;
		case R.id.play_show_name_layout:// 播放器返回
			if (vv_player == null || mMediaController == null) {
				return;
			}
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				LogUtil.e(TAG, "onClick关闭播放器");
				stopVideoview();
				finish();
			}
			break;
		case R.id.tv_video_desc:// 显示详细的简介内容
			Object desc = v.getTag();
			if (desc instanceof String) {
				if (desc_tag) {
					desc_tag = false;
					mTv_video_desc
							.setText(Html
									.fromHtml("<font color=#464545>简介 :</font>"
											+ ((String) desc).substring(0, 35)
											+ "<font color=#30A8EC> <u>查看全部</u></font>"));
				} else {
					desc_tag = true;
					mTv_video_desc.setText(Html
							.fromHtml("<font color=#464545>简介 :</font>" + desc
									+ "<font color=#30A8EC> <u>收起</u></font>"));
				}
			}
			break;
		case R.id.tv_step_down:// 点踩
			DownOrUp_setting(down_tag);
			break;
		case R.id.civ_user_icon:// 进入个人资料
			gotoUserDate();
			break;
		case R.id.btnSendComment:// 确认发布
			publishCommend();
			break;
		case R.id.tv_add_pl_btn:// 添加评论
			addCommend();
			break;
		case R.id.tv_step_up:// 顶
			DownOrUp_setting(up_tag);
			break;

		case R.id.iv_share:// 分享
			sharePlatform();
			break;
		case R.id.tv_gz_btn:
			if (mTv_gz_btn.isSelected()) {
				mHttpHelp.sendGet(NetworkConfig.getgoFollow(mUserinfo.uid),
						BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

							@Override
							public void onSucceed(BaseBaen bean) {
								if (bean == null) {
									return;
								}
								if ("1".equals(bean.status)) {
									PrefUtils
											.setInt(GlobalConstant.USER_FOLLOWSCOUNT,
													PrefUtils
															.getInt(GlobalConstant.USER_FOLLOWSCOUNT,
																	0) + 1);
									mTv_gz_btn.setSelected(false);
									mTv_gz_btn.setText("取消关注");
								}
								UIUtils.showToast(UIUtils.getContext(),
										bean.msg);
							}
						});
			} else {
				mHttpHelp.sendGet(NetworkConfig.getdelfollows(mUserinfo.uid),
						BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

							@Override
							public void onSucceed(BaseBaen bean) {
								if (bean == null) {
									return;
								}
								if ("1".equals(bean.status)) {
									PrefUtils
											.setInt(GlobalConstant.USER_FOLLOWSCOUNT,
													PrefUtils
															.getInt(GlobalConstant.USER_FOLLOWSCOUNT,
																	0) - 1);
									mTv_gz_btn.setSelected(true);
									mTv_gz_btn.setText("关注发布者");
								}
								UIUtils.showToast(UIUtils.getContext(),
										bean.msg);
							}
						});
			}
			break;

		}

	}

	/**
	 * 转发当前视频操作
	 */
	private void gotoZF() {
		mHttpHelp.sendGet(NetworkConfig.goZF(mVideoID), BaseBaen.class,
				new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || bean.status == null
								|| bean.msg == null) {
							UIUtils.showToast(mActivity, "转发失败");

							return;
						}
						if ("1".equals(bean.status)) {
							UIUtils.showToast(mActivity, "转发成功");
						} else {
							UIUtils.showToast(mActivity, bean.msg);
						}
					}
				});

	}

	/**
	 * 进入当前用户的个人资料
	 * 
	 * @param type
	 */
	private void gotoUserDate() {
		if (StringUtils.isEmpty(mUserinfo.uid)) {
			return;
		}
		if (vv_player != null && vv_player.isPlaying()) {
			vv_player.pause();
		}
		Intent intent = null;
		if (StringUtils.isEmpty(mUserinfo.darentype)
				|| "0".equals(mUserinfo.darentype)) { // 基本用户资料页
			intent = new Intent(this, OtherDataActivity.class).putExtra(
					GlobalConstant.COMMENT_ADD_FRIEND, mUserinfo.uid);
		} else { // 达人资料页
			intent = new Intent(this, DoyenDetailActvity.class).putExtra(
					GlobalConstant.INTENT_ID, mUserinfo.uid);
		}
		startActivity(intent);

	}

	/**
	 * == 发布评论
	 */
	private void publishCommend() {
		final String Comment_text = mEtComment.getText().toString().trim();
		Editable text = mEtComment.getText();
		if (!StringUtils.isEmpty(Comment_text)) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("pid", "0");
			params.addBodyParameter("msg", Comment_text);
			params.addBodyParameter("ckuid", mVideoID);

			mHttpHelp.sendPost(NetworkConfig.commentTextUrl(), params,
					BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

						@Override
						public void onSucceed(BaseBaen bean) {
							if (bean == null || bean.status == null
									|| bean.msg == null) {
								UIUtils.showToast(mActivity, "网络异常");
								return;
							}
							if ("1".equals(bean.status)) {
								mRl_input_layout.setVisibility(View.GONE);
								KeyBoardUtils.closeKeybord(mEtComment,
										UIUtils.getContext());
								mEtComment.setText("");
								UIUtils.showToast(mActivity, bean.msg);
								Comlist list = new Comlist();
								list.face = PrefUtils.getString(
										GlobalConstant.USER_FACE,
										GlobalConstant.HEAD_ICON_PATH);
								list.time = "刚刚";
								list.replynum = "0";
								list.msg = Comment_text;
								list.nickname = PrefUtils.getString(
										GlobalConstant.USER_NICKNAME, "");
								mComlist.add(0, list);
							} else {
								UIUtils.showToast(mActivity, bean.msg);

							}

						}
					});
		}

	}

	/**
	 * 添加评论操作
	 */
	private void addCommend() {
		mRl_input_layout.setVisibility(View.VISIBLE);
		KeyBoardUtils.openKeybord(mEtComment, mActivity);

	}

	// 点击EditText以外的任何区域隐藏键盘
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN
				&& StringUtils.isEmpty(mEtComment.getText().toString().trim())) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				if (hideInputMethod(this, v)) {
					mRl_input_layout.setVisibility(View.GONE);
					return true; // 隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
				}
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// v.getLocationInWindow(1);
			int left = leftTop[0], top = leftTop[1], bottom = top
					+ v.getHeight(), right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public Boolean hideInputMethod(Context context, View v) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
		return false;
	}

	/**
	 * 设置当前操作，顶或踩
	 * 
	 * @param tag
	 * 
	 */
	private void DownOrUp_setting(final String tag) {
		String type = null;
		if (up_tag.equals(mIsupdown)) {
			UIUtils.showToast(mActivity, "已经点过顶！");
			return;
		} else if (down_tag.equals(mIsupdown)) {
			UIUtils.showToast(mActivity, "已经点过踩！");
			return;
		}
		if (up_tag.equals(tag)) { // 顶操作
			type = up_tag;
		} else {
			type = down_tag;
		}
		mHttpHelp.sendGet(NetworkConfig.getUpOrDownUrl(mVideoID, type),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {
					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || bean.status == null) {
							UIUtils.showToast(mActivity, "网络异常！");
							return;
						}
						if ("1".equals(bean.status)) {
							mIsupdown = tag;
							UIUtils.showToast(mActivity, "操作成功！");

							if (up_tag.equals(tag)) {
								mTv_step_up.setSelected(true);
								mTv_step_down.setSelected(false);
								mTv_step_up.setText(setUpload_num(mTv_step_up
										.getText().toString().trim()));
							} else if (down_tag.equals(tag)) {
								mTv_step_down.setSelected(true);
								mTv_step_up.setSelected(false);
								mTv_step_down
										.setText(setUpload_num(mTv_step_down
												.getText().toString().trim()));
							} else {
								mTv_step_down.setSelected(false);
								mTv_step_up.setSelected(false);
							}

						} else {
							UIUtils.showToast(mActivity, bean.msg);
						}
					}

					/**
					 * 设置顶踩后更新的数字
					 * 
					 * @param text
					 * @return
					 */
					private String setUpload_num(String text) {
						if (text.contains("万")) {
							return text;
						} else {
							return String.valueOf(Integer.parseInt(text) + 1);
						}
					}
				});
	}

	@Override
	public void onPause() {
		// if (vv_player != null && mbean != null) {
		// super.onPause();
		if (vv_player != null) {
			current_Play_Position = vv_player.getCurrentPosition();
			LogUtil.i(TAG, "停止播放时长 ： " + current_Play_Position);
			
		}
		// String fileHash = mbean.cont.cfilminfo.lmlink;
		// if (fileHash != null && fileHash.contains("//")) {
		// fileHash = fileHash.substring(fileHash.indexOf("//") + 2);
		// // }
		// ContentValues values = new ContentValues();
		// values.put(CacheMessgeDao.COLUMN_TV_PLAY_END_TIME,
		// vv_player.getDuration());
		// values.put(CacheMessgeDao.COLUMN_TV_PLAY_POSITION,
		// current_Play_Position);
		// mDao.updateMessage(fileHash, values);
		// }
		super.onPause();
		MobclickAgent.onPause(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

	}

	@Override
	protected void onStop() {
		if (mHttpHelp != null) {
			mHttpHelp.stopHttpNET();
		}

		super.onStop();
	}

	private boolean isVideoviewStop = false;

	private void stopVideoview() {
		if (isVideoviewStop) {
			return;
		}
		LogUtil.e(TAG, "stopVideoview-------");
		isVideoviewStop = true;
		if (vv_player != null) {

			vv_player.stopPlayback();
			vv_player.release(true);
			vv_player.stopBackgroundPlay();
			vv_player.destroyDrawingCache();
			vv_player.closeCustomParmeter();
			IjkMediaPlayer.native_profileEnd();
			if (lmp2pMediaPlayer != null) {
				lmp2pMediaPlayer.finalize(hash);
				lmp2pMediaPlayer = null;
			}
			vv_player = null;
		}
		// else {
		// vv_player.enterBackground();
		// }

	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		// if (vv_player != null && mbean != null) {
		// String fileHash = mbean.cont.cfilminfo.lmlink;
		// P2PManager.getInstance().deleteFile(fileHash);
		// }
		// if (NetWorkUtil.isWifiAvailable(this)) {
		// P2PManager.getInstance().startTask();
		// } else {
		//
		// P2PManager.getInstance().notifyWifi(0);
		// }

		clear();
		if (!TextUtils.isEmpty(hash)) {
			P2PManager.getInstance(getApplicationContext()).deleteFile(hash);

		}
		instace = null;
//		stopVideoview();
		super.onDestroy();
	}

	public void onEventMainThread(PlayVideo_event event) {
		switch (event.getTag()) {
		case PLAY_TAG:
			if (vv_player != null && !vv_player.isPlaying()) {
				vv_player.start();
			}
			break;

		default:
			break;
		}

	}

	/**
	 * 退出页面，清除
	 */
	private void clear() {
		if (vv_player != null) {
			// final String fileHash = vv_player.getFileHash();
			// vv_player.stopPlayback();
			// vv_player.destroyDrawingCache();
			// vv_player.unregisterReceiver();

			// if (!TextUtils.isEmpty(fileHash)) {
			// new Thread(new Runnable() {
			// @Override
			// public void run() {
			// PlayManager.cancelDownload(fileHash);
			// PlayManager.clearListenersOnActivityDestroy(fileHash);
			// }
			// }).start();
			// }

			// vv_player = null;
		}

		if (mObserver != null) {
			mObserver.stopScreenStateUpdate();
		}
	}

	/**
	 * 初始化播放器
	 */
	private void initIjkVideoView() {
		IjkMediaPlayer.loadLibrariesOnce(null);
		IjkMediaPlayer.native_profileBegin("libijkplayer.so");
		mMediaController = new MediaController(this);
		vv_player.setMediaBufferingIndicator(buffering_indicator);
		mMediaController.setOnHiddenListener(this);
		mMediaController.setOnShownListener(this);
		vv_player.setMediaController(mMediaController);
		vv_player.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(IMediaPlayer mp, int what, int extra) {
				return true;
			}
		});

	}

	private Lmp2pMediaPlayer lmp2pMediaPlayer;

	/**
	 * 开始播放
	 * 
	 * @param vinfo
	 * 
	 * @param hash
	 */
	private String hash = null;

	private void startIjkVideoView(Vinfo vinfo) {

		if (vv_player == null || vinfo == null) {
			LogUtil.e(TAG, "播放电影信息为null ");
			return;
		}
		LogUtil.e(TAG, "电影名称 ： " + vinfo.name);
		tv_play_name.setText(vinfo.name);

		// vt 1 是http 2 是hash
		String vt = vinfo.vt;
		if ("1".equals(vt)) {
			String url = vinfo.url;
			if (TextUtils.isEmpty(url)) {
				return;
			}
			vv_player.setVideoPath(url);
			vv_player.start();

		} else if ("2".equals(vt)) {
			String lmhash = vinfo.lmhash;
			if (TextUtils.isEmpty(lmhash)) {
				return;
			}
			lmp2pMediaPlayer = new Lmp2pMediaPlayer();
			hash = lmhash;
			if (tv_play_speed_porgress != null) {
				lmp2pMediaPlayer.setPorgressShow(tv_play_speed_porgress);
			}
			lmp2pMediaPlayer.setLMPlayHash(hash, vv_player);
//			if (LogUtil.getDeBugState()) {
//				P2PManager.getInstance(getApplicationContext()).addQureyTask(
//						new Runnable() {
//
//							@Override
//							public void run() {
//								runOnUiThread(new Runnable() {
//
//									@SuppressWarnings("static-access")
//									@Override
//									public void run() {
//										if (tv_down_speed != null) {
//											tv_down_speed.setText("下载速度："
//													+ lmp2pMediaPlayer
//															._getP2pDownloadSpeed(hash));
//										}
//
//									}
//								});
//							}
//						});
//			}

		}
		screenObserver();
		// if ("1".equals(vt)) {
		// // vv_player.setLocalPlayMode();
		// LogUtil.e(TAG, "电影名称 ： " + vinfo.name);
		// tv_play_name.setText(vinfo.name);
		// Lmp2pMediaPlayer lmp2pMediaPlayer = new Lmp2pMediaPlayer();
		// hash = "limaoso://btih:3413683b4377ab021e8bed1ae9fc1fd48667751a";
		// lmp2pMediaPlayer.setLMPlayHash(hash, vv_player);
		// screenObserver();
		// return;
		// }
		//
		// if (tv_play_name != null && mDao != null) {
		// CacheInfo cacheInfo = mDao.getCacheInfo(hash);
		// if (cacheInfo != null) {
		// LogUtil.e(TAG, "电影名称 ： " + cacheInfo.getTvName());
		// tv_play_name.setText(cacheInfo.getTvName());
		// current_Play_Position = cacheInfo.getTvPlayPosition();
		// }
		// }
		// // if (vv_player != null) {
		// // vv_player.setFileHash(hash);
		// // }
		// // LogUtil.e(TAG, "电影hash ： " + vinfo);
		// LogUtil.e(TAG, "电影已经播放时长 ： " + current_Play_Position);
		// PlayManager.prepareToPlay(getApplicationContext(), vv_player, hash,
		// "mp4", current_Play_Position, new OnPreparedListener() {
		//
		// @Override
		// public void onPrepared(String fileHash, String path,
		// int progress) {
		// if (vv_player != null) {
		// // vv_player.setLimaoPlayMode();
		// vv_player.setVideoPath(path);
		// // vv_player.seekTo(current_Play_Position);
		// vv_player.start();
		// screenObserver();
		// // if (vv_player.mMediaBufferingIndicator != null) {
		// //
		// // vv_player.mMediaBufferingIndicator
		// // .setVisibility(View.GONE);
		// // }
		// }
		//
		// }
		//
		// @Override
		// public void onDownloadFailed(String hash, int state,
		// long fileSize) {
		//
		// if (fileSize > SDUtils.getCacheAvailableSize()) {
		// Intent intent = new Intent(PlayActivity.this,
		// AlertDialogActivity.class);
		// PlayActivity.this.startActivity(intent);
		// }
		//
		// }
		// });

	}

	private boolean iscolor;

	/**
	 * 处理下载速度
	 */
	public long old_KB = 0;
	public long new_KB = 0;

	/**
	 * 注册屏幕关闭监听
	 */
	protected void screenObserver() {
		if (mObserver == null) {
			mObserver = new ScreenObserver(PlayActivity.this);
		}
		mObserver.requestScreenStateUpdate(new ScreenStateListener() {

			@Override
			public void onScreenOn() {
				// 屏幕开启时
				LogUtil.e(TAG, "-------------屏幕开启时-----------");
				if (vv_player != null && !vv_player.isPlaying()) {
					vv_player.start();
				}
			}

			@Override
			public void onScreenOff() {
				// 屏幕关闭时
				LogUtil.e(TAG, "-------------屏幕关闭时-----------");
				if (vv_player != null && vv_player.isPlaying()) {
					vv_player.pause();
				}
			}
		});

	}

	/**
	 * 保存下载信息 Cfilminfo cfilminfo
	 * 
	 * @param cfilmlist
	 */
	// private void saveInfo(Cfilminfo cfilminfo) {
	// if (cfilminfo == null) {
	// return;
	// }
	// CacheInfo info = new CacheInfo();
	// String lmlinkurl = cfilminfo.lmlink;
	// if (TextUtils.isEmpty(lmlinkurl)) {
	// return;
	// }
	// // TODO tv_id
	// info.setTvId("tv_id");
	// // TODO current_play_position_num
	// info.setTvPlaynum("current_play_position_num" + "");
	// info.setTvHash(lmlinkurl);
	// info.setTvName(cfilminfo.name);
	// info.setTvPicPath(cfilminfo.pic);
	// info.setTvDownFileSize(cfilminfo.filesize);
	// mDao.saveMessage(info);
	// }

	private View play_show_name_layout;
	private MediaController mMediaController;
	// 当前播放的位置
	private long current_Play_Position;
	private View buffering_indicator;
	private CacheMessgeDao mDao;
	private ScreenObserver mObserver;
	private CircleImageView mCiv_user_icon;
	private CommentContentAdapter mCommentAdapter;
	private TextView mTv_add_pl_btn;
	private List<Videoother> mVideos;

	// 获取总的接受字节数，包含Mobile和WiFi等

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (vv_player == null) {
			return;
		}
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

			closeEditText();
			landscapeView();// 横
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			portraitView();// 竖
		}

	}

	public void portraitView() {
		if (vv_player != null) {
			showStatusBar(false, this);
			vv_player
					.setConfigurationChanged(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

	}

	public void landscapeView() {
		if (vv_player != null) {
			showStatusBar(true, this);
			vv_player
					.setConfigurationChanged(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	/**
	 * 去掉状态栏
	 * 
	 * @param enable
	 * @param activity
	 */
	public void showStatusBar(boolean enable, Activity activity) {

		if (enable) {
			WindowManager.LayoutParams lp = activity.getWindow()
					.getAttributes();
			lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			activity.getWindow().setAttributes(lp);
			activity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		} else {
			WindowManager.LayoutParams attr = activity.getWindow()
					.getAttributes();
			attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			activity.getWindow().setAttributes(attr);
			activity.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}

	/**
	 * 播放器控制面板显示
	 */
	@Override
	public void onHidden() {
		if (play_show_name_layout != null) {
			play_show_name_layout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onShown() {
		if (play_show_name_layout != null) {
			play_show_name_layout.setVisibility(View.VISIBLE);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (vv_player != null
					&& this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				// return true;
			} else {
				LogUtil.e(TAG, "onKeyDown关闭播放器");
				stopVideoview();
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		closeEditText();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	private int mCurrentPosition;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 1) {
			return;
		}
		if (StringUtils.isEmpty(mComlist.get(position - 2).id)) {
			return;
		}
		mCurrentPosition = position;
		Intent intent = new Intent(UIUtils.getContext(), PlayCommentActivity.class);
		intent.putExtra(GlobalConstant.COMMENTDATAURL,
				mComlist.get(position - 2).id);
		intent.putExtra(GlobalConstant.PLAY_VIDEO_ID, mVideoID);
		startActivity(intent);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable editable) {
		// 屏蔽第三方输入法中表情图标
		int index = mEtComment.getSelectionStart() - 1;
		if (index > 0) {
			if (isEmojiCharacter(editable.charAt(index))) {
				Editable edit = mEtComment.getText();
				edit.delete(index, index + 1);
			}
		}

	}

	/**
	 * 判断字符是否为表情
	 */
	private boolean isEmojiCharacter(char codePoint) {
		return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
				|| (codePoint == 0xD) || (codePoint >= 0x20 && codePoint <= 0xD7FF))
				|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}
	// public void onEventMainThread(PlayVideo_event event) {
	// switch (event.getTag()) {
	// case PlayCommentActivity.update_num_tag:
	// mComlist.get(mCurrentPosition).replynum =
	// String.valueOf(Integer.parseInt(mComlist.get(mCurrentPosition).replynum)+1);
	// mCommentAdapter.notifyDataSetChanged();
	// break;
	//
	// default:
	// break;
	// }
	//
	// }

}
