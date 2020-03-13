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
package ch.innovazion.arionide.project.managers;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.project.CodeChain;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Manager;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.managers.specification.SpecificationManager;
import ch.innovazion.arionide.project.mutables.MutableActor;
import ch.innovazion.arionide.project.mutables.MutableCodeChain;
import ch.innovazion.arionide.project.mutables.MutableHierarchyElement;
import ch.innovazion.arionide.project.mutables.MutableInheritanceElement;
import ch.innovazion.arionide.project.mutables.MutableStructure;
import ch.innovazion.arionide.ui.ApplicationTints;

public class StructureManager extends Manager {
		
	private final HostStructureStack hostStack;
	private final ResourceAllocator allocator;
	private final CodeManager codeManager;
	private final SpecificationManager specManager;
	private final InheritanceManager inheritanceManager;
	
	public StructureManager(Project project) {
		super(project.getStorage());
		
		hostStack = new HostStructureStack();
		allocator = new ResourceAllocator(project);
		codeManager = new CodeManager(getStorage(), allocator, hostStack);
		specManager = new SpecificationManager(getStorage());
		inheritanceManager = new InheritanceManager(getStorage());
	}

	public MessageEvent newStructure(String name) {
		if(getStructures().values().stream().filter(meta -> meta.getName().equals(name)).count() > 0) {
			return new MessageEvent("This structure name is already used", MessageType.ERROR);
		} else {
			int structureID = allocator.allocStructure();
			
			MutableHierarchyElement structure = new MutableHierarchyElement(structureID, new ArrayList<>());
			List<MutableHierarchyElement> generation = getMutableCurrentGeneration();
			
			if(generation == null) {
				return new MessageEvent("Invalid parent hierarchy", MessageType.ERROR);
			}
						
			generation.add(structure);
			saveHierarchy();

			getInheritance().put(structureID, new MutableInheritanceElement());
			saveInheritance();
			
			getCallGraph().add(structure);
			saveCallGraph();
			
			getStructures().put(structureID, new MutableActor(structureID, allocator.allocSpecification()));
			getCode().put(structureID, new MutableCodeChain());

			MessageEvent message = setName(structureID, name);
			
			if(message.getMessageType() == MessageType.ERROR) {
				return message;
			}
			
			CodeChain code = getCode().get(hostStack.getCurrent());
			
			if(code != null && !code.isAbstract()) {
				// Saving is delegated to insertCode
				getCodeManager().insertCode(-1, code.getID(0));
			} else {
				saveStructures();
				saveCode();
			}
			
			return success();
		}
	}
	
	public int addInstructionDefinition(String name, int color, Specification specification) {
		if(getStructures().values().stream().filter(meta -> meta.getName().equals(name)).count() > 0) {
			return -1;
		} else {
			int structureID = allocator.allocStructure();
					
			MutableHierarchyElement structure = new MutableHierarchyElement(structureID, new ArrayList<>());
			List<MutableHierarchyElement> generation = getMutableCurrentGeneration();
			
			if(generation == null) {
				return -1;
			}
			
			generation.add(structure);
			saveHierarchy();
			
			MutableStructure meta = new MutableActor(structureID, allocator.allocSpecification());
			meta.setName(name);
			meta.setColorID(color);
			meta.setAccessAllowed(false);
			meta.setSpecification(specification);
			
			getStructures().put(structureID, meta);
			saveStructures();
						
			return structureID;
		}
	}
	
	public int retrieveInstructionDefinition(String name) {
		return getStructures().entrySet().stream().filter(meta -> meta.getValue().getName().equals(name)).findAny().orElse(new SimpleEntry<Integer, MutableStructure>(-1, null)).getKey();
	}
	
