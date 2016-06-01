package com.aliamauri.meat.service;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.db.Dynamic_db.DynamicShowDao;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.parse.JsonParse;
import com.aliamauri.meat.utils.IconCompress;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.MapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class UploadService extends Service {

	private String TAG = "UploadService";
	private HttpUtils httpUtils;
	// 文件id
	private String fileId;
	// 文件背景
	private String filebg;
	private Map<String, UplaodType> taskMap = new LinkedHashMap<String, UploadService.UplaodType>();
	private Map<String, UplaodType> unfinishFilePaths = new LinkedHashMap<String, UplaodType>();
	private Map<UploadFileBean, UplaodType> finishFile = new LinkedHashMap<UploadFileBean, UplaodType>();
	private int upCount;
	/**
	 * 正在下载文件路径
	 */
	// private String upLoadingFilePath;
	/**
	 * 是否正在在上传
	 */
	private boolean isUpLoad;
	private UpLoadListener loadListener;

	public class MyBinder extends Binder {
		public Service getService() {
			return UploadService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.e(TAG, "UploadService开启");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.e(TAG, "onStartCommand开启");
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 上传文件
	 * 
	 * @param path
	 * @param type
	 */
	public void uploadFile(String path, UplaodType type) {
		unfinishFilePaths.clear();
		finishFile.clear();
		taskMap.put(path, type);
		// 是否有在上
		if (!isUpLoad) {
			startUpload();
		}

	}

	/**
	 * 上传文件集合
	 * 
	 * @param tasks
	 */
	public void uploadFileMaps(Map<String, UplaodType> tasks) {
		unfinishFilePaths.clear();
		finishFile.clear();
		taskMap.putAll(tasks);
		LogUtil.e(TAG, taskMap.toString());
		// 是否有在上
		if (!isUpLoad) {
			startUpload();
		}
	}

	/**
	 * 开始上传
	 */
	private void startUpload() {
		if (taskMap == null || taskMap.isEmpty() || isUpLoad) {
			if (loadListener != null) {
				loadListener.onFinish(finishFile, unfinishFilePaths);
			}
			finishFile.clear();
			unfinishFilePaths.clear();
			return;
		}
		isUpLoad = true;
		LogUtil.e(TAG, "上传还剩文件数--------->" + taskMap.size() + " 个 ");
		// upFile(MapUtils.getFirstOrKey(taskMap));
		fileId = MapUtils.getFirstOrKey(taskMap);
		final String upTypePath = getUpTypePath(taskMap);
		if (!TextUtils.isEmpty(upTypePath)) {
			Thread upFileThread = new Thread(new Runnable() {
				@Override
				public void run() {
					upFile(upTypePath);
				}
			});
			upFileThread.start();

		}
	}

	/**
	 * 获取相应类型文件的路径
	 * 
	 * @param taskMap2
	 * @return
	 */
	private String getUpTypePath(Map<String, UplaodType> taskMap) {
		String localSrc = null;
		switch (MapUtils.getFirstOrValue(taskMap)) {
		case VIDEO_TYPE:
			Videos_model singleItem_video = DynamicShowDao.getInstance()
					.getSingleItem_video(DynamicShowDao.COLUMN_NAME_ID,
							new String[] { fileId });
			if (singleItem_video != null) {
				localSrc = singleItem_video.getLocalSrc();
				filebg = singleItem_video.getLocalSrcbg();
			}
			break;
		case IMAGE_TYPE:
			Imgs_model singleItem_img = DynamicShowDao.getInstance()
					.getSingleItem_img(DynamicShowDao.COLUMN_NAME_ID,
							new String[] { fileId });
			if (singleItem_img != null) {
				localSrc = singleItem_img.getLocalImgOri();
			}
			break;
		case AUDIO_TYPE:
			Voices_model singleItem_voice = DynamicShowDao.getInstance()
					.getSingleItem_voice(DynamicShowDao.COLUMN_NAME_ID,
							new String[] { fileId });
			if (singleItem_voice != null) {
				localSrc = singleItem_voice.getLocalSrc();
			}
			break;

		}
		return localSrc;
	}

	/**
	 * 获取请求参数
	 * 
	 * @return
	 */
	// private RequestParams getRequestParams(UplaodType type) {
	// RequestParams params = new RequestParams();
	// switch (type) {
	// case VIDEO_TYPE:
	//
	// break;
	// case IMAGE_TYPE:
	//
	// break;
	// case AUDIO_TYPE:
	//
	// break;
	//
	//
	// }
	// return null;
	// }
	/**
	 * 删除指定路径文件
	 * 
	 * @param filePath
	 */
	private void delete_temp_file(String filePath) {
		File file = new File(filePath);
		if (file.delete()) {
			file.delete();
		}
	}

	File file = null;

	private synchronized void upFile(String filePath) {
		file = new File(filePath);
		final String ImagePath = GlobalConstant.HEAD_ICON_SAVEPATH
				+ "temp_image.jpg";
		if (!file.exists()) {
			return;
		}
		// String upLoadingFilePath = filePath;
		if (httpUtils == null) {
			httpUtils = new HttpUtils();
			httpUtils.configDefaultHttpCacheExpiry(0);
		}
		RequestParams params = new RequestParams();

		// 上传背景图片
		if (taskMap.get(fileId) == UplaodType.VIDEO_TYPE) {

			File bgFile = new File(filebg);
			if (bgFile.exists()) {
				params.addBodyParameter("uploadedvimgfile", bgFile);
			}
		}
		// 压缩图片操作
		if (taskMap.get(fileId) == UplaodType.IMAGE_TYPE) {
			if (file.length() > GlobalConstant.IMAGEFILESIZE) {
				delete_temp_file(ImagePath);
				IconCompress.compressImageFile(filePath,
						GlobalConstant.IMAGEFILESIZE, ImagePath);
				File tmFile = new File(ImagePath);
				if (tmFile.exists()) {
					file = tmFile;
					params.addBodyParameter("uploadedfile", file);
				}
			} else {
				params.addBodyParameter("uploadedfile", file);
			}

		} else {
			params.addBodyParameter("uploadedfile", file);

		}
		LogUtil.e(TAG,
				"开始上传文件-----------> " + file.getName() + "---第 "
						+ taskMap.size() + " 个任务");
		httpUtils.send(HttpMethod.POST,
				NetworkConfig.getUpLoadFilePath(taskMap.get(fileId)), params,
				new com.lidroid.xutils.http.callback.RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						delete_temp_file(ImagePath);
						LogUtil.e(TAG, "---onFailure----" + file.getName()
								+ "----上传失败---次数----" + upCount);
						if (taskMap.isEmpty()) {
							return;
						}
						if (upCount == 3) {
							if (taskMap.containsKey(fileId)) {

								unfinishFilePaths.put(fileId,
										taskMap.get(fileId));
								taskMap.remove(fileId);
								upCount = 0;
								if (loadListener != null) {
									loadListener.onFailure(file.getName(), arg1);
								}
							}
						} else {
							upCount++;
						}
						isUpLoad = false;
						startUpload();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						delete_temp_file(ImagePath);
						UploadFileBean fileBean = null;
						try {
							fileBean = (UploadFileBean) JsonParse
									.parserJson(responseInfo.result,
											UploadFileBean.class);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						LogUtil.e(TAG, "************" + fileBean.msg + "**********");
						if ("1".equals(fileBean.status) && taskMap.size() != 5
								|| taskMap.size() != 11) {
							if (taskMap.containsKey(fileId)) {
								fileBean.fileID = fileId;
								finishFile.put(fileBean, taskMap.get(fileId));
								taskMap.remove(fileId);
								if (upCount != 0) {
									upCount = 0;
								}
								if (loadListener != null) {
									loadListener.onSuccess(file.getName());
								}
							}
							LogUtil.e(TAG, "--------" + file.getName()
									+ "-------上传成功---------");

						} else {
							upCount++;
							LogUtil.e(TAG, "--------" + file.getName()
									+ "---onSuccess----上传失败---------次数---"
									+ upCount + "  -----原因---" + fileBean.msg
									+ "--状态码---" + fileBean.status);
							if (upCount == 3) {
								if (taskMap.containsKey(fileId)) {
									unfinishFilePaths.put(fileId,
											taskMap.get(fileId));
									taskMap.remove(fileId);
									upCount = 0;

									if (loadListener != null) {
										loadListener.onFailure(file.getName(),
												"服务器错误");
									}
								}
							}
						}
						isUpLoad = false;
						startUpload();

					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						LogUtil.e(TAG, "total ：" + total + "----current : "
								+ current);
						super.onLoading(total, current, isUploading);

						if (loadListener == null) {
							loadListener.onLoading(file.getName(), total,
									current, isUploading);
						}
					}
				});
	}

	/**
	 * 请求回调
	 * 
	 * @author admin
	 * 
	 */
	public interface UpLoadListener {
		/**
		 * 上传失败
		 * 
		 * @param fileName
		 *            文件名
		 * @param reason
		 *            失败原因
		 */
		void onFailure(String fileName, String reason);

		/**
		 * 上传成功
		 * 
		 * @param fileName
		 *            文件名
		 */
		void onSuccess(String fileName);

		/**
		 * 正在上传文件进度
		 * 
		 * @param fileName
		 *            文件名
		 * @param total
		 *            文件大小
		 * @param current
		 *            当前上传大小
		 * @param isUploading
		 *            是否正在
		 */
		void onLoading(String fileName, long total, long current,
				boolean isUploading);

		/**
		 * 
		 * @param finishFile
		 *            成功返回的文件
		 * @param UnfinishFilePaths
		 *            公共文件
		 */
		void onFinish(Map<UploadFileBean, UplaodType> finishFile,
				Map<String, UplaodType> UnfinishFilePaths);
	}

	/**
	 * 设置上传进度监听
	 * 
	 * @param loadListener
	 */
	public void setUpLoadListener(UpLoadListener loadListener) {
		this.loadListener = loadListener;
	}

	/**
	 * 上传文件类型
	 * 
	 * @author admin
	 * 
	 */
	public static enum UplaodType {
		/**
		 * 视频类型
		 */
		VIDEO_TYPE,
		/**
		 * 音频类型
		 */
		AUDIO_TYPE,
		/**
		 * 图片类型
		 */
		IMAGE_TYPE
	}

	public class UploadFileBean extends BaseBaen {
		// 路径
		public String url;
		// 远程路径
		public String oriurl;
		// 文件ID
		public String id;
		// 本地ID
		public String fileID;
		// 大图路径
		public String bigimgurl;
		// 视频截图路径
		public String videoimgurl;

	}

}
