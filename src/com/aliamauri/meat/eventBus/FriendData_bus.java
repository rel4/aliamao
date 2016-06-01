package com.aliamauri.meat.eventBus;

public class FriendData_bus {

	private String mTag;
	private String mNickName;

	public FriendData_bus(String tag,String name) {
		this.mTag = tag;
		this.mNickName = name;
	}

	public String getmTag() {
		return mTag;
	}

	public String getNikeName() {
		return mNickName;
	}
	
}
