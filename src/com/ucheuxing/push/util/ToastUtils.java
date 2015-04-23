package com.ucheuxing.push.util;

import java.io.Serializable;

import android.content.Context;
import android.widget.Toast;

/**
 * 
 * @author Tony DateTime 2015-4-23 下午3:03:58
 * @version 1.0
 */
public class ToastUtils {
	// Toast
	private static Toast toast;

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, Serializable message) {
		if (null == toast) {
			toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		}
		if (message instanceof String) {
			toast.setText((String) message);
		} else if (message instanceof Integer) {
			toast.setText((Integer) message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, Serializable message) {
		if (null == toast) {
			toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
		}
		if (message instanceof String) {
			toast.setText((String) message);
		} else if (message instanceof Integer) {
			toast.setText((Integer) message);
		}
		toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, Serializable message, int duration) {
		if (null == toast) {
			toast = Toast.makeText(context, "", duration);
		}
		if (message instanceof String) {
			toast.setText((String) message);
		} else if (message instanceof Integer) {
			toast.setText((Integer) message);
		}
		toast.show();
	}

	/**
	 * 取消toast，当前管理的任何toast
	 */
	public static void hideToast() {
		if (null != toast) {
			toast.cancel();
		}
	}
}
