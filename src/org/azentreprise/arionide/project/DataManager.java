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
package org.azentreprise.arionide.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.ui.menu.edition.Coloring;

public class DataManager {
	
	private final Project project;
	private final Storage storage;
	private final HostStructureStack hostStack;
	
	public DataManager(Project project) {
		this.project = project;
		this.storage = this.project.getStorage();
		this.hostStack = new HostStructureStack();
	}
	
	public int allocSpecification() {
		return this.alloc("specificationGen");
	}
	
	public int allocStructure() {
		return this.alloc("structureGen");
	}
	
	private int alloc(String object) {
		int id = this.project.getProperty(object, Coder.integerDecoder).intValue();
		this.project.setProperty(object, (long) id + 1, Coder.integerEncoder); // Increment generator
		this.project.save();
		
		return id;
	}
	
	public MessageEvent newStructure(String name) {
		if(this.storage.getStructureMeta().values().stream().filter(meta -> meta.getName().equals(name)).count() > 0) {
			return new MessageEvent("This structure name is already used", MessageType.ERROR);
		} else {
			int structureID = this.allocStructure();
			
			HierarchyElement structure = new HierarchyElement(structureID, new ArrayList<>());
			
			List<HierarchyElement> generation = this.getCurrentGeneration(this.storage.hierarchy);
			
			if(generation == null) {
				return new MessageEvent("Invalid parent hierarchy", MessageType.ERROR);
			}
			
			generation.add(structure);
			this.storage.saveHierarchy();

			this.storage.inheritance.put(structureID, new InheritanceElement());
			this.storage.saveInheritance();
			
			this.storage.callGraph.add(structure);
			this.storage.saveCallGraph();
			
			this.storage.structMeta.put(structureID, new StructureMeta(this.allocSpecification()));
			
			this.storage.code.put(structureID, new ArrayList<>());
			
			MessageEvent message = this.setName(structureID, name);
			
			if(message.getMessageType() != MessageType.SUCCESS) {
				return message;
			} 
				
			if(this.project.getLanguage().isReady()) {
				this.hostStack.push(structureID);
				
				message = this.insertCode(0, this.retrieveInstructionDefinition("init"));
				
				this.hostStack.pop();								
				
				if(message.getMessageType() == MessageType.ERROR) {
					return message;
				}
			}
			
			return new MessageEvent("Structure created", MessageType.SUCCESS);
		}
	}
	
	public int addInstructionDefinition(String name, int color, Specification specification) {
		if(this.storage.getStructureMeta().values().stream().filter(meta -> meta.getName().equals(name)).count() > 0) {
			return -1;
		} else {
			int structureID = this.allocStructure();
					
			HierarchyElement structure = new HierarchyElement(structureID, new ArrayList<>());
			
			List<HierarchyElement> generation = this.getCurrentGeneration(this.storage.hierarchy);
			
			if(generation == null) {
				return -1;
			}
			
			generation.add(structure);
			this.storage.saveHierarchy();
			
			StructureMeta meta = new StructureMeta(this.allocSpecification());
			meta.setName(name);
			meta.setColorID(color);
			meta.setAccessAllowed(false);
			meta.setSpecification(specification);
			
			this.storage.structMeta.put(structureID, meta);
			this.storage.saveStructureMeta();
						
			return structureID;
		}
	}
	
	public int retrieveInstructionDefinition(String name) {
		return this.storage.getStructureMeta().entrySet().stream().filter(meta -> meta.getValue().getName().equals(name)).findAny().get().getKey();
	}
	
	public MessageEvent deleteStructure(int id) {
		Iterator<HierarchyElement> iterator = this.getCurrentGeneration(this.storage.hierarchy).iterator();
		
		while(iterator.hasNext()) {
			HierarchyElement element = iterator.next();
			
			if(element.getID() == id) {
				iterator.remove();
				this.storage.saveHierarchy();
				
				this.storage.inheritance.remove(id);
				this.storage.saveInheritance();
				
				this.deleteMeta(element);
				this.storage.saveStructureMeta();
				
				return new MessageEvent("Structure deleted", MessageType.SUCCESS);
			}
		}
		
		return new MessageEvent("This structure doesn't exist anymore", MessageType.ERROR);			
	}
	
