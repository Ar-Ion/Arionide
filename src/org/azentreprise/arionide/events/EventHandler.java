package org.azentreprise.arionide.events;

public interface EventHandler<T extends Event> {
	public void handleEvent(T event);
}
