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
package org.azentreprise.arionide.ui.primitives;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;

public class AWTPrimitives implements IPrimitives {
	
	public void drawRect(AppDrawingContext context, Rectangle bounds) {
		this.loadContext(context).getRenderer().drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public void fillRect(AppDrawingContext context, Rectangle bounds) {
		this.loadContext(context).getRenderer().fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public void drawRoundRect(AppDrawingContext context, Rectangle bounds) {
		this.loadContext(context).getRenderer().drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 25, 25);
	}

	public void fillRoundRect(AppDrawingContext context, Rectangle bounds) {
		this.loadContext(context).getRenderer().fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 25, 25);
	}

	public void drawLine(AppDrawingContext context, int x1, int y1, int x2, int y2) {
		this.loadContext(context).getRenderer().drawLine(x1, y1, x2, y2);
	}
	
	public Point drawText(AppDrawingContext context, String text, Rectangle bounds) {
		return this.drawText(context, text, bounds, 0);
	}
	
	public Point drawText(AppDrawingContext context, String text, Rectangle bounds, int yCorrection) {			
		Graphics2D g2d = this.loadContext(context).getRenderer();
		
		g2d.setFont(context.getFontAdapter().adapt(text, bounds.width, bounds.height));
		
		FontMetrics metrics = g2d.getFontMetrics();
				
		int x = bounds.x + (bounds.width - metrics.stringWidth(text)) / 2;
		int y = bounds.y + (bounds.height - metrics.getMaxDescent() + metrics.getMaxAscent() + 1 + yCorrection) / 2;
		
		g2d.drawString(text, x, y);
		
		return new Point(x, y);
	}
	
	private AWTDrawingContext loadContext(AppDrawingContext context) {
		assert context instanceof AWTDrawingContext;
		return (AWTDrawingContext) context;
	}
}