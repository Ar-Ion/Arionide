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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.ui.UIFontAdapter;
import org.azentreprise.ui.views.UIView;

public class UILabel extends Component {
	
	protected int color;
	protected int opacity;
	protected String label;
	protected Point textRenderPosition;

	public UILabel(UIView parent, float x, float y, float width, float height, String label) {
		super(parent, x, y, width, height);
		this.label = label;
	}
	
	public UILabel setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public UILabel setColor(int argb) {
		this.color = argb;
		this.opacity = argb >>> 24;
		return this;
	}

	public void draw(Graphics2D g2d) {		
		if(this.opacity > 0) {
			g2d.setColor(new Color((this.opacity << 24) | (this.color & 0xFFFFFF), true));
		}
		
		this.drawString(g2d, this.label);
	}
	
	public void drawString(Graphics2D g2d, String str) {
		if(UIFontAdapter.needSetup()) {
			g2d.setFont(this.getFont());
			UIFontAdapter.setup(g2d.getFontMetrics());
		}
		
		Rectangle bounds = g2d.getClipBounds();
		
		g2d.setFont(UIFontAdapter.adapt(str, bounds.width, bounds.height, 0.8f));
		
		FontMetrics metrics = g2d.getFontMetrics();
		
		int x = (bounds.width - metrics.stringWidth(str)) / 2;
		int y = (bounds.height - metrics.getHeight()) / 2 + metrics.getAscent();
		
		g2d.drawString(str, x, y);
		
		this.textRenderPosition = new Point(x, y);
	}
	
	public boolean isFocusable() {
		return false;
	}
}
