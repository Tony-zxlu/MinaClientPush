package com.ucheuxing.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ucheuxing.push.receiver.ConnectivityReceiver;
import com.ucheuxing.push.receiver.PhoneStateChangeListener;
import com.ucheuxing.push.util.Constants;

public class PushService extends Service {

	public static final String SERVICE_NAME = "com.ucheuxing.push.PushService";

	private static final String TAG = PushService.class.getSimpleName();
	private ConnectivityReceiver connectivityReceiver;
	private PhoneStateListener phoneStateListener;
	private TelephonyManager telephonyManager;
	private PushManager pushManager;
	
	public PushService() {
		super();
		connectivityReceiver = new ConnectivityReceiver(this);
		phoneStateListener = new PhoneStateChangeListener(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, " onCreate  ");
		setAlarmWakeup();
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		registerConnectivityReceiver();
		pushManager = new PushManager(this);
	}

	private void setAlarmWakeup() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// 包装需要执行Service的Intent
		Intent intent = new Intent(this, this.getClass());
		intent.putExtra(Constants.ALARM_WAKEUP_TAG, true);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// 触发服务的起始时间
		long triggerAtTime = SystemClock.elapsedRealtime()+10*1000;
		// 使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				triggerAtTime, Constants.ALARM_WAKEUP_INTERVAL, pendingIntent);
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
		Log.d(TAG, " onStart intent == null " + (intent == null));
		if (intent != null) {
			boolean wakeup = intent.getBooleanExtra(Constants.ALARM_WAKEUP_TAG,
					false);
			boolean sessionIsConnected = pushManager.sessionIsConnected();
			Log.d(TAG, "  wakeup : " + wakeup + " sessionIsConnected : "
					+ sessionIsConnected);
			if (wakeup && sessionIsConnected) {
				return;
			}
		}
		connect();
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
		Log.d(TAG, " onDestroy ");
		unregisterConnectivityReceiver();
		if (pushManager != null) {
			pushManager.disConnect();
		}
		System.gc();
	}

	private void registerConnectivityReceiver() {
		Log.d(TAG, "registerConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectivityReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		Log.d(TAG, "unregisterConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(connectivityReceiver);
	}

}
