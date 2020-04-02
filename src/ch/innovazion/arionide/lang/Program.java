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
package ch.innovazion.arionide.lang;

import java.util.List;
import java.util.stream.Collectors;

import ch.innovazion.arionide.lang.programs.ProgramIO;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.InheritanceElement;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.mutables.MutableActor;

public abstract class Program {
	
	private final Storage storage;
	
	public Program(Storage storage) {
		this.storage = storage;
	}
	
	protected InheritanceElement getInheritance(int id) {
		return storage.getInheritance().get(id);
	}
	
	protected Callable getCallable(int id) {
		return storage.getStructures().get(id);
	}
	
	protected Node getState(int id) {
		Callable callable = getCallable(id);
		
		if(callable != null && callable instanceof MutableActor) {
			return ((MutableActor) callable).getWrapper().getState();
		} else {
			return new Node();
		}
	}
	
	protected Node getProperties(int id) {
		Callable callable = getCallable(id);
		
		if(callable != null && callable instanceof MutableActor) {
			return ((MutableActor) callable).getWrapper().getProperties();
		} else {
			return new Node();
		}
	}
	
	protected Node getConstants(int id) {
		Callable callable = getCallable(id);
		
		if(callable != null && callable instanceof MutableActor) {
			return ((MutableActor) callable).getWrapper().getConstants();
		} else {
			return new Node();
		}
	}
	
	protected List<Callable> getInstructions(int id) {
		return storage.getCode().get(id).list().stream().map(HierarchyElement::getID).map(this::getCallable).collect(Collectors.toList());
	}

	public abstract void run(int rootStructure, ProgramIO io);
	public abstract String getName();
}
