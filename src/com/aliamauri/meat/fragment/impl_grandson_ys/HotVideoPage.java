package com.aliamauri.meat.fragment.impl_grandson_ys;

import com.aliamauri.meat.fragment.BaseFragment_grandson_ys;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 第三级fragment 最热影视
 * 
 * @author limaokeji-windosc
 * 
 */

public class HotVideoPage extends BaseFragment_grandson_ys {

	
	private static final String  HOTDYNAMIC_TAG = "hot";   //最热动态url标志
	
	@Override
	public String getUrl(int page) {
		return NetworkConfig.getVideoUrl(HOTDYNAMIC_TAG, page);
	}

	@Override
	public int getType() {
		return GlobalConstant.TYPE_ZRYS;
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("HotVideoPage");
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		 MobclickAgent.onPageEnd("HotVideoPage"); 
	}


}
