package com.ucheuxing.push.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.WindowManager;

import com.ucheuxing.push.PushActivity;
import com.ucheuxing.push.R;

public class NotifyManager {

	public static void showPayDialog(Context mContext, String msg) {
		AlertDialog.Builder builder = new Builder(mContext);
		AlertDialog alertDialog = builder.setTitle("XXX先生").setMessage(msg)
				.setPositiveButton("确认放行", null).create();
		alertDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alertDialog.show();
	}

	@SuppressLint("NewApi")
	public static void showPayNotification(Context mContext, String msg) {
		NotificationManager nm = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder notiBuilder = new Notification.Builder(mContext);
		notiBuilder.setAutoCancel(true);
		notiBuilder.setContentText(msg);
		notiBuilder.setContentTitle("付款通知");
		notiBuilder.setSmallIcon(R.drawable.ic_launcher);
		Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(mContext,
				RingtoneManager.TYPE_NOTIFICATION);
		RingtoneManager.getRingtone(mContext, ringtoneUri).play();
		notiBuilder.setDefaults(Notification.DEFAULT_ALL);
		notiBuilder.setSound(ringtoneUri);
		Intent notificationIntent = new Intent(mContext, PushActivity.class); // 点击该通知后要跳转的Activity
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
				notificationIntent, 0);
		notiBuilder.setContentIntent(pendingIntent);
		Notification notification = notiBuilder.build();

		nm.notify(0, notification);
	}
}
