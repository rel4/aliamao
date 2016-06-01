package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.activity.nearby_activity.Details_DT;
import com.aliamauri.meat.activity.nearby_activity.Retrans_DT;
import com.aliamauri.meat.activity.nearby_activity.ShowDetailsImages;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist.Imgs;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist.Videos;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist.Voices;
import com.aliamauri.meat.bean.SingleDynamicBean;
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
import com.aliamauri.meat.utils.MyBDmapUtlis;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.RefreshListView;
import com.aliamauri.meat.view.RefreshListView.OnRefreshListener;
import com.aliamauri.meat.weight.MyDialog;
import com.baidu.mapapi.model.LatLng;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 我的动态
 * 
 * @author ych
 * 
 */
public class MyDynamicActivity extends BaseActivity implements
		OnItemClickListener, OnRefreshListener {
	private final String SHIELDTA_TAG = "屏蔽Ta";
	private final String FOLLOWTA_TAG = "关注Ta";
 	private final String TAG = "MyDynamicActivity";
	private final String USER_DYNAMIC = "1"; // 用户发布的动态
	private final String USER_TRANSMIT = "2"; // 用户转发的动态
	private final String USER_VIDEO = "3"; // 用户对视频评论的动态（预留）
	private final String USER_RECORD = "4"; // 用户的观看记录 （预留）

	private RefreshListView mLv; // 展示内容的listview控件
	private MyBaseAdapter adapter;
	private TextView mBtn_popup_delete; // 动态条目弹窗 popup删除
	private TextView mBtn_popup_dz; // 动态条目弹窗 popup点赞
	private TextView mBtn_popup_message; // 动态条目弹窗 popup信息
	private TextView mBtn_popup_retrans; // 动态条目弹窗 popup转发
	private PopupWindow mPopupWindow;
	private HttpHelp mHttp;
	private int mCurrentPage = 1; // 默认页为1
	private String mWd; // 伟度
	private String mJd; // 经度
	private int height; // 屏幕的高度
	private int width; // 屏幕的宽度
	private ArrayList<Tlist> mTlist; // 所有动态的集合
	private String mUid; // 获取传过来的本机或者好友的id值
	private String mUser_Id; // 本机用户的uID
	private TextView mIsShield_btn; //好友动态时候，是否屏蔽的按钮
	private boolean mIsShield_tag ;//屏蔽/关注好友标记
	

	@Override
	protected View getRootView() {
		EventBus.getDefault().register(this);
		mUid = baseIntent.getExtras().getString(GlobalConstant.DYNAMIC_TAG);
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION,
				"0&&0").split("&&");
		mWd = String.valueOf(location[0]);
		mJd = String.valueOf(location[location.length - 1]);
		mUser_Id = PrefUtils.getString(UIUtils.getContext(),GlobalConstant.USER_ID, "");
		View view = View.inflate(MyDynamicActivity.this,R.layout.activity_my_dynamic, null);
		mIsShield_btn = $(view,R.id.tv_title_right);
		mLv = $(view, R.id.lv_zxdt_content);
		if(!StringUtils.isEmpty(mUser_Id) && !StringUtils.isEmpty(mUid)&& !mUser_Id.equals(mUid)){
			// 当前是好友的动态 才显示屏蔽和关注ta按钮
			Map<String, User> allUid = ContactManager.getInstance()
					.getAllContactUidMap();
			for (Map.Entry<String, User> entry : allUid.entrySet()) {
				if (entry != null && entry.getKey() != null) {
					String key = entry.getKey().toString();
					if (key != null && key.equals(mUid)) {
						mIsShield_btn.setVisibility(View.VISIBLE);	
						mIsShield_btn.setOnClickListener(new MyItemClickListener(0, null));
						break;
					}
				}
			}
		}
		return view;
	}
	@Override
	protected void onResume() {
		super.onResume();
		  MobclickAgent.onResume(this);
		
	}
	
	
	@Override
	protected void initView() {
		WindowManager ss = (WindowManager) mActivity
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		ss.getDefaultDisplay().getMetrics(dm);
		height = dm.heightPixels;
		width = dm.widthPixels;
		mTlist = new ArrayList<Tlist>();
		mTlist.clear();
		mHttp = new HttpHelp();
		mVoicePlay = new VoicePlay();
		mCurrentPage = 1; // 默认页为1
	}

	@Override
	protected void setListener() {

	}
	
	/**
	 * 每个条目中popup弹窗回复信息按钮
	 *
	 * @param position
	 */
	private void popup_button_message(int position, ViewHolder_DT dt) {
		String pj_num = String.valueOf(Integer.parseInt(mTlist.get(position).pj) + 1);
		dt.tv_dynamic_appraise.setText(String.valueOf(pj_num+ getTitleName(R.string.text_dynamic_appraise)));
		mTlist.get(position).pj = pj_num;
		String id = mTlist.get(position).id;
		EventBus.getDefault().post(new UpdateShowMsg(id, null, pj_num, null, null));
		goDetailsActivity(position, true);
	}

	/**
	 * 每个条目中dialog弹窗删除按钮
	 * 
	 * @param position
	 */
	private void dialog_button_delete(final int position) {

		mHttp.sendGet(NetworkConfig.getDeleta_DtUrl(mTlist.get(position).id),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						EventBus.getDefault().post(new RefurbishDTItem(mTlist.get(position).id,GlobalConstant.DELETE_TAG));
						UIUtils.showToast(mActivity, "删除成功！");
					}

				});
	}
	
	/**
	 * 每个条目中popup弹窗转发按钮
	 * 
	 * @param position
	 * @param dt 
	 */
	private void popup_button_retrans(int position, ViewHolder_DT dt) {
		String zf_num = String.valueOf(Integer.parseInt(mTlist.get(position).zf) + 1);
		dt.tv_dynamic_retransmission.setText(String.valueOf(zf_num+ getTitleName(R.string.text_dynamic_retransmission)));
		mTlist.get(position).zf = zf_num;
		String id = mTlist.get(position).id;
		EventBus.getDefault().post(new UpdateShowMsg(id, zf_num, null, null, null));
		
		Intent intent = new Intent(mActivity, Retrans_DT.class);
		intent.putExtra(GlobalConstant.RETRANS_USER_TAG,DynamicUtlis.getInstance().transferFormat(mTlist.get(position)));
		mActivity.startActivity(intent);
	}

	/**
	 * 每个条目中popup弹窗点赞按钮 服务端添加字段
	 * 
	 * @param dt
	 */
	private void popup_button_dianZan(final int position, final ViewHolder_DT dt) {
		if (mTlist == null) {
			return;
		}
		/**
		 * 通过判断iszan字段值，1为已经点过该动态，0为未点过该条动态
		 */
		if ("1".equals(mTlist.get(position).iszan)) {
			UIUtils.showToast(mActivity, "已经点过赞了~~");
		} else {
			mHttp.sendGet(NetworkConfig.getDianZanUrl(mTlist.get(position).id),
					BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

						@Override
						public void onSucceed(BaseBaen bean) {
							UIUtils.showToast(mActivity, "点赞成功！");
							String dz_sum = String.valueOf(Integer.parseInt(mTlist.get(position).dz) + 1);
							dt.tv_dynamic_praise.setText(dz_sum+ getTitleName(R.string.text_dynamic_praise));
							mTlist.get(position).iszan = "1";
							mTlist.get(position).dz = dz_sum;
							EventBus.getDefault().post(new UpdateShowMsg(mTlist.get(position).id,null, null, dz_sum, null));
						}
					});
		}

	}

	/**
	 * 点击listview条目监听
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		String yd_num = String.valueOf(Integer.parseInt(mTlist.get(position).yd) + 1);
		TextView read = (TextView) view.findViewById(R.id.tv_dynamic_read);
		read.setText(String.valueOf(yd_num+ getTitleName(R.string.text_dynamic_read)));
		mTlist.get(position).yd = yd_num;
		String item_id = mTlist.get(position).id;
		EventBus.getDefault().post(new UpdateShowMsg(item_id, null, null, null, yd_num));
		
		goDetailsActivity(position, false);
	}

	/**
	 * 进入动态详情页面,传入该条动态的id值
	 * 
	 * @param position
	 * @param b
	 * 
	 *            true:说明当前点击的是整个条目 false：说明当前是点击的评论条目按钮，直接进入详情后弹出键盘布局
	 */

	private void goDetailsActivity(int position, boolean b) {
		Intent intent = new Intent(mActivity, Details_DT.class);
		intent.putExtra(GlobalConstant.DT_CURRENT_DATA_NAME,mTlist.get(position).id);
		intent.putExtra(GlobalConstant.DT_CURRENT_DATA_UID,mTlist.get(position).uid);
		intent.putExtra(GlobalConstant.DT_ISOPEN_COMMENT_LAYOUT, b);
		startActivity(intent);
	}
	public void onEventMainThread(RefurbishDTItem event){
		if(event == null){
			return ;
		}
		if(GlobalConstant.DELETE_TAG.equals(event.getTag())){
			Iterator<Tlist> iterator = mTlist.iterator();
			while (iterator.hasNext()) {
				Tlist tlist = iterator.next();
				if(event.getId().equals(tlist.id)){
					iterator.remove();
					adapter.notifyDataSetChanged();
					return ;
				}
			}
		}
	}
	
	
	/**
	 * 初始化网络请求
	 * 
	 * @param url
	 */

	@Override
	protected void initNet() {
		if (mHttp != null) {
			mHttp.stopHttpNET();
		}
		mHttp.sendGet(NetworkConfig.getDynamicPage_url(mUid,mCurrentPage), SingleDynamicBean.class,
				new MyRequestCallBack<SingleDynamicBean>() {

					@Override
					public void onSucceed(SingleDynamicBean bean) {
						
						if (mCurrentPage <= 1 && (bean == null || bean.cont == null||bean.cont.size() <= 0)) {
							UIUtils.showToast(mActivity, "还没有发过动态哦~~~~");
							return;
						}
						if(mIsShield_btn.getVisibility() == View.VISIBLE){
							if("0".equals(bean.isshield)){   //1屏蔽，，0关注
								mIsShield_tag = true;
								mIsShield_btn.setText(SHIELDTA_TAG);
							}else{
								mIsShield_tag = false;
								mIsShield_btn.setText(FOLLOWTA_TAG);
							}
						}
						if("1".equals(bean.status)){
							if (bean.cont == null|| bean.cont.size() <= 0) {
								UIUtils.showToast(
										mActivity,
										mActivity.getResources().getString(
												R.string.text_no_data));
								mLv.onRefreashFinish();
								return;
							}
							if (mCurrentPage <= 1) {
								mTlist.addAll(bean.cont);
								adapter = new MyBaseAdapter();
								mLv.setAdapter(adapter);
								mLv.setOnItemClickListener(MyDynamicActivity.this);
								mLv.setOnRefreshListener(MyDynamicActivity.this);

							} else {
								mTlist.addAll(bean.cont);
								adapter.notifyDataSetChanged();
								mLv.onRefreashFinish();
							}
							mCurrentPage++;
						}else{
							UIUtils.showToast(mActivity,bean.msg);
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

	class MyBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mTlist.size();
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
			ViewHolder_DT holder_dt = null;
			if (convertView == null) {
				holder_dt = new ViewHolder_DT();
				convertView = View.inflate(mActivity, R.layout.my_dynamic_item,
						null);
				holder_dt.im_mydynamic_show_more = $(convertView,
						R.id.im_mydynamic_show_more);
				holder_dt.ci_dynamicitem_headicon = $(convertView,
						R.id.ci_dynamicitem_headicon);
				holder_dt.tv_mydynamic_username = $(convertView,
						R.id.tv_mydynamic_username);
				holder_dt.tv_dynamic_item_introduce = $(convertView,
						R.id.tv_dynamic_item_introduce);
				holder_dt.tv_dynamic_current_time = $(convertView,
						R.id.tv_dynamic_current_time);
				holder_dt.iv_dynamic_video_icon = $(convertView,
						R.id.iv_dynamic_video_icon);
				holder_dt.iv_dynamic_video_play_icon = $(convertView,
						R.id.iv_dynamic_video_play_icon);
				holder_dt.tv_dynamic_distance = $(convertView,
						R.id.tv_dynamic_distance);
				holder_dt.tv_dynamic_retransmission = $(convertView,
						R.id.tv_dynamic_retransmission);
				holder_dt.tv_dynamic_appraise = $(convertView,
						R.id.tv_dynamic_appraise);
				holder_dt.tv_dynamic_praise = $(convertView,
						R.id.tv_dynamic_praise);
				holder_dt.tv_dynamic_read = $(convertView, R.id.tv_dynamic_read);
				holder_dt.fl_content_layout = $(convertView,
						R.id.fl_content_layout);
				holder_dt.tv_dynamic_item_video = $(convertView,
						R.id.tv_dynamic_item_video);
				holder_dt.gv_photo_album = $(convertView, R.id.gv_photo_album);
				holder_dt.rl_item_video = $(convertView, R.id.rl_item_video);
				holder_dt.ll_transmit_layout = $(convertView,
						R.id.ll_transmit_layout);
				holder_dt.tv_transmit_item_introduce = $(convertView,
						R.id.tv_transmit_item_introduce);
				holder_dt.tv_transmit_item_video = $(convertView,
						R.id.tv_transmit_item_video);
				holder_dt.ll_transmit_title_layout = $(convertView,
						R.id.ll_transmit_title_layout);
				holder_dt.tv_transmit_name = $(convertView,
						R.id.tv_transmit_name);
				convertView.setTag(holder_dt);
			} else {
				holder_dt = (ViewHolder_DT) convertView.getTag();
			}

			setItem_dt(position, holder_dt, mTlist);

			return convertView;
		}
	}

	/**
	 * viewHolder_dt -----动态
	 * 
	 * @author limaokeji-windosc
	 * 
	 */

	class ViewHolder_DT {
		public TextView tv_transmit_name; // 转发状态转发人的名字
		public LinearLayout ll_transmit_layout; // 转发状态背景布局
		public TextView tv_transmit_item_video; // 转发状态------ 语音
		public TextView tv_transmit_item_introduce; // 转发状态----- 文字
		public LinearLayout ll_transmit_title_layout; // 转发状态的时候我的语音或者文字布局
		public RelativeLayout rl_item_video; // 视频的布局
		public TextView tv_dynamic_read; // 阅读
		public TextView tv_dynamic_praise; // 点赞
		public TextView tv_dynamic_appraise; // 评价
		public TextView tv_dynamic_retransmission; // 转发
		public TextView tv_dynamic_distance; // 距离s
		public ImageView iv_dynamic_video_icon; // 视频预览图
		public ImageView iv_dynamic_video_play_icon; // 视频播放按钮
		public TextView tv_dynamic_current_time; // 当前时间
		public TextView tv_dynamic_item_introduce; // 该条动态的文字状态
		public TextView tv_mydynamic_username; // 用户名
		public CircleImageView ci_dynamicitem_headicon;// 用户名头像
		public ImageView im_mydynamic_show_more; // 按钮----更多操作
		public GridView gv_photo_album;// 展示照片
		public FrameLayout fl_content_layout; // 用户上传视频布局
		public TextView tv_dynamic_item_video; // 用户发送的语音时常

	}
	
	/**
	 * 设置动态每个条目的点击事件
	 * 
	 * @param position
	 * @param tlist
	 * @param cont
	 * @param holder_dt
	 * 
	 */
	public void setItem_dt(int position, final ViewHolder_DT dt,
			ArrayList<Tlist> t_list) {

		Tlist tlist = t_list.get(position);
		dt.im_mydynamic_show_more.setOnClickListener(new MyItemClickListener(
				position, dt));
		dt.ci_dynamicitem_headicon.setOnClickListener(new MyItemClickListener(
				position, dt));

		dt.tv_dynamic_read.setText(tlist.yd
				+ getTitleName(R.string.text_dynamic_read));
		dt.tv_dynamic_retransmission.setText(tlist.zf
				+ getTitleName(R.string.text_dynamic_retransmission));
		dt.tv_dynamic_appraise.setText(tlist.pj
				+ getTitleName(R.string.text_dynamic_appraise));
		dt.tv_dynamic_praise.setText(tlist.dz
				+ getTitleName(R.string.text_dynamic_praise));
		dt.tv_mydynamic_username.setText(tlist.nickname);
		dt.tv_dynamic_distance.setText(new MyBDmapUtlis().getCurrentDistance(
				new LatLng(Double.valueOf(tlist.wd), Double.valueOf(tlist.jd)),
				new LatLng(Double.valueOf(mWd), Double.valueOf(mJd))));
		dt.tv_dynamic_current_time.setText(tlist.createtime);
		mHttp.showImage(dt.ci_dynamicitem_headicon, tlist.face + "##");

		switch (tlist.type) {
		case USER_DYNAMIC:
			setUser_dynamic(position, dt, tlist);
			break;
		case USER_TRANSMIT:
			setuser_transmit(position, dt, tlist);
			break;
		case USER_VIDEO:
			/* 用户对视频的评论 ，预留 */
			break;
		case USER_RECORD:
			/* 用户观看记录 ，预留 */
			break;

		default:
			break;
		}

	}
	/**
	 * 设置用户转发动态的样式
	 * 
	 * @param position
	 * @param dt
	 * @param tlist
	 */
	private void setuser_transmit(int position, ViewHolder_DT dt, Tlist tlist) {
		if (tlist.zfinfox == null) {
			return;
		}
		dt.ll_transmit_title_layout.setVisibility(View.VISIBLE);
		showTextOrVoice(position, dt.tv_transmit_item_introduce,
				dt.tv_transmit_item_video, dt, tlist.voices, tlist.infos);
		dt.ll_transmit_layout.setBackgroundColor(mActivity.getResources()
				.getColor(R.color.bg_greywhite));
		dt.ll_transmit_layout.setPadding(width * 20 / 720, height * 10 / 1280,
				width * 20 / 720, height * 20 / 1280);
		dt.tv_transmit_name.setVisibility(View.VISIBLE);
		dt.tv_transmit_name.setText(Html
				.fromHtml("转发: <font color=\"#f95c71\">"
						+ tlist.zfinfox.fnickname + "</font> 的动态"));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		if (tlist.zfinfox.voices != null && tlist.zfinfox.voices.size() > 0) {
			lp.setMargins(0, 0, 0, height * 10 / 1280);
			dt.tv_transmit_name.setLayoutParams(lp);
		} else {
			lp.setMargins(0, 0, 0, height * 1 / 1280);
			dt.tv_transmit_name.setLayoutParams(lp);
		}

		showTextOrVoice(position, dt.tv_dynamic_item_introduce,
				dt.tv_dynamic_item_video, dt, tlist.zfinfox.voices,
				tlist.zfinfox.infos);
		showPhotoOrVideo(position, dt, tlist.zfinfox.videos, tlist.zfinfox.imgs);
	}
	/**
	 * 设置用户的动态样式
	 * 
	 * @param position
	 * @param dt
	 * @param tlist
	 */
	private void setUser_dynamic(final int position, final ViewHolder_DT dt,
			Tlist tlist) {
		dt.ll_transmit_title_layout.setVisibility(View.GONE);
		showTextOrVoice(position, dt.tv_dynamic_item_introduce,
				dt.tv_dynamic_item_video, dt, tlist.voices, tlist.infos);
		showPhotoOrVideo(position, dt, tlist.videos, tlist.imgs);
		dt.tv_transmit_name.setVisibility(View.GONE);
		dt.ll_transmit_layout.setBackgroundColor(mActivity.getResources()
				.getColor(R.color.bg_white));
		dt.ll_transmit_layout.setPadding(0, 0, 0, 0);
	}

	/**
	 * 根据当前情况控制显示 图片或者视频动态 若图片和视频动态同时存在，显示视频状态为主，在动态详情页在全部展示出来
	 * 
	 * @param position
	 * @param dt
	 * @param imgs
	 * @param videos
	 * @param tlist
	 */
	private void showPhotoOrVideo(int position, ViewHolder_DT dt,
			ArrayList<Videos> videos, ArrayList<Imgs> imgs) {

		if (videos != null && videos.size() > 0) { // 情况1：图片和视频动态同时存在显示视频动态，或者只有视频的情况下,显示视频数据
			dt.rl_item_video.setVisibility(View.VISIBLE);
			dt.gv_photo_album.setVisibility(View.GONE);
			mHttp.showImage(dt.iv_dynamic_video_icon, videos.get(0).srcbg);
		} else if (videos.size() <= 0 && imgs != null && imgs.size() > 0) { // 情况2：没有视频只有图片的情况下
																			// ，显示图片数据
			dt.rl_item_video.setVisibility(View.GONE);
			dt.gv_photo_album.setVisibility(View.VISIBLE);
			dt.gv_photo_album.setAdapter(new MyImgsAdapter(imgs));
		} else { // 情况3：视频和图片都没有的情况下， 都不显示
			dt.rl_item_video.setVisibility(View.GONE);
			dt.gv_photo_album.setVisibility(View.GONE);
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
			if (imgs.size() >= 3) {
				return 3;
			} else {
				return imgs.size();
			}

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
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = View.inflate(mActivity, R.layout.item_imgs_gv, null);
			ImageView iv = $(view, R.id.iv_dt_img);
			mHttp.showImage(iv, imgs.get(position).img);
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ArrayList<String> imgoris = new ArrayList<>();
					imgoris.clear();
					for (int i = 0; i < imgs.size(); i++) {
						imgoris.add(imgs.get(i).imgori);
					}
					Intent intent = new Intent(mActivity,ShowDetailsImages.class);
					intent.putStringArrayListExtra(GlobalConstant.SHOW_DETAILS_IMAGES, imgoris);
					intent.putExtra(GlobalConstant.SHOW_IMAGE_POSITION, position);
					startActivity(intent);
				}
			});
			return view;
		}

	}
	/**
	 * 根据当前情况控制显示 文字或者语音动态 若语音和文字动态同时存在，显示语音状态为主，在动态详情页在全部展示出来
	 * 
	 * @param position
	 * @param introduce
	 * @param video
	 * @param dt
	 * @param voices
	 * @param infos
	 */

	private void showTextOrVoice(final int position, TextView introduce,
			TextView video, ViewHolder_DT dt, ArrayList<Voices> voices,
			String infos) {

		if (voices != null && voices.size() > 0) { // 情况1：语音和文字动态同时存在显示语音动态，或者只有语音的情况下
													// 显示语音数据
			introduce.setVisibility(View.GONE);
			video.setVisibility(View.VISIBLE);
			video.setText(StringUtils.secToTime((int) Float.parseFloat(voices
					.get(0).sc)));
			// 设置音频条的长度
			UIUtils.setVideoShape(video, voices.get(0).sc, mActivity);
			video.setOnClickListener(new MyItemClickListener(position, dt));

		} else if (voices.size() <= 0 && !StringUtils.isEmpty(infos)) { // 情况2：没有语音只有文字的情况下显示文字数据
			introduce.setVisibility(View.VISIBLE);
			video.setVisibility(View.GONE);
			introduce.setText(infos);
		} else { // 情况3：语音和文字都没有的情况下， 都不显示
			introduce.setVisibility(View.GONE);
			video.setVisibility(View.GONE);
		}
	}

	/**
	 * 根据id获取相应的系统资源
	 * 
	 * @param id
	 * @return
	 */
	private String getTitleName(int id) {
		return mActivity.getResources().getString(id);
	}
	
	private TextView mVoiceBtn;// 播放语音按钮
	/**
	 * 条目中按钮点击监听实现类
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyItemClickListener implements OnClickListener {

		private int position; // 当前的位置
		private ViewHolder_DT dt; // 当前动态条目的Viewholder

		public MyItemClickListener(int position, ViewHolder_DT dt) {
			this.position = position;
			this.dt = dt;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.im_mydynamic_show_more: // 动态条目的popup弹窗按钮
				initPopupWindow(dt, position);
				break;
			case R.id.tv_dynamic_item_video: // 点击播放语音功能
				switch (mTlist.get(position).type) {
				case USER_DYNAMIC:
					mVoiceBtn = dt.tv_dynamic_item_video;
					playVideo(mTlist.get(position).voices);
					break;
				case USER_TRANSMIT:
					if (mTlist.get(position).voices != null && mTlist.get(position).voices.size() > 0) {
						stopRelativeVoice(
								transferFormat_voices(mTlist.get(position).voices),
								dt.tv_dynamic_item_video);
					}
					mVoiceBtn = dt.tv_dynamic_item_video;
					playVideo(mTlist.get(position).zfinfox.voices);
					break;
				}

				break;
			case R.id.tv_transmit_item_video: // 点击播放语音功能,转发状态下
				if (USER_TRANSMIT.equals(mTlist.get(position).type)) {
					if (mTlist.get(position).zfinfox.voices != null
							&& mTlist.get(position).zfinfox.voices.size() > 0) {
						stopRelativeVoice(
								transferFormat_voices(mTlist.get(position).zfinfox.voices),
								dt.tv_transmit_item_video);
					}
					mVoiceBtn = dt.tv_transmit_item_video;
					playVideo(mTlist.get(position).voices);
				}
				break;
			case R.id.ci_dynamicitem_headicon: // 点击好友头像跳到好友的界面
				getFrinedPage(position);
				break;
			case R.id.tv_title_right: // 屏蔽/关注按钮
				setIsShieldFriend();
				break;
			default:
				break;
			}
		}
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
	 * 发送屏蔽ta操作
	 */
	private void setFriendAction(final String action) {
		mHttp.sendGet(NetworkConfig.getShieldFriend(mUid,action),
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
								mIsShield_btn.setText(SHIELDTA_TAG);
							}else{              //屏蔽操作
								UIUtils.showToast(getApplicationContext(), bean.msg);
								mIsShield_btn.setText(FOLLOWTA_TAG);
								EventBus.getDefault().post(new RefurbishDTItem(mUid ,GlobalConstant.SHIELD_TAG));  //该uid的动态本地全部删除
							}
						}else{
							UIUtils.showToast(getApplicationContext(), bean.msg);
						}
					}
				});
		
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
	 * @param voices_model
	 * @param dt
	 */
	private void playVideo(ArrayList<Voices> voices) {
		if (mIsplay) {
			mIsplay = false;
			Voices_model voice_model = DynamicShowDao.getInstance().getSingleItem_voice(DynamicShowDao.COLUMN_NAME_ID, new String[]{voices.get(0).id});
			if(voice_model != null && voice_model.getLocalSrc() !=null && voice_model.getSc() !=null){
				DynamicUtlis.getInstance().playVoice(mVoicePlay,voice_model, mVoiceBtn, TAG);
			}else{
				DynamicUtlis.getInstance().playVoice(mVoicePlay,transferFormat_voices(voices), mVoiceBtn, TAG);
			}
		} else {
			mIsplay = true;
			DynamicUtlis.getInstance().stopPlayVoice(mVoiceBtn,
					transferFormat_voices(voices), mVoicePlay, TAG);
		}
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
				Voices_model voices_model = DynamicShowDao.getInstance().getSingleItem_voice(DynamicShowDao.COLUMN_NAME_ID,new String[] { event.getId() });
				/*
				 * 如果读取语音表中没有只有id和本地路径就应该在此处补全模型插入数据库
				 */
				if(voices_model.getSrc() == null && voices_model.getSc() == null){
					for(int i = 0; i<mTlist.size() ; i++){
						ArrayList<Voices> voices = mTlist.get(i).voices;
						 if(voices!=null && voices.size()>0 && voices_model.getId().equals(voices.get(0).id)){
							 voices_model.setSc(voices.get(0).sc);
							 voices_model.setFilesize(voices.get(0).filesize);
							 voices_model.setSrc(voices.get(0).src);
							 DynamicShowDao.getInstance().singleItemUpdate(GlobalConstant.VOICE_TABLE, voices_model.getId(), null, null, null, null, voices_model);
							 break;
						 }
					}
				}
				DynamicUtlis.getInstance().playVoice(mVoicePlay, voices_model,mVoiceBtn, TAG);
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
	 * 点击好友头像进入好友界面
	 * 
	 * @param position
	 */
	public void getFrinedPage(int position) {
		if (!(mUser_Id.equals(mTlist.get(position).uid))) { // 当前是朋友发布的动态
			Intent intent = new Intent(mActivity, OtherDataActivity.class);
			intent.putExtra(GlobalConstant.COMMENT_ADD_FRIEND,
					mTlist.get(position).uid);
			startActivity(intent);
		}
	}

	/**
	 * 初始化popupwindow操作
	 * 
	 * @param dt
	 * @param position
	 */
	@SuppressWarnings("deprecation")
	private void initPopupWindow(ViewHolder_DT dt, int position) {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			dt.im_mydynamic_show_more.setEnabled(false);
		} else {
			dt.im_mydynamic_show_more.setEnabled(true);
		}
		View popupView = UIUtils.inflate(R.layout.popupwindow);
		initPopup(dt, popupView, position);
		int width = mActivity.getWindowManager().getDefaultDisplay().getWidth();
		int height = mActivity.getWindowManager().getDefaultDisplay()
				.getHeight();

		mPopupWindow = new PopupWindow(popupView, width * 460 / 720,
				height * 85 / 1280);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));
		mPopupWindow.setOutsideTouchable(true);
		popupView.setOnTouchListener(new MyPopup(dt, position));
		int[] location = new int[2];
		dt.im_mydynamic_show_more.getLocationOnScreen(location);
		mPopupWindow.showAtLocation(dt.im_mydynamic_show_more,
				Gravity.NO_GRAVITY, location[0] - mPopupWindow.getWidth(),
				location[1]);
	}
	
	/**
	 * PopupWindow 对话框中自选项的点击事件
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyPopup implements OnClickListener, OnTouchListener {

		private ViewHolder_DT dt; // 动态显示viewholder
		private int position; // 当前的条目位置

		public MyPopup(ViewHolder_DT dt, int position) {
			this.dt = dt;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_popup_delete: // 删除
				LayoutInflater lf = LayoutInflater.from(mActivity);
				RelativeLayout layout = (RelativeLayout) lf.inflate(R.layout.dialog_layout, null);
				showDialog(position, layout);
				popupWindowHite(dt);
				break;
			case R.id.btn_popup_retrans: // 转发、
				popup_button_retrans(position, dt);
				popupWindowHite(dt);
				break;
			case R.id.btn_popup_dz: // 点赞

				popup_button_dianZan(position, dt);
				popupWindowHite(dt);
				break;
			case R.id.btn_popup_message: // 信息
				popup_button_message(position, dt);
				popupWindowHite(dt);
				break;

			default:
				break;
			}

		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				dt.im_mydynamic_show_more.setEnabled(true);
			}
			return false;
		}

	}
	/**
	 * 初始化popupwindow控件
	 * 
	 * @param dta
	 * @param popupView
	 * @param position
	 */
	private void initPopup(ViewHolder_DT dt, View popupView, int position) {
		mBtn_popup_delete = $(popupView, R.id.btn_popup_delete);
		mBtn_popup_dz = $(popupView, R.id.btn_popup_dz);
		mBtn_popup_message = $(popupView, R.id.btn_popup_message);
		mBtn_popup_retrans = $(popupView, R.id.btn_popup_retrans);
		setPopup(position);

		mBtn_popup_dz.setText(getResources().getString(
				R.string.text_dynamic_praise));
		mBtn_popup_delete.setOnClickListener(new MyPopup(dt, position));
		mBtn_popup_dz.setOnClickListener(new MyPopup(dt, position));
		mBtn_popup_message.setOnClickListener(new MyPopup(dt, position));
		mBtn_popup_retrans.setOnClickListener(new MyPopup(dt, position));
	}
	
	/**
	 * 点击条目中删除选项弹出对话框
	 * 
	 * @param layout
	 */
	private void showDialog(int position, RelativeLayout layout) {
		new MyDialog(mActivity, position, layout) {

			@Override
			public String getTitleText() {

				return mActivity.getResources().getString(
						R.string.text_is_delete);
			}

			@Override
			public void setButton_ok(int position) {
				dialog_button_delete(position);
			}
		};
	}
	/**
	 * 设置当前位置 状态 判断是否是自己发布的动态
	 * 
	 * @param position
	 */
	private void setPopup(int position) {

		if (mTlist.get(position).uid != null
				&& mUser_Id.equals(mTlist.get(position).uid)) {
			mBtn_popup_delete.setVisibility(View.VISIBLE);
			mBtn_popup_retrans.setVisibility(View.GONE);
		} else {
			mBtn_popup_delete.setVisibility(View.GONE);
			mBtn_popup_retrans.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏popupWindow对话框
	 * 
	 * @param dt
	 */
	private void popupWindowHite(ViewHolder_DT dt) {
		mPopupWindow.dismiss();
		mPopupWindow = null;
		dt.im_mydynamic_show_more.setEnabled(true);
	}


	@Override
	protected String getCurrentTitle() {
		if (!StringUtils.isEmpty(mUser_Id) && !StringUtils.isEmpty(mUid)&& mUser_Id.equals(mUid)) {
			return "我的动态";
		} else {
			return "好友动态";
		}

	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		// 取消当前的网络请求操作
		if (mHttp != null) {
			mHttp.stopHttpNET();
		}
		super.onDestroy();
	}

	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}

}
