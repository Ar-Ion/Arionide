package ch.innovazion.arionide.events;

public class TeleportEvent extends Event {
	
	private final int target;
	
	public TeleportEvent(int target) {
		this.target = target;
	}
	
	public int getTarget() {
		return target;
	}
}
