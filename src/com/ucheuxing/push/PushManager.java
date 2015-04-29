package com.ucheuxing.push;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ucheuxing.push.bean.BaseBean;
import com.ucheuxing.push.bean.HeartBeat;
import com.ucheuxing.push.bean.InitConnect;
import com.ucheuxing.push.bean.LoginRequest;
import com.ucheuxing.push.bean.LoginResponse;
import com.ucheuxing.push.bean.PayCodeNotify;
import com.ucheuxing.push.receiver.UUPushBaseReceiver;
import com.ucheuxing.push.util.Constants;
import com.ucheuxing.push.util.SharedPreferUtils;
import com.ucheuxing.push.util.SignUtil;
import com.ucheuxing.push.util.ToastUtils;
import com.ucheuxing.push.util.Utils;

/**
 * 
 * @author Tony DateTime 2015-4-24 上午9:27:14
 * @version 1.0
 */
public class PushManager {

	private static final String TAG = PushManager.class.getSimpleName();

	private PushService pushService;
	private NioSocketConnector connector;
	private IoSession ioSession;
	private ReceiveDataHandler receiveDataHandler;
	private static String heartBeatJson;
	private static String heartBeatFeedBackJson;
	private Gson gson;
	private int connectTime, reConnectTime;
	private boolean isConnecting = false;

	public PushManager(PushService pushService) {
		super();
		this.pushService = pushService;
		gson = new Gson();
		receiveDataHandler = new ReceiveDataHandler(pushService);
		heartBeatJson = gson.toJson(new HeartBeat("ping"));
		heartBeatFeedBackJson = gson.toJson(new HeartBeat("pong"));
		Log.d(TAG, " heartBeatJson : " + heartBeatJson
				+ " heartBeatFeedBackJson : " + heartBeatFeedBackJson);
	}

	public void connect() {
		doConnect();
	}

	public boolean sessionIsConnected() {
		return (ioSession != null) && ioSession.isConnected();
	}

	public void sendPingMsg() {
		if (sessionIsConnected()) {
			Log.d(TAG, " send ping msg ");
			ioSession.write(heartBeatJson);
		}
	}

