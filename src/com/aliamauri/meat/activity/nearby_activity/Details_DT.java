package com.aliamauri.meat.activity.nearby_activity;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.activity.MyDataActivity;
import com.aliamauri.meat.activity.OtherDataActivity;
import com.aliamauri.meat.activity.IM.activity.ShowVideoActivity;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.DetailsDtBean;
import com.aliamauri.meat.bean.DetailsDtBean.Cont.Commentlist;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist.Imgs;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist.Videos;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist.Voices;
import com.aliamauri.meat.db.Dynamic_db.DynamicShowDao;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.eventBus.RefurbishDTItem;
import com.aliamauri.meat.eventBus.UpdateShowMsg;
import com.aliamauri.meat.eventBus.sendDownLoadedOrder;
import com.aliamauri.meat.fragment.DynamicUtlis;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.listener.VoicePlay;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.play.PlaySourceActivity;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.MyBDmapUtlis;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.FlowLayout;
import com.aliamauri.meat.view.TextViewSpanClickable.CommentModel;
import com.aliamauri.meat.view.TextViewSpanClickable.CommentTextView;
import com.aliamauri.meat.view.TextViewSpanClickable.TextBlankClickListener;
import com.aliamauri.meat.weight.MyDialog;
import com.baidu.mapapi.model.LatLng;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 附近----动态----动态详情页
 * 
 * @author limaokeji-windosc
 * 
 * 
 *         思路： 一，从3个标题页面可以跳入动态详情页，， 最新动态，最热动态，朋友圈， 其中最新动态，和最热动态中
 *         可有包含，好友，陌生人，自己的动态，在朋友圈中只能有，我和好友的动态， 二，动态详情页分为3种状态，好友，自己，陌生人的状态。
 * 
 *         好友：标题右边按钮具有“屏蔽Ta”按钮 自己：标题右边按钮具有“删除”自己动态按钮 陌生人：标题右边按钮具有弹出对话框的功能，
 *         子功能：举报 私聊 取消
 */

