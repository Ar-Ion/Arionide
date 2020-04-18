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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.LanguageManager;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Manager;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.specification.SpecificationManager;
import ch.innovazion.arionide.project.mutables.MutableActor;
import ch.innovazion.arionide.project.mutables.MutableAtomicStructure;
import ch.innovazion.arionide.project.mutables.MutableCode;
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
		codeManager = new CodeManager(getStorage(), allocator);
		specManager = new SpecificationManager(getStorage(), allocator, this);
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
			
			MutableStructure actor = new MutableActor(structureID, allocator.allocSpecification());
			MutableCodeChain chain = new MutableCodeChain();
			
			getStructures().put(structureID, actor);
			getCode().put(structureID, chain);
			
			MessageEvent message = rename(structureID, name);
			
			if(message.getMessageType() == MessageType.ERROR) {
				return message;
			}
			
			Structure current = getStructures().get(hostStack.getCurrent());
						
			if(current != null) {
				Integer entryID = getLanguages().get(current.getLanguage());
							
				if(entryID != null) {
					actor.setLanguage(current.getLanguage());
					
					Structure entryCodeBase = getStructures().get(entryID);
					
					int entryStructID = allocator.allocStructure();
					MutableCode code = new MutableCode(entryStructID, entryCodeBase);
					
					code.setLanguage(current.getLanguage());
					
					getStructures().put(entryStructID, code);
					chain.getMutableList().add(new MutableHierarchyElement(entryStructID, new ArrayList<>()));
				}
			}
			
			saveStructures();
			saveCode();
			
			return success();
		}
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
		
		return new MessageEvent("Structure deleted", MessageType.ERROR);			
	}
	
	private void deleteMeta(HierarchyElement element) {
		getStructures().remove(element.getID());
		element.getChildren().stream().forEach(this::deleteMeta);
	}
	
	public MessageEvent setLanguage(int id, String language) {		
		getCode().get(id).getMutableList().clear();
		
		List<MutableHierarchyElement> list = getMutableCurrentGeneration();
		
		for(MutableHierarchyElement element : list) {
			if(element.getID() == id) {
				if(language != null) {
					Language lang = LanguageManager.get(language);
					Integer installationPoint = getLanguages().get(language);
					Structure entryCodeBase = null;
					
					if(installationPoint == null) {
						System.out.println("Installing language " + language + " onto project...");
						
						try {
							entryCodeBase = installLanguage(lang);
							getLanguages().put(language, entryCodeBase.getIdentifier()); // Register language in the project root
						} catch(Exception e) {
							e.printStackTrace();
							return new MessageEvent("Failed to install language " + lang, MessageType.ERROR);
						}
					} else {
						entryCodeBase = getStructures().get(installationPoint);
					}
					
					setLanguage(element, language, entryCodeBase);
				} else {
					resetCodeChain(element);
				}
			}
		}
		
		saveCode();
		saveStructures();
		saveLanguages();
				
		return success();
	}
	
	private void setLanguage(MutableHierarchyElement element, String language, Structure entryCodeBase) {
		int currentID = element.getID();
		
		getStructures().get(currentID).setLanguage(language);
			
		int entryInstanceID = allocator.allocStructure();
		
		MutableCodeChain chain = new MutableCodeChain();
		MutableCode entryPointInfo = new MutableCode(entryInstanceID, entryCodeBase);
		MutableHierarchyElement entryPoint = new MutableHierarchyElement(entryInstanceID, new ArrayList<>());
		
		entryPointInfo.setLanguage(language);
		
		chain.getMutableList().add(entryPoint);
		
		getStructures().put(entryInstanceID, entryPointInfo);	
		getCode().put(currentID, chain);
		
		
		for(MutableHierarchyElement child : element.getMutableChildren()) {
			setLanguage(child, language, entryCodeBase); // Recursion
		}
	}
	
	private void resetCodeChain(MutableHierarchyElement element) {		
		codeManager.resetCodeChain(element.getID());

		for(MutableHierarchyElement child : element.getMutableChildren()) {
			resetCodeChain(child);
		}
	}
	
	/*
	 *  Returns the structure of the entry point
	 */
	private Structure installLanguage(Language lang) {		
		if(lang != null) {
			Structure entry = installInstruction(lang.getEntryPoint());
			lang.getStandardInstructions().forEach(this::installInstruction);
			return entry;
		} else {
			throw new IllegalArgumentException("Unable to find requested language");
		}
	}
	
	/*
	 * Return the structure of the installed instruction (using the last signature)
	 */
	private Structure installInstruction(Instruction instr) {
		int size = instr.createStructureModel().getPossibleSignatures().size();
		
		MutableStructure struct = null;
		
		for(int i = 0; i < size; i++) {
			int structID = allocator.allocStructure();
			int specID = allocator.allocSpecification();
			
			struct = new MutableAtomicStructure(structID, specID, instr.createStructureModel(), i);
			getStructures().put(structID, struct);
		}
				
		return struct;
	}
	
	public MessageEvent rename(int id, String name) {
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
	
	public MessageEvent setComment(int id, List<String> comment) {
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
	
	public InheritanceManager getInheritanceManager() {
		return inheritanceManager;
	}
	
	public SpecificationManager getSpecificationManager() {
		return specManager;
	}
	
	public SpecificationManager loadSpecificationManager(Structure context) {
		specManager.setContext(context.getSpecification());
		return specManager;
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