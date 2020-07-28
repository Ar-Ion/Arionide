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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.core.Geometry;

public abstract class HierarchicalGeometry extends Geometry {
	
	public static final float MAKE_THE_UNIVERSE_GREAT_AGAIN = 1E0f;
	
	private static final float initialSize = 1.0f * MAKE_THE_UNIVERSE_GREAT_AGAIN;
	
	private final float relativeSize;
	private final float relativeDistance;
	
	protected WorldElementFactory factory = new WorldElementFactory();
	
	public HierarchicalGeometry(float relativeSize, float relativeDistance) {
		this.relativeSize = relativeSize;
		this.relativeDistance = relativeDistance;
	}
	
	public void updateSeed(long newSeed) {
		super.updateSeed(newSeed);
		this.factory = new WorldElementFactory(newSeed);
	}
	
	@IAm("constructing a hierarchical geometry")
	protected void construct(List<WorldElement> elements, List<Connection> connections) throws GeometryException {
		Storage storage = this.getProject().getStorage();
		
		this.factory.reset();

		Map<Integer, Structure> structures = storage.getStructures();
		List<HierarchyElement> input = getHierarchyElements();
		
		
		WorldElement main = this.factory.makeRandomTrivial();
		
		this.construct0(main, withoutLambdas(input, structures), elements, structures, initialSize);
	}
	
	
	private List<HierarchyElement> withoutLambdas(List<HierarchyElement> input, Map<Integer, Structure> structures) {
		return input.stream().filter(e -> !structures.get(e.getID()).isLambda()).collect(Collectors.toList());
	}
	
			
	private void construct0(WorldElement parent, List<HierarchyElement> input, List<WorldElement> output, Map<Integer, Structure> structures, float size) throws GeometryException {
		if(input != null && input.size() > 0) {
			Quaternionf quaternion = new Quaternionf(new AxisAngle4f(Geometry.PI * 2.0f / (input.size() - (input.contains(HierarchyElement.dummy) ? 1 : 0)), parent.getAxis()));
			Vector3f base = input.size() > 1 || parent.getID() != -1 ? parent.getBaseVector() : new Vector3f();
			
			for(HierarchyElement element : input) {
				Vector3f position = new Vector3f(base.rotate(quaternion)).mul(this.relativeDistance * size / this.relativeSize).add(parent.getCenter());
				
				Structure structure = structures.get(element.getID());

				if(structure != null) {
					WorldElement object = constructElement(parent, structure, position, size, output);
					this.construct0(object, withoutLambdas(element.getChildren(), structures), output, structures, this.relativeSize * size);
				} else {
					throw new GeometryException("Invalid element ID " + element.getID());
				}
			}
		}
	}
	
	protected WorldElement constructElement(WorldElement parent, Structure struct, Vector3f position, float size, List<WorldElement> output) {
		Vector4f color = new Vector4f(ApplicationTints.getColorByID(struct.getColorID()), 0.5f);
		Vector4f spotColor = new Vector4f(ApplicationTints.getColorByID(struct.getSpotColorID()), 0.5f);
		boolean access = struct.isAccessAllowed();
		
		WorldElement element = factory.make(struct.getIdentifier(), parent.getID(), struct.getName(), struct.getComment(), position, color, spotColor, size, access);
		output.add(element);
		return element;
	}
	
	public float getSize(int generation) {
		return initialSize * this.getRelativeSize(generation);
	}
	
	public float getRelativeSize(int generation) {
		return (float) Math.pow(this.relativeSize, generation);
	}
	
	protected abstract List<HierarchyElement> getHierarchyElements();
}