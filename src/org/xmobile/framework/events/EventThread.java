package org.xmobile.framework.events;

import java.util.ArrayList;
import java.util.LinkedList;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

public class EventThread extends Thread {

	public Handler mThreadHandler = null;
	private Handler mEventManagerHandler = null;
	private EventObserver mEventObserver = null;
	private LinkedList<Events> mEventQueue = null;
	
	public final static int EVENT_ID_NONE = 0;
	public final static int EVENT_ID_ADD_HANDLER = 1;
	public final static int EVENT_ID_REMOVE_HANDLER = 2;
	public final static int EVENT_ID_ENQUEUE = 3;
	public final static int EVENT_ID_CHECK_QUEUE = 4;
	public final static int EVENT_ID_CHECK_FOR_NEW_HANDLER = 5;
	public final static int EVENT_ID_TIME_RESET = 6;

	public EventThread(Handler handler){
		mEventManagerHandler = handler;
	}
	
	private void initEventObserser(){
		if(mEventObserver == null){
			mEventObserver = new EventObserver();
		}
	}
	
	private void initEventQueue(){
		if(mEventQueue == null){
			mEventQueue = new LinkedList<Events>();
		}
	}
	
	public Handler getHandler(){
		return mThreadHandler;
	}
	
	/*******************************************************************************
	 * Loop for binding self's handler
	 *******************************************************************************/
	@Override
	public void run() {
		Looper.prepare();
		if(mThreadHandler == null){
			initEventQueue();
			initHandler();
		}
		Looper.loop();
	}
	
	/*******************************************************************************
	 * Register / UnRegister handler
	 *******************************************************************************/
	private void register(EventHandler handler){
		if(mEventObserver == null){
			initEventObserser();
		}
		
		if(mEventObserver.addTo(handler)){
			Message msg = mThreadHandler.obtainMessage();
			msg.what = EVENT_ID_CHECK_FOR_NEW_HANDLER;
			msg.obj = handler;
			mThreadHandler.sendMessage(msg);
		}
	}
	private void unregister(EventHandler handler){
		if(mEventObserver != null){
			mEventObserver.removeFrom(handler);
		}
	}
	
	/*******************************************************************************
	 * EnQueue
	 *******************************************************************************/
	private void enQueue(Events event){
		if(mEventQueue == null){
			initEventQueue();
		}
		
		/*********************************************
		 * If when == Events.WAIT_UNTIL_LOAD, then add
		 * it at the last position.
		 *********************************************/
		if(event.getWhen() == Events.WAIT_UNTIL_LOAD){
			mEventQueue.addLast(event);
			return;
		}
		
		/*********************************************
		 * If when != Events.WAIT_UNTIL_LOAD, then add
		 * it before the Events.WAIT_UNTIL_LOAD item
		 *********************************************/
		Events e = null;
		int i = 0;
		for(i = 0; i < mEventQueue.size(); i ++){
			e = mEventQueue.get(i);
			if(e.getWhen() == Events.WAIT_UNTIL_LOAD || e.getWhen() > event.getWhen()){
				break;
			}
		}
		mEventQueue.add(i, event);
		
		if(i == 0){
			mThreadHandler.sendEmptyMessage(EVENT_ID_TIME_RESET);
		}
	}
	
	/*******************************************************************************
	 * Check Queue, if when = 0, then dispatch event to the target handler or all
	 *******************************************************************************/
	private void checkQueue(){
		if(mEventQueue == null || mEventQueue.size() == 0){
			return;
		}

		Events e = null;
		long timeNow = SystemClock.uptimeMillis();
		while(mEventQueue.size() > 0 && 
			 (mEventQueue.get(0).getWhen() >= 0 && mEventQueue.get(0).getWhen() <= timeNow)){
			e = mEventQueue.remove(0);
			ArrayList<EventHandler> hdrs = mEventObserver.getHandlers();
			if(e.getTarget().equals(Events.EVENT_TARGET_ALL)){
				for(EventHandler h : hdrs){
					Message msg = mEventManagerHandler.obtainMessage();
					msg.what = EventManager.EVENT_ID_DISPATCH;
					msg.obj = new Object[]{h, e};
					mEventManagerHandler.sendMessage(msg);
				}
			}else{
				for(EventHandler h : hdrs){
					if(e.getTarget().equals(h.src)){
						Message msg = mEventManagerHandler.obtainMessage();
						msg.what = EventManager.EVENT_ID_DISPATCH;
						msg.obj = new Object[]{h, e};
						mEventManagerHandler.sendMessage(msg);
						break;
					}
				}
			}
			timeNow = SystemClock.uptimeMillis();
		}
		
		if(mEventQueue.size() > 0 && mEventQueue.get(0).getWhen() > 0){
			long slot = mEventQueue.get(0).getWhen() - SystemClock.uptimeMillis();
			mThreadHandler.postDelayed(mRunnable, slot);
		}
	}
	
	/*******************************************************************************
	 * Check if exist Events.WAIT_UNTIL_LOAD event for this new handler
	 *******************************************************************************/
	private void checkForNewHandler(EventHandler h){
		if(mEventQueue == null || mEventQueue.size() == 0){
			return;
		}

		if(h != null){
			Events e = null;
			for(int i = mEventQueue.size() - 1; i >= 0; i --){
				e = mEventQueue.get(i);
				if(e.getWhen() != Events.WAIT_UNTIL_LOAD){
					break;
				}else if(e.getTarget().equals(h.src)){
					Message msg = mEventManagerHandler.obtainMessage();
					msg.what = EventManager.EVENT_ID_DISPATCH;
					msg.obj = new Object[]{h, e};
					mEventManagerHandler.sendMessage(msg);
					mEventQueue.remove(e);
				}
			}
		}
	}

	/*******************************************************************************
	 * Runnable to awake the next event in the queue if exist
	 *******************************************************************************/
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mThreadHandler.sendEmptyMessage(EVENT_ID_TIME_RESET);
		}
	};

	/*******************************************************************************
	 * Handler for process all events
	 *******************************************************************************/
	private void initHandler(){
		mThreadHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case EVENT_ID_ADD_HANDLER:
					register((EventHandler) msg.obj);
					break;
					
				case EVENT_ID_REMOVE_HANDLER:
					unregister((EventHandler) msg.obj);
					break;
					
				case EVENT_ID_ENQUEUE:
					enQueue((Events) msg.obj);
					break;
					
				case EVENT_ID_CHECK_QUEUE:
					checkQueue();
					break;
					
				case EVENT_ID_CHECK_FOR_NEW_HANDLER:
					checkForNewHandler((EventHandler) msg.obj);
					break;
					
				case EVENT_ID_TIME_RESET:
					mThreadHandler.removeCallbacks(mRunnable);
					mThreadHandler.sendEmptyMessage(EVENT_ID_CHECK_QUEUE);
					break;
				}
			}
		};
	}
}
