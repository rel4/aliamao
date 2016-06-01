package com.aliamauri.meat.activity.nearby_activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.SendFileManager;
import com.aliamauri.meat.activity.PhonePicActivity;
import com.aliamauri.meat.activity.IM.activity.ChatActivity;
import com.aliamauri.meat.activity.IM.activity.RecorderVideoActivity;
import com.aliamauri.meat.bean.DTTagBean;
import com.aliamauri.meat.bean.DTTagBean.Cont;
import com.aliamauri.meat.db.Dbutils;
import com.aliamauri.meat.db.Dynamic_db.DynamicShowDao;
import com.aliamauri.meat.db.Dynamic_db.model.HomeTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.eventBus.BroadCast_OK;
import com.aliamauri.meat.fragment.DynamicUtlis;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.listener.PressToSpeakListenr;
import com.aliamauri.meat.listener.VoicePlay;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.IconCompress;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.FlowLayout;
import com.aliamauri.meat.view.MyGridView;
import com.aliamauri.meat.view.MyPhoneOrVideoDialog;
import com.aliamauri.meat.weight.MyDialog;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 附近----动态----发布广播（发布动态）
 * 
 * @author limaokeji-windosc
 * 
 */
public class BroadCast_DT extends Activity implements OnClickListener {
	public static String TAG = "BroadCast_DT";
	public static boolean mIsplay = true; // 播放或停止，
	private final String TITLE_NAME = "发布动态";
	private final String RIGHT_BTN_NAME = "确认";
	private final int STATE_CRYPTONYM_NAME = 101; // 当前状态为匿名状态
	private final int STATE_REAL_NAME = 102; // 当前状态为实名状态
	private ImageView mIv_title_backicon; // 标题头左边回退按钮
	private TextView mTv_title_right; // 标题头右边发布按钮
	private TextView mTv_broadcast_icon_tag; // 匿名 或者实名状态按钮
	private TextView mTv_broadcast_biaoqing; // 添加表情的功能按钮
	private TextView mTv_broadcast_add; // 添加图片，视频按钮
	private TextView mTv_icon_fingerboard; // 键盘输入按钮按钮 
	private EditText mEt_import_text; // 语言输入框
	private TextView mTv_press_speak; // 按下说话按钮
	private LinearLayout mLl_import_text; // 整体的输入框
	private TextView mTv_broadcast_fasong; // 输入框文字的确认按钮
	private ListView mLv_broadcast_content; // 用户转发的内容区域
	private FrameLayout mFl_loading;
	private int mCurrentState; // 设置默认状态
	private MyBaseAdapter mLvAdapter; // listview 的适配器
	private StringBuffer mBuffer; // 存储用户输入的信息
	private LinearLayout mLl_select_photograph_video; // 照片与视频布局
	private TextView mTv_select_photograph; // 照片布局
	private TextView mTv_select_video; // 视频布局
	private int height; // 屏幕高度

	private boolean isFingerboardBtn; // 键盘/语音的切换
	private boolean isShowTextItem; // 判断是否显示输入文字条目
	private boolean isImportEdit; // 用于切换输入状态是用户输入动态还是编辑标签

	private ViewHolder_broadcast_content mVh_r_c = null; // 用户转发内容的条目
	private ViewHolderVoice mVh_t_v = null; // 语音类型的条目
	private ViewHolderText mVh_t = null; // 文字类型的条目
	private ViewHolder_select_tag mVh_s_t = null; // 选择标签的条目
	private ViewHolder_show_tag mVh_sh_t = null;// 显示用户选择的标签

	private MyPressToSpeakListenr myPressToSpeakListenr;

	private String[] mTag = new String[] { GlobalConstant.TAG_1,
			GlobalConstant.TAG_2, GlobalConstant.TAG_3, GlobalConstant.TAG_4,
			GlobalConstant.TAG_5, GlobalConstant.TAG_6 };
	private ArrayList<String> mTagLists; // 存储用户选择的标签
	private HttpHelp mHttpHelp;
	private MyAlbumListAdapter mAlbumAdapter; // 用户选择后的所有照片
	private String mUser_id;// 当前用户的userId

	// 使用照相机拍照获取图片
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
	// 使用相册中的图片
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
	// 从Intent获取图片路径的KEY
	public static final String KEY_PHOTO_PATH = "photo_path";

