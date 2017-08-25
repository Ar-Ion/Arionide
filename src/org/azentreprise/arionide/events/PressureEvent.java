package org.azentreprise.arionide.events;

public class PressureEvent extends WriteEvent {
	
	private final boolean down;
	
	public PressureEvent(char ch, int keycode, int modifiers, boolean down) {
		super(ch, keycode, modifiers);
		this.down = down;
	}
	
	public boolean isDown() {
		return this.down;
	}
	
	public boolean isUp() {
		return !this.down;
	}
}