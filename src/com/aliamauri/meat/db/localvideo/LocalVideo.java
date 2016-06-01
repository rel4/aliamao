package com.aliamauri.meat.db.localvideo;

import java.io.Serializable;

public class LocalVideo implements Serializable {
	/**
  * 
  */
	private static final long serialVersionUID = -7920222595800367956L;
	public long id;
	public int videoId;
	public String title;
	public String album;
	public String artist;
	public String displayName;
	public String mimeType;
	public String path;
	public String imgPath;
	public long size;
	public String duration;

	/**
    * 
    */
	public LocalVideo() {
		super();
	}

	/**
	 * @param id
	 * @param title
	 * @param album
	 * @param artist
	 * @param displayName
	 * @param mimeType
	 * @param data
	 * @param size
	 * @param duration
	 */
	public LocalVideo(int videoId, String title, String album, String artist,
			String displayName, String mimeType, String path, long size,
			String duration) {
		super();
		this.videoId = videoId;
		this.title = title;
		this.album = album;
		this.artist = artist;
		this.displayName = displayName;
		this.mimeType = mimeType;
		this.path = path;
		this.size = size;
		this.duration = duration;
	}

}