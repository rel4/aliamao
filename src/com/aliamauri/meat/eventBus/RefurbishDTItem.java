package com.aliamauri.meat.eventBus;
/**
 * 动态详情页点击删除按钮后，通知动态列表页面重新请求网络
 * @author limaokeji-windosc
 *
 */
public class RefurbishDTItem {
	private String id;   //动态id/该用户的id
	private String tag;  //当前传入的操作标记
	
	public RefurbishDTItem(String id,String tag) {
		this.id = id;
		this.tag = tag;
	}
	public String getTag() {
		return tag;
	}
	public String getId() {
		return id;
	}
}
