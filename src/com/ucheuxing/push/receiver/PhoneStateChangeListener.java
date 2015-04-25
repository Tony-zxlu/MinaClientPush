package com.ucheuxing.push.receiver;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ucheuxing.push.PushService;

/**
 * A listener class for monitoring changes in phone connection states.
 * 
 */
public class PhoneStateChangeListener extends PhoneStateListener {

	private static final String TAG = PhoneStateChangeListener.class
			.getSimpleName();

	private final PushService pushService;

	public PhoneStateChangeListener(PushService pushService) {
		this.pushService = pushService;
	}

	@Override
	public void onDataConnectionStateChanged(int state) {
		super.onDataConnectionStateChanged(state);
		Log.d(TAG, "onDataConnectionStateChanged()...");
		Log.d(TAG, "Data Connection State = " + getState(state));

		if (state == TelephonyManager.DATA_CONNECTED) {
			pushService.connect();
		}
	}

	private String getState(int state) {
		switch (state) {
		case 0: // '\0'
			return "DATA_DISCONNECTED";
		case 1: // '\001'
			return "DATA_CONNECTING";
		case 2: // '\002'
			return "DATA_CONNECTED";
		case 3: // '\003'
			return "DATA_SUSPENDED";
		}
		return "DATA_<UNKNOWN>";
	}

}
