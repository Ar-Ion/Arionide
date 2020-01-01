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
		setProject(geom.getProject());
	}

	public float getSize(int generation) {
		return 0;
	}

	public float getRelativeSize(int generation) {
		return 0;
	}
}