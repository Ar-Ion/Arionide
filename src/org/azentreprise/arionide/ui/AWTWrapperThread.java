package org.azentreprise.arionide.ui;

import java.util.concurrent.Semaphore;

import org.azentreprise.arionide.threading.WorkingThread;

public class AWTWrapperThread extends WorkingThread {

	private Semaphore semaphore = new Semaphore(2);
	
	public synchronized void tick() {
		try {
			this.semaphore.acquire();
			this.semaphore.release();
		} catch (InterruptedException e) {
			;
		}
	}
	
	public synchronized void startDrawing() {
		this.resetTimer();

		try {
			this.semaphore.acquire();
		} catch (InterruptedException e) {
			;
		}
	}
	
	public synchronized void stopDrawing() {
		this.semaphore.release();
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