package com.ucheuxing.push.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * 网络工作类
 * 
 * @author liujiandong
 * 
 * @param: <T>
 */
public class NetUtils {
	private final String y = "ucheu13@xing";
	private Context context;
	/**
	 * @param context
	 * @param url
	 *            服务器方法名 如User.register
	 * @param maps
	 *            参数 sign ct v 不需要传
	 * @param clazz
	 *            需要解析的实体
	 */
	public NetUtils(Context context) {
		this.context = context;
	}

	public String getSign() {
		List<String> list = new ArrayList<String>();
		list.add(y);
		list.add(GetPhoneStatec.getVersionName(context));
		list.add(GetPhoneStatec.getClientType());
		list.add(GetPhoneStatec.getTime());
		return GetPhoneStatec.getMd5(list);
	}

	public static String testSign() {
		List<String> list = new ArrayList<String>();
		list.add("1.0.0");
		list.add("xiaomi");
		list.add("201504");
		list.add("ucheu13@xing");
		return GetPhoneStatec.getMd5(list);
	}

}
