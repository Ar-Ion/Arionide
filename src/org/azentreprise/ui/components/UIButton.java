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
package org.azentreprise.ui.components;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.azentreprise.ui.UIEvents;
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.animations.Animation;
import org.azentreprise.ui.animations.FieldModifierAnimation;
import org.azentreprise.ui.render.RoundRectRenderer;
import org.azentreprise.ui.views.UIView;

public class UIButton extends UILabel {
		
	protected final Animation animation = new FieldModifierAnimation("opacity", UILabel.class, this);
	protected boolean hasFocus;
	
	private boolean disabled = false;
	private int colorKeepRef;
	private UIButtonClickListener listener;
	private Object[] signals;
	
	public UIButton(UIView parent, float x, float y, float width, float height, String label) {
		super(parent, x, y, width, height, label);
		
		this.setColor(0x6942CAFE);
		this.colorKeepRef = this.color;
				
		UIEvents.registerMouseHandler(this);
	}
	
	public UIButton setHandler(UIButtonClickListener listener, Object... signals) {
		this.listener = listener;
		this.signals = signals;
		return this;
	}
	
	public UIButton setDisabled(boolean disabled) {
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
	
	public void draw(Graphics2D g2d) {
		super.draw(g2d);
		Rectangle bounds = g2d.getClipBounds();
		RoundRectRenderer.draw(g2d, 0, 0, bounds.width - 1, bounds.height - 1);
	}
	
	public void focusGained() {
		this.hasFocus = true;
		this.animation.startAnimation(15, 0xFF);
	}
	
	public void focusLost() {
		this.hasFocus = false;
		this.animation.startAnimation(15, this.color >>> 24);
	}

	public void handleMouseEvent(byte event) {
		if(!this.disabled) {
			switch(event) {
				case UIEvents.EVENT_MOUSE_ENTER:
					if(!this.hasFocus) {
						this.animation.startAnimation(15, 0xFF);
					}
					
					UIMain.setFrameCursor(new Cursor(Cursor.HAND_CURSOR));
					
					break;
				case UIEvents.EVENT_MOUSE_EXIT:
					if(!this.hasFocus) {
						this.animation.startAnimation(15, this.color >>> 24);
					}
					
					UIMain.setFrameCursor(Cursor.getDefaultCursor());
					
					break;
				case UIEvents.EVENT_MOUSE_CLICK:
					this.getParentView().requestFocus(this);
					
					if(this.listener != null) {
						this.listener.onClick(this.signals);
					}
			}
		}
	}
	
	public String toString() {
		return this.label;
	}
	
	public boolean isFocusable() {
		return !this.disabled;
	}
	
	public static interface UIButtonClickListener {
		public void onClick(Object... signals);
	}
}