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

public class Line implements Set {

	private Point p1;
	private Point p2;
	
	public Line() {
		this(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	public Line(int x1, int y1, int x2, int y2) {
		this(new Point(x1, y1), new Point(x2, y2));
	}
	
	public Line(float x1, float y1, float x2, float y2) {
		this(new Point(x1, y1), new Point(x2, y2));
	}
	
	public Line(Point p1, Point p2) {
		this.p1 = p1.copy();
		this.p2 = p2.copy();
	}

	public void setX1(float x1) {
		this.p1.setX(x1);
	}
	
	public void setY1(float y1) {
		this.p1.setY(y1);
	}
	
	public void setX2(float x2) {
		this.p2.setX(x2);
	}
	
	public void setY2(float y2) {
		this.p2.setY(y2);
	}
	
	public void setFirstPoint(float x1, float y1) {
		this.p1 = new Point(x1, y1);
	}
	
	public void setFirstPoint(Point p1) {
		this.p1 = p1.copy();
	}
	
	public void setSecondPoint(float x2, float y2) {
		this.p2 = new Point(x2, y2);
	}
	
	public void setSecondPoint(Point p2) {
		this.p2 = p2.copy();
	}
	
	public void setLine(float x1, float y1, float x2, float y2) {
		this.setFirstPoint(x1, y1);
		this.setSecondPoint(x2, y2);
	}
	
	public float getX1() {
		return this.p1.getX();
	}
	
	public float getY1() {
		return this.p1.getY();
	}
	
	public float getX2() {
		return this.p2.getX();
	}
	
	public float getY2() {
		return this.p2.getY();
	}

	public int getX1AsInt() {
		return (int) this.p1.getX();
	}
	
	public int getY1AsInt() {
		return (int) this.p1.getY();
	}
	
	public int getX2AsInt() {
		return (int) this.p2.getX();
	}
	
	public int getY2AsInt() {
		return (int) this.p2.getY();
	}
	
	public Point getFirstPoint() {
		return this.p1.copy();
	}
	
	public Point getSecondPoint() {
		return this.p2.copy();
	}
	
	public Bounds getSpan() {
		return new Bounds(this.p1, this.p2);
	}

	public List<Point> getPoints() {
		return Arrays.asList(this.p1, this.p2);
	}

	public Bounds copy() {
		return new Bounds(this.p1.copy(), this.p2.copy());
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.p1.hashCode();
		result = prime * result + this.p2.hashCode();
		return result;
	}

	public boolean equals(Object obj) {
		if(obj instanceof Line) {
			Line other = (Line) obj;
			return this.p1.equals(other.p1) && this.p2.equals(other.p2);
		} else {
			return false;
		}
	}
	
	public String toString() {
		return this.p1 + "; " + this.p2;
	}
}