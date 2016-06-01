package com.aliamauri.meat.eventBus;

public class PlayVideo_event {
	private String tag;
	private String value;

	public PlayVideo_event(String Tag,String value) {
		this.tag= Tag;
		this.value = value;
	}

	public String getTag() {
		return tag;
	}

	public String getValue() {
		return value;
	}
}
