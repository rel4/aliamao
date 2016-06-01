package com.aliamauri.meat.fragment.impl_grandson_ys;

import com.aliamauri.meat.fragment.BaseFragment_grandson_ys;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 第三级fragment 标签
 * 
 * @author limaokeji-windosc
 * 
 */
public class TagsPage extends BaseFragment_grandson_ys{
	
	
	@Override
	public String getUrl(int page) {
		return NetworkConfig.getTagUrl_ys();
	}

	@Override
	public int getType() {
		return GlobalConstant.TYPE_TAG;
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("TagsPage");
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		 MobclickAgent.onPageEnd("TagsPage"); 
	}

}
