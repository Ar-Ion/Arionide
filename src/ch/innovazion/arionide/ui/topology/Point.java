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
package ch.innovazion.arionide.ui.topology;

import java.util.Arrays;
import java.util.List;

public class Point implements Set, Comparable<Point> {

	private float x;
	private float y;
	
	public Point() {
		this(0.0f, 0.0f);
	}
	
	public Point(int xy) {
		this(xy, xy);
	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(float xy) {
		this(xy, xy);
	}
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void setXY(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public int getXAsInt() {
		return (int) this.x;
	}
	
	public int getYAsInt() {
		return (int) this.y;
	}
	
	public List<Point> getPoints() {
		return Arrays.asList(this);
	}
	
	public Point copy() {
		return new Point(this.x, this.y);
	}

	public int compareTo(Point other) {
		if(this.x < other.x && this.y < other.y) {
			return -1;
		} else if(this.x > other.x && this.y > other.y) {
			return 1;
		}
		
		return 0;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(this.x);
		result = prime * result + Float.floatToIntBits(this.y);
		return result;
	}

	public boolean equals(Object obj) {
		if(obj instanceof Point) {
			Point other = (Point) obj;
			return this.x == other.x && this.y == other.y;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "(" + this.x + "; " + this.y + ")";
	}
}