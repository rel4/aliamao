package com.aliamauri.meat.eventBus;
/**
 * 用于下载，大图或者语音完成时候的指令
 * @author limaokeji-windosc
 *
 */
public class sendDownLoadedOrder {

	private int tag;    //表的类型
	private String id;  //表中条目的id
	private String type; //代表哪个类中调用了他

	public sendDownLoadedOrder(int tag, String id,String type) {
		this.tag = tag;
		this.id = id;
		this.type = type;
	}

	public int getTag() {
		return tag;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}
	

}
