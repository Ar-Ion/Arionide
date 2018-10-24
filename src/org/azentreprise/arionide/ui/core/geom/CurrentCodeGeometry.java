package org.azentreprise.arionide.ui.core.geom;

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
