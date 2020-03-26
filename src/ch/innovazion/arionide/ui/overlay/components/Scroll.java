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
package ch.innovazion.arionide.ui.overlay.components;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.DragEvent;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.InvalidateLayoutEvent;
import ch.innovazion.arionide.events.ScrollEvent;
import ch.innovazion.arionide.events.WheelEvent;
import ch.innovazion.arionide.ui.animations.Animation;
import ch.innovazion.arionide.ui.animations.ObjectModifierAnimation;
import ch.innovazion.arionide.ui.overlay.Component;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.topology.Bounds;

public class Scroll extends Tab {
	
	private static final int MOUSE_DRAG_SENSIBILITY = 1;
	private static final double MOUSE_DRAG_ACCELERATION = 1.2d;

	private boolean doubleFocusSystem = false;
	private boolean globalWheelListening = true;
	
	private int numElements = 0;
	private int cycle = 0;
	
	private Animation animation;
	
	private final List<Bounds> originalBounds = new LinkedList<>();
	
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
		synchronized(rectangles) {
			rectangles.clear();
			originalBounds.clear();
			
			Bounds bounds = getBounds();
			
			this.numElements = getComponents().size();
			
			float initialWidth = bounds.getWidth() / 3;
			float initialHeight = bounds.getHeight();
									
			for(int i = 0; i < 2 * numElements; i++) {
				float power = (float) Math.max((1.0d + Math.random()) * 0.01f, Math.pow(2, -Math.abs(i - numElements))); // The real way to mitigate a bug XD
				float width = initialWidth * power;
				float height = initialHeight * power;
								 
				Bounds elementBounds = new Bounds(bounds.getCenter().getX() + Math.signum(i - numElements) * 0.667f * (1 - power) - width/2, bounds.getCenter().getY() - height / 2, width, height);				
				originalBounds.add(elementBounds);
			}
		}
		
		IntStream.range(numElements - activeComponent, 2 * numElements - activeComponent).mapToObj(originalBounds::get).map(Bounds::copy).forEach(rectangles::add);
	
		animation = new ObjectModifierAnimation(getAppManager(), LinkedList.class, rectangles);
	}
	
	public void updateAll() {
		super.updateAll();
				
		if(this.getBounds().getWidth() > 0) {
			this.shadow = this.getBounds().getCenter().getX();
			this.setShadowRadius(this.getBounds().getWidth() / 2);
		}
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(!isEnabled() || !isVisible() || getBounds() == null) {
			return; // Abort event if the button is not supposed to handle it.
		}
		
		if(this.doubleFocusSystem) {
			super.handleEvent(event);
		}
		
		if(event instanceof WheelEvent) {
			WheelEvent wheel = (WheelEvent) event;
			
			if(this.globalWheelListening || this.getBounds().contains(wheel.getPoint())) {
				this.commitDelta((int) wheel.getDelta());
			}
		} else if(event instanceof DragEvent) {
			DragEvent drag = (DragEvent) event;
			
			if(this.getBounds().contains(drag.getAnchor())) {				
				this.commitDelta((int) (-2 * MOUSE_DRAG_SENSIBILITY * this.getComponents().size() / this.getBounds().getWidth() * (int) Utils.fakeComplexPower(drag.getDeltaX(), MOUSE_DRAG_ACCELERATION)));
			}
		} else if(event instanceof InvalidateLayoutEvent) {
			super.handleEvent(event);
		}
	}
	
	private void commitDelta(int delta) {
		activeComponent += delta;
		
		if(activeComponent >= numElements) {
			activeComponent = numElements - 1;
		}
		
		if(activeComponent < 0) {
			activeComponent = 0;
		}
		
		System.out.println(originalBounds.subList(numElements - activeComponent, 2 * numElements - activeComponent));
				
		animation.startAnimation(200, new LinkedList<>(originalBounds.subList(numElements - activeComponent, 2 * numElements - activeComponent)));
				
		if(delta != 0) {
			if(cycle >= 0) {
				this.getAppManager().getEventDispatcher().fire(new ScrollEvent(this, activeComponent - cycle));
			} else {
				this.getAppManager().getEventDispatcher().fire(new ScrollEvent(this, activeComponent));
			}
		}
	}
	
	public Tab setActiveComponent(int id) {
		return super.setActiveComponent(id + cycle);
	}
	
	public void setCyclicComponents(String... tabs) {
		if(tabs != null) {
			cycle = tabs.length;
			
			String[] resulting = new String[3 * cycle];
			
			System.arraycopy(tabs, 0, resulting, 0, cycle);
			System.arraycopy(tabs, 0, resulting, cycle, cycle);
			System.arraycopy(tabs, 0, resulting, 2 * cycle, cycle);
	
			setComponents(resulting);
			
			activeComponent = 0;
		} else {
			activeComponent = 0;
			cycle = 0;
		}
	}
	
	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.combine(super.getHandleableEvents(), WheelEvent.class, DragEvent.class);
	}
}