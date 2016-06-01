package com.aliamauri.meat.connetJS;

import android.content.Intent;
import android.os.Message;
import android.webkit.JavascriptInterface;

import com.aliamauri.meat.activity.SelectPicPopupWindow;
import com.aliamauri.meat.activity.WebViewActivity;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.view.ShowAlertDialog;

/**
 * 自定义的Android代码和JavaScript代码之间的桥梁类
 * 
 * @author 1
 * 
 */
public class WebAppInterface {

	public WebAppInterface() {
	}

	/** Show a toast from the web page */
	@JavascriptInterface
	public void gotoWebView(String url) {

	}
}