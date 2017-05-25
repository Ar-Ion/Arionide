/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.arionide.threading;

public abstract class WorkingThread extends Thread {

	private static final long CONTROL_DELAY = 500L;
	
	private boolean running = true;
	private long initTime = System.currentTimeMillis();
	
	public void run() {
		while(true) {
			try {
				while(this.running) {
					this.initTime = System.currentTimeMillis();
					
					this.tick();
					
					long delta = System.currentTimeMillis() - this.initTime - this.getRefreshDelay();
					
					if(delta > 0) {
						Thread.sleep(delta);
					}
				}
			
				Thread.sleep(WorkingThread.CONTROL_DELAY);
			} catch (InterruptedException e) {
				; // thread killed
			}			
		}
	}
	
	public float getLagRate() {
		return  (float) (System.currentTimeMillis() - this.initTime) / this.getRefreshDelay();
	}
	
	public abstract void tick();
	public abstract long getRefreshDelay();
	public abstract String getDescriptor();
	public abstract boolean respawn(int attempt); // the implementation must be safe and not thread-blocking. Please catch any exceptions.
}
