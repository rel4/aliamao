package com.aliamauri.meat.Manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.aliamauri.meat.db.Dbutils;
import com.aliamauri.meat.db.TempFileUpBeanTask;
import com.aliamauri.meat.db.TempUploadFileDao;
import com.aliamauri.meat.db.Dynamic_db.DbManager_dynamic;
import com.aliamauri.meat.db.Dynamic_db.DynamicShowDao;
import com.aliamauri.meat.db.Dynamic_db.model.HomeTable_model;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.listener.ConnectionChangeListener;
import com.aliamauri.meat.listener.ConnectionChangeListener.OnConnectionChangeListener;
import com.aliamauri.meat.listener.ConnectionChangeListener.PhoneNetType;
import com.aliamauri.meat.service.UploadService;
import com.aliamauri.meat.service.UploadService.UpLoadListener;
import com.aliamauri.meat.service.UploadService.UplaodType;
import com.aliamauri.meat.service.UploadService.UploadFileBean;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.MapUtils;
import com.aliamauri.meat.utils.ObjectUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.easemob.util.NetUtils;

/**
 * 发送文件管理
 * 
 * @author admin
 * 
 */
public class SendFileManager {
	private final static SendFileManager manager = new SendFileManager();
	private Context mContext;
	private UploadService us;
	private final static int FLAG_START_SERVICE = 1;// 开启服务
	//数据缓存
	private final static String FLAG_SAVE_DATAS="CacheList";
	//缓存数据ID
	private final static String FLAGE_SAVE_IDS="CacheList_id";
	private boolean isExecuteFileUptask = false;// 是否有执行任务在上传
	private boolean isTaskExceptionStop = false;// 任务异常停止
	private String FileUpName;
	private List<String> ids =new ArrayList<String>();
	private Map<String, TempFileUpBeanTask> taskMap = new TreeMap<String, TempFileUpBeanTask>(
			new Comparator<String>() {

				@Override
				public int compare(String object1, String object2) {
					return object1.compareTo(object2);
				}
			});

