package com.aliamauri.meat.fragment;

import java.io.File;
import java.util.ArrayList;

import android.os.Handler;
import android.widget.TextView;

import com.aliamauri.meat.activity.nearby_activity.BroadCast_DT;
import com.aliamauri.meat.activity.nearby_activity.Retrans_DT;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist.Imgs;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist.Videos;
import com.aliamauri.meat.bean.DtBean.Cont.Tlist.Voices;
import com.aliamauri.meat.db.Dynamic_db.DbManager_dynamic;
import com.aliamauri.meat.db.Dynamic_db.model.Dynamic_model;
import com.aliamauri.meat.db.Dynamic_db.model.Imgs_model;
import com.aliamauri.meat.db.Dynamic_db.model.Videos_model;
import com.aliamauri.meat.db.Dynamic_db.model.Voices_model;
import com.aliamauri.meat.eventBus.sendDownLoadedOrder;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.listener.VoicePlay;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;

import de.greenrobot.event.EventBus;

/**
 * 动态中的工具类
 * 
 * @author limaokeji-windosc
 * 
 */
public class DynamicUtlis {
	private DynamicUtlis() {
	};

	private static DynamicUtlis utils;
	private static Object obj = new Object();

	public static DynamicUtlis getInstance() {
		synchronized (obj) {
			if (utils == null) {
				utils = new DynamicUtlis();
			}
			return utils;
		}
	}

	/**
	 * 播放语音消息
	 * 
	 * @param tAG
	 * 
	 * @param mVoiceBtn
	 */
	public void playVoice(VoicePlay mVoicePlay, Voices_model model,
			TextView tv, String type) {
		if (model == null) {
			return;
		}

		if (model.getLocalSrc() != null
				&& new File(model.getLocalSrc()).exists()) { // 本地有播放本地语音
			ShowTimer(tv, model, true, type);
			mVoicePlay.playVoice(model.getLocalSrc());
		} else if (model.getSrc() != null) {// 本地没有将网络路径的语音文件下载本地后进行播放\
			if (model.getSrc() == null) {
				UIUtils.showToast(UIUtils.getContext(), "音频文件丢失~~");
				return;
			}
			if (tv != null && tv.isClickable()) {
				tv.setFocusable(false);
				tv.setClickable(false);
			}
			String[] split = model.getSrc().split("/");
			String targetPath = GlobalConstant.DYNAMIC_MOVICE_SAVEPATH
					+ model.getId() + "$$$" + split[split.length - 1];
			if (new File(targetPath).exists()) { // 如果插入的目的地路径存在同名文件就删除它
				new File(targetPath).delete();									
			}
			
			DbManager_dynamic.getInstance().downloadFile(model.getSrc(),
						targetPath, model.getId(), GlobalConstant.VOICE_TABLE,
						type);
		}
	}

	/**
	 * 停止播放语音消息
	 */
	public void stopPlayVoice(TextView mVoiceBtn, Voices_model model,
			VoicePlay mVoicePlay, String type) {
		mVoicePlay.stopPlayVoice();
		if (model != null) {
			ShowTimer(mVoiceBtn, model, false, type);
		}
	}

	Runnable mRunnable = null;

	/**
	 * 动态显示语音条上的时间
	 * 
	 * @param mVoiceBtn
	 *            播放语音按钮
	 * @param model
	 *            从数据库中获取的语音数据
	 * @param b
	 *            true,播放语音，false；停止播放语音
	 * @param type
	 */
	public void ShowTimer(final TextView mVoiceBtn, final Voices_model model,
			boolean b, final String type) {
		if (mVoiceBtn == null) {
			return;
		}
		final Handler handler = UIUtils.getHandler();
		if (b) {
			mRunnable = new Runnable() {
				int i = 0;

				@Override
				public void run() {
					i++;
					mVoiceBtn.setText(StringUtils.secToTime(i));
					if (StringUtils.secToTime(i).equals(
							StringUtils.secToTime(Integer.parseInt(model
									.getSc())))) {
						handler.removeCallbacks(this);
						EventBus.getDefault().post(
								new sendDownLoadedOrder(0, null, type));
						return;
					}
					handler.postDelayed(this, 1000);
				}
			};

			handler.postDelayed(mRunnable, 1000);
		} else {
			handler.removeCallbacks(mRunnable);
			mVoiceBtn.setText(StringUtils.secToTime(Integer.parseInt(model
					.getSc())));
			EventBus.getDefault().post(new sendDownLoadedOrder(0, null, type));
		}
	}

	Runnable mRunnable_bc = null;

	/**
	 * 发布,转发动态显示语音条上的时间
	 * 
	 * @param mVoiceBtn
	 *            播放语音按钮
	 * @param sc
	 *            时长
	 * @param b
	 *            true,播放语音，false；停止播放语音
	 */
	public void ShowTimer_bc(final TextView mVoiceBtn, final String sc,
			boolean b) {
		if (mVoiceBtn == null) {
			return;
		}
		final Handler handler = UIUtils.getHandler();
		if (b) {
			mRunnable_bc = new Runnable() {
				int i = 0;

				@Override
				public void run() {
					i++;
					mVoiceBtn.setText(StringUtils.secToTime(i));
					if (StringUtils.secToTime(i).equals(
							StringUtils.secToTime(Integer.parseInt(sc)))) {
						handler.removeCallbacks(this);
						BroadCast_DT.mIsplay = true;
						Retrans_DT.mIsplay = true;
						return;
					}
					handler.postDelayed(this, 1000);
				}
			};
			handler.postDelayed(mRunnable_bc, 1000);
		} else {
			handler.removeCallbacks(mRunnable_bc);
			mVoiceBtn.setText(StringUtils.secToTime(Integer.parseInt(sc)));
			BroadCast_DT.mIsplay = true;
			Retrans_DT.mIsplay = true;
		}
	}

