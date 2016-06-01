package com.aliamauri.meat.utils;

import java.util.Comparator;

import com.aliamauri.meat.bean.cont.TelFriend.TelFriends;

/**
 * 
 * @author xiaanming
 * 
 */
public class PinyinComparatorTelFriend implements Comparator<TelFriends> {

	@Override
	public int compare(TelFriends o1, TelFriends o2) {
		if (o1 == null || o2 == null || o1.title == null || o2.title == null) {
			return -1;
		} else if ("@".equals(o1.title) || "#".equals(o2.title)) {
			return -1;
		} else if ("#".equals(o1.title) || "@".equals(o2.title)) {
			return 1;
		} else {
			return o1.title.compareTo(o2.title);
		}
	}

}