	/** 获取到的图片路径 */
	private String picPath;
	private Uri photoUri;
	private ArrayList<String> mAlbumLists; // 获取到用户选择的相片
	private VoicePlay mVoicePlay;
	@Override
	protected void onResume() {
		super.onResume();
		  MobclickAgent.onResume(this);
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setBackgroundDrawableResource(R.color.bg_greywhite);
		setContentView(R.layout.activity_broadcast);
		mUser_id = PrefUtils.getString(GlobalConstant.USER_ID, "");
		mVoicePlay = new VoicePlay();
		mCurrentState = STATE_REAL_NAME; // 设置当前状态为实名状态
		isFingerboardBtn = true; // 键盘/语音的切换 默认语音
		isImportEdit = true; // 设置默认状态为编辑动态
		isShowTextItem = false; // 默认下不显示输入文字框
		getLocation();
		mBuffer = new StringBuffer();
		mTagLists = new ArrayList<String>();
		mTagLists.clear();
		mHttpHelp = new HttpHelp();
		inflater = LayoutInflater.from(this);
		mLi = LayoutInflater.from(this);
		height = getWindowManager().getDefaultDisplay().getHeight();
		mAlbumLists = new ArrayList<String>();
		initView();
		set_cryptonym_or_realname_state(); // 设置切换匿名或者实名下的操作
		setFingerboardOrVoice(); // 点击键盘按钮进行键盘和语音输入的切换
		initTagNet();

	}

	private String mWd; // 伟度
	private String mJd; // 经度

	/**
	 * 获取当前的位置
	 */
	private void getLocation() {
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION, "0&&0").split("&&");
		mWd = String.valueOf(location[0]);
		mJd = String.valueOf(location[location.length-1]);
		
		
		
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

