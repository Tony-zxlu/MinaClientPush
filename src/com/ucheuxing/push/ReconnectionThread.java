package com.ucheuxing.push;

import android.util.Log;

/**
 * A thread class for recennecting the server.
 * 
 */
public class ReconnectionThread extends Thread {

	private static final String LOGTAG = ReconnectionThread.class
			.getSimpleName();

	private final PushManager pushManager;

	private int waiting;

	ReconnectionThread(PushManager pushManager) {
		this.pushManager = pushManager;
		this.waiting = 0;
	}

	public void run() {
		try {
			while (!isInterrupted() && !pushManager.isSessoinOpen()) {
				Log.d(LOGTAG, "Trying to reconnect in " + waiting()
						+ " seconds");
				Thread.sleep((long) waiting() * 1000L);
				pushManager.connect();
				waiting++;
			}
		} catch (final InterruptedException e) {
			Log.d(LOGTAG, "reconnct failed");
		}
	}

	private int waiting() {
		if (waiting > 20) {
			return 600;
		}
		if (waiting > 13) {
			return 300;
		}
		return waiting <= 7 ? 10 : 60;
	}
}
