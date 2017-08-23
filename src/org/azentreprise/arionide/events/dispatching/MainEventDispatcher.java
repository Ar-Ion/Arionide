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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.events.dispatching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.threading.EventDispatchingThread;

public class MainEventDispatcher extends AbstractThreadedEventDispatcher {

	private final List<EventHandler> handlers = Collections.synchronizedList(new ArrayList<>());
	private final Queue<Event> events = new LinkedList<>();
		
	public MainEventDispatcher(EventDispatchingThread thread) {
		thread.setup(this);
	}
	
	public synchronized void fire(Event event) {
		this.events.add(event);
	}
	
	public void purge() {
		this.handlers.clear();
		this.events.clear(); // this should cause a crash
	}
	
	public synchronized void dispatchEvents() {	
		while(!this.events.isEmpty()) {
			Event event = this.events.poll();
							
			for(EventHandler handler : this.handlers) {
				if(handler != null && handler.getHandleableEvents() != null && event != null) {
					if(handler.getHandleableEvents().contains(event.getClass())) {
						if(!event.hasBeenAborted()) {
							handler.handleEvent(event);
						}
					}
				}
			}
		}
	}

	public synchronized void registerHandler(EventHandler handler) {
		this.handlers.add(handler);
	}
}