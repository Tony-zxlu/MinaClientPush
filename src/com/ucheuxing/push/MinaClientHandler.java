package com.ucheuxing.push;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.ucheuxing.push.bean.LoginRequest;
import com.ucheuxing.push.bean.LoginResponse;
import com.ucheuxing.push.bean.NotifyConnect;
import com.ucheuxing.push.receiver.UUPushBaseReceiver;
import com.ucheuxing.push.util.GetPhoneStatec;
import com.ucheuxing.push.util.LogUtil;
import com.ucheuxing.push.util.NetUtils;
import com.ucheuxing.push.util.SignUtil;
public class MinaClientHandler extends IoHandlerAdapter {

	private static final String TAG = MinaClientHandler.class.getSimpleName();

	private static final String TYPE = "type";
	private static final String CODE = "code";
	private static final String OBJ = "obj";

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
		if (message == null)
			return;
		if (!(message instanceof String))
			return;
		String jsonStr = (String) message;
		int code = getCodeFromJson(jsonStr);
		Intent intent = new Intent(UUPushBaseReceiver.UCHEUXING_PUSH_ACTION);
		if (code != 0) {// 数据错误
			intent.putExtra(TYPE, BusinessType.RET_DATA_ERROR);
			mContext.sendBroadcast(intent);
			return;
		}

		// 数据OK
		BusinessType type = getTypeFromJson(jsonStr);
		intent.putExtra(TYPE, type);
		if (type == null)
			// TODO: throw an exception
			return;
		switch (type) {
		case LOGIN:
			LoginResponse loginResponse = gson.fromJson(jsonStr, LoginResponse.class);
			intent.putExtra(OBJ, loginResponse);
			break;
		case CONNECT:
			NotifyConnect notifyConnect = gson.fromJson(jsonStr,
					NotifyConnect.class);
			intent.putExtra(OBJ, notifyConnect);
			sendLoginRequest(session, notifyConnect);
			break;

		default:
			break;
		}

		mContext.sendBroadcast(intent);
		log.d("messageReceived : " + jsonStr.toString());
	}

	private void sendLoginRequest(IoSession session, NotifyConnect notifyConnect) {
		SignUtil netUtils = new SignUtil(mContext);
		LoginRequest loginRequest = new LoginRequest("login",
				netUtils.getSign(), GetPhoneStatec.getClientType(),
				GetPhoneStatec.getVersionName(mContext),
				notifyConnect.client_id, "userid01");
		String logingRequestStr = gson.toJson(loginRequest);
		if (session != null && session.isConnected()) {
			session.write(logingRequestStr);
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

	private int getCodeFromJson(String jsonStr) {
		int code = -1;
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			code = jsonObject.getInt(CODE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}

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

	public enum BusinessType {
		CONNECT, LOGIN, RE_LOGIN, RET_DATA_ERROR
	}

}