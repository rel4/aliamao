package com.aliamauri.meat.eventBus;
/**
 * 更新动态集合数据中的id
 * 		自动命名的id改成从服务器中获取的id值
 * @author limaokeji-windosc
 *
 */
public class UpdateDynamicLists {
	
	private String pastId;    //旧的id值
	private String latestId;  //新的id值

	public UpdateDynamicLists(String pastId, String latestId) {
		this.pastId = pastId;
		this.latestId = latestId;
	}
	public String getPastId() {
		return pastId;
	}

	public String getLatestId() {
		return latestId;
	}
}
