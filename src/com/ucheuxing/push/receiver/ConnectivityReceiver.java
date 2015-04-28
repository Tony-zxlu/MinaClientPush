package com.ucheuxing.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ucheuxing.push.PushService;

/**
 * A broadcast receiver to handle the changes in network connectiion states.
 * 
 */
public class ConnectivityReceiver extends BroadcastReceiver {

	private static final String TAG = ConnectivityReceiver.class
			.getSimpleName();
	private PushService pushService;

	private boolean isFirst = true;
	
	public ConnectivityReceiver(PushService pushService) {
		this.pushService = pushService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "ConnectivityReceiver.onReceive()...");
		String action = intent.getAction();
		Log.d(TAG, "action=" + action);
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			Log.d(TAG, "Network Type  = " + networkInfo.getTypeName());
			Log.d(TAG, "Network State = " + networkInfo.getState());
			if (networkInfo.isConnected()) {
				Log.i(TAG, "Network connected");
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
			Log.e(TAG, "Network unavailable");
		}
	}

}
