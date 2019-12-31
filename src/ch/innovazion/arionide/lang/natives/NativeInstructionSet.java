/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.lang.natives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.InstructionSet;
import ch.innovazion.arionide.lang.Reference;
import ch.innovazion.arionide.lang.Specification;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.managers.DataManager;
import ch.innovazion.arionide.project.managers.ResourceAllocator;

public class NativeInstructionSet extends InstructionSet {
	
	private final Map<String, Integer> instructionSet = new HashMap<>();
	
	public NativeInstructionSet(Project project) {
		super(project);
	}
	
	public void install() {
		Project project = getProject();
		DataManager manager = project.getDataManager();
				
		int structID = manager.getResourceAllocator().nextStructure();
		
		boolean alreadyExists = !manager.newStructure("natives", false).getMessageType().equals(MessageType.SUCCESS);
		
		manager.getHostStack().push(structID);
		
		if(alreadyExists) {
			retrieve("init");
			retrieve("print");
			retrieve("call");
			retrieve("defineText");
			retrieve("defineInteger");
			retrieve("defineStructure");
			retrieve("redo");
			retrieve("if");
			retrieve("compareText");
			retrieve("compareInteger");
			retrieve("compareStructure");
			retrieve("object");
			retrieve("addInteger");
			retrieve("addComplex");
			retrieve("write");
			retrieve("iterate");
			retrieve("size");
			retrieve("merge");
			retrieve("load");
		} else {
			manager.getCodeManager().resetCodeChain(-1);
			manager.getCodeManager().resetCodeChain(structID);
			
			ResourceAllocator allocator = manager.getResourceAllocator();
			
			add("init", 0, new Specification(allocator.allocSpecification()));
			
			add("print", 10, new Specification(
					allocator.allocSpecification(), 
					new Data("message", "debug", NativeTypes.TEXT)));
			
			add("call", 20, new Specification(
					allocator.allocSpecification(),
					new Reference("reference", null, new ArrayList<>(), new ArrayList<>())));
		
			add("defineText", 28, new Specification(
					allocator.allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("value", null, NativeTypes.TEXT),
					new Data("local", IntegerTypeManager.FALSE, NativeTypes.INTEGER)));
			
			add("defineInteger", 30, new Specification(
					allocator.allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("value", null, NativeTypes.INTEGER),
					new Data("local", IntegerTypeManager.FALSE, NativeTypes.INTEGER)));
			
			add("defineStructure", 32, new Specification(
					allocator.allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("value", null, NativeTypes.STRUCTURE),
					new Data("local", IntegerTypeManager.FALSE, NativeTypes.INTEGER)));
			
			add("redo", 40, new Specification(
					allocator.allocSpecification(),
					new Reference("predicate", null, new ArrayList<>(Arrays.asList(new Data("condition", null, NativeTypes.INTEGER))), new ArrayList<>())));
		
			add("if", 50, new Specification(
					allocator.allocSpecification(),
					new Reference("predicate", null, new ArrayList<>(Arrays.asList(new Data("condition", null, NativeTypes.INTEGER))), new ArrayList<>()),
					new Reference("true", null, new ArrayList<>(), new ArrayList<>()),
					new Reference("false", null, new ArrayList<>(), new ArrayList<>())));
			
			add("compareText", 58, new Specification(
					allocator.allocSpecification(),
					new Data("first", null, NativeTypes.TEXT),
					new Data("second", null, NativeTypes.TEXT),
					new Data("result", null, NativeTypes.INTEGER)));
			
			add("compareInteger", 60, new Specification(
					allocator.allocSpecification(),
					new Data("first", null, NativeTypes.INTEGER),
					new Data("second", null, NativeTypes.INTEGER),
					new Data("result", null, NativeTypes.INTEGER)));
			
			add("compareStructure", 62, new Specification(
					allocator.allocSpecification(),
					new Data("first", null, NativeTypes.STRUCTURE),
					new Data("second", null, NativeTypes.STRUCTURE),
					new Data("result", null, NativeTypes.INTEGER)));
			
			add("object", 70, new Specification(
					allocator.allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("structure", null, NativeTypes.STRUCTURE),
					new Reference("constructor", null, new ArrayList<>(), new ArrayList<>())));
			
			
			add("addInteger", 79, new Specification(
					allocator.allocSpecification(), 
					new Data("data", null, NativeTypes.INTEGER)));
			
			add("addComplex", 81, new Specification(
					allocator.allocSpecification(), 
					new Data("data", null, NativeTypes.TEXT)));
			
			add("write", 90, new Specification(
					allocator.allocSpecification(), 
					new Data("object", null, NativeTypes.TEXT),
					new Data("path", null, NativeTypes.TEXT)));
			
			add("iterate", 100, new Specification(
					allocator.allocSpecification(), 
					new Data("object", null, NativeTypes.TEXT),
					new Data("selector", null, NativeTypes.TEXT),
					new Reference("updater", null, new ArrayList<>(Arrays.asList(new Data("value", null, NativeTypes.TEXT), new Data("index", null, NativeTypes.INTEGER))), new ArrayList<>()),
					new Data("layers", "b0", NativeTypes.INTEGER)));
			
			add("size", 110, new Specification(
					allocator.allocSpecification(),
					new Data("object", null, NativeTypes.TEXT),
					new Data("result", null, NativeTypes.TEXT)));
			
			add("merge", 120, new Specification(
					allocator.allocSpecification(),
					new Data("source1", null, NativeTypes.TEXT),
					new Data("source2", null, NativeTypes.TEXT),
					new Data("destination", null, NativeTypes.TEXT)));
			
			add("load", 130, new Specification(
					allocator.allocSpecification(),
					new Data("source", null, NativeTypes.INTEGER),
					new Data("target", null, NativeTypes.TEXT)));
		}
		
		manager.getHostStack().pop();
	}
	
	private void add(String name, int color, Specification specification) {
		instructionSet.put(name, addInstructionDefinition(name, color, specification));
	}
	
	private void retrieve(String name) {
		instructionSet.put(name, retrieveInstructionDefinition(name));
	}

	public int getInstructionID(String name) {
		return instructionSet.get(name);
	}
}