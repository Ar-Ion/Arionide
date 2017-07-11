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
package org.azentreprise.arionide.events;

import java.awt.geom.Point2D;

public class DragEvent extends Event {
	
	private final Point2D anchor;
	private final double dx;
	private final double dy;
	
	public DragEvent(Point2D anchor, double dx, double dy) {
		this.anchor = anchor;
		this.dx = dx;
		this.dy = dy;
	}
	
	public Point2D getAnchor() {
		return this.anchor;
	}
	
	public double getDeltaX() {
		return this.dx;
	}
	
	public double getDeltaY() {
		return this.dy;
	}
}