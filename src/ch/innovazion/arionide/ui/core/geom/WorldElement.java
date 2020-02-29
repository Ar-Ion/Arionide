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
package ch.innovazion.arionide.ui.core.geom;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldElement {	

	
	/* Data needed for tessellation, rendering and collision detection. */
	
	private final int id;
	private Vector3f center;
	private Vector3f base;
	private Vector3f axis;
	private float size;
	private boolean accessAllowed;
	
	private String name;
	private String desc;
	private Vector4f color;
	private Vector3f spotColor;
	
	protected WorldElement(int id, String name, String desc, Vector3f center, Vector3f base, Vector3f axis, Vector4f color, Vector3f spotColor, float size, boolean accessAllowed) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.center = center;
		this.base = base;
		this.axis = axis;
		this.color = color;
		this.spotColor = spotColor;
		this.size = size;
		this.accessAllowed = accessAllowed;
	}
	
	public WorldElement recycle(int id, WorldElement target) {
		assert this.id == id;
		
		target.name = name;
		target.desc = desc;
		target.center = center;
		target.base = base;
		target.axis = axis;
		target.color = color;
		target.spotColor = spotColor;
		target.size = size;
		target.accessAllowed = accessAllowed;
		
		return target;
	}
	
	public boolean collidesWith(Vector3f object) {
		return object.distance(this.center) <= this.size;
	}
	
	public int getID() {
		return this.id;
	}
	
	public Vector3f getCenter() {
		return new Vector3f(this.center);
	}
	
	public Vector3f getBaseVector() {
		return new Vector3f(this.base);
	}
	
	public Vector3f getAxis() {
		return new Vector3f(this.axis);
	}
	
	public Vector3f getSpotColor() {
		return this.spotColor;
	}
	
	public float getSize() {
		return this.size;
	}
	
	public boolean isAccessAllowed() {
		return this.accessAllowed;
	}
	
	public Vector4f getColor() {
		return new Vector4f(this.color);
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
	
	public int hashCode() {
		return this.id;
	}
}