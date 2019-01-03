package ch.innovazion.arionide.ui.core.geom;

import java.util.ArrayList;
import java.util.List;

public class MergedGeometry extends Geometry {

	private final List<Geometry> geometries = new ArrayList<>();
	
	protected void construct(List<WorldElement> elements, List<Connection> connections) throws GeometryException {
		for(Geometry geom : geometries) {
			elements.addAll(geom.getElements());
			connections.addAll(geom.getConnections());
		}
	}
	
	public void addGeometry(Geometry geom) {
		geometries.add(geom);
	}

	public float getSize(int generation) {
		return 0;
	}

	public float getRelativeSize(int generation) {
		return 0;
	}
}