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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureElement;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldGeometry {
	
	private final List<WorldElement> hierarchy = new ArrayList<>();
	private final List<WorldElement> inheritance = new ArrayList<>();
	private final List<WorldElement> callgraph = new ArrayList<>();

	private List<WorldElement> current = new ArrayList<>();
	
	private final IEventDispatcher dispatcher;
	
	protected WorldGeometry(IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	@IAm("building the world's geometry")
	protected void buildGeometry(Project project) {
		Storage storage = project.getStorage();
		
		WorldElement main = new WorldElement("Lambda", new Vector3f(0.0f, 0.0f, 0.0f), new Vector4f(0.929f, 0.0392f, 0.247f, 0.3f), 1.0f);
		
		this.hierarchy.add(main);
		
		this.build(main, this.hierarchy, Arrays.asList(new StructureElement("caca", null), new StructureElement("prout", null)), 1.0f);
		
		this.current = hierarchy;
	}
	
	private void build(WorldElement parent, List<WorldElement> list, List<StructureElement> elements, float size) {
		if(elements != null && elements.size() > 0) {
			Quaternionf quaternion = new Quaternionf(new AxisAngle4f((float) Math.PI * 2.0f / elements.size(), parent.getAxis()));
			Vector3f base = parent.getBaseVector();
			
			size /= 20.0f;
			
			for(StructureElement element : elements) {
				Vector3f position = new Vector3f(base.rotate(quaternion)).mul(0.8f);
				
				WorldElement object = new WorldElement(element.getName(), position, new Vector4f(1.0f, 1.0f, 1.0f, 0.3f), size);
				list.add(object);
				
				this.build(object, list, element.getChildren(), size);
			}
		}
	}
	
	protected float getSizeForGeneration(int count) {
		return (float) Math.pow(0.05f, count);
	}

	protected Stream<WorldElement> getCollisions(Vector3f player) {
		return this.current.stream().filter((element) -> element.collidesWith(player));
	}
	
	protected List<WorldElement> getElements() {
		return this.current;
	}
}