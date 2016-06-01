package com.aliamauri.meat.eventBus;

/**
 * 后台进行用户上传操作通过此方法进行界面更新
 * @author limaokeji-windosc
 *
 */
public class SendUploadSignal {
	
	private String size;

	public SendUploadSignal(String size){
		this.size = size;
	}
	

	public SendUploadSignal() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getTag() {
		return size;
	}
	
}
