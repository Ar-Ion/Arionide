package org.azentreprise.arionide.events;

public class FrameResizedEvent extends Event {
	
	private final int width;
	private final int height;
	
	public FrameResizedEvent(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
}