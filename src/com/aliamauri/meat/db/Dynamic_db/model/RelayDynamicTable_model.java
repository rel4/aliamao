package com.aliamauri.meat.db.Dynamic_db.model;

import java.io.Serializable;

/**
 * 动态转发表读写数据库的模型
 * @author limaokeji-windosc
 *
 */
public class RelayDynamicTable_model implements Serializable{
	private static final long serialVersionUID = -4224270006024706367L;
	private String createtime;
	private String dz;
	private String flid;
	private String fnickname;// 上一级名称
	private String fuid;
	private String id;
	private String imgs;
	private String infos;
	private String isnm;
	private String isopen;
	private String jd;
	private String olid;
	private String onickname;// 原始名称
	private String ouid;
	private String pj;
	private String relinfo;
	private String tags;
	private String type;
	private String uid;
	private String videos;
	private String voices;
	private String wd;
	private String yd;
	private String zf;
	private String zfinfo;
	
	
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getDz() {
		return dz;
	}
	public void setDz(String dz) {
		this.dz = dz;
	}
	public String getFlid() {
		return flid;
	}
	public void setFlid(String flid) {
		this.flid = flid;
	}
	public String getFnickname() {
		return fnickname;
	}
	public void setFnickname(String fnickname) {
		this.fnickname = fnickname;
	}
	public String getFuid() {
		return fuid;
	}
	public void setFuid(String fuid) {
		this.fuid = fuid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImgs() {
		return imgs;
	}
	public void setImgs(String imgs) {
		this.imgs = imgs;
	}
	public String getInfos() {
		return infos;
	}
	public void setInfos(String infos) {
		this.infos = infos;
	}
	public String getIsnm() {
		return isnm;
	}
	public void setIsnm(String isnm) {
		this.isnm = isnm;
	}
	public String getIsopen() {
		return isopen;
	}
	public void setIsopen(String isopen) {
		this.isopen = isopen;
	}
	public String getJd() {
		return jd;
	}
	public void setJd(String jd) {
		this.jd = jd;
	}
	public String getOlid() {
		return olid;
	}
	public void setOlid(String olid) {
		this.olid = olid;
	}
	public String getOnickname() {
		return onickname;
	}
	public void setOnickname(String onickname) {
		this.onickname = onickname;
	}
	public String getOuid() {
		return ouid;
	}
	public void setOuid(String ouid) {
		this.ouid = ouid;
	}
	public String getPj() {
		return pj;
	}
	public void setPj(String pj) {
		this.pj = pj;
	}
	public String getRelinfo() {
		return relinfo;
	}
	public void setRelinfo(String relinfo) {
		this.relinfo = relinfo;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getVideos() {
		return videos;
	}
	public void setVideos(String videos) {
		this.videos = videos;
	}
	public String getVoices() {
		return voices;
	}
	public void setVoices(String voices) {
		this.voices = voices;
	}
	public String getWd() {
		return wd;
	}
	public void setWd(String wd) {
		this.wd = wd;
	}
	public String getYd() {
		return yd;
	}
	public void setYd(String yd) {
		this.yd = yd;
	}
	public String getZf() {
		return zf;
	}
	public void setZf(String zf) {
		this.zf = zf;
	}
	public String getZfinfo() {
		return zfinfo;
	}
	public void setZfinfo(String zfinfo) {
		this.zfinfo = zfinfo;
	}
	@Override
	public String toString() {
		return "RelayDynamicTable_model [createtime=" + createtime + ", dz="
				+ dz + ", flid=" + flid + ", fnickname=" + fnickname
				+ ", fuid=" + fuid + ", id=" + id + ", imgs=" + imgs
				+ ", infos=" + infos + ", isnm=" + isnm + ", isopen=" + isopen
				+ ", jd=" + jd + ", olid=" + olid + ", onickname=" + onickname
				+ ", ouid=" + ouid + ", pj=" + pj + ", relinfo=" + relinfo
				+ ", tags=" + tags + ", type=" + type + ", uid=" + uid
				+ ", videos=" + videos + ", voices=" + voices + ", wd=" + wd
				+ ", yd=" + yd + ", zf=" + zf + ", zfinfo=" + zfinfo + "]";
	}
	
}
