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
package ch.innovazion.arionide.project.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.Specification;
import ch.innovazion.arionide.menu.edition.Coloring;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Manager;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.mutables.MutableCodeChain;
import ch.innovazion.arionide.project.mutables.MutableHierarchyElement;
import ch.innovazion.arionide.project.mutables.MutableInheritanceElement;
import ch.innovazion.arionide.project.mutables.MutableStructureMeta;

public class DataManager extends Manager {
	
	private final HostStructureStack hostStack;
	private final ResourceAllocator allocator;
	private final CodeManager codeManager;
	private final SpecificationManager specManager;
	private final InheritanceManager inheritanceManager;
	
	public DataManager(Project project) {
		super(project.getStorage());
		
		this.hostStack = new HostStructureStack();
		this.allocator = new ResourceAllocator(project);
		this.codeManager = new CodeManager(this.getStorage(), allocator, hostStack);
		this.specManager = new SpecificationManager(this.getStorage());
		this.inheritanceManager = new InheritanceManager(this.getStorage());
	}
	
	public MessageEvent newStructure(String name) {
		return this.newStructure(name, true);
	}

	public MessageEvent newStructure(String name, boolean withCodeBase) {
		if(this.getMeta().values().stream().filter(meta -> meta.getName().equals(name)).count() > 0) {
			return new MessageEvent("This structure name is already used", MessageType.ERROR);
		} else {
			int structureID = this.allocator.allocStructure();
			
			MutableHierarchyElement structure = new MutableHierarchyElement(structureID, new ArrayList<>());
			List<MutableHierarchyElement> generation = this.getMutableCurrentGeneration(this.getHierarchy());
			
			if(generation == null) {
				return new MessageEvent("Invalid parent hierarchy", MessageType.ERROR);
			}
			
			generation.add(structure);
			this.saveHierarchy();

			this.getInheritance().put(structureID, new MutableInheritanceElement());
			this.saveInheritance();
			
			this.getCallGraph().add(structure);
			this.saveCallGraph();
			
			this.getMeta().put(structureID, new MutableStructureMeta(this.allocator.allocSpecification()));
			this.getCode().put(structureID, new MutableCodeChain());
			// Save responsibility is declined to the invocation of "insertCode"
			
			MessageEvent message = this.setName(structureID, name);
			
			if(message.getMessageType() != MessageType.SUCCESS) {
				return message;
			} 
			
			if(withCodeBase) {
				this.hostStack.push(structureID);
				message = this.codeManager.insertCode(0, this.retrieveInstructionDefinition("init"));
				this.hostStack.pop();								
			}
			
			if(message.getMessageType() == MessageType.ERROR) {
				return message;
			}
			
			return new MessageEvent("Structure created", MessageType.SUCCESS);
		}
	}
	
	public int addInstructionDefinition(String name, int color, Specification specification) {
		if(this.getMeta().values().stream().filter(meta -> meta.getName().equals(name)).count() > 0) {
			return -1;
		} else {
			int structureID = this.allocator.allocStructure();
					
			MutableHierarchyElement structure = new MutableHierarchyElement(structureID, new ArrayList<>());
			List<MutableHierarchyElement> generation = this.getMutableCurrentGeneration(this.getHierarchy());
			
			if(generation == null) {
				return -1;
			}
			
			generation.add(structure);
			this.saveHierarchy();
			
			MutableStructureMeta meta = new MutableStructureMeta(this.allocator.allocSpecification());
			meta.setName(name);
			meta.setColorID(color);
			meta.setAccessAllowed(false);
			meta.setSpecification(specification);
			
			this.getMeta().put(structureID, meta);
			this.saveMeta();
						
			return structureID;
		}
	}
	
	public int retrieveInstructionDefinition(String name) {
		return this.getMeta().entrySet().stream().filter(meta -> meta.getValue().getName().equals(name)).findAny().get().getKey();
	}
	
	public MessageEvent deleteStructure(int id) {
		Iterator<MutableHierarchyElement> iterator = this.getMutableCurrentGeneration(this.getHierarchy()).iterator();
		
		while(iterator.hasNext()) {
			HierarchyElement element = iterator.next();
			
			if(element.getID() == id) {
				iterator.remove();
				this.saveHierarchy();
				
				this.getInheritance().remove(id);
				this.saveInheritance();
				
				this.deleteMeta(element);
				this.saveMeta();
				
				return new MessageEvent("Structure deleted", MessageType.SUCCESS);
			}
		}
		
		return new MessageEvent("This structure doesn't exist anymore", MessageType.ERROR);			
	}
	
	private void deleteMeta(HierarchyElement element) {
		this.getMeta().remove(element.getID());
		element.getChildren().stream().forEach(this::deleteMeta);
	}
	
	public MessageEvent setName(int id, String name) {
		MutableStructureMeta meta = this.getMeta().get(id);
		
		if(meta != null) {
			meta.setName(name);
			this.saveMeta();
			
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
		HierarchyElement object = this.getHierarchy().get(element);
		
		this.setLanguage0(object, language);
		
		this.saveMeta();
		
		return new MessageEvent("Language successfully set", MessageType.SUCCESS);
	}
	
	private void setLanguage0(HierarchyElement element, int language) {
		this.getMeta().get(element.getID()).setLanguage(language);
		
		for(HierarchyElement children : element.getChildren()) {
			this.setLanguage0(children, language);
		}
	}
	
	public MessageEvent setColor(int id, int colorID) {
		if(Coloring.hasColor(colorID)) {
			MutableStructureMeta meta = this.getMeta().get(id);
			
			if(meta != null) {
				meta.setColorID(colorID);
				
				this.saveMeta();
				
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
	
	public ResourceAllocator getResourceAllocator() {
		return this.allocator;
	}
	
	public CodeManager getCodeManager() {
		return this.codeManager;
	}
	
	public SpecificationManager getSpecificationManager() {
		return this.specManager;
	}
	
	public InheritanceManager getInheritanceManager() {
		return this.inheritanceManager;
	}
	
	private List<MutableHierarchyElement> getMutableCurrentGeneration(List<MutableHierarchyElement> root) {
		Collection<Integer> parents = this.hostStack.getStack();
		
		for(Integer id : parents) {
			boolean found = false;
			
			for(MutableHierarchyElement bro : root) {
				if(bro.getID() == id) {
					root = bro.getMutableChildren();
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
	
	public List<HierarchyElement> getCurrentGeneration(List<HierarchyElement> root) {
		Collection<Integer> parents = this.hostStack.getStack();
		
		for(Integer id : parents) {
			boolean found = false;
			
			for(HierarchyElement bro : root) {
				if(bro.getID() == id) {
					root = bro.getChildren();
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