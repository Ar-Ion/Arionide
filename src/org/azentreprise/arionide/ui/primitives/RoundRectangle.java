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

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class RoundRectangle {
	
	public static void draw(Graphics2D g2d, int x, int y, int width, int height) {
		g2d.drawLine(x + 8, y, x + width - 8, y);
		g2d.drawLine(x + width, y + 8, x + width, y + height - 8);
		g2d.drawLine(x + 8, y + height, x + width - 8, y + height);
		g2d.drawLine(x, y + 8, x, y + height - 8);
		
		g2d.drawLine(x + width - 8, y, x + width, y + 8);
		g2d.drawLine(x + width, y + height - 8, x + width - 8, y + height);
		g2d.drawLine(x + 8, y + height, x, y + height - 8);
		g2d.drawLine(x, y + 8, x + 8, y);
	}
	
	public static void draw(Graphics2D g2d, Rectangle bounds) {
		RoundRectangle.draw(g2d, (int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
	}
	
	public static void fill(Graphics2D g2d, int x, int y, int width, int height) {
		g2d.fillRect(x, y + 5, width, height - 9);
		
		g2d.drawLine(x + 5, y, x + width - 5, y);
		g2d.drawLine(x + 3, y + 1, x + width - 3, y + 1);
		g2d.drawLine(x + 2, y + 2, x + width - 2, y + 2);
		g2d.drawLine(x + 1, y + 3, x + width - 1, y + 3);
		g2d.drawLine(x + 1, y + 4, x + width - 1, y + 4);
		
		g2d.drawLine(x + 5, y + height, x + width - 5, y + height);
		g2d.drawLine(x + 3, y + height - 1, x + width - 3, y + height - 1);
		g2d.drawLine(x + 2, y + height - 2, x + width - 2, y + height - 2);
		g2d.drawLine(x + 1, y + height - 3, x + width - 1, y + height - 3);
		g2d.drawLine(x + 1, y + height - 4, x + width - 1, y + height - 4);
	}
	
	public static void fill(Graphics2D g2d, Rectangle bounds) {
		RoundRectangle.fill(g2d, (int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
	}
}