package org.azentreprise.arionide.events;

import org.azentreprise.arionide.ui.overlay.Component;

public abstract class FocusEvent extends Event {
	
	private final Component target;
	
	public FocusEvent(Component target) {
		this.target = target;
	}
	
	public boolean isTargetting(Component potential) {
		return this.target == potential;
	}
}