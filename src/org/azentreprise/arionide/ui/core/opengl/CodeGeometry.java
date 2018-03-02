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
package org.azentreprise.arionide.ui.core.opengl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.menu.edition.Coloring;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CodeGeometry implements Geometry {
	
	private static final double structRelSize = 0.05d;
	private static final double structRelDistance = 0.1d;
	private static final double axisEntropy = 1.0d;
	private static final double axisCorrection = 0.2d;
	private static final double axisCorrectionFlexibility = 5.0d;

	private final Random random = new Random();
	private final List<WorldElement> elements = Collections.synchronizedList(new ArrayList<>());
	private final List<Connection> connections = Collections.synchronizedList(new ArrayList<>());

	private long seed;
	
	public void setGenerationSeed(long seed) {
		this.seed = seed;
	}
	
	@IAm("building the code geometry")
	protected void buildGeometry(Project project, WorldElement element) {
		synchronized(this.elements) {
			this.elements.clear();
			this.connections.clear();
						
			if(element != null && element.getID() > -1) {
				Storage storage = project.getStorage();
				storage.loadData(element.getID());
				
				WorldElement.setSeed(this.seed * element.getID());
				this.random.setSeed(this.seed * element.getID());
				
				List<WorldElement> specElements = new ArrayList<>();
				
				this.build(element, storage.getStructureMeta(), storage.getCurrentData(), specElements, element.getSize() * structRelSize);
				
				this.elements.addAll(specElements);
			}
		}
	}
	
	private void build(WorldElement parent, Map<Integer, StructureMeta> meta, List<HierarchyElement> code, List<WorldElement> specElements, double size) {
		Vector3d axis = parent.getAxis();
		Vector3d position = parent.getCenter();
				
		for(HierarchyElement element : code) {
			StructureMeta structMeta = meta.get(element.getID());
						
			if(structMeta != null) {
				int index = structMeta.getComment().indexOf("code@");
				
				if(index > -1) {					
					StructureMeta resolved = meta.get(Integer.parseInt(structMeta.getComment().substring(index + 5)));
					
					Vector4f color = new Vector4f(Coloring.getColorByID(resolved.getColorID()), 0.5f);
					Vector3f spotColor = new Vector3f(Coloring.getColorByID(resolved.getSpotColorID()));
					
					/* Process instruction */
					axis.normalize(parent.getSize() * structRelDistance);
					
					WorldElement.setAxisGenerator(() -> WorldElement.RANDOM_GENERATOR.get().cross(axis));
					WorldElement object = new WorldElement(element.getID(), resolved.getName(), resolved.getComment(), new Vector3d(position), color, spotColor, size, structMeta.isAccessAllowed());
					this.elements.add(object);
					
					/* Process specification */					
					List<SpecificationElement> specification = structMeta.getSpecification().getElements();
										
					Vector3d specPos = new Vector3d(axis).cross(0.0d, 1.0d, 0.0d).normalize(size * 1.5d);
					
					Quaterniond specQuaternion = new Quaterniond(new AxisAngle4d(2.0d * Math.PI / specification.size(), new Vector3d(axis).normalize()));
					
					for(int i = 0; i < specification.size(); i++) {
						SpecificationElement specElement = specification.get(i);
						WorldElement specObject = new WorldElement((((i + 1) & 0xFF) << 24) | element.getID(), specElement.getName(), specElement.getValue(), new Vector3d(specPos).add(position), color, spotColor, size / 5.0d, structMeta.isAccessAllowed());
						
						specElements.add(specObject);
						this.connections.add(new Connection(object, specObject));
						
						specPos.rotate(specQuaternion);
					}
					
					/* Process children and apply transformation */
					this.build(parent, meta, element.getChildren(), specElements, size);
					position.add(axis);
					this.applyDerivation(axis, new Vector3d(position).sub(parent.getCenter()).div(parent.getSize()).mul(2.0f));
				}
			}
		}
	}
	
	private void applyDerivation(Vector3d axis, Vector3d relPos) {
		double length = relPos.length();
		
		Vector3d entropy = new Vector3d(this.random.nextDouble() - 0.5d, this.random.nextFloat() - 0.5d, this.random.nextFloat() - 0.5d);
		Vector3d correction = new Vector3d(axis).reflect(new Vector3d(relPos.negate().normalize()));
		
		entropy.normalize(axis.length() * axisEntropy);
		correction.normalize(axis.length() * axisCorrection * Math.pow(length, axisCorrectionFlexibility));

		axis.add(entropy);
		axis.add(correction);
	}
	
	public List<WorldElement> getCollisions(Vector3d player) {
		synchronized(this.elements) {
			List<WorldElement> collisions = new ArrayList<>();
			
			for(WorldElement element : this.elements) {
				if(element != null && element.collidesWith(player)) {
					collisions.add(element);
				}
			}
			
			return collisions;
		}
	}
	
	public List<WorldElement> getElements() {
		return this.elements;
	}

	public List<Connection> getConnections() {
		return this.connections;
	}
}