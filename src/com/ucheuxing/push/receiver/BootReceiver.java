package com.ucheuxing.push.receiver;

import com.ucheuxing.push.PushService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private static final String TAG = BootReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, " BootReceiver onReceive start PushService ");
		context.startService(new Intent(context, PushService.class));
	}
}