	private void deleteMeta(HierarchyElement element) {
		this.storage.structMeta.remove(element.getID());
		element.getChildren().stream().forEach(this::deleteMeta);
	}
	
	public MessageEvent inherit(int child, int parent) {
		if(child != parent) {
			InheritanceElement parentElement = this.storage.getInheritance().get(parent);
			InheritanceElement childElement = this.storage.getInheritance().get(child);
			
			if(this.recursiveCheck(childElement, parent) && this.recursiveCheck(parentElement, child)) { /* Check for cycle */
				List<Integer> children = parentElement.children;
				List<Integer> parents = childElement.parents;
				
				if(!children.contains(child)) {
					children.add(child);
				}
		
				if(!parents.contains(parent)) {
					parents.add(parent);
				}
				
				this.storage.saveInheritance();
				
				return new MessageEvent("Inheritance updated", MessageType.SUCCESS);
			} else {
				return new MessageEvent("Cyclic inheritance is not permitted", MessageType.ERROR);
			}
		} else {
			return new MessageEvent("A structure cannot inherit itself", MessageType.ERROR);
		}
	}
	
	private boolean recursiveCheck(InheritanceElement object, int potentialParent) {		
		for(int nextGenParent : object.parents) {
			if(nextGenParent == potentialParent || !this.recursiveCheck(this.storage.getInheritance().get(nextGenParent), potentialParent)) {
				return false;
			}
		}
		
		return true;
	}
	
	public MessageEvent desinherit(Integer parent, Integer child) {
		this.storage.getInheritance().get(parent).children.remove(child);
		this.storage.getInheritance().get(child).parents.remove(parent);
		
		this.storage.saveInheritance();
		
		return new MessageEvent("Inheritance updated", MessageType.SUCCESS);
	}
	
	public MessageEvent insertCode(int index, int instructionID) {
		int structureID = this.allocStructure();
		
		StructureMeta meta = new StructureMeta(-1);
		meta.setComment("code@" + instructionID);
		meta.setSpecification(new Specification(this.storage.getStructureMeta().get(instructionID).getSpecification()));
		meta.setAccessAllowed(false);
		
		this.storage.structMeta.put(structureID, meta);
		this.storage.saveStructureMeta();
		
		this.getCurrentCode().add(index, new HierarchyElement(structureID, new ArrayList<>()));
		this.storage.saveCode();
			
		return new MessageEvent("Added an instruction to the code", MessageType.SUCCESS);
	}

	public MessageEvent deleteCode(int id) {
		HierarchyElement element = this.getCurrentCode().remove(id);
		this.storage.saveCode();
		
		if(element != null) {
			this.storage.structMeta.remove(element.getID());
		}
		
		return new MessageEvent("Removed an instruction from the code", MessageType.SUCCESS);
	}
	
	public MessageEvent setName(int id, String name) {
		StructureMeta meta = this.storage.getStructureMeta().get(id);
		
		if(meta != null) {
			meta.setName(name);
			this.storage.saveStructureMeta();
			
			if(name.isEmpty()) {
				return new MessageEvent("Empty strings are discouraged", MessageType.WARN);
			} else {
				return new MessageEvent("Update successful", MessageType.SUCCESS);
			}
		} else {
			return new MessageEvent("Invalid structure id", MessageType.ERROR);
		}
	}
	
	public MessageEvent setLanguage(int element, int language) {
		HierarchyElement object = this.storage.getHierarchy().get(element);
		
		this.setLanguage0(object, language);
		
		this.storage.saveStructureMeta();
		
		return new MessageEvent("Language successfully set", MessageType.SUCCESS);
	}
	
