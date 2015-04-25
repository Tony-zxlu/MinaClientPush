package com.ucheuxing.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.ucheuxing.push.ReceiveDataHandler.BusinessType;
import com.ucheuxing.push.bean.LoginResponse;
import com.ucheuxing.push.bean.InitConnect;
import com.ucheuxing.push.bean.PayCodeNotify;
import com.ucheuxing.push.util.ToastUtils;
import static com.ucheuxing.push.ReceiveDataHandler.*;
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
	
	public abstract void onConnectSuccess(Context mContext, InitConnect notifyConnect);
	
	public abstract void onPayCodeNotify(Context mContext, PayCodeNotify payNotify);

	@Override
	public void onReceive(Context mContext, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.equals(action, UCHEUXING_PUSH_ACTION)) {// we need the
																// action
			BusinessType type = (BusinessType) intent
					.getSerializableExtra(TYPE);

			switch (type) {
			case LOGIN:
				LoginResponse loginResponse = (LoginResponse) intent
						.getSerializableExtra(DATA);
				onLoginSuccess(mContext, loginResponse);
				break;
			case CONNECT:
				InitConnect notifyConnect = (InitConnect) intent
				.getSerializableExtra(DATA);
				onConnectSuccess(mContext, notifyConnect);
				break;
				
			case PING:
				ToastUtils.showShort(mContext, "ping");
				break;
				
			case PAY:
			case CODE:
				PayCodeNotify payCodeNotify = (PayCodeNotify) intent.getSerializableExtra(DATA);
				onPayCodeNotify(mContext, payCodeNotify);
				break;

			default:
				break;
			}
		} else if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {
			ConnectivityManager manager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileInfo = manager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiInfo = manager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo activeInfo = manager.getActiveNetworkInfo();

			ToastUtils.showShort(
					mContext,
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
