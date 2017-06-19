package org.azentreprise.arionide.ui.overlay.components;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.WheelEvent;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public class Scroll extends Tab {
	
	private boolean doubleFocusSystem = false;
	private int deltaX = 0;
	
	public Scroll(View parent, String... labels) {
		this(parent, Tab.makeLabels(parent, labels));
		super.setSeparatorsRenderable(false);
	}
	
	public Scroll(View parent, List<Component> components) {
		super(parent, components);
	}
	
	public Scroll setDoubleFocusSystemState(boolean enabled) {
		this.doubleFocusSystem = enabled;
		return this;
	}
	
	public void drawSurface(AppDrawingContext context) {
		super.drawSurface(context);
	}
	
	public final Tab setSeparatorsRenderable(boolean yes) {
		return this; // Ignore
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
			
			if(this.getBounds().contains(wheel.getPoint())) {
				if(this.deltaX + wheel.getDelta() >= 0 && this.deltaX + wheel.getDelta() < this.getComponents().size()) {
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