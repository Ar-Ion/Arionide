package org.azentreprise.arionide.ui.core.opengl;

import java.util.Random;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldElement {
	
	private static final Random rand = new Random();
	
	/* Data needed for tesselation, rendering and collision detection. */
	
	private final String identifier;
	private final Vector3f center;
	private final Vector3f randVector;
	private final Vector3f randAxis;
	private final Vector4f color;
	private final float size;
	
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
	
	protected Vector4f getColor() {
		return this.color;
	}
	
	protected float getSize() {
		return this.size;
	}
	
	protected String getID() {
		return this.identifier;
	}
	
	public String toString() {
		return this.identifier;
	}
}