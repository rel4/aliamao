package com.aliamauri.meat.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.TypeBean;
import com.aliamauri.meat.bean.UploadPostBackBean;
import com.aliamauri.meat.bean.cont.TypeCont;
import com.aliamauri.meat.db.localvideo.LocalVideo;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.FileMd5Utils;
import com.aliamauri.meat.utils.MyBDmapUtlis;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.SystemUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.MyGridView;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.lidroid.xutils.http.RequestParams;

public class UploadPhoneVideoActivity extends BaseActivity implements
		OnClickListener, OnDismissListener {

	private HttpHelp httpHelp;

	private MyGridView ptr_aulocalvideo_list;
	private ImageView iv_aul_movies;
	private LocalVideo localVideo;
	private TypeAdapter typeAdapter;
	private PopupWindow mPopupWindow;
	private RelativeLayout rl_progress;
	private RelativeLayout rl_aul_movies;

	private TypeBean tBean;
	private String tag;
	private String pid;

	private EditText et_aul_title;
	private EditText et_aul_desc;
	private TextView tv_aul_type;

	@Override
	protected View getRootView() {
		Bundle bundle = this.getIntent().getExtras();
		localVideo = (LocalVideo) bundle.get(GlobalConstant.INTENT_BUNDLE);
		if (localVideo == null) {
			UIUtils.showToast(UIUtils.getContext(), "参数有误");
			finish();
		}
		return View.inflate(UIUtils.getContext(),
				R.layout.activity_upload_localvideo, null);
	}

	private HttpHelp getHttpHelp() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		return httpHelp;
	}

	protected String getCurrentTitle() {
		return "上传视频";
	}

	@Override
	protected void initView() {
		findViewById(R.id.tv_title_uploadvideo).setOnClickListener(this);
		findViewById(R.id.tv_title_uploadvideo).setVisibility(View.VISIBLE);
		ptr_aulocalvideo_list = (MyGridView) findViewById(R.id.ptr_aulocalvideo_list);
		iv_aul_movies = (ImageView) findViewById(R.id.iv_aul_movies);
		rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
		rl_aul_movies = (RelativeLayout) findViewById(R.id.rl_aul_movies);
		et_aul_title = (EditText) findViewById(R.id.et_aul_title);
		et_aul_desc = (EditText) findViewById(R.id.et_aul_desc);
		tv_aul_type = (TextView) findViewById(R.id.tv_aul_type);

		initData();
	}

	@Override
	protected void setListener() {
		rl_aul_movies.setOnClickListener(this);
		rl_progress.setOnClickListener(this);
		super.setListener();
	}

	@Override
	protected void initNet() {
		getHttpHelp().sendGet(NetworkConfig.getVideoType(), TypeBean.class,
				new MyRequestCallBack<TypeBean>() {

					@Override
					public void onSucceed(TypeBean bean) {
						if (bean == null) {
							return;
						}
						tBean = bean;
						initLVdata();
					}
				});
	}

	private void initLVdata() {
		if (typeAdapter == null) {
			typeAdapter = new TypeAdapter();
			ptr_aulocalvideo_list.setAdapter(typeAdapter);
			ptr_aulocalvideo_list
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							tv_aul_type.setText("类型-"
									+ tBean.cont.get(position).name.trim());
							pid = tBean.cont.get(position).id;
							showPopopWindow(position, tBean);
						}
					});
		} else {
			typeAdapter.notifyDataSetChanged();
		}
	}

	private void initData() {
		getHttpHelp().showImage(iv_aul_movies, localVideo.imgPath);
	}

	class TypeAdapter extends BaseAdapter {
		private TextView tv_iul_type;

		@Override
		public int getCount() {
			if (tBean == null || tBean.cont == null) {
				return 0;
			}
			return tBean.cont.size();
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
			if (convertView == null) {
				convertView = View.inflate(UIUtils.getContext(),
						R.layout.item_upload_localvideo_textview, null);

			}
			tv_iul_type = (TextView) convertView.findViewById(R.id.tv_iul_type);
			// if (test.get(position).length() == 2) {
			// tv_iul_type.setTextSize(R.dimen.x32);
			// } else if (test.get(position).length() == 3) {
			// tv_iul_type.setTextSize(R.dimen.x30);
			// } else if (test.get(position).length() == 4) {
			// tv_iul_type.setTextSize(R.dimen.x28);
			// } else {
			// tv_iul_type.setTextSize(R.dimen.x24);
			// }
			tv_iul_type.setText(tBean.cont.get(position).name.trim());
			return convertView;
		}
	}

	private View popView;
	private PullToRefreshGridView ptr_pop_localvideo_list;
	private List<TypeCont> child_type;
	private TypeChildAdapter typeChildAdapter;

	private void initPopuWindowView() {
		popView = View.inflate(UIUtils.getContext(),
				R.layout.popupwindow_uploadlocalvideo, null);
		ptr_pop_localvideo_list = (PullToRefreshGridView) popView
				.findViewById(R.id.ptr_pop_localvideo_list);

		int[] position = new int[2];
		findViewById(R.id.tv_aul_type).getLocationInWindow(position);
		int popuHeight = SystemUtils.getScreenHeight()
				- position[1]
				- (int) UploadPhoneVideoActivity.this.getResources()
						.getDimension(R.dimen.y60);
		// (int) UploadPhoneVideoActivity.this.getResources()
		// .getDimension(R.dimen.dp258))
		mPopupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
				popuHeight);
	}

	private void setPopLVData() {
		if (typeChildAdapter == null) {
			typeChildAdapter = new TypeChildAdapter(child_type);
			ptr_pop_localvideo_list.setAdapter(typeChildAdapter);
			ptr_pop_localvideo_list
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							tv_aul_type.setText(tv_aul_type.getText()
									.toString().trim()
									+ "-"
									+ typeChildAdapter.cont.get(position).name
											.trim());
							tag = typeChildAdapter.cont.get(position).id;
							mPopupWindow.dismiss();
						}
					});
		} else {
			typeChildAdapter.updateTypeChild(child_type);
		}

	}

	private void showPopopWindow(int pos, TypeBean tBean) {
		if (tBean == null || tBean.cont == null
				|| tBean.cont.get(pos).son == null
				|| tBean.cont.get(pos).son.size() <= 0) {
			return;
		}
		if (child_type == null) {
			child_type = new ArrayList<TypeCont>();
		}
		child_type.clear();
		for (int i = 0; i < tBean.cont.get(pos).son.size(); i++) {
			TypeCont cont = new TypeCont();
			cont.name = tBean.cont.get(pos).son.get(i).name;
			cont.id = tBean.cont.get(pos).son.get(i).id;
			child_type.add(cont);
		}
		if (popView == null) {
			initPopuWindowView();// 设置布局
		}
		setPopLVData();
		// popwindow_layout.setFocusable(true); // 这个很重要
		// popwindow_layout.setFocusableInTouchMode(true);
		mPopupWindow.setOnDismissListener(this);
		// 设置可以获取焦点，否则弹出菜单中的EditText是无法获取输入的
		mPopupWindow.setFocusable(true);
		// 这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 防止虚拟软键盘被弹出菜单遮住
		mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		mPopupWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		mPopupWindow.setAnimationStyle(R.style.AnimCommentList);
		mPopupWindow.showAtLocation(findViewById(R.id.tv_aul_type),
				Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
	}

	class TypeChildAdapter extends BaseAdapter {
		private TextView tv_iul_type;
		public List<TypeCont> cont;

		public TypeChildAdapter(List<TypeCont> cont) {
			this.cont = new ArrayList<TypeCont>();
			this.cont.addAll(cont);
		}

		public void updateTypeChild(List<TypeCont> cont) {
			if (this.cont == null) {
				this.cont = new ArrayList<TypeCont>();
			}
			this.cont.clear();
			this.cont.addAll(cont);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (cont == null) {
				return 0;
			}
			return cont.size();
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
			if (convertView == null) {
				convertView = View.inflate(UIUtils.getContext(),
						R.layout.item_upload_localvideo_textview_bgwhite, null);

			}
			tv_iul_type = (TextView) convertView.findViewById(R.id.tv_iul_type);
			// if (test.get(position).length() == 2) {
			// tv_iul_type.setTextSize(R.dimen.x32);
			// } else if (test.get(position).length() == 3) {
			// tv_iul_type.setTextSize(R.dimen.x30);
			// } else if (test.get(position).length() == 4) {
			// tv_iul_type.setTextSize(R.dimen.x28);
			// } else {
			// tv_iul_type.setTextSize(R.dimen.x24);
			// }
			tv_iul_type.setText(cont.get(position).name.trim());
			return convertView;
		}

	}

	private void uploadVideo() {
		RequestParams params = new RequestParams();
		long time = System.currentTimeMillis() / 1000;
		params.addBodyParameter("time", time + "");
		params.addBodyParameter("uid",
				PrefUtils.getString(GlobalConstant.USER_ID, ""));
		params.addBodyParameter("up_image", new File(localVideo.imgPath));
		params.addBodyParameter("up_video", new File(localVideo.path));
		try {
			params.addBodyParameter("timestamp",
					FileMd5Utils.toMD5_32(time + "limao"));
			params.addBodyParameter("md5",
					FileMd5Utils.getFileMD5String(localVideo.path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		getHttpHelp().sendPost(NetworkConfig.getupFile(), params,
				UploadPostBackBean.class,
				new MyRequestCallBack<UploadPostBackBean>() {

					@Override
					public void onSucceed(UploadPostBackBean bean) {
						if (bean == null) {
							rl_progress.setVisibility(View.GONE);
							return;
						}
						if ("1".equals(bean.status)) {
							RequestParams params = new RequestParams();
							params.addBodyParameter(
									"imgUrl",
									bean.cont.imagePath != null ? bean.cont.imagePath
											: "");
							params.addBodyParameter(
									"videourl",
									bean.cont.videoPath != null ? bean.cont.videoPath
											: "");
							params.addBodyParameter("tag", tag);
							params.addBodyParameter("pid", pid);
							params.addBodyParameter("desc", et_aul_desc
									.getText().toString().trim());
							params.addBodyParameter("title", et_aul_title
									.getText().toString().trim());
							params.addBodyParameter("size", localVideo.size
									+ "");
							params.addBodyParameter("duration",
									localVideo.duration + "");

							String[] location = PrefUtils.getString(
									GlobalConstant.USER_LOCATION, "0&&0")
									.split("&&");
							params.addBodyParameter("wd",
									String.valueOf(location[0]).trim());
							params.addBodyParameter(
									"jd",
									String.valueOf(
											location[location.length - 1])
											.trim());

							MyBDmapUtlis dbUtlis = new MyBDmapUtlis();
							String[] strs = dbUtlis
									.splitCityNameArray(PrefUtils.getString(
											GlobalConstant.USER_LOCATION_MSG,
											null));
							if (strs != null && strs[0] != null
									&& !"err".equals(strs[0])) { // 省
								params.addBodyParameter("province",
										strs[0].trim());
							} else {
								params.addBodyParameter("province", "");
							}
							if (strs != null && strs[1] != null
									&& !"err".equals(strs[1])) { // 市
								params.addBodyParameter("city", strs[1]);
							} else {
								params.addBodyParameter("city", "");
							}
							if (strs != null && strs[2] != null
									&& !"err".equals(strs[2])) { // 县
								params.addBodyParameter("district",
										strs[2].trim());
							} else {
								params.addBodyParameter("district", "");
							}

							getHttpHelp().sendPost(NetworkConfig.getAddVideo(),
									params, BaseBaen.class,
									new MyRequestCallBack<BaseBaen>() {

										@Override
										public void onSucceed(BaseBaen bean) {
											rl_progress
													.setVisibility(View.GONE);
											if (bean == null) {
												return;
											}
											UIUtils.showToast(
													UIUtils.getContext(),
													bean.msg);
											if ("1".equals(bean.status)) {
												finish();
											}
										}
									});
						} else {
							rl_progress.setVisibility(View.GONE);
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_uploadvideo:
			String[] StrArr = tv_aul_type.getText().toString().trim()
					.split("-");
			if (et_aul_title.getText().toString().length() > 20) {
				UIUtils.showToast(UIUtils.getContext(), "标题过长");
			} else if ("".equals(et_aul_title.getText().toString().trim())) {
				UIUtils.showToast(UIUtils.getContext(), "请添加标题");
			} else if (StrArr.length < 2) {
				UIUtils.showToast(UIUtils.getContext(), "请选择视频类型");
			} else if (StrArr.length < 3) {
				UIUtils.showToast(UIUtils.getContext(), "请选择视频标签");
			} else {
				rl_progress.setVisibility(View.VISIBLE);
				uploadVideo();
			}
			break;
		case R.id.rl_aul_movies:
			startActivity(new Intent(this, ShowShortVideoActivity.class)
					.putExtra(GlobalConstant.SHOW_SHORT_VIDEO_PATH,
							localVideo.path));
			break;
		default:
			break;
		}
	}

	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub

	}

}
