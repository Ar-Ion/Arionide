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
import java.util.function.Function;
import java.util.function.Supplier;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldElement {	
	private static final Random rand = new Random();
		
	public static final Supplier<Vector3f> RANDOM_GENERATOR = () -> new Vector3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

	private static Supplier<Vector3f> axisGenerator = RANDOM_GENERATOR;
	private static Function<Vector3f, Vector3f> baseGenerator = (axis) -> RANDOM_GENERATOR.get().cross(axis);

	protected static void setAxisGenerator(Supplier<Vector3f> generator) {
		axisGenerator = generator;
	}
	
	protected static void setBaseGenerator(Function<Vector3f, Vector3f> generator) {
		baseGenerator = generator;
	}
	
	/* Data needed for tesselation, rendering and collision detection. */
	
	private final int id;
	private final Vector3f center;
	private final Vector3f axis;
	private final Vector3f base;
	private final float size;
	
	private String name;
	private Vector4f color;
	
	protected WorldElement(int id, String name, Vector3f center, Vector4f color, float size) {
		this.id = id;
		this.name = name;
		this.center = new Vector3f(center);
		this.axis = axisGenerator.get().normalize();
		this.base = baseGenerator.apply(this.axis).normalize();
		this.color = new Vector4f(color);
		this.size = size;
	}
	
	protected boolean collidesWith(Vector3f object) {
		return object.distance(this.center) <= this.size;
	}
	
	public int getID() {
		return this.id;
	}
	
	public Vector3f getCenter() {
		return this.center;
	}
	
	public Vector3f getBaseVector() {
		return new Vector3f(this.base);
	}
	
	public Vector3f getAxis() {
		return new Vector3f(this.axis);
	}
	
	public float getSize() {
		return this.size;
	}
	
	public Vector4f getColor() {
		return this.color;
	}
	
	public void setColor(Vector4f color) {
		this.color = color;
	}
		
	protected String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}
}