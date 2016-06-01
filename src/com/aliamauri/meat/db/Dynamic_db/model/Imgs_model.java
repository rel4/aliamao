package com.aliamauri.meat.db.Dynamic_db.model;

import java.io.Serializable;

/**
 * 动态图片表读写数据库的模型
 * @author limaokeji-windosc
 *
 */
public class Imgs_model implements Serializable {
	private static final long serialVersionUID = 2823806363701799689L;
	private String id;
	private String auto_id;
	private String img;
	private String LocalImg;
	private String imgori;
	private String LocalImgOri;
	
	public String getAuto_id() {
		return auto_id;
	}

	public void setAuto_id(String auto_id) {
		this.auto_id = auto_id;
	}
	public String getLocalImg() {
		return LocalImg;
	}

	public void setLocalImg(String localImg) {
		LocalImg = localImg;
	}

	public String getLocalImgOri() {
		return LocalImgOri;
	}

	public void setLocalImgOri(String localImgOri) {
		LocalImgOri = localImgOri;
	}

	private String info;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getImgori() {
		return imgori;
	}

	public void setImgori(String imgori) {
		this.imgori = imgori;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "Imgs_model [id=" + id + ", auto_id=" + auto_id + ", img=" + img
				+ ", LocalImg=" + LocalImg + ", imgori=" + imgori
				+ ", LocalImgOri=" + LocalImgOri + ", info=" + info + "]";
	}
	
	
}
