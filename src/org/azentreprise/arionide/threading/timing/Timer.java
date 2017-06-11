package org.azentreprise.arionide.threading.timing;

import org.azentreprise.arionide.events.dispatching.IEventDispatcher;

public class Timer {
	
	private final java.util.Timer theTimer = new java.util.Timer();
	
	private final IEventDispatcher dispatcher;
	
	public Timer(IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	public void schedule(Object target, long delay) {
		this.theTimer.schedule(new TimerRing(this.dispatcher, target), delay);
	}
}