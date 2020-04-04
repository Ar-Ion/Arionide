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
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.SymbolResolutionException;
import ch.innovazion.arionide.lang.symbols.Variable;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.mutables.MutableActor;

public class VariableManager extends ContextualManager<Variable> {
	protected VariableManager(Storage storage) {
		super(storage);
	}
	
	public Collection<Node> getState(Structure parent) {
		if(parent instanceof MutableActor) {
			return ((MutableActor) parent).getWrapper().getState().getNodes();
		} else {
			return Arrays.asList();
		}
	}
	
	public Collection<Node> getProps(Structure parent) {
		if(parent instanceof MutableActor) {
			return ((MutableActor) parent).getWrapper().getProperties().getNodes();
		} else {
			return Arrays.asList();
		}
	}
	
	public Collection<Node> getVariables(Structure parent) {
		return Stream.concat(getState(parent).stream(), getProps(parent).stream()).collect(Collectors.toList());
	}
	
	public MessageEvent create(Structure parent, Variable var, String name) {
		if(parent instanceof MutableActor) {
			Information initialValue = new Information("Variable");
			Node rootNode = initialValue.getRoot();
			
			rootNode.label(name);
			
			try {
				((MutableActor) parent).getWrapper().getState().connect(rootNode);
			} catch (SymbolResolutionException e) {
				return new MessageEvent("Unable to connect a new variable to the structure's state", MessageType.ERROR);
			}
			
			saveStructures();
			
			return success();
		} else {
			return new MessageEvent("This structure does not support variables", MessageType.ERROR);
		}
	}
	
	public MessageEvent delete(Structure parent, String name) {
		if(parent instanceof MutableActor) {
			try {
				Node state = ((MutableActor) parent).getWrapper().getState();
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
	
	public MessageEvent rename(Structure parent, String currentName, String newName) {
		if(parent instanceof MutableActor) {
			try {
				Node state = ((MutableActor) parent).getWrapper().getState();
				Node resolved = state.resolve(currentName);
				resolved.label(newName);
			} catch (SymbolResolutionException e) {
				return new MessageEvent("Unable to rename this variable", MessageType.ERROR);
			}
			
			saveStructures();
			
			return success();
		} else {
			return new MessageEvent("This structure does not support variables", MessageType.ERROR);
		}
	}
}
