package org.azentreprise.arionide.ui;

import org.azentreprise.arionide.threading.WorkingThread;

public class AWTWrapperThread extends WorkingThread {

	private Object lock;
	
	public void tick() {
		if(this.lock != null) {
			synchronized(this.lock) {
				try {
					this.lock.wait();
				} catch (InterruptedException e) {
					;
				}
			}
		}
	}
	
	public void startDrawing() {
		this.lock = new Object();
		this.resetTimer();
	}
	
	public void stopDrawing() {
		if(this.lock != null) {
			synchronized(this.lock) {
				this.lock.notify();
				this.lock = null;
			}
		}
	}

	public long getRefreshDelay() {
		return 20;
	}

	public String getDescriptor() {
		return "AWT Drawing Thread";
	}

	public boolean respawn(int attempt) {
		return false;
	}
	
}