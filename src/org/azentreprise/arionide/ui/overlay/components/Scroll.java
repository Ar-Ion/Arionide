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
package org.azentreprise.arionide.ui.overlay.components;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.DragEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.events.ScrollEvent;
import org.azentreprise.arionide.events.WheelEvent;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public class Scroll extends Tab {
	
	private static final int MOUSE_DRAG_SENSIBILITY = 1;
	private static final double MOUSE_DRAG_ACCELERATION = 1.2d;

	private boolean doubleFocusSystem = false;
	private boolean globalWheelListening = true;
	
	private int anchor = 0;
	
	public Scroll(View parent, String... labels) {
		this(parent, Tab.makeLabels(parent, labels));
		
		this.setAlpha(0);
		this.setSeparatorsRenderable(false);
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

	protected void compute() {
		synchronized(this.rectangles) {
			this.rectangles.clear();
			
			Rectangle2D bounds = this.getBounds();
			int count = this.getComponents().size();
			
			double initial = bounds.getWidth() / 3;
			double initialHeight = bounds.getHeight();
			
			for(int i = -this.activeComponent; i < 1; i++) {
				double power = Math.pow(2, i);
				double width = initial * power;
				double height = initialHeight * power;
				
				this.rectangles.add(new Rectangle2D.Double(bounds.getX() + width, bounds.getCenterY() - height / 2, width, height));
			}
						
			for(int i = 1; i < count; i++) {
				double power = Math.pow(2, -i);
				double x = initial * (1 - power) * 2;
				double width = initial * power;
				double height = initialHeight * power;
	
				this.rectangles.add(new Rectangle2D.Double(bounds.getX() + initial + x, bounds.getCenterY() - height / 2, width, height));
			}
		}
	}
	
	protected void updateAll() {
		super.updateAll();
		
		if(this.getBounds().getWidth() > 0) {
			this.shadow = this.getBounds().getCenterX();
			this.setShadowRadius(this.getBounds().getWidth() / 2);
		}
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
				this.commitDelta(this.activeComponent + (int) wheel.getDelta());
			}
		} else if(event instanceof ActionEvent) {
			ActionEvent action = (ActionEvent) event;
			
			if(action.getType().equals(ActionType.PRESS) && this.getBounds().contains(action.getPoint())) {
				this.anchor = this.activeComponent;
			}
		} else if(event instanceof DragEvent) {
			DragEvent drag = (DragEvent) event;
			
			if(this.getBounds().contains(drag.getAnchor())) {				
				this.commitDelta((int) (this.anchor - 2 * MOUSE_DRAG_SENSIBILITY * this.getComponents().size() / this.getBounds().getWidth() * (int) Utils.fakeComplexPower(drag.getDeltaX(), MOUSE_DRAG_ACCELERATION)));
			}
		} else if(event instanceof InvalidateLayoutEvent) {
			super.handleEvent(event);
		}
	}
	
	private void commitDelta(int delta) {
		if(delta < 0) {
			delta = 0;
		} else if(delta >= this.getComponents().size()) {
			delta = this.getComponents().size() - 1;
		}
		
		if(this.activeComponent != delta) {
			this.activeComponent = delta;
			
			this.getAppManager().getEventDispatcher().fire(new ScrollEvent(this, this.activeComponent));
			
			this.updateAll();
		}
	}
	
	public List<Class<? extends Event>> getHandleableEvents() {
		List<Class<? extends Event>> theList = new ArrayList<>();
		
		theList.addAll(super.getHandleableEvents());
		theList.add(WheelEvent.class);
		theList.add(ActionEvent.class);
		theList.add(DragEvent.class);
		
		return theList;
	}
}