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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.ActionEvent;
import ch.innovazion.arionide.events.ActionType;
import ch.innovazion.arionide.events.ClickEvent;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.InvalidateLayoutEvent;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.animations.Animation;
import ch.innovazion.arionide.ui.animations.FieldModifierAnimation;
import ch.innovazion.arionide.ui.overlay.AlphaLayer;
import ch.innovazion.arionide.ui.overlay.AlphaLayeringSystem;
import ch.innovazion.arionide.ui.overlay.Component;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.render.PrimitiveFactory;
import ch.innovazion.arionide.ui.render.Shape;
import ch.innovazion.arionide.ui.render.UILighting;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;
import ch.innovazion.arionide.ui.topology.Scalar;
import ch.innovazion.arionide.ui.topology.Size;
import ch.innovazion.arionide.ui.topology.Translation;

public class Tab extends MultiComponent implements EventHandler {
		
	private static final int ANIMATION_TIME = 500;
		
	private final Animation animation;
	private final Shape borders;
	
	protected final List<Bounds> rectangles = new LinkedList<>();
	
	protected float shadow = 0.0f;
	protected int activeComponent = 0;
	private boolean renderSeparators;
	private String signal;
	private int alpha = ApplicationTints.INACTIVE_ALPHA;
	
	public Tab(View parent, String... tabs) {
		this(parent, makeLabels(parent, tabs));
	}
	
	public Tab(View parent, List<Component> components) {
		super(parent, components);
				
		this.animation = new FieldModifierAnimation(parent.getAppManager(), "shadow", Tab.class, this);
		
		this.getAppManager().getEventDispatcher().registerHandler(this);
		
		this.borders = PrimitiveFactory.instance().newRectangle(ApplicationTints.MAIN_COLOR, ApplicationTints.INACTIVE_ALPHA);
	}
	
	public void load() {
		this.borders.prepare();
		
		for(Component component : this.getComponents()) {
			if(component != null) {
				component.load();
			}
		}
	}
	
	public Tab setBounds(Bounds bounds) {
		super.setBounds(bounds);
		this.borders.updateBounds(bounds);
		return this;
	}
	
	public Tab setColor(int rgb) {
		this.borders.updateRGB(rgb);
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
	
	public Tab setShadowRadius(float radius) {
		this.borders.updateLightRadius(radius);
		
		for(Component component : this.getComponents()) {
			if(component instanceof Enlightenable) {
				for(UILighting primitive : ((Enlightenable) component).getEnlightenablePrimitives()) {
					primitive.updateLightRadius(radius);
				}
			}
		}
		
		return this;
	}
	
	public Tab setActiveComponent(int id) {
		if(id >= 0 && id < getComponents().size()) {
			this.activeComponent = id;
		}
		
		return this;
	}
	
	public void setComponents(List<Component> components) {
		super.setComponents(components);
		this.updateAll();
		
		if(components.size() > 0) {
			if(this.signal != null) {
				this.getAppManager().getEventDispatcher().fire(new ClickEvent(this, this.signal, this.activeComponent));
			}
		}
	}
	
	public void setComponents(String... tabs) {
		this.setComponents(makeLabels(this.getParent(), tabs));
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	protected void componentWillAppear() {		
		updateAll();
	}

	public void drawSurface(AppDrawingContext context) {
		List<Component> components = this.getComponents();
		Bounds bounds = this.getBounds();
		
		AlphaLayeringSystem layering = this.getAppManager().getAlphaLayering();
		
		float lightStrength = layering.getCurrentAlpha() / 255.0f;
		float y = bounds.getCenter().getY();
		
		layering.push(AlphaLayer.CONTAINER, this.alpha);
		
		this.borders.updateLightStrength(lightStrength);
		this.borders.updateAlpha(layering.getCurrentAlpha());
		this.borders.updateLightCenter(new Point(this.shadow, y));
		
		if(components.size() > 0) {
			getParent().getPreferedRenderingSystem(context).renderLater(this.borders);
		}

		int i = 0;
		
		synchronized(this.rectangles) {
			if(rectangles.size() == components.size()) {
				for(Component component : components) {
					Bounds rect = this.rectangles.get(i++);

					if(component != null) {						
						if(rect.getWidth() > 0.0f) {
							component.setBounds(rect);
							
							if(component instanceof Enlightenable) {
								for(UILighting primitive : ((Enlightenable) component).getEnlightenablePrimitives()) {
									primitive.updateLightStrength(lightStrength);
									primitive.updateLightCenter(new Point(this.shadow, y));
								}
							}
							
							component.drawSurface(context);
							
							try {
								Bounds next = this.rectangles.get(i);
									
								if(next.getWidth() > 0 && this.renderSeparators) {
									// TODO
									// context.getPrimitives().drawLine(new Line(next.getX(), next.getY(), next.getX(), next.getY() + next.getHeight()));
								}
							} catch(Exception e) {
								break;
							}
						}
					}
				}
			}
		}
		
		this.getAppManager().getAlphaLayering().pop(AlphaLayer.CONTAINER);
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
			
			Size size = this.getBounds().getSize();
			Scalar scalar = new Scalar(1.0f / this.getComponents().size(), 1.0f);
			scalar.apply(size);
			
			Bounds bounds = new Bounds(this.getBounds().getOrigin(), size);
			
			Translation translation = new Translation(size.getWidth(), 0.0f);
			
			for(int i = 0; i < count; i++) {
				this.rectangles.add(bounds.copy());
				translation.apply(bounds);
			}
		}
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(!isEnabled() || !isVisible() || getBounds() == null) {
			return; // Abort event if the button is not supposed to handle it.
		}
		
		if(event instanceof ActionEvent) {
			ActionEvent action = (ActionEvent) event;

			if(this.getBounds().contains(action.getPoint()) && action.getType().equals(ActionType.PRESS)) {
				this.updateAll();
				
				int target = this.getTarget(action.getPoint(), 0, this.rectangles.size() - 1, this.rectangles);
				
				if(target != -666) {
					this.activeComponent = target;
					
					float center = this.rectangles.get(this.activeComponent).getCenter().getX();
					
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
	
	public void updateAll() {
		this.compute();

		
		if(activeComponent >= 0 && activeComponent < rectangles.size() && rectangles.size() > 0) {
			Bounds rect = this.rectangles.get(this.activeComponent);
			this.shadow = rect.getCenter().getX();
			
			if(rect.getWidth() > 0) {
				this.setShadowRadius(rect.getWidth());
			}
		}
	}
	
	// Dichotomy algorithm
	private int getTarget(Point point, int index, int size, List<Bounds> rectangles) {
		int middle = (index + size) / 2;
		Bounds middleRect = rectangles.get(middle);
		
		if(middleRect.contains(point)) {
			return middle;
		} else if(point.getX() - middleRect.getCenter().getX() > 0.0D) {
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

	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(ActionEvent.class, InvalidateLayoutEvent.class);
	}
	
	protected static List<Component> makeLabels(Container parent, String[] tabs) {
		List<Component> labels = new ArrayList<>();
		
		for(String tab : tabs) {
			if(tab != null) {
				Label label = new Label(parent, tab);
				label.load();
				labels.add(label);
			} else {
				labels.add(null);
			}
		}
				
		return labels;
	}
}