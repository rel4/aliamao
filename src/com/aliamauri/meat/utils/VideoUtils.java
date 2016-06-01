package com.aliamauri.meat.utils;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.media.audiofx.LoudnessEnhancer;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import com.aliamauri.meat.R;
import com.aliamauri.meat.db.localvideo.LocalVideo;
import com.aliamauri.meat.db.localvideo.LocalVideoDao;
import com.aliamauri.meat.top.utils.FileUtils;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;

public class VideoUtils {
	private static VideoUtils videoUtils;
	private LocalVideo localVideo;

	public static synchronized VideoUtils getInstance() {
		if (videoUtils == null) {
			videoUtils = new VideoUtils();
		}
		return videoUtils;
	}

	private VideoUtils() {
	}

	public List<LocalVideo> getList(Context context) {
		List<LocalVideo> list = null;
		if (context != null) {
			Cursor cursor = context.getContentResolver().query(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
					null, null);
			if (cursor != null) {
				list = new ArrayList<LocalVideo>();
				while (cursor.moveToNext()) {
					int id = cursor.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
					localVideo = LocalVideoDao.getInstance()
							.SelectInfoByVideoId(id);
					if (localVideo != null
							&& CheckUtils.getInstance().isIcon(
									localVideo.imgPath)) {
						list.add(localVideo);
					} else {
						long duration = cursor
								.getInt(cursor
										.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
						if (duration >= 1000) {
							String title = cursor
									.getString(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
							String album = cursor
									.getString(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
							String artist = cursor
									.getString(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
							String displayName = cursor
									.getString(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
							String mimeType = cursor
									.getString(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
							String path = cursor
									.getString(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
							long size = cursor
									.getLong(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
							String imgPath = getVideoData(path);
							LocalVideo video = new LocalVideo(id, title, album,
									artist, displayName, mimeType, path, size,
									CaculationUtils.calSecond(duration / 1000));
							video.imgPath = imgPath;
							LocalVideoDao.getInstance().SaveDL(video);
							list.add(video);
						}
					}
				}
				cursor.close();
			}
		}
		LogUtil.e("LocalVideoActivity", "大小1==" + list.size() + "");
		return list;
	}

	public String getVideoThumbnail(String path) {
		String resultPath = null;
		if (path == null || "".equals(path)) {
			return resultPath;
		}
		File f = new File(path);
		File file = new File(PathUtil.getInstance().getImagePath()
				.getAbsolutePath(), "thvideo" + System.currentTimeMillis()
				+ ".jpg");
		Bitmap bitmap = null;
		FileOutputStream fos = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(path);
			bitmap = retriever.getFrameAtTime();

			if (bitmap == null) {
				EMLog.d("chatactivity",
						"problem load video thumbnail bitmap,use default icon");
				bitmap = BitmapFactory.decodeResource(UIUtils.getContext()
						.getResources(), R.drawable.app_panel_video_icon);
			}
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			resultPath = file.getAbsolutePath();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
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
			try {
				retriever.release();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		// return bitmap;
		return resultPath;
	}

	/**
	 * 获取视频数据
	 * 
	 * @param data
	 */
	public String getVideoData(String path) {
		String resultPath = null;
		if (path == null || "".equals(path)) {
			return resultPath;
		}
		File file = new File(PathUtil.getInstance().getImagePath()
				.getAbsolutePath(), "thvideo" + System.currentTimeMillis()
				+ ".jpg");
		Bitmap bitmap = null;
		FileOutputStream fos = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			try {
				MediaMetadataRetriever retriever = new MediaMetadataRetriever();
				retriever.setDataSource(path);
				bitmap = retriever.getFrameAtTime();
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
			}
			if (bitmap == null) {
				bitmap = ThumbnailUtils.createVideoThumbnail(path,
						Images.Thumbnails.MINI_KIND);
			}

			if (bitmap == null) {
				EMLog.d("chatactivity",
						"problem load video thumbnail bitmap,use default icon");
				bitmap = BitmapFactory.decodeResource(UIUtils.getContext()
						.getResources(), R.drawable.app_panel_video_icon);
			
			}
			// bitmap = ThumbnailUtils.extractThumbnail(bitmap, 512, 512);
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			LogUtil.e(this, "bitmap：Width=" + NetWorkUtil.formatSize(bitmap.getWidth()));
			LogUtil.e(this, "bitmap：Height=" + NetWorkUtil.formatSize(bitmap.getHeight()));
			LogUtil.e(this, "截图大小：" + NetWorkUtil.formatSize(file.length()));
			resultPath = file.getAbsolutePath();
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
		return resultPath;
	}

	/**
	 * 得到视频的长度
	 */
	public static long getVideoTime(File f) {
		long ls = 0;
		// 新建编码器对象
		Encoder encoder = new Encoder();
		try {
			// 得到多媒体视频的信息
			MultimediaInfo m = encoder.getInfo(f);
			// 得到毫秒级别的多媒体是视频长度
			ls = m.getDuration();
			// 转换为分秒
			// time = ls / 60000 + "分" + (ls - (ls / 60000 * 60000)) / 1000 +
			// "秒";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ls;

	}

	//
	// public static void main(String[] args) throws IOException {
	// String fileName = "F:\\QQ消息\\2663261303\\FileRecv\\33.mp4";
	// File f = new File(fileName);
	// System.out.println(getVideoTime(f));
	// }

}
