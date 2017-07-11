/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.events.dispatching;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.DragEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.MoveEvent;
import org.azentreprise.arionide.events.MoveType;

public class DragSystem implements EventHandler {
	
	private final IEventDispatcher dispatcher;
	
	private Point2D anchor;
	
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

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ActionEvent.class, MoveEvent.class);
	}
}