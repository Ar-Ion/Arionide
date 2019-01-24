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
package ch.innovazion.arionide.ui.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.InvalidateLayoutEvent;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;
import ch.innovazion.arionide.ui.topology.Size;

public class LayoutManager implements EventHandler {
	
	private final AppDrawingContext drawingContext;
	
	private final Map<Surface, LayoutConfiguration> surfaces = new HashMap<>();
	
	private int frameWidth;
	private int frameHeight;
	
	public LayoutManager(AppDrawingContext drawingContext, IEventDispatcher dispatcher) {
		this.drawingContext = drawingContext;
		
		this.frameWidth = drawingContext.getWindowSize().getWidthAsInt();
		this.frameHeight = drawingContext.getWindowSize().getHeightAsInt();
		
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
		this.surfaces.values().forEach(config -> {
			this.compute(config);
		});
	}

	private void compute(LayoutConfiguration config) {
		if(config.parent != null) {
			LayoutConfiguration parentConfig = this.surfaces.get(config.parent);
			
			if(parentConfig.parent != null) {
				this.compute(parentConfig);
			}
			
			config.parent = null;
			config.x1 = parentConfig.x1 + config.x1 * (parentConfig.x2 - parentConfig.x1);
			config.y1 = parentConfig.y1 + config.y1 * (parentConfig.y2 - parentConfig.y1);
			config.x2 = parentConfig.x2 + (config.x2 - 1.0f) * (parentConfig.x2 - parentConfig.x1);
			config.y2 = parentConfig.y2 + (config.y2 - 1.0f) * (parentConfig.y2 - parentConfig.y1);
		}
	}
	
	@IAm("applying the layout")
	public synchronized void apply() {
		this.surfaces.forEach((surface, config) -> {
			float x1 = config.x1 * this.frameWidth;
			float y1 = config.y1 * this.frameHeight;
			float x2 = config.x2 * this.frameWidth;
			float y2 = config.y2 * this.frameHeight;
			
			surface.setBounds(new Bounds(new Point(x1, y1), new Point(x2, y2)));
		});
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof InvalidateLayoutEvent) {
			Size size = this.drawingContext.getWindowSize();
			
			this.frameWidth = size.getWidthAsInt();
			this.frameHeight = size.getHeightAsInt();
			
			this.apply();
		}
	}

	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(InvalidateLayoutEvent.class);
	}
	
	private final class LayoutConfiguration {
		
		private Surface parent;
		private float x1;
		private float y1;
		private float x2;
		private float y2;
		
		private LayoutConfiguration(Surface parent, float x1, float y1, float x2, float y2) {
			this.parent = parent;
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	}
}