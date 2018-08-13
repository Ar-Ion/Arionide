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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.ui.AWTContext;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.overlay.AlphaLayer;

public class AWTTabDesign implements TabDesign {
		
	private Paint paint = null;
	private Point2D lastCenter = null;
	private double lastRadius = 0;
	
	private Paint restore;
	
	public void enterDesignContext(AppManager manager, Point2D center, double radius) {
		assert manager.getDrawingContext() instanceof AWTContext;
		
		Graphics2D g2d = ((AWTContext) manager.getDrawingContext()).getRenderer();
		
		this.restore = g2d.getPaint();
		
		if((center != this.lastCenter || radius != this.lastRadius) && radius > 0) {
			int initialAlpha = g2d.getColor().getAlpha();
			manager.getAlphaLayering().pop(AlphaLayer.COMPONENT);
			int preAlpha = g2d.getColor().getAlpha();
			manager.getAlphaLayering().push(AlphaLayer.COMPONENT, Utils.fakeDivision(255 * initialAlpha, preAlpha, 255));
			
			Color color = g2d.getColor();
			
			this.paint = new RadialGradientPaint(center, (int) radius, new float[] {0.0f, 1.0f}, new Color[] {new Color((preAlpha << 24) | color.getRGB(), true), color});
			this.lastCenter = center;
			this.lastRadius = radius;
		}
		
		g2d.setPaint(this.paint);
	}
	
	public void exitDesignContext(AppManager manager) {
		assert manager.getDrawingContext() instanceof AWTContext;
		
		if(this.restore == null) {
			throw new IllegalStateException("Exiting a non-entered context");
		}
		
		((AWTContext) manager.getDrawingContext()).getRenderer().setPaint(this.restore);
		
		this.restore = null;
	}
}