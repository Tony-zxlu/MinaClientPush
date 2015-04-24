package com.ucheuxing.push.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

@SuppressLint("DefaultLocale")
public class GetPhoneStatec {
<<<<<<< HEAD
=======
	private static final String PACKAGE_NAME = "com.lansejuli.ucheuxing";
>>>>>>> 17ed83ee4905812d12cc9caaf19cbe20e9ae50ae

	/**
	 * 获取版本信息
	 * 
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(
<<<<<<< HEAD
					context.getPackageName(), 0).versionName;
=======
					PACKAGE_NAME, 0).versionName;
>>>>>>> 17ed83ee4905812d12cc9caaf19cbe20e9ae50ae
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
<<<<<<< HEAD
=======

>>>>>>> 17ed83ee4905812d12cc9caaf19cbe20e9ae50ae
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
				.getSystemService(context.TELEPHONY_SERVICE);
		String str = doTrim(tm.getDeviceId());
		return str;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
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
