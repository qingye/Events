package org.xmobile.framework;

import org.xmobile.framework.events.EventManager;

import android.app.Application;

public class BaseApplication extends Application {

	/*****************************************************************************
	 * Application's Instance
	 *****************************************************************************/
	private static BaseApplication mInstance = null;
	
	public static synchronized BaseApplication getInstance(){
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		EventManager.getInstance();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}