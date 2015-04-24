package com.ucheuxing.push.util;

import java.util.List;

import com.ucheuxing.push.PushActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class Utils {

	private static final String TAG = Utils.class.getSimpleName();

	public static boolean isInOurUserInterface(Context mContext) {
		boolean flag = false;
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
		if (runningTasks != null && runningTasks.size() > 0) {
			String topClassName = runningTasks.get(0).topActivity
					.getClassName();
			String targetClassName = PushActivity.class.getName();
			Log.d(TAG, " topClassName : " + topClassName
					+ " targetClassName :　" + targetClassName);
			if (TextUtils.equals(topClassName, targetClassName)) {
				flag = true;
			}
		}

		return flag;
	}
}
