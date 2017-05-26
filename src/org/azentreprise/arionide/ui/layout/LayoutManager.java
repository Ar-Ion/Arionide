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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.IEventDispatcher;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
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
		
		dispatcher.registerHandler(this, IEventDispatcher.MEDIUM_PRIORITY);
	}
	
	public void register(Surface surface, Surface parent, float x, float y, float width, float height) {
		if(parent == null || this.surfaces.containsKey(parent)) {
			this.surfaces.put(surface, new LayoutConfiguration(parent, x, y, width, height));
		} else {
			throw new RuntimeException("Parent surface is not registered");
		}
	}
	
	public void unregister(Surface surface) {
		this.surfaces.remove(surface);
		this.unregister0(surface, this.surfaces.entrySet());
	}
	
	private void unregister0(Surface surface, Set<Entry<Surface, LayoutConfiguration>> children) {
		Iterator<Entry<Surface, LayoutConfiguration>> iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Entry<Surface, LayoutConfiguration> child = iterator.next();
			
			if(child.getValue().getParent() == surface) {
				children.iterator();
				this.unregister0(child.getKey(), children);
			}
		}
	}
	
	@IAm("computing the layout")
	public void compute() {
		Map<Surface, Rectangle> layout = new HashMap<>();
		
		this.surfaces.forEach((surface, configuration) -> {
			if(configuration.getParent() != null) {
				if(layout.containsKey(configuration.getParent())) {
					Rectangle parentBounds = layout.get(configuration.getParent());
					
					int x = (int) (parentBounds.x + parentBounds.width * configuration.getX());
					int y = (int) (parentBounds.y + parentBounds.height * configuration.getY());
					int width = (int) (parentBounds.width * configuration.getWidth()) - x;
					int height = (int) (parentBounds.height * configuration.getHeight()) - y;
					
					layout.put(surface, new Rectangle(x, y, width, height));
					
					surface.setLayoutBounds(x, y, width, height);
				} else {
					System.err.println("Parent surface (" + configuration.getParent() + ") has not been computed");
				}
			} else {
				int width = (int) (this.frameWidth * configuration.width);
				int height = (int) (this.frameHeight * configuration.height);
				
				layout.put(surface, new Rectangle(0, 0, width, height));
				
				surface.setLayoutBounds(0, 0, width, height);
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
		private final float x;
		private final float y;
		private final float width;
		private final float height;
		
		private LayoutConfiguration(Surface parent, float x, float y, float width, float height) {
			this.parent = parent;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		private Surface getParent() {
			return this.parent;
		}
		
		private float getX() {
			return this.x;
		}
		
		private float getY() {
			return this.y;
		}
		
		private float getWidth() {
			return this.width;
		}
		
		private float getHeight() {
			return this.height;
		}
	}
}