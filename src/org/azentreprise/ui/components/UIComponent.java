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
package org.azentreprise.ui.components;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.azentreprise.Arionide;
import org.azentreprise.ui.Drawable;
import org.azentreprise.ui.views.UIView;

public abstract class UIComponent implements Drawable {
	
	private Font font = Arionide.getSystemFont();
	private UIView parent;
	protected final float x1, y1, x2, y2;
	private int renderXPos = 0, renderYPos = 0, renderWidth = 0, renderHeight;
	
	public UIComponent(UIView parent, float x1, float y1, float x2, float y2) {
		this.parent = parent;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public abstract boolean isFocusable();
	
	public UIView getParentView() {
		return this.parent;
	}
	
	public void prerender(int width, int height) {
		this.renderXPos = (int) (this.x1 * width);
		this.renderYPos = (int) (this.y1 * height);
		this.renderWidth = (int) (this.x2 * width) - this.renderXPos;
		this.renderHeight = (int) (this.y2 * height) - this.renderYPos;
	}
	
	public Rectangle getPrerenderBounds() {
		return new Rectangle(this.renderXPos, this.renderYPos, this.renderWidth, this.renderHeight);
	}
	
	public void render(Graphics2D g2d) {
		this.draw((Graphics2D) g2d.create(this.renderXPos, this.renderYPos, this.renderWidth, this.renderHeight));
	}
	
	public void handleMouseEvent(byte event) {
		return;
	}

	public boolean handleKeyboardEvent(int keyCode, char keyChar, int modifiers) {
		return true;
	}
	
	public UIComponent alterFont(int style, float size) {
		this.font = this.font.deriveFont(style, size);
		return this;
	}
	
	public UIComponent alterFont(int style) {
		this.font = this.font.deriveFont(style);
		return this;
	}
	
	public UIComponent alterFont(float size) {
		this.font = this.font.deriveFont(size);
		return this;
	}
	
	protected Font getFont() {
		return this.font;
	}
	
	public void focusGained() {
		return;
	}
	
	public void focusLost() {
		return;
	}
}
