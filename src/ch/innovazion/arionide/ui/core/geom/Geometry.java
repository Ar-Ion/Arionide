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
package ch.innovazion.arionide.ui.core.geom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

import ch.innovazion.arionide.project.Project;

public abstract class Geometry {
	
	public static final float PI = 3.141592653589793f;
	
	private Map<Integer, WorldElement> elementsByID = new HashMap<>();
	private List<WorldElement> elements = new ArrayList<>();
	private List<Connection> connections = new ArrayList<>();
	private Project project;
	private boolean contructionRequested = false;
	
	public void setProject(Project project) {
		this.project = project;
	}
	
	public void updateSeed(long seed) {
		return;
	}
	
	protected Project getProject() {
		return this.project;
	}
	
	public void sort(Vector3f camera) {
		this.elements.sort((a, b) -> Float.compare(distance(camera, b), distance(camera, a)));
		this.connections.sort((a, b) -> Float.compare(distance(camera, b), distance(camera, a)));
	}
	
	public WorldElement getElementByID(int id) {
		return this.elementsByID.get(id);
	}
	
	public List<WorldElement> getElements() {
		return Collections.unmodifiableList(this.elements);
	}
	
	public List<Connection> getConnections() {
		return Collections.unmodifiableList(this.connections);
	}
	
	public List<WorldElement> getCollisions(Vector3f camera) {
		List<WorldElement> collisions = new ArrayList<>();
		
		for(WorldElement element : this.elements) {
			if(element.collidesWith(camera)) {
				collisions.add(element);
			}
		}
		
		return collisions;
	}
	
	public void requestReconstruction() {
		this.contructionRequested = true;
	}
	
	public void processEventQueue() throws GeometryException {
		if(this.contructionRequested && this.project != null) {
			this.contructionRequested = false;

			this.clearAll();
			
			this.construct(this.elements, this.connections);
			
			this.reconstructMapping();
		}
	}
	
	private void clearAll() {
		this.elementsByID.clear();
		this.elements.clear();
		this.connections.clear();
	}
	
	private void reconstructMapping() throws GeometryException {
		for(WorldElement element : this.elements) {
			if(element != null) {
				this.elementsByID.put(element.getID(), element);
			} else {
				throw new GeometryException("Geometry implementation generated at least one invalid vertex");
			}
		}
	}
	
	public void sync(Geometry other) throws GeometryException {
		this.elementsByID = other.elementsByID;
		this.elements = other.elements;
		this.connections = other.connections;
	}
	
	protected abstract void construct(List<WorldElement> elements, List<Connection> connections) throws GeometryException;
	public abstract float getSize(int generation);
	public abstract float getRelativeSize(int generation); // assert getRelativeSize(0) - 1.0f < Math.ulp(1.0f);

	public static float distance(Vector3f camera, WorldElement element) {
		return element.getCenter().distance(camera) - element.getSize();
	}
	
	public static float distance(Vector3f camera, Connection connection) {
		return connection.getFirstElement().getCenter().add(connection.getSecondElement().getCenter()).mul(0.5f).distance(camera);
	}
}