	private List<Cont> mTagListsFromNet; // 获取网络提供的标签
	private final int TAGPAGESIZE = 5;// 每次请求的数量
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
							UIUtils.showToast(BroadCast_DT.this,
									"网络异常");
							return;
						}
						if (bean.cont.size() <= 0) {
							UIUtils.showToast(BroadCast_DT.this,
									"没有更多内容了……");
							return;

						}
						mTagListsFromNet = bean.cont;
						if (tag_page <= 1) {
							mLvAdapter = new MyBaseAdapter();
							mLv_broadcast_content.setAdapter(mLvAdapter);
						} else {
							mLvAdapter.notifyDataSetChanged();

						}

						++tag_page;
					}
				});
	}

	/**
	 * 保存数据到服务器中
	 */
	private void saveDataToDB() {
		// 设置确认操作 显示正在上传图片的提示框 若当前状态图片为空，语音为空， 视频为空，应该禁止用户上传动态。
		if (mAlbumLists.size() <= 0 && mSpeakSize == null && mVideoPath == null) {
			UIUtils.showToast(BroadCast_DT.this, "添加点内容吧~~");
		} else {
			// 检查相册数量是否超过9张
			if (mAlbumLists.size() > 9) {
				UIUtils.showToast(this, "数量不能超过9张……");
			} else {
				setImgModel2Db();
				setVideoModel2Db();
				setVoiceModel2Db();
				setDynamicModel2Db();
				addIndexTable2Db();
				SendFileManager.getInstance().startUpTask(mDynamicId);
				EventBus.getDefault().post(new BroadCast_OK());
				finish();
			}
		}

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
		mDynamicId = "aaa" + getCurrentTime();
		HomeTable_model home = new HomeTable_model();
		home.setId(mDynamicId);
		home.setCreatetime(getCurrentTime());
		home.setLocalFace(DynamicUtlis.getInstance().getHead_icon());
		home.setInfos(mParams_infos);
		home.setIsnm(mParams_isnm);
		home.setZf("0");
		home.setWd(mWd);
		home.setJd(mJd);
		home.setUpdate_type(GlobalConstant.UPLOADTYPE_1);
		home.setPj("0");
		home.setDz("0");
		home.setDistance("0km");
		home.setYd("0");
		home.setType("1");
			home.setNickname(PrefUtils.getString(GlobalConstant.USER_NICKNAME, null));
		home.setTags(getUserTags());
		home.setUid(mUser_id);
		home.setImgs(getImg_ids());
		home.setVideos(getVideo_ids());
		home.setVoices(getVoice_ids());

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

	/**
	 * 获取所有视频的id
	 * 
	 * @return
	 */
	private String getVideo_ids() {
		if (mVideo_modelLists == null) {
			return null;
		}
		String videoId = null;
		for (int i = 0; i < mVideo_modelLists.size(); i++) {
			videoId = mVideo_modelLists.get(i).getId();
		}
		return videoId;

	}

	/**
	 * 获取照片id字符串
	 * 
	 * @return
	 */
	private String getImg_ids() {
		if (mImg_modelLists == null) {
			return null;
		}
		ArrayList<String> imgLists = new ArrayList<>();
		for (int i = 0; i < mImg_modelLists.size(); i++) {
			imgLists.add(mImg_modelLists.get(i).getId());
		}
		return Dbutils.jionFileName(imgLists);
	}

	/**
	 * 将用户选择的标签设置成字符串
	 */
	private String getUserTags() {
		if (mTagLists == null) {
			return null;
		}
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
	 * 获取当前发布的时间戳
	 * 
	 * @return
	 */
	private String getCurrentTime() {

		return String.valueOf(System.currentTimeMillis());
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
		DynamicShowDao.getInstance().ItemListInsert(null, null, null, null,
				mVoice_models);
	}

	ArrayList<Videos_model> mVideo_modelLists = null;

	/**
	 * 设置视频数据到数据库中
	 */
	private void setVideoModel2Db() {
		if (mVideoPath == null) {
			return;
		}
		Videos_model videos_model = new Videos_model();
		mVideo_modelLists = new ArrayList<>();
		if (mVideoBgPath != null) {
			videos_model.setLocalSrcbg(mVideoBgPath);
		}
		if (mVideoDuration != null) {
			videos_model.setSc(mVideoDuration);
		}
		if (mVideoPath != null) {
			videos_model.setLocalSrc(mVideoPath);
		}
		if (new File(mVideoPath).exists()) {
			videos_model.setFilesize(String.valueOf(new File(mVideoPath)
					.getAbsoluteFile().length()));
		}
		videos_model.setId("vid" + getCurrentTime());
		mVideo_modelLists.add(videos_model);

		DynamicShowDao.getInstance().ItemListInsert(null, null, null,
				mVideo_modelLists, null);
	}

	ArrayList<Imgs_model> mImg_modelLists = null;

	/**
	 * 设置相册数据到数据库中
	 */
	private void setImgModel2Db() {
		if (mAlbumLists != null && mAlbumLists.size() <= 0) {
			return;
		}
		Imgs_model img_model = null;
		mImg_modelLists = new ArrayList<>();
		for (int i = 0; i < mAlbumLists.size(); i++) {
			img_model = new Imgs_model();
			img_model.setLocalImgOri(mAlbumLists.get(i));
			img_model.setLocalImg(mAlbumLists.get(i));
			img_model.setId(i + "img" + getCurrentTime());
			mImg_modelLists.add(img_model);
		}
		DynamicShowDao.getInstance().ItemListInsert(null, mImg_modelLists,
				null, null, null);
	}

	/**
	 * 
	 * 添加视频按钮
	 */

	private void addVideo() {
		mCurrent_state = VIDEO_STATE;
		Intent intent = new Intent(this, RecorderVideoActivity.class);
		startActivityForResult(intent, ChatActivity.REQUEST_CODE_SELECT_VIDEO);
	}

	/**
	 * 设置添加表情按钮
	 */
	private void addBiaoqingBtn() {
		UIUtils.showToast(this, "我是添加表情按钮");

	}

	/**
	 * 设置按下说话的按钮
	 * 
	 * @param length
	 */
	private void setPressSpeakAction(String length) {
		setVoiceitem_show(length);
		mLv_broadcast_content.setSelection(0);
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
	 * 用户选择标签，从网络获取，，（换一换）
	 */
	private void swapFormNetworkDate() {
		initTagNet();
	}

	int camear_icon_num = 0; // 为每个拍照的图片设置不同的名字

	/**
	 * 添加照片按钮
	 */
	private void addPhone() {
		mCurrent_state = PHOTO_STATE;
		LinearLayout ll = (LinearLayout) mLi.inflate(
				R.layout.dialog_phone_or_video, null);
		new MyPhoneOrVideoDialog(this, ll) {

			@Override
			public void setButton_camear() { // 照相机
				show_photo_video_layout();
				++camear_icon_num;
				takePhoto();

			}

			@Override
			public void setButton_album() { // 相册
				show_photo_video_layout();
				// 进入选择照片界面
				Intent intent = new Intent(BroadCast_DT.this,
						PhonePicActivity.class);
				intent.putExtra(GlobalConstant.DT_TAG, "braodcast_dt");
				startActivity(intent);
			}

		};

	}

	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
		// 执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
			/***
			 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
			 * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
			 */
			ContentValues values = new ContentValues();
			photoUri = this.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
			/** ----------------- */
			startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
		} else {
			Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) { // 获取视频文件路径
			if (requestCode == ChatActivity.REQUEST_CODE_SELECT_VIDEO) { // 发送选择的文件
				if (data != null) {
					getVideoData(data);
					isShowalbumLists = true;
					onStart();
				}
			} else {
				doPhoto2(requestCode, resultCode, data); // 获取照片
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private String mVideoDuration = null; // 视频时长
	private String mVideoPath = null; // 视频路径
	private String mVideoBgPath = null;// 视频路径背景

	/**
	 * 获取视频数据
	 * 
	 * @param data
	 */
	private void getVideoData(Intent data) {
		mVideoDuration = String.valueOf(data.getIntExtra("dur", 0));
		mVideoPath = data.getStringExtra("path");
		File file = new File(PathUtil.getInstance().getImagePath()
				.getAbsolutePath(), "thvideo" + System.currentTimeMillis()
				+ ".jpg");
		Bitmap bitmap = null;
		FileOutputStream fos = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			bitmap = ThumbnailUtils.createVideoThumbnail(mVideoPath, 3);
			if (bitmap == null) {
				EMLog.d("chatactivity",
						"problem load video thumbnail bitmap,use default icon");
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.app_panel_video_icon);
			}
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			mVideoBgPath = file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}

		}
	}

	/**
	 * 选择图片后，获取图片的路径
	 * 
	 * @param requestCode
	 * @param data
	 */
	private void doPhoto2(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUESTCODE_CUTTING) {

			if (data != null) {
				savePic(data);
			}
		} else {
			Cursor cursor = this.getContentResolver().query(photoUri, null,
					null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				picPath = cursor.getString(1); // 图片文件路径 ;
				cursor.close();
			}
			if (picPath != null) {
				File f = new File(picPath);
				Uri originalUri = Uri.fromFile(f);
				startPhotoZoom(originalUri);
				// Bitmap bm = null;
				// bm = IconCompress.getimage(picPath);
				// faceFile = IconCompress.saveBitmap(bm,
				// GlobalConstant.HEAD_ICON_SAVEPATH, "backup.jpg");
				// bm.recycle();
				// bm = null;
				// updataFace();
			} else {
				Toast.makeText(this, "选择文件不正确!", Toast.LENGTH_LONG).show();

			}
		}
	}

	private static final int REQUESTCODE_CUTTING = 3;

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}

	private void savePic(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Bitmap bmCoompress = null;
			bmCoompress = IconCompress.comp(photo);
			IconCompress.saveBitmap(bmCoompress,
					GlobalConstant.DT_UPDATE_ICON_SAVEPATH, "dt_"
							+ camear_icon_num + ".jpg");

			if (CheckUtils.getInstance().isIcon(GlobalConstant.DT_UPDATE_ICON_SAVEPATH
					+ "dt_" + camear_icon_num + ".jpg")) {
				mAlbumLists.add(GlobalConstant.DT_UPDATE_ICON_SAVEPATH + "dt_"
						+ camear_icon_num + ".jpg");
			} else {
				UIUtils.showToast(this, "照片获取失败，请重新拍照……");
			}
		}

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mFl_loading = $(R.id.fl_loading);
		mIv_title_backicon = $(R.id.iv_title_backicon);
		((TextView) $(R.id.tv_title_title)).setText(TITLE_NAME);
		$(R.id.ll_title_talk).setVisibility(View.GONE);
		mTv_title_right = $(R.id.tv_title_right);
		mTv_title_right.setVisibility(View.VISIBLE);
		mTv_title_right.setText(RIGHT_BTN_NAME);
		$(R.id.iv_title_righticon).setVisibility(View.GONE);
		mIv_title_backicon.setOnClickListener(this);
		mTv_title_right.setOnClickListener(this);

		mTv_broadcast_icon_tag = $(R.id.tv_broadcast_icon_tag);
		mTv_broadcast_biaoqing = $(R.id.tv_broadcast_biaoqing);
		mTv_icon_fingerboard = $(R.id.tv_icon_fingerboard);
		mTv_press_speak = $(R.id.tv_press_speak);
		mTv_broadcast_fasong = $(R.id.tv_broadcast_fasong);
		mEt_import_text = $(R.id.et_import_text);
		mLv_broadcast_content = $(R.id.lv_broadcast_content);
		mLl_import_text = $(R.id.ll_import_text);
		mTv_broadcast_add = $(R.id.tv_broadcast_add);

		mLl_select_photograph_video = $(R.id.ll_select_photograph_video);
		mTv_select_photograph = $(R.id.tv_select_photograph);
		mTv_select_video = $(R.id.tv_select_video);

		mTv_select_photograph.setOnClickListener(this);
		mTv_select_video.setOnClickListener(this);
		mTv_broadcast_add.setOnClickListener(this);
		mTv_broadcast_icon_tag.setOnClickListener(this);
		mTv_broadcast_biaoqing.setOnClickListener(this);
		mTv_icon_fingerboard.setOnClickListener(this);
		mTv_broadcast_fasong.setOnClickListener(this);
		myPressToSpeakListenr = new MyPressToSpeakListenr(this, mUser_id,
				$(R.id.recod_layout));
		mTv_press_speak.setOnTouchListener(myPressToSpeakListenr);
	}

	/**
	 * 设置语音子布局的显示与隐藏
	 * 
	 * @param length
	 */
	private void setVoiceitem_show(String length) {
		if (mVh_t_v == null) {
			return;
		}
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
		if (mVh_t_v == null) {
			return;
		}
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
					convertView = set_item_type_broadcast_content(convertView);// 设置当前的条目为用户照片视频
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
					mVh_r_c = (ViewHolder_broadcast_content) convertView
							.getTag();
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
				init_item_type_broadcast_content(mVh_r_c);
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
	 * 初始化文字条目
	 * @param holder
	 */
	public void init_item_type_text(ViewHolderText holder) {
		holder.tv_item_text_hite.setOnClickListener(this);
		holder.iv_item_text_icon_delete.setOnClickListener(this);
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
	 * 初始化第三种条目类型的控件 用户发布的图片或视频的3个内容 图片和背景图之间： 如何有图片就将背景图隐藏，图片显示
	 * 
	 * 注：每次在onstart时将背景图显示，图片集隐藏
	 * 
	 * @param holder
	 */

	public void init_item_type_broadcast_content(ViewHolder_broadcast_content holder) {
		holder.tv_default_icon.setOnClickListener(this);
		holder.iv_add_photo_btn.setOnClickListener(this);
		holder.iv_video_add_btn.setOnClickListener(this);
		if (mAlbumLists != null && mAlbumLists.size()<=0 && mVideoBgPath == null) {
			return;
		}else{
			holder.tv_default_icon.setVisibility(View.GONE);
			holder.title_hite.setVisibility(View.GONE);
			holder.iv_video_add_btn.setVisibility(View.VISIBLE);
			switch (mCurrent_state) {
			case PHOTO_STATE:
				if (mAlbumLists.size() >= 1 && isShowalbumLists) {
					holder.gv_add_album_list.setVisibility(View.VISIBLE);
					mAlbumAdapter = new MyAlbumListAdapter(mAlbumLists);
					holder.gv_add_album_list.setAdapter(mAlbumAdapter);
					isShowalbumLists = false;
				}
				break;
			case VIDEO_STATE:
				if(mVideoBgPath !=null && mVideoPath !=null && isShowalbumLists){
					isShowalbumLists = false;
					holder.rl_video.setVisibility(View.VISIBLE);
					mHttpHelp.showImage(holder.iv_video_img, mVideoBgPath);
					holder.iv_video_del_icon.setTag(holder);
					holder.iv_video_del_icon.setOnClickListener(BroadCast_DT.this);
					if(mAlbumLists == null ||(mAlbumLists !=null && mAlbumLists.size()<=0)){
						holder.iv_add_photo_btn.setVisibility(View.VISIBLE);
					}
				}
				break;
			}
			
		}
	}

	/**
	 * 防止多次显示activity画面的时候多次添加相册中的照片、 在每次stop方法中将集合中的数据清空
	 */
	@Override
	protected void onStop() {
		MyApplication.setAlbumLists(null);// 将用户选择照片的集合清空
		if (mHttpHelp != null) {
			mHttpHelp.stopHttpNET();
		}
		super.onStop();
	}
	private boolean isShowalbumLists = false; // 防止每次显示键盘，更多部分的布局导致的刷新listview
	private final String PHOTO_STATE = "photo_state";  //添加照片状态
	private final String VIDEO_STATE = "video_state";  //添加视频状态
	private String mCurrent_state = null;
	@Override
	protected void onStart() {
		
		if(mLvAdapter != null && mCurrent_state!=null){
			switch (mCurrent_state) {
			case PHOTO_STATE:
				if ((MyApplication.getAlbumLists() != null && MyApplication.getAlbumLists().size()>0)||mAlbumLists.size()>0) {
					if(mVh_r_c !=null && mVh_r_c.iv_add_photo_btn.getVisibility() == View.VISIBLE ){
						mVh_r_c.iv_add_photo_btn.setVisibility(View.GONE);
					}
					if(MyApplication.getAlbumLists() != null){
						mAlbumLists.addAll(MyApplication.getAlbumLists());
					}
					isShowalbumLists = true;
					mLvAdapter.notifyDataSetChanged();
				}
				break;
			case VIDEO_STATE:
				if(mVideoBgPath !=null && mVideoPath !=null){
					isShowalbumLists = true;
					mLvAdapter.notifyDataSetChanged();
				}
				break;
			}
		}
		super.onStart();

	}

	/**
	 * 设置照片集的网络适配器
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	public class MyAlbumListAdapter extends BaseAdapter {

		private ArrayList<String> albums;

		public MyAlbumListAdapter(ArrayList<String> albums) {
			this.albums = albums;
		}

		@Override
		public int getCount() {
			if(albums.size()>=9){
				return albums.size();
			}else{
				return albums.size()+1;
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
		public View getView(final int position, View convertView,ViewGroup parent) {
			convertView = View.inflate(BroadCast_DT.this,R.layout.item_show_album, null);
			ImageView iv_album = $(convertView, R.id.iv_album);
			ImageView iv_album_del_icon = $(convertView, R.id.iv_album_del_icon);
			
			if(albums.size()>=9){
				iv_album_del_icon.setVisibility(View.VISIBLE);
				mHttpHelp.showImage(iv_album, albums.get(position));
				iv_album_del_icon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						del_album(position);
					}
				});
			}else{
				if(position == albums.size()){
					iv_album_del_icon.setVisibility(View.GONE);
					iv_album.setImageDrawable(getResources().getDrawable(R.drawable.photo_btn));
					iv_album.setScaleType(ScaleType.CENTER);
					iv_album.setOnClickListener(BroadCast_DT.this);
				}else{
					iv_album_del_icon.setVisibility(View.VISIBLE);
					mHttpHelp.showImage(iv_album, albums.get(position));
					iv_album_del_icon.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							del_album(position);
						}
					});
				}
			}
			
		
			return convertView;
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
	 * 设置当前的条目为用户照片视频的内容
	 * 
	 * @param convertView
	 * @return
	 */
	public View set_item_type_broadcast_content(View view) {
		mVh_r_c = new ViewHolder_broadcast_content();
		view = UIUtils.inflate(R.layout.item_broadcast_content);
		mVh_r_c.tv_default_icon = $(view, R.id.tv_add_phone_video);
		mVh_r_c.gv_add_album_list = $(view, R.id.gv_add_album_list);
		mVh_r_c.title_hite = $(view, R.id.title_hite);
		mVh_r_c.iv_video_add_btn = $(view, R.id.iv_video_add_btn);
		mVh_r_c.rl_video = $(view, R.id.rl_video);
		mVh_r_c.iv_video_img = $(view, R.id.iv_video_img);
		mVh_r_c.iv_video_del_icon = $(view, R.id.iv_video_del_icon);
		mVh_r_c.iv_video_play_btn = $(view, R.id.iv_video_play_btn);
		mVh_r_c.iv_add_photo_btn = $(view, R.id.iv_add_photo_btn);
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
		mVh_t_v.tv_hint.setOnTouchListener(myPressToSpeakListenr);
		mVh_t_v.tv_show_length = $(view, R.id.tv_item_voice_show_length);
		mVh_t_v.tv_show_text = $(view, R.id.tv_item_voice_show_text);
		mVh_t_v.iv_delete_icon = $(view, R.id.iv_item_voice_icon_delete);
		mVh_t_v.ll_voice_list = $(view, R.id.ll_item_voice_list);
		mVh_t_v.rl_item_speek = $(view, R.id.rl_item_speek);
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
		 RelativeLayout rl_item_speek;
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
	class ViewHolder_broadcast_content {

		public TextView title_hite;  //标题文字
		public MyGridView gv_add_album_list; // 展示图片的布局
		public TextView tv_default_icon; // 初始状态下的背景图片
		public ImageView iv_video_add_btn; // 添加拍摄电影
		public RelativeLayout rl_video; //  显示视频布局
		public ImageView iv_video_img; //  显示视频的截图
		public ImageView iv_video_play_btn; // 播放视频按钮
		public ImageView iv_video_del_icon; // 删除视频按钮
		public ImageView iv_add_photo_btn;  //添加照片按钮
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

	/**
	 * 用户进入发布页面第一种情况显示输入文本
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
	 * 用户进入发布页面第二种情况显示输入文本
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_backicon:
			finishA_closeK();
			break;
		case R.id.tv_title_right:
			saveDataToDB(); // 设置确认操作
			break;
		
		case R.id.tv_broadcast_biaoqing:
			addBiaoqingBtn(); // 设置添加表情按钮
			break;
		case R.id.tv_item_text_hite:
		case R.id.tv_icon_fingerboard:
			setFingerboardOrVoice(); // 进行键盘或者语音的切换
			break;
		case R.id.tv_broadcast_fasong:
			getImportText();// 获取输入框的文字
			break;
		case R.id.tv_broadcast_icon_tag:
			show_cryptonym_or_realname_dialog();// 匿名或者实名对话框

			break;
		case R.id.iv_video_del_icon:  //删除视频按钮
			deleteVideo(v);
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
		case R.id.tv_broadcast_add: // 添加图片，视频按钮
			show_photo_video_layout();
			break;
		case R.id.tv_add_phone_video: // 条目中添加图片，视频按钮
			if (!isFingerboardBtn) {
				show_photo_video_layout();
			}
			break;
		case R.id.iv_add_photo_btn:// 添加照片按钮
		case R.id.iv_album: // 添加照片按钮
		case R.id.tv_select_photograph: // 添加照片按钮
			addPhone();
			break;
		case R.id.iv_video_add_btn: // 添加视频按钮
		case R.id.tv_select_video: // 添加视频按钮
			addVideo();
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
	 * 删除用户的拍摄的视频
	 * @param v 
	 */
	private void deleteVideo(View v) {
		ViewHolder_broadcast_content holder =  (ViewHolder_broadcast_content)v.getTag();
		if(holder == null){
			return;
		}
		if(mVideoBgPath !=null){
			mVideoBgPath = null;
		}
		if(mVideoDuration !=null){
			mVideoDuration = null;
		}
		if(mVideoPath !=null){
			File file = new File(mVideoPath);
			if(file.exists()){
				file.delete();
			}
			mVideoPath = null;
		}
		if( mAlbumLists == null ||(mAlbumLists !=null && mAlbumLists.size()<=0)){
			holder.tv_default_icon.setVisibility(View.VISIBLE);
			holder.title_hite.setVisibility(View.VISIBLE);
			holder.iv_video_add_btn.setVisibility(View.GONE);
			holder.gv_add_album_list.setVisibility(View.GONE);
			holder.iv_add_photo_btn.setVisibility(View.GONE);
			holder.rl_video.setVisibility(View.GONE);
		}else{
			holder.rl_video.setVisibility(View.GONE);
		}
		
		
//		mLvAdapter.notifyDataSetChanged();
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

	@Override
	public void onPause() {
		if (mVh_t_v !=null && mVh_t_v.tv_show_text != null && mVoicePlay != null && !mIsplay) { // 界面消失的时候判断是否正在播放语音
			DynamicUtlis.getInstance().stopPlayVoice(mVh_t_v.tv_show_text,
					null, mVoicePlay, null);
		}
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 当用户选的照片超过了9张就设置删除操作
	 * 
	 * @param position
	 */
	private void del_album(int position) {
		mAlbumLists.remove(position);
		mAlbumAdapter.notifyDataSetChanged();
	}

	boolean isShow = true;

	/**
	 * 显示,隐藏照片或者视频的布局
	 */
	private void show_photo_video_layout() {
		if (isShow) {
			mLl_select_photograph_video.setVisibility(View.VISIBLE);
			isShow = false;
		} else {
			mLl_select_photograph_video.setVisibility(View.GONE);
			isShow = true;

		}

	}

	/**
	 * 获取输入框中的文字
	 */
	private void getImportText() {
		String UserImportMes = mEt_import_text.getText().toString().trim();
		if (!TextUtils.isEmpty(UserImportMes)) {
			if (isImportEdit) {
				set_import_broadcast_text(UserImportMes);
			} else {
				set_import_tag(UserImportMes);
			}
			mEt_import_text.setText("");
		} else if (!isImportEdit && mTagLists.size() == 0 && mVh_sh_t != null) {
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
		if (mVh_sh_t == null) {
			return;
		}

		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mVh_sh_t.iv_add_btn
				.getLayoutParams();
		layoutParams.setMargins(0, i, 0, 0);
		mVh_sh_t.iv_add_btn.setLayoutParams(layoutParams);
	}

	private String mParams_infos; // 该字段值表示用户设置的文字信息

	/**
	 * 设置输入动态文本的文字
	 * 
	 * @param str
	 */
	private void set_import_broadcast_text(String str) {
		if (mVh_t == null) {
			return;
		}
		mVh_t.iv_item_text_icon_delete.setVisibility(View.VISIBLE);
		String userMessage = mBuffer.append(str).toString();
		mVh_t.tv_item_text_hite.setText(userMessage);
		mParams_infos = userMessage;
	}

	private String mParams_isnm; // 该字段值表示用户设置的是否实名状态

	/**
	 * 设置当前聊天窗口的状态 匿名或者实名状态下
	 */
	private void set_cryptonym_or_realname_state() {
		switch (mCurrentState) {
		case STATE_REAL_NAME: // 实名状态
			mTv_broadcast_icon_tag.setText(getResources().getString(
					R.string.text_cryptonym));
			mCurrentState = STATE_CRYPTONYM_NAME;
			mParams_isnm = "0";
			break;
		case STATE_CRYPTONYM_NAME: // 匿名状态
			mTv_broadcast_icon_tag.setText(getResources().getString(
					R.string.text_real_name));
			mCurrentState = STATE_REAL_NAME;
			mParams_isnm = "1";
			break;

		default:
			break;
		}
	}

	/**
	 * 匿名或者实名对话框
	 * 
	 */
	private void show_cryptonym_or_realname_dialog() {
		RelativeLayout rl = (RelativeLayout) mLi.inflate(
				R.layout.dialog_layout, null);
		new MyDialog(this, 0, rl) {

			@Override
			public void setButton_ok(int i) {
				set_cryptonym_or_realname_state(); // 设置切换匿名或者实名下的操作

			}

			@Override
			public String getTitleText() {

				switch (mCurrentState) {
				case STATE_REAL_NAME: // 实名状态
					return BroadCast_DT.this.getResources().getString(
							R.string.text_is_real_name);

				case STATE_CRYPTONYM_NAME: // 匿名状态

					return BroadCast_DT.this.getResources().getString(
							R.string.text_is_crytonym_name);
				default:
					return null;
				}
			}
		};
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
	 * 设置用户输入自定义标签的文字
	 */
	private LayoutInflater inflater;
	private LayoutInflater mLi;

	private RelativeLayout mRecod_layout;

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
			TextView tv_broadcast_dt_tag = $(fl, R.id.tv_retrans_dt_tag);
			tv_broadcast_dt_tag.setText(str);
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
	 * 将提示文字布局隐藏 将选择的标签布局显示
	 */
	private void showSelectTag() {
		if (mVh_sh_t == null) {
			return;
		}
		mVh_sh_t.fl_content.setVisibility(View.VISIBLE);
		mVh_sh_t.tv_hite_text.setVisibility(View.GONE);
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
	 * 点击键盘按钮进行键盘和语音输入的切换
	 */
	private void setFingerboardOrVoice() {
		if (isFingerboardBtn) { // 语音状态
			isFingerboardBtn = false;
			mTv_icon_fingerboard
					.setBackgroundResource(R.drawable.bg_tv_retrans_afingerboard);
			mTv_press_speak.setVisibility(View.VISIBLE);
			mLl_import_text.setVisibility(View.GONE);
			mTv_broadcast_fasong.setVisibility(View.GONE);
			mTv_broadcast_add.setVisibility(View.VISIBLE);
			KeyBoardUtils.closeKeybord(mEt_import_text, this);
		} else { // 键盘状态

			isFingerboardBtn = true;
			// *********************点击键盘按钮显示输入文字的item*************************
			// isImportEdit 用于切换编辑标签和编辑动态
			if (isShowTextItem != true && isImportEdit == true
					&& mLvAdapter != null) {
				Log.d("------>", "**********当前是输入发布动态状态*************");
				isShowTextItem = true;
				mLvAdapter.notifyDataSetChanged();
			}
			// *********************点击键盘按钮显示输入文字的item*************************

			mTv_icon_fingerboard
					.setBackgroundResource(R.drawable.bg_tv_retrans_voice);
			mTv_press_speak.setVisibility(View.GONE);
			mTv_broadcast_add.setVisibility(View.GONE);
			mLl_select_photograph_video.setVisibility(View.GONE);
			mLl_import_text.setVisibility(View.VISIBLE);
			mTv_broadcast_fasong.setVisibility(View.VISIBLE);
			KeyBoardUtils.openKeybord(mEt_import_text, this);

		}

	}

	/**
	 * 销毁当前activity并且关闭软键盘
	 */
	private void finishA_closeK() {
		KeyBoardUtils.closeKeybord(mEt_import_text, this);
		MyApplication.setAlbumLists(null);// 将用户选择照片的集合清空
		mBuffer = null;
		mTagLists.clear();
		mTagLists = null;
		finish();
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
