/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018, 2019 AZEntreprise Corporation. All rights reserved.
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
package ch.innovazion.arionide.events.dispatching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.threading.EventDispatchingThread;

public class MainEventDispatcher extends AbstractThreadedEventDispatcher {
	
	private final List<HandlerContainer> handlers = Collections.synchronizedList(new ArrayList<HandlerContainer>());
	private final Queue<Event> events = new ConcurrentLinkedQueue<>();
	
	private volatile boolean paused = false;
	private volatile boolean waiting = false;
		
	public MainEventDispatcher(EventDispatchingThread thread) {
		thread.setup(this);
	}
	
	public void fire(Event event) {
		events.add(event);
	}
	
	public void purge() {
		events.clear();
	}
	
	public synchronized void dispatchEvents() {		
		while(!paused && !events.isEmpty()) {
			Event event = events.poll();			

			for(HandlerContainer container : handlers) {
				EventHandler handler = container.handler;
				
				if(handler.getHandleableEvents().contains(event.getClass())) {
					if(!event.hasBeenAborted()) {
						handler.handleEvent(event);
					}
				}
			}
		}
		
		if(waiting) {
			notifyAll();
			waiting = false;
		}
	}

	public synchronized void registerHandler(EventHandler handler) {
		registerHandler(handler, 0.5f);
	}
	
	public synchronized void registerHandler(EventHandler handler, float priority) {
		handlers.add(new HandlerContainer(handler, priority));
		Collections.sort(handlers);
	}
	
	public synchronized void flush() { // Acquire lock (terminate current event dispatching) and wait for a complete event dispatching
		waiting = true;
		
		try {
			wait();
		} catch(InterruptedException exception) {
			;
		} finally {
			waiting = false;
		}
	}
	
	public void pause() {
		paused = true;
	}
	
	public void resume() {
		paused = false;
	}
	
	private class HandlerContainer implements Comparable<HandlerContainer> {

		private final EventHandler handler;
		private final float priority;
		
		private HandlerContainer(EventHandler handler, float priority) {
			this.handler = handler;
			this.priority = priority;
		}
		
		public int compareTo(HandlerContainer other) {
			return Float.compare(other.priority, priority);
		}
	}
}