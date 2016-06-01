package com.aliamauri.meat.activity.nearby_activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.SendFileManager;
import com.aliamauri.meat.Manager.UploadDynamicData;
import com.aliamauri.meat.bean.DTTagBean;
import com.aliamauri.meat.bean.DTTagBean.Cont;
import com.aliamauri.meat.db.Dbutils;
import com.aliamauri.meat.db.Dynamic_db.DynamicShowDao;
import com.aliamauri.meat.db.Dynamic_db.model.Dynamic_model;
import com.aliamauri.meat.db.Dynamic_db.model.HomeTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.RelayDynamicTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.eventBus.BroadCast_OK;
import com.aliamauri.meat.eventBus.UpdateShowMsg;
import com.aliamauri.meat.eventBus.sendDownLoadedOrder;
import com.aliamauri.meat.fragment.DynamicUtlis;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.listener.PressToSpeakListenr;
import com.aliamauri.meat.listener.VoicePlay;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.FlowLayout;
import com.aliamauri.meat.weight.MyDialog;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 动态条目中，转发按钮的转发页面
 * 
 * @author limaokeji-windosc
 * 
 * 
 * 
 */
public class Retrans_DT extends Activity implements OnClickListener {
	public static final String TAG = "Retrans_DT";
	public static boolean mIsplay = true; // 播放或停止，
	private final int STATE_CRYPTONYM_NAME = 101; // 当前状态为匿名状态
	private final int STATE_REAL_NAME = 102; // 当前状态为实名状态

	private TextView mTv_retrans_icon_tag; // 匿名 或者实名状态按钮
	private TextView mTv_retrans_biaoqing; // 添加表情的功能按钮
	private TextView mTv_icon_fingerboard; // 键盘输入按钮按钮
	private EditText mEt_import_text; // 语言输入框
	private TextView mTv_press_speak; // 按下说话按钮
	private LinearLayout mLl_import_text; // 整体的输入框
	private TextView mTv_retrans_fasong; // 输入框文字的确认按钮
	private ListView mLv_retrans_content; // 用户转发的内容区域
	private int mCurrentState; // 设置默认状态
	private MyBaseAdapter mAdapter; // listview 的适配器
	private StringBuffer mBuffer; // 存储用户输入的信息
	private int height; // 屏幕高度
	private String mParams_infos; // 该字段值表示用户设置的文字信息
	private String mParams_isnm; // 该字段值表示用户设置的是否实名状态
	boolean isAddBtn; // 控制添加按钮的子布局显示与隐藏
	boolean isFingerboardBtn; // 键盘/语音的切换
	boolean isShowTextItem; // 判断是否显示输入文字条目
	private FrameLayout mFl_loading;
	private String mWd; // 伟度
	private String mJd; // 经度
	boolean isImportEdit; // 用于切换输入状态是用户输入动态还是编辑标签
	private ViewHolder_retrans_content mVh_r_c = null; // 用户转发内容的条目
	private ViewHolderVoice mVh_t_v = null; // 语音类型的条目
	private ViewHolderText mVh_t = null; // 文字类型的条目
	private ViewHolder_select_tag mVh_s_t = null; // 选择标签的条目
	private ViewHolder_show_tag mVh_sh_t = null;// 显示用户选择的标签

	private String[] mTag = new String[] { GlobalConstant.TAG_1,
			GlobalConstant.TAG_2, GlobalConstant.TAG_3, GlobalConstant.TAG_4,
			GlobalConstant.TAG_5, GlobalConstant.TAG_6 };
	private ArrayList<String> mTagLists; // 存储用户选择的标签
	private Dynamic_model mRetrans_user_tag; // 获取动态的数据
	private String mUser_id;// 当前用户的userId
	private VoicePlay mVoicePlay;
	@Override
	protected void onResume() {
		super.onResume();
		  MobclickAgent.onResume(this);
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_retrans_dt);
		EventBus.getDefault().register(this);
		getLocation();
		mUser_id = PrefUtils.getString(GlobalConstant.USER_ID, "");
		mRetrans_user_tag = (Dynamic_model) getIntent().getSerializableExtra(GlobalConstant.RETRANS_USER_TAG);
		mVoicePlay = new VoicePlay();
		mCurrentState = STATE_REAL_NAME; // 设置当前状态为实名状态
		isAddBtn = false; // 默认添加按钮的子布局不显示
		isFingerboardBtn = true; // 键盘/语音的切换 默认语音
		isShowTextItem = false; // 默认下不显示输入文字框
		isImportEdit = true; // 设置默认状态为编辑动态
		mHttpHelp = new HttpHelp();
		mBuffer = new StringBuffer();
		mTagLists = new ArrayList<String>();
		mTagLists.clear();
		inflater = LayoutInflater.from(this);
		height = getWindowManager().getDefaultDisplay().getHeight();
		initView();

