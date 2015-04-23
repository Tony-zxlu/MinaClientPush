package com.ucheuxing.push;

import android.app.Application;

public class UcheUxingApplication extends Application {

	private static UcheUxingApplication instance;

	public static UcheUxingApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

}
