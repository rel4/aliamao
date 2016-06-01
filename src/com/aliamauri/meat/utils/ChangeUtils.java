package com.aliamauri.meat.utils;

public class ChangeUtils {
	public static String ChangeNumberToSex(String number) {
		if ("0".equals(number)) {
			return "女";
		} else if ("1".equals(number)) {
			return "男";
		} else if ("2".equals(number)) {
			return "不限";
		}
		return "未知";
	}

	public static String ChangeSexToNumber(String sex) {
		if ("女".equals(sex)) {
			return "0";
		} else if ("男".equals(sex)) {
			return "1";
		} else if ("不限".equals(sex)) {
			return "2";
		}
		return "1000";
	}
}
