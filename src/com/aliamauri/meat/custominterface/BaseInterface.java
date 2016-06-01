package com.aliamauri.meat.custominterface;

public class BaseInterface {
	public interface ICMDThreadHelperListener {
		/**
		 * 透传处理状态
		 * 
		 * @param isSucceed
		 */
		void onDisposeState(boolean isSucceed);
	}
}
