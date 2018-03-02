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
package org.azentreprise.arionide.ui.core.opengl;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldElement {	
	private static final Random rand = new Random();
	
	// "Random": this one is not uniform (more likely to become vertical)
	public static final Supplier<Vector3d> RANDOM_GENERATOR = () -> new Vector3d(rand.nextDouble() - 0.5d, rand.nextDouble() * 2.0d, rand.nextDouble() - 0.5d);

	private static Supplier<Vector3d> axisGenerator = RANDOM_GENERATOR;
	private static Function<Vector3d, Vector3d> baseGenerator = RANDOM_GENERATOR.get()::cross;

	protected static void setAxisGenerator(Supplier<Vector3d> generator) {
		axisGenerator = generator;
	}
	
	protected static void setBaseGenerator(Function<Vector3d, Vector3d> generator) {
		baseGenerator = generator;
	}
	
	protected static void setSeed(long seed) {
		rand.setSeed(seed);
	}
	
	/* Data needed for tesselation, rendering and collision detection. */
	
	private final int id;
	private final Vector3d center;
	private final Vector3d axis;
	private final Vector3d base;
	private final double size;
	private final boolean accessAllowed;
	
	private String name;
	private String desc;
	private Vector4f color;
	private Vector3f spotColor;
	
	protected WorldElement(int id, String name, String desc, Vector3d center, Vector4f color, Vector3f spotColor, double size, boolean accessAllowed) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.center = center;
		this.axis = axisGenerator.get().normalize();
		this.base = baseGenerator.apply(this.axis).normalize();
		this.color = color;
		this.spotColor = spotColor;
		this.size = size;
		this.accessAllowed = accessAllowed;
	}
	
	protected boolean collidesWith(Vector3d object) {
		return object.distance(this.center) <= this.size;
	}
	
	public int getID() {
		return this.id;
	}
	
	public Vector3d getCenter() {
		return new Vector3d(this.center);
	}
	
	public Vector3d getBaseVector() {
		return new Vector3d(this.base);
	}
	
	public Vector3d getAxis() {
		return new Vector3d(this.axis);
	}
	
	public Vector3f getSpotColor() {
		return this.spotColor;
	}
	
	public double getSize() {
		return this.size;
	}
	
	public boolean isAccessAllowed() {
		return this.accessAllowed;
	}
	
	public Vector4f getColor() {
		return this.color;
	}
	
	public void setColor(Vector4f color) {
		this.color = color;
	}
		
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.desc;
	}

	public void setDescription(String desc) {
		this.desc = desc;
	}
	
	public String toString() {
		return this.name;
	}
	
	public boolean equals(Object other) {
		if(other instanceof WorldElement) {
			return ((WorldElement) other).id == this.id;
		}
		
		return false;
	}
}