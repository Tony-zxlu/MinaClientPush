package com.ucheuxing.push.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences 工具类
 * 
 * @author Tony DateTime 2015-4-25 上午11:10:54
 * @version 1.0
 */
public class SharedPreferUtils {

	public static final String MY_PREFERENCE = "ucheuxing_pre";

	public static void setString(Context context, String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MY_PREFERENCE, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(Context context, String key,
			String defaultValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MY_PREFERENCE, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, defaultValue);
	}

	public static void setInt(Context context, String key, int value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MY_PREFERENCE, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getInt(Context context, String key, int defaultValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MY_PREFERENCE, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, defaultValue);
	}

	public static void setBoolean(Context context, String key, Boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MY_PREFERENCE, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static Boolean getBoolean(Context context, String key,
			Boolean defaultValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MY_PREFERENCE, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	public static void setLong(Context context, String key, long value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MY_PREFERENCE, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static long getLong(Context context, String key, long defaultValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MY_PREFERENCE, Context.MODE_PRIVATE);
		return sharedPreferences.getLong(key, defaultValue);
	}

	public static void setString(Context context, String key, String value,
			String preference) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(Context context, String key,
			String defaultValue, String preference) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, defaultValue);
	}

	public static void setInt(Context context, String key, int value,
			String preference) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getInt(Context context, String key, int defaultValue,
			String preference) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, defaultValue);
	}

	public static void setBoolean(Context context, String key, Boolean value,
			String preference) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static Boolean getBoolean(Context context, String key,
			Boolean defaultValue, String preference) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	public static void setLong(Context context, String key, long value,
			String preference) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static long getLong(Context context, String key, long defaultValue,
			String preference) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, Context.MODE_PRIVATE);
		return sharedPreferences.getLong(key, defaultValue);
	}

}
