/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.ui.topology;

import java.util.Arrays;
import java.util.List;

public class Bounds implements Set {

	private Point p1;
	private Point p2;
	
	public Bounds() {
		this(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	public Bounds(Point origin, Size size) {
		this(origin.getX(), origin.getY(), size.getWidth(), size.getHeight());
	}
	
	public Bounds(int x, int y, int width, int height) {
		this(new Point(x, y), new Point(x + width, y + height));
	}
	
	public Bounds(float x, float y, float width, float height) {
		this(new Point(x, y), new Point(x + width, y + height));
	}
	
	public Bounds(Point p1, Point p2) {
		this.p1 = p1.copy();
		this.p2 = p2.copy();
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
	
	public void setOrigin(Point origin) {
		this.p1 = origin.copy();
	}
	
	public void setOrigin(float x, float y) {
		this.setX(x);
		this.setY(y);
	}
	
	public void setSize(Size size) {
		this.setSize(size.getWidth(), size.getHeight());
	}
	
	public void setSize(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public void setFrame(Point origin, Size size) {
		this.setOrigin(origin);
		this.setSize(size);
	}
	
	public void setFrame(float x, float y, float width, float height) {
		this.setOrigin(x, y);
		this.setSize(x, y);
	}
	
	public float getX() {
		return this.p1.getX();
	}
	
	public float getY() {
		return this.p1.getY();
	}
	
	public Point getOrigin() {
		return this.p1.copy();
	}
	
	public float getWidth() {
		return this.p2.getX() - this.getX();
	}
	
	public float getHeight() {
		return this.p2.getY() - this.getY();
	}
	
	public Size getSize() {
		return new Size(this.getWidth(), this.getHeight());
	}
	
	public int getXAsInt() {
		return (int) this.p1.getX();
	}
	
	public int getYAsInt() {
		return (int) this.p1.getY();
	}
	
	public int getWidthAsInt() {
		return (int) (this.p2.getX() - this.getX());
	}
	
	public int getHeightAsInt() {
		return (int) (this.p2.getY() - this.getY());
	}
	
	public Point getFirstPoint() {
		return this.p1.copy();
	}
	
	public Point getSecondPoint() {
		return this.p2.copy();
	}
	
	public boolean contains(float x, float y) {
		return this.contains(new Point(x, y));
	}
	
	public boolean contains(Point point) {
		return Math.abs(point.compareTo(this.p1) - point.compareTo(this.p2)) > 1;
	}
	
	public Point getCenter() {
		return new Point((this.p1.getX() + this.p2.getX()) / 2, (this.p1.getY() + this.p2.getY()) / 2);
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
		if(obj instanceof Bounds) {
			Bounds other = (Bounds) obj;
			return this.p1.equals(other.p1) && this.p2.equals(other.p2);
		} else {
			return false;
		}
	}
	
	public String toString() {
		return this.p1 + "; " + this.p2;
	}
}