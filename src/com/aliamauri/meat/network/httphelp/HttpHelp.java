package com.aliamauri.meat.network.httphelp;

import java.io.File;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.httphelp.HttpInterface.LoadRequestCallBack;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.parse.JsonParse;
import com.aliamauri.meat.utils.IconStyle;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.SDUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 网络请求
 * 
 * @author jjm
 * 
 */
public class HttpHelp {

	private HttpUtils mUtils;
	public static final String FLAG_PAGE = "&page=";
	private MyRequestCallBack requestCallBack;
	private Class claz;
	private BitmapUtils mBitmapUtils;

	private HttpHandler<String> mHandler;
	private String TAG = "HttpHelp";

	/**
	 * 
	 * 项目名称：phonevideo 类名称：MyRequestCallBack
	 * 
	 * @Description: POST和GET请求回调 创建人：jjm 创建时间：2015年7月28日 下午12:08:31 修改人：jjm
	 *               修改时间：2015年7月28日 下午12:08:31 修改备注：
	 */

	/**
	 * get解析
	 * 
	 * @param url
	 * @param clazz
	 *            bean类
	 * @param getRequestCallBack
	 *            回调方法
	 */
	public synchronized void sendGet(String url, Class clazz,
			final MyRequestCallBack requestCallBack) {
		this.requestCallBack = requestCallBack;
		this.claz = clazz;
		if (TextUtils.isEmpty(url)) {
			return;
		}
		if (url.contains(" ")) {
			url = url.replace(" ", "***");
		}
		if (url.contains("|")) {
			url = url.replace(" ", "_*_");
		}
		sendHttp(HttpMethod.GET, null, url);
	}

	public synchronized <T> void sendPost(String url, RequestParams params,
			Class<T> claz, MyRequestCallBack<T> requestCallBack) {
		if (url == null) {
			return;
		}
		this.requestCallBack = requestCallBack;
		this.claz = claz;

		sendHttp(HttpMethod.POST, params, url);
	}

	/**
	 * json解析
	 * 
	 * @param json串
	 * @param claz类
	 * @return
	 */

