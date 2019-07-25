package com.blink.ewb.util;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {

	public static String toJson(Object obj, String[] excludeFields) {
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes arg0) {
				if (excludeFields != null && excludeFields.length != 0) {
					List<String> excludeFieldList = Arrays.asList(excludeFields);
					return excludeFieldList.contains(arg0.getName());
				}
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}
		}).create();
		return gson.toJson(obj);
	}

	public static String toJson(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	public static <T> T fromJson(String json, Class<T> classOfT) {
		Gson gson = new Gson();
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(String json, Type typeOfT) {
		Gson gson = new Gson();
		return gson.fromJson(json, typeOfT);
	}

}
