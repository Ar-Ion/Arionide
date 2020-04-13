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
import java.util.Map;
import java.util.Random;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.project.CodeChain;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.core.Geometry;

public class CodeGeometry extends Geometry {
	
	private static final float relativeSize = 0.05f;
	private static final float relativeDistance = 0.12f;
	private static final float axisEntropy = 0.3f;
	private static final float axisCorrection = 0.3f;
	private static final float axisCorrectionFlexibility = 5.0f;

	private WorldElement container;
	
	private WorldElementFactory factory = new WorldElementFactory();
	
	public CodeGeometry(WorldElement container) {
		assert container != null;
		this.container = container;
	}
	
	public void updateSeed(long newSeed) {
		super.updateSeed(newSeed);
		this.factory = new WorldElementFactory(newSeed);
	}
	
	public void updateContainer(WorldElement container) {
		this.container = container;
	}
	
	@IAm("constructing the code geometry")
	protected void construct(List<WorldElement> elements, List<Connection> connections) {
		this.factory.reset();
			
		Storage storage = this.getProject().getStorage();
				
		List<WorldElement> specification = new ArrayList<>();

		CodeChain chain = storage.getCode().get(this.container.getID());
		
		if(chain != null) {
			this.build(this.container, chain.list(), elements, specification, connections, storage.getStructures(), this.container.getSize() * relativeSize);
		}
		
		elements.addAll(specification); // So that one can iterate through the code with the wheel having to pass through an instruction's specification.
	}
	
	private void build(WorldElement parent, List<? extends HierarchyElement> input, List<WorldElement> outputElements, List<WorldElement> outputSpecification, List<Connection> outputConnections, Map<Integer, Structure> mapping, float size) {
		Vector3f position = parent.getCenter();
		Vector3f axis = parent.getAxis();
		WorldElement previous = null;

		List<List<? extends HierarchyElement>> groups = new ArrayList<>();
		int groupStart = 0;
		
		for(int i = 0; i < input.size(); i++) {
			Structure struct = mapping.get(input.get(i).getID());
						
			if(struct != null) {
				Specification spec = struct.getSpecification();
				
				for(Parameter param : spec.getParameters()) {
					if(param.getValue() instanceof Reference) {
						Reference ref = (Reference) param.getValue();
						Callable target = ref.getTarget();
						
						if(target.getIdentifier() == parent.getID()) {
							groups.add(input.subList(groupStart, i + 1));
							groupStart = i + 1;
						}
					}
				}
			}
		}
				
		if(groupStart < input.size()) {
			groups.add(input.subList(groupStart, input.size()));
		}
				
		float y = 0.0f;
		
		for(int i = 0; i < groups.size(); i++) {
			List<? extends HierarchyElement> group = groups.get(i);
			
			Quaternionf mainQuaternion;
			float deltaHeight;
			float spreadFactor;

			if(i < groups.size() - 1) {
				mainQuaternion = new Quaternionf(new AxisAngle4f(Geometry.PI * 2.0f / group.size(), new Vector3f(0.0f, 1.0f, 0.0f)));
				deltaHeight = -parent.getSize() * relativeDistance / group.size();
				spreadFactor = 0.5f;
			} else {
				mainQuaternion = new Quaternionf(new AxisAngle4f(Geometry.PI * 2.0f / 16, new Vector3f(0.0f, 1.0f, 0.0f)));
				deltaHeight = (-0.75f * parent.getSize() - y) / group.size();
				spreadFactor = 1.25f;
			}
			
			for(int j = 0; j < group.size(); j++) {
				HierarchyElement element = group.get(j);
				Structure struct = mapping.get(element.getID());
							
				if(struct != null) {
					
					Vector4f color = new Vector4f(ApplicationTints.getColorByID(struct.getColorID()), 0.5f);
					Vector4f spotColor = new Vector4f(ApplicationTints.getColorByID(struct.getSpotColorID()), 1.0f);
					
					/* Process instruction */
					Vector3f current = this.factory.getAxisGenerator().get();
					
					factory.updateAxisGenerator(() -> current.cross(axis));
					WorldElement output = this.factory.make(element.getID(), struct.getName(), struct.getComment(), new Vector3f(position), color, spotColor, size, struct.isAccessAllowed());
					outputElements.add(output);
					
					/* Process connection to previous instruction */
					if(previous != null) {
						outputConnections.add(new Connection(previous, output));
						
						if(i < groups.size() - 1 && j == group.size() - 1) {
							outputConnections.add(new Connection(output, outputElements.get(0)));
						}
					}
					
					previous = output;
					
					/* Process specification */					
					List<Parameter> specification = struct.getSpecification().getParameters();
										
					Vector3f specPos = new Vector3f(axis).cross(0.0f, 1.0f, 0.0f).normalize(size * 1.5f);
					
					Quaternionf specQuaternion = new Quaternionf(new AxisAngle4f(Geometry.PI * 2.0f / specification.size(), new Vector3f(axis).normalize()));
					
					for(int k = 0; k < specification.size(); k++) {
						Parameter param = specification.get(k);
						
						WorldElement specObject = this.factory.make((((k + 1) & 0x7F) << 24) | element.getID(), param.getName(), param.getDisplayValue(), new Vector3f(specPos).add(position), color, spotColor, size / 3.0f, struct.isAccessAllowed());
						
						outputSpecification.add(specObject);
						outputConnections.add(new Connection(output, specObject));
						
						specPos.rotate(specQuaternion);
						
						if(param.getValue() instanceof Reference) {
							Reference ref = (Reference) param.getValue();
							Callable target = ref.getTarget();
							
							if(target != null) {
								Structure refStruct = mapping.get(target.getIdentifier());
								
								if(refStruct.isLambda()) {
									CodeChain chain = getProject().getStorage().getCode().get(target.getIdentifier());
									
									if(chain != null) {
										this.build(specObject, chain.list(), outputElements, outputSpecification, outputConnections, mapping, size / 3.0f);
									}
								}
							}
						}
					}
					
					axis.normalize(2 * spreadFactor * size);
					axis.rotate(mainQuaternion);
					position.add(axis);
					
					position.y = parent.getCenter().y - y;
					y -= deltaHeight;
					
					if(i == groups.size() - 1) {
						applyDerivation(axis, new Vector3f(position).sub(parent.getCenter()));
					}
					
					/* Process children and apply transformation */
					this.build(parent, element.getChildren(), outputElements, outputSpecification, outputConnections, mapping, size);
				}
			}
		}
	}
	
	private void applyDerivation(Vector3f axis, Vector3f relPos) {
		float length = relPos.length() + axis.length();
		
		Random random = this.factory.getRandom();
		
		Vector3f entropy = new Vector3f(random.nextFloat() - 0.5f, 0.0f, random.nextFloat() - 0.5f);
		Vector3f correction = new Vector3f(axis).reflect(new Vector3f(relPos).negate().normalize());
		
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