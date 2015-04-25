package com.ucheuxing.push;

import android.content.Context;
import android.content.Intent;

import com.ucheuxing.push.util.Constants;
import com.ucheuxing.push.util.SharedPreferUtils;

public class ServiceManager {

	private Context context;
	private Intent intent;

	public ServiceManager(Context context) {
		super();
		this.context = context;
		intent = new Intent(context, PushService.class);
	}

	/**
	 * start push serive
	 * 
	 * @author Tony DateTime 2015-4-25 上午11:06:46
	 */
	public void startService() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				context.startService(intent);
			}
		}).start();
	}

	/**
	 * stop push service
	 * 
	 * @author Tony DateTime 2015-4-25 上午11:07:02
	 * @return
	 */
	public boolean stopService() {
		return context.stopService(intent);
	}

	public void setNotificationIcon(int iconId) {
		SharedPreferUtils.setInt(context, Constants.NOTIFICATION_ICON, iconId);
	}

}
