package com.ucheuxing.push.receiver;

import android.content.Context;

import com.ucheuxing.push.ReceiveDataHandler.BusinessType;
import com.ucheuxing.push.bean.BaseBean;
import com.ucheuxing.push.bean.InitConnect;
import com.ucheuxing.push.bean.LoginResponse;
import com.ucheuxing.push.bean.PayCodeNotify;
import com.ucheuxing.push.util.NotifyManager;
import com.ucheuxing.push.util.ToastUtils;
import com.ucheuxing.push.util.Utils;

public class MyUUPushReceiver extends UUPushBaseReceiver {

	@Override
	public void onLoginSuccess(Context mContext, LoginResponse loginResponse) {

		if (loginResponse != null && loginResponse.code == BaseBean.CODE_OK) {
			ToastUtils.showShort(mContext, "登录验证成功" + loginResponse.toString());
		} else {
			ToastUtils.showShort(mContext, "登录验证失败:" + loginResponse.msg);
		}
	}

	@Override
	public void onConnectSuccess(Context mContext, InitConnect initConnect) {
		if (initConnect != null && initConnect.code == BaseBean.CODE_OK) {
			ToastUtils.showShort(mContext,
					"服务端分配clientid成功" + initConnect.toString());
		} else {
			ToastUtils.showShort(mContext, "服务端分配clientid失败" + initConnect.msg);
		}
	}

	@Override
	public void onPayCodeNotify(Context mContext, PayCodeNotify payCodeNotify) {
		if (payCodeNotify == null
				|| payCodeNotify.status == PayCodeNotify.PAY_OK) {
			BusinessType type = BusinessType.valueOf(payCodeNotify.type.toUpperCase());
			String msg;
			if (Utils.isInOurUserInterface(mContext)) {
				msg = (type == BusinessType.PAY) ? "付款成功" : "扫码成功";
				NotifyManager.showPayDialog(mContext, msg);
			} else {
				ToastUtils.showShort(mContext, "当前界面没有处于我们app的交互界面，不应该暴力弹出窗口");
				msg = (type == BusinessType.PAY) ? "XX先生，您好，MM先生付款成功，可以放行了"
						: "扫码成功";
				NotifyManager.showPayNotification(mContext, msg);
			}
		} else {
			ToastUtils.showShort(mContext, payCodeNotify.msg);
		}
	}
}