	private void doConnect() {
		if (isConnecting) {
			Log.i(TAG, " doConnect isConnecting exit ");
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					isConnecting = true;
					Log.i(TAG, "建立connect对象 ");
					// 1.建立connect对象
					connector = new NioSocketConnector();
					// 2.为connector设置handler
					connector.setHandler(receiveDataHandler);
					// 3.为connector设置filter
					connector.getFilterChain()
							.addLast(
									"codec",
									new ProtocolCodecFilter(
											new TextLineCodecFactory()));

					connector.getFilterChain().addFirst("reconnection",
							new IoFilterAdapter() {

								@Override
								public void sessionClosed(
										NextFilter nextFilter, IoSession session)
										throws Exception {
									reConnection();
								}

							});
					// connector.getSessionConfig().setIdleTime(
					// IdleStatus.WRITER_IDLE, 20);
					connector.setConnectTimeoutMillis(5000);

					String host = SharedPreferUtils.getString(pushService,
							Constants.SOCKET_HOST_NAME, "");
					int port = SharedPreferUtils.getInt(pushService,
							Constants.SOCKET_PORT, -1);
					if (TextUtils.isEmpty(host) || port == -1) {
						ToastUtils.showShort(pushService, "请配置好服务IP和端口！");
						return;
					}
					InetSocketAddress inetSocketAddress = new InetSocketAddress(
							host, port);
					connector.setDefaultRemoteAddress(inetSocketAddress);
					connectTime = 0;
					for (;;) {
						try {

							if (sessionIsConnected()) {
								Log.d(TAG, "连接服务器---已经登录。。。。 退出");
								break;
							}

							if (!Utils.isNetworkConnected(pushService)) {
								Log.d(TAG, "连接服务器---重新连接服器， 无网络。。。。 退出");
								return;
							}

							if (connector != null && connector.isDisposed()) {
								Log.d(TAG, "连接服务器---connector已经销毁了。。。。 退出");
								break;
							}
							ConnectFuture future = connector.connect();
							// 等待连接创建成功
							future.awaitUninterruptibly();
							// 获取会话
							ioSession = future.getSession();
							Log.d(TAG,
									"连接服务端"
											+ host
											+ ":"
											+ port
											+ "[成功]"
											+ ",,时间:"
											+ new SimpleDateFormat(
													"yyyy-MM-dd HH:mm:ss")
													.format(new Date()));
							break;
						} catch (RuntimeIoException e) {
							Log.d(TAG,
									"连接服务端"
											+ host
											+ ":"
											+ port
											+ "失败"
											+ ",,时间:"
											+ new SimpleDateFormat(
													"yyyy-MM-dd HH:mm:ss")
													.format(new Date())
											+ ", 连接MSG异常,请检查MSG端口、IP是否正确,MSG服务是否启动,异常内容:"
											+ e.getMessage(), e);
							try {
								int waiting = waiting(connectTime++);
								Log.d(TAG, " reTry to connect server in "
										+ waiting + " s "
										+ " current connectTime : "
										+ connectTime);
								Thread.sleep(1000 * waiting);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}// 连接失败后,重连10次,间隔30s
						}
					}
				} finally {
					isConnecting = false;
				}
			}
		}).start();
	}

	// //////////////////////////

	private void reConnection() {
		reConnectTime = 0;
		for (;;) {
			try {
				int waiting = waiting(reConnectTime++);
				Log.d(TAG, " reConnect server in " + waiting
						+ " s  , current reConnectTime : " + reConnectTime);
				Thread.sleep(1000 * waiting);
				if (!Utils.isNetworkConnected(pushService)) {
					Log.d(TAG, "reConnection 无网络。。。。 退出");
					break;
				}

				if (sessionIsConnected()) {
					Log.d(TAG, "已经登录。。。。 退出");
					break;
				}

				if (connector != null && connector.isDisposed()) {
					Log.d(TAG, "connector已经销毁了。。。。 退出");
					break;
				}
				ConnectFuture future = connector.connect();
				future.awaitUninterruptibly();
				ioSession = future.getSession();
				if (ioSession.isConnected()) {
					Log.d(TAG, "断线重连["
							+ connector.getDefaultRemoteAddress().getHostName()
							+ ":"
							+ connector.getDefaultRemoteAddress().getPort()
							+ "]成功");
					break;
				} else {
					Log.d(TAG, "断线重连失败");
				}

			} catch (Exception e) {
				// e.printStackTrace();
				Log.d(TAG, "重连服务器登录失败 :" + e.getMessage());
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////

	public void disConnect() {
		if (ioSession != null && ioSession.isConnected()) {
			ioSession.close(false);
		}
		if (connector != null && !connector.isDisposed()) {
			connector.dispose();
		}
	}

	// ////////////////////////////////////////////////////////////////////
	/**
	 * According to the retryTime set the waiting time
	 * 
	 * @param retryTime
	 * @return
	 */
	private int waiting(int retryTime) {
		if (retryTime > 20) {
			return 600;
		}
		if (retryTime > 13) {
			return 300;
		}
		return retryTime <= 7 ? 10 : 60;
	}

	// ///////////////////////////////////////////////////////////
	public static final String TYPE = "type";
	public static final String DATA = "data";

	public class ReceiveDataHandler extends IoHandlerAdapter {

		private PushService pushService;

		public ReceiveDataHandler(PushService pushService) {
			super();
			this.pushService = pushService;
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			Log.d(TAG, "exceptionCaught" + cause.getMessage());
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			Log.d(TAG, "============start=============="
					+ (message == null ? "NULL" : message.toString()));
			if (message == null)
				return;
			if (!(message instanceof String))
				return;
			String jsonStr = (String) message;
			Log.d(TAG, jsonStr);
			// 过滤类型
			Intent intent = new Intent(UUPushBaseReceiver.UCHEUXING_PUSH_ACTION);
			BusinessType type = getTypeFromJson(jsonStr);

			if (type == null) {
				throw new IllegalArgumentException(
						" The msg type must be BusinessType ");
			}

			intent.putExtra(TYPE, type);

			switch (type) {
			case CONNECT:// 连接初始化
				InitConnect initConnect = gson.fromJson(jsonStr,
						InitConnect.class);
				intent.putExtra(DATA, initConnect);
				sendLoginRequest(session, initConnect);
				break;

			case LOGIN:// 登录反馈
				LoginResponse loginResponse = gson.fromJson(jsonStr,
						LoginResponse.class);
				intent.putExtra(DATA, loginResponse);
				if (loginResponse.code == BaseBean.CODE_OK) {
					Log.d(TAG, " login success ");
				}
				break;

			case PING:// 服务端测试联通性
				sendPingFeedBack(session);
				break;

			case PAY:// 付款通知
			case CODE:// 扫码通知
				PayCodeNotify payCodeNotify = gson.fromJson(jsonStr,
						PayCodeNotify.class);
				intent.putExtra(DATA, payCodeNotify);
				sendPayFeedBack(session, payCodeNotify);
				break;

			default:
				break;
			}

			pushService.sendBroadcast(intent);
			Log.d(TAG, "messageReceived : " + jsonStr.toString());
		}

		//
		private void sendPayFeedBack(IoSession session,
				PayCodeNotify paymentNotify) {

		}

		private void sendPingFeedBack(IoSession session) {
			if (session != null && session.isConnected()) {
				session.write(heartBeatFeedBackJson);
			}
		}

		private void sendLoginRequest(IoSession session, InitConnect initConnect) {
			SignUtil signUtil = new SignUtil(pushService);
			LoginRequest loginRequest = new LoginRequest("login",
					signUtil.getSign(), Utils.getClientType(),
					Utils.getVersionName(pushService), initConnect.client_id,
					"userid01");
			String logingRequestStr = gson.toJson(loginRequest);
			if (session != null && session.isConnected()) {
				WriteFuture write = session.write(logingRequestStr);
			}
		}

		@Override
		public void messageSent(IoSession session, Object message)
				throws Exception {
			Log.d(TAG, "messageSent : " + message.toString());
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			Log.d(TAG, "sessionClosed");
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			Log.d(TAG, "sessionCreated ");
			// session.getConfig().setIdleTime(IdleStatus.WRITER_IDLE,
			// Constants.HEART_BEAT_INTERVAL);
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status)
				throws Exception {
			Log.d(TAG, "sessionIdle and send ping messgae ");
			// if (session != null) {
			// session.write(heartBeatJson);
			// }
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			Log.d(TAG, "sessionOpened sessionId ： " + session.getId());
		}

		// /////////////////////////////////////////////

		@SuppressLint("DefaultLocale")
		private BusinessType getTypeFromJson(String jsonStr) {
			BusinessType type = null;
			try {
				JSONObject jsonObject = new JSONObject(jsonStr);
				String typeStr = jsonObject.getString(TYPE).toUpperCase();
				Log.d(TAG, " typeStr : " + typeStr);
				type = BusinessType.valueOf(typeStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return type;
		}

	}

	/**
	 * CONNECT:连接初始化后，服务端分配clientid LOGIN：登录请求，携带md5值和userid、clientid
	 * PING：服务端的连接检测 PAY:付款成功通知 CODE:扫码成功通知
	 * 
	 * @author Tony DateTime 2015-4-24 下午1:01:34
	 * @version 1.0
	 */
	public enum BusinessType {
		CONNECT, LOGIN, RE_LOGIN, RET_DATA_ERROR, PING, PAY, CODE
	}
	// ///////////////////////////////////////////////////////////
}
