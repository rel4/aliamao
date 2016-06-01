package com.aliamauri.meat.db;

import java.util.List;

public class TempFileUpBeanTask {
	/**
	 * 视频集
	 */
	private List<String> fileTypeVideo;
	/**
	 * 音頻集
	 */
	private List<String> fileTypeAudio;
	/**
	 * 图片集
	 */
	private List<String> fileTypeImage;
	/**
	 * 文本
	 */
	private String fileTypeText;
	/**
	 * 提交时间戳
	 */
	private String submitTime;
	public List<String> getFileTypeVideo() {
		return fileTypeVideo;
	}
	public void setFileTypeVideo(List<String> fileTypeVideo) {
		this.fileTypeVideo = fileTypeVideo;
	}
	public List<String> getFileTypeAudio() {
		return fileTypeAudio;
	}
	public void setFileTypeAudio(List<String> fileTypeAudio) {
		this.fileTypeAudio = fileTypeAudio;
	}
	public List<String> getFileTypeImage() {
		return fileTypeImage;
	}
	public void setFileTypeImage(List<String> fileTypeImage) {
		this.fileTypeImage = fileTypeImage;
	}
	public String getFileTypeText() {
		return fileTypeText;
	}
	public void setFileTypeText(String fileTypeText) {
		this.fileTypeText = fileTypeText;
	}
	public String getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}
	
	
}
