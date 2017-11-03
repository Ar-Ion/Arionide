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
package org.azentreprise.arionide.ui.primitives;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;

public class AWTPrimitives implements IPrimitives {
		
	public void drawRect(AppDrawingContext context, Rectangle2D bounds) {
		this.loadContext(context).getRenderer().draw(bounds);
	}
	
	public void fillRect(AppDrawingContext context, Rectangle2D bounds) {
		this.loadContext(context).getRenderer().fill(bounds);
	}
	
	public void drawRoundRect(AppDrawingContext context, Rectangle2D bounds) {
		Rectangle integerRect = bounds.getBounds();
		this.loadContext(context).getRenderer().drawRoundRect(integerRect.x, integerRect.y, integerRect.width, integerRect.height, 32, 32);
	}

	public void fillRoundRect(AppDrawingContext context, Rectangle2D bounds) {
		Rectangle integerRect = bounds.getBounds();
		this.loadContext(context).getRenderer().fillRoundRect(integerRect.x, integerRect.y, integerRect.width, integerRect.height, 32, 32);
	}

	public void drawLine(AppDrawingContext context, double x1, double y1, double x2, double y2) {
		this.loadContext(context).getRenderer().drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}
	
	public Point2D drawText(AppDrawingContext context, String text, Rectangle2D bounds) {
		return this.drawText(context, text, bounds, 0);
	}
	
	public Point2D drawText(AppDrawingContext context, String text, Rectangle2D bounds, int yCorrection) {			
		Graphics2D g2d = this.loadContext(context).getRenderer();
		
		Font font = context.getFontAdapter().adapt(text, bounds.getWidth(), bounds.getHeight());
		
		if(g2d.getFont() != font) {
			g2d.setFont(font);
		}
		
		FontMetrics metrics = g2d.getFontMetrics();
				
		double x = bounds.getX() + (bounds.getWidth() - metrics.stringWidth(text)) / 2;
		double y = bounds.getY() + (bounds.getHeight() - metrics.getMaxDescent() + metrics.getMaxAscent() + 1 + yCorrection) / 2;
		
		g2d.drawString(text, (int) x, (int) y);
		
		return new Point2D.Double(x, y);
	}
	
	private AWTDrawingContext loadContext(AppDrawingContext context) {
		assert context instanceof AWTDrawingContext;
		return (AWTDrawingContext) context;
	}
}