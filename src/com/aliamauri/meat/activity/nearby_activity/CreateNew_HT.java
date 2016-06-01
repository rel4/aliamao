package com.aliamauri.meat.activity.nearby_activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.IM.Constant;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.activity.IM.adapter.ContactAdapter;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.bean.DTTagBean;
import com.aliamauri.meat.bean.DTTagBean.Cont;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.IconCompress;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.FlowLayout;
import com.aliamauri.meat.view.MyPhoneOrVideoDialog;
import com.aliamauri.meat.weight.Sidebar;
import com.umeng.analytics.MobclickAgent;

/**
 * 附近--创建话题
 * 
 * @author limaokeji-windosc
 * 
 */
public class CreateNew_HT extends FragmentActivity implements OnClickListener,
		OnItemClickListener {

	private final int HEADICON = 1;
	private final String CREATE_HT_TAG = "create_ht_tag"; // 创建话题页面的标记
	private final String OK_HT_TAG = "ok_ht_tag"; // 创建话题完成页面的标记
	private String mCurrent_tag;
	private boolean Iconboo;// 判断是否修改了头像

	private TextView mTv_title_title; // 标题名称
	private TextView mTv_title_right; // 右边按钮
	private EditText mCreat_new_ht_title; // 创建群标题
	private EditText mCeat_new_ht_content; // 创建群内容
	private RelativeLayout mRl_create_new_ht; // 创建话题布局
	private FrameLayout mFl_create_new_ht; // 创建完成话题布局
	private ListView mLv_create_new_ht_content; // 创建完成话题布局
	private CircleImageView mCi_create_ht_ok_headicon; // 群头像
	private PickContactAdapter contactAdapter;

	private ImageView mIv_created_ht_add_tag;// 添加自定义标签按钮
	private int width; // 屏幕宽
	private int height; // 屏幕高
	private TextView mTv_created_ht_hite_text; // 添加标签的提示文字
	private FlowLayout mFl_created_ht_show_tag; // 添加标签的容器
	private FlowLayout mFl_created_ht_show_net_tag; // 显示服务器推荐标签的容器
	private ArrayList<String> mTagLists; // 存储用户选择的标签
	private LayoutInflater inflater;

	private HttpHelp mHttpHelp;
	private String[] mTag = new String[] { GlobalConstant.TAG_1,
			GlobalConstant.TAG_2, GlobalConstant.TAG_3, GlobalConstant.TAG_4,
			GlobalConstant.TAG_5, GlobalConstant.TAG_6 };
	private LinearLayout mRl_created_ht_edit_layout; // 编辑标签的布局
	private TextView mTv_created_ht_fasong; // 确认按钮
	private EditText mEt_created_ht_import_text; // 输入的文本

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_ht);
		mCurrent_tag = CREATE_HT_TAG; // 设置当前状态
		WindowManager ss = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		ss.getDefaultDisplay().getMetrics(outMetrics);
		width = outMetrics.widthPixels;
		height = outMetrics.heightPixels;
		inflater = LayoutInflater.from(this);
		mTagLists = new ArrayList<String>();
		mTagLists.clear();
		mHttpHelp = new HttpHelp();
		initView();
		initDate();
		initTagNet();
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
						if (bean.cont == null || bean.msg == null) {
							return;
						}
						if (bean.cont.size() <= 0) {
							UIUtils.showToast(CreateNew_HT.this, "没有更多内容了……");
							return;

						}
						mTagListsFromNet = bean.cont;
						if (mTagListsFromNet != null
								&& mTagListsFromNet.size() != 0) {
							mFl_created_ht_show_net_tag.removeAllViews();
							LayoutInflater from = LayoutInflater
									.from(CreateNew_HT.this);
							for (int i = 0; i < mTagListsFromNet.size(); i++) {
								TextView tv = (TextView) from.inflate(
										R.layout.fl_text,
										mFl_created_ht_show_net_tag, false);
								tv.setTag(mTag[i]);
								tv.setOnClickListener(CreateNew_HT.this);
								tv.setText(mTagListsFromNet.get(i).name);
								mFl_created_ht_show_net_tag.addView(tv);
							}
						}

						++tag_page;
					}
				});
	}

	/**
	 * 数据初始化操作
	 */
	private void initDate() {
		// 获取好友列表
		final List<User> alluserList = new ArrayList<User>();
		for (User user : ((MySDKHelper) SDKHelper.getInstance())
				.getContactList().values()) {
			if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME)
					& !user.getUsername().equals(Constant.GROUP_USERNAME)
					& !user.getUsername().equals(Constant.CHAT_ROOM)
					& !user.getUsername().equals(Constant.CHAT_ROBOT))
				alluserList.add(user);
		}
		// 对list进行排序
		Collections.sort(alluserList, new Comparator<User>() {
			@Override
			public int compare(User lhs, User rhs) {
				return (lhs.getUsername().compareTo(rhs.getUsername()));

			}
		});

		contactAdapter = new PickContactAdapter(this,
				R.layout.row_contact_with_checkbox, alluserList);
		mLv_create_new_ht_content.setAdapter(contactAdapter);
		((Sidebar) findViewById(R.id.sidebar))
				.setListView(mLv_create_new_ht_content);
		mLv_create_new_ht_content.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
		checkBox.toggle();

	}

	/**
	 * adapter
	 */
	private class PickContactAdapter extends ContactAdapter {

		private boolean[] isCheckedArray;

		public PickContactAdapter(Context context, int resource,
				List<User> users) {
			super(context, resource, users);
			isCheckedArray = new boolean[users.size()];
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = super.getView(position, convertView, parent);

			// 选择框checkbox
			final CheckBox checkBox = (CheckBox) view
					.findViewById(R.id.checkbox);

			checkBox.setButtonDrawable(R.drawable.checkbox_bg_selector);
			if (checkBox != null) {

				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						isCheckedArray[position] = isChecked;
					}
				});

				checkBox.setChecked(isCheckedArray[position]);
			}
			return view;
		}
	}

	/**
	 * 获取要被添加的成员
	 * 
	 * @return
	 */
	private List<String> getToBeAddMembers() {
		List<String> serIds = new ArrayList<String>();
		int length = contactAdapter.isCheckedArray.length;
		for (int i = 0; i < length; i++) {
			String userId = contactAdapter.getItem(i).getUserId();
			if (contactAdapter.isCheckedArray[i]) {
				serIds.add(userId);
			}
		}
		return serIds;
	}

	private void initView() {
		$(R.id.iv_title_backicon).setOnClickListener(this);
		$(R.id.tv_title_right).setOnClickListener(this);
		$(R.id.tv_title_right).setVisibility(View.VISIBLE);
		mTv_title_title = $(R.id.tv_title_title);
		mTv_title_right = $(R.id.tv_title_right);
		mTv_title_right.setText("下一步");
		mTv_title_title.setText("创建话题");

		mCreat_new_ht_title = $(R.id.creat_new_ht_title);
		mCeat_new_ht_content = $(R.id.creat_new_ht_content);

		mRl_create_new_ht = $(R.id.rl_create_new_ht);
		mFl_create_new_ht = $(R.id.fl_create_new_ht);
		mLv_create_new_ht_content = $(R.id.list);
		mCi_create_ht_ok_headicon = $(R.id.ci_create_ht_ok_headicon);
		mCi_create_ht_ok_headicon.setOnClickListener(this);
		$(R.id.tv_create_ht_ok_headicon).setOnClickListener(this);
		mTv_created_ht_hite_text = $(R.id.tv_created_ht_hite_text);
		mFl_created_ht_show_tag = $(R.id.fl_created_ht_show_tag);
		mFl_created_ht_show_net_tag = $(R.id.fl_created_ht_show_net_tag);

		mRl_created_ht_edit_layout = $(R.id.rl_created_ht_edit_layout);
		mTv_created_ht_fasong = $(R.id.tv_created_ht_fasong);
		mEt_created_ht_import_text = $(R.id.et_created_ht_import_text);
		mTv_created_ht_fasong.setOnClickListener(this);

		mIv_created_ht_add_tag = $(R.id.iv_created_ht_add_tag);
		mIv_created_ht_add_tag.setOnClickListener(this);
		$(R.id.tv_created_ht_title_button_swap).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			switch (mCurrent_tag) {
			case CREATE_HT_TAG:
				setNext();

				break;
			case OK_HT_TAG:
				createOk_ht();
				closePageStyle();
				break;
			}
			break;
		case R.id.iv_title_backicon:
			switch (mCurrent_tag) {
			case CREATE_HT_TAG:
				closePageStyle();
				break;
			case OK_HT_TAG:
				KeyBoardUtils.closeKeybord(mEt_created_ht_import_text, this);
				setCreatHtPage();
				break;
			}
			break;
		case R.id.iv_created_ht_add_tag:
			addTag();
			break;
		case R.id.tv_created_ht_title_button_swap:
			freshenTags();
			break;
		case R.id.ci_create_ht_ok_headicon:
		case R.id.tv_create_ht_ok_headicon:
			addPhone();
			break;
		case R.id.tv_tag_text_click:
			setClickTextAction(v); // 设置标签点击后的动作
		case R.id.tv_created_ht_fasong:
			getImportText();// 获取输入框的文字

			break;

		}
	}

	/**
	 * 添加照片按钮
	 */
	private void addPhone() {

		LinearLayout ll = (LinearLayout) inflater.inflate(
				R.layout.dialog_phone_or_video, null);
		new MyPhoneOrVideoDialog(this, ll) {

			@Override
			public void setButton_camear() { // 照相机
				takePhoto();

			}

			@Override
			public void setButton_album() { // 相册
				// 进入选择照片界面
				pickPhoto();
			}

		};

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) { //
			doPhoto2(requestCode, resultCode, data);
		}

		if (requestCode == HEADICON) {
			if (CheckUtils.getInstance().isIcon(
					GlobalConstant.HEAD_ICON_SAVEPATH + "chat_head_icon.jpg")) {
				mHttpHelp.showImage(mCi_create_ht_ok_headicon,
						GlobalConstant.HEAD_ICON_SAVEPATH
								+ "chat_head_icon.jpg" + "##");
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
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
					GlobalConstant.HEAD_ICON_SAVEPATH, "chat_head_icon.jpg");
			if (CheckUtils.getInstance().isIcon(
					GlobalConstant.HEAD_ICON_SAVEPATH + "chat_head_icon.jpg")) {
				mHttpHelp.showImage(mCi_create_ht_ok_headicon,
						GlobalConstant.HEAD_ICON_SAVEPATH
								+ "chat_head_icon.jpg" + "##");
			} else {
				UIUtils.showToast(this, "照片获取失败，请重新拍照……");
			}
		}

	}

	/**
	 * 每次退出该类后销毁用户拍摄的头像
	 */
	@Override
	protected void onDestroy() {
		if (CheckUtils.getInstance().isIcon(
				GlobalConstant.HEAD_ICON_SAVEPATH + "chat_head_icon.jpg")) {
			File file = new File(GlobalConstant.HEAD_ICON_SAVEPATH
					+ "chat_head_icon.jpg");
			file.delete();
		}
		super.onDestroy();
	}

	/**
	 * 选择图片后，获取图片的路径
	 * 
	 * @param requestCode
	 * @param data
	 */
	private File faceFile;

	private void doPhoto2(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUESTCODE_CUTTING) {

			if (data != null) {
				savePic(data);
			}
		} else if (requestCode == SELECT_PIC_BY_PICK_PHOTO) // 从相册取图片，有些手机有异常情况，请注意
		{
			Uri originalUri = data.getData(); // 获得图片的uri
			startPhotoZoom(originalUri);

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

			} else {
				Toast.makeText(this, "选择文件不正确!", Toast.LENGTH_LONG).show();

			}
		}
	}

	private final String IMAGE_TYPE = "image/*";

	/***
	 * 从相册中取图片
	 */
	private void pickPhoto() {
		Intent intent = new Intent();
		intent.setType(IMAGE_TYPE);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

	/** 获取到的图片路径 */
	private String picPath;
	private Uri photoUri;

	// 使用照相机拍照获取图片
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
	// 使用相册中的图片
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

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
			overridePendingTransition(R.anim.slide_in_from_left,
					R.anim.slide_out_to_right);
		} else {
			Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 完成创建话题按钮
	 */
	private void createOk_ht() {
		// TODO Auto-generated method stub
		UIUtils.showToast(this, "******完成创建*******");
	}

	/**
	 * 添加自定义标签的按钮
	 */
	private void addTag() {
		mRl_created_ht_edit_layout.setVisibility(View.VISIBLE);
		KeyBoardUtils.openKeybord(mEt_created_ht_import_text, this);

	}

	/**
	 * 换一批网络推荐标签
	 */
	private void freshenTags() {
		initTagNet();

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

	private void set_import_tag(final String str) {
		set_margin_top_distance(height * 70 / 1280);

		if (str.length() >= 10) {
			UIUtils.showToast(this, "标签长度不能超过10哦~~");
			if (mTagLists.size() == 0) {
				mFl_created_ht_show_tag.setVisibility(View.GONE);
				mTv_created_ht_hite_text.setVisibility(View.VISIBLE);
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

		if (mFl_created_ht_show_tag.getChildCount() < 5) {
			final FrameLayout fl = (FrameLayout) inflater.inflate(
					R.layout.add_tag_layout, mFl_created_ht_show_tag, false);
			TextView tv_retrans_dt_tag = $(fl, R.id.tv_retrans_dt_tag);
			tv_retrans_dt_tag.setText(str);
			mFl_created_ht_show_tag.addView(fl);
			mTagLists.add(str); // 将用户选择的标签进行存储
			/**
			 * 点击清除按钮将选择的标签进行删除
			 */
			$(fl, R.id.iv_retrans_dt_delete_icon).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							mTagLists.remove(str);
							mFl_created_ht_show_tag.removeView(fl);
							if (mFl_created_ht_show_tag.getChildCount() == 0) {
								mFl_created_ht_show_tag
										.setVisibility(View.GONE);
								mTv_created_ht_hite_text
										.setVisibility(View.VISIBLE);
								set_margin_top_distance(0);
							}

						}
					});

		} else {
			UIUtils.showToast(this, "标签最多只能输入5个~~~");
		}

	}

	/**
	 * 设置添加按钮图片的距离
	 * 
	 * @param i
	 */
	private void set_margin_top_distance(int i) {

		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mIv_created_ht_add_tag
				.getLayoutParams();
		layoutParams.setMargins(0, i, 0, 0);
		mIv_created_ht_add_tag.setLayoutParams(layoutParams);
	}

	/**
	 * 将提示文字布局隐藏 将选择的标签布局显示
	 */
	private void showSelectTag() {
		mFl_created_ht_show_tag.setVisibility(View.VISIBLE);
		mTv_created_ht_hite_text.setVisibility(View.GONE);
	}

	/**
	 * 获取输入框中的文字
	 */
	private void getImportText() {
		KeyBoardUtils.closeKeybord(mEt_created_ht_import_text, this);
		mRl_created_ht_edit_layout.setVisibility(View.GONE);
		String UserImportMes = mEt_created_ht_import_text.getText().toString()
				.trim();
		if (!TextUtils.isEmpty(UserImportMes)) {
			showSelectTag();
			set_import_tag(UserImportMes);
			mEt_created_ht_import_text.setText("");
		} else if (mTagLists.size() == 0) {

			mFl_created_ht_show_tag.setVisibility(View.GONE);
			mTv_created_ht_hite_text.setVisibility(View.VISIBLE);
			set_margin_top_distance(0);
		}
	}

	/**
	 * 关闭activity的动画
	 */
	private void closePageStyle() {
		KeyBoardUtils.closeKeybord(mEt_created_ht_import_text, this);
		finish();
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

	/**
	 * 设置进入创建页面样式
	 */
	private void setCreatHtPage() {

		mCurrent_tag = CREATE_HT_TAG;
		setTitleSytle(CREATE_HT_TAG);
		mRl_create_new_ht.setVisibility(View.VISIBLE);
		mRl_create_new_ht.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_in_from_left));
		mRl_create_new_ht.setAnimationCacheEnabled(false);
		mFl_create_new_ht.setVisibility(View.GONE);
		mFl_create_new_ht.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_out_to_right));
		mFl_create_new_ht.setAnimationCacheEnabled(false);
	}

	/**
	 * 设置进入创建完成页面样式
	 */
	private void setCreatOkPage() {
		mCurrent_tag = OK_HT_TAG;
		setTitleSytle(OK_HT_TAG);
		mRl_create_new_ht.setVisibility(View.GONE);
		mRl_create_new_ht.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_out_to_left));
		mRl_create_new_ht.setAnimationCacheEnabled(false);
		mFl_create_new_ht.setVisibility(View.VISIBLE);
		mFl_create_new_ht.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_in_from_right));
		mFl_create_new_ht.setAnimationCacheEnabled(false);
	}

	private String title_text; // 标题文字
	private String content_text; // 介绍文字
	private String userIdParams; // 所有用户id

	/**
	 * 获取输入框的文字信息内容
	 */
	private void setNext() {

		title_text = mCreat_new_ht_title.getText().toString().trim();
		content_text = mCeat_new_ht_content.getText().toString().trim();

		if (StringUtils.isEmpty(title_text)) {
			UIUtils.showToast(getApplicationContext(), "话题标题不能为空~~");
			return;
		}
		if (StringUtils.isEmpty(content_text)) {
			UIUtils.showToast(getApplicationContext(), "话题内容不能为空~~");
			return;
		}

		if (title_text.length() > 12) {
			UIUtils.showToast(getApplicationContext(), "话题标题不能超过12个字~~");
			return;
		}
		if (content_text.length() > 30) {
			UIUtils.showToast(getApplicationContext(), "话题内容不能超过30个字~~");
			return;
		}

		KeyBoardUtils.closeKeybord(mCeat_new_ht_content,
				getApplicationContext());
		KeyBoardUtils.closeKeybord(mCeat_new_ht_content,
				getApplicationContext());
		userIdParams = setUserIdParams(getToBeAddMembers());

		setCreatOkPage();

	}

	/**
	 * 设置用户id拼接字符串
	 * 
	 * @param ids
	 */
	private String setUserIdParams(List<String> ids) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < ids.size(); i++) {
			buffer.append(ids.get(i)).append("|||");
		}
		return buffer.toString();
	}

	/**
	 * 设置标题头的样式
	 * 
	 * @param okHtTag
	 */
	private void setTitleSytle(String Tag) {
		switch (Tag) {
		case CREATE_HT_TAG:
			mTv_title_title.setVisibility(View.VISIBLE);
			mTv_title_right.setText("下一步");
			break;
		case OK_HT_TAG:
			mTv_title_title.setVisibility(View.GONE);
			mTv_title_right.setText("完成");
			break;
		}

	}

	// 点击EditText以外的任何区域隐藏键盘
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				if (hideInputMethod(this, v)) {
					return true; // 隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
				}
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	public static boolean isShouldHideInput(View v, MotionEvent event) {
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

	public static Boolean hideInputMethod(Context context, View v) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
		return false;
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

}