	private void setLanguage0(HierarchyElement element, int language) {
		this.storage.getStructureMeta().get(element.getID()).setLanguage(language);
		
		for(HierarchyElement children : element.getChildren()) {
			this.setLanguage0(children, language);
		}
	}
	
	public MessageEvent addSpecificationElement(Specification spec, SpecificationElement element) {
		this.storage.structMeta.values().stream()
			.map(StructureMeta::getSpecification)
			.filter(spec::hasSameOrigin)
			.forEach(other -> other.getElements().add(element));
	
		this.storage.saveStructureMeta();
		
		return new MessageEvent("Specification successfully updated", MessageType.SUCCESS);
	}
	
	public MessageEvent deleteSpecificationElement(Specification spec, int id) {
		this.doForeachConnectedSpecification(spec, id, l -> l.getElements().remove(id));

		this.storage.saveStructureMeta();

		return new MessageEvent("Specification element successfully deleted", MessageType.SUCCESS);
	}
	
	public MessageEvent refactorSpecificationName(Specification spec, int id, String newName) {
		this.doForeachConnectedSpecification(spec, id, l -> l.getElements().get(id).setName(newName));

		this.storage.saveStructureMeta();
		
		return new MessageEvent("Name successfully refactored", MessageType.SUCCESS);
	}

	public MessageEvent refactorSpecificationType(Specification spec, int id, int newType) {
		this.doForeachConnectedSpecification(spec, id, l -> ((Data) l.getElements().get(id)).setType(newType));
		
		this.storage.saveStructureMeta();
		
		return new MessageEvent("Type successfully refactored", MessageType.SUCCESS);
	}
	
	public MessageEvent refactorParameterName(Specification spec, int id, int data, String newName) {
		this.doForeachConnectedSpecification(spec, id, l -> ((Reference) l.getElements().get(id)).getNeededParameters().get(data).setName(newName));

		this.storage.saveStructureMeta();
		
		return new MessageEvent("Name successfully refactored", MessageType.SUCCESS);
	}
	
	public MessageEvent refactorParameterType(Specification spec, int id, int data, int newType) {
		this.doForeachConnectedSpecification(spec, id, l -> ((Data) ((Reference) l.getElements().get(id)).getNeededParameters().get(data)).setType(newType));
		
		this.storage.saveStructureMeta();
		
		return new MessageEvent("Type successfully refactored", MessageType.SUCCESS);
	}
	
	private void doForeachConnectedSpecification(Specification spec, int id, Consumer<Specification> action) {
		this.storage.structMeta.values().stream()
			.map(StructureMeta::getSpecification)
			.filter(spec::hasSameOrigin)
			.forEach(action);
	}
	
	public MessageEvent setColor(int id, int colorID) {
		if(Coloring.hasColor(colorID)) {
			StructureMeta meta = this.storage.getStructureMeta().get(id);
			
			if(meta != null) {
				meta.setColorID(colorID);
				
				this.storage.saveStructureMeta();
				
				return new MessageEvent("Color sucessfully changed", MessageType.SUCCESS);
			} else {
				return new MessageEvent("Invalid structure id", MessageType.ERROR);
			}
		} else {
			return new MessageEvent("Invalid color id", MessageType.ERROR);
		}
	}
	
	public HostStructureStack getHostStack() {
		return this.hostStack;
	}
	
	public List<HierarchyElement> getCurrentCode() {
		return this.storage.getCode().get(this.hostStack.getCurrent());
	}
	
	public List<HierarchyElement> getCurrentGeneration(List<HierarchyElement> root) {
		Collection<Integer> parents = this.hostStack.getStack();
		
		for(Integer id : parents) {
			boolean found = false;
			
			for(HierarchyElement bro : root) {
				if(bro.getID() == id) {
					root = bro.children;
					found = true;
					break;
				}
			}
			
			if(!found) {
				return new ArrayList<>();
			}
		}
		
		return root;
	}
}