package org.azentreprise.arionide.ui.overlay.components;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.primitives.RoundRectangle;

public class Tab extends Component implements EventHandler {
	
	private final Label[] labels;
	private int active = 0;

	public Tab(View parent, String[] tabs) {
		super(parent);
		
		assert tabs.length > 0;
		
		this.labels = new Label[tabs.length];
		
		for(int i = 0; i < tabs.length; i++) {
			this.labels[i] = new Label(parent, tabs[i]);
		}
		
		this.getParentView().getAppManager().getEventDispatcher().registerHandler(this);
	}

	public boolean isFocusable() {
		return true;
	}

	public void drawSurface(Graphics2D g2d, Rectangle bounds) {
		RoundRectangle.draw(g2d, bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
	}

	public <T extends Event> void handleEvent(T event) {
		
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return null;
	}
}