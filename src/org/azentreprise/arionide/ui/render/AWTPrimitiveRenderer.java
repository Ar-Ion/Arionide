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
package org.azentreprise.arionide.ui.render;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.azentreprise.arionide.ui.AWTContext;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.render.font.GLFontRenderer;

public class AWTPrimitiveRenderer implements PrimitiveRenderer {
	
	private AWTContext context;
	
	public void init(AppDrawingContext context) {
		assert context instanceof AWTContext;
		this.context = (AWTContext) context;
	}
	
	public void drawRect(Rectangle2D bounds) {
		this.context.getRenderer().draw(bounds);
	}
	
	public void fillRect(Rectangle2D bounds) {
		this.context.getRenderer().fill(bounds);
	}
	
	public void drawRoundRect(Rectangle2D bounds) {
		Rectangle integerRect = bounds.getBounds();
		this.context.getRenderer().drawRoundRect(integerRect.x, integerRect.y, integerRect.width, integerRect.height, 32, 32);
	}

	public void fillRoundRect(Rectangle2D bounds) {
		Rectangle integerRect = bounds.getBounds();
		this.context.getRenderer().fillRoundRect(integerRect.x, integerRect.y, integerRect.width, integerRect.height, 32, 32);
	}

	public void drawLine(double x1, double y1, double x2, double y2) {
		this.context.getRenderer().drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}
	
	public Point2D drawText(String text, Rectangle2D bounds) {			
		Graphics2D g2d = this.context.getRenderer();
		
		/*Font font = context.getFontAdapter().adapt(text, bounds.getWidth(), bounds.getHeight());
		
		if(g2d.getFont() != font) {
			g2d.setFont(font);
		}*/
		
		FontMetrics metrics = g2d.getFontMetrics();
				
		double x = bounds.getX() + (bounds.getWidth() - metrics.stringWidth(text)) / 2;
		double y = bounds.getY() + (bounds.getHeight() - metrics.getMaxDescent() + metrics.getMaxAscent() + 1) / 2;
		
		g2d.drawString(text, (int) x, (int) y);
		
		return new Point2D.Double(x, y);
	}

	public void drawCursor() {
		// Not implemented
	}

	public GLFontRenderer getFontRenderer() {
		return null;
	}
}