	/**
	 * 获取头像布局
	 * 
	 * @return
	 */
	public String getHead_icon() {

		File file = new File(
				PrefUtils.getString(GlobalConstant.USER_FACE, null));
		if (file.exists()) {
			return PrefUtils.getString(GlobalConstant.USER_FACE, null);
		}
		return null;
	}

	/**
	 * 将Tlist转换成Dynamic_model格式
	 * 
	 * @param tlist
	 * @return Dynamic_model
	 */
	public Dynamic_model transferFormat(Tlist tlist) {
		Dynamic_model model = new Dynamic_model();
		model.createtime = tlist.createtime;
		model.distance = tlist.distance;
		model.dz = tlist.dz;
		model.face = tlist.face;
		model.iszan = tlist.iszan;
		model.id = tlist.id;
		model.imgs = getImgs(tlist.imgs);
		model.infos = tlist.infos;
		model.isnm = tlist.isnm;
		model.isopen = tlist.isopen;
		model.jd = tlist.jd;
		model.nickname = tlist.nickname;
		model.pj = tlist.pj;
		model.relinfo = tlist.relinfo;
		model.tags = tlist.tags;
		model.mmtype = tlist.mmtype;
		model.type = tlist.type;
		model.uid = tlist.uid;
		model.videos = getVideos(tlist.videos);
		model.voices = getVoices(tlist.voices);
		model.wd = tlist.wd;
		model.yd = tlist.yd;
		model.zf = tlist.zf;
		model.zfinfo = tlist.zfinfo;
		if (tlist.zfinfox != null) {
			model.zfinfox = tlist.zfinfox.id;
			model.zf_createtime = tlist.zfinfox.createtime;
			model.zf_dz = tlist.zfinfox.dz;
			model.zf_flid = tlist.zfinfox.flid;
			model.zf_fnickname = tlist.zfinfox.fnickname;
			model.zf_fuid = tlist.zfinfox.fuid;
			model.zf_id = tlist.zfinfox.id;
			model.zf_imgs = getImgs(tlist.zfinfox.imgs);
			model.zf_infos = tlist.zfinfox.infos;
			model.zf_isnm = tlist.zfinfox.isnm;
			model.zf_isopen = tlist.zfinfox.isopen;
			model.zf_jd = tlist.zfinfox.jd;
			model.zf_olid = tlist.zfinfox.olid;
			model.zf_onickname = tlist.zfinfox.onickname;
			model.zf_ouid = tlist.zfinfox.ouid;
			model.zf_pj = tlist.zfinfox.pj;
			model.zf_relinfo = tlist.zfinfox.relinfo;
			model.zf_tags = tlist.zfinfox.tags;
			model.zf_type = tlist.zfinfox.type;
			model.zf_uid = tlist.zfinfox.uid;
			model.zf_videos = getVideos(tlist.zfinfox.videos);
			model.zf_voices = getVoices(tlist.zfinfox.voices);
			model.zf_wd = tlist.zfinfox.wd;
			model.zf_yd = tlist.zfinfox.yd;
			model.zf_zf = tlist.zfinfox.zf;
			model.zf_zfinfo = tlist.zfinfox.zfinfo;
		}
		return model;
	}

	/**
	 * 获取语音数据
	 * 
	 * @param voices
	 * @return
	 */
	private ArrayList<Voices_model> getVoices(ArrayList<Voices> voices) {
		ArrayList<Voices_model> list = new ArrayList<>();
		Voices_model model = null;
		for (int i = 0; voices != null && voices.size() > 0 && i<voices.size(); i++) {
			model = new Voices_model();
			model.setId(voices.get(i).id);
			model.setSrc(voices.get(i).src);
			model.setSrcbg(voices.get(i).srcbg);
			model.setSc(voices.get(i).sc);
			model.setFilesize(voices.get(i).filesize);
			list.add(model); 
		}
		return list;
	}

	/**
	 * 获取视频数据
	 * 
	 * @param videos
	 * @return
	 */
	private ArrayList<Videos_model> getVideos(ArrayList<Videos> videos) {
		ArrayList<Videos_model> list = new ArrayList<>();
		Videos_model model = null;
		for (int i = 0; videos != null && videos.size() > 0 && i<videos.size(); i++) {
			model = new Videos_model();
			model.setId(videos.get(i).id);
			model.setSrc(videos.get(i).src);
			model.setSrcbg(videos.get(i).srcbg);
			model.setSc(videos.get(i).sc);
			model.setFilesize(videos.get(i).filesize);
			list.add(model); 
		}
		return list;
	}

	/**
	 * 获取照片数据
	 * 
	 * @param imgs
	 * @return
	 */
	private ArrayList<Imgs_model> getImgs(ArrayList<Imgs> imgs) {
		ArrayList<Imgs_model> list = new ArrayList<>();
		Imgs_model model =null; 
		for (int i = 0; imgs != null && imgs.size() > 0 && i<imgs.size(); i++) {
			model = new Imgs_model();
			model.setId(imgs.get(i).id);
			model.setImg(imgs.get(i).img);
			model.setImgori(imgs.get(i).imgori);
			model.setInfo(imgs.get(i).info);
			list.add(model); 
		}
		return list;
	}

}