	public MessageEvent deleteStructure(int id) {
		Iterator<MutableHierarchyElement> iterator = getMutableCurrentGeneration().iterator();
		
		while(iterator.hasNext()) {
			HierarchyElement element = iterator.next();
			
			if(element.getID() == id) {
				iterator.remove();
				saveHierarchy();
				
				getInheritance().remove(id);
				saveInheritance();
				
				deleteMeta(element);
				saveStructures();
				
				return success();
			}
		}
		
		return new MessageEvent("This structure doesn't exist anymore", MessageType.ERROR);			
	}
	
	private void deleteMeta(HierarchyElement element) {
		getStructures().remove(element.getID());
		element.getChildren().stream().forEach(this::deleteMeta);
	}
	
	public MessageEvent abstractifyStructure(int id) {		
		getCode().get(id).getMutableList().clear();
		
		List<MutableHierarchyElement> list = getMutableCurrentGeneration();
		
		for(MutableHierarchyElement element : list) {
			if(element.getID() == id) {
				abstractify(element);
			}
		}

		saveCode();
		
		return success();			
	}
	
	private void abstractify(MutableHierarchyElement element) {		
		codeManager.resetCodeChain(element.getID());

		for(MutableHierarchyElement child : element.getMutableChildren()) {
			abstractify(child);
		}
	}
	
	public MessageEvent setName(int id, String name) {
		MutableStructure meta = getStructures().get(id);
		
		if(meta != null) {
			meta.setName(name);
			saveStructures();
			
			if(name.isEmpty()) {
				return new MessageEvent("Empty strings are discouraged", MessageType.WARN);
			} else {
				return success();
			}
		} else {
			return new MessageEvent("Invalid structure id", MessageType.ERROR);
		}
	}
	
	public MessageEvent setComment(int id, String comment) {
		MutableStructure meta = getStructures().get(id);
		
		if(meta != null) {
			if(comment.isEmpty()) {
				return warn();
			} else {
				meta.setComment(comment);
				saveStructures();
				return success();
			}
		} else {
			return new MessageEvent("Invalid structure id", MessageType.ERROR);
		}
	}
	
	public MessageEvent setColor(int id, int colorID) {
		if(ApplicationTints.hasColor(colorID)) {
			MutableStructure meta = getStructures().get(id);
			
			if(meta != null) {
				meta.setColorID(colorID);
				
				saveStructures();
				
				return success();
			} else {
				return new MessageEvent("Invalid structure id", MessageType.ERROR);
			}
		} else {
			return new MessageEvent("Invalid color id", MessageType.ERROR);
		}
	}
	
	public HostStructureStack getHostStack() {
		return hostStack;
	}
	
	public ResourceAllocator getResourceAllocator() {
		return allocator;
	}
	
	public CodeManager getCodeManager() {
		return codeManager;
	}
	
	public SpecificationManager getSpecificationManager() {
		return specManager;
	}
	
	public InheritanceManager getInheritanceManager() {
		return inheritanceManager;
	}
	
	private List<MutableHierarchyElement> getMutableCurrentGeneration() {
		return getCurrentGeneration0(getHierarchy(), hostStack.getStack(), MutableHierarchyElement::getMutableChildren);
	}

	public List<HierarchyElement> getCurrentGeneration(List<HierarchyElement> root) {
		return getCurrentGeneration(root, hostStack.getStack());
	}
	
	public List<HierarchyElement> getCurrentGeneration(List<HierarchyElement> root, Collection<Integer> stateStack) {
		return getCurrentGeneration0(root, stateStack, HierarchyElement::getChildren);
	}
	
	private <T extends HierarchyElement> List<T> getCurrentGeneration0(List<T> root, Collection<Integer> stateStack, Function<T, List<T>> expander) {
		List<Integer> parents = new ArrayList<>(stateStack);
		
		Collections.reverse(parents);
		
		for(Integer id : parents) {
			boolean found = false;
			
			for(T bro : root) {
				if(bro.getID() == id) {
					root = expander.apply(bro);
					found = true;
					break;
				}
			}
			
			if(!found && id >= 0) {
				return new ArrayList<>();
			}
		}
		
		return root;
	}
}