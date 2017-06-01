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

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.MoveEvent;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.ui.render.RoundRectRenderer;

public class Button extends Label implements EventHandler {
	
	private static final int ANIMATION_TIME = 300;
	
	protected final Animation animation;
	protected boolean hasFocus;
	
	private boolean disabled = false;
	
	private boolean mouseOver = false;
	private boolean mousePressed = false;
	
	private int colorKeepRef;
	private ClickListener listener;
	private Object[] signals;
	
	public Button(View parent, String label) {
		super(parent, label);
		
		this.setColor(0x6942CAFE);
		this.colorKeepRef = this.color;
		
		this.animation = new FieldModifierAnimation(this.getParentView().getAppManager(), "opacity", Label.class, this);
		
		this.getParentView().getAppManager().getEventDispatcher().registerHandler(this);
	}
	
	public Button setHandler(ClickListener listener, Object... signals) {
		this.listener = listener;
		this.signals = signals;
		return this;
	}
	
	public Button setDisabled(boolean disabled) {
		this.disabled = disabled;
		
		if(this.disabled) {
			this.colorKeepRef = this.color;
			this.setColor(0x63FF0000);
			
			if(this.hasFocus) {
				this.getParentView().nextFocus();
			}
		} else {
			this.setColor(this.colorKeepRef);
		}
				
		return this;
	}
	
	public void drawSurface(Graphics2D g2d) {
		super.drawSurface(g2d);
		Rectangle bounds = g2d.getClipBounds();
		RoundRectRenderer.draw(g2d, 0, 0, bounds.width - 1, bounds.height - 1);
	}
	
	public void focusGained() {
		this.hasFocus = true;
		this.animation.startAnimation(15, 0xFF);
	}
	
	public void focusLost() {
		this.hasFocus = false;
		this.animation.startAnimation(1000, this.color >>> 24);
	}
	
	public String toString() {
		return this.label;
	}
	
	public boolean isFocusable() {
		return !this.disabled;
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof MoveEvent) {
			MoveEvent casted = (MoveEvent) event;
			
			if(this.getBounds().contains(casted.getPoint())) {
				if(!this.mouseOver) {
					this.mouseOver = true;
					
					this.getParentView().getAppManager().getDrawingContext().setCursor(new Cursor(Cursor.HAND_CURSOR));

					if(!this.hasFocus) {
						this.animation.startAnimation(Button.ANIMATION_TIME, 0xFF);
					}
				}
			} else {
				if(this.mouseOver) {
					this.mouseOver = false;
					
					this.getParentView().getAppManager().getDrawingContext().setCursor(Cursor.getDefaultCursor());

					if(!this.hasFocus) {
						this.animation.startAnimation(Button.ANIMATION_TIME, this.color >>> 24);
					}
				}
			}
		} else if(event instanceof ActionEvent) {
			ActionEvent casted = (ActionEvent) event;
			
			if(this.getBounds().contains(casted.getPoint())) {
				switch(casted.getType()) {
					case CLICK:
						break;
					case PRESS:
						this.mousePressed = true;
						break;
					case RELEASE:
						this.mousePressed = false;
						
						if(this.listener != null) {
							this.listener.onClick(this.signals);
						}
						
						break;
				}
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(MoveEvent.class, ActionEvent.class);
	}
}