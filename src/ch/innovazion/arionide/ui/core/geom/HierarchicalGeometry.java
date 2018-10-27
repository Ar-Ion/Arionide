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

import java.util.List;
import java.util.Map;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.ui.menu.edition.Coloring;

public abstract class HierarchicalGeometry extends Geometry {
	
	public static final float MAKE_THE_UNIVERSE_GREAT_AGAIN = 1E17f;
	
	private static final float initialSize = 1.0f * MAKE_THE_UNIVERSE_GREAT_AGAIN;
	
	private final float relativeSize;
	private final float relativeDistance;
	
	private WorldElementFactory factory = new WorldElementFactory();
	
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

		Map<Integer, StructureMeta> metaData = storage.getStructureMeta();
		
		WorldElement main = this.factory.makeRandomTrivial();
						
		this.construct0(main, this.getHierarchyElements(), elements, metaData, initialSize);
	}
			
	private void construct0(WorldElement parent, List<HierarchyElement> input, List<WorldElement> output, Map<Integer, StructureMeta> metaData, float size) throws GeometryException {
		if(input != null && input.size() > 0) {
			Quaternionf quaternion = new Quaternionf(new AxisAngle4f(Geometry.PI * 2.0f / (input.size() - (input.contains(HierarchyElement.dummy) ? 1 : 0)), parent.getAxis()));
			Vector3f base = input.size() > 1 ? parent.getBaseVector() : new Vector3f();
			
			for(HierarchyElement element : input) {
				Vector3f position = new Vector3f(base.rotate(quaternion)).mul(this.relativeDistance * size / this.relativeSize).add(parent.getCenter());
				
				StructureMeta structMeta = metaData.get(element.getID());

				if(structMeta != null) {
					Vector4f color = new Vector4f(Coloring.getColorByID(structMeta.getColorID()), 0.6f);
					Vector3f spotColor = new Vector3f(Coloring.getColorByID(structMeta.getSpotColorID()));
					boolean access = structMeta.isAccessAllowed();
					
					WorldElement object = this.factory.make(element.getID(), structMeta.getName(), structMeta.getComment(), position, color, spotColor, size, access);
					output.add(object);
					
					this.construct0(object, element.getChildren(), output, metaData, this.relativeSize * size);
				} else {
					throw new GeometryException("Invalid element ID " + element.getID());
				}
			}
		}
	}
	
	public float getSize(int generation) {
		return initialSize * this.getRelativeSize(generation);
	}
	
	public float getRelativeSize(int generation) {
		return (float) Math.pow(this.relativeSize, generation);
	}
	
	protected abstract List<HierarchyElement> getHierarchyElements();
}