	private SendFileManager() {
		mContext = UIUtils.getContext();
		// mContext.startService(new Intent(mContext, UploadService.class));
		// mContext.bindService(new Intent(mContext, UploadService.class), conn,
		// Context.BIND_AUTO_CREATE);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FLAG_START_SERVICE:
				startExeTask();
				break;
			}
		};
	};
	private static String TAG = "SendFileManager";

	public static synchronized SendFileManager getInstance() {
		return manager;
	}

	/**
	 * 上传视频文件
	 * 
	 * @param filePath
	 *            文件路径
	 */
	private void sendVideoFile(String filePath) {
		LogUtil.e(TAG, "-------->下载路径-------->" + filePath);
		// if (us != null) {
		us.uploadFile(filePath, UplaodType.VIDEO_TYPE);
		// }else {
		// startSendService();
		// mHandler.sendEmptyMessageDelayed(FLAG_START_SERVICE_AND_VIDEO, 1000);
		// }

	}

	/**
	 * 上传音频文件
	 * 
	 * @param filePath
	 */
	private void sendAudioFile(String filePath) {
		// audioPath=filePath;
		// if (us != null) {
		us.uploadFile(filePath, UplaodType.AUDIO_TYPE);
		// }else {
		// startSendService();
		// mHandler.sendEmptyMessageDelayed(FLAG_START_SERVICE_AND_AUDIO, 1000);
		// }
	}

	/**
	 * 上传图片文件
	 * 
	 * @param filePath
	 */
	private void sendImageFile(String filePath) {
		// audioPath=filePath;
		// if (us != null) {
		us.uploadFile(filePath, UplaodType.IMAGE_TYPE);
		// }else {
		//
		// }
	}

	private void startSendService() {
		if (!isServiceRunning(UploadService.class)) {
			mContext.startService(new Intent(mContext, UploadService.class));
		}
		mContext.bindService(new Intent(mContext, UploadService.class), conn,
				Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			us = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			us = (UploadService) ((UploadService.MyBinder) service)
					.getService();
		}
	};
	private SendUpLoadListener listener;// 发送监听

	/**
	 * 判断服务开启
	 * 
	 * @param serviceName
	 * @return
	 */
	private boolean isServiceRunning(Class clz) {
		String serviceName = clz.getName();
		ActivityManager manager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 上传
	 * 
	 * @param taskId
	 *            上传id
	 */
	public void startUpTask(String taskId) {
		
		ids.add(taskId);
		DynamicShowDao instance = DynamicShowDao.getInstance();
		HomeTable_model dynamic = instance.getSingleItem_dynamic(
				DynamicShowDao.COLUMN_NAME_ID, new String[] { taskId });
		if (dynamic == null) {
			return;
		}
		TempFileUpBeanTask tempFileUpBeanTask = new TempFileUpBeanTask();
		List imgs = instance.getListItems_imgs(DynamicShowDao.COLUMN_NAME_ID,
				Dbutils.splitFileNameArray(dynamic.getImgs()));
		List videos = instance.getListItem_videos(
				DynamicShowDao.COLUMN_NAME_ID,
				Dbutils.splitFileNameArray(dynamic.getVideos()));
		List voices = instance.getListItem_voices(
				DynamicShowDao.COLUMN_NAME_ID,
				Dbutils.splitFileNameArray(dynamic.getVoices()));
		tempFileUpBeanTask.setFileTypeImage(getFilePathList(imgs));
		tempFileUpBeanTask.setFileTypeAudio(getFilePathList(voices));
		tempFileUpBeanTask.setFileTypeVideo(getFilePathList(videos));

		// tempFileUpBeanTask.setFileTypeAudio(instance.getSingleItem_img(DynamicShowDao.COLUMN_NAME_ID,
		// new String[]{taskId})Dbutils.splitFileName(dynamic.getVoices()));
		// tempFileUpBeanTask.setFileTypeAudio(Dbutils.splitFileName(dynamic.getVoices())));
		// tempFileUpBeanTask.setFileTypeVideo(Dbutils.splitFileName(dynamic.getVideos()));
		// tempFileUpBeanTask.setFileTypeImage(Dbutils.splitFileName(dynamic.getImgs()));
		tempFileUpBeanTask.setSubmitTime(dynamic.getCreatetime());
		startUpfFileTask(tempFileUpBeanTask);

	}

	/**
	 * 获取文件集合
	 * 
	 * @param obj
	 * @return
	 */
	private List<String> getFilePathList(List<Object> obj) {
		if(obj == null){
			return null;
		}
		List<String> arrayList = new ArrayList<String>();

//		for (Object object : obj) {
//			if (object instanceof Imgs_model) {
//				// arrayList.add(((Imgs_model)object).getImg());
//				arrayList.add(((Imgs_model) object).getLocalImgOri());
//			} else if (object instanceof Videos_model) {
//				arrayList.add(((Videos_model) object).getLocalSrc());
//			} else if (object instanceof Voices_model) {
//				arrayList.add(((Voices_model) object).getLocalSrc());
//			}
//		}
		for (Object object : obj) {
			if (object instanceof Imgs_model) {
				// arrayList.add(((Imgs_model)object).getImg());
				arrayList.add(((Imgs_model) object).getId());
			} else if (object instanceof Videos_model) {
				arrayList.add(((Videos_model) object).getId());
			} else if (object instanceof Voices_model) {
				arrayList.add(((Voices_model) object).getId());
			}
		}
		return arrayList;
	}

	private void startUpfFileTask(TempFileUpBeanTask beanTask) {

		saveTask(beanTask);
		LogUtil.e(TAG, "目前共有" + taskMap.size() + "个任务");
		LogUtil.e(TAG, "添加之后任务------->" + taskMap.toString());
		startExeTask();
		// if (us == null) {
		// startSendService();
		// mHandler.sendEmptyMessageDelayed(FLAG_START_SERVICE, 1000);
		// } else {
		// startExeTask();
		//
		// }
	}

	/**
	 * 开始执行任务
	 */
	private void startExeTask() {
		// if (taskMap == null || taskMap.isEmpty()) {
		// return;
		// }
		if (us == null) {
			startSendService();
			mHandler.sendEmptyMessageDelayed(FLAG_START_SERVICE, 1000);
		} else {
			if (!isExecuteFileUptask && !taskMap.isEmpty()) {
				isExecuteFileUptask = true;
				LogUtil.e(TAG, "剩下任务为------->" + taskMap.toString());
				FileUpName = MapUtils.getFirstOrKey(taskMap);
				LogUtil.e(TAG, "开始执行-------" + FileUpName);
				distributeTask(taskMap.get(FileUpName));
				if (listener == null) {
					listener = new SendUpLoadListener();
				}
				us.setUpLoadListener(listener);
			}

		}

	}

	/**
	 * 保存任务
	 */
	private void saveTask(TempFileUpBeanTask beanTask) {
		// taskMap.
		SendFileManager.getInstance().addMap(beanTask);
		TempUploadFileDao uploadFileDao = new TempUploadFileDao();
		uploadFileDao.saveTempFileTask(beanTask);
	}

	/**
	 * 分配任务
	 * 
	 * @param beanTask
	 */
	private void distributeTask(TempFileUpBeanTask beanTask) {
		if (beanTask == null) {
			return;
		}
		Map<String, UplaodType> tasks = new LinkedHashMap<String, UplaodType>();
		Map<String, UplaodType> exeImageTaskMap = getExeTaskMap(
				beanTask.getFileTypeImage(), UplaodType.IMAGE_TYPE);
		if (exeImageTaskMap != null && !exeImageTaskMap.isEmpty()) {
			tasks.putAll(exeImageTaskMap);
		}
		Map<String, UplaodType> exeAudioTaskMap = getExeTaskMap(
				beanTask.getFileTypeAudio(), UplaodType.AUDIO_TYPE);
		if (exeAudioTaskMap != null && !exeAudioTaskMap.isEmpty()) {
			tasks.putAll(exeAudioTaskMap);
		}
		Map<String, UplaodType> exeVideoTaskMap = getExeTaskMap(
				beanTask.getFileTypeVideo(), UplaodType.VIDEO_TYPE);
		if (exeVideoTaskMap != null && !exeVideoTaskMap.isEmpty()) {
			tasks.putAll(exeVideoTaskMap);
		}
		us.uploadFileMaps(tasks);

	}

	private Map<String, UplaodType> getExeTaskMap(List<String> tasks,
			UplaodType uplaodType) {
		Map<String, UplaodType> hashMap = new LinkedHashMap<String, UplaodType>();
		if (tasks == null || tasks.isEmpty()) {
			return null;
		}
		for (String string : tasks) {
			hashMap.put(string, uplaodType);
		}
		return hashMap;
	}

	/**
	 * 上传监听
	 * 
	 * @author admin
	 * 
	 */
	private class SendUpLoadListener implements UpLoadListener {

		private UpFileConnectionChangeListener connectionChangeListener;

		@Override
		public void onFailure(String fileName, String reason) {

		}

		@Override
		public void onSuccess(String fileName) {

		}

		@Override
		public void onLoading(String fileName, long total, long current,
				boolean isUploading) {

		}

		@Override
		public void onFinish(Map<UploadFileBean, UplaodType> finishFile,
				Map<String, UplaodType> unfinishFilePaths) {

			if (unfinishFilePaths.size() != 0) {
				// 添加上上传中断数据
				Map<String, Map<UploadFileBean, UplaodType>> loadList = ObjectUtils
						.loadMap(UIUtils.getContext(),FLAG_SAVE_DATAS);
				if (loadList != null && loadList.containsKey(FileUpName)) {
					finishFile.putAll(loadList.get(FileUpName));
				}
				ObjectUtils.saveCacheMap(saveExceptionFinishData(finishFile),FLAG_SAVE_DATAS);
				//添加上次中断ID
				List<String> oldIds = ObjectUtils.loadList(mContext, FLAGE_SAVE_IDS);
				for (String str : ids) {
					if (!oldIds.contains(str)) {
						oldIds.add(str);
					}
				}
				ObjectUtils.saveCacheList(oldIds, FLAGE_SAVE_IDS);
				LogUtil.e(TAG, "上传失败-------->  " + FileUpName + "  任务，等待下次唤醒任务");
				LogUtil.e(TAG, "上传失败名单--------> " + unfinishFilePaths.toString());
				// 保存上传未完成数据
				if (taskMap.containsKey(FileUpName)) {
					TempUploadFileDao uploadFileDao = new TempUploadFileDao();
					TempFileUpBeanTask tempFileUpBeanTask = taskMap
							.get(FileUpName);
					if (tempFileUpBeanTask == null) {
						return;
					}
					TempFileUpBeanTask upTempFileUpBeanTask = upTempFileUpBeanTask(
							tempFileUpBeanTask, unfinishFilePaths);
					taskMap.remove(FileUpName);
					taskMap.put(FileUpName, upTempFileUpBeanTask);
					uploadFileDao.upTempFileTask(upTempFileUpBeanTask);
					LogUtil.e(TAG,
							"完成的任务返回的数据----"+oldIds.size()+"----> "
									+ ObjectUtils
											.loadMap(UIUtils.getContext(),FLAG_SAVE_DATAS)
											.get(FileUpName).size());
				}
				// 中断
				isTaskExceptionStop = true;
				// uploadFileDao.upTempFileTask(fileUpBean);
				// 添加网络变化监听,并再次测试网络环境,并上传成功概率大于75% 再次唤醒上传
				if (NetUtils.isWifiConnection(UIUtils.getContext())
						&& (finishFile.size() * 100)
								/ (taskMap.size() == 0 ? 1 : taskMap.size()) > 60) {
					isExecuteFileUptask = false;
					awakenTask();
				} else {
					if (connectionChangeListener == null) {
						connectionChangeListener = new UpFileConnectionChangeListener();
					}
					ConnectionChangeListener.getInstance()
							.addConnectionChangeListener(
									connectionChangeListener);

				}
			} else {
				LogUtil.e(TAG, "完成的任务返回的数据--------> " + finishFile.toString());
				// if (isTaskExceptionStop) {
				// 添加网络中断完成的数据
				Map<String, Map<UploadFileBean, UplaodType>> loadList = ObjectUtils
						.loadMap(UIUtils.getContext(),FLAG_SAVE_DATAS);
				if (loadList != null && loadList.containsKey(FileUpName)) {
					Map<UploadFileBean, UplaodType> map = loadList
							.get(FileUpName);
					map.putAll(finishFile);
					LogUtil.e(TAG, "完成的任务--全部--的数据--------> " + map.size());
					saveDBdata(map);
				} else {
					saveDBdata(finishFile);
				}

				// }
				// 开始下次任务
				executeNextTask();
			}

		}

	}

	/**
	 * 保存上传中断完成的带标识文件
	 * 
	 * @param finishFile
	 */
	private Map<String, Map<UploadFileBean, UplaodType>> saveExceptionFinishData(
			Map<UploadFileBean, UplaodType> finishFile) {
		Map<String, Map<UploadFileBean, UplaodType>> hashMap = new HashMap<String, Map<UploadFileBean, UplaodType>>();
		hashMap.put(FileUpName, finishFile);
		return hashMap;
	}

	/**
	 * 保存数据
	 * 
	 * @param map
	 */
	public void saveDBdata(Map<UploadFileBean, UplaodType> map) {
		if (map==null||map.size()==0) {
			return;
		}
		DbManager_dynamic instance = DbManager_dynamic.getInstance();
		ArrayList<String> imgs = null;
		ArrayList<String> voices = null;
		ArrayList<String> videos = null;
		for (UploadFileBean key : map.keySet()) {
			switch (map.get(key)) {
			case AUDIO_TYPE:
				Voices_model singleItem_voice = instance.getSingleItem_voice(
						DynamicShowDao.COLUMN_NAME_ID,
						new String[] { key.fileID });
				if (singleItem_voice != null) {
					singleItem_voice.setSrc(key.oriurl);
					singleItem_voice.setId(key.id);
					LogUtil.e(TAG, "测试" + key.oriurl);
					instance.UpdateToVoice_Table(singleItem_voice,
							key.fileID);
					if (voices == null) {
						voices = new ArrayList<String>();
					}
					voices.add(key.id);
				}
				break;
			case IMAGE_TYPE:
				Imgs_model singleItem_img = instance.getSingleItem_img(
						DynamicShowDao.COLUMN_NAME_ID,
						new String[] { key.fileID });
				if (singleItem_img != null) {
					// 大图
					singleItem_img.setId(key.id);
					singleItem_img.setImgori(key.oriurl);
					singleItem_img.setImg(key.url);
					LogUtil.e(TAG, "测试" + key.oriurl);
					instance.UpdateToImage_Table(singleItem_img, key.fileID);
					if (imgs == null) {
						imgs = new ArrayList<String>();
					}
					imgs.add(key.id);
				}
				break;
			case VIDEO_TYPE:
				Videos_model singleItem_video = instance.getSingleItem_video(
						DynamicShowDao.COLUMN_NAME_ID,
						new String[] { key.fileID });
				if (singleItem_video != null) {
					singleItem_video.setSrc(key.oriurl);
					singleItem_video.setId(key.id);
					singleItem_video.setSrcbg(key.videoimgurl);
					LogUtil.e(TAG, "测试" + key.oriurl);
					instance.UpdateToVideo_Table(singleItem_video,
							key.fileID);
					if (videos == null) {
						videos = new ArrayList<String>();
					}
					videos.add(key.id);
				}
				break;

			}
			
		}
		List<String> oldIds = ObjectUtils.loadList(mContext, FLAGE_SAVE_IDS);
		if (oldIds==null) {
			oldIds=new ArrayList<String>();
		}
		for (String str : ids) {
			if (!oldIds.contains(str)) {
				oldIds.add(str);
			}
		}
		if (oldIds==null||oldIds.size()==0) {
			return;
		}
		String id = oldIds.get(0);
		if (TextUtils.isEmpty(id)) {
			return;
		}
		if (videos!=null) {
			DynamicShowDao.getInstance().UpdateDynamicTable_video(videos, id);
		}
		if (voices!=null) {
			DynamicShowDao.getInstance().UpdateDynamicTable_voice(voices, id);
		}
		if (imgs!=null) {
			DynamicShowDao.getInstance().UpdateDynamicTable_img(imgs, id);
		}
		ObjectUtils.saveCacheList(null, FLAGE_SAVE_IDS);
		if (ids.contains(id)) {
			
			HomeTable_model model = new HomeTable_model();
			model.setUpdate_type(GlobalConstant.UPLOADTYPE_2);
			DynamicShowDao.getInstance().singleItemUpdate(GlobalConstant.DYNAMIC_TABLE, id, model, null, null, null, null);
			
			LogUtil.e(TAG, "移除ID-------->"+id);
			UploadDynamicData dynamicData = new UploadDynamicData();
			dynamicData.uploadDataToNet(id);
			ids.remove(id);
		}
	}

	/**
	 * 保存未完成任务
	 * 
	 * @param beanTask
	 * @param finishFilePaths
	 * @return
	 */
	private TempFileUpBeanTask upTempFileUpBeanTask(
			TempFileUpBeanTask beanTask,
			Map<String, UplaodType> unfinishFilePaths) {
		List<String> fileTypeVideo = null;
		List<String> fileTypeAudio = null;
		List<String> fileTypeImage = null;
		TempFileUpBeanTask tempFileUpBeanTask = new TempFileUpBeanTask();
		for (String key : unfinishFilePaths.keySet()) {
			switch (unfinishFilePaths.get(key)) {
			case VIDEO_TYPE:
				if (fileTypeVideo == null) {
					fileTypeVideo = new ArrayList<String>();
				}
				fileTypeVideo.add(key);
				continue;
			case AUDIO_TYPE:
				if (fileTypeAudio == null) {
					fileTypeAudio = new ArrayList<String>();
				}
				fileTypeAudio.add(key);
			case IMAGE_TYPE:
				if (fileTypeImage == null) {
					fileTypeImage = new ArrayList<String>();
				}
				fileTypeImage.add(key);
				continue;
			}
		}
		tempFileUpBeanTask.setFileTypeAudio(fileTypeAudio);
		tempFileUpBeanTask.setFileTypeImage(fileTypeImage);
		tempFileUpBeanTask.setFileTypeVideo(fileTypeVideo);
		tempFileUpBeanTask.setFileTypeText(beanTask.getFileTypeText());
		tempFileUpBeanTask.setSubmitTime(beanTask.getSubmitTime());
		return tempFileUpBeanTask;

	}

	/**
	 * 开始执行下个任务
	 */
	public synchronized void executeNextTask() {

		SendFileManager.getInstance().removeMap();
		isExecuteFileUptask = false;
		LogUtil.e(TAG, "完成-------->  " + FileUpName + "  任务，开始下个任务");
		startExeTask();

	}

	/**
	 * 添加任务
	 */
	private synchronized void addMap(TempFileUpBeanTask beanTask) {
		taskMap.put(beanTask.getSubmitTime(), beanTask);
	}

	/**
	 * 移除任务
	 */
	private synchronized void removeMap() {
		if (taskMap.containsKey(FileUpName)) {
			taskMap.remove(FileUpName);
			TempUploadFileDao fileDao = new TempUploadFileDao();
			fileDao.deleteTempFileTask(FileUpName);
			boolean containsKey = ObjectUtils.loadMap(UIUtils.getContext(),FLAG_SAVE_DATAS)
					.containsKey(FileUpName);
			if (containsKey) {
				ObjectUtils.saveCacheMap(null,FLAG_SAVE_DATAS);
			}
		}
	}

	private class UpFileConnectionChangeListener implements
			OnConnectionChangeListener {

		@Override
		public void onConnectionChange(PhoneNetType type) {
			if (type == PhoneNetType.WIFI_NET) {
				LogUtil.e(TAG, "wifi网络");
				if (isTaskExceptionStop) {
					if (!taskMap.isEmpty()) {
						isExecuteFileUptask = false;
						startExeTask();
					}
				}

			}

		}

	}

	/**
	 * 唤醒任务
	 */
	public void awakenTask() {
		LogUtil.e(TAG, "唤醒" + FileUpName + "开始任务");
		if (taskMap.isEmpty()) {
			List<TempFileUpBeanTask> tempFileUpTask = new TempUploadFileDao()
					.getTempFileUpTask();
			for (TempFileUpBeanTask tempFileUpBeanTask : tempFileUpTask) {
				if (!taskMap.containsKey(tempFileUpBeanTask.getSubmitTime())) {
					addMap(tempFileUpBeanTask);
				}
			}
		}
		if (!taskMap.isEmpty()) {
			isExecuteFileUptask = false;
			startExeTask();
		}
	}

	/**
	 * 获取是否有任务上传
	 * 
	 * @return
	 */
	public boolean isHaveTask() {
		return isExecuteFileUptask;
	}

}
