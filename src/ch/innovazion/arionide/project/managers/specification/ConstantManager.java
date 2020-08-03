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

import java.util.Arrays;
import java.util.List;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.symbols.AtomicValue;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.SymbolResolutionException;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.ContextualManager;
import ch.innovazion.arionide.project.managers.StructureManager;
import ch.innovazion.arionide.project.mutables.MutableActor;


/*
 * This class is a mix between InformationManager.java and VariableManager.java
 * Inheritance principle was discarded for this design because too many small things must be changed
 * between the implementations. Code would have been unreadable, I think.
 */
public class ConstantManager extends ContextualManager<Node> {
	
	private final StructureManager structManager;
	
	protected ConstantManager(Storage storage, StructureManager structManager) {
		super(storage);
		this.structManager = structManager;
	}
	
	public List<Node> getConstants() {
		Structure parent = structManager.getCurrentStructure();
		
		if(parent instanceof MutableActor) {
			return ((MutableActor) parent).getWrapper().getConstants().getNodes();
		} else {
			return Arrays.asList();
		}
	}
	
	// Following code from VariableManager.java
	public MessageEvent createAndAssign(String name, Node model) {
		Structure parent = structManager.getCurrentStructure();

		if(parent instanceof MutableActor) {
			Node constant = model.clone();
			
			constant.label(name);
						
			try {
				((MutableActor) parent).getWrapper().getConstants().connect(constant);
			} catch (SymbolResolutionException e) {
				return new MessageEvent("Unable to connect a new variable to the structure's state", MessageType.ERROR);
			}

			return assign(constant);
		} else {
			return new MessageEvent("This structure does not support variables", MessageType.ERROR);
		}
	}

	public MessageEvent delete(String name) {
		Structure parent = structManager.getCurrentStructure();

		if(parent instanceof MutableActor) {
			try {
				Node state = ((MutableActor) parent).getWrapper().getConstants();
				Node resolved = state.resolve(name);
				state.disconnect(resolved);
			} catch (SymbolResolutionException e) {
				return new MessageEvent("Unable to disconnect this variable from the structure's state", MessageType.ERROR);
			}
			
			saveStructures();
			
			return success();
		} else {
			return new MessageEvent("This structure does not support variables", MessageType.ERROR);
		}
	}
	
	public MessageEvent rename(String currentName, String newName) {
		Structure parent = structManager.getCurrentStructure();

		if(parent instanceof MutableActor) {
			try {
				Node state = ((MutableActor) parent).getWrapper().getConstants();
				Node resolved = state.resolve(currentName);
				resolved.label(newName);
			} catch (SymbolResolutionException e) {
				return new MessageEvent("Unable to rename this constant", MessageType.ERROR);
			}
			
			saveStructures();
			
			return success();
		} else {
			return new MessageEvent("This structure does not support constants", MessageType.ERROR);
		}
	}
	
	// From InformationManager.java
	public MessageEvent assign(Node newValue) {
		try {
			if(hasContext()) {
				Node prevValue = getContext();
				Node parent = prevValue.getParent();
	
				if(prevValue != null && parent != null) {
					if(parent instanceof AtomicValue) {
						return new MessageEvent("Cannot assign a value to an atomic information", MessageType.ERROR);
					}
					
					newValue.label(prevValue.getLabel());
									
					int index = parent.indexOf(prevValue);
					parent.disconnect(prevValue);
					parent.connect(newValue, index);
				}
			}
			
			setContext(newValue);

			return success();
		} catch (SymbolResolutionException exception) {
			return new MessageEvent(exception.getMessage(), MessageType.ERROR);
		}
	}
}
