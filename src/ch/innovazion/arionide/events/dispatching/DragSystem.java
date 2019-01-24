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

import java.util.Set;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.ActionEvent;
import ch.innovazion.arionide.events.ActionType;
import ch.innovazion.arionide.events.DragEvent;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.MoveEvent;
import ch.innovazion.arionide.events.MoveType;
import ch.innovazion.arionide.ui.topology.Point;

public class DragSystem implements EventHandler {
	
	private final IEventDispatcher dispatcher;
	
	private Point anchor;
	
	private DragSystem(IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	public static void init(IEventDispatcher dispatcher) {
		dispatcher.registerHandler(new DragSystem(dispatcher));
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ActionEvent) {
			ActionEvent action = (ActionEvent) event;
			
			if(action.getType().equals(ActionType.PRESS)) {
				this.anchor = action.getPoint();
			} else if(action.getType().equals(ActionType.RELEASE)) {
				this.anchor = null;
			}
		} else if(event instanceof MoveEvent) {
			MoveEvent move = (MoveEvent) event;
			
			if(this.anchor != null && move.getType().equals(MoveType.DRAG)) {
				DragEvent drag = new DragEvent(this.anchor, move.getPoint().getX() - this.anchor.getX(), move.getPoint().getY() - this.anchor.getY());
				this.dispatcher.fire(drag);
			}
		}
	}

	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(ActionEvent.class, MoveEvent.class);
	}
}