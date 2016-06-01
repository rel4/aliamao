package com.aliamauri.meat.utils;

import java.util.Map;
import java.util.Map.Entry;

public class MapUtils {
	/**
	 * 获取第一值
	 * 
	 * @param map
	 * @return
	 */
	public static <K, V> V getFirstOrValue(Map<K, V> map) {
		V obj = null;
		for (Entry<K, V> entry : map.entrySet()) {
			obj = entry.getValue();
			if (obj != null) {
				break;
			}
		}
		return obj;
	}

	/**
	 * 获取第一KEY
	 * 
	 * @param map
	 * @return
	 */
	public static <K, V> K getFirstOrKey(Map<K, V> map) {
		K obj = null;
		for (Entry<K, V> entry : map.entrySet()) {
			obj = entry.getKey();
			if (obj != null) {
				break;
			}
		}
		return obj;
	}

	/**
	 * 获取最后KEY
	 * 
	 * @param map
	 * @return
	 */
	public static <K, V> K getLastOrKey(Map<K, V> map) {
		K obj = null;
		
		int count=0;
		for (Entry<K, V> entry : map.entrySet()) {
			obj = entry.getKey();
			
			if (obj != null&&count==map.size()-1) {
				break;
			}
			count++;
		}
		return obj;
	}

}
