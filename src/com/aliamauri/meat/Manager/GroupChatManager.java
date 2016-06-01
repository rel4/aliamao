package com.aliamauri.meat.Manager;

import com.aliamauri.meat.network.httphelp.HttpHelp;

/**
 * 多人聊天管理
 * 
 * @author limaokeji-windosc
 * 
 */
public class GroupChatManager {
	private GroupChatManager() {};
	public final static GroupChatManager gcm = new GroupChatManager();
	public static GroupChatManager getInstance(){
		return gcm;
	}
	
	private HttpHelp mhh = null;
	public HttpHelp getHttpHelp(){
		if(mhh == null){
			mhh = new HttpHelp();
		}
		return mhh;
	}
	
	
	
	
}
