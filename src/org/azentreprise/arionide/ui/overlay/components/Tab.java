package org.azentreprise.arionide.ui.overlay.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public class Tab extends Component implements EventHandler {
	
	private final TabDesign design;
	
	private final Label[] labels;
	private float active = 0;

	public Tab(View parent, String... tabs) {
		super(parent);
		
		assert tabs.length > 0;
		
		AppDrawingContext context = parent.getAppManager().getDrawingContext();
		
		if(context instanceof AWTDrawingContext) {
			this.design = new AWTTabDesign();
		} else if(context instanceof OpenGLDrawingContext) {
			this.design = new OpenGLTabDesign();
		} else {
			this.design = null;
		}
		
		this.labels = new Label[tabs.length];
		
		for(int i = 0; i < tabs.length; i++) {
			this.labels[i] = new Label(parent, tabs[i]).setOpacity(0);
		}
		
		this.getParentView().getAppManager().getEventDispatcher().registerHandler(this);
	}

	public boolean isFocusable() {
		return true;
	}

	public void drawSurface(AppDrawingContext context) {
		Rectangle bounds = (Rectangle) this.getBounds().clone();
		
	    context.setDrawingColor(new Color(0x6000CAFE, true)); // there's a lot of coffee right there =P
	    
	    double center = bounds.getX() + (this.active + 0.5f) * bounds.getWidth() / this.labels.length;
	    this.design.createDesign(context, new Point2D.Double(center, bounds.getCenterY()), bounds.width / this.labels.length);
		
		context.getPrimitives().drawRoundRect(context, bounds);
		
		bounds.width /= this.labels.length;

		for(Label label : this.labels) {
			label.setLayoutBounds(bounds);
			label.drawSurface(context);
			
			
			if(label != this.labels[this.labels.length - 1]) {
				bounds.x += bounds.width;
				context.getPrimitives().drawLine(context, bounds.x, bounds.y + 1, bounds.x, bounds.y + bounds.height - 2);
			}
		}
	}

	public <T extends Event> void handleEvent(T event) {
		if(this.isHidden() || this.getBounds() == null) {
			return;
		}
		
		if(event instanceof ActionEvent) {
			ActionEvent action = (ActionEvent) event;
			
			if(this.getBounds().contains(action.getPoint()) && action.getType().equals(ActionType.PRESS)) {
				double delta = this.getBounds().getWidth() / this.labels.length - (action.getPoint().getX() - this.getBounds().getX());
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ActionEvent.class);
	}
}