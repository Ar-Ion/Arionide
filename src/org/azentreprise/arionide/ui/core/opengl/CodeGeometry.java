/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
import java.util.stream.Stream;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.menu.edition.Coloring;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CodeGeometry implements Geometry {
	
	private static final float structRelSize = 0.05f;
	private static final float structRelDistance = 0.1f;
	private static final float axisEntropy = 1.0f;
	private static final float axisCorrection = 0.2f;
	private static final float axisCorrectionFlexibility = 5.0f;

	private final Random random = new Random();
	private final List<WorldElement> elements = Collections.synchronizedList(new ArrayList<>());
	
	private long seed;
	
	public void setGenerationSeed(long seed) {
		this.seed = seed;
	}
	
	@IAm("building the code geometry")
	protected void buildGeometry(Project project, WorldElement element) {
		synchronized(this.elements) {
			this.elements.clear();
						
			if(element != null && element.getID() > -1) {
				Storage storage = project.getStorage();
				storage.loadData(element.getID());
				
				WorldElement.setSeed(this.seed * element.getID());
				this.random.setSeed(this.seed * element.getID());
				
				this.build(element, storage.getStructureMeta(), storage.getCurrentData(), element.getSize() * structRelSize);
			}
		}
	}
	
	private void build(WorldElement parent, Map<Integer, StructureMeta> meta, List<HierarchyElement> code, float size) {
		Vector3f axis = parent.getAxis();
		Vector3f position = parent.getCenter();
		
		for(HierarchyElement element : code) {
			StructureMeta structMeta = meta.get(element.getID());
						
			if(structMeta != null) {
				int index = structMeta.getComment().indexOf("code@");
				
				if(index > -1) {					
					StructureMeta resolved = meta.get(Integer.parseInt(structMeta.getComment().substring(index + 5)));
					
					Vector4f color = new Vector4f(Coloring.getColorByID(resolved.getColorID()), 0.5f);
					Vector3f spotColor = new Vector3f(Coloring.getColorByID(resolved.getSpotColorID()));
	
					axis.normalize(parent.getSize() * structRelDistance);
					
					WorldElement.setAxisGenerator(() -> WorldElement.RANDOM_GENERATOR.get().cross(axis));
					WorldElement object = new WorldElement(element.getID(), resolved.getName(), new Vector3f(position), color, spotColor, size, structMeta.isAccessAllowed());
					this.elements.add(object);
					
					this.build(parent, meta, element.getChildren(), size);
					position.add(axis);
					this.applyDerivation(axis, new Vector3f(position).sub(parent.getCenter()).div(parent.getSize()).mul(2.0f));
				}
			}
		}
	}
	
	private void applyDerivation(Vector3f axis, Vector3f relPos) {
		float length = relPos.length();
		
		Vector3f entropy = new Vector3f(this.random.nextFloat() - 0.5f, this.random.nextFloat() - 0.5f, this.random.nextFloat() - 0.5f);
		Vector3f correction = new Vector3f(axis).reflect(new Vector3f(relPos.negate().normalize()));
		
		entropy.normalize(axis.length() * axisEntropy);
		correction.normalize(axis.length() * axisCorrection * (float) Math.pow(length, axisCorrectionFlexibility));

		axis.add(entropy);
		axis.add(correction);
	}
	
	public List<WorldElement> getCollisions(Vector3f player) {
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
}