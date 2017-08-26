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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.ui.overlay.components;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.ClickEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.overlay.AlphaLayer;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public class Tab extends MultiComponent implements EventHandler {
		
	private static final int ANIMATION_TIME = 500;
	
	protected final TabDesign design;
	
	private final Animation animation;
	protected final List<Rectangle2D> rectangles = Collections.synchronizedList(new ArrayList<>());
	
	protected double shadow = 0;
	protected int activeComponent = 0;
	private int rgb = 0xCAFE;
	private int alpha = Button.DEFAULT_ALPHA;
	private boolean renderSeparators;
	private String signal;
	private double shadingRadius;
	
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
				
		this.animation = new FieldModifierAnimation(parent.getAppManager(), "shadow", Tab.class, this);
		
		this.getAppManager().getEventDispatcher().registerHandler(this);
	}
	
	public Tab setColor(int rgb) {
		this.rgb = rgb;
		return this;
	}

	public Tab setAlpha(int alpha) {
		Utils.checkColorRange("Alpha", alpha);
		this.alpha = alpha;
		return this;
	}
	
	public Tab setSeparatorsRenderable(boolean yes) {
		this.renderSeparators = yes;
		return this;
	}
	
	public Tab setSignal(String signal) {
		this.signal = signal;
		return this;
	}
	
	public Tab setShadowRadius(double radius) {
		this.shadingRadius = radius;
		return this;
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	public void show() {
		super.show();
		this.updateAll();
	}

	public void drawSurface(AppDrawingContext context) {
		List<Component> components = this.getComponents();
		Rectangle2D bounds = this.getBounds();
		
		this.getAppManager().getAlphaLayering().push(AlphaLayer.COMPONENT, this.alpha);
		context.setColor(this.rgb);

		this.design.enterDesignContext(this.getAppManager(), new Point2D.Double(this.shadow, bounds.getCenterY()), this.shadingRadius);
		
		context.getPrimitives().drawRoundRect(context, bounds);

		int i = 0;
		
		synchronized(this.rectangles) {
			for(Component component : components) {
				Rectangle2D rect = this.rectangles.get(i++);
					
				if(rect.getWidth() > 0) {
					component.setLayoutBounds(rect);				
					component.drawSurface(context);
						
					try {
						Rectangle2D next = this.rectangles.get(i);
							
						if(next.getWidth() > 0 && this.renderSeparators) {
							context.getPrimitives().drawLine(context, next.getX(), next.getY(), next.getX(), next.getY() + next.getHeight());
						}
					} catch(Exception e) {
						break;
					}
				}
			}
		}
		
		this.design.exitDesignContext(this.getAppManager());
		this.getAppManager().getAlphaLayering().pop(AlphaLayer.COMPONENT);
	}
	
	public void update() {
		super.update();
		
		if(this.animation.isAnimating()) {
			this.compute();
		}
	}
	
	protected void compute() {
		synchronized(this.rectangles) {
			this.rectangles.clear();
						
			int count = this.getComponents().size();
			
			double x = this.getBounds().getX();
			double y = this.getBounds().getY();
			double width = this.getBounds().getWidth() / 3;
			double height = this.getBounds().getHeight();
			
			for(int i = 0; i < count; i++) {
				this.rectangles.add(new Rectangle2D.Double(x, y, width, height));
				x += width;
			}
		}
	}
	
	public void setComponents(List<Component> components) {
		super.setComponents(components);
		
		this.activeComponent = 0;
		this.updateAll();
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(this.isHidden() || this.getBounds() == null) {
			return;
		}
		
		if(event instanceof ActionEvent) {
			ActionEvent action = (ActionEvent) event;

			if(this.getBounds().contains(action.getPoint()) && action.getType().equals(ActionType.PRESS)) {
				this.updateAll();
				
				int target = this.getTarget(action.getPoint(), 0, this.rectangles.size() - 1, this.rectangles);
				
				if(target != -666) {
					this.activeComponent = target;
					
					double center = this.rectangles.get(this.activeComponent).getCenterX();
					
					if(this.shadow != center) {
						this.animation.startAnimation(ANIMATION_TIME, center);
							
						if(this.signal != null) {
							this.getAppManager().getEventDispatcher().fire(new ClickEvent(this, this.signal, target));
						}
					}
				}
			}
		} else if(event instanceof InvalidateLayoutEvent) {
			this.updateAll();
		}
	}
	
	protected void updateAll() {
		this.compute();

		if(this.rectangles.size() > 0) {
			Rectangle2D rect = this.rectangles.get(this.activeComponent);
			this.shadow = rect.getCenterX();
			
			if(rect.getWidth() > 0) {
				this.shadingRadius = rect.getWidth();
			}
		}
	}
	
	// Dichotomy algorithm
	private int getTarget(Point2D point, int index, int size, List<Rectangle2D> rectangles) {
		int middle = (index + size) / 2;
		Rectangle2D middleRect = rectangles.get(middle);
		
		if(middleRect.contains(point)) {
			return middle;
		} else if(point.getX() - middleRect.getCenterX() > 0.0D) {
			if(middle < size) {
				return this.getTarget(point, middle + 1, size, rectangles);
			}
		} else {
			if(middle > 0) {
				return this.getTarget(point, 0, middle - 1, rectangles);
			}
		}
		
		return -666; // error
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ActionEvent.class, InvalidateLayoutEvent.class);
	}
	
	protected static List<Component> makeLabels(View parent, String[] tabs) {
		List<Component> labels = new ArrayList<>();
		
		for(String tab : tabs) {
			labels.add(new Label(parent, tab).setAlpha(0)); // Let the TabDesign handle the color
		}
		
		return labels;
	}
}