package com.ucheuxing.push;

import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.ucheuxing.push.bean.HeartBeatFeedBack;
import com.ucheuxing.push.bean.InitConnect;
import com.ucheuxing.push.bean.LoginRequest;
import com.ucheuxing.push.bean.LoginResponse;
import com.ucheuxing.push.bean.PayNotify;
import com.ucheuxing.push.receiver.UUPushBaseReceiver;
import com.ucheuxing.push.util.GetPhoneStatec;
import com.ucheuxing.push.util.LogUtil;
import com.ucheuxing.push.util.SignUtil;

public class MinaClientHandler extends IoHandlerAdapter {

	private static final String TAG = MinaClientHandler.class.getSimpleName();

	public static final String TYPE = "type";
	public static final String DATA = "data";

	private Gson gson;

	private LogUtil log;

	private Context mContext;

	public MinaClientHandler(Context mContext) {
		super();
		log = new LogUtil(TAG);
		gson = new Gson();
		this.mContext = mContext;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.d("exceptionCaught");
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		log.d("============start=============="+(message == null ? "NULL":message.toString()));
		if (message == null)
			return;
		if (!(message instanceof String))
			return;
		String jsonStr = (String) message;
		log.d(jsonStr);
		// 过滤类型
		Intent intent = new Intent(UUPushBaseReceiver.UCHEUXING_PUSH_ACTION);
		BusinessType type = getTypeFromJson(jsonStr);
		
		if (type == null) {
			throw new IllegalArgumentException(
					" The msg type must be BusinessType ");
		}

		intent.putExtra(TYPE, type);
		// 过滤信息是否正确
//		int code = getCodeFromJson(jsonStr);
//		if (code != 0) {// 数据错误
//			intent.putExtra(TYPE, BusinessType.RET_DATA_ERROR);
//			mContext.sendBroadcast(intent);
//			return;
//		}

		switch (type) {
		case CONNECT://连接初始化
			InitConnect initConnect = gson.fromJson(jsonStr, InitConnect.class);
			intent.putExtra(DATA, initConnect);
			sendLoginRequest(session, initConnect);
			break;
			
		case LOGIN://登录反馈
			LoginResponse loginResponse = gson.fromJson(jsonStr,
					LoginResponse.class);
			intent.putExtra(DATA, loginResponse);
			break;
			
		case PING:// 服务端测试联通性
			sendPingFeedBack(session);
			break;
			
		case PAY:// 付款的通知
			PayNotify payNotify = gson.fromJson(jsonStr, PayNotify.class);
			intent.putExtra(DATA, payNotify);
			sendPayFeedBack(session,payNotify);
			break;

		default:
			break;
		}

		mContext.sendBroadcast(intent);
		log.d("messageReceived : " + jsonStr.toString());
	}

	//
	private void sendPayFeedBack(IoSession session, PayNotify paymentNotify) {
		
	}

	private void sendPingFeedBack(IoSession session) {
		if (session != null && session.isConnected()) {
			HeartBeatFeedBack heartBeatFeedBack = new HeartBeatFeedBack("pong");
			String jsonStr = gson.toJson(heartBeatFeedBack);
			session.write(jsonStr);
		}
	}

	private void sendLoginRequest(IoSession session, InitConnect initConnect) {
		SignUtil signUtil = new SignUtil(mContext);
		LoginRequest loginRequest = new LoginRequest("login",
				signUtil.getSign(), GetPhoneStatec.getClientType(),
				GetPhoneStatec.getVersionName(mContext), initConnect.client_id,
				"userid01");
		String logingRequestStr = gson.toJson(loginRequest);
		if (session != null && session.isConnected()) {
			WriteFuture write = session.write(logingRequestStr);
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// ToastUtils.showShort(mContext, "messageSent : " +
		// message.toString());
		log.d("messageSent : " + message.toString());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// ToastUtils.showShort(mContext, "sessionClosed");
		log.d("sessionClosed");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		log.d("sessionCreated");
		// ToastUtils.showShort(mContext, "sessionCreated");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		// ToastUtils.showShort(mContext, "sessionIdle");
		log.d("sessionIdle");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("sessionOpened");
		log.d("sessionOpened");
	}

	// /////////////////////////////////////////////

	@SuppressLint("DefaultLocale")
	private BusinessType getTypeFromJson(String jsonStr) {
		BusinessType type = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			String typeStr = jsonObject.getString(TYPE).toUpperCase();
			log.d(" typeStr : " + typeStr);
			type = BusinessType.valueOf(typeStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return type;
	}

	/**
	 * CONNECT:连接初始化后，服务端分配clientid LOGIN：登录请求，携带md5值和userid、clientid
	 * PING：服务端的连接检测 PAY:付款成功通知
	 * 
	 * @author Tony DateTime 2015-4-24 下午1:01:34
	 * @version 1.0
	 */
	public enum BusinessType {
		CONNECT, LOGIN, RE_LOGIN, RET_DATA_ERROR, PING, PAY
	}

}