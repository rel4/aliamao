package com.aliamauri.meat.utils;

import java.util.UUID;

public class UUIDUtils {
	/**
	 * 获取uuid
	 * 
	 * @return
	 */
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
