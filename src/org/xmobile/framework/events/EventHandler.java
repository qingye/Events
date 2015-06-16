package org.xmobile.framework.events;

public class EventHandler {

	protected Class<?> src = null;
	private ICallback mCallback = null;
	
	public EventHandler(Class<?> clz){
		this(clz, null);
	}
	public EventHandler(Class<?> clz, ICallback cb){
		src = clz;
		mCallback = cb;
		EventManager.getInstance().register(this);
	}
	
	public void handleEvent(Events event){
	}

	/*******************************************************************************
	 * Send event in queue
	 *******************************************************************************/
	public void sendEvent(Events event){
		sendEvent(event, 0);
	}
	public void sendEvent(Events event, long delay){
		EventManager.getInstance().enQueueEvent(event, delay);
	}
	
	/*******************************************************************************
	 * Dispatch event
	 *******************************************************************************/
	public void dispatchEvent(Events event){
		if(event.getCallback() != null){
			event.getCallback().handleEvent(event);
		}else if(mCallback != null){
			mCallback.handleEvent(event);
		}else{
			handleEvent(event);
		}
	}
}
