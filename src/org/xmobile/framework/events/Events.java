package org.xmobile.framework.events;

import java.io.Serializable;
import java.util.ArrayList;

public class Events implements Serializable{

	private static final long serialVersionUID = 6178664537380843525L;
	
	/*******************************************************************
	 * Notify all targets
	 *******************************************************************/
	public final static Class<?> EVENT_TARGET_ALL = Object.class;
	
	/*******************************************************************
	 * Wait until target add into Observer, then trigger the event
	 *******************************************************************/
	public final static long WAIT_UNTIL_LOAD = 0xffffffff;
	
	/*******************************************************************
	 * If target = Events.EVENT_TARGET_ALL, it means notify all targets
	 * exist in the Observers
	 * 
	 * @see
	 * if target = Events.EVENT_TARGET_ALL and when = WAIT_UNTIL_LOAD,
	 * then when is ineffective.
	 *******************************************************************/
	private Class<?> target = null;
	private ICallback callback = null;
	private long when = 0;
	private int code = 0;
	private ArrayList<Object> datalist = null;
	
	public Events(){
		datalist = new ArrayList<Object>();
	}

	public Class<?> getTarget() {
		return target;
	}
	public void setTarget(Class<?> target) {
		this.target = target;
	}
	public ICallback getCallback() {
		return callback;
	}
	public void setCallback(ICallback callback) {
		this.callback = callback;
	}
	public long getWhen() {
		return when;
	}
	public void setWhen(long when) {
		this.when = when;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public ArrayList<Object> getDatalist() {
		return datalist;
	}
	public void setDatalist(ArrayList<Object> datalist) {
		this.datalist = datalist;
	}
}
