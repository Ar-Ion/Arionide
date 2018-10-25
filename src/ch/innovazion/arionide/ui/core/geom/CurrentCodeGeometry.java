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

import java.util.Arrays;
import java.util.List;

import org.joml.Vector3f;

public class CurrentCodeGeometry extends Geometry {
	
	private Geometry geom;
	
	public void updateCodeGeometry(Geometry geom) {
		this.geom = geom;
	}

	protected void construct(List<WorldElement> elements, List<Connection> connections) throws GeometryException {
		throw new GeometryException("The current code geometry is just a shadow wrapper of a concrete Geometry");
	}
	
	public WorldElement getElementByID(int id) {
		if(this.geom != null) {
			return this.geom.getElementByID(id);
		} else {
			return null;
		}
	}
	
	public List<WorldElement> getElements() {
		if(this.geom != null) {
			return this.geom.getElements();
		} else {
			return Arrays.asList();
		}
	}
	
	public List<Connection> getConnections() {
		if(this.geom != null) {
			return this.geom.getConnections();
		} else {
			return Arrays.asList();
		}
	}
	
	public List<WorldElement> getCollisions(Vector3f camera) {
		if(this.geom != null) {
			return this.geom.getCollisions(camera);
		} else {
			return Arrays.asList();
		}
	}
	
	public void requestReconstruction() {
		if(this.geom != null) {
			this.geom.requestReconstruction();
		}
	}
	
	public void processEventQueue() throws GeometryException {
		throw new GeometryException("The current code geometry is just a shadow wrapper of a concrete Geometry");
	}

	public float getSize(int generation) {
		return this.geom != null ? this.geom.getSize(generation) : -666.0f;
	}
	
	public float getRelativeSize(int generation) {
		return this.geom != null ? this.geom.getRelativeSize(generation) : -666.0f;
	}
}
