package org.xmobile.framework.events;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public class EventManager {

	private static EventManager mInstance = null;
	private EventThread mEventThread = null;
	
	public final static int EVENT_ID_DISPATCH = 0;

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
			mEventThread = new EventThread(mHandler);
			mEventThread.start();
		}else{
			mEventThread.run();
		}
	}
	
	/*******************************************************************************
	 * In theory, this handle is created at main thread.
	 *******************************************************************************/
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case EVENT_ID_DISPATCH:
				Object[] obj = (Object[]) msg.obj;
				if(obj != null && obj.length == 2){
					((EventHandler)obj[0]).dispatchEvent((Events) obj[1]);
				}
				break;
			}
		}
	};
	
	public Handler getEventManagerHandler(){
		return mHandler;
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
		
		if(when == Events.WAIT_UNTIL_LOAD){
			event.setWhen(when);
		}else{
			event.setWhen(SystemClock.uptimeMillis() + when);
		}
		Message msg = mEventThread.getHandler().obtainMessage();
		msg.what = EventThread.EVENT_ID_ENQUEUE;
		msg.obj = event;
		mEventThread.getHandler().sendMessage(msg);
	}
}
