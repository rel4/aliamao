package com.aliamauri.meat.fragment.impl_grandson_dt;

import com.aliamauri.meat.fragment.BaseFragment_grandson_dt;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 第三级fragment 最热动态
 * 
 * @author limaokeji-windosc
 * 
 */
public class HotDynamicPage extends BaseFragment_grandson_dt {

	@Override
	public String getUrl(String action,String curid) {
		return NetworkConfig.getDynamicUrl_hot();
	}

	@Override
	public int getType() {
		return GlobalConstant.TYPE_ZRDT;
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("HotDynamicPage");
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		 MobclickAgent.onPageEnd("HotDynamicPage"); 
	}

}
