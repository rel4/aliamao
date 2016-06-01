package com.aliamauri.meat.db.Dynamic_db.model;

import java.io.Serializable;


/**
 * 动态主表读写数据库的模型
 * 
 * @author limaokeji-windosc
 * 
 */
	public class HomeTable_model implements Serializable{
	private static final long serialVersionUID = 4190980745516616067L;
		private String createtime;
		private String distance;
		private String update_type;
		private String dz;
		private String face;
		private String LocalFace;
		private String iszan;
		private String id;
		private String imgs;
		private String infos;
		private String isnm;
		private String isopen;
		private String jd;
		private String nickname;
		private String pj;
		private String relinfo;
		private String tags;
		private String mmtype;
		private String type;
		private String uid;
		private String videos;
		private String voices;
		private String wd;
		private String yd;
		private String zf;
		private String zfinfo;
		private String zfinfox;
		public String getUpdate_type() {
			return update_type;
		}

		public void setUpdate_type(String update_type) {
			this.update_type = update_type;
		}
		public String getLocalFace() {
			return LocalFace;
		}

		public void setLocalFace(String localFace) {
			LocalFace = localFace;
		}
		public String getCreatetime() {
			return createtime;
		}

		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}

		public String getDistance() {
			return distance;
		}

		public void setDistance(String distance) {
			this.distance = distance;
		}

		public String getDz() {
			return dz;
		}

		public void setDz(String dz) {
			this.dz = dz;
		}

		public String getFace() {
			return face;
		}

		public void setFace(String face) {
			this.face = face;
		}

		public String getIszan() {
			return iszan;
		}

		public void setIszan(String iszan) {
			this.iszan = iszan;
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

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
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

		public String getMmtype() {
			return mmtype;
		}

		public void setMmtype(String mmtype) {
			this.mmtype = mmtype;
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

		public String getZfinfox() {
			return zfinfox;
		}

		public void setZfinfox(String zfinfox) {
			this.zfinfox = zfinfox;
		}

		@Override
		public String toString() {
			return "HomeTable_model [createtime=" + createtime + ", distance="
					+ distance + ", dz=" + dz + ", face=" + face
					+ ", LocalFace=" + LocalFace + ", iszan=" + iszan + ", id="
					+ id + ", imgs=" + imgs + ", infos=" + infos + ", isnm="
					+ isnm + ", isopen=" + isopen + ", jd=" + jd
					+ ", nickname=" + nickname + ", pj=" + pj + ", relinfo="
					+ relinfo + ", tags=" + tags + ", mmtype=" + mmtype
					+ ", type=" + type + ", uid=" + uid + ", videos=" + videos
					+ ", voices=" + voices + ", wd=" + wd + ", yd=" + yd
					+ ", zf=" + zf + ", zfinfo=" + zfinfo + ", zfinfox="
					+ zfinfox + "]";
		}

	}
