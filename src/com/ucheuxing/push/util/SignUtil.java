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
public class SignUtil {
	private Context context;

	public SignUtil(Context context) {
		this.context = context;
	}

	public String getSign() {
		List<String> list = new ArrayList<String>();
		list.add(Contansts.SALT_FIGURE);
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
