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
package org.azentreprise.arionide.ui.topology;

import java.util.Arrays;
import java.util.List;

public class Bounds implements Set {

	private Point p1;
	private Point p2;
	
	public Bounds() {
		this(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	public Bounds(float x, float y, float width, float height) {
		this(new Point(x, y), new Point(x + width, y + height));
	}
	
	public Bounds(Point p1, Point p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public void setX(float x) {
		this.p1.setX(x);
	}
	
	public void setY(float y) {
		this.p1.setY(y);
	}
	
	public void setWidth(float width) {
		this.p2.setX(this.getX() + width);
	}
	
	public void setHeight(float height) {
		this.p2.setY(this.getY() + height);
	}
	
	public void setFirstPoint(Point p1) {
		this.p1 = p1;
	}
	
	public void setSecondPoint(Point p2) {
		this.p2 = p2;
	}
	
	public float getX() {
		return this.p1.getX();
	}
	
	public float getY() {
		return this.p1.getY();
	}
	
	public float getWidth() {
		return this.p2.getX() - this.getX();
	}
	
	public float getHeight() {
		return this.p2.getY() - this.getY();
	}
	
	public Point getFirstPoint() {
		return this.p1;
	}
	
	public Point getSecondPoint() {
		return this.p2;
	}
	
	public List<Point> getPoints() {
		return Arrays.asList(this.p1, this.p2);
	}

	public Set copy() {
		return new Bounds(this.p1, this.p2);
	}
}