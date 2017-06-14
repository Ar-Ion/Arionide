package org.azentreprise.arionide.ui.overlay.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.primitives.RoundRectangle;

public class Tab extends Component implements EventHandler {
	
	private final Label[] labels;
	private float active = 0;

	public Tab(View parent, String... tabs) {
		super(parent);
		
		assert tabs.length > 0;
		
		this.labels = new Label[tabs.length];
		
		for(int i = 0; i < tabs.length; i++) {
			this.labels[i] = new Label(parent, tabs[i]).setOpacity(0);
		}
		
		this.getParentView().getAppManager().getEventDispatcher().registerHandler(this);
	}

	public boolean isFocusable() {
		return true;
	}

	public void drawSurface(Graphics2D g2d, Rectangle bounds) {
		bounds = (Rectangle) bounds.clone();
		
		float[] dist = {0.0f, 1.0f / this.labels.length};
	    Color[] colors = {new Color(0xCAFE), new Color(0x6000CAFE, true)};
	    
	    double center = bounds.getX() + (this.active + 0.5f) * bounds.getWidth() / this.labels.length;
	    
	    g2d.setPaint(new RadialGradientPaint(new Point2D.Double(center, bounds.getCenterY()), (float) bounds.getWidth(), dist, colors));
		
		RoundRectangle.draw(g2d, bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
		
		bounds.width /= this.labels.length;

		for(Label label : this.labels) {
			label.drawSurface(g2d, bounds);
			
			if(label != this.labels[this.labels.length - 1]) {
				bounds.x += bounds.width;
				g2d.drawLine(bounds.x, bounds.y + 1, bounds.x, bounds.y + bounds.height - 2);
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
				double delta =  + this.getBounds().getWidth() / this.labels.length - (action.getPoint().getX() - this.getBounds().getX());
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ActionEvent.class);
	}
}