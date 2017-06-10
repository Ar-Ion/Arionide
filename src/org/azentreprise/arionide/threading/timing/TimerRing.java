package org.azentreprise.arionide.threading.timing;

import java.util.TimerTask;

import org.azentreprise.arionide.events.IEventDispatcher;
import org.azentreprise.arionide.events.TimerEvent;

public final class TimerRing extends TimerTask {
	
	private final IEventDispatcher dispatcher;
	
	private final Object target;
	
	protected TimerRing(IEventDispatcher dispatcher, Object target) {
		this.dispatcher = dispatcher;
		this.target = target;
	}

	public void run() {
		this.dispatcher.fire(new TimerEvent(this.target));
	}
}