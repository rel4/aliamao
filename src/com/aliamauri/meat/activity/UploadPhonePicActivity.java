package com.aliamauri.meat.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.UploadPicBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.IconCompress;
import com.aliamauri.meat.utils.UIUtils;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

/**
 * 上传照片
 * 
 * @author ych
 * 
 */
public class UploadPhonePicActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private TextView tv_title_right;
	private GridView lv_upload_phonepic;
	private List<String> select_list;

	private HttpHelp httpHelp;

	@Override
	protected View getRootView() {
		View view = View.inflate(UploadPhonePicActivity.this,
				R.layout.upload_phonepic, null);
		if (getIntent().getStringArrayListExtra("data") != null) {
			select_list = getIntent().getStringArrayListExtra("data");
		} else {
			select_list = new ArrayList<String>();
		}
		httpHelp = new HttpHelp();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "上传照片";
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

	@Override
	protected void initView() {
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("上传");

		lv_upload_phonepic = (GridView) findViewById(R.id.lv_upload_phonepic);
		lv_upload_phonepic.setAdapter(new UploadPicAdapter());
		lv_upload_phonepic.setOnItemClickListener(this);
	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
	}

	class UploadPicAdapter extends BaseAdapter {
		ViewHolder viewHolder;

		@Override
		public int getCount() {
			return select_list.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(UploadPhonePicActivity.this,
						R.layout.upload_phonepic_item, null);
				viewHolder.iv_uploadphone_pic = (ImageView) convertView
						.findViewById(R.id.iv_uploadphone_pic);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (position == select_list.size()) {
				viewHolder.iv_uploadphone_pic
						.setImageResource(R.drawable.add_pic_icon_upload);
				viewHolder.iv_uploadphone_pic
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								finish();

							}
						});
			} else {
				httpHelp.showImage(viewHolder.iv_uploadphone_pic,
						select_list.get(position));
			}

			return convertView;
		}
	}

	class ViewHolder {
		public ImageView iv_uploadphone_pic;
	}

	private File file;
	private int uploadNum = 0;

	private void UploadPic() {
		Bitmap bm = null;
		uploadNum = 0;
		for (String f : select_list) {
			file = new File(f);
			if (file.length() / 1024.0 > 100) {
				bm = IconCompress.comPressPathThree(f);
				file = IconCompress.saveBitmap(bm,
						GlobalConstant.HEAD_ICON_SAVEPATH, "compress.jpg");
			}
			RequestParams params = new RequestParams();
			params.addBodyParameter("uploadedfile", file);
			httpHelp.sendPost(NetworkConfig.imgUploads(), params,
					UploadPicBean.class,
					new MyRequestCallBack<UploadPicBean>() {

						@Override
						public void onSucceed(UploadPicBean bean) {
							if (bean == null || bean.cont == null) {
								UIUtils.showToast(getApplicationContext(),
										"上传失败！");
								return;
							}
							uploadNum++;
							if ("1".equals(bean.status)) {
								// uploadList.add(bean.cont.bigimgurl);
								if (uploadNum >= select_list.size()) {
									MyApplication.getMainActivity().mp
											.netWorkReInfo();
									sendBroadCast();
								}
							}
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
						}
					});
		}
		sendBroadCastFinish();
		finish();
		// httpHelp.sendGet(url, clazz, requestCallBack);
	}

	private void sendBroadCastFinish() {
		Intent i = new Intent();
		i.setAction("ActivityFinish");
		UploadPhonePicActivity.this.sendBroadcast(i);
	}

	private void sendBroadCast() {
		Intent i = new Intent();
		i.setAction("MyAlbumUpdata");
		UploadPhonePicActivity.this.sendBroadcast(i);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			UploadPic();
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// Toast.makeText(this, position + "", Toast.LENGTH_SHORT).show();

	}

}
