package com.aliamauri.meat.fragment.impl_grandson_dt;

import com.aliamauri.meat.fragment.BaseFragment_grandson_dt;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 第三级fragment 最新动态
 * 
 * @author limaokeji-windosc
 * 
 *         动态详情展示分为3种状态，好友，自己，匿名的状态。
 * 
 *         三种状态 DT_FRIED_TAG 好友 DT_MY_TAG 自己 DT_ANONYMOUS_TAG 匿名
 * 
 * 
 */
public class NewDynamicPage extends BaseFragment_grandson_dt {

	@Override
	public String getUrl(String action,String curid) {
		return NetworkConfig.getDynamicUrl_new(action,curid);
	}

	@Override
	public int getType() {
		return GlobalConstant.TYPE_ZXDT;
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("NewDynamicPage");
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		 MobclickAgent.onPageEnd("NewDynamicPage"); 
	}

}
