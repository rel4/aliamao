package com.aliamauri.meat.eventBus;

import android.widget.TextView;
/**
 * 是否显示人界面的筛选布局
 * @author limaokeji-windosc
 *
 */
public class IsShowRenSelectLayout {

	private TextView tv;

	public IsShowRenSelectLayout(TextView tv) {
		this.tv = tv;
	}
	
	public TextView getTv(){
		return tv;
	}

}
