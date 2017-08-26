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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.ui.core.opengl;

import java.util.Random;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldElement {
	
	private static final Random rand = new Random();
	
	/* Data needed for tesselation, rendering and collision detection. */
	
	private final Vector3f center;
	private final Vector3f randVector;
	private final Vector3f randAxis;
	private final float size;
	
	private String identifier;
	private Vector4f color;
	
	protected WorldElement(String id, Vector3f center, Vector4f color, float size) {
		this.identifier = id;
		this.center = new Vector3f(center);
		this.randVector = this.generateRandomVector().normalize();
		this.randAxis = new Vector3f(this.randVector).cross(this.generateRandomVector()).normalize();
		this.color = new Vector4f(color);
		this.size = size;
	}
	
	private Vector3f generateRandomVector() {
		return new Vector3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
	}
	
	protected boolean collidesWith(Vector3f object) {
		return object.distance(this.center) <= this.size;
	}
	
	protected Vector3f getCenter() {
		return this.center;
	}
	
	protected Vector3f getBaseVector() {
		return new Vector3f(this.randVector);
	}
	
	protected Vector3f getAxis() {
		return new Vector3f(this.randAxis);
	}
	
	protected float getSize() {
		return this.size;
	}
	
	protected Vector4f getColor() {
		return this.color;
	}
	
	protected String getID() {
		return this.identifier;
	}
	
	public void setColor(Vector4f color) {
		this.color = color;
	}
	
	public void setID(String id) {
		this.identifier = id;
	}
	
	public String toString() {
		return this.identifier;
	}
}