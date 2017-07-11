/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.ui.overlay.components;

import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.WriteEvent;
import org.azentreprise.arionide.ui.AlphaLayer;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.overlay.View;

public class Text extends Button implements EventHandler {	
	
	private static final int CURSOR_BLINKING_PERIOD = 500;
	
	private final Animation animation;
	
	protected String placeholder;
	protected StringBuilder text = new StringBuilder();
	
	protected int cursorPosition = 0;
	protected int cursorAlpha = 255;
	
	private long counter = 0L;
	protected boolean highlighted = false;
	
	public Text(View parent, String placeholder) {
		super(parent, placeholder);
		
		this.placeholder = placeholder;
		this.animation = new FieldModifierAnimation(parent.getAppManager(), "cursorAlpha", Text.class, this);
		
		this.setOverCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
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
	
	public String getText() {
		return this.text.toString();
	}
	
	public String getPlaceholder() {
		return this.placeholder;
	}

	public void drawSurface(AppDrawingContext context) {
		super.drawSurface(context);
		
		if(this.hasFocus && this.text.length() > 0) {
			FontMetrics metrics = context.getFontAdapter().getLastMetrics();
			
			double x = this.textRenderPosition.getX() + metrics.charsWidth(this.text.toString().toCharArray(), 0, this.cursorPosition);
			double y = this.getBounds().getY() + this.getBounds().getHeight() / 2;
			
			this.getAppManager().getAlphaLayering().push(AlphaLayer.COMPONENT, this.cursorAlpha);
			context.setColor(0xFFFFFF);
			context.getPrimitives().fillRoundRect(context, new Rectangle2D.Double(x, y - metrics.getAscent() / 2, 2, metrics.getAscent()));
			this.getAppManager().getAlphaLayering().pop(AlphaLayer.COMPONENT);

			if(this.highlighted) {
				this.getAppManager().getAlphaLayering().push(AlphaLayer.COMPONENT, 0x42);
				
				Rectangle2D selection = new Rectangle2D.Double(this.textRenderPosition.getX(), y - metrics.getAscent() / 2, metrics.stringWidth(this.text.toString()), metrics.getAscent());
				context.setColor(0xC0FFEE);
				context.getPrimitives().fillRect(context, selection);
				
				this.getAppManager().getAlphaLayering().pop(AlphaLayer.COMPONENT);
			}
		}
	}
	
	private void cursorAnimationOpacityIncrease() {
		this.animation.startAnimation(CURSOR_BLINKING_PERIOD, useless -> this.cursorAnimationOpacityDecrease(), 255);
	}
	
	private void cursorAnimationOpacityDecrease() {
		this.animation.startAnimation(CURSOR_BLINKING_PERIOD, useless -> this.cursorAnimationOpacityIncrease(), 0);
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(this.isHidden() || this.getBounds() == null) {
			return;
		}
		
		super.handleEvent(event);
		
		if(this.hasFocus && (event instanceof WriteEvent)) {
			WriteEvent writeEvent = (WriteEvent) event;
			
			int code = writeEvent.getKeycode();
			
			boolean noSeek = code != KeyEvent.VK_LEFT && code != KeyEvent.VK_RIGHT;
			
			if(this.highlighted && noSeek) {
				this.text = new StringBuilder();
				this.cursorPosition = 0;
				this.highlighted = false;
			}
			
			this.dispatch(code, writeEvent.getChar(), writeEvent.getModifiers(), !noSeek);
			
			if(this.cursorPosition < 0) {
				this.cursorPosition = 0;
			} else if(this.cursorPosition > this.text.length()) {
				this.cursorPosition = this.text.length();
			}
			
			if(noSeek) {
				this.updateText();
			}
		}
	}
	
	public boolean dispatch(int code, char ch, int modifiers, boolean seek) {
		if(code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE) {
			this.dispatchDeletion(code, modifiers > 0);
		} else if(seek) {
			this.dispatchSeek(code);
		} else if(this.getAppManager().getDrawingContext().getFontAdapter().getLastMetrics().getFont().canDisplay(ch)) {
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
				this.highlighted = false;
			} else {
				this.cursorPosition--;
			}
		} else if(code == KeyEvent.VK_RIGHT) {
			if(this.highlighted) {
				this.cursorPosition = this.text.length();
				this.highlighted = false;
			} else {
				this.cursorPosition++;
			}
		}
	}
	
	protected void updateText() {
		this.setLabel(this.text.length() > 0 ? this.text.toString() : this.placeholder);
	}
	
	protected void onFocusGained() {
		super.onFocusGained();
		this.cursorAnimationOpacityDecrease();
	}
	
	protected void fireMouseClick() {
		super.fireMouseClick();
		
		if(System.currentTimeMillis() - this.counter < 400L) {
			this.highlighted = true;
		} else {
			this.counter = System.currentTimeMillis();
			this.highlighted = false;
		}
	}
	
	public List<Class<? extends Event>> getHandleableEvents() {
		List<Class<? extends Event>> theList = new ArrayList<>();
		
		theList.addAll(super.getHandleableEvents());
		theList.add(WriteEvent.class);
		
		return theList;
	}
}
