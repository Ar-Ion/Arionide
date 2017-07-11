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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.ui.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

public class UIStructureRenderer {
	
	private static volatile float[] fractions;
	private static volatile Color[] colors;
	
	public static void init(float hue, float saturation) {
		fractions = new float[] {
				0.0f,
				0.7f,
				0.75f,
				1.0f
			};
			
		colors = new Color[] { 
			new Color(Color.HSBtoRGB(hue, saturation, 0.5f)),
			new Color(Color.HSBtoRGB(hue, saturation, 0.7f)), 
			ComplexProjectRenderer.glow,
			new Color(0, true)
		};
	}

	public static void render(Graphics2D g2d, int x, int y, int radius) {     
        Paint gradient = new RadialGradientPaint(new Point2D.Float(x, y), radius, fractions, colors);
        g2d.setPaint(gradient);
        g2d.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
	}
}