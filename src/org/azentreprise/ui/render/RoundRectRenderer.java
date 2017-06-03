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
package org.azentreprise.ui.render;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class RoundRectRenderer {
	
	public static void draw(Graphics2D g2d, int x, int y, int width, int height) {
		g2d.drawRoundRect(x, y, width, height, 30, 30);
	}
	
	public static void draw(Graphics2D g2d, Rectangle bounds) {
		RoundRectRenderer.draw(g2d, (int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
	}
	
	public static void fill(Graphics2D g2d, int x, int y, int width, int height) {
		g2d.fillRoundRect(x, y, width, height, 30, 30);
	}
	
	public static void fill(Graphics2D g2d, Rectangle bounds) {
		RoundRectRenderer.fill(g2d, (int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
	}
}