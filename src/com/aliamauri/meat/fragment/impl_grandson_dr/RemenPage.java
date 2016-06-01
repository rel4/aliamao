package com.aliamauri.meat.fragment.impl_grandson_dr;

import com.aliamauri.meat.fragment.BaseFragment_grandson_dr;
import com.aliamauri.meat.fragment.impl_child.DaRenPage;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.umeng.analytics.MobclickAgent;

public class RemenPage extends BaseFragment_grandson_dr {

	@Override
	public String getUrl(int page) {
		return NetworkConfig.getDr_URL(page,getType());
	}

	@Override
	public int getType() {
		return DaRenPage.RM_TAG;
	}

	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("RemenPage");
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		 MobclickAgent.onPageEnd("RemenPage"); 
	}
}
