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
package org.azentreprise.arionide.ui.layout;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.ui.AppDrawingContext;

public class LayoutManager implements EventHandler {
	
	private final AppDrawingContext drawingContext;
	
	private final Map<Surface, LayoutConfiguration> surfaces = new LinkedHashMap<>();
	
	private int frameWidth;
	private int frameHeight;
	
	public LayoutManager(AppDrawingContext drawingContext, IEventDispatcher dispatcher) {
		this.drawingContext = drawingContext;
		
		this.frameWidth = drawingContext.getSize().width;
		this.frameHeight = drawingContext.getSize().height;
		
		dispatcher.registerHandler(this);
	}
	
	public synchronized void register(Surface surface, Surface parent, float x1, float y1, float x2, float y2) {
		if(parent == null || this.surfaces.containsKey(parent)) {
			this.surfaces.put(surface, new LayoutConfiguration(parent, x1, y1, x2, y2));
		} else {
			throw new RuntimeException("Parent surface is not registered");
		}
	}
	
	@IAm("computing the layout")
	public synchronized void compute() {
		Map<Surface, Rectangle> layout = new HashMap<>();
		
		this.surfaces.forEach((surface, configuration) -> {
			if(configuration.getParent() != null) {
				if(layout.containsKey(configuration.getParent())) {
					Rectangle parentBounds = layout.get(configuration.getParent());
										
					int x = (int) (parentBounds.x + parentBounds.width * configuration.getX1());
					int y = (int) (parentBounds.y + parentBounds.height * configuration.getY1());
					int width = (int) (parentBounds.width * (configuration.getX2() - configuration.getX1()));
					int height = (int) (parentBounds.height * (configuration.getY2() - configuration.getY1()));
					
					Rectangle bounds = new Rectangle(x, y, width, height);
										
					layout.put(surface, bounds);
					
					surface.setLayoutBounds(bounds);
				} else {
					System.err.println("Parent surface (" + configuration.getParent() + ") has not been computed");
				}
			} else {
				int x = (int) (this.frameWidth * configuration.getX1());
				int y = (int) (this.frameHeight * configuration.getY1());
				int width = (int) (this.frameWidth * configuration.getX2()) - x;
				int height = (int) (this.frameHeight * configuration.getY2()) - y;
				
				Rectangle bounds = new Rectangle(x, y, width, height);
				
				layout.put(surface, bounds);
				
				surface.setLayoutBounds(bounds);
			}
		});
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof InvalidateLayoutEvent) {
			Dimension size = this.drawingContext.getSize();
			
			this.frameWidth = size.width;
			this.frameHeight = size.height;
			
			this.compute();
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(InvalidateLayoutEvent.class);
	}
	
	private final class LayoutConfiguration {
		
		private final Surface parent;
		private final float x1;
		private final float y1;
		private final float x2;
		private final float y2;
		
		private LayoutConfiguration(Surface parent, float x1, float y1, float x2, float y2) {
			this.parent = parent;
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
		private Surface getParent() {
			return this.parent;
		}
		
		private float getX1() {
			return this.x1;
		}
		
		private float getY1() {
			return this.y1;
		}
		
		private float getX2() {
			return this.x2;
		}
		
		private float getY2() {
			return this.y2;
		}
	}
}