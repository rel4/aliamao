package com.aliamauri.meat.fragment.impl_grandson_dt;

import com.aliamauri.meat.fragment.BaseFragment_grandson_dt;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 第三级fragment 朋友圈
 * 
 * @author limaokeji-windosc
 * 
 */
public class FriendsPage extends BaseFragment_grandson_dt {
	
	@Override
	public String getUrl(String action,String curid) {
		return NetworkConfig.getDynamicUrl_friend(action, curid);
	}

	@Override
	public int getType() {
		
		return GlobalConstant.TYPE_PYQ;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("FriendsPage");
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		 MobclickAgent.onPageEnd("FriendsPage"); 
	}


}
