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
package ch.innovazion.arionide.ui.overlay.views;

import java.util.Set;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.MoveEvent;
import ch.innovazion.arionide.events.PressureEvent;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.render.PrimitiveRenderingSystem;

public abstract class OverlayView extends View implements EventHandler {
		
	public OverlayView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		appManager.getEventDispatcher().registerHandler(this, 0.7f);
	}
	
	public void drawSurface(AppDrawingContext context) {	
		super.drawSurface(context);
	}
	
	public PrimitiveRenderingSystem getPreferedRenderingSystem(AppDrawingContext context) {
		return context.getOverlayRenderingSystem();
	}
	
	public <T extends Event> void handleEvent(T event) {
		event.abortDispatching(); // Block events below 0.7 priority (CoreController / Menu)
	}

	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(PressureEvent.class, MoveEvent.class);
	}
}
