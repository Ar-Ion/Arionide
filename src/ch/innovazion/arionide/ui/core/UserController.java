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
package ch.innovazion.arionide.ui.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.joml.Vector3f;

import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.project.managers.HostStructureStack;
import ch.innovazion.arionide.ui.core.geom.GeometryException;
import ch.innovazion.arionide.ui.core.geom.HierarchicalGeometry;
import ch.innovazion.arionide.ui.core.geom.MergedGeometry;
import ch.innovazion.arionide.ui.core.geom.WorldElement;

public class UserController {
	
	private static final float initialAcceleration = 0.005f * HierarchicalGeometry.MAKE_THE_UNIVERSE_GREAT_AGAIN;
	private static final float spaceFriction = 0.075f;
	private static final float cameraFriction = 0.75f;
	private static final float timeResolution = 0.00000005f;
	private static final float cameraSpeedGain = 2.0f;
	private static final float maxCameraSpeed = 0.5f;

	
	private final CoreController core;
	
	private WorldElement focus;
	
	private float dyaw = 0.0f;
	private float yaw = 0.0f;
	private float dpitch = 0.0f;
	private float pitch = 0.0f;
	private Vector3f position = new Vector3f();
	private Vector3f velocity = new Vector3f();
	private Vector3f acceleration = new Vector3f();
	private Vector3f cameraDirection = new Vector3f();
	private float generalAcceleration = initialAcceleration;
	
	private long lastPositionUpdate = System.nanoTime();
	
	protected UserController(CoreController core) {
		this.core = core;
	}
	
	public String getUserDescription() {
		String elementName;
		
		if(focus != null) {
			if(focus.getName() == null || focus.getName().isEmpty()) {
				elementName = "a mysterious structure";
			} else {
				elementName = "'" + focus.getName() + "'";
			}
		} else {
			elementName = "the space";
		}
		
		return position + " | Looking at " + elementName + " (" + (focus != null ? focus.getID() : -1) + ")";
	}
	
	public Vector3f getPosition() {
		return new Vector3f(position);
	}
	
