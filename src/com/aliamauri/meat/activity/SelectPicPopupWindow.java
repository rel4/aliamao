package com.aliamauri.meat.activity;

import java.io.File;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.utils.IconCompress;
import com.umeng.analytics.MobclickAgent;

/* 
 *  author 院彩华
 弹出头像菜单

 */
public class SelectPicPopupWindow extends Activity implements OnClickListener {
	private HttpHelp httpHelp;
	// private String faceUrl = "";
	private final String IMAGE_TYPE = "image/*";
	// 使用照相机拍照获取图片
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
	// 使用相册中的图片
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
	// 从Intent获取图片路径的KEY
	public static final String KEY_PHOTO_PATH = "photo_path";
	private static final String TAG = "SelectPicActivity";
	private TextView takePhotoBtn, pickPhotoBtn, cancelBtn;

	/** 获取到的图片路径 */
	private String picPath;
	private Intent lastIntent;
	private Uri photoUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popupwindow_editdata_headicon);

		takePhotoBtn = (TextView) findViewById(R.id.btn_take_photo);
		takePhotoBtn.setOnClickListener(this);
		pickPhotoBtn = (TextView) findViewById(R.id.btn_pick_photo);
		pickPhotoBtn.setOnClickListener(this);
		cancelBtn = (TextView) findViewById(R.id.btn_cancel);
		cancelBtn.setOnClickListener(this);
		lastIntent = getIntent();
	}

	private long exitTime = 0;

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_take_photo:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();

				takePhoto();
			}
			break;
		case R.id.btn_pick_photo:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				pickPhoto();
			}
			break;
		default:
			finish();
			break;
		}
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

	/***
	 * 从相册中取图片
	 */
	private void pickPhoto() {
		Intent intent = new Intent();
		intent.setType(IMAGE_TYPE);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			doPhoto2(requestCode, resultCode, data);
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
					GlobalConstant.HEAD_ICON_SAVEPATH, "backup.jpg");
			setResult(1, lastIntent);
			finish();
		}

	}

	/**
	 * 选择图片后，获取图片的路径
	 * 
	 * @param requestCode
	 * @param data
	 */
	private File faceFile;

	private void doPhoto2(int requestCode, int resultCode, Intent data) {
		httpHelp = new HttpHelp();
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

}