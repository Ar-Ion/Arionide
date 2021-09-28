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

import java.util.Set;

import com.jogamp.newt.event.KeyEvent;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.PressureEvent;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.Viewport;
import ch.innovazion.arionide.ui.animations.Animation;
import ch.innovazion.arionide.ui.animations.FieldModifierAnimation;
import ch.innovazion.arionide.ui.overlay.AlphaLayer;
import ch.innovazion.arionide.ui.overlay.AlphaLayeringSystem;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.render.PrimitiveFactory;
import ch.innovazion.arionide.ui.render.Shape;
import ch.innovazion.arionide.ui.render.font.FontRenderer;
import ch.innovazion.arionide.ui.render.font.TextCacheEntry;
import ch.innovazion.arionide.ui.render.font.TextTessellator;
import ch.innovazion.arionide.ui.topology.Affine;
import ch.innovazion.arionide.ui.topology.Application;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;
import ch.innovazion.arionide.ui.topology.Scalar;
import ch.innovazion.arionide.ui.topology.Size;
import ch.innovazion.arionide.ui.topology.Translation;

public class Input extends Button implements EventHandler {	
	
	private static final int cursorAdvance = 15;
	private static final int cursorBlinkingPeriod = 500;
	private static final Application minusOne = new Translation(-1, -1);

	private final Shape cursor = PrimitiveFactory.instance().newLine(ApplicationTints.WHITE, ApplicationTints.ACTIVE_ALPHA);
	private final Shape selection = PrimitiveFactory.instance().newRectangle(ApplicationTints.WHITE, ApplicationTints.INACTIVE_ALPHA);
	private final Animation animation;
	
	protected String placeholder;
	protected StringBuilder text = new StringBuilder();
	
	protected int cursorPosition = 0;
	protected int cursorAlpha = 255;
	
	private long counter = 0L;
	protected boolean highlighted = false;
	
	private int textWidth = 0;
	
	public Input(View parent, String placeholder) {
		super(parent, placeholder);
		
		this.placeholder = placeholder;
		this.animation = new FieldModifierAnimation(parent.getAppManager(), "cursorAlpha", Input.class, this);
	}
	
