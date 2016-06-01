package com.aliamauri.meat.db.Dynamic_db.model;

import java.io.Serializable;

/**
 * 动态音频表读写数据库的模型
 * @author limaokeji-windosc
 *
 */
public class Voices_model implements Serializable{
	private static final long serialVersionUID = -1324136835942535116L;
	private String id;
	private String src;
	private String localSrc;
	private String srcbg;
	private String localSrcbg;
	private String voiceText;
	private String sc;
	private String filesize;

	public String getLocalSrc() {
		return localSrc;
	}

	public String getVoiceText() {
		return voiceText;
	}

	public void setVoiceText(String voiceText) {
		this.voiceText = voiceText;
	}

	public void setLocalSrc(String localSrc) {
		this.localSrc = localSrc;
	}

	public String getLocalSrcbg() {
		return localSrcbg;
	}

	public void setLocalSrcbg(String localSrcbg) {
		this.localSrcbg = localSrcbg;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getSrcbg() {
		return srcbg;
	}

	public void setSrcbg(String srcbg) {
		this.srcbg = srcbg;
	}

	public String getSc() {
		return sc;
	}

	public void setSc(String sc) {
		this.sc = sc;
	}

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	@Override
	public String toString() {
		return "Voices_model [id=" + id + ", src=" + src + ", localSrc="
				+ localSrc + ", srcbg=" + srcbg + ", localSrcbg=" + localSrcbg
				+ ", voiceText=" + voiceText + ", sc=" + sc + ", filesize="
				+ filesize + "]";
	}
}
