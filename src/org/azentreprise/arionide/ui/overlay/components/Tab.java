package org.azentreprise.arionide.ui.overlay.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.ClickEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public class Tab extends MultiComponent implements EventHandler {
	
	public static final String VALUE_CHANGED_EVENT_IDENTIFIER = "tabValueChanged";
	
	private static final int ANIMATION_TIME = 500;
	
	protected final TabDesign design;	
	protected float activeComponent = 0;

	private final Animation animation;
	
	private String signal;
	
	public Tab(View parent, String... tabs) {
		this(parent, makeLabels(parent, tabs));
	}
	
	public Tab(View parent, List<Component> components) {
		super(parent, components);
		
		AppDrawingContext context = parent.getAppManager().getDrawingContext();
		
		if(context instanceof AWTDrawingContext) {
			this.design = new AWTTabDesign();
		} else if(context instanceof OpenGLDrawingContext) {
			this.design = new OpenGLTabDesign();
		} else {
			this.design = null;
		}
		
		this.animation = new FieldModifierAnimation(parent.getAppManager(), "activeComponent", Tab.class, this);
		
		this.getParentView().getAppManager().getEventDispatcher().registerHandler(this);
	}
	
	public Tab setSignal(String signal) {
		this.signal = signal;
		return this;
	}
	
	public boolean isFocusable() {
		return true;
	}

	public void drawSurface(AppDrawingContext context) {
		Rectangle bounds = (Rectangle) this.getBounds().clone();
		List<Component> components = this.getComponents();
				
		context.setDrawingColor(new Color(0x6000CAFE, true)); // there's a lot of coffee right there =P
	    
	    double center = bounds.getX() + (this.activeComponent + 0.5f) * bounds.getWidth() / components.size();
	    this.design.createDesign(context, new Point2D.Double(center, bounds.getCenterY()), bounds.width / components.size() / 2);
		
		context.getPrimitives().drawRoundRect(context, bounds);

		int i = 0;
		
		for(Component component : components) {
			
			bounds.x = this.getX(i);
			bounds.width = this.getWidth(i++);
			
			if(bounds.width > 0) {
				component.setLayoutBounds(bounds);
				component.drawSurface(context);
				
				
				if(component != components.get(components.size() - 1) && this.getWidth(i) > 0) {
					bounds.x += bounds.width;
					context.getPrimitives().drawLine(context, bounds.x, bounds.y + 1, bounds.x, bounds.y + bounds.height - 2);
				}
			}
		}
	}
	
	protected int getX(int id) {
		return id * this.getBounds().width / this.getComponents().size();
	}
	
	protected int getWidth(int id) {
		return this.getBounds().width / this.getComponents().size();
	}

	public <T extends Event> void handleEvent(T event) {
		if(this.isHidden() || this.getBounds() == null) {
			return;
		}
		
		if(event instanceof ActionEvent) {
			ActionEvent action = (ActionEvent) event;
			
			if(this.getBounds().contains(action.getPoint()) && action.getType().equals(ActionType.PRESS)) {
				int target = this.getComponents().size() * (action.getPoint().x - this.getBounds().x) / this.getBounds().width;
				
				if((int) this.activeComponent != target) {
					this.animation.startAnimation(ANIMATION_TIME, target);

					if(this.signal != null) {
						this.getParentView().getAppManager().getEventDispatcher().fire(new ClickEvent(this, this.signal, (int) this.activeComponent));
					}
				}
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ActionEvent.class);
	}
	
	protected static List<Component> makeLabels(View parent, String[] tabs) {
		List<Component> labels = new ArrayList<>();
		
		for(String tab : tabs) {
			labels.add(new Label(parent, tab).setOpacity(0)); // Let the tab design handle the color
		}
		
		return labels;
	}
}