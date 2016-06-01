package com.aliamauri.meat.db.Dynamic_db.model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 从数据库中取出来 的动态数据赋值的bean
 * @author limaokeji-windosc
 *
 */


public class Dynamic_model implements Serializable {
	private static final long serialVersionUID = -6888699400004603851L;
	public String createtime;
	public String distance;
	public String dz;
	public String update_type;
	public String face;
	public String iszan;
	public String id;
	public ArrayList<Imgs_model> imgs;
	public String infos;
	public String isnm;
	public String isopen;
	public String jd;
	public String nickname;
	public String pj;
	public String relinfo;
	public String tags;
	public String mmtype;
	public String type;
	public String uid;
	public ArrayList<Videos_model> videos;
	public ArrayList<Voices_model> voices;
	public String wd;
	public String yd;
	public String zf;
	public String zfinfo;
	public String zfinfox;
	
	public String zf_createtime;
	public String zf_dz;
	public String zf_flid;
	public String zf_fnickname;//上一级名称
	public String zf_fuid;
	public String zf_id;
	public ArrayList<Imgs_model> zf_imgs;
	public String zf_infos;
	public String zf_isnm;
	public String zf_isopen;
	public String zf_jd;
	public String zf_olid;
	public String zf_onickname;//原始名称
	public String zf_ouid;
	public String zf_pj;
	public String zf_relinfo;
	public String zf_tags;
	public String zf_type;
	public String zf_uid;
	public ArrayList<Videos_model> zf_videos;
	public ArrayList<Voices_model> zf_voices;
	public String zf_wd;
	public String zf_yd;
	public String zf_zf;
	public String zf_zfinfo;
	@Override
	public String toString() {
		return "Dynamic_model [createtime=" + createtime + ", distance="
				+ distance + ", dz=" + dz + ", update_type=" + update_type
				+ ", face=" + face + ", iszan=" + iszan + ", id=" + id
				+ ", imgs=" + imgs + ", infos=" + infos + ", isnm=" + isnm
				+ ", isopen=" + isopen + ", jd=" + jd + ", nickname="
				+ nickname + ", pj=" + pj + ", relinfo=" + relinfo + ", tags="
				+ tags + ", mmtype=" + mmtype + ", type=" + type + ", uid="
				+ uid + ", videos=" + videos + ", voices=" + voices + ", wd="
				+ wd + ", yd=" + yd + ", zf=" + zf + ", zfinfo=" + zfinfo
				+ ", zfinfox=" + zfinfox + ", zf_createtime=" + zf_createtime
				+ ", zf_dz=" + zf_dz + ", zf_flid=" + zf_flid
				+ ", zf_fnickname=" + zf_fnickname + ", zf_fuid=" + zf_fuid
				+ ", zf_id=" + zf_id + ", zf_imgs=" + zf_imgs + ", zf_infos="
				+ zf_infos + ", zf_isnm=" + zf_isnm + ", zf_isopen="
				+ zf_isopen + ", zf_jd=" + zf_jd + ", zf_olid=" + zf_olid
				+ ", zf_onickname=" + zf_onickname + ", zf_ouid=" + zf_ouid
				+ ", zf_pj=" + zf_pj + ", zf_relinfo=" + zf_relinfo
				+ ", zf_tags=" + zf_tags + ", zf_type=" + zf_type + ", zf_uid="
				+ zf_uid + ", zf_videos=" + zf_videos + ", zf_voices="
				+ zf_voices + ", zf_wd=" + zf_wd + ", zf_yd=" + zf_yd
				+ ", zf_zf=" + zf_zf + ", zf_zfinfo=" + zf_zfinfo + "]";
	}

	
	
}
