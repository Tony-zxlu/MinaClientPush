package com.ucheuxing.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.ucheuxing.push.MinaClientHandler.BusinessType;
import com.ucheuxing.push.bean.LoginResponse;
import com.ucheuxing.push.bean.NotifyConnect;
import com.ucheuxing.push.util.ToastUtils;

public abstract class UUPushBaseReceiver extends BroadcastReceiver {

	public static final String UCHEUXING_PUSH_ACTION = "com.ucheuxing.action.push";
	public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	/**
	 * loging success
	 * 
	 * @author Tony DateTime 2015-4-23 下午4:31:42
	 * @param mContext
	 *            上下文
	 * @param loginParam
	 *            登录成功的返回参数
	 */
	public abstract void onLoginSuccess(Context mContext, LoginResponse loginParam);
	
	public abstract void onInitSuccess(Context mContext, NotifyConnect notifyConnect);

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.equals(action, UCHEUXING_PUSH_ACTION)) {// we need the
																// action
			BusinessType type = (BusinessType) intent
					.getSerializableExtra("type");

			switch (type) {
			case LOGIN:
				LoginResponse loginParam = (LoginResponse) intent
						.getSerializableExtra("obj");
				onLoginSuccess(context, loginParam);
				break;
			case CONNECT:
				NotifyConnect notifyConnect = (NotifyConnect) intent
				.getSerializableExtra("obj");
				onInitSuccess(context, notifyConnect);
				break;

			default:
				break;
			}
		} else if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileInfo = manager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiInfo = manager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo activeInfo = manager.getActiveNetworkInfo();

			ToastUtils.showShort(
					context,
					"mobile:"
							+ mobileInfo.isConnected()
							+ "\n"
							+ "wifi:"
							+ wifiInfo.isConnected()
							+ "\n"
							+ "active:"
							+ (activeInfo == null ? "NULL" : activeInfo
									.getType()));
		}
	}
}
