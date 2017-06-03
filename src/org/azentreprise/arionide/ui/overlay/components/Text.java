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
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import org.azentreprise.Arionide;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.ui.UIEvents;
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.animations.Animation;
import org.azentreprise.ui.animations.FieldModifierAnimation;

public class Text extends Button {	
	private final Animation animation = new FieldModifierAnimation("cursorOpacity", Text.class, this);
	
	protected String placeholder;
	protected StringBuilder text = new StringBuilder();
	
	protected int cursorPosition = 0;
	protected int cursorOpacity = 255;
	
	private long counter = 0L;
	protected boolean highlighted = false;
	
	public Text(View parent, String placeholder) {
		super(parent, placeholder);
		this.placeholder = placeholder;
	}
	
	public Text setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
		
		this.updateText();
		
		return this;
	}
	
	public Text setText(String text) {
		this.text = new StringBuilder(text);
		
		this.cursorPosition = this.text.length();
		
		this.updateText();
		
		return this;
	}
	
	protected String getText() {
		return this.text.toString();
	}
	
	protected String getPlaceholder() {
		return this.placeholder;
	}

	public void drawSurface(Graphics2D g2d, Rectangle bounds) {
		super.drawSurface(g2d, bounds);
		
		if(this.hasFocus && this.text.length() > 0) {
			FontMetrics metrics = g2d.getFontMetrics();
			
			int x = this.textRenderPosition.x + metrics.charsWidth(this.text.toString().toCharArray(), 0, this.cursorPosition);
			int y = bounds.height / 2;
			
			g2d.setColor(new Color(255, 255, 255, this.cursorOpacity));
			g2d.fillRoundRect(x, y - metrics.getAscent() / 2, 2, metrics.getAscent(), 2, 2);
		
			if(this.highlighted) {
				g2d.setColor(new Color(0x42C0FFEE, true));
				g2d.fillRect(this.textRenderPosition.x, y - metrics.getAscent() / 2, metrics.stringWidth(this.text.toString()), metrics.getAscent());
			}
		}
	}
	
	private void cursorAnimationOpacityIncrease() {
		this.animation.startAnimation(20, useless -> this.cursorAnimationOpacityDecrease(), 255);
	}
	
	private void cursorAnimationOpacityDecrease() {
		this.animation.startAnimation(20, useless -> this.cursorAnimationOpacityIncrease(), 0);
	}
	
	public boolean handleKeyboardEvent(int code, char ch, int modifiers) {
		if(this.hasFocus) {
			
			boolean noSeek = code != KeyEvent.VK_LEFT && code != KeyEvent.VK_RIGHT;
			
			if(this.highlighted && noSeek) {
				this.text = new StringBuilder();
				this.cursorPosition = 0;
				this.highlighted = false;
			}
			
			this.dispatch(code, ch, modifiers, !noSeek);
			
			if(this.cursorPosition < 0) {
				this.cursorPosition = 0;
			} else if(this.cursorPosition > this.text.length()) {
				this.cursorPosition = this.text.length();
			}
			
			if(noSeek) {
				this.updateText();
			}
			
			return false;
		} else {
			return true;
		}
	}
	
	public boolean dispatch(int code, char ch, int modifiers, boolean seek) {
		if(code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE) {
			this.dispatchDeletion(code, modifiers > 0);
		} else if(seek) {
			this.dispatchSeek(code);
		} else if(Arionide.getSystemFont().canDisplay(ch) && code != KeyEvent.VK_TAB && code != KeyEvent.VK_ENTER && code != KeyEvent.VK_ESCAPE) {
			this.text.insert(this.cursorPosition, ch);
			this.cursorPosition++;
		} else {
			return true;
		}
		
		return false;
	}
	
	public void dispatchDeletion(int code, boolean strong) {
		if(code == KeyEvent.VK_BACK_SPACE) {
			if(strong) {
				this.text.delete(0, this.cursorPosition);
				this.cursorPosition = 0;
			} else if(this.text.length() > 0 && this.cursorPosition > 0) {
				this.text.deleteCharAt(this.cursorPosition - 1);
				this.cursorPosition--;
			}
		} else if(code == KeyEvent.VK_DELETE) {
			if(strong) {
				this.text.delete(this.cursorPosition, this.text.length());
			} else if(this.text.length() > this.cursorPosition) {
				this.text.deleteCharAt(this.cursorPosition);
			}
		}
	}
	
	public void dispatchSeek(int code) {
		if(code == KeyEvent.VK_LEFT) {
			if(this.highlighted) {
				this.cursorPosition = 0;
			} else {
				this.cursorPosition--;
			}
		} else if(code == KeyEvent.VK_RIGHT) {
			if(this.highlighted) {
				this.cursorPosition = this.text.length();
			} else {
				this.cursorPosition++;
			}
		}
	}
	
	public void focusGained() {
		super.focusGained();
		this.cursorAnimationOpacityDecrease();
	}
	
	protected void updateText() {
		this.setLabel(this.text.length() > 0 ? this.text.toString() : this.placeholder);
	}
	
	public void handleMouseEvent(byte event) {		
		switch(event) {
			case UIEvents.EVENT_MOUSE_ENTER:
				UIMain.setFrameCursor(new Cursor(Cursor.TEXT_CURSOR));
				break;
			case UIEvents.EVENT_MOUSE_EXIT:
				UIMain.setFrameCursor(Cursor.getDefaultCursor());
				break;
			case UIEvents.EVENT_MOUSE_CLICK:
				if(System.currentTimeMillis() - this.counter < 400L) {
					this.highlighted = !this.highlighted;
				} else {
					this.counter = System.currentTimeMillis();
					this.highlighted = false;
				}
				
				break;
		}
	}
}
