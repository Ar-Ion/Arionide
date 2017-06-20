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
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;

public class AWTTabDesign implements TabDesign {
	
	private Paint paint = null;
	private Point2D lastCenter = null;
	private int lastRadius = 0;
	
	public void enterDesignContext(AppDrawingContext context, Point2D center, int radius) {
		assert context instanceof AWTDrawingContext;
		
		Graphics2D g2d = ((AWTDrawingContext) context).getRenderer();
		
		if(center != this.lastCenter || radius != this.lastRadius) {
			this.paint = new RadialGradientPaint(center, radius, new float[] {0.0f, 1.0f}, new Color[] {new Color(g2d.getColor().getRGB(), false), g2d.getColor()});
			this.lastCenter = center;
			this.lastRadius = radius;
		}
		
		g2d.setPaint(this.paint);
	}
	
	public void exitDesignContext(AppDrawingContext context) {
		assert context instanceof AWTDrawingContext;
	}
}