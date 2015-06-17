package org.xmobile.framework.events;

import java.util.ArrayList;

public class EventObserver {

	/*******************************************************************************
	 * Like notification center, cache all registered handlers
	 *******************************************************************************/
	private ArrayList<EventHandler> mObserver = null;	
	
	public EventObserver(){
		if(mObserver == null){
			mObserver = new ArrayList<EventHandler>();
		}
	}
	
	public boolean addTo(EventHandler handler){
		boolean ret = false;
		if(mObserver != null){
			for(EventHandler h : mObserver){
				if(h.src == handler.src){
					return ret;
				}
			}
			ret = mObserver.add(handler);
		}
		return ret;
	}
	
	public void removeFrom(EventHandler handler){
		if(mObserver != null){
			for(int i = 0; i < mObserver.size(); i ++){
				EventHandler h = mObserver.get(i);
				if(h.src == handler.src){
					mObserver.remove(h);
					return;
				}
			}
		}
	}
	
	public ArrayList<EventHandler> getHandlers(){
		return mObserver;
	}
}
