package com.aliamauri.meat.fragment.impl_grandson_ys;

import com.aliamauri.meat.fragment.BaseFragment_grandson_ys;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 第三级fragment 最新影视
 * 
 * @author limaokeji-windosc
 * 
 */
public class NewVideoPage extends BaseFragment_grandson_ys{

	

	private static final String  NEWDYNAMIC_TAG = "new";   //最新影视url标志

	@Override
	public String getUrl(int page) {
		return NetworkConfig.getVideoUrl(NEWDYNAMIC_TAG, page);
	}

	@Override
	public int getType() {
		return GlobalConstant.TYPE_ZXYS;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("NewVideoPage");
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		 MobclickAgent.onPageEnd("NewVideoPage"); 
	}


}
