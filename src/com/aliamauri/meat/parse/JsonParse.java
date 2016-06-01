package com.aliamauri.meat.parse;

import org.json.JSONException;

import com.google.gson.Gson;

public class JsonParse {
	private static Gson gson;

	private static Gson getGson() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}

	public static <T> T parserJson(String json, Class<T> claz)
			throws JSONException {
		if (claz == null) {
			throw new RuntimeException("---------------->解析出问题:not find "
					+ claz);
		}

		T t = (T) getGson().fromJson(json, claz);
		return t;

	}

}
