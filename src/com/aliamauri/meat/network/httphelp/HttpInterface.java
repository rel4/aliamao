package com.aliamauri.meat.network.httphelp;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

public interface HttpInterface {
	public interface MyRequestCallBack<T> {
		void onSucceed(T bean);
	}

	public interface LoadRequestCallBack<T> {
		/**
		 * 文件下载成功
		 * 
		 * @param file
		 */
		void onSucceed(ResponseInfo<T> t);

		/**
		 * 文件下载失败
		 * 
		 * @param arg1
		 * @param arg0
		 */
		void onFailure(HttpException arg0, String arg1);

		/**
		 * 文件下载过程
		 */
		void onLoading(long total, long current, boolean isUploading);
	}
}