	/**
	 * 网络请求的类
	 * 
	 * @param method
	 *            方法
	 * @param params
	 *            参数
	 * @param url
	 *            = 地址
	 */
	private synchronized <T> void sendHttp(final HttpMethod method,
			RequestParams params, final String url) {
		if (url == null) {
			if (LogUtil.getDeBugState()) {
				UIUtils.showToast(UIUtils.getContext(), "url 为空。。。。。。。");
			}
			return;
		}
		LogUtil.e(TAG, url);
		if (mUtils == null) {
			mUtils = new HttpUtils();
			mUtils.configCurrentHttpCacheExpiry(0);
			mUtils.configDefaultHttpCacheExpiry(0);
		}
		mHandler = mUtils.send(method, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtil.e(TAG, "onFailure url=" + url);
						requestCallBack.onSucceed(null);
						UIUtils.showToast(UIUtils.getContext(), "网络有问题，请检查！");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						T t = null;
						String json = arg0.result;
						try {

							JSONObject jsonObject = new JSONObject(json);
							String status = jsonObject.getString("status");
							// status = "1000";

							if (status.equals("0")) {
								String msg = jsonObject.getString("msg");
								if (!"".equals(msg)) {
									UIUtils.showToast(UIUtils.getContext(), msg);
								}
							} else if (status.equals("1000")) {// 当前为请求网络错误时调用
								MainActivity activity = (MainActivity) MyApplication
										.getMainActivity();
								if (activity != null) {
									activity.showConflictDialog();
								}
							} else {
								t = (T) JsonParse.parserJson(json, claz);
							}
						} catch (Exception e) {
							LogUtil.e(TAG, "解析出问题json=" + json);
							LogUtil.e(TAG, "解析出问题Class=" + claz.getName());
							if (LogUtil.getDeBugState()) {
								throw new RuntimeException(e.toString());
							} else {
								if (requestCallBack != null) {
									requestCallBack.onSucceed(t);
								}
							}
						}
						if (requestCallBack == null) {
							if (LogUtil.getDeBugState()) {
								UIUtils.showToast(UIUtils.getContext(),
										"not find bean requestCallBack");
								LogUtil.e(TAG, "requestCallBack =null");
							}
						} else {
							requestCallBack.onSucceed(t);
						}
					}
				});

	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 *            地址
	 * @param key
	 *            上传参数
	 * @param path
	 *            文件的路径
	 */
	public <T> void upFile(String url, String key, String path,
			MyRequestCallBack<T> requestCallBack) {
		if (mUtils == null) {
			mUtils = new HttpUtils();
		}

		this.requestCallBack = requestCallBack;
		RequestParams params = new RequestParams();
		// params.addBodyParameter("action", key);
		params.addBodyParameter("pic", new File(path));
		sendHttp(HttpMethod.POST, params, url);
	}

	/**
	 * 
	 * 
	 * 项目名称：phonevideo 类名称：FileBean
	 * 
	 * @Description: POST请求的类 创建人：jjm 创建时间：2015年7月28日 上午11:57:10 修改人：jjm
	 *               修改时间：2015年7月28日 上午11:57:10 修改备注：
	 * @version
	 * 
	 */
	public class FileBean {
		public String status;
		public String reason;
	}

	public <T> void downLoad(final String url, String targetPath,
			final LoadRequestCallBack loadRequestCallBack) {
		if (url == null)
			return;
		if (mUtils == null) {
			mUtils = new HttpUtils();
		}
		mUtils.download(url, targetPath, true, true,
				new RequestCallBack<File>() {

					@Override
					public void onCancelled() {
						super.onCancelled();
					}

					/**
					 * 下载文件过程
					 */
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						super.onLoading(total, current, isUploading);
						if (loadRequestCallBack != null) {
							loadRequestCallBack.onLoading(total, current,
									isUploading);
						}
					}

					@Override
					public void onStart() {
						// UIUtils.showToast(UIUtils.getContext(), "开始下载...");
					}

					/**
					 * 下载文件成功
					 */
					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						// UIUtils.showToast(UIUtils.getContext(),
						// arg0.result.getName() + "下载成功");
						if (loadRequestCallBack != null) {
							loadRequestCallBack.onSucceed(arg0);
						}
					}

					/**
					 * 下载文件失败
					 */
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// UIUtils.showToast(UIUtils.getContext(),
						// arg0.getMessage() + ":下载失败");
						if (loadRequestCallBack != null) {
							loadRequestCallBack.onFailure(arg0, arg1);
						}

						LogUtil.e("----下载失败----->", "url ：" + url
								+ "  原因  ：   " + arg0.getMessage());
					}
				});
	}

	/**
	 * 显示图片
	 * 
	 * @param imageView
	 *            显示空间
	 * @param uri
	 *            地址
	 */
	public void showImage(final ImageView imageView, String uri) {
		if (imageView == null || TextUtils.isEmpty(uri)) {
			return;
		}
		if (mBitmapUtils == null) {
			String packageName = UIUtils.getContext().getPackageName();
			mBitmapUtils = new BitmapUtils(UIUtils.getContext(),
					SDUtils.getSDPath()
							+ File.separator
							+ packageName.substring(packageName
									.lastIndexOf(".")) + File.separator
							+ "iocn", 0.4f);
			mBitmapUtils.configDefaultCacheExpiry(3000);
			mBitmapUtils.configMemoryCacheEnabled(true);
			mBitmapUtils.configDefaultLoadingImage(UIUtils.getContext()
					.getResources().getDrawable(R.drawable.default_logo));
			mBitmapUtils.configDefaultLoadFailedImage(UIUtils.getContext()
					.getResources().getDrawable(R.drawable.default_logo));

		}
		if (uri.contains("##")) {
			uri = uri.substring(0, uri.indexOf("##"));
		} else {
			imageView.setScaleType(ScaleType.CENTER_CROP);
		}
		mBitmapUtils.display(imageView, uri);
		// mBitmapUtils.display(imageView, uri,
		// new BitmapLoadCallBack<ImageView>() {
		//
		// @Override
		// public void onLoadCompleted(ImageView arg0, String arg1,
		// Bitmap arg2, BitmapDisplayConfig arg3,
		// BitmapLoadFrom arg4) {
		// }
		//
		// @Override
		// public void onLoadFailed(ImageView arg0, String arg1,
		// Drawable arg2) {
		// // UIUtils.showToast(UIUtils.getContext(), "加载失败");
		// imageView.setImageDrawable(new BitmapDrawable(
		// BitmapFactory.decodeResource(UIUtils
		// .getContext().getResources(),
		// R.drawable.default_logo)));
		// }
		// });
	}

	/**
	 * 显示充满view图片
	 * 
	 * @param imageView
	 *            显示空间
	 * @param uri
	 *            地址
	 */
	public void showFitImage(ImageView imageView, String uri) {
		if (imageView == null || TextUtils.isEmpty(uri)) {
			return;
		}
		if (mBitmapUtils == null) {
			String packageName = UIUtils.getContext().getPackageName();
			mBitmapUtils = new BitmapUtils(UIUtils.getContext(),
					SDUtils.getSDPath()
							+ File.separator
							+ packageName.substring(packageName
									.lastIndexOf(".")) + File.separator
							+ "iocn", 0.4f);
			mBitmapUtils.configDefaultCacheExpiry(3000);
			mBitmapUtils.configMemoryCacheEnabled(true);
			mBitmapUtils.configDefaultLoadingImage(UIUtils.getContext()
					.getResources().getDrawable(R.drawable.default_logo));
			mBitmapUtils.configDefaultLoadFailedImage(UIUtils.getContext()
					.getResources().getDrawable(R.drawable.default_logo));

		}
		if (uri.contains("##")) {
			uri = uri.substring(0, uri.indexOf("##"));
		} else {
			imageView.setScaleType(ScaleType.FIT_XY);
		}
		mBitmapUtils.display(imageView, uri);

	}

	/**
	 * 显示图片
	 * 
	 * @param imageView
	 *            显示空间
	 * @param uri
	 *            地址
	 */

	/**
	 * 显示图片
	 * 
	 * @param imageView
	 *            显示空间
	 * @param uri
	 *            地址
	 */
	// Bitmap bmp;

	public void showImage(String uri, final ImageView imageView) {

		if (TextUtils.isEmpty(uri) || imageView == null) {
			return;
		}
		if (mBitmapUtils == null) {
			mBitmapUtils = new BitmapUtils(UIUtils.getContext(),
					SDUtils.getSDPath() + "limaoso/iocn", 0.4f);
			mBitmapUtils.configDefaultCacheExpiry(3000);
			mBitmapUtils.configMemoryCacheEnabled(true);
			mBitmapUtils.configDefaultLoadingImage(UIUtils.getContext()
					.getResources().getDrawable(R.drawable.default_logo));
			mBitmapUtils.configDefaultLoadFailedImage(UIUtils.getContext()
					.getResources().getDrawable(R.drawable.default_logo));

		}
		if (uri.contains("##")) {
			uri = uri.substring(0, uri.indexOf("##"));
		} else {
			imageView.setScaleType(ScaleType.CENTER_CROP);
		}
		mBitmapUtils.display(imageView, uri,
				new BitmapLoadCallBack<ImageView>() {

					@Override
					public void onLoadCompleted(ImageView arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						imageView.setImageDrawable(IconStyle
								.BoxBlurFilter(arg2));
					}

					@Override
					public void onLoadFailed(ImageView arg0, String arg1,
							Drawable arg2) {
						// UIUtils.showToast(UIUtils.getContext(), "加载失败");
						imageView.setImageDrawable(IconStyle
								.BoxBlurFilter(BitmapFactory.decodeResource(
										UIUtils.getContext().getResources(),
										R.drawable.default_logo)));
					}
				});
	}

	/**
	 * 显示图片
	 * 
	 * @param imageView
	 *            显示空间
	 * @param uri
	 *            地址
	 */
	public void showImage_headIcon(ImageView imageView, String uri) {
		if (mBitmapUtils == null) {
			mBitmapUtils = new BitmapUtils(UIUtils.getContext(),
					SDUtils.getSDPath() + "limaoso/iocn", 0.4f);
			mBitmapUtils.configDefaultCacheExpiry(3000);
			mBitmapUtils.configMemoryCacheEnabled(true);
			mBitmapUtils.configDefaultLoadingImage(UIUtils.getContext()
					.getResources().getDrawable(R.drawable.head_default_icon));
			mBitmapUtils.configDefaultLoadFailedImage(UIUtils.getContext()
					.getResources().getDrawable(R.drawable.head_default_icon));

		}
		if (uri.contains("##")) {
			uri = uri.substring(0, uri.indexOf("##"));
		} else {
			imageView.setScaleType(ScaleType.CENTER_CROP);
		}
		mBitmapUtils.display(imageView, uri);
	}

	/**
	 * 用于显示用户上传的照片集
	 * 
	 * @param imageView
	 *            显示空间
	 * @param uri
	 *            地址
	 * @param text
	 * @param layout
	 */
	public void showImage_album(final ImageView imageView, String uri,
			final FrameLayout layout, final TextView text) {
		if (mBitmapUtils == null) {
			mBitmapUtils = new BitmapUtils(UIUtils.getContext(),
					SDUtils.getSDPath() + "limaoso/iocn", 0.4f);
			mBitmapUtils.configDefaultCacheExpiry(3000);
			mBitmapUtils.configMemoryCacheEnabled(true);
		}
		if (uri.contains("##")) {
			uri = uri.substring(0, uri.indexOf("##"));
		} else {
			imageView.setScaleType(ScaleType.FIT_CENTER);
		}
		mBitmapUtils.display(imageView, uri,
				new BitmapLoadCallBack<ImageView>() {
					boolean isShow = true;

					@Override
					public void onLoadCompleted(ImageView arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						isShow = true;
						layout.setVisibility(View.GONE);
						text.setVisibility(View.GONE);

						arg0.setImageBitmap(arg2);
					}

					@Override
					public void onLoading(ImageView container, String uri,
							BitmapDisplayConfig config, long total, long current) {
						if (isShow) {
							isShow = false;
							layout.setVisibility(View.VISIBLE);
							text.setVisibility(View.VISIBLE);
						}
						text.setText(String.valueOf(current / total * 100)
								+ "%");
						super.onLoading(container, uri, config, total, current);
					}

					@Override
					public void onLoadFailed(ImageView arg0, String arg1,
							Drawable arg2) {
						isShow = true;
						layout.setVisibility(View.GONE);
						text.setVisibility(View.GONE);
						UIUtils.showToast(UIUtils.getContext(), "加载失败");

					}
				});

	}

	/**
	 * 停止请求网络
	 */
	public void stopHttpNET() {
		if (mHandler != null) {
			mHandler.cancel();
		}
	}

}
