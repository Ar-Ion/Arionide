/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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

public class Size extends Application implements Set {

	private Point size;
	
	public Size() {
		this(0.0f, 0.0f);
	}
	
	public Size(int width, int height) {
		this.size = new Point(width, height);
	}
	
	public Size(float width, float height) {
		this.size = new Point(width, height);
	}
	
	public void setWidth(float width) {
		this.size.setX(width);
	}
	
	public void setHeight(float height) {
		this.size.setY(height);
	}
	
	public void setSize(float width, float height) {
		this.size.setX(width);
		this.size.setY(height);
	}
	
	public float getWidth() {
		return this.size.getX();
	}
	
	public float getHeight() {
		return this.size.getY();
	}
	
	public int getWidthAsInt() {
		return this.size.getXAsInt();
	}
	
	public int getHeightAsInt() {
		return this.size.getYAsInt();
	}
	
	public float getWidthOverHeight() {
		return this.size.getX() / this.size.getY();
	}
	
	public float getHeightOverWidth() {
		return this.size.getY() / this.size.getX();
	}
	
	public void apply(Set input) {
		for(Point point : input.getPoints()) {
			point.setX(point.getX() * this.size.getX());
			point.setY(point.getY() * this.size.getY());
		}
	}
	
	public List<Point> getPoints() {
		return Arrays.asList(this.size);
	}
	
	public Size copy() {
		return new Size(this.size.getX(), this.size.getY());
	}
	
	public String toString() {
		return this.size.toString(); 
	}
}
