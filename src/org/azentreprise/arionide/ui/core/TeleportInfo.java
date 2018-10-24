package org.azentreprise.arionide.ui.core;

public class TeleportInfo {
	
	private int destination;
	private int focus;
	private long expires;
	
	public TeleportInfo(int destination, int focus) {
		this.destination = destination;
		this.focus = focus;
	}
	
	public void updateDestination(int destination) {
		this.destination = destination;
	}
	
	public void updateFocus(int focus) {
		this.focus = focus;
	}
	
	public void updateLifeTime(long lifeTime) {
		this.expires = System.currentTimeMillis() + lifeTime;
	}
	
	public int getDestination() {
		return this.destination;
	}
	
	public int getFocus() {
		return this.focus;
	}
	
	public boolean isAlive() {
		return System.currentTimeMillis() < this.expires;
	}
	
	public String toString() {
		return "<TeleportInfo | Destination: " + this.destination + "; Focus: " + this.focus + ">";
	}
}
