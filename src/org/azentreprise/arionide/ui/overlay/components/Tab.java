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

import java.awt.Color;
import java.awt.Point;
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
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public class Tab extends MultiComponent implements EventHandler {
		
	private static final int ANIMATION_TIME = 500;
	
	protected final TabDesign design;
	
	protected double activeComponent = 0;

	private final Animation animation;
	
	private Color color = new Color(0x6000CAFE, true); // that's a lot of coffee =P
	private boolean renderSeparators;
	private String signal;
	private int shadingRadius;
	
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
	
	public Tab setColor(int color) {
		this.color = new Color(color, true);
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
	
	public Tab setShadowRadius(int radius) {
		this.shadingRadius = radius;
		return this;
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	public void show() {
		super.show();
		this.update();
	}

	public void drawSurface(AppDrawingContext context) {
		List<Component> components = this.getComponents();
		List<Rectangle> rectangles = this.computeBounds();
		Rectangle bounds = this.getBounds();
		
		context.setDrawingColor(this.color);
	    
	    this.design.createDesign(context, new Point2D.Double(this.activeComponent, bounds.getCenterY()), this.shadingRadius);
		
		context.getPrimitives().drawRoundRect(context, bounds);

		int i = 0;
		
		for(Component component : components) {
			Rectangle rect = rectangles.get(i++);
			
			if(rect.width > 0) {
				component.setLayoutBounds(rect);				
				component.drawSurface(context);
				
				try {
					Rectangle next = rectangles.get(i);
					
					if(next.width > 0 && this.renderSeparators) {
						context.getPrimitives().drawLine(context, next.x, next.y + 1, next.x, next.y + next.height - 2);
					}
				} catch(Exception e) {
					break;
				}
			}
		}
	}
	
	protected List<Rectangle> computeBounds() {
		List<Rectangle> rectangles = new ArrayList<>();
		Rectangle reference = (Rectangle) this.getBounds().clone();
		
		int count = this.getComponents().size();
		
		reference.width /= count;
		
		for(int i = 0; i < count; i++) {
			rectangles.add((Rectangle) reference.clone());
			reference.x += reference.width;
		}
		
		return rectangles;
	}

	public <T extends Event> void handleEvent(T event) {
		if(this.isHidden() || this.getBounds() == null) {
			return;
		}
		
		if(event instanceof ActionEvent) {
			ActionEvent action = (ActionEvent) event;

			if(this.getBounds().contains(action.getPoint()) && action.getType().equals(ActionType.PRESS)) {
				List<Rectangle> rectangles = this.computeBounds();

				int target = this.getTarget(action.getPoint(), 0, rectangles.size() - 1, rectangles);
				
				if(target != -666) {
					double center = rectangles.get(target).getCenterX();
					
					if(this.activeComponent != center) {
						this.animation.startAnimation(ANIMATION_TIME, center);
	
						if(this.signal != null) {
							this.getParentView().getAppManager().getEventDispatcher().fire(new ClickEvent(this, this.signal, target));
						}
					}
				}
			}
		} else if(event instanceof InvalidateLayoutEvent) {
			this.update();
		}
	}
	
	protected void update() {
		List<Rectangle> rectangles = this.computeBounds();
		
		if(rectangles.size() > 0) {
			Rectangle first = rectangles.get(0);
			this.activeComponent = first.getCenterX();
			
			if(first.width > 0) {
				this.shadingRadius = first.width;
			}
		}
	}
	
	// Dichotomy algorithm
	private int getTarget(Point point, int index, int size, List<Rectangle> rectangles) {
		int middle = (index + size) / 2;
		Rectangle middleRect = rectangles.get(middle);
		
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
			labels.add(new Label(parent, tab).setOpacity(0)); // Let the tab design handle the color
		}
		
		return labels;
	}
}