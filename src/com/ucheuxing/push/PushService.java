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
	private TaskSubmitter mTaskSubmitter;

	private TaskTracker mTaskTracker;
	
	private PushManager pushManager;

	public PushService() {
		super();
		connectivityReceiver = new ConnectivityReceiver(this);
		phoneStateListener = new PhoneStateChangeListener(this);
		executorService = Executors.newSingleThreadExecutor();
		mTaskSubmitter = new TaskSubmitter(this);
		mTaskTracker = new TaskTracker(this);
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

	/**
     * Class for monitoring the running task count.
     */
    public class TaskTracker {

        final PushService mPushService;

        public int count;

        public TaskTracker(PushService mPushService) {
            this.mPushService = mPushService;
            this.count = 0;
        }

        public void increase() {
            synchronized (mPushService.getTaskTracker()) {
            	mPushService.getTaskTracker().count++;
                Log.d(TAG, "Incremented task count to " + count);
            }
        }

        public void decrease() {
            synchronized (mPushService.getTaskTracker()) {
            	mPushService.getTaskTracker().count--;
                Log.d(TAG, "Decremented task count to " + count);
            }
        }
        
        public void clear() {
            synchronized (mPushService.getTaskTracker()) {
            	mPushService.getTaskTracker().count = 0;
                Log.d(TAG, "Decremented task count to " + count);
            }
        }

    }
	
    
	public TaskTracker getTaskTracker() {
		return mTaskTracker;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public TaskSubmitter getTaskSubmitter() {
		return mTaskSubmitter;
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
		Log.d(TAG, " onDestroy ");
		unregisterConnectivityReceiver();
		if (pushManager != null) {
			pushManager.disConnect();
		}

		if (executorService != null) {
			executorService.shutdownNow();
		}
		System.gc();
	}

	private void registerConnectivityReceiver() {
		Log.d(TAG, "registerConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		IntentFilter filter = new IntentFilter();
		// filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
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