		set_cryptonym_or_realname_state(); // 设置切换匿名或者实名下的操作
		setFingerboardOrVoice(); // 点击键盘按钮进行键盘和语音输入的切换
		initTagNet();
	}
	/**
	 * 获取当前的位置
	 */
	private void getLocation() {
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION, "0&&0").split("&&");
		mWd = String.valueOf(location[0]);
		mJd = String.valueOf(location[location.length-1]);
	}
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	/**
	 * 保存数据到服务器中
	 */
	private void saveDataToDB() {
		if(mSpeakPath == null && mParams_infos == null){
			UIUtils.showToast(getApplicationContext(), "写点内容吧~");
			return;
		}
	
		setRelayModel2Db();
		setVoiceModel2Db();
		setDynamicModel2Db();
		addIndexTable2Db();
		if(mVoice_models != null && mVoice_models.size()>0){
			SendFileManager.getInstance().startUpTask(mDynamicId);
		}else{
			new UploadDynamicData().uploadDataToNet(mDynamicId);
		}
		EventBus.getDefault().post(new BroadCast_OK());  //发布新动态广播
		EventBus.getDefault().post(new UpdateShowMsg(mRetrans_user_tag.id, "1", null, null, null));//更新动态展示处的转发数
		finish();
	}
	
	/**
	 * 添加id到索引表
	 */
	private void addIndexTable2Db() {
		ArrayList<String> ids = new ArrayList<>();
		ids.add(mDynamicId);
		if (ids.size()>0) {
			DynamicShowDao.getInstance().listItemInsert_index(
					DynamicShowDao.COLUMN_NAME_NEW_DYNAMIC_ID, ids);
			DynamicShowDao.getInstance().listItemInsert_index(
					DynamicShowDao.COLUMN_NAME_FRIEND_DYNAMIC_ID, ids);
		}
	}
	private String mDynamicId = null; // 该条动态的id

	/**
	 * 设置动态数据到数据库中
	 */
	private void setDynamicModel2Db() {
		mDynamicId = "bbb" + getCurrentTime();
		HomeTable_model home = new HomeTable_model();
		home.setId(mDynamicId);
		home.setCreatetime(getCurrentTime());
		home.setDistance("0km");
		home.setDz("0");
		home.setLocalFace(DynamicUtlis.getInstance().getHead_icon());
		home.setInfos(mParams_infos);
		home.setIsnm(mParams_isnm);
		home.setZf("0");
		home.setWd(mWd);
		home.setJd(mJd);
		home.setUpdate_type(GlobalConstant.UPLOADTYPE_1);
		home.setPj("0");
		home.setYd("0");
		home.setType("2");
			home.setNickname(PrefUtils.getString(GlobalConstant.USER_NICKNAME, null));
		home.setTags(getUserTags());
		home.setUid(PrefUtils.getString(GlobalConstant.USER_ID, null));
		home.setVoices(getVoice_ids());
		home.setZfinfox(mModel_relay.getId());
		DynamicShowDao.getInstance().singleItemInsert(home, null, null, null,
				null);
	}

	/**
	 * 获取所有语音的id
	 * 
	 * @return
	 */
	private String getVoice_ids() {
		if (mVoice_models == null) {
			return null;
		}
		String voiceId = null;
		for (int i = 0; i < mVoice_models.size(); i++) {
			voiceId = mVoice_models.get(i).getId();
		}
		return voiceId;
	}

	ArrayList<Voices_model> mVoice_models = null;
	/**
	 * 设置语音数据到数据库中
	 */
	private void setVoiceModel2Db() {
		if (mSpeakPath == null) {
			return;
		}
		Voices_model voices_model = new Voices_model();
		mVoice_models = new ArrayList<>();
		if (mSpeakDuration != null) {
			voices_model.setSc(mSpeakDuration);
		}
		if (mSpeakSize != null) {
			voices_model.setFilesize(mSpeakSize);
		}
		if (mSpeakPath != null) {
			voices_model.setLocalSrc(mSpeakPath);
		}
		voices_model.setId("voi" + getCurrentTime());
		mVoice_models.add(voices_model);
		DynamicShowDao.getInstance().singleItemInsert(null, null, null, null,mVoice_models);
	}


	RelayDynamicTable_model mModel_relay = null;
	/**
	 * 设置转发数据到数据库中
	 */
	private void setRelayModel2Db() {
		mModel_relay = new RelayDynamicTable_model();
		mModel_relay.setId("rel"+getCurrentTime());
		mModel_relay.setCreatetime(mRetrans_user_tag.createtime);
		if (mRetrans_user_tag.zfinfox == null) {
			mModel_relay.setFlid(mRetrans_user_tag.id);
			mModel_relay.setFuid(mRetrans_user_tag.uid);
			mModel_relay.setFnickname(mRetrans_user_tag.nickname);
			mModel_relay.setDz(mRetrans_user_tag.dz);
			mModel_relay.setInfos(mRetrans_user_tag.infos);
			mModel_relay.setIsnm(mRetrans_user_tag.isnm);
			mModel_relay.setIsopen(mRetrans_user_tag.isopen);
			mModel_relay.setJd(mRetrans_user_tag.jd);
			mModel_relay.setPj(mRetrans_user_tag.pj);
			mModel_relay.setRelinfo(mRetrans_user_tag.relinfo);
			mModel_relay.setTags(mRetrans_user_tag.tags);
			mModel_relay.setType(mRetrans_user_tag.type);
			mModel_relay.setUid(mRetrans_user_tag.uid);
			mModel_relay.setWd(mRetrans_user_tag.wd);
			mModel_relay.setYd(mRetrans_user_tag.yd);
			mModel_relay.setZf(mRetrans_user_tag.zf);
			mModel_relay.setZfinfo(mRetrans_user_tag.zfinfo);
			mModel_relay.setImgs(getImgIds(mRetrans_user_tag.imgs));
			mModel_relay.setVideos(getVideoIds(mRetrans_user_tag.videos));
			mModel_relay.setVoices(getVoiceIds(mRetrans_user_tag.voices));
		} else {
			mModel_relay.setFlid(mRetrans_user_tag.zf_id);
			mModel_relay.setFuid(mRetrans_user_tag.zf_uid);
			mModel_relay.setFnickname(mRetrans_user_tag.zf_fnickname);
			mModel_relay.setDz(mRetrans_user_tag.zf_dz);
			mModel_relay.setInfos(mRetrans_user_tag.zf_infos);
			mModel_relay.setIsnm(mRetrans_user_tag.zf_isnm);
			mModel_relay.setIsopen(mRetrans_user_tag.zf_isopen);
			mModel_relay.setJd(mRetrans_user_tag.zf_jd);
			mModel_relay.setPj(mRetrans_user_tag.zf_pj);
			mModel_relay.setRelinfo(mRetrans_user_tag.zf_relinfo);
			mModel_relay.setTags(mRetrans_user_tag.zf_tags);
			mModel_relay.setType(mRetrans_user_tag.zf_type);
			mModel_relay.setUid(mRetrans_user_tag.zf_uid);
			mModel_relay.setWd(mRetrans_user_tag.zf_wd);
			mModel_relay.setYd(mRetrans_user_tag.zf_yd);
			mModel_relay.setZf(mRetrans_user_tag.zf_zf);
			mModel_relay.setZfinfo(mRetrans_user_tag.zf_zfinfo);
			mModel_relay.setImgs(getImgIds(mRetrans_user_tag.zf_imgs));
			mModel_relay.setVideos(getVideoIds(mRetrans_user_tag.zf_videos));
			mModel_relay.setVoices(getVoiceIds(mRetrans_user_tag.zf_voices));
		}
		DynamicShowDao.getInstance().singleItemInsert(null, null, mModel_relay, null, null);
	}
	/**
	 * 获取语音id集合
	 * @param voices
	 * @return
	 */
	private String getVoiceIds(ArrayList<Voices_model> voices) {
		if(voices == null){
			return null;
		}
		List<String> list = new ArrayList<>();
		for (Voices_model model : voices) {
			list.add(model.getId());
		}
		return Dbutils.jionFileName(list);
	}

	/**
	 * 获取视频id集合
	 * @param videos
	 * @return
	 */
	private String getVideoIds(ArrayList<Videos_model> videos) {
		if(videos == null){
			return null;
		}
		List<String> list = new ArrayList<>();
		for (Videos_model model : videos) {
			list.add(model.getId());
		}
		return Dbutils.jionFileName(list);
	}

	/**
	 * 获取照片id
	 * 
	 * @param imgs
	 * @return
	 */
	private String getImgIds(ArrayList<Imgs_model> imgs) {
		if(imgs == null){
			return null;
		}
		List<String> list = new ArrayList<>();
		for (Imgs_model model : imgs) {
			list.add(model.getId());
		}
		return Dbutils.jionFileName(list);
	}

	/**
	 * 获取当前发布的时间戳
	 * 
	 * @return
	 */
	private String getCurrentTime() {

		return String.valueOf(System.currentTimeMillis());
	}

	/*
	 * 将用户选择的标签设置成字符串
	 */
	private String getUserTags() {
		StringBuffer bufferParams_tag = new StringBuffer();

		for (int i = 0; i < mTagLists.size(); i++) {
			bufferParams_tag.append(mTagLists.get(i));
			if (i != mTagLists.size() - 1) {
				bufferParams_tag.append("|||");
			}
		}
		return bufferParams_tag.toString();
	}

	/**
	 * 用户选择标签，从网络获取，，（换一换）
	 */
	private void swapFormNetworkDate() {
		initTagNet();
	}

	// ************************各个功能键**************************************
	/**
	 * 设置添加表情按钮
	 */
	private void addBiaoqingBtn() {
		UIUtils.showToast(this, "我是添加表情按钮");

	}

	/**
	 * 删除用户说过的语音
	 */
	private void deleteVoice() {
		mSpeakDuration = null;
		mSpeakPath = null;
		mSpeakSize = null;
		delete_file(mSpeakPath);
		setVoiceitem_hide();
	}

	TextView mVoiceBtn;// 播放语音按钮

	/**
	 * 设置被转发的语音
	 * 
	 * @param v
	 */
	private void playRetrans_voice() {
		if (mRetrans_user_tag.zfinfox != null) { // 多次转发的内容
			play_video(mRetrans_user_tag.zf_voices.get(0));
		} else { // 初次转发的内容
			play_video(mRetrans_user_tag.voices.get(0));
		}

	}

	boolean mIsplay_retrans = true; // 播放或停止，

	/**
	 * 点击播放语音，在次点击停止播放
	 * 
	 * @param v
	 * 
	 * @param voices_model
	 * @param dt
	 */
	public void play_video(Voices_model voices_model) {
		if (mVoiceBtn == null) {
			return;
		}
		if (mIsplay_retrans) {
			mIsplay_retrans = false;
			DynamicUtlis.getInstance().playVoice(mVoicePlay, voices_model,
					mVoiceBtn, TAG);
		} else {
			mIsplay_retrans = true;
			DynamicUtlis.getInstance().stopPlayVoice(mVoiceBtn, voices_model,
					mVoicePlay, TAG);
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
				mIsplay_retrans = true;
			}
		} else {
			mIsplay_retrans = true;
		}
	}

	@Override
	public void onPause() {
		if (mVh_t_v !=null && mVh_t_v.tv_show_text != null && mVoicePlay != null && !mIsplay) { // 界面消失的时候判断是否正在播放语音
			DynamicUtlis.getInstance().stopPlayVoice(mVh_t_v.tv_show_text,
					null, mVoicePlay, TAG);
		}

		if (!mIsplay_retrans) { // 界面消失的时候判断是否正在播放语音(转发语音)
			DynamicUtlis.getInstance().stopPlayVoice(mVoiceBtn, null,
					mVoicePlay, TAG);
		}
		super.onPause();
		 MobclickAgent.onPause(this);
	}

	private HttpHelp mHttpHelp;
	private List<Cont> mTagListsFromNet; // 获取网络提供的标签
	private final int TAGPAGESIZE = 5; // 每次请求的数量
	private int tag_page = 1; // 页数

	/**
	 * 通过网络获取推荐的标签
	 */
	private void initTagNet() {
		mHttpHelp.sendGet(NetworkConfig.getDtTagUrl(TAGPAGESIZE, tag_page),
				DTTagBean.class, new MyRequestCallBack<DTTagBean>() {

					@Override
					public void onSucceed(DTTagBean bean) {
						if(mFl_loading.getVisibility() != View.GONE){
							mFl_loading.setVisibility(View.GONE);
						}
						if (bean == null || bean.cont == null) {
							UIUtils.showToast(Retrans_DT.this,
									"网络异常");
							return;
						}
						if (bean.cont.size() <= 0) {
							UIUtils.showToast(Retrans_DT.this,
									"没有更多内容了……");
							return;

						}
						mTagListsFromNet = bean.cont;
						if (tag_page <= 1) {
							mAdapter = new MyBaseAdapter();
							mLv_retrans_content.setAdapter(mAdapter);
						} else {
							mAdapter.notifyDataSetChanged();

						}

						++tag_page;
					}
				});
	}

	/**
	 * 获取输入框中的文字
	 */
	private void getImportText() {
		String UserImportMes = mEt_import_text.getText().toString().trim();
		if (!TextUtils.isEmpty(UserImportMes)) {
			if (isImportEdit) {
				set_import_retrans_text(UserImportMes);
			} else {
				set_import_tag(UserImportMes);
			}
			mEt_import_text.setText("");
		} else if (!isImportEdit && mTagLists.size() == 0) {
			mVh_sh_t.fl_content.setVisibility(View.GONE);
			mVh_sh_t.tv_hite_text.setVisibility(View.VISIBLE);
			set_margin_top_distance(0);
		}
		isImportEdit = true;// 用户添加自定义标签完成后切换为编辑文字动态状态
		setFingerboardOrVoice(); // 用户输入完后点击确定按钮切回语音状态
	}

	/**
	 * 设置添加按钮图片的距离
	 * 
	 * @param i
	 */
	private void set_margin_top_distance(int i) {

		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mVh_sh_t.iv_add_btn
				.getLayoutParams();
		layoutParams.setMargins(0, i, 0, 0);
		mVh_sh_t.iv_add_btn.setLayoutParams(layoutParams);
	}

	/**
	 * 设置用户输入自定义标签的文字
	 */
	private LayoutInflater inflater;

	private void set_import_tag(final String str) {
		set_margin_top_distance(height * 70 / 1280);
		if (mVh_sh_t == null) {
			Log.d("------>", "**********显示用户标签的holder为空*****vh_sh_t*****");
			return;
		}
		if (str.length() >= 10) {
			UIUtils.showToast(this, "标签长度不能超过10哦~~");
			if (mTagLists.size() == 0) {
				mVh_sh_t.fl_content.setVisibility(View.GONE);
				mVh_sh_t.tv_hite_text.setVisibility(View.VISIBLE);
				set_margin_top_distance(0);
			}
			return;
		}
		for (int i = 0; i < mTagLists.size(); i++) {
			if (str.equals(mTagLists.get(i))) {
				UIUtils.showToast(this, "已经输入过啦~~");
				return;
			}
		}

		if (mVh_sh_t.fl_content.getChildCount() < 5) {
			final FrameLayout fl = (FrameLayout) inflater.inflate(
					R.layout.add_tag_layout, mVh_sh_t.fl_content, false);
			TextView tv_retrans_dt_tag = $(fl, R.id.tv_retrans_dt_tag);
			tv_retrans_dt_tag.setText(str);
			mVh_sh_t.fl_content.addView(fl);
			mTagLists.add(str); // 将用户选择的标签进行存储
			/**
			 * 点击清除按钮将选择的标签进行删除
			 */
			$(fl, R.id.iv_retrans_dt_delete_icon).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							mTagLists.remove(str);
							mVh_sh_t.fl_content.removeView(fl);
							if (mVh_sh_t.fl_content.getChildCount() == 0) {
								mVh_sh_t.fl_content.setVisibility(View.GONE);
								mVh_sh_t.tv_hite_text
										.setVisibility(View.VISIBLE);
								set_margin_top_distance(0);
							}

						}
					});

		} else {
			UIUtils.showToast(this, "标签最多只能输入5个~~~");
			isImportEdit = true;// 用户添加自定义标签完成后切换为编辑文字动态状态
		}

	}

	/**
	 * 设置输入动态文本的文字
	 * 
	 * @param str
	 */
	private void set_import_retrans_text(String str) {
		if (mVh_t == null) {
			return;
		}
		mVh_t.iv_item_text_icon_delete.setVisibility(View.VISIBLE);
		String userMessage = mBuffer.append(str).toString();
		mVh_t.tv_item_text_hite.setText(userMessage);
		mParams_infos = userMessage;
	}

	/**
	 * 设置语音子布局的显示与隐藏
	 * 
	 * @param length
	 */
	private void setVoiceitem_show(String length) {
		mVh_t_v.tv_hint.setVisibility(View.GONE);
		mVh_t_v.ll_voice_list.setVisibility(View.VISIBLE);
		UIUtils.setVideoShape(mVh_t_v.tv_show_length, length, this);
		mVh_t_v.tv_show_text.setText(StringUtils.secToTime((int) Float
				.parseFloat(length)));
	}

	/**
	 * 设置语音子布局的显示与隐藏
	 */
	private void setVoiceitem_hide() {
		mVh_t_v.tv_hint.setVisibility(View.VISIBLE);
		mVh_t_v.ll_voice_list.setVisibility(View.GONE);
	}

	class MyBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (isShowTextItem) {
				return 5;
			} else {
				return 4;
			}
		}

		@Override
		public int getItemViewType(int position) {
			if (isShowTextItem) {

				return item_type_1(position);
			} else {
				return item_type_2(position);
			}
		}

		@Override
		public int getViewTypeCount() {
			return GlobalConstant.LV_CONTENT_TYPE_TOTE;

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
			int type = getItemViewType(position);

			if (convertView == null) {

				switch (type) {
				case GlobalConstant.ITEM_TYPE_VOICE:
					convertView = set_item_type_voice(convertView);// 设置当前条目为语音的状态
					break;

				case GlobalConstant.ITEM_TYPE_TEXT:

					convertView = set_item_type_text(convertView);// 设置当前条目为文字状态
					if (isShowTextItem) {
						convertView.setVisibility(View.VISIBLE);
					} else {
						convertView.setVisibility(View.GONE);
					}
					break;

				case GlobalConstant.ITEM_TYPE_RETRANS_CONTENT:
					convertView = set_item_type_retrans_content(convertView);// 设置当前的条目为用户转发的内容
					break;
				case GlobalConstant.ITEM_TYPE_SHOW_TAG:
					convertView = set_item_type_show_tag(convertView);// 设置当前的条目为显示用户的标签
					break;
				case GlobalConstant.ITEM_TYPE_SELECT_TAG:
					convertView = set_item_type_select_tag(convertView);// 设置当前的条目为显示用户选择的标签
					break;
				}

			} else {
				switch (type) {
				case GlobalConstant.ITEM_TYPE_VOICE:
					mVh_t_v = (ViewHolderVoice) convertView.getTag();
					break;
				case GlobalConstant.ITEM_TYPE_TEXT:
					mVh_t = (ViewHolderText) convertView.getTag();
					break;
				case GlobalConstant.ITEM_TYPE_RETRANS_CONTENT:
					mVh_r_c = (ViewHolder_retrans_content) convertView.getTag();
					break;
				case GlobalConstant.ITEM_TYPE_SHOW_TAG:
					mVh_sh_t = (ViewHolder_show_tag) convertView.getTag();
					break;
				case GlobalConstant.ITEM_TYPE_SELECT_TAG:
					mVh_s_t = (ViewHolder_select_tag) convertView.getTag();

				}
			}
			switch (type) {
			case GlobalConstant.ITEM_TYPE_VOICE:
				init_item_type_voice(mVh_t_v);
				break;
			case GlobalConstant.ITEM_TYPE_TEXT:
				init_item_type_text(mVh_t);
				break;
			case GlobalConstant.ITEM_TYPE_RETRANS_CONTENT:
				init_item_type_retrans_content(mVh_r_c);
				break;
			case GlobalConstant.ITEM_TYPE_SHOW_TAG:
				init_item_type_show_tag(mVh_sh_t);
				break;
			case GlobalConstant.ITEM_TYPE_SELECT_TAG:
				init_item_type_select_tag(mVh_s_t);

			}
			return convertView;
		}

	}
	/**
	 * 初始化文字条目
	 * @param holder
	 */
	public void init_item_type_text(ViewHolderText holder) {
		holder.tv_item_text_hite.setOnClickListener(this);
		holder.iv_item_text_icon_delete.setOnClickListener(this);
	}
	/**
	 * 初始化控件操作
	 */
	private void initView() {
		mFl_loading = $(R.id.fl_loading);
		$(R.id.iv_title_backicon).setOnClickListener(this);
		((TextView) $(R.id.tv_title_title)).setText(getResources().getString(
				R.string.text_retrabs_dynamic));
		$(R.id.ll_title_talk).setVisibility(View.GONE);
		$(R.id.iv_title_righticon).setVisibility(View.GONE);
		((TextView) $(R.id.tv_title_right)).setText(getResources().getString(
				R.string.text_dialog_btn_ok));
		$(R.id.tv_title_right).setOnClickListener(this);
		$(R.id.tv_title_right).setVisibility(View.VISIBLE);

		mTv_retrans_icon_tag = $(R.id.tv_retrans_icon_tag);
		mTv_retrans_biaoqing = $(R.id.tv_retrans_biaoqing);
		mTv_icon_fingerboard = $(R.id.tv_icon_fingerboard);
		mTv_press_speak = $(R.id.tv_press_speak);
		mTv_retrans_fasong = $(R.id.tv_retrans_fasong);
		mEt_import_text = $(R.id.et_import_text);
		mLv_retrans_content = $(R.id.lv_retrans_content);
		mLl_import_text = $(R.id.ll_import_text);

		mTv_retrans_icon_tag.setOnClickListener(this);
		mTv_retrans_biaoqing.setOnClickListener(this);
		mTv_icon_fingerboard.setOnClickListener(this);
		mTv_press_speak.setOnClickListener(this);
		mTv_retrans_fasong.setOnClickListener(this);

		mTv_press_speak.setOnTouchListener(new MyPressToSpeakListenr(this,
				mUser_id, $(R.id.recod_layout)));

	}

	private String mSpeakPath; // 语音路径
	private String mSpeakSize; // 语音大小
	private String mSpeakDuration; // 语音时长

	/**
	 * 录制语音操作
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyPressToSpeakListenr extends PressToSpeakListenr {

		public MyPressToSpeakListenr(Context context, String toChatUsername,
				View recordingContainer) {
			super(context, toChatUsername, recordingContainer);

		}

		@Override
		public void sendListener(String filePath, String fileName,
				String length, boolean isResend) {

			if (mSpeakDuration != null) {
				mSpeakDuration = null;
			}
			if (mSpeakPath != null) {
				delete_file(mSpeakPath);
			}
			if (mSpeakSize != null) {
				mSpeakSize = null;
			}
			mSpeakPath = filePath;
			mSpeakDuration = length;
			if (new File(filePath).exists()) {
				mSpeakSize = String.valueOf(new File(filePath)
						.getAbsolutePath().length());
			}
			setPressSpeakAction(length);

		}

	}

	/**
	 * 设置按下说话的按钮
	 * 
	 * @param length
	 */
	private void setPressSpeakAction(String length) {
		setVoiceitem_show(length);
		mLv_retrans_content.setSelection(0);
	}


	/**
	 * 根据路径删除文件
	 * 
	 * @param path
	 */
	private void delete_file(String path) {

		mVoicePlay.stopPlayVoice();
		if (path != null) {
			File speak = new File(path);
			if (speak.exists()) {
				speak.delete();
			}
		}
	}

	/**
	 * 点击播放语音，在次点击停止播放
	 */
	private void ClickPlayVoide() {
		if (mVh_t_v == null) {
			return;
		}
		if (mIsplay) {
			mIsplay = false;
			if (mSpeakPath != null) {
				mVoicePlay.playVoice(mSpeakPath);
				DynamicUtlis.getInstance().ShowTimer_bc(mVh_t_v.tv_show_text,
						mSpeakDuration, true);
			}
		} else {
			mIsplay = true;
			if (mSpeakPath != null) {
				mVoicePlay.stopPlayVoice();
				DynamicUtlis.getInstance().ShowTimer_bc(mVh_t_v.tv_show_text,
						mSpeakDuration, false);
			}
		}
	}

	/**
	 * 用户进入转发页面第一种情况显示输入文本
	 * 
	 * @param position
	 * @return
	 */
	private int item_type_1(int position) {
		if (position == 0) {
			return GlobalConstant.ITEM_TYPE_VOICE;
		} else if (position == 1) {
			return GlobalConstant.ITEM_TYPE_TEXT;
		} else if (position == 2) {
			return GlobalConstant.ITEM_TYPE_RETRANS_CONTENT;
		} else if (position == 3) {
			return GlobalConstant.ITEM_TYPE_SHOW_TAG;
		} else {
			return GlobalConstant.ITEM_TYPE_SELECT_TAG;
		}
	}

	/**
	 * 用户进入转发页面第二种情况显示输入文本
	 * 
	 * @param position
	 * @return
	 */
	public int item_type_2(int position) {
		if (position == 0) {
			return GlobalConstant.ITEM_TYPE_VOICE;
		} else if (position == 1) {
			return GlobalConstant.ITEM_TYPE_RETRANS_CONTENT;
		} else if (position == 2) {
			return GlobalConstant.ITEM_TYPE_SHOW_TAG;
		} else {
			return GlobalConstant.ITEM_TYPE_SELECT_TAG;
		}
	}

	/**
	 * 初始化第五种条目类型的控件
	 * 
	 * @param holder
	 */

	public void init_item_type_select_tag(ViewHolder_select_tag holder) {
		holder.btn_swap.setOnClickListener(this);
		if (mTagListsFromNet != null && mTagListsFromNet.size() != 0) {

			holder.tag_swap.removeAllViews();
			LayoutInflater from = LayoutInflater.from(this);
			for (int i = 0; i < mTagListsFromNet.size(); i++) {
				TextView tv = (TextView) from.inflate(R.layout.fl_text,
						holder.tag_swap, false);
				tv.setTag(mTag[i]);
				tv.setOnClickListener(this);
				tv.setText(mTagListsFromNet.get(i).name);
				holder.tag_swap.addView(tv);
			}
		}
	}

	/**
	 * 初始化第四种条目类型的控件
	 * 
	 * @param
	 */
	public void init_item_type_show_tag(ViewHolder_show_tag holder) {
		holder.iv_add_btn.setOnClickListener(this);

	}

	/**
	 * 初始化第三种条目类型的控件 网络请求 转发的视频的3个内容
	 * 
	 * @param holder
	 */
	public void init_item_type_retrans_content(ViewHolder_retrans_content holder) {
		if (mRetrans_user_tag == null) {
			return;
		}
		if (mRetrans_user_tag.zfinfox != null) {
			holder.tv_item_retrans_username.setText(mRetrans_user_tag.zf_fnickname);
			showRetrans_content(holder, mRetrans_user_tag.zf_videos,
					mRetrans_user_tag.zf_imgs, mRetrans_user_tag.zf_voices,
					mRetrans_user_tag.zf_infos);
		} else {
			holder.tv_item_retrans_username.setText(mRetrans_user_tag.nickname);
			showRetrans_content(holder, mRetrans_user_tag.videos,
					mRetrans_user_tag.imgs, mRetrans_user_tag.voices,
					mRetrans_user_tag.infos);
		}
	}

	/**
	 * 显示用户转发的内容
	 * 
	 * @param holder
	 * @param videos
	 * @param imgs
	 * @param voices
	 * @param infos
	 */
	private void showRetrans_content(ViewHolder_retrans_content holder,
			ArrayList<Videos_model> videos, ArrayList<Imgs_model> imgs,
			ArrayList<Voices_model> voices, String infos) {
		/*
		 * 判断显示视频和图片布局
		 */
		if (videos != null && videos.size() > 0) { // 情况1：图片和视频动态同时存在显示视频动态，或者只有视频的情况下,显示视频数据
			holder.iv_video_retrans_icon.setVisibility(View.VISIBLE);
			holder.iv_slbum_retrans_icon.setVisibility(View.GONE);
			mHttpHelp.showImage(holder.iv_video_retrans_icon, videos.get(0)
					.getSrcbg());

		} else if (imgs != null && imgs.size() > 0) { // 情况2：没有视频只有图片的情况下
			// ，显示图片数据
			holder.iv_video_retrans_icon.setVisibility(View.GONE);
			holder.iv_slbum_retrans_icon.setVisibility(View.VISIBLE);
			mHttpHelp.showImage(holder.iv_slbum_retrans_icon, imgs.get(0)
					.getImg());

		} else { // 情况3：视频和图片都没有的情况下， 都不显示
			holder.iv_video_retrans_icon.setVisibility(View.GONE);
			holder.iv_slbum_retrans_icon.setVisibility(View.GONE);
		}
		/*
		 * 判断显示语音和文字布局
		 */
		if (voices != null && voices.size() > 0) { // 情况1：语音和文字动态同时存在显示语音动态，或者只有语音的情况下
			// 显示语音数据
			holder.tv_item_retrans_voice.setVisibility(View.VISIBLE);
			holder.tv_item_retrans_content.setVisibility(View.GONE);
			holder.tv_item_retrans_voice.setText(StringUtils
					.secToTime((int) Float.parseFloat(voices.get(0).getSc())));
			UIUtils.setVideoShape_padding(holder.tv_item_retrans_voice, voices
					.get(0).getSc());
			holder.tv_item_retrans_voice.setOnClickListener(this);

		} else if (!StringUtils.isEmpty(infos)) { // 情况2：没有语音只有文字的情况下显示文字数据
			holder.tv_item_retrans_content.setVisibility(View.VISIBLE);
			holder.tv_item_retrans_voice.setVisibility(View.GONE);
			holder.tv_item_retrans_content.setText(infos);

		} else { // 情况3：语音和文字都没有的情况下， 都不显示
			holder.tv_item_retrans_voice.setVisibility(View.GONE);
			holder.tv_item_retrans_content.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化第一种条目类型的控件 `
	 * 
	 * @param holder
	 */
	public void init_item_type_voice(ViewHolderVoice holder) {
		holder.iv_delete_icon.setOnClickListener(this);
		holder.tv_show_length.setOnClickListener(this);
	}

	/**
	 * 设置当前的条目为显示用户选择的标签
	 * 
	 * @param convertView
	 * @return
	 */
	public View set_item_type_select_tag(View view) {
		mVh_s_t = new ViewHolder_select_tag();
		view = UIUtils.inflate(R.layout.item_select_tag);
		mVh_s_t.btn_swap = $(view, R.id.tv_title_button_swap);
		mVh_s_t.tag_swap = $(view, R.id.fl_show_net_tag);

		view.setTag(mVh_s_t);
		return view;
	}

	/**
	 * 设置当前的条目为显示用户的标签
	 * 
	 * @param convertView
	 * @return
	 */
	public View set_item_type_show_tag(View view) {
		mVh_sh_t = new ViewHolder_show_tag();
		view = UIUtils.inflate(R.layout.item_show_tag);
		mVh_sh_t.fl_content = $(view, R.id.fl_show_tag);
		mVh_sh_t.iv_add_btn = $(view, R.id.iv_add_tag_2);
		mVh_sh_t.tv_hite_text = $(view, R.id.tv_hite_text);

		view.setTag(mVh_sh_t);
		return view;
	}

	/**
	 * 设置当前的条目为用户转发的内容
	 * 
	 * @param convertView
	 * @return
	 */
	public View set_item_type_retrans_content(View view) {
		mVh_r_c = new ViewHolder_retrans_content();
		view = UIUtils.inflate(R.layout.item_retrans_content);
		mVh_r_c.iv_video_retrans_icon = $(view, R.id.iv_video_retrans_icon);
		mVh_r_c.iv_slbum_retrans_icon = $(view, R.id.iv_slbum_retrans_icon);
		mVh_r_c.tv_item_retrans_username = $(view,
				R.id.tv_item_retrans_username);
		mVh_r_c.tv_item_retrans_content = $(view, R.id.tv_item_retrans_content);
		mVh_r_c.tv_item_retrans_voice = $(view, R.id.tv_item_retrans_voice);
		view.setTag(mVh_r_c);
		return view;
	}

	/**
	 * 设置当前条目为文字状态
	 * 
	 * @param mVh_t_v
	 * @param convertView
	 * @return
	 */
	public View set_item_type_text(View view) {
		mVh_t = new ViewHolderText();
		view = UIUtils.inflate(R.layout.item_retrans_dt_text);
		mVh_t.rl_item_wrod = $(view, R.id.rl_item_wrod);
		mVh_t.tv_item_text_hite = $(view, R.id.tv_item_text_hite);
		mVh_t.iv_item_text_icon_delete = $(view, R.id.iv_item_text_icon_delete);
		view.setTag(mVh_t);
		return view;
	}

	/**
	 * 设置当前条目为语音的状态
	 * 
	 * @param vh_v
	 * @param convertView
	 * @return
	 */
	public View set_item_type_voice(View view) {
		mVh_t_v = new ViewHolderVoice();
		view = UIUtils.inflate(R.layout.item_retrans_dt_voice);
		mVh_t_v.tv_hint = $(view, R.id.tv_item_voice_hite);
		mVh_t_v.tv_show_length = $(view, R.id.tv_item_voice_show_length);
		mVh_t_v.tv_show_text = $(view, R.id.tv_item_voice_show_text);
		mVh_t_v.iv_delete_icon = $(view, R.id.iv_item_voice_icon_delete);
		mVh_t_v.ll_voice_list = $(view, R.id.ll_item_voice_list);

		view.setTag(mVh_t_v);
		return view;

	}

	/**
	 * 第一种条目类型的viewholder
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class ViewHolderVoice {
		TextView tv_hint;
		TextView tv_show_length;
		TextView tv_show_text;
		ImageView iv_delete_icon;
		LinearLayout ll_voice_list;
	}

	/**
	 * 第二种条目类型的viewholder
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class ViewHolderText {
		public ImageView iv_item_text_icon_delete;
		public RelativeLayout rl_item_wrod;
		public TextView tv_item_word;// 只是用于显示
		public TextView tv_item_text_hite;//

	}

	/**
	 * 第三种条目类型的viewholder
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class ViewHolder_retrans_content {

		public TextView tv_item_retrans_voice; // 语音内容
		public TextView tv_item_retrans_content; // 文字内容
		public TextView tv_item_retrans_username; // 转发人的名字
		public ImageView iv_slbum_retrans_icon; // 照片预览图
		public ImageView iv_video_retrans_icon; // 视频预览图

	}

	/**
	 * 第四种条目类型的viewholder
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class ViewHolder_show_tag {
		public FlowLayout fl_content;
		public ImageView iv_add_btn;
		public TextView tv_hite_text;
	}

	/**
	 * 第五种条目类型的viewholder
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class ViewHolder_select_tag {

		public TextView btn_swap; // 换一换
		public FlowLayout tag_swap; // 盛有标签的布局

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_backicon:
			finishA_closeK();
			break;
		case R.id.tv_title_right:
			saveDataToDB(); // 设置确认发布动态操作
			break;
		case R.id.tv_retrans_biaoqing:
			addBiaoqingBtn(); // 设置添加表情按钮
			break;
		case R.id.tv_item_text_hite:
		case R.id.tv_icon_fingerboard:
			setFingerboardOrVoice(); // 进行键盘或者语音的切换

			break;
		case R.id.tv_retrans_fasong:
			getImportText();// 获取输入框的文字

			break;

		case R.id.tv_retrans_icon_tag:
			show_cryptonym_or_realname_dialog();// 匿名或者实名对话框

			break;
		case R.id.iv_item_voice_icon_delete:
			deleteVoice(); // 删除用户说过的语音

			break;
		case R.id.tv_title_button_swap:
			swapFormNetworkDate(); // 换一换功能，更换标签的文字

			break;
		case R.id.tv_tag_text_click:
			setClickTextAction(v); // 设置标签点击后的动作

			break;
		case R.id.iv_add_tag_2:
			addEditTagItem();// 用户添加自定义标签

			break;
		case R.id.tv_item_retrans_voice:
			mVoiceBtn = (TextView) v;
			playRetrans_voice();
			break;
		case R.id.tv_item_voice_show_length: // 点击播放语音功能
			ClickPlayVoide();
			break;
		case R.id.iv_item_text_icon_delete: //删除发布文字
			deleteText(v);
			break;
		default:
			break;
		}

	}
	/**
	 * 删除用户发布的文字
	 * @param v 
	 */
	private void deleteText(View v) {
		if(mParams_infos !=null){
			mParams_infos = null;
			mBuffer.setLength(0);
			v.setVisibility(View.GONE);
			if(mVh_t !=null){
				mVh_t.tv_item_text_hite.setText(getResources().getText(R.string.text_text_hite));
			}
		}
	}
	/**
	 * 添加自定义标签
	 * 
	 */
	private void addEditTagItem() {
		isImportEdit = false;
		showSelectTag();
		if (!isImportEdit) {
			if (isFingerboardBtn != false) { // 判断当前状态是否为键盘输入状态
				isFingerboardBtn = false;
			}
			setFingerboardOrVoice(); // 此时为编辑自定义标签
		}
		set_margin_top_distance(height * 70 / 1280);
	}

	/**
	 * 将提示文字布局隐藏 将选择的标签布局显示
	 */
	private void showSelectTag() {
		mVh_sh_t.fl_content.setVisibility(View.VISIBLE);
		mVh_sh_t.tv_hite_text.setVisibility(View.GONE);
	}

	/**
	 * 点击标签，在用户条处显示出来
	 * 
	 * @param v
	 */
	private void setClickTextAction(View v) {
		switch ((String) (v.getTag())) {
		case GlobalConstant.TAG_1:

			showSelectTag();
			set_import_tag(mTagListsFromNet.get(0).name);
			break;
		case GlobalConstant.TAG_2:

			showSelectTag();
			set_import_tag(mTagListsFromNet.get(1).name);
			break;
		case GlobalConstant.TAG_3:
			showSelectTag();
			set_import_tag(mTagListsFromNet.get(2).name);
			break;
		case GlobalConstant.TAG_4:

			showSelectTag();
			set_import_tag(mTagListsFromNet.get(3).name);
			break;
		case GlobalConstant.TAG_5:

			showSelectTag();
			set_import_tag(mTagListsFromNet.get(4).name);
			break;
		case GlobalConstant.TAG_6:

			showSelectTag();
			set_import_tag(mTagListsFromNet.get(5).name);
			break;

		default:
			break;
		}

	}

	/**
	 * 销毁当前activity并且关闭软键盘
	 */
	private void finishA_closeK() {
		KeyBoardUtils.closeKeybord(mEt_import_text, this);
		mBuffer = null;
		if (mTagLists != null) {
			mTagLists.clear();
			mTagLists = null;
		}
		finish();
	}

	/**
	 * 匿名或者实名对话框
	 */
	private void show_cryptonym_or_realname_dialog() {
		LayoutInflater li = LayoutInflater.from(this);
		RelativeLayout rl = (RelativeLayout) li.inflate(R.layout.dialog_layout,
				null);
		new MyDialog(this, 0, rl) {

			@Override
			public void setButton_ok(int i) {
				set_cryptonym_or_realname_state(); // 设置切换匿名或者实名下的操作

			}

			@Override
			public String getTitleText() {

				switch (mCurrentState) {
				case STATE_REAL_NAME: // 实名状态
					return Retrans_DT.this.getResources().getString(
							R.string.text_is_real_name);

				case STATE_CRYPTONYM_NAME: // 匿名状态

					return Retrans_DT.this.getResources().getString(
							R.string.text_is_crytonym_name);
				default:
					return null;
				}
			}
		};
	}

	/**
	 * 点击键盘按钮进行键盘和语音输入的切换
	 */
	private void setFingerboardOrVoice() {
		if (isFingerboardBtn) { // 语音状态
			isFingerboardBtn = false;
			mTv_icon_fingerboard
					.setBackgroundResource(R.drawable.bg_tv_retrans_afingerboard);
			mTv_press_speak.setVisibility(View.VISIBLE);
			mLl_import_text.setVisibility(View.GONE);
			mTv_retrans_fasong.setVisibility(View.GONE);
			KeyBoardUtils.closeKeybord(mEt_import_text, this);
		} else { // 键盘状态
			isFingerboardBtn = true;
			// *********************点击键盘按钮显示输入文字的item*************************
			// isImportEdit 用于切换编辑标签和编辑动态
			if (mAdapter !=null && isShowTextItem != true && isImportEdit == true) {
				Log.d("------>", "**********当前是输入发布动态状态*************");
				isShowTextItem = true;
				mAdapter.notifyDataSetChanged();
			}
			// *********************点击键盘按钮显示输入文字的item*************************

			mTv_icon_fingerboard
					.setBackgroundResource(R.drawable.bg_tv_retrans_voice);
			mTv_press_speak.setVisibility(View.GONE);
			mLl_import_text.setVisibility(View.VISIBLE);
			mTv_retrans_fasong.setVisibility(View.VISIBLE);
			KeyBoardUtils.openKeybord(mEt_import_text, this);

		}

	}

	/**
	 * 设置当前聊天窗口的状态 匿名或者实名状态下
	 */
	private void set_cryptonym_or_realname_state() {
		switch (mCurrentState) {
		case STATE_REAL_NAME: // 实名状态
			mTv_retrans_icon_tag.setText(getResources().getString(
					R.string.text_cryptonym));
			mCurrentState = STATE_CRYPTONYM_NAME;
			mParams_isnm = "0";
			break;
		case STATE_CRYPTONYM_NAME: // 匿名状态
			mTv_retrans_icon_tag.setText(getResources().getString(
					R.string.text_real_name));
			mCurrentState = STATE_REAL_NAME;
			mParams_isnm = "1";
			break;

		default:
			break;
		}
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
