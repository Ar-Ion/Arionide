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
package org.azentreprise.arionide.events;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import org.azentreprise.arionide.threading.EventDispatchingThread;

public class MainEventDispatcher extends AbstractThreadedEventDispatcher {

	private final List<EventHandler> handlers = new ArrayList<>();
	private final Queue<Event> events = new LinkedList<>();
	
	private final TransferQueue<EventHandler> newHandlers = new LinkedTransferQueue<>();
	
	public MainEventDispatcher(EventDispatchingThread thread) {
		thread.setup(this);
	}
	
	public void fire(Event event) {
		this.events.add(event);
	}
	
	public void dispatchEvents() {
		this.newHandlers.drainTo(this.handlers);
		
		while(!this.events.isEmpty()) {
			Event event = this.events.poll();
						
			this.handlers.stream()
				.filter(handler -> handler.getHandleableEvents().contains(event.getClass()))
				.forEach(handler -> handler.handleEvent(event));
		}
	}

	public void registerHandler(EventHandler handler) {
		this.newHandlers.add(handler);
	}
}