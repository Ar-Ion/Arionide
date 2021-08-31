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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.ActionEvent;
import ch.innovazion.arionide.events.ActionType;
import ch.innovazion.arionide.events.ClickEvent;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.FocusEvent;
import ch.innovazion.arionide.events.FocusGainedEvent;
import ch.innovazion.arionide.events.FocusLostEvent;
import ch.innovazion.arionide.events.MoveEvent;
import ch.innovazion.arionide.events.ValidateEvent;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.animations.Animation;
import ch.innovazion.arionide.ui.animations.FieldModifierAnimation;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.render.AffineTransformable;
import ch.innovazion.arionide.ui.render.PrimitiveFactory;
import ch.innovazion.arionide.ui.render.Shape;
import ch.innovazion.arionide.ui.render.UILighting;
import ch.innovazion.arionide.ui.topology.Bounds;

public class Button extends Label implements EventHandler, Deformable {
		
	private static final int ANIMATION_TIME = 200;
	
	private final Shape borders = PrimitiveFactory.instance().newRectangle(ApplicationTints.MAIN_COLOR, ApplicationTints.INACTIVE_ALPHA);
	private final int inactiveAlpha;
	protected final Animation animation;
	
	protected boolean hasFocus;
	
	private boolean hasBorders = true;
	private boolean pressed = false;
	private boolean mouseOver = false;
	
	private int color = ApplicationTints.MAIN_COLOR;
	
	private ClickEvent event;
	
	public Button(Container container, String label) {
		this(container, label, ApplicationTints.INACTIVE_ALPHA);
	}
	
	public Button(Container parent, String label, int inactiveAlpha) {
		super(parent, label);
				
		this.inactiveAlpha = inactiveAlpha;
		this.animation = new FieldModifierAnimation(this.getAppManager(), "alpha", Label.class, this);
		
		setAlpha(inactiveAlpha);
		
		getAppManager().getEventDispatcher().registerHandler(this, 0.8f);
	}
	
	public void load() {
		super.load();
		borders.prepare();
	}
	
	public Button setBounds(Bounds bounds) {
		super.setBounds(bounds);
		borders.updateBounds(bounds);
		return this;
	}
	
	public Button setColor(int rgb) {
		super.setColor(this.color = rgb);
		return this;
	}
	
	public Button pushTemporaryColor(int rgb) {
		super.setColor(rgb);
		return this;
	}
	
	public Button popTemporaryColor() {
		super.setColor(this.color);
		return this;
	}
	
	public Button setSignal(String signal, Object... data) {
		this.event = new ClickEvent(this, signal, data);
		return this;
	}
	
	public Button setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if(enabled) {
			setAlpha(inactiveAlpha);
			
			if(hasFocus) {
				getAppManager().getFocusManager().next();
			}
		} else {
			onFocusLost();
		}
				
		return this;
	}
	
	public Button setBordered(boolean bordered) {
		this.hasBorders = bordered;
		return this;
	}
		
	public void drawComponent(AppDrawingContext context) {
		super.drawComponent(context);
		this.drawBorders(context);
	}
	
	protected void drawBorders(AppDrawingContext context) {
		if(this.hasBorders) {
			this.borders.updateAlpha(getAppManager().getAlphaLayering().getCurrentAlpha());
			getParent().getPreferedRenderingSystem(context).renderLater(borders);
		}
	}
	
	public boolean isFocusable() {
		return isEnabled() && isVisible();
	}
	
	public boolean isPressed() {
		return pressed;
	}

	public <T extends Event> void handleEvent(T event) {	
		if(!isEnabled() || !isVisible() || getBounds() == null) {
			return; // Abort event if the button is not supposed to handle it.
		}
		
		if(event instanceof MoveEvent) {
			MoveEvent casted = (MoveEvent) event;
						
			if(getBounds().contains(casted.getPoint())) {
				if(!mouseOver) {
					mouseOver = true;
					
					if(!hasFocus) {
						animation.startAnimation(ANIMATION_TIME, 0xFF);
					}
				}
			} else {
				if(mouseOver) {
					mouseOver = false;
					
					if(!hasFocus) {
						animation.startAnimation(ANIMATION_TIME, inactiveAlpha);
					}
				}
			}
		} else if(event instanceof ActionEvent) {
			ActionEvent casted = (ActionEvent) event;
			
			if(getBounds().contains(casted.getPoint())) {
				if(casted.getType().equals(ActionType.PRESS)) {
					pressed = true;
					fireMouseClick();
				} else if(casted.getType().equals(ActionType.RELEASE)) {
					pressed = false;
				}
			}
		} else if(event instanceof ValidateEvent) {
			if(hasFocus) {
				fireMouseClick();
			}
		} else if(event instanceof FocusEvent) {
			if(((FocusEvent) event).isTargetting(this)) {
				if(event instanceof FocusGainedEvent) {
					onFocusGained();
				} else if(event instanceof FocusLostEvent) {
					onFocusLost();
				}
			}
		}
	}
	
	protected void fireMouseClick() {
		if(event != null) {
			getAppManager().getEventDispatcher().fire(event);
		}
	}
	
	protected void onFocusGained() {
		hasFocus = true;
		animation.startAnimation(ANIMATION_TIME, 0xFF);
	}
	
	protected void onFocusLost() {
		hasFocus = false;
		animation.startAnimation(ANIMATION_TIME, inactiveAlpha);
	}

	public List<UILighting> getEnlightenablePrimitives() {
		return Stream.concat(super.getEnlightenablePrimitives().stream(), Stream.of(this.borders)).collect(Collectors.toList());
	}
	
	public List<AffineTransformable> getDeformablePrimitives() {
		return Arrays.asList(this.borders);
	}
	
	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(MoveEvent.class, ActionEvent.class, FocusGainedEvent.class, FocusLostEvent.class, ValidateEvent.class);
	}
}