public class Details_DT extends Activity implements OnClickListener,
		OnTouchListener {
	private final String TAG = "Details_DT";
	private final String USER_DYNAMIC = "1"; // 用户发布的动态
	private final String USER_TRANSMIT = "2"; // 用户转发的动态
	private final String USER_DYNAMIC_VIDEO = "3"; // 搜索触发动态
	private final String SHIELDTA_TAG = "屏蔽Ta";
	private final String FOLLOWTA_TAG = "关注Ta";
	private static final String TITLE_NAME = "动态详情";
	private static final String DT_FRIED_TAG = "2"; // 朋友动态标识符
	private static final String DT_MY_TAG = "1"; // 自己动态标识符
	private static final String DT_ANONYMOUS_TAG = "0"; // 陌生人标识符
	private ImageView mIv_title_backicon; // 标题头左边回退按钮
	private ListView mLv_details_dt_content; // 展示用户回复的条目
	private String mCurrentItem_id; // 展示界面的传入的条目id
	private String mOther_uid; // 当前动态的用户的id
	private boolean mIsopen_comment_layout;// 判断当前情况是否直接弹出输入评论布局
	private TextView mTv_title_right;
	private ImageView mIv_title_righticon;
	private AlertDialog dialog;
	private MyBaseAdapter mAdapter; // listview的适配器
	private View headView; // listView的头布局
	private HttpHelp mHttpHelp;
	private FrameLayout mFl_loading;
	private String mUrl; // url
	private String mWd; // 伟度
	private String mJd; // 经度
	private Tlist mUserdetail; // 该条动态
	private ImageView mIm_mydynamic_show_more; // 按钮----更多操作
	private CircleImageView mCi_dynamicitem_headicon; // 用户名头像
	private TextView mTv_mydynamic_username;// 用户名
	private TextView mTv_dynamic_item_introduce;// 该条动态的文字状态
	private TextView mTv_dynamic_current_time;// 当前时间
	private ImageView mIv_dynamic_video_icon;// 视频预览图
	private ImageView mIv_dynamic_video_play_icon; // 视频播放按钮
	private TextView mTv_dynamic_distance;// 距离s
	private TextView mTv_dynamic_retransmission;// 转发
	private TextView mTv_dynamic_appraise;// 评价
	private TextView mTv_dynamic_praise;// 点赞
	private TextView mTv_dynamic_read; // 阅读
	private FrameLayout mFl_content_layout; // 用户上传视频布局
	private TextView mTv_dynamic_item_video;// 用户发送的语音时常
	private GridView mGv_photo_album;// 展示照片
	private RelativeLayout mRl_item_video;// 视频的布局
	private LinearLayout mLl_transmit_layout;// 转发状态背景布局
	private TextView mTv_transmit_item_introduce;// 转发状态----- 文字
	private TextView mTv_transmit_item_video; // 转发状态------ 语音
	private LinearLayout mLl_transmit_title_layout;// 转发状态的时候我的语音或者文字布局
	private TextView mTv_transmit_name;// 转发状态转发人的名字
	private FlowLayout mFl_details_head_view_tag;// 显示用户设置的标签
	private int height; // 屏幕的高度
	private int width; // 屏幕的宽度
	private String mUser_Id; // 本机用户的uID
	private String mUser_nickname;// 本机用户的名称
	private TextView mTv_details_fasong; // 回复评论确认按钮
	private RelativeLayout mRl_details; // 回复评论布局
	private EditText mEt_details_import_text; // 回复评论
	private boolean mIsShield_tag ;//屏蔽/关注好友标记
	@Override
	protected void onResume() {
		super.onResume();
		  MobclickAgent.onResume(this);
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置该activity的背景颜色
		this.getWindow().setBackgroundDrawableResource(R.color.bg_white);

		setContentView(R.layout.activity_details);
		EventBus.getDefault().register(this);
		mHttpHelp = new HttpHelp();
		isComment_notifi = true;
		mIsShield_tag = true;
		mVoicePlay = new VoicePlay();
		WindowManager ss = (WindowManager) Details_DT.this
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		ss.getDefaultDisplay().getMetrics(dm);
		height = dm.heightPixels;
		width = dm.widthPixels;
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION,
				"0&&0").split("&&");
		mWd = String.valueOf(location[0]);
		mJd = String.valueOf(location[location.length - 1]);
		mCurrentItem_id = getIntent().getStringExtra(
				GlobalConstant.DT_CURRENT_DATA_NAME);
		mOther_uid = getIntent().getStringExtra(
				GlobalConstant.DT_CURRENT_DATA_UID);
		mIsopen_comment_layout = getIntent().getBooleanExtra(
				GlobalConstant.DT_ISOPEN_COMMENT_LAYOUT, false);
		mUser_Id = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_ID, "");
		mUser_nickname = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_NICKNAME, "");
		initView();
		setTitleStyle();
		initNet();

	}

	/**
	 * 设置当前头布局的样式
	 */
	private void setTitleStyle() {

		if (mUser_Id != null && mUser_Id.equals(mOther_uid)) {// 当前是自己的动态
			getCurrentState(DT_MY_TAG);
			return;
		}
		// 当前是好友的动态
		Map<String, User> allUid = ContactManager.getInstance()
				.getAllContactUidMap();
		for (Map.Entry<String, User> entry : allUid.entrySet()) {
			if (entry != null && entry.getKey() != null) {
				String key = entry.getKey().toString();
				if (key != null && key.equals(mOther_uid)) {
					getCurrentState(DT_FRIED_TAG);
					return;
				}
			}
		}
		getCurrentState(DT_ANONYMOUS_TAG);// 当前是陌生人的动态
	}

	/**
	 * 设置是否屏蔽当前好友
	 */
	public void setIsShieldFriend() {
		mIsShield_tag = !mIsShield_tag;
		if(mIsShield_tag){  //关注操作
			setFriendAction(NetworkConfig.SHIELDNO_TAG);
		}else{              //屏蔽操作
			setFriendAction(NetworkConfig.SHIELDYES_TAG);
		}
	}
	/**
	 * 屏蔽好友按钮
	 */
	private void setFriendAction(final String action) {
		if(mUserdetail == null){
			return ;
		}
		mHttpHelp.sendGet(NetworkConfig.getShieldFriend(mUserdetail.uid,action),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || bean.status == null
								|| bean.msg == null) {
							UIUtils.showToast(getApplicationContext(), "修改失败!");
							return;
						}
						
						if("1".equals(bean.status)){
							if(mIsShield_tag){  //关注操作
								UIUtils.showToast(getApplicationContext(), bean.msg);
								mTv_title_right.setText(SHIELDTA_TAG);
							}else{              //屏蔽操作
								UIUtils.showToast(getApplicationContext(), bean.msg);
								mTv_title_right.setText(FOLLOWTA_TAG);
								EventBus.getDefault().post(new RefurbishDTItem(mOther_uid ,GlobalConstant.SHIELD_TAG));  //该uid的动态本地全部删除
							}
						}else{
							UIUtils.showToast(getApplicationContext(), bean.msg);
						}
						
					}
				});
	}

	/**
	 * /陌生人动态详情扩展功能---私聊
	 */
	private void setNiMing_sl() {
		if (mUserdetail.uid != null) {
			dialog.dismiss();
			CmdManager.getInstance().addContact(mUserdetail.uid, "",
					new CmdManagerCallBack() {
						@Override
						public void onState(boolean isSucceed) {
							if (isSucceed) {
								UIUtils.showToast(getApplicationContext(),
										"发送请求成功~~");
							} else {
								UIUtils.showToast(getApplicationContext(),
										"请求失败~~");
							}

						}
					});
		}
	}

	/**
	 * 陌生人动态详情扩展功能---举报
	 */
	private void setNiMing_jb() {
		UIUtils.showToast(this, "举报成功");
		dialog.dismiss();
	}

	/**
	 * 删除自己评论的条目(网络请求刷新)
	 * 
	 * @param position
	 */
	protected void dialog_delete_comment(final int position) {
		 mHttpHelp.sendGet(NetworkConfig.getDeleteComment(mCommentlist.get(position).id), BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

				@Override
				public void onSucceed(BaseBaen bean) {
					if(bean ==null || bean.msg ==null || bean.status ==null ){
						return ;
					}
					if("1".equals(bean.status)){
						UIUtils.showToast(getApplicationContext(), "删除成功");
						mCommentlist.remove(position);
						mAdapter.notifyDataSetChanged();
					}else{
						UIUtils.showToast(getApplicationContext(), bean.msg);
					}
				}
			});

		
	}

	private ArrayList<Commentlist> mCommentlist; // 所有评论的集合
	public boolean isComment_notifi; // 当前开关用于判断用户是否确认提交评论，刷新评论数据，初次通过，自关闭方法，，防止多次addHeadView；

	/**
	 * 初始化数据
	 */
	private void initNet() {
		if (StringUtils.isEmpty(mCurrentItem_id)) {
			return;
		}
		mUrl = NetworkConfig.getDetails_dt_url(mCurrentItem_id);

		mHttpHelp.sendGet(mUrl, DetailsDtBean.class,
				new MyRequestCallBack<DetailsDtBean>() {

					@Override
					public void onSucceed(DetailsDtBean bean) {
						if(mFl_loading.getVisibility() != View.GONE){
							mFl_loading.setVisibility(View.GONE);
						}
						if(bean != null && "2".equals(bean.status)){
							UIUtils.showToast(getApplicationContext(), "该条动态已被删除~");
							EventBus.getDefault().post(new RefurbishDTItem(mCurrentItem_id,GlobalConstant.DELETE_TAG));
							finish();
							return ;
						}
						if (bean == null || bean.cont == null) {
							UIUtils.showToast(getApplicationContext(), "网络异常");
							return;
						}
						if("1".equals(bean.status)){
							mUserdetail = bean.cont.latestdetail;
							mCommentlist = bean.cont.commentlist;

							if (isComment_notifi) {
								isComment_notifi = false;

								headView = UIUtils
										.inflate(R.layout.details_dt_head_view);
								initHeadView(headView);
								setHeadView();
								mLv_details_dt_content.addHeaderView(headView);
								if (mIsopen_comment_layout) {
									isComment = true;
									popup_button_message(null);
								}
								mAdapter = new MyBaseAdapter();
								mLv_details_dt_content.setAdapter(mAdapter);
							} else {
								mAdapter.notifyDataSetChanged();
							}
						}
					}
				});
	}

	/**
	 * 设置头布局的样式
	 * 
	 * @param headView2
	 */
	protected void setHeadView() {
		mIv_dynamic_video_icon.setOnClickListener(this);
		mIm_mydynamic_show_more.setOnClickListener(this);
		setTagStyle();
		mTv_dynamic_read.setText(mUserdetail.yd
				+ getTitleName(R.string.text_dynamic_read));
		mTv_dynamic_retransmission.setText(mUserdetail.zf
				+ getTitleName(R.string.text_dynamic_retransmission));
		mTv_dynamic_appraise.setText(mUserdetail.pj
				+ getTitleName(R.string.text_dynamic_appraise));
		mTv_dynamic_praise.setText(mUserdetail.dz
				+ getTitleName(R.string.text_dynamic_praise));
		mTv_mydynamic_username.setText(mUserdetail.nickname);
		mTv_dynamic_current_time.setText(mUserdetail.createtime);
		mHttpHelp.showImage(mCi_dynamicitem_headicon, mUserdetail.face + "##");
		mTv_dynamic_distance.setText(new MyBDmapUtlis().getCurrentDistance(
				new LatLng(Double.valueOf(mUserdetail.wd), Double.valueOf(mUserdetail.jd)),
				new LatLng(Double.valueOf(mWd), Double.valueOf(mJd))));
		mCi_dynamicitem_headicon.setOnClickListener(this);

		switch (mUserdetail.type) {
		case USER_DYNAMIC:
		case USER_DYNAMIC_VIDEO:
			setUser_dynamic();
			break;
		case USER_TRANSMIT:
			setuser_transmit();
			break;

		default:
			break;
		}
	}

	/**
	 * 设置标签样式。。
	 */
	private void setTagStyle() {
		if (mUserdetail.tags != null && mUserdetail.tags.length() >= 1) {
			mFl_details_head_view_tag.setVisibility(View.VISIBLE);
			String[] userTags = mUserdetail.tags.split("\\|\\|\\|");

			LayoutInflater from = LayoutInflater.from(this);
			for (int i = 0; i < userTags.length; i++) {
				TextView tv = (TextView) from.inflate(R.layout.fl_text,
						mFl_details_head_view_tag, false);
				tv.setText(userTags[i]);
				mFl_details_head_view_tag.addView(tv);
			}
		} else {
			mFl_details_head_view_tag.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置用户的动态样式
	 * 
	 * @param position
	 * @param dt
	 * @param tlist
	 */
	private void setUser_dynamic() {
		mLl_transmit_title_layout.setVisibility(View.GONE);

		showTextOrVoice(mTv_dynamic_item_introduce, mTv_dynamic_item_video,
				mUserdetail.voices, mUserdetail.infos);
		showPhotoOrVideo(mUserdetail.videos, mUserdetail.imgs);
		mTv_transmit_name.setVisibility(View.GONE);
		mLl_transmit_layout.setBackgroundColor(Details_DT.this.getResources()
				.getColor(R.color.bg_white));
		mLl_transmit_layout.setPadding(0, 0, 0, 0);
	}

	/**
	 * 设置用户转发动态的样式
	 * 
	 * @param position
	 * @param dt
	 * @param tlist
	 */
	private void setuser_transmit() {
		if (mUserdetail.zfinfox == null) {
			return;
		}
		mLl_transmit_title_layout.setVisibility(View.VISIBLE);
		showTextOrVoice(mTv_transmit_item_introduce, mTv_transmit_item_video,
				mUserdetail.voices, mUserdetail.infos);
		mLl_transmit_layout.setBackgroundColor(Details_DT.this.getResources()
				.getColor(R.color.bg_greywhite));
		mLl_transmit_layout.setPadding(width * 20 / 720, height * 10 / 1280,
				width * 20 / 720, height * 20 / 1280);
		mTv_transmit_name.setVisibility(View.VISIBLE);
		mTv_transmit_name.setText("转发: " + mUserdetail.zfinfox.fnickname
				+ "的动态");
		mTv_transmit_name.setText(Html.fromHtml("转发: <font color=\"#f95c71\">"
				+ mUserdetail.zfinfox.fnickname + "</font> 的动态"));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		if (mUserdetail.zfinfox.voices != null
				&& mUserdetail.zfinfox.voices.size() > 0) {
			lp.setMargins(0, 0, 0, height * 10 / 1280);
			mTv_transmit_name.setLayoutParams(lp);
		} else {
			lp.setMargins(0, 0, 0, height * 1 / 1280);
			mTv_transmit_name.setLayoutParams(lp);
		}

		showTextOrVoice(mTv_dynamic_item_introduce, mTv_dynamic_item_video,
				mUserdetail.zfinfox.voices, mUserdetail.zfinfox.infos);
		showPhotoOrVideo(mUserdetail.zfinfox.videos, mUserdetail.zfinfox.imgs);
	}

	/**
	 * 根据当前情况控制显示 图片或者视频动态 图片和视频动态同时存在，全部展示出来
	 * 
	 * @param position
	 * @param dt
	 * @param imgs
	 * @param videos
	 * @param tlist
	 */
	private void showPhotoOrVideo(ArrayList<Videos> videos, ArrayList<Imgs> imgs) {

		if (videos != null && videos.size() > 0 && imgs != null
				&& imgs.size() > 0) {// 图片和视频动态同时存在都显示
			mRl_item_video.setVisibility(View.VISIBLE);
			mGv_photo_album.setVisibility(View.VISIBLE);
			mHttpHelp.showImage(mIv_dynamic_video_icon, videos.get(0).srcbg);
			mGv_photo_album.setAdapter(new MyImgsAdapter(imgs));
		} else if (videos != null && videos.size() > 0) { // 只有视频的情况下
			mRl_item_video.setVisibility(View.VISIBLE);
			mGv_photo_album.setVisibility(View.GONE);
			mHttpHelp.showImage(mIv_dynamic_video_icon, videos.get(0).srcbg);
		} else if (imgs != null && imgs.size() > 0) {// 只有照片的情况下
			mRl_item_video.setVisibility(View.GONE);
			mGv_photo_album.setVisibility(View.VISIBLE);
			mGv_photo_album.setAdapter(new MyImgsAdapter(imgs));
		} else { // 两者都没有的情况下
			mRl_item_video.setVisibility(View.GONE);
			mGv_photo_album.setVisibility(View.GONE);
		}

	}

	/**
	 * 设置相册中的图片展示
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyImgsAdapter extends BaseAdapter {

		private ArrayList<Imgs> imgs;

		public MyImgsAdapter(ArrayList<Imgs> imgs) {
			this.imgs = imgs;
		}

		@Override
		public int getCount() {

			return imgs.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = View.inflate(Details_DT.this, R.layout.item_imgs_gv,
					null);
			ImageView iv = $(view, R.id.iv_dt_img);
			mHttpHelp.showImage(iv, imgs.get(position).img);
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ArrayList<String> imgoris = new ArrayList<>();
					imgoris.clear();
					for (int i = 0; i < imgs.size(); i++) {
						imgoris.add(imgs.get(i).imgori);
					}
					Intent intent = new Intent(Details_DT.this,
							ShowDetailsImages.class);
					intent.putStringArrayListExtra(
							GlobalConstant.SHOW_DETAILS_IMAGES, imgoris);
					intent.putExtra(GlobalConstant.SHOW_IMAGE_POSITION,
							position);
					startActivity(intent);
				}
			});
			return view;
		}

	}

	@Override
	protected void onDestroy() {
		if (mHttpHelp != null) {
			mHttpHelp.stopHttpNET();
		}
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	/**
	 * 根据当前情况控制显示 文字或者语音动态 若语音和文字动态同时存在，显示在动态详情页在全部展示出来
	 * 
	 * @param position
	 * @param introduce
	 * @param video
	 * @param dt
	 * @param voices
	 * @param infos
	 */

	private void showTextOrVoice(TextView introduce, TextView video,
			ArrayList<Voices> voices, String infos) {

		if (voices != null && voices.size() > 0 && !StringUtils.isEmpty(infos)) {// if语音和文字同时存在，同时显示
			introduce.setVisibility(View.VISIBLE);
			video.setVisibility(View.VISIBLE);
			video.setText(StringUtils.secToTime((int) Float.parseFloat(voices
					.get(0).sc)));
			// 设置音频条的长度
			UIUtils.setVideoShape(video, voices.get(0).sc, Details_DT.this);
			video.setOnClickListener(this);
			introduce.setText(infos);
			// ************动态设置文字和语音的间距*********************
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					MarginLayoutParams.MATCH_PARENT,
					MarginLayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 0, height * 5 / 1280);
			introduce.setLayoutParams(params);
			// ************动态设置文字和语音的间距*********************
		} else if (voices != null && voices.size() > 0) {// 如果只有语音状态下显示语音数据
			introduce.setVisibility(View.GONE);
			video.setVisibility(View.VISIBLE);
			video.setText(StringUtils.secToTime((int) Float.parseFloat(voices
					.get(0).sc)));
			// 设置音频条的长度
			UIUtils.setVideoShape(video, voices.get(0).sc, Details_DT.this);
			video.setOnClickListener(this);
		} else if (!StringUtils.isEmpty(infos)) { // 只有文字状态下显示文字数据
			introduce.setVisibility(View.VISIBLE);
			video.setVisibility(View.GONE);
			introduce.setText(infos);
		} else { // 语音和文字都没有的情况下， 都不显示
			introduce.setVisibility(View.GONE);
			video.setVisibility(View.GONE);
		}

	}

	/**
	 * 初始化头布局控件
	 * 
	 * @param hv
	 */
	private void initHeadView(View hv) {
		mIm_mydynamic_show_more = $(hv, R.id.im_mydynamic_show_more);
		mCi_dynamicitem_headicon = $(hv, R.id.ci_dynamicitem_headicon);
		mTv_mydynamic_username = $(hv, R.id.tv_mydynamic_username);
		mTv_dynamic_item_introduce = $(hv, R.id.tv_dynamic_item_introduce);
		mTv_dynamic_current_time = $(hv, R.id.tv_dynamic_current_time);
		mIv_dynamic_video_icon = $(hv, R.id.iv_dynamic_video_icon);
		mIv_dynamic_video_play_icon = $(hv, R.id.iv_dynamic_video_play_icon);
		mTv_dynamic_distance = $(hv, R.id.tv_dynamic_distance);
		mTv_dynamic_retransmission = $(hv, R.id.tv_dynamic_retransmission);
		mTv_dynamic_appraise = $(hv, R.id.tv_dynamic_appraise);
		mTv_dynamic_praise = $(hv, R.id.tv_dynamic_praise);
		mTv_dynamic_read = $(hv, R.id.tv_dynamic_read);
		mFl_content_layout = $(hv, R.id.fl_content_layout);
		mTv_dynamic_item_video = $(hv, R.id.tv_dynamic_item_video);
		mGv_photo_album = $(hv, R.id.gv_photo_album);
		mRl_item_video = $(hv, R.id.rl_item_video);
		mLl_transmit_layout = $(hv, R.id.ll_transmit_layout);
		mTv_transmit_item_introduce = $(hv, R.id.tv_transmit_item_introduce);
		mTv_transmit_item_video = $(hv, R.id.tv_transmit_item_video);
		mLl_transmit_title_layout = $(hv, R.id.ll_transmit_title_layout);
		mTv_transmit_name = $(hv, R.id.tv_transmit_name);
		mFl_details_head_view_tag = $(hv, R.id.fl_details_head_view_tag);
	}

	/**
	 * 初始化标题布局控件
	 */
	private void initView() {
		mFl_loading = $(R.id.fl_loading);
		mTv_details_fasong = $(R.id.tv_details_fasong);
		mRl_details = $(R.id.rl_details);
		mEt_details_import_text = $(R.id.et_details_import_text);
		mIv_title_backicon = $(R.id.iv_title_backicon);
		((TextView) $(R.id.tv_title_title)).setText(TITLE_NAME);
		$(R.id.ll_title_talk).setVisibility(View.GONE);
		mTv_title_right = $(R.id.tv_title_right);
		mIv_title_righticon = $(R.id.iv_title_righticon);
		mLv_details_dt_content = $(R.id.lv_details_dt_content);
		mTv_title_right.setText(getResources().getString(
				R.string.title_name_shield_ta));
		mIv_title_backicon.setOnClickListener(this);
		mTv_title_right.setOnClickListener(this);
		mIv_title_righticon.setOnClickListener(this);
		mTv_details_fasong.setOnClickListener(this);

	}

	/**
	 * 陌生人动态功能按钮
	 */
	private void anonymous_DT() {  
		LayoutInflater inflater = LayoutInflater.from(this);
		RelativeLayout rl = (RelativeLayout) inflater.inflate(
				R.layout.dialog_anonymous_expand_function, null);
		dialog = new AlertDialog.Builder(this).create();
		dialog.show();
		dialog.getWindow().setContentView(rl);
		$(rl, R.id.niming_jb).setOnClickListener(this);
		$(rl, R.id.niming_sl).setOnClickListener(this);
		$(rl, R.id.niming_qx).setOnClickListener(this);
	}

	/**
	 * 根据点击条目传过来的状态值判断当前条目的类型 1，好友 2，自己 3，陌生人
	 * 
	 * @param i
	 */
	private void getCurrentState(String i) {
		switch (i) {
		case DT_FRIED_TAG: // 好友的动态
			mTv_title_right.setText(getResources().getString(
					R.string.title_name_shield_ta));
			mTv_title_right.setVisibility(View.VISIBLE);
			mIv_title_righticon.setVisibility(View.GONE);
			break;
		case DT_MY_TAG: // 自己的动态
			mTv_title_right.setVisibility(View.GONE);
			mIv_title_righticon.setVisibility(View.VISIBLE);
			mIv_title_righticon
					.setImageResource(R.drawable.title_right_icon_delete);
			break;
		case DT_ANONYMOUS_TAG: // 陌生人动态
			mTv_title_right.setVisibility(View.GONE);
			mIv_title_righticon.setVisibility(View.VISIBLE);
			mIv_title_righticon.setImageResource(R.drawable.title_right_icon_more);
			break;

		default:
			break;
		}
	}

	TextView mVoiceBtn;// 播放语音按钮

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_backicon:
			KeyBoardUtils.closeKeybord(mEt_details_import_text, this);
			if(mUserdetail!=null){
				String yd_num = String.valueOf(Integer.parseInt(mUserdetail.yd) + 1);
				EventBus.getDefault().post(new UpdateShowMsg(mCurrentItem_id, null, null, null, yd_num));
			}
			finish();
			break;

		case R.id.niming_jb: // 陌生人动态详情扩展功能---举报
			setNiMing_jb();
			break;
		case R.id.iv_dynamic_video_icon: //进入播放视频yemian
			goPlayVideo();
			break;
		case R.id.niming_sl: // 陌生人动态详情扩展功能---加为好友
			setNiMing_sl();
			break;
		case R.id.niming_qx:// 陌生人动态详情扩展功能---取消
			setNiMing_qx();
			break;
		case R.id.btn_popup_delete: // 删除
			showDeleteDialog(true, 0);
			popupWindowHite();

			break;
		case R.id.btn_popup_retrans: // 转发、
			popup_button_retrans();
			popupWindowHite();
			break;
		case R.id.btn_popup_dz: // 点赞

			popup_button_dianZan();
			popupWindowHite();
			break;
		case R.id.btn_popup_message: // 信息
			isComment = true;
			popup_button_message(null);
			popupWindowHite();
			break;
		case R.id.im_mydynamic_show_more: // 动态条目的popup弹窗按钮
			initPopupWindow();
			break;
		case R.id.tv_dynamic_item_video: // 点击播放语音功能
			switch (mUserdetail.type) {
			case USER_DYNAMIC:
				mVoiceBtn = mTv_dynamic_item_video;
				playVoice(mUserdetail.voices);
				break;
			case USER_TRANSMIT:
				if (mUserdetail.voices != null && mUserdetail.voices.size() > 0) {
					stopRelativeVoice(
							transferFormat_voices(mUserdetail.voices),
							mTv_dynamic_item_video);
				}
				mVoiceBtn = mTv_dynamic_item_video;
				playVoice(mUserdetail.zfinfox.voices);
				break;
			}

			break;
		case R.id.tv_transmit_item_video: // 点击播放语音功能,转发状态下
			if (USER_TRANSMIT.equals(mUserdetail.type)) {
				if (mUserdetail.zfinfox.voices != null
						&& mUserdetail.zfinfox.voices.size() > 0) {
					stopRelativeVoice(
							transferFormat_voices(mUserdetail.zfinfox.voices),
							mTv_transmit_item_video);
				}
				mVoiceBtn = mTv_transmit_item_video;
				playVoice(mUserdetail.voices);
			}
			break;
		case R.id.ci_dynamicitem_headicon: // 点击好友头像进入个人资料的主页
			goFriendPage();

			break;
		case R.id.iv_title_righticon:
			if(mUserdetail == null){
				return;
			}
			switch (mUserdetail.mmtype) {
			case DT_MY_TAG: // 我的动态
				showDeleteDialog(true, 0);
				break;
			case DT_ANONYMOUS_TAG: // 陌生人动态
				anonymous_DT();
				break;
			}
			break;
		case R.id.tv_title_right: // 好友动态
			setIsShieldFriend(); // 屏蔽好友功能
			break;
		case R.id.tv_details_fasong: // 确认发布评论
			comment_fasong();
			break;

		default:
			break;

		}
	}
	/**
	 * 进入播放视频页面
	 */
	private void goPlayVideo() {
		if(mUserdetail==null){
			return;
		}
		if(USER_DYNAMIC_VIDEO.equals(mUserdetail.type)){
			
			if(mUserdetail.videos!=null && mUserdetail.videos.size()>0){
				Intent intent = new Intent(
						this,
						PlaySourceActivity.class);
				intent.putExtra(GlobalConstant.TV_ID,
						mUserdetail.videos.get(0).id);
				startActivity(intent);
				return;
			}
			if(mUserdetail.zfinfox !=null && mUserdetail.zfinfox.videos !=null && mUserdetail.zfinfox.videos.size()>0){
				
				
				Intent intent = new Intent(
						this,
						PlaySourceActivity.class);
				intent.putExtra(GlobalConstant.TV_ID,
						mUserdetail.zfinfox.videos.get(0).id);
				startActivity(intent);
				return;	
			}
			
			
			

		}else{
			if(mUserdetail.videos!=null && mUserdetail.videos.size()>0){
				startActivity(new Intent(this, ShowVideoActivity.class).putExtra("remotepath",mUserdetail.videos.get(0).src));
				return;
			}
			if(mUserdetail.zfinfox !=null && mUserdetail.zfinfox.type !=null && USER_DYNAMIC_VIDEO.equals(mUserdetail.zfinfox.type)){
				if( mUserdetail.zfinfox.videos !=null && mUserdetail.zfinfox.videos.size()>0){

					Intent intent = new Intent(
							this,
							PlaySourceActivity.class);
					intent.putExtra(GlobalConstant.TV_ID,
							mUserdetail.zfinfox.videos.get(0).id);
					startActivity(intent);
					return;
				}
			}else{ 
				if( mUserdetail.zfinfox.videos !=null && mUserdetail.zfinfox.videos.size()>0){
					startActivity(new Intent(this, ShowVideoActivity.class).putExtra("remotepath",mUserdetail.zfinfox.videos.get(0).src));
					return;
				}
			}
		}
	}

	/**
	 * 当该条动态是转发动态的时候 防止发布语音和转发语音同时播放冲突
	 * 
	 * @param tv
	 * @param model
	 */
	private void stopRelativeVoice(Voices_model model, TextView tv) {
		if (!mIsplay && mVoiceBtn != null && !mVoiceBtn.equals(tv)
				&& model != null) {
			mIsplay = true;
			DynamicUtlis.getInstance().stopPlayVoice(mVoiceBtn, model,
					mVoicePlay, TAG);
		}

	}

	boolean mIsplay = true; // 播放或停止，
	VoicePlay mVoicePlay = null; // 播放语音设备

	/**
	 * 点击播放语音，在次点击停止播放
	 * 
	 * @param voices_model
	 * @param dt
	 */
	private void playVoice(ArrayList<Voices> voices) {
		if (mIsplay) {
			mIsplay = false;
			DynamicUtlis.getInstance().playVoice(mVoicePlay,
					transferFormat_voices(voices), mVoiceBtn, TAG);
		} else {
			mIsplay = true;
			DynamicUtlis.getInstance().stopPlayVoice(mVoiceBtn,
					transferFormat_voices(voices), mVoicePlay, TAG);
		}

	}

	/**
	 * 将网络语音下载到本地后通过该方法获取下载完成指令 进行下载完成后的播放
	 * 
	 * @param event
	 */
	public void onEventMainThread(sendDownLoadedOrder event) {
		if (!TAG.equals(event.getType())) {
			return;
		}
		if (event.getTag() == GlobalConstant.VOICE_TABLE) {
			if (mVoiceBtn != null && !mVoiceBtn.isClickable()) {
				mVoiceBtn.setFocusable(true);
				mVoiceBtn.setClickable(true);
			}
			if (!"Failure".equals(event.getId())) {
				Voices_model voices_model = DynamicShowDao.getInstance()
						.getSingleItem_voice(DynamicShowDao.COLUMN_NAME_ID,
								new String[] { event.getId() });
				DynamicUtlis.getInstance().playVoice(mVoicePlay, voices_model,
						mVoiceBtn, TAG);
			} else {
				mIsplay = true;
			}
		} else {
			mIsplay = true;
		}
	}

	@Override
	public void onPause() {
		if (!mIsplay) { // 界面消失的时候判断是否正在播放语音
			DynamicUtlis.getInstance().stopPlayVoice(mVoiceBtn, null,
					mVoicePlay, TAG);
		}
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 转换语音数据的格式
	 * 
	 * @param voices
	 * @return
	 */
	private Voices_model transferFormat_voices(ArrayList<Voices> voices) {
		Voices_model model = new Voices_model();
		Voices v = voices.get(0);
		model.setId(v.id);
		model.setSrc(v.src);
		model.setSrcbg(v.srcbg);
		model.setSc(v.sc);
		model.setFilesize(v.filesize);
		return model;
	}

	private void goFriendPage() {
		if (!(mUser_Id.equals(mUserdetail.uid))) { // 当前是朋友发布的动态
			Intent intent = new Intent(this, OtherDataActivity.class);
			intent.putExtra(GlobalConstant.COMMENT_ADD_FRIEND, mUserdetail.uid);
			startActivity(intent);
		}

	}

	/**
	 * 显示删除的提示框
	 * 
	 * @param b
	 *            true；删除该条动态 false:删除自己发布的评论
	 * @param position
	 */
	private void showDeleteDialog(boolean b, int position) {
		LayoutInflater lf = LayoutInflater.from(Details_DT.this);
		RelativeLayout layout = (RelativeLayout) lf.inflate(
				R.layout.dialog_layout, null);
		showDelDialog(position, layout, b);
	}

	private PopupWindow mPopupWindow;

	/**
	 * 初始化popupwindow操作
	 * 
	 * @param dt
	 * @param position
	 */
	@SuppressWarnings("deprecation")
	private void initPopupWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mIm_mydynamic_show_more.setEnabled(false);
		} else {
			mIm_mydynamic_show_more.setEnabled(true);
		}
		View popupView = UIUtils.inflate(R.layout.popupwindow);
		initPopup(popupView);
		int width = Details_DT.this.getWindowManager().getDefaultDisplay()
				.getWidth();
		int height = Details_DT.this.getWindowManager().getDefaultDisplay()
				.getHeight();

		mPopupWindow = new PopupWindow(popupView, width * 460 / 720,
				height * 85 / 1280);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));
		mPopupWindow.setOutsideTouchable(true);
		popupView.setOnTouchListener(this);
		int[] location = new int[2];
		mIm_mydynamic_show_more.getLocationOnScreen(location);
		mPopupWindow.showAtLocation(mIm_mydynamic_show_more,
				Gravity.NO_GRAVITY, location[0] - mPopupWindow.getWidth(),
				location[1]);
	}

	private TextView mBtn_popup_delete; // 动态条目弹窗 popup删除
	private TextView mBtn_popup_dz; // 动态条目弹窗 popup点赞
	private TextView mBtn_popup_message; // 动态条目弹窗 popup信息
	private TextView mBtn_popup_retrans; // 动态条目弹窗 popup转发

	/**
	 * 初始化popupwindow控件
	 * 
	 * @param dta
	 * @param popupView
	 * @param position
	 */
	private void initPopup(View popupView) {
		mBtn_popup_delete = $(popupView, R.id.btn_popup_delete);
		mBtn_popup_dz = $(popupView, R.id.btn_popup_dz);
		mBtn_popup_message = $(popupView, R.id.btn_popup_message);
		mBtn_popup_retrans = $(popupView, R.id.btn_popup_retrans);
		setPopup();

		mBtn_popup_dz.setText(getResources().getString(
				R.string.text_dynamic_praise));
		mBtn_popup_delete.setOnClickListener(this);
		mBtn_popup_dz.setOnClickListener(this);
		mBtn_popup_message.setOnClickListener(this);
		mBtn_popup_retrans.setOnClickListener(this);
	}

	/**
	 * 设置当前位置 状态 判断是否是自己发布的动态
	 * 
	 */
	private void setPopup() {

		if (mUserdetail.uid != null && mUser_Id.equals(mUserdetail.uid)) {
			mBtn_popup_delete.setVisibility(View.VISIBLE);
			mBtn_popup_retrans.setVisibility(View.GONE);
		} else {
			mBtn_popup_delete.setVisibility(View.GONE);
			mBtn_popup_retrans.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			mIm_mydynamic_show_more.setEnabled(true);
		}
		return false;
	}

	private boolean isComment; // 判断当前状态是评论还是回复好友状态

	/**
	 * 每个条目中popup弹窗回复信息按钮
	 * 
	 * @param model
	 */
	private void popup_button_message(CommentModel model) {
		if (mBtn_popup_message != null) {
			mBtn_popup_message.setEnabled(false);
		}
		mRl_details.setVisibility(View.VISIBLE);
		KeyBoardUtils.openKeybord(mEt_details_import_text, this);

		if (isComment) { // 发表评论状态
			mEt_details_import_text.setHint("请输入评论内容~");
			
		} else { // 回复好友状态
			mEt_details_import_text.setHint("回复 " + model.getReviewUName()
					+ " :");
			mEt_details_import_text.setHintTextColor(getResources().getColor(
					R.color.bg_app_line));
		}

	}

	private String mParams_aid; // 当前动态id
	private String mParams_ouid; // 被评论用户id
	private String mParams_msg; // 留言信息
	private String mParams_pid; // 上一级评论id

	/**
	 * 确认发布评论按钮
	 */
	private void comment_fasong() {

		if (mBtn_popup_message != null) {
			mBtn_popup_message.setEnabled(true);
		}

		mParams_msg = mEt_details_import_text.getText().toString().trim(); // 获取输入的文字信息
		if (mParams_msg.length() <= 0) {
			UIUtils.showToast(this, "请输入内容哦~~");
			return;
		}
		RequestParams params = new RequestParams();
		if (mUserdetail.id == null) {
			mParams_aid = " ";
		} else {
			mParams_aid = mUserdetail.id;
		}
		params.addBodyParameter("aid", mParams_aid);

		if (mParams_msg == null) {
			mParams_msg = " ";
		}
		params.addBodyParameter("msg", mParams_msg);
		if (!isComment) {// 发表回复状态时添加两个参数
			if (mParams_ouid == null) {
				mParams_ouid = " ";
			}
			params.addBodyParameter("ouid", mParams_ouid);

			if (mParams_pid == null) {
				mParams_pid = " ";
			}
			params.addBodyParameter("pid", mParams_pid);
		}
		mHttpHelp.sendPost(NetworkConfig.getConmmentUrl(), params,
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean != null ) {
							if ("1".equals(bean.status)) {
								
								mEt_details_import_text.setText("");
								mRl_details.setVisibility(View.GONE);
								KeyBoardUtils.closeKeybord(
										mEt_details_import_text,
										Details_DT.this);
								if (isComment) {
									UIUtils.showToast(Details_DT.this, "评论成功~");
									mUserdetail.pj = String.valueOf(Integer.parseInt(mUserdetail.pj) + 1);
									mTv_dynamic_appraise.setText(mUserdetail.pj+ getTitleName(R.string.text_dynamic_appraise));
									EventBus.getDefault().post(new UpdateShowMsg(mUserdetail.id, null, mUserdetail.pj, null, null));
								} else {
									UIUtils.showToast(Details_DT.this, "回复成功~");
								}
								// 请求的网络，后期可以存入集合
								initNet();
							} else if ("5".equals(bean.status)) {
								UIUtils.showToast(Details_DT.this, "请输入内容~~");
							}else{
								UIUtils.showToast(Details_DT.this, bean.msg);
							}
						}

					}
				});
	}


	/**
	 * 每个条目中popup弹窗点赞按钮 服务端添加字段
	 * 
	 * @param dt
	 */
	private void popup_button_dianZan() {
		if (mUserdetail == null) {
			return;
		}
		/**
		 * 通过判断iszan字段值，1为已经点过该动态，0为未点过该条动态
		 */
		if ("1".equals(mUserdetail.iszan)) {
			UIUtils.showToast(Details_DT.this, "已经点过赞了~~");
		} else {
			mHttpHelp.sendGet(NetworkConfig.getDianZanUrl(mUserdetail.id),
					BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

						@Override
						public void onSucceed(BaseBaen bean) {
							if(bean == null || bean.status == null|| bean.msg == null){
								return ;
							}
							if("1".equals(bean.status)){
							UIUtils.showToast(Details_DT.this, "点赞成功！");
							mUserdetail.dz = String.valueOf(Integer.parseInt(mUserdetail.dz) + 1);
							mTv_dynamic_praise.setText(mUserdetail.dz+ getTitleName(R.string.text_dynamic_praise));
							mUserdetail.iszan = "1";
							EventBus.getDefault().post(new UpdateShowMsg(mUserdetail.id,null, null, mUserdetail.dz, null));
							
							}else{
								UIUtils.showToast(Details_DT.this, bean.msg);
							}
						}
					});
		}
	}

	/**
	 * 根据id获取相应的系统资源
	 * 
	 * @param id
	 * @return
	 */
	private String getTitleName(int id) {
		return Details_DT.this.getResources().getString(id);
	}

	/**
	 * 每个条目中popup弹窗转发按钮
	 * 
	 * @param position
	 */
	private void popup_button_retrans() {
//	    mUserdetail.zf = String.valueOf(Integer.parseInt(mUserdetail.zf) + 1);
//		mTv_dynamic_retransmission.setText( mUserdetail.zf+ getTitleName(R.string.text_dynamic_retransmission));
//		EventBus.getDefault().post(new UpdateShowMsg(mUserdetail.id,  mUserdetail.zf, null, null, null));
		Intent intent = new Intent(Details_DT.this, Retrans_DT.class);
		intent.putExtra(GlobalConstant.RETRANS_USER_TAG, DynamicUtlis
				.getInstance().transferFormat(mUserdetail));
		Details_DT.this.startActivity(intent);
	}
	/**
	 * 设置页面上的 转发，的显示数字
	 */
	public void onEventMainThread(UpdateShowMsg event) {
		if (event == null) {
			return;
		}
	
		if (event.getZf() != null) {
			 mUserdetail.zf = String.valueOf(Integer.parseInt(mUserdetail.zf) + 1);
				mTv_dynamic_retransmission.setText( mUserdetail.zf+ getTitleName(R.string.text_dynamic_retransmission));
		}
	}

	/**
	 * 隐藏popupWindow对话框
	 * 
	 * @param dt
	 */
	private void popupWindowHite() {
		mPopupWindow.dismiss();
		mPopupWindow = null;
		mIm_mydynamic_show_more.setEnabled(true);
	}

	/**
	 * 点击条目中删除选项弹出对话框
	 * 
	 * @param layout
	 * @param b
	 *            true；删除该条动态 false:删除自己发布的评论
	 */
	private void showDelDialog(int position, RelativeLayout layout,
			final boolean b) {
		new MyDialog(Details_DT.this, position, layout) {

			@Override
			public String getTitleText() {
				return Details_DT.this.getResources().getString(
						R.string.text_is_delete);
			}

			@Override
			public void setButton_ok(int position) {
				if (b) {
					dialog_button_delete(position);
				} else {
					dialog_delete_comment(position);
				}
			}
		};
	}

	/**
	 * 每个条目中dialog弹窗删除按钮
	 * 
	 * @param position
	 */
	private void dialog_button_delete(final int position) {

		mHttpHelp.sendGet(NetworkConfig.getDeleta_DtUrl(mUserdetail.id),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || bean.status == null
								|| bean.msg == null) {
							UIUtils.showToast(getApplicationContext(), "网络异常！");
							return;

						}
						if ("1".equals(bean.status)) {
							// 点击删除按钮或者删除图标时，动态消失，退出当前界面
							UIUtils.showToast(Details_DT.this, "动态删除成功~~");
							EventBus.getDefault().post(new RefurbishDTItem(mCurrentItem_id,GlobalConstant.DELETE_TAG));
							finish();
						} else {
							UIUtils.showToast(getApplicationContext(), bean.msg);
						}

					}
				});
	}

	/**
	 * listview适配器
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCommentlist.size();
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
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(Details_DT.this,
						R.layout.item_details_dt_lv, null);
				holder.tv_comment_reply = $(convertView, R.id.tv_comment_reply);
				holder.line_details_dt = $(convertView, R.id.line_details_dt);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			/*
			 * 判断当前的被回复者的id是否为0 若为0：该条为评论， 否则为“a回复b”
			 */
			CommentModel model = new CommentModel();
			model.setReplyUid(mCommentlist.get(position).ouid);
			model.setReviewUid(mCommentlist.get(position).uid);
			model.setReviewContent(mCommentlist.get(position).msg);
			if (mUser_nickname != null
					&& mUser_nickname
							.equals(mCommentlist.get(position).onickname)) {
				model.setReplyUName("我 ");
			} else {
				model.setReplyUName(mCommentlist.get(position).onickname);
			}
			if (mUser_nickname != null
					&& mUser_nickname
							.equals(mCommentlist.get(position).nickname)) {
				model.setReviewUName("我 ");
			} else {
				model.setReviewUName(mCommentlist.get(position).nickname);
			}

			if (position == 0) {
				holder.line_details_dt.setVisibility(View.VISIBLE);
			} else {
				holder.line_details_dt.setVisibility(View.GONE);
			}

			holder.tv_comment_reply.setReply(model);
			holder.tv_comment_reply.setListener(new MyCommentItem(position,
					mCommentlist)); // 设置textview的各个点击事件

			return convertView;
		}
	}

	class MyCommentItem implements TextBlankClickListener {

		private int position;
		private ArrayList<Commentlist> commentlist;

		public MyCommentItem(int position, ArrayList<Commentlist> commentlist) {
			this.position = position;
			this.commentlist = commentlist;
		}

		@Override
		public void onReplyClick(View view, CommentModel model) {
			if (mUser_Id != null && mUser_Id.equals(model.getReplyUid())) { // 我的资料页
				startActivity(new Intent(Details_DT.this, MyDataActivity.class));
			} else { // 好友的资料页
				Intent intent = new Intent(Details_DT.this,
						OtherDataActivity.class);
				intent.putExtra(GlobalConstant.COMMENT_ADD_FRIEND,
						model.getReplyUid());
				startActivity(intent);

			}
		}

		@Override
		public void onReviewClick(View view, CommentModel model) {
			if (mUser_Id != null && mUser_Id.equals(model.getReviewUid())) { // 我的资料页
				startActivity(new Intent(Details_DT.this, MyDataActivity.class));

			} else { // 好友的资料页
				Intent intent = new Intent(Details_DT.this,
						OtherDataActivity.class);
				intent.putExtra(GlobalConstant.COMMENT_ADD_FRIEND,
						model.getReviewUid());
				startActivity(intent);

			}
		}

		@Override
		public void onBlankClick(View view, CommentModel model) {
			isComment = false;
			details_click_user(model, position);
			mParams_ouid = model.getReviewUid();
			mParams_pid = commentlist.get(position).id;
		}
	}

	// 动态详情页，回复留言处点击姓名的操作。。
	private void details_click_user(CommentModel model, int position) {
		if (mUser_Id == null) {
			return;
		}
		if (!mUser_Id.equals(model.getReviewUid())) {
			popup_button_message(model);
		} else {
			showDeleteDialog(false, position);
		}
	}

	class ViewHolder {
		public View line_details_dt;
		public CommentTextView tv_comment_reply;

	}

	/**
	 * 陌生人动态详情扩展功能---取消
	 */
	private void setNiMing_qx() {
		dialog.dismiss();
	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	/**
	 * 根据id查找控件
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T $(int id) {
		return (T) findViewById(id);
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
