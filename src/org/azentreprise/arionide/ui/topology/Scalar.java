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

public class Scalar extends Application {

	private float scaleX;
	private float scaleY;
	
	public Scalar() {
		this(1.0f, 1.0f);
	}
	
	public Scalar(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}
	
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}
	
	public void setScalar(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	public float getScaleX() {
		return this.scaleX;
	}
	
	public float getScaleY() {
		return this.scaleY;
	}
	
	public void invert() {
		this.scaleX = 1.0f / this.scaleX;
		this.scaleY = 1.0f / this.scaleY;
	}
	
	public void apply(Set input) {
		for(Point point : input.getPoints()) {
			point.setX(point.getX() * this.scaleX);
			point.setY(point.getY() * this.scaleY);
		}
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(this.scaleX);
		result = prime * result + Float.floatToIntBits(this.scaleY);
		return result;
	}

	public boolean equals(Object obj) {
		if(obj instanceof Scalar) {
			Scalar other = (Scalar) obj;
			return this.scaleX == other.scaleX && this.scaleY == other.scaleY;
		} else {
			return false;
		}
	}
}