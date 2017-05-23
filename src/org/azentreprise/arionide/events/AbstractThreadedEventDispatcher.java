package org.azentreprise.arionide.events;

public abstract class AbstractThreadedEventDispatcher implements IEventDispatcher {
	protected abstract void dispatchEvents();
}