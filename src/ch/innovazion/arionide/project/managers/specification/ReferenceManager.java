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
package ch.innovazion.arionide.project.managers.specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.HostStructureStack;
import ch.innovazion.arionide.project.managers.ResourceAllocator;
import ch.innovazion.arionide.project.mutables.MutableActor;
import ch.innovazion.arionide.project.mutables.MutableCodeChain;
import ch.innovazion.arionide.project.mutables.MutableHierarchyElement;
import ch.innovazion.arionide.project.mutables.MutableStructure;

public class ReferenceManager extends ContextualManager<Reference> {
	
	private final ResourceAllocator allocator;
	private final HostStructureStack hostStack;
	
	protected ReferenceManager(Storage storage, ResourceAllocator allocator, HostStructureStack hostStack) {
		super(storage);
		
		this.allocator = allocator;
		this.hostStack = hostStack;
	}
	
	public List<Callable> getAccessibleCallables(Structure parent) {
		if(hostStack.getCurrent() == parent.getIdentifier()) {
			List<MutableHierarchyElement> elements = getCurrentGeneration();
			return elements.stream().map(HierarchyElement::getID).map(getStructures()::get).collect(Collectors.toList());
		} else {
			return Arrays.asList();
		}
	}

	public List<String> getParameterNames() {
		return getContext().getLazyParameters().stream().map(Parameter::getName).collect(Collectors.toList());
	}
	
	public MessageEvent create(String name) {
		getContext().addLazyParameter(new Parameter(name, new Information()));
		saveStructures();
		return success();
	}
	
	public MessageEvent remove(int paramID) {
		getContext().removeLazyParameter(paramID);
		saveStructures();
		return success();
	}
	
	public ParameterValue getValue(int paramID) {
		return getContext().getLazyParameters().get(paramID).getValue();
	}
	
	public MessageEvent assignCallable(Callable target) {
		getContext().setTarget(target);
		saveCode();
		return success();
	}
	
	public MessageEvent assignLambda() {
		int structureID = allocator.allocStructure();
		int specificationID = allocator.allocSpecification();
		
		List<MutableHierarchyElement> elements = getCurrentGeneration();

		MutableHierarchyElement structure = new MutableHierarchyElement(structureID, new ArrayList<>());
		
		elements.add(structure);
		saveHierarchy();
		
		MutableStructure actor = new MutableActor(structureID, specificationID);
		
		actor.setLambda(true);
		
		for(Parameter lazy : getContext().getLazyParameters()) {
			actor.getSpecification().getParameters().add(lazy);
		}
		
		getStructures().put(structureID, actor);
		saveStructures();
		
		getCode().put(structureID, new MutableCodeChain());
		saveCode();
		
		getContext().setTarget(actor);
		
		return success();
	}
	
	private List<MutableHierarchyElement> getCurrentGeneration() {
		List<Integer> parents = new ArrayList<>(hostStack.getStack());
		List<MutableHierarchyElement> root = getHierarchy();
		
		Collections.reverse(parents);
		
		for(Integer id : parents) {
			boolean found = false;
			
			for(MutableHierarchyElement bro : root) {
				if(bro.getID() == id) {
					root = bro.getMutableChildren();
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
