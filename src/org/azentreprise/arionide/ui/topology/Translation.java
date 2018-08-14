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

public class Translation extends Application {

	private float translateX;
	private float translateY;
	
	public Translation() {
		this(0.0f, 0.0f);
	}
	
	public Translation(float translateX, float translateY) {
		this.translateX = translateX;
		this.translateY = translateY;
	}
	
	public void setTranslateX(float translateX) {
		this.translateX = translateX;
	}
	
	public void setTranslateY(float translateY) {
		this.translateY = translateY;
	}
	
	public void setTranslation(float translateX, float translateY) {
		this.translateX = translateX;
		this.translateY = translateY;
	}
	
	public float getTranslateX() {
		return this.translateX;
	}
	
	public float getTranslateY() {
		return this.translateY;
	}
	
	public void apply(Set input) {
		for(Point point : input.getPoints()) {
			point.setX(point.getX() + this.translateX);
			point.setY(point.getY() + this.translateY);
		}
	}
}