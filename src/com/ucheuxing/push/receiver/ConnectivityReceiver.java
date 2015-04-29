package com.ucheuxing.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ucheuxing.push.PushService;
import com.ucheuxing.push.util.Logger;

/**
 * A broadcast receiver to handle the changes in network connectiion states.
 * 
 */
public class ConnectivityReceiver extends BroadcastReceiver {

	private static final String TAG = ConnectivityReceiver.class
			.getSimpleName();
	private PushService pushService;

	private boolean isFirst = true;

	private Logger logger;

	public ConnectivityReceiver(PushService pushService) {
		this.pushService = pushService;
		logger = new Logger(TAG, Logger.TONY);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		logger.d("ConnectivityReceiver.onReceive()...");
		String action = intent.getAction();
		logger.d("action=" + action);
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			logger.d("Network Type  = " + networkInfo.getTypeName());
			logger.d("Network State = " + networkInfo.getState());
			if (networkInfo.isConnected()) {
				logger.i("Network connected");
				if (isFirst) {
					isFirst = false;
					return;
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pushService.disConnect();
				pushService.connect();
			}
		} else {
			logger.e("Network unavailable");
		}
	}

}
