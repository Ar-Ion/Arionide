/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.WriteEvent;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLContext;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.overlay.AlphaLayer;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.render.font.GLFontRenderer;
import org.azentreprise.arionide.ui.render.font.Metrics;
import org.azentreprise.arionide.ui.render.font.TextTessellator;

public class Input extends Button implements EventHandler {	
	
	private static final int CURSOR_BLINKING_PERIOD = 500;
	
	private final Animation animation;
	
	protected String placeholder;
	protected StringBuilder text = new StringBuilder();
	
	protected int cursorPosition = 0;
	protected int cursorAlpha = 255;
	
	private long counter = 0L;
	protected boolean highlighted = false;
	
	public Input(View parent, String placeholder) {
		super(parent, placeholder);
		
		this.placeholder = placeholder;
		this.animation = new FieldModifierAnimation(parent.getAppManager(), "cursorAlpha", Input.class, this);
		
		this.setOverCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
	}
	
	public Input setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
		
		this.updateText();
		
		return this;
	}
	
	public Input setText(String text) {
		this.text = new StringBuilder(text);
		
		this.cursorPosition = this.text.length();
		
		this.updateText();
		
		return this;
	}

	public String getPlaceholder() {
		return this.placeholder;
	}

	public void drawSurface(AppDrawingContext context) {
		super.drawSurface(context);
		
		if(this.hasFocus && this.text.length() > 0) {			
			double[] scdsp = new double[] {1.0d, 1.0d, 0.0d, 0.0d};
 			
			if(context instanceof OpenGLContext) {
				scdsp = OpenGLTextMetricsTransform.getScalarsAndDisplacements((OpenGLContext) context);
			}

			TextTessellator tessellator = context.getFontRenderer().getTessellator();
			Metrics metrics = tessellator.getMetrics();
			
			double x = this.getText().getRenderPosition().getX() + scdsp[0] * tessellator.getWidth(this.text.substring(0, this.cursorPosition) + scdsp[2]);
			double y = this.getBounds().getY() + this.getBounds().getHeight() / 2 + scdsp[3];
			
			System.out.println(x + "; " + y);

			this.getAppManager().getAlphaLayering().push(AlphaLayer.COMPONENT, 0xFF);
			//context.getPrimitives().fillRoundRect(new Bounds(x, y - scdsp[1] * metrics.getLineHeight() / 2, scdsp[0] * 2, scdsp[1] * metrics.getLineHeight()));
			this.getAppManager().getAlphaLayering().pop(AlphaLayer.COMPONENT);

			if(this.highlighted) {
				this.getAppManager().getAlphaLayering().push(AlphaLayer.COMPONENT, 0x42);
				
				//Bounds selection = new Bounds(this.getText().getRenderPosition().getX() + scdsp[2], y - scdsp[1] * metrics.getLineHeight() / 2, scdsp[0] * tessellator.getWidth(this.text.toString()), scdsp[1] * metrics.getLineHeight());
				//context.setColor(0xC0FFEE);
				// context.getPrimitives().fillRect(selection);
				
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
		} else if(GLFontRenderer.CHARSET.indexOf(ch) >= 0) {
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
	
	public String toString() {
		return this.text.toString();
	}
}