package com.ucheuxing.push.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.ucheuxing.push.PushActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class Utils {

	private static final String TAG = Utils.class.getSimpleName();

	public static boolean isInOurUserInterface(Context mContext) {
		boolean flag = false;
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
		if (runningTasks != null && runningTasks.size() > 0) {
			String topClassName = runningTasks.get(0).topActivity
					.getClassName();
			String targetClassName = PushActivity.class.getName();
			Log.d(TAG, " topClassName : " + topClassName
					+ " targetClassName :　" + targetClassName);
			if (TextUtils.equals(topClassName, targetClassName)) {
				flag = true;
			}
		}

		return flag;
	}

	/**
	 * 判断一个服务是否正在运行中
	 * 
	 * @author Tony DateTime 2015-4-25 上午11:02:21
	 * @param mContext
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am.getRunningServices(50);
		if (runningServices != null && runningServices.size() > 0) {
			for (RunningServiceInfo serviceInfo : runningServices) {
				if (serviceInfo.service.getClassName().equals(className)) {
					Log.d(TAG, " serviceInfo.service.getClassName() : "
							+ serviceInfo.service.getClassName()
							+ " targetClassName : " + className);
					isRunning = true;
					break;
				}
			}
		}
		return isRunning;
	}

	/**
	 * 网络是否连接
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
	
	
	/**
	 * 获取版本信息
	 * 
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 转换MD5
	 * 
	 * @param list
	 * @return
	 */
	public static String getMd5(List<String> list) {
		String[] strs = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			strs[i] = toLower(list.get(i));
		}
		Arrays.sort(strs);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			sb.append(strs[i]);
		}
		return Md5Utils.encode(sb.toString());
	}

	/**
	 * 获取 ClientType
	 * 
	 * @return
	 */
	public static String getClientType() {
		String mtyb = android.os.Build.BRAND;// 手机品牌
		String mtype = android.os.Build.MODEL; // 手机型号
		String str = doTrim(mtyb + mtype);
		return str;
	}

	/**
	 * 获取IMEI
	 * 
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String str = doTrim(tm.getDeviceId());
		return str;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		String str = doTrim(df.format(new Date()));
		return str;
	}

	/**
	 * 大写转小写
	 * 
	 * @param str
	 * @return
	 */
	public static String toLower(String str) {
		str = doTrim(str);
		return str.toLowerCase();
	}

	private static String doTrim(String str) {
		str = str.trim();
		str = str.replace(" ", "");
		return str;
	}
}
