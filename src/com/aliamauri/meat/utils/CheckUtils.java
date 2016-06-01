package com.aliamauri.meat.utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.content.Context;
import android.media.AudioManager;

import com.aliamauri.meat.global.MyApplication;

public class CheckUtils {
	public AudioManager localAudioManager;

	private static CheckUtils checkUtils;

	public static CheckUtils getInstance() {
		if (checkUtils == null) {
			checkUtils = new CheckUtils();
		}
		return checkUtils;

	}

	/*
	 * 判断是否为用户名应为1-12个字符的字母，数字、中文 [\\da-zA-Z\\d+\\u4E00-\\u9FA5]{1,12}
	 */
	public boolean isNickName(String nickName) {
		if (nickName == null) {
			return false;
		} else {
			String strPattern = "[a-zA-Z\\d+\\u4e00-\\u9fa5]{1,12}";
			Pattern p = Pattern.compile(strPattern);
			Matcher m = p.matcher(nickName);
			return m.matches();
		}
	}

	/*
	 * 判断是否为密码6-16位数字、字母 [a-zA-Z\d+]{6,16} ^[A-Za-z]+$
	 */
	public boolean isPassWord(String password) {
		if (password == null) {
			return false;
		} else {
			String strPattern = "[a-zA-Z\\d+]{6,16}";
			Pattern p = Pattern.compile(strPattern);
			Matcher m = p.matcher(password);
			return m.matches();
		}
	}

	// 判断是不是纯数字
	public boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("^[0-9]+(.[0-9]*)?$");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证邮箱输入是否合法
	 * 
	 * @param strEmail
	 * @return
	 */
	public boolean isEmail(String strEmail) {
		// String strPattern =
		// "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		String strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	/**
	 * 验证是否是手机号码
	 * 
	 * @param str
	 * @return
	 */
	public boolean isMobile(String str) {
		if (str == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	// 判断图片存不存在
	public boolean isIcon(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	// 判断是否是身份证18位
	public boolean isIDCard(String str) {
		Pattern pattern = Pattern
				.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;

		}
	}

	public boolean chineseFilter(String str) throws PatternSyntaxException {
		// 判断是否有汉字 var reg=/^[\u4e00-\u9fa5]{0,}$/;
		Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public String charFilter(char str) throws PatternSyntaxException {
		// 只允许字母、数字和汉字
		String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
		Pattern p = Pattern.compile(regEx);
		String s = str + "";
		Matcher m = p.matcher(s);
		return m.replaceAll("").trim();
	}

	public boolean isTitle(String str) throws PatternSyntaxException {
		if (str == null || "".equals(str.trim())) {
			return false;
		}
		// 判断是否有汉字 var reg=/^[\u4e00-\u9fa5]{0,}$/;
		Pattern pattern = Pattern.compile("[A-Z#@]");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 得到视频的长度
	 */
	public boolean getHeadPhoneState() {
		if (localAudioManager == null) {
			localAudioManager = (AudioManager) MyApplication.getMainActivity()
					.getSystemService(Context.AUDIO_SERVICE);
		}
		return localAudioManager.isWiredHeadsetOn();
	}

}
