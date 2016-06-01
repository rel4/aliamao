package com.aliamauri.meat.db.dlapp;

public class FindInfo {
	private long id;
	private String appid;
	private String downloadurl;
	private String appname;
	private String apppackage;
	private int state;// 0未下载，，1正在下载，2下载完成
	private String localurl;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getDownloadurl() {
		return downloadurl;
	}

	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getApppackage() {
		return apppackage;
	}

	public void setApppackage(String apppackage) {
		this.apppackage = apppackage;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getLocalurl() {
		return localurl;
	}

	public void setLocalurl(String localurl) {
		this.localurl = localurl;
	}

}
