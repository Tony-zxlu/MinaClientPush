package com.ucheuxing.push.receiver;

import android.content.Context;

import com.ucheuxing.push.bean.InitConnect;
import com.ucheuxing.push.bean.LoginResponse;
import com.ucheuxing.push.bean.PayNotify;
import com.ucheuxing.push.util.NotifyManager;
import com.ucheuxing.push.util.ToastUtils;
import com.ucheuxing.push.util.Utils;

public class MyUUPushReceiver extends UUPushBaseReceiver {

	@Override
	public void onLoginSuccess(Context mContext, LoginResponse loginParam) {
		ToastUtils.showShort(mContext, "登录验证成功" + loginParam.toString());
	}

	@Override
	public void onConnectSuccess(Context mContext, InitConnect notifyConnect) {
		ToastUtils.showShort(mContext,
				"服务端分配clientid成功" + notifyConnect.toString());
	}

	@Override
	public void onPayNotify(Context mContext, PayNotify payNotify) {
		if (payNotify == null || payNotify.status == PayNotify.PAY_OK) {
			// TOOD:去服务器请求最新的数据，付款成功的通知
			if (Utils.isInOurUserInterface(mContext)) {
				NotifyManager.showPayDialog(mContext, "付款成功");
			} else {
				ToastUtils.showShort(mContext, "当前界面没有处于我们app的交互界面，不应该暴力弹出窗口");
				NotifyManager.showPayNotification(mContext, "XX先生，您好，MM先生付款成功，可以放行了");
			}
		} else {

		}
	}
}
