package com.aliamauri.meat.utils;

public class StringNumUtils {
	public static double string2Num(String num) {
		if (num == null) {
			return 0.00;
		}
		return Double.parseDouble(num);
	}

	/**
	 * 得到剧集数
	 * 
	 * @param tvListNum
	 *            总剧集数
	 * @param gridPage
	 *            页面显示数量
	 * @return
	 */
	public static String[] getPageTitle(int tvAllNum, int singePageNum) {
		int pageNum = getPageNum(tvAllNum, singePageNum);
		String[] titles = new String[getPageNum(tvAllNum, singePageNum)];
		for (int i = 1; i <= pageNum; i++) {
			if ((tvAllNum - (i - 1) * singePageNum) > singePageNum) {
				titles[i - 1] = (i - 1) * singePageNum + 1 + "-" + i * singePageNum;
			} else {
				titles[i - 1] = (i - 1) * singePageNum + 1 + "-" + (tvAllNum);
			}
		}
		return titles;
	}
	/***
	 * 得到剧集页数
	 * @param tvAllNum 
	 * @param singePageNum
	 * @return
	 */
	public static int getPageNum(int tvAllNum, int singePageNum) {
		int page = 0;
		if (tvAllNum % singePageNum == 0) {
			page = tvAllNum / singePageNum;
		} else {
			page = tvAllNum / singePageNum + 1;
		}
		return page;
	}

}
