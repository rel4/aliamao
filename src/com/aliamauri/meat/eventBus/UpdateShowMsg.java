package com.aliamauri.meat.eventBus;
/**
 * 设置动态页面上的 转发，评价，点赞 ，阅读 的显示数字
 */
public class UpdateShowMsg {
	private String zf;  //转发
	private String pj;  //评价
	private String dz;  //点赞
	private String yd;  //阅读
	private String id;  //该条动态的id
	private String mTag;  //当前action的tag
	private String mData;
	

	/**
	 * 设置参数
	 * @param id  该条动态的id
	 * @param zf  转发
	 * @param pj  评价
	 * @param dz  点赞
	 * @param yd  阅读
	 */
	public UpdateShowMsg(String id,String zf,String pj,String dz,String yd){
		this.zf = zf;
		this.pj = pj;
		this.dz = dz;
		this.yd = yd;
		this.id = id;
		
	}
	
	public UpdateShowMsg(String tag){
		this.mTag = tag;
		
	}
	public String getmTag() {
		return mTag;
	}
	
	public String getZf() {
		return zf;
	}

	public String getPj() {
		return pj;
	}

	public String getDz() {
		return dz;
	}

	public String getYd() {
		return yd;
	}

	public String getId() {
		return id;
	}
	public String getmData() {
		return mData;
	}

	public UpdateShowMsg setmData(String mData) {
		this.mData = mData;
		return this;
	}

	public UpdateShowMsg setId(String id) {
		this.id = id;
		return this;
	}

}
