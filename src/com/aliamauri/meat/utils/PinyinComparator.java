package com.aliamauri.meat.utils;

import java.util.Comparator;

import com.aliamauri.meat.bean.Cont;

/**
 * 
 * @author xiaanming
 * 
 */
public class PinyinComparator implements Comparator<Cont> {

	public int compare(Cont o1, Cont o2) {
		if (o1.title.equals("@") || o2.title.equals("#")) {
			return -1;
		} else if (o1.title.equals("#") || o2.title.equals("@")) {
			return 1;
		} else {
			return o1.title.compareTo(o2.title);
		}
	}

}
