/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.ui.layout;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
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
	
	private final Map<Surface, LayoutConfiguration> surfaces = new HashMap<>();
	
	private int frameWidth;
	private int frameHeight;
	
	public LayoutManager(AppDrawingContext drawingContext, IEventDispatcher dispatcher) {
		this.drawingContext = drawingContext;
		
		this.frameWidth = drawingContext.getSize().width;
		this.frameHeight = drawingContext.getSize().height;
		
		dispatcher.registerHandler(this);
	}
	
	public synchronized void register(Surface surface, Surface parent, double x1, double y1, double x2, double y2) {
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
			double x1 = config.x1 * this.frameWidth;
			double y1 = config.y1 * this.frameHeight;
			double x2 = config.x2 * this.frameWidth;
			double y2 = config.y2 * this.frameHeight;

			surface.setBounds(new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1));
		});
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof InvalidateLayoutEvent) {
			Dimension size = this.drawingContext.getSize();
			
			this.frameWidth = size.width;
			this.frameHeight = size.height;
			
			this.apply();
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(InvalidateLayoutEvent.class);
	}
	
	private final class LayoutConfiguration {
		
		private Surface parent;
		private double x1;
		private double y1;
		private double x2;
		private double y2;
		
		private LayoutConfiguration(Surface parent, double x1, double y1, double x2, double y2) {
			this.parent = parent;
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	}
}