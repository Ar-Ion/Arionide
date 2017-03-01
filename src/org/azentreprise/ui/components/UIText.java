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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azentreprise.Arionide;
import org.azentreprise.ui.UIEvents;
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.animations.Animation;
import org.azentreprise.ui.animations.FieldModifierAnimation;
import org.azentreprise.ui.views.UIView;

public class UIText extends UIButton {
	
	private static final Map<UIView, List<UIText>> accessor = new HashMap<UIView, List<UIText>>();
	
	private final Animation animation = new FieldModifierAnimation("cursorOpacity", UIText.class, this);
	
	private String placeholder;
	private StringBuilder text = new StringBuilder();
	
	private int cursorPosition = 0;
	private int cursorOpacity = 255;
	
	private long counter = 0L;
	private boolean highlighted = false;
	
	public UIText(UIView parent, float x, float y, float width, float height, String placeholder) {
		super(parent, x, y, width, height, placeholder);
		
		this.placeholder = placeholder;
		
		UIEvents.registerKeyboardHandler(this);
		
		if(!UIText.accessor.containsKey(parent)) {
			UIText.accessor.put(parent, new ArrayList<UIText>());
		}

		UIText.accessor.get(parent).add(this);
	}
	
	public UIText setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
		
		this.updateText();
		
		return this;
	}
	
	public UIText setText(String text) {
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

	public void draw(Graphics2D g2d) {
		super.draw(g2d);
		
		if(this.hasFocus && this.text.length() > 0) {
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle bounds = g2d.getClipBounds();
			
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
		super.handleMouseEvent(event);
		
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
	
	public static String[] fetchUserData(UIView parent) {
		List<String> list = new ArrayList<String>();
		
		for(UIText uitext : UIText.accessor.get(parent)) {
			if(uitext.text.length() > 0) {
				list.add(uitext.text.toString());
			}
		}
		
		return list.toArray(new String[0]);
	}
}
