/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package ch.innovazion.arionide.threading;

import ch.innovazion.arionide.debugging.Debug;

public abstract class WorkingThread extends Thread {

	private static final long CONTROL_DELAY = 200L;
	
	private boolean running = false;
	private long initTime = System.currentTimeMillis();
	private int ticks = 0;
	
	public void run() {
		while(true) {
			try {
				while(this.running) {
					this.resetTimer();
					
					this.tick();
					
					long delta = System.currentTimeMillis() - this.initTime + this.getRefreshDelay();
										
					if(delta > 0) {
						Thread.sleep(delta);
					}
					
					this.incrementTicks();
				}
			
				Thread.sleep(WorkingThread.CONTROL_DELAY);
			} catch(InterruptedException e) {
				; // thread killed
			} catch(RuntimeException exception) {
				Debug.exception(exception);
			} catch(Exception exception) {
				Debug.exception(exception);
			}
		}
	}
	
	protected void resetTimer() {
		this.initTime = System.currentTimeMillis();
	}
	
	public int pollTicks() {
		int buffer = this.ticks;
		this.ticks = 0;
		return buffer;
	}
	
	public void incrementTicks() {
		this.ticks++;
	}
	
	public void start() {
		this.running = true;
		super.start();
	}
	
	public void pause() {
		this.running = false;
	}
	
	public abstract void tick();
	public abstract long getRefreshDelay();
	public abstract String getDescriptor();
	public abstract boolean respawn(int attempt); // the implementation must be safe and not thread-blocking. Please catch any exceptions.
}
