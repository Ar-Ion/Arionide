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
import java.util.Map;
import java.util.stream.Stream;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureElement;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.menu.Coloring;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldGeometry {
	
	public static final float STRUCTURE_INITIAL_SIZE = 1.0f;
	public static final float STRUCTURE_RELATIVE_SIZE = 0.1f;
	
	// The distance from the center of a parent structure to the center of a child structure relative to the size of the parent structure.
	
	public static final float SUB_STRUCT_DIST_CENTER_REL_SIZE_HIERARCHY = 0.75f; 
	public static final float SUB_STRUCT_DIST_CENTER_REL_SIZE_INHERITANCE = 2.5f;
	
	private final List<WorldElement> hierarchy = new ArrayList<>();
	private final List<WorldElement> inheritance = new ArrayList<>();
	private final List<WorldElement> callGraph = new ArrayList<>();

	private final IEventDispatcher dispatcher;
	
	private List<WorldElement> current = this.hierarchy;
	private RenderingScene currentScene = RenderingScene.HIERARCHY;
	
	private List<StructureElement> inheritanceBuffer = Arrays.asList();
	private List<StructureElement> callGraphBuffer = Arrays.asList();
	
	private InheritanceGenerator inheritanceGenerator;

	private float currentSubStructDistCenterRelSize = 0.0f;
	
	private WorldElement selected;
	
	public WorldGeometry(IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@IAm("building the world's geometry")
	protected void buildGeometry(Project project) {
		
		this.hierarchy.clear();
				
		if(project != null) {
			Storage storage = project.getStorage();
			
			this.inheritanceGenerator = new InheritanceGenerator(storage.getInheritance(), this::loadInheritance);
			
			WorldElement main = new WorldElement(-1, null, new Vector3f(), new Vector4f(), -1.0f);
			
			Map<Integer, StructureMeta> metaData = storage.getStructureMeta();
			
			float virtualSize = STRUCTURE_INITIAL_SIZE / STRUCTURE_RELATIVE_SIZE;
			
			this.currentSubStructDistCenterRelSize = SUB_STRUCT_DIST_CENTER_REL_SIZE_HIERARCHY;
			this.build(main, this.hierarchy, storage.getHierarchy(), metaData, virtualSize);
			
			this.currentSubStructDistCenterRelSize = SUB_STRUCT_DIST_CENTER_REL_SIZE_INHERITANCE;
			WorldElement.setAxisGenerator(() -> new Vector3f(0.0f, 1.0f, 0.0f));
			WorldElement.setBaseGenerator((axis) -> new Vector3f(1.0f, 1.0f, 0.0f));
			this.build(main, this.inheritance, this.inheritanceBuffer, metaData, virtualSize);
			
			/* Undefined */
			this.build(main, this.callGraph, storage.getCallGraph(), metaData, virtualSize);
		}
		
		this.current = this.hierarchy;
	}
	
	private void build(WorldElement parent, List<WorldElement> list, List<StructureElement> elements, Map<Integer, StructureMeta> metaData, float size) {
		if(elements != null && elements.size() > 0) {
			Quaternionf quaternion = new Quaternionf(new AxisAngle4f((float) Math.PI * 2.0f / elements.size(), parent.getAxis()));
			Vector3f base = parent.getBaseVector();
			
			size *= STRUCTURE_RELATIVE_SIZE;
			
			for(StructureElement element : elements) {
				Vector3f position = new Vector3f(base.rotate(quaternion)).mul(this.currentSubStructDistCenterRelSize * size / STRUCTURE_RELATIVE_SIZE).add(parent.getCenter());
				
				StructureMeta structMeta = metaData.get(element.getID());
				
				if(structMeta != null) {
					Vector4f color = new Vector4f(Coloring.getColorByID(structMeta.getColorID()), 0.3f);
					
					WorldElement object = new WorldElement(element.getID(), structMeta.getName(), position, color, size);
					list.add(object);
					
					this.build(object, list, element.getChildren(), metaData, size);
				}
			}
		}
	}

	private void loadInheritance(List<StructureElement> elements) {
		this.inheritanceBuffer = elements;
	}
	
	protected void loadScene(RenderingScene scene) {
		this.currentScene = scene;
		
		switch(scene) {
			case INHERITANCE:
				if(this.selected != null) {
					this.current = this.inheritance;
					this.inheritanceGenerator.generate(this.selected.getID());
				} else {
					this.dispatcher.fire(new MessageEvent("Please select a structure to view its inheritance", MessageType.ERROR));
				}
				
				break;
			case HIERARCHY:
				this.current = this.hierarchy;
				break;
			case CALLGRAPH:
				if(this.selected != null) {
					this.current = this.callGraph;
				} else {
					this.dispatcher.fire(new MessageEvent("Please first select a structure to view its call graph", MessageType.ERROR));
				}
				
				break;
		}
	}
	
	protected void select(WorldElement element) {
		if(this.selected != element) {
			this.selected = element;
			
			if(this.currentScene.equals(RenderingScene.INHERITANCE)) {
				this.inheritanceGenerator.generate(element.getID());
			}
		}
	}
	
	protected float getSizeForGeneration(int count) {
		return (float) Math.pow(STRUCTURE_RELATIVE_SIZE, count);
	}

	protected Stream<WorldElement> getCollisions(Vector3f player) {
		return this.current.stream().filter((element) -> element.collidesWith(player));
	}
	
	protected List<WorldElement> getElements() {
		return this.current;
	}
}