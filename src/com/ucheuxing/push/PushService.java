package com.ucheuxing.push;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ucheuxing.push.receiver.ConnectivityReceiver;
import com.ucheuxing.push.receiver.PhoneStateChangeListener;

public class PushService extends Service {

	public static final String SERVICE_NAME = "com.ucheuxing.push.PushService";

	private static final String TAG = PushService.class.getSimpleName();

	private ExecutorService executorService;
	private ConnectivityReceiver connectivityReceiver;
	private PhoneStateListener phoneStateListener;
	private TelephonyManager telephonyManager;
	private TaskSubmitter taskSubmitter;

	private PushManager pushManager;

	public PushService() {
		super();
		connectivityReceiver = new ConnectivityReceiver(this);
		phoneStateListener = new PhoneStateChangeListener(this);
		executorService = Executors.newSingleThreadExecutor();
		taskSubmitter = new TaskSubmitter(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, " onCreate  ");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		registerConnectivityReceiver();
		pushManager = new PushManager(this);
	}

	public void connect() {
		pushManager.connect();
	}

	public void disConnect() {
		if (pushManager != null) {
			pushManager.disConnect();
		}
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG, " onStart  ");
		connect();
	}

	/**
	 * Class for summiting a new runnable task.
	 */
	public class TaskSubmitter {

		final PushService pushService;

		public TaskSubmitter(PushService pushService) {
			this.pushService = pushService;
		}

		@SuppressWarnings("unchecked")
		public Future submit(Runnable task) {
			Future result = null;
			if (!pushService.getExecutorService().isTerminated()
					&& !pushService.getExecutorService().isShutdown()
					&& task != null) {
				result = pushService.getExecutorService().submit(task);
			}
			return result;
		}

	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public TaskSubmitter getTaskSubmitter() {
		return taskSubmitter;
	}

	public PushManager getPushManager() {
		return pushManager;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, " onBind ");
		return null;
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG, " onRebind ");
		super.onRebind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterConnectivityReceiver();
		if (pushManager != null) {
			pushManager.disConnect();
			pushManager = null;
		}

		if (executorService != null) {
			executorService.shutdownNow();
			executorService = null;
		}
		taskSubmitter = null;
		System.gc();
	}

	private void registerConnectivityReceiver() {
		Log.d(TAG, "registerConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
//		IntentFilter filter = new IntentFilter();
//		// filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
//		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
//		registerReceiver(connectivityReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		Log.d(TAG, "unregisterConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_NONE);
//		unregisterReceiver(connectivityReceiver);
	}

}
