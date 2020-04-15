/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
	
	private volatile boolean running = false;
	private volatile long initTime = 0;
	private volatile long ticks = 0;
	
	public void run() {
		while(true) {
			try {
				this.initTime = System.currentTimeMillis();
				
				while(running) {					
					this.initTime = System.currentTimeMillis();

					tick();
					incrementTicks();

					long delta = getRefreshDelay() - (System.currentTimeMillis() - initTime);
					
					if(delta > 1) {
						Thread.sleep(delta - 1);
					}
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
	
	public long pollTicks() {
		long buffer = this.ticks;
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
