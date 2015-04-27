package com.ucheuxing.push;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ucheuxing.push.PushService.TaskSubmitter;
import com.ucheuxing.push.PushService.TaskTracker;
import com.ucheuxing.push.bean.BaseBean;
import com.ucheuxing.push.bean.HeartBeat;
import com.ucheuxing.push.bean.InitConnect;
import com.ucheuxing.push.bean.LoginRequest;
import com.ucheuxing.push.bean.LoginResponse;
import com.ucheuxing.push.bean.PayCodeNotify;
import com.ucheuxing.push.receiver.UUPushBaseReceiver;
import com.ucheuxing.push.util.Constants;
import com.ucheuxing.push.util.GetPhoneStatec;
import com.ucheuxing.push.util.LogUtil;
import com.ucheuxing.push.util.SharedPreferUtils;
import com.ucheuxing.push.util.SignUtil;

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
	private TaskSubmitter taskSubmitter;
	private TaskTracker taskTracker;
	private List<Runnable> taskList;
	private boolean running;
	private boolean isSessionOpen;
	public boolean isAuthenticated = false;
	private Future<?> futureTask;
	private static String heartBeatJson;
	private static String heartBeatFeedBackJson;
	private Gson gson;
	private ReconnectionThread reconnection;

	public PushManager(PushService pushService) {
		super();
		this.pushService = pushService;
		gson = new Gson();
		taskSubmitter = pushService.getTaskSubmitter();
		receiveDataHandler = new ReceiveDataHandler(pushService);
		taskTracker = pushService.getTaskTracker();
		taskList = new ArrayList<Runnable>();
		reconnection = new ReconnectionThread(this);
		heartBeatJson = gson.toJson(new HeartBeat("ping"));
		heartBeatFeedBackJson = gson.toJson(new HeartBeat("pong"));
		Log.d(TAG, " heartBeatJson : " + heartBeatJson
				+ " heartBeatFeedBackJson : " + heartBeatFeedBackJson);
	}

	public void connect() {
		submitConnectTask();
	}

	private void submitConnectTask() {
		addTask(new ConnectTask());
	}

	public void startReconnectionThread() {
		synchronized (PushManager.class) {
			if (!reconnection.isAlive()) {
				reconnection.setName("Push Reconnection Thread");
				reconnection.start();
			}
		}
	}

	public void startNewReconnectionThread() {
		synchronized (PushManager.class) {
			Log.d(TAG, "restart new reconnectionThread");
			reconnection = new ReconnectionThread(this);
			reconnection.setName("Push Reconnection Thread");
			reconnection.start();
		}
	}

	private void addTask(Runnable runnable) {
		Log.d(TAG, "addTask(runnable)...");
		taskTracker.increase();
		synchronized (taskList) {
			if (taskList.isEmpty() && !running) {
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			} else {
				taskList.add(runnable);
			}
		}
		Log.d(TAG, "addTask(runnable)... done");
	}

	/**
	 * A runnable task to connect the server.
	 */
	private class ConnectTask implements Runnable {

		final PushManager pushManager;

		private ConnectTask() {
			this.pushManager = PushManager.this;
		}

		public void run() {
			Log.i(TAG, "ConnectTask.run()...");

			if (!pushManager.isActive()) {
				try {
					isAuthenticated = false;
					Log.d(TAG, " bulid socket connector ");
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
					connector.setConnectTimeoutMillis(5000);
					String address = SharedPreferUtils.getString(pushService,
							Constants.SOCKET_HOST_NAME, "");
					int port = SharedPreferUtils.getInt(pushService,
							Constants.SOCKET_PORT, -1);
					Log.d(TAG, " address : " + address.toString() + " port :"
							+ port);
					if (!TextUtils.isEmpty(address) && port != -1) {
						// 4.连接socket
						ConnectFuture future = connector
								.connect(new InetSocketAddress(address, port));
						future.awaitUninterruptibly();
						ioSession = future.getSession();
						Log.d(TAG,
								"connect successfully "
										+ (ioSession == null ? "NULL"
												: ioSession.isConnected()));
						clearTask();
					} else {
						runTask();
					}
				} catch (Exception e) {
					Log.d(TAG, " connect failed " + e.getMessage());
					startReconnectionThread();
					runTask();
				}
			} else {
				clearTask();
			}
		}
	}

	public void runTask() {
		Log.d(TAG, "runTask()...");
		synchronized (taskList) {
			running = false;
			futureTask = null;
			if (!taskList.isEmpty()) {
				Runnable runnable = (Runnable) taskList.get(0);
				taskList.remove(0);
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			}
		}
		taskTracker.decrease();
		Log.d(TAG, "runTask()...done");
	}

	private void clearTask() {
		Log.d(TAG, "clearTask()...");
		synchronized (taskList) {
			taskList.clear();
			taskTracker.clear();
		}
	}

	/**
	 * this connector is active or not
	 */
	public boolean isActive() {
		return connector != null && connector.isActive() && ioSession != null
				&& ioSession.isConnected();
	}

	public boolean isSessoinOpen() {
		return isSessionOpen;
	}

	public boolean isAuthenticated() {
		return isActive() && isAuthenticated;
	}

	public void disConnect() {
		if (ioSession != null && ioSession.isConnected()) {
			ioSession.close(false);
		}
		if (connector != null && !connector.isDisposed()) {
			connector.dispose();
		}
		running = false;
		clearTask();
	}

	public List<Runnable> getTaskList() {
		return taskList;
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
					isAuthenticated = true;
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

		/**
		 * 
		 * @param session
		 */
		private void startHeartBeatThread(final IoSession session) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (session != null && session.isConnected()) {
						try {
							session.write(heartBeatJson);
							TimeUnit.SECONDS
									.sleep(Constants.HEART_BEAT_INTERVAL);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
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
					signUtil.getSign(), GetPhoneStatec.getClientType(),
					GetPhoneStatec.getVersionName(pushService),
					initConnect.client_id, "userid01");
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
			isSessionOpen = false;
			isAuthenticated = false;
			disConnect();
			runTask();
			startNewReconnectionThread();
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			Log.d(TAG, "sessionCreated");
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status)
				throws Exception {
			Log.d(TAG, "sessionIdle");
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			Log.d(TAG,
					"sessionOpened and start heart beat session.isConnected() :"
							+ (session == null ? " NUll" : session
									.isConnected()));
			isSessionOpen = true;
			startHeartBeatThread(session);
			clearTask();
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
