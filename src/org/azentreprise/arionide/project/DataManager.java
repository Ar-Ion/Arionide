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
package org.azentreprise.arionide.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.ui.menu.edition.Coloring;

public class DataManager {
	
	private final Project project;
	private final Storage storage;
	
	public DataManager(Project project) {
		this.project = project;
		this.storage = this.project.getStorage();
	}
	
	public MessageEvent newStructure(String name, List<Integer> parents) {
		if(this.storage.getStructureMeta().values().stream().filter(meta -> meta.getName().equals(name)).count() > 0) {
			return new MessageEvent("This structure name is already used", MessageType.ERROR);
		} else {
			int structureID = this.project.getProperty("structureGen", Coder.integerDecoder).intValue();
			this.project.setProperty("structureGen", (long) structureID + 1, Coder.integerEncoder); // Increment generator
			this.project.save();
					
			HierarchyElement structure = new HierarchyElement(structureID, new ArrayList<>());
			
			List<HierarchyElement> brothers = this.getBrothers(this.storage.hierarchy, parents);
			
			if(brothers == null) {
				return new MessageEvent("Invalid parent hierarchy", MessageType.ERROR);
			}
			
			brothers.add(structure);
			this.storage.saveHierarchy();

			this.storage.inheritance.put(structureID, new InheritanceElement());
			this.storage.saveInheritance();
			
			this.storage.callGraph.add(structure);
			this.storage.saveCallGraph();
			
			this.storage.structMeta.put(structureID, new StructureMeta());
			
			MessageEvent message = this.setName(structureID, name);
			
			if(message.getMessageType() != MessageType.SUCCESS) {
				return message;
			} else if(this.project.getLanguage().isReady()) {
				this.project.getStorage().loadData(structureID); // Push

				if(this.insertCode(0, this.project.getLanguage().getInstructionSet().getStructureEntry())) {
					if(parents.size() > 0) {
						this.project.getStorage().loadData(parents.get(parents.size() - 1)); // Pop
					}
															
					return new MessageEvent("Structure created", MessageType.SUCCESS);
				} else {
					return new MessageEvent("Couldn't initialize structure", MessageType.ERROR);
				}
			} else {
				return new MessageEvent("#flag0#", MessageType.SUCCESS);
			}
		}
	}
	
	public int installInstruction(String name, int color, List<Integer> parents, Specification specification) {
		if(this.storage.getStructureMeta().values().stream().filter(meta -> meta.getName().equals(name)).count() > 0) {
			return -1;
		} else {
			int structureID = this.project.getProperty("structureGen", Coder.integerDecoder).intValue();
			this.project.setProperty("structureGen", (long) structureID + 1, Coder.integerEncoder); // Increment generator
			this.project.save();
					
			HierarchyElement structure = new HierarchyElement(structureID, new ArrayList<>());
			
			List<HierarchyElement> brothers = this.getBrothers(this.storage.hierarchy, parents);
			
			if(brothers == null) {
				return -1;
			}
			
			brothers.add(structure);
			this.storage.saveHierarchy();
			
			StructureMeta meta = new StructureMeta();
			meta.setName(name);
			meta.setColorID(color);
			meta.setAccessAllowed(false);
			meta.setSpecification(specification);
			
			this.storage.structMeta.put(structureID, meta);
			this.storage.saveStructureMeta();
						
			return structureID;
		}
	}
	
	public int retrieveInstruction(String name) {
		return this.storage.getStructureMeta().entrySet().stream().filter(meta -> meta.getValue().getName().equals(name)).findAny().get().getKey();
	}
	
	public MessageEvent deleteStructure(int id, List<Integer> parents) {
		Iterator<HierarchyElement> iterator = this.getBrothers(this.storage.hierarchy, parents).iterator();
		
		while(iterator.hasNext()) {
			HierarchyElement element = iterator.next();
			
			if(element.getID() == id) {
				iterator.remove();
				this.storage.saveHierarchy();
				
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
	
	public MessageEvent inherit(int id, int parent) {
		if(id != parent) {
			List<Integer> children = this.storage.getInheritance().get(parent).children;
			List<Integer> parents = this.storage.getInheritance().get(id).parents;
			
			if(!children.contains(id)) {
				children.add(id);
			}
	
			if(!parents.contains(parent)) {
				parents.add(parent);
			}
			
			this.storage.saveInheritance();
			
			return new MessageEvent("Inheritance updated", MessageType.SUCCESS);
		} else {
			return new MessageEvent("A structure cannot inherit itself", MessageType.ERROR);
		}
	}
	
	public MessageEvent desinherit(Integer parent, Integer id) {
		this.storage.getInheritance().get(parent).children.remove(id);
		this.storage.getInheritance().get(id).parents.remove(parent);
		
		this.storage.saveInheritance();
		
		return new MessageEvent("Inheritance updated", MessageType.SUCCESS);
	}
	
	public MessageEvent insertCode(int index, String element) {
		if(this.insertCode(index, this.project.getLanguage().getInstructionSet().getInstructionID(element))) {
			return new MessageEvent("Added" + (element.matches("[^aeiou]") ? " an " : " a ") + element + " instruction to the code", MessageType.SUCCESS);
		} else {
			return new MessageEvent("Failed to insert code", MessageType.ERROR);
		}
	}
	
	public boolean insertCode(int index, int instructionID) {
		int structureID = this.project.getProperty("structureGen", Coder.integerDecoder).intValue();
		this.project.setProperty("structureGen", (long) structureID + 1, Coder.integerEncoder); // Increment generator
		this.project.save();
		
		StructureMeta meta = new StructureMeta();
		meta.setComment("code@" + instructionID);
		meta.setSpecification(new Specification(this.storage.getStructureMeta().get(instructionID).getSpecification()));
		meta.setAccessAllowed(false);
		
		this.storage.structMeta.put(structureID, meta);
		this.storage.saveStructureMeta();
		
		this.storage.currentData.add(index, new HierarchyElement(structureID, new ArrayList<>()));
		this.storage.saveData();
		
		return true;
	}

	public MessageEvent deleteCode(int id) {
		HierarchyElement element = this.storage.currentData.remove(id);
		this.storage.saveData();
		
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
	
	private List<HierarchyElement> getBrothers(List<HierarchyElement> root, List<Integer> parents) {
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
				return null;
			}
		}
		
		return root;
	}
}