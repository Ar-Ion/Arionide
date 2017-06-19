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
package org.azentreprise.arionide.ui.overlay.components;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.events.WheelEvent;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public class Scroll extends Tab {
	
	private boolean doubleFocusSystem = false;
	private boolean globalWheelListening = true;
	private int deltaX = 0;
	
	public Scroll(View parent, String... labels) {
		this(parent, Tab.makeLabels(parent, labels));
		
		super.setColor(0x0000CAFE);
		super.setSeparatorsRenderable(false);
	}
	
	public Scroll(View parent, List<Component> components) {
		super(parent, components);
	}
	
	public Scroll setDoubleFocusSystemState(boolean enabled) {
		this.doubleFocusSystem = enabled;
		return this;
	}
	
	public Scroll toggleGlobalWheelListening() {
		this.globalWheelListening = !this.globalWheelListening;
		return this;
	}
	
	public final Tab setSeparatorsRenderable(boolean yes) {
		return this; // Ignore
	}
	
	protected void update() {
		super.update();
		
		if(this.getBounds().width > 0) {
			this.setShadowRadius(this.getBounds().width / 2);
		}
	}

	public List<Rectangle> computeBounds() {
		List<Rectangle> rectangles = new ArrayList<>();
		Rectangle bounds = this.getBounds();
		int count = this.getComponents().size();
		int initial = bounds.width / 3;

		for(int i = -this.deltaX; i < 0; i++) {			
			double x = initial * (1 - Math.pow(2, i));
			double width = initial * Math.pow(2, i);

			rectangles.add(new Rectangle(bounds.x + initial - (int) x, bounds.y, (int) width, bounds.height));
		}
		
		rectangles.add(new Rectangle(bounds.x + initial, bounds.y, bounds.width / 3, bounds.height));
		
		for(int i = 1; i < count; i++) {			
			double x = initial * (1 - Math.pow(2, -i)) * 2;
			double width = initial * Math.pow(2, -i);

			rectangles.add(new Rectangle(bounds.x + initial + (int) x, bounds.y, (int) width, bounds.height));
		}
		
		return rectangles;
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(this.isHidden() || this.getBounds() == null) {
			return;
		}
		
		if(this.doubleFocusSystem) {
			super.handleEvent(event);
		}
		
		if(event instanceof WheelEvent) {
			WheelEvent wheel = (WheelEvent) event;
			
			if(this.globalWheelListening || this.getBounds().contains(wheel.getPoint())) {
				if(this.deltaX + wheel.getDelta() < 0) {
					this.deltaX = 0;
				} else if(this.deltaX + wheel.getDelta() >= this.getComponents().size()) {
					this.deltaX = this.getComponents().size() - 1;
				} else {
					this.deltaX += wheel.getDelta();
				}
			}
		}
	}
	
	public List<Class<? extends Event>> getHandleableEvents() {
		List<Class<? extends Event>> theList = new ArrayList<>();
		
		theList.addAll(super.getHandleableEvents());
		theList.add(WheelEvent.class);
		
		return theList;
	}
}