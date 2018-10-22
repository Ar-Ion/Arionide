package org.azentreprise.arionide.ui.core.geom;

import java.util.List;

public class CurrentCodeGeometry extends Geometry {
	
	private Geometry geom;
	
	public void updateCodeGeometry(Geometry geom) {
		this.geom = geom;
	}

	protected void construct(List<WorldElement> elements, List<Connection> connections) throws GeometryException {
		if(this.geom != null) {
			this.geom.construct(elements, connections);
		}
	}

	public float getSize(int generation) {
		return this.geom != null ? this.geom.getSize(generation) : -666.0f;
	}
	
	public float getRelativeSize(int generation) {
		return this.geom != null ? this.geom.getRelativeSize(generation) : -666.0f;
	}
}
