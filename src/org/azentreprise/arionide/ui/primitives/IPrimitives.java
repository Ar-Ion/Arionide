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
package org.azentreprise.arionide.ui.primitives;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.azentreprise.arionide.ui.AppDrawingContext;

public interface IPrimitives {
	public void drawRect(AppDrawingContext context, Rectangle2D bounds);
	public void fillRect(AppDrawingContext context, Rectangle2D bounds);
	public void drawRoundRect(AppDrawingContext context, Rectangle2D bounds);
	public void fillRoundRect(AppDrawingContext context, Rectangle2D bounds);
	public void drawLine(AppDrawingContext context, double x1, double y1, double x2, double y2);
	public Point2D drawText(AppDrawingContext context, String text, Rectangle2D bounds);
	public Point2D drawText(AppDrawingContext context, String text, Rectangle2D bounds, int yCorrection);
}