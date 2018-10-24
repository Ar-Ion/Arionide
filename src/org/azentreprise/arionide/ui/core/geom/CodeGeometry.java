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
package org.azentreprise.arionide.ui.core.geom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.menu.edition.Coloring;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CodeGeometry extends Geometry {
	
	private static final float relativeSize = 0.05f;
	private static final float relativeDistance = 0.1f;
	private static final float axisEntropy = 1.0f;
	private static final float axisCorrection = 0.2f;
	private static final float axisCorrectionFlexibility = 5.0f;

	private final WorldElement container;
	
	private WorldElementFactory factory = new WorldElementFactory();
	
	public CodeGeometry(WorldElement container) {
		this.container = container;
	}
	
	public void updateSeed(long newSeed) {
		super.updateSeed(newSeed);
		this.factory = new WorldElementFactory(newSeed);
	}
	
	@IAm("constructing the code geometry")
	protected void construct(List<WorldElement> elements, List<Connection> connections) {		
		System.out.println("Constructing code...");

		this.factory.reset();
		
		Storage storage = this.getProject().getStorage();
				
		List<WorldElement> specification = new ArrayList<>();
		List<HierarchyElement> input = storage.getCode().get(this.container.getID());
		
		this.build(this.container, input, elements, specification, connections, storage.getStructureMeta(), this.container.getSize() * relativeSize);

		elements.addAll(specification); // So that one can iterate through the code with the wheel having to pass through an instruction's specification.
	}
	
	private void build(WorldElement parent, List<HierarchyElement> input, List<WorldElement> outputElements, List<WorldElement> outputSpecification, List<Connection> outputConnections, Map<Integer, StructureMeta> meta, float size) {
		Vector3f axis = parent.getAxis();
		Vector3f position = parent.getCenter();
				
		for(HierarchyElement element : input) {
			StructureMeta structMeta = meta.get(element.getID());
						
			if(structMeta != null) {
				int index = structMeta.getComment().indexOf("code@");
				
				if(index > -1) {					
					StructureMeta resolved = meta.get(Integer.parseInt(structMeta.getComment().substring(index + 5)));
					
					Vector4f color = new Vector4f(Coloring.getColorByID(resolved.getColorID()), 0.5f);
					Vector3f spotColor = new Vector3f(Coloring.getColorByID(resolved.getSpotColorID()));
					
					/* Process instruction */
					axis.normalize(parent.getSize() * relativeDistance);
					
					Vector3f current = this.factory.getAxisGenerator().get();
					
					this.factory.updateAxisGenerator(() -> current.cross(axis));
					WorldElement output = this.factory.make(element.getID(), resolved.getName(), resolved.getComment(), new Vector3f(position), color, spotColor, size, structMeta.isAccessAllowed());
					outputElements.add(output);
					
					/* Process specification */					
					List<SpecificationElement> specification = structMeta.getSpecification().getElements();
										
					Vector3f specPos = new Vector3f(axis).cross(0.0f, 1.0f, 0.0f).normalize(size * 1.5f);
					
					Quaternionf specQuaternion = new Quaternionf(new AxisAngle4f(Geometry.PI * 2.0f / specification.size(), new Vector3f(axis).normalize()));
					
					for(int i = 0; i < specification.size(); i++) {
						SpecificationElement specElement = specification.get(i);
						WorldElement specObject = this.factory.make((((i + 1) & 0xFF) << 24) | element.getID(), specElement.getName(), specElement.getValue(), new Vector3f(specPos).add(position), color, spotColor, size / 5.0f, structMeta.isAccessAllowed());
						
						outputSpecification.add(specObject);
						outputConnections.add(new Connection(output, specObject));
						
						specPos.rotate(specQuaternion);
					}
					
					/* Process children and apply transformation */
					this.build(parent, element.getChildren(), outputElements, outputSpecification, outputConnections, meta, size);
					position.add(axis);
					this.applyDerivation(axis, new Vector3f(position).sub(parent.getCenter()).div(parent.getSize()).mul(2.0f));
				}
			}
		}
	}
	
	private void applyDerivation(Vector3f axis, Vector3f relPos) {
		double length = relPos.length();
		
		Random random = this.factory.getRandom();
		
		Vector3f entropy = new Vector3f(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, random.nextFloat() - 0.5f);
		Vector3f correction = new Vector3f(axis).reflect(new Vector3f(relPos.negate().normalize()));
		
		entropy.normalize(axis.length() * axisEntropy);
		correction.normalize(axis.length() * axisCorrection * (float) Math.pow(length, axisCorrectionFlexibility));

		axis.add(entropy);
		axis.add(correction);
	}
	
	public float getSize(int generation) {
		throw new UnsupportedOperationException("Code instructions are atomic by definition");
	}
	
	public float getRelativeSize(int generation) {
		throw new UnsupportedOperationException("Code instructions are atomic by definition");
	}
	
	public WorldElement getContainer() {
		return this.container;
	}
	
	public int hashCode() {
		return this.container.hashCode();
	}
	
	public boolean equals(Object other) {
		return other instanceof CodeGeometry && ((CodeGeometry) other).container.equals(this.container);
	}
	
	public String toString() {
		return "<CodeGeometry for " + this.container + " (" + this.container.getID() +")>";
	}
}