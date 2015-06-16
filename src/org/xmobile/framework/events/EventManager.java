package org.xmobile.framework.events;

import android.os.Message;
import android.os.SystemClock;

public class EventManager {

	private static EventManager mInstance = null;
	private EventThread mEventThread = null;

	private EventManager(){
		initEventThread();
	}
	
	public synchronized static EventManager getInstance(){
		if(mInstance == null){
			mInstance = new EventManager();
		}
		return mInstance;
	}
	
	private void initEventThread(){
		if(mEventThread == null){
			mEventThread = new EventThread();
		}
	}
	
	/*******************************************************************************
	 * Register / UnRegister handler
	 *******************************************************************************/
	public void register(EventHandler handler){
		Message msg = mEventThread.getHandler().obtainMessage();
		msg.what = EventThread.EVENT_ID_ADD_HANDLER;
		msg.obj = handler;
		mEventThread.getHandler().sendMessage(msg);
	}
	public void unregister(EventHandler handler){
		Message msg = mEventThread.getHandler().obtainMessage();
		msg.what = EventThread.EVENT_ID_REMOVE_HANDLER;
		msg.obj = handler;
		mEventThread.getHandler().sendMessage(msg);
	}
	
	/*******************************************************************************
	 * Event enQueue
	 *******************************************************************************/
	public void enQueueEvent(Events event, long when){
		if(event == null){
			new Throwable("event is null");
		}
		if(event.getTarget() == null){
			new Throwable("event.target is null");
		}
		
		event.setWhen(SystemClock.uptimeMillis() + when);
		Message msg = mEventThread.getHandler().obtainMessage();
		msg.what = EventThread.EVENT_ID_ENQUEUE;
		msg.obj = event;
		mEventThread.getHandler().sendMessage(msg);
	}
}
