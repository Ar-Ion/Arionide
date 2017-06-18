package org.azentreprise.arionide.ui.overlay.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.WheelEvent;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public class Scroll extends Tab {
	
	private final int displayableComponents;
	
	private int deltaX = 0;
	
	public Scroll(View parent, int displayableComponents, String... labels) {
		this(parent, displayableComponents, Tab.makeLabels(parent, labels));
	}
	
	public Scroll(View parent, int displayableComponents, List<Component> components) {
		super(parent, components);
		
		this.displayableComponents = displayableComponents;
	}
	
	public void drawSurface(AppDrawingContext context) {
		super.drawSurface(context);
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(this.isHidden() || this.getBounds() == null) {
			return;
		}
		
		if(event instanceof WheelEvent) {
			WheelEvent wheel = (WheelEvent) event;
			
			if(this.getBounds().contains(wheel.getPoint())) {
				this.deltaX += wheel.getDelta();
			}
		}
	}
	
	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(WheelEvent.class);
	}
}