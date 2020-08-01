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
import java.util.List;
import java.util.stream.Collectors;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.ContextualManager;
import ch.innovazion.arionide.project.managers.StructureManager;
import ch.innovazion.arionide.project.mutables.MutableStructure;

public class ReferenceManager extends ContextualManager<Reference> {
	
	private final StructureManager structManager;
	
	protected ReferenceManager(Storage storage, StructureManager structManager) {
		super(storage);
		this.structManager = structManager;
	}
	
	public List<Callable> getAccessibleCallables() {
		List<HierarchyElement> elements = structManager.getCurrentGeneration(getStorage().getHierarchy());
		List<Callable> output = new ArrayList<>();
				
		if(!structManager.getHostStack().isEmpty()) {
			output.add(structManager.getCurrentStructure());
		}
		
		for(HierarchyElement child : elements) {
			Structure struct = getStructures().get(child.getID());
			output.add(struct);
		}
					
		return output;
	}

	public List<String> getParameterNames() {
		return getContext().getLazyParameters().stream().map(Parameter::getName).collect(Collectors.toList());
	}
	
	public MessageEvent create(String name) {
		getContext().addLazyParameter(new Parameter(name, new Information("Information")));
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
		String lambdaName = this.structManager.getCurrentStructure().getName();
		
		MessageEvent event;
		int i = 0;
		
		do {
			event = structManager.newStructure("Lambda (" + lambdaName + "#" + i + ")");
		} while(event.getMessageType() != MessageType.ERROR);
		                                     
		List<HierarchyElement> generation = structManager.getCurrentGeneration(getStorage().getHierarchy());
		HierarchyElement last = generation.get(generation.size() - 1);
		MutableStructure struct = getStructures().get(last.getID());
					
		struct.setLambda(true);
		
		saveStructures();
		
		getContext().setTarget(struct);
		
		return success();
	}
}
