package com.ucheuxing.push.receiver;

import android.content.Context;

import com.ucheuxing.push.bean.LoginResponse;
import com.ucheuxing.push.bean.NotifyConnect;
import com.ucheuxing.push.util.ToastUtils;

public class MyUUPushReceiver extends UUPushBaseReceiver {

	@Override
	public void onLoginSuccess(Context mContext, LoginResponse loginParam) {

		ToastUtils.showShort(mContext, loginParam.toString());
	}

	@Override
	public void onInitSuccess(Context mContext, NotifyConnect notifyConnect) {
		
		ToastUtils.showShort(mContext, notifyConnect.toString());
	}

}
