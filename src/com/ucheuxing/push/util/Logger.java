package com.ucheuxing.push.util;

import android.util.Log;

/**
 * 
 * @author Tony DateTime 2015-4-29 上午10:13:23
 * @version 1.0
 */
public class Logger {

	public boolean DEBUG = true;
	public int LOG_LEVEL = Log.VERBOSE;
	private String logTag;
	private String filterTag;

	public static final String TONY = "TONY";

	public Logger(String logTag, String filterTag) {
		super();
		this.logTag = logTag;
		this.filterTag = filterTag;
	}

	private String getExtraInfo() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (stackTrace != null) {
			for (StackTraceElement st : stackTrace) {
				if (st.isNativeMethod())
					continue;
				if (st.getClassName().equals(Thread.class.getName()))
					continue;
				if (st.getClassName().equals(this.getClass().getName()))
					continue;

				return filterTag + "[" + Thread.currentThread().getName()
						+ " : " /*+ st.getFileName() + " : " */+ st.getMethodName()
						+ " : " + st.getLineNumber() + "]";

			}
		}
		return "NULL : ";
	}

	public void v(Object obj) {
		if (DEBUG && LOG_LEVEL <= Log.VERBOSE) {
			String extraInfo = getExtraInfo();
			Log.d(logTag, extraInfo + (obj == null ? "NULL" : obj));
		}
	}

	public void d(Object obj) {
		if (DEBUG && LOG_LEVEL <= Log.DEBUG) {
			String extraInfo = getExtraInfo();
			Log.d(logTag, extraInfo + (obj == null ? "NULL" : obj));
		}
	}

	public void i(Object obj) {
		if (DEBUG && LOG_LEVEL <= Log.INFO) {
			String extraInfo = getExtraInfo();
			Log.d(logTag, extraInfo + (obj == null ? "NULL" : obj));
		}
	}

	public void w(Object obj) {
		if (DEBUG && LOG_LEVEL <= Log.WARN) {
			String extraInfo = getExtraInfo();
			Log.d(logTag, extraInfo + (obj == null ? "NULL" : obj));
		}
	}

	public void e(Object obj) {
		if (DEBUG && LOG_LEVEL <= Log.ERROR) {
			String extraInfo = getExtraInfo();
			Log.d(logTag, extraInfo + (obj == null ? "NULL" : obj));
		}
	}

}