	public WorldElement getFocus() {
		return focus;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getAcceleration() {
		return generalAcceleration;
	}
	
	protected void setCameraDirection(Vector3f direction) {
		this.cameraDirection = direction;
	}
	
	
	protected void updatePhysics() {
		long deltaTime = System.nanoTime() - lastPositionUpdate;
		lastPositionUpdate += deltaTime;
		
		Vector3f acceleration = new Vector3f(this.acceleration).mul(deltaTime * timeResolution);
		
		yaw += dyaw * deltaTime * timeResolution * cameraSpeedGain;
		pitch += dpitch * deltaTime * timeResolution * cameraSpeedGain;
						
		velocity.x += Math.sin(yaw) * acceleration.x - Math.cos(yaw) * acceleration.z;
		velocity.y += acceleration.y;
		velocity.z += -Math.cos(yaw) * acceleration.x - Math.sin(yaw) * acceleration.z;
				
		velocity.mul(1.0f - spaceFriction * deltaTime * timeResolution);
		dyaw *= 1.0f - cameraFriction * deltaTime * timeResolution;
		dpitch *= 1.0f - cameraFriction * deltaTime * timeResolution;
										
		position.add(new Vector3f(velocity).mul(deltaTime * timeResolution));
	}
	
	protected void detectCollisions(HostStructureStack stack, Geometry coreGeometry, Geometry codeGeometry) {		
		MergedGeometry merged = new MergedGeometry();
				
		try {
			merged.addGeometry(coreGeometry);
			merged.addGeometry(codeGeometry);
			merged.requestReconstruction();
			merged.processEventQueue();
			merged.sort(position);
		} catch (GeometryException exception) {
			Debug.exception(exception);
		}
		
		List<WorldElement> collisions = merged.getCollisions(position);
		Collections.reverse(collisions);
		Iterator<WorldElement> iterator = collisions.iterator();
		WorldElement last = null;
		int sync = 0;
				
		while(iterator.hasNext()) {
			WorldElement next = iterator.next();
			
			if(stack.contains(next.getID())) {
				sync++;
			} else {
				last = next;
				break;
			}
		}
				
		synchronized(stack) {
			int delta = stack.getGeneration() - sync;
			
			for(int i = 0; i < delta; i++) {
				// Clear the entries which are no longer colliding with the user
				WorldElement current = merged.getElementByID(stack.pop());
				
				if(current != null) {
					exitElement(current);
				}
			}
			
			if(last != null) {
				// Resync with new entries
				if(enterElement(last)) {
					stack.push(last.getID());
				} else {
					last = null;
				}
				
				while(iterator.hasNext()) {
					WorldElement next = iterator.next();
					
					if(enterElement(next)) {
						stack.push(next.getID());
					}
				}
			}
						
			if(delta != 0 || last != null) {
				core.onDiscontinuityCrossed();
			}
		}
	}
	
	private boolean enterElement(WorldElement element) {
		if(element.isAccessAllowed()) {
			return true;
		} else {
			this.repulseFrom(element.getCenter());
			return false;
		}
	}
	
	private void exitElement(WorldElement element) {
		;
	}
	
 	private void repulseFrom(Vector3f position) {
		Vector3f normal = new Vector3f(this.position).sub(position).normalize();
		
		if(new Vector3f(this.velocity).normalize().dot(normal) < 0.0d) {
			this.velocity.reflect(normal).normalize(this.generalAcceleration * 32.0f);
		}
 	}
	
 	protected void detectFocus(HostStructureStack stack, Geometry coreGeometry, Geometry codeGeometry) {
		int generation = stack.getGeneration();
		WorldElement current = coreGeometry.getElementByID(stack.getCurrent());
		
		float currentSize = coreGeometry.getSize(generation);
		float parentSize = coreGeometry.getSize(generation - 1);
		
		WorldElement found = null;
		float distance = Float.MAX_VALUE;
		
		for(WorldElement element : coreGeometry.getElements()) {
			boolean isInsideSameStruct = generation == 0 || current.getCenter().distance(element.getCenter()) < parentSize;
			boolean isSameSize = Math.abs(element.getSize() - currentSize) < Math.ulp(currentSize);
			boolean isSizeOfActorObject = Math.abs(element.getSize() - parentSize / 40.0f) < Math.ulp(currentSize);
		
			if((isSameSize || isSizeOfActorObject) && isInsideSameStruct) {
				float currentDistance = this.position.distance(element.getCenter());
				
				if(currentDistance < distance) {
					if(element.collidesWith(new Vector3f(cameraDirection).normalize(currentDistance).add(this.position))) {
						found = element;
						distance = currentDistance;
					}
				}
			}
		}
				
		for(WorldElement element : codeGeometry.getElements()) {
			float currentDistance = this.position.distance(element.getCenter());
			
			if(currentDistance < distance) {
				if(element.collidesWith(new Vector3f(cameraDirection).normalize(currentDistance).add(this.position))) {
					found = element;
					distance = currentDistance;
				}
			}
		}

		this.focus = found;
	}

	
	protected void setPosition(Vector3f newPosition) {
		position.set(newPosition);
		
		dyaw = 0.0f;
		yaw = 0.0f;
		dpitch = 0.0f;
		pitch = 0.0f;
	}
	
	protected void setFocus(WorldElement element) {		
		if(!core.getHostStack().isEmpty()) {				
			Vector3f focus = element.getCenter().sub(position).normalize();
			
			dyaw = 0.0f;
			yaw = Geometry.PI - (float) Math.atan2(focus.x, focus.z);
			dpitch = 0.0f;
			pitch = (float) Math.asin(focus.y);	
		}
	}
	
	protected void reset() {
		position.set(0.0f);
		resetAcceleration();
	}
	
	protected void resetAcceleration() {
		this.generalAcceleration = initialAcceleration;
	}
	
	public void resetMotion() {
		acceleration.set(0.0f);
	}
	
	protected void moveX(int factor) {
		acceleration.x = factor * generalAcceleration;
	}
	
	protected void moveY(int factor) {
		acceleration.y = factor * generalAcceleration;
	}
	
	protected void moveZ(int factor) {
		acceleration.z = factor * generalAcceleration;
	}
	
	protected void updateYaw(float radians) {
		if(Math.abs(dyaw + radians) < maxCameraSpeed) {
			dyaw += radians;
		}
	}
	
	protected void updatePitch(float radians) {
		if(Math.abs(dpitch + radians) < maxCameraSpeed) {
			dpitch += radians;
		}
	}
	
	protected void normaliseCameraQuaternion() {
		float halfPI = Geometry.PI / 2.0f;
		
		if(this.pitch > halfPI) {
			this.pitch = halfPI;
		} else if(this.pitch < -halfPI) {
			this.pitch = -halfPI;
		}
				
		this.yaw %= 4.0f * halfPI;
	}
	
	protected void accelerate(double rate) {
		generalAcceleration *= rate;
	}
}
