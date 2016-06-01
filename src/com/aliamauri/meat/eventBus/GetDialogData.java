package com.aliamauri.meat.eventBus;

/**
 * 获取dialog弹窗中用户选择的数据
 * 
 * @author limaokeji-windosc
 * 
 */
public class GetDialogData {

	private int right_value;
	private int left_value;

	private String city;

	public GetDialogData(int left_value, int right_value) {
		this.left_value = left_value;
		this.right_value = right_value;
	}

	/**
	 * 获取dialog右边的数值
	 * 
	 * @return
	 */
	public int getRight_value() {
		return right_value;
	}

	/**
	 * 获取dialog左边的数值
	 * 
	 * @return
	 */
	public int getLeft_value() {
		return left_value;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