	public void load() {
		super.load();
		
		this.cursor.updateBounds(new Bounds(1, 1, 0, 2));
		this.selection.updateBounds(new Bounds(0, 0, 2, 2));
		
		this.cursor.prepare();    // Updating the bounds already toggles the "reprepare" bit though. 
		this.selection.prepare(); // Might be useful for further implementations...
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
			Size symbolicSize = context.getWindowSize();
			
			TextTessellator tessellator = context.getFontRenderer().getTessellator();
						
			Affine affine = this.getText().getRenderTransformation();
			
			TextCacheEntry entry = context.getFontRenderer().getCacheEntry(this.text.toString());
			// This entry is not granted to be non-null because of deferred rendering. Thus, the following code might introduce a frame bubble.
			
			if(entry != null) {
				Point center = this.getBounds().getCenter();
								
				minusOne.apply(center);
				
				Size pixelSize = Viewport.getPixelSize(context);
				
				float cursorDisplacement = entry.getWidth() * affine.getScalar().getScaleX() * tessellator.getWidth(this.text.substring(0, this.cursorPosition)) / this.textWidth;
							
				if(this.cursorPosition > 0) {
					cursorDisplacement += cursorAdvance * pixelSize.getWidth();
				} else {
					cursorDisplacement = -cursorAdvance * pixelSize.getWidth();
				}
				
				Scalar scalar = new Scalar(1.0f, -affine.getScalar().getScaleY() / symbolicSize.getHeight() * entry.getHeight());
				Translation translation = new Translation(center.getX() + cursorDisplacement - entry.getWidth() * affine.getScalar().getScaleX() * 0.5f, -center.getY() - entry.getHeight() * affine.getScalar().getScaleY() * 0.5f);
	
				this.cursor.updateAffine(new Affine(scalar, translation));
				
				AlphaLayeringSystem layering = this.getAppManager().getAlphaLayering();
				
				layering.push(AlphaLayer.COMPONENT, this.cursorAlpha);
				this.cursor.updateAlpha(this.cursorAlpha);
				getParent().getPreferedRenderingSystem(context).renderLater(this.cursor);
				layering.pop(AlphaLayer.COMPONENT);
	
				if(this.highlighted) {
					layering.push(AlphaLayer.COMPONENT, 0x42);
					this.selection.updateAlpha(layering.getCurrentAlpha());
					this.selection.updateAffine(new Affine(scalar, new Translation(center.getX(), center.getY())));
					layering.pop(AlphaLayer.COMPONENT);
				}
			}
		}
	}
	
	public void drawComponent(AppDrawingContext context) {
		if(this.text.length() != 0) {
			getParent().getPreferedRenderingSystem(context).renderLater(this.getText());
		} else {
			AlphaLayeringSystem layering = this.getAppManager().getAlphaLayering();
			
			layering.push(AlphaLayer.COMPONENT, ApplicationTints.PLACEHOLDER_ALPHA);
			
			this.getText().updateAlpha(layering.getCurrentAlpha());
			getText().updateRGB(ApplicationTints.PLACEHOLDER_COLOR);
			getParent().getPreferedRenderingSystem(context).renderLater(this.getText());
			
			layering.pop(AlphaLayer.COMPONENT);
		}
		
		this.drawBorders(context);
	}
	
	private void cursorAnimationOpacityIncrease() {
		this.animation.startAnimation(cursorBlinkingPeriod, nil -> this.cursorAnimationOpacityDecrease(), 255);
	}
	
	private void cursorAnimationOpacityDecrease() {
		this.animation.startAnimation(cursorBlinkingPeriod, nil -> this.cursorAnimationOpacityIncrease(), 0);
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(!isEnabled() || !isVisible() || getBounds() == null) {
			return; // Abort event if the button is not supposed to handle it.
		}
		
		super.handleEvent(event);
		
		if(this.hasFocus && (event instanceof PressureEvent)) {
			PressureEvent pressure = (PressureEvent) event;
						
			event.abortDispatching();
			
			if(pressure.isUp()) {
				return; // Do not respond twice to the "same" event
			}
			
			int code = pressure.getKeycode();
			
			boolean noSeek = code != KeyEvent.VK_LEFT && code != KeyEvent.VK_RIGHT;
						
			if(this.highlighted && noSeek) {
				this.text = new StringBuilder();
				this.cursorPosition = 0;
				this.highlighted = false;
			}

			this.dispatch(code, pressure.getChar(), pressure.getModifiers(), !noSeek);
			
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
		String specialChars = "≠±“#Ç[]|{}";
		int specialID = code - KeyEvent.VK_0;
		
		if(specialID >= 0 && specialID <= 9 && (modifiers & (KeyEvent.ALT_MASK | KeyEvent.ALT_GRAPH_MASK)) != 0 && (modifiers & KeyEvent.SHIFT_MASK) == 0) {
			ch = specialChars.charAt(specialID);
		} else if(code == KeyEvent.VK_7 && (modifiers & (KeyEvent.ALT_MASK | KeyEvent.ALT_GRAPH_MASK)) != 0 && (modifiers & KeyEvent.SHIFT_MASK) != 0) {
			ch = '\\';
		} else if(code == KeyEvent.VK_EQUALS && modifiers == 0) {
			ch = '^';
		}
		
		
		System.out.println(code + " -> " + ch + " / " + modifiers);
		if(code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE) {
			this.dispatchDeletion(code, modifiers > 0);
		} else if(seek) {
			this.dispatchSeek(code);
		} else if(FontRenderer.CHARSET.indexOf(ch) >= 0) {
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
		if(this.text.length() > 0) {
			this.textWidth = this.getAppManager().getDrawingContext().getFontRenderer().getTessellator().getWidth(this.text.toString());
			this.setLabel(this.text.toString());
		} else {
			this.setLabel(this.placeholder);
		}
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
	
	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.combine(super.getHandleableEvents(), PressureEvent.class);
	}
	
	public String toString() {
		return this.text != null ? this.text.toString() : "<Uninitialized Input component>";
	}
}