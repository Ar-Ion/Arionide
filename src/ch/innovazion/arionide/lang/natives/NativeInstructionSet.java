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
		Project project = this.getProject();
		DataManager manager = project.getDataManager();
		
		int structID = manager.getResourceAllocator().nextStructure();
		
		manager.getHostStack().push(structID);
		
		if(manager.newStructure("natives", false).getMessageType().equals(MessageType.SUCCESS)) {
			ResourceAllocator allocator = manager.getResourceAllocator();
			
			this.add("init", 0, new Specification(allocator.allocSpecification()));
			
			this.add("print", 10, new Specification(
					allocator.allocSpecification(), 
					new Data("message", "debug", NativeTypes.TEXT)));
			
			this.add("call", 20, new Specification(
					allocator.allocSpecification(),
					new Reference("reference", null, new ArrayList<>(), new ArrayList<>())));
		
			this.add("defineText", 28, new Specification(
					allocator.allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("value", null, NativeTypes.TEXT),
					new Data("local", IntegerTypeManager.FALSE, NativeTypes.INTEGER)));
			
			this.add("defineInteger", 30, new Specification(
					allocator.allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("value", null, NativeTypes.INTEGER),
					new Data("local", IntegerTypeManager.FALSE, NativeTypes.INTEGER)));
			
			this.add("defineStructure", 32, new Specification(
					allocator.allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("value", null, NativeTypes.STRUCTURE),
					new Data("local", IntegerTypeManager.FALSE, NativeTypes.INTEGER)));
			
			this.add("redo", 40, new Specification(
					allocator.allocSpecification(),
					new Reference("predicate", null, new ArrayList<>(Arrays.asList(new Data("condition", null, NativeTypes.INTEGER))), new ArrayList<>())));
		
			this.add("if", 50, new Specification(
					allocator.allocSpecification(),
					new Reference("predicate", null, new ArrayList<>(Arrays.asList(new Data("condition", null, NativeTypes.INTEGER))), new ArrayList<>()),
					new Reference("true", null, new ArrayList<>(), new ArrayList<>()),
					new Reference("false", null, new ArrayList<>(), new ArrayList<>())));
			
			this.add("compareText", 58, new Specification(
					allocator.allocSpecification(),
					new Data("first", null, NativeTypes.TEXT),
					new Data("second", null, NativeTypes.TEXT),
					new Data("result", null, NativeTypes.INTEGER)));
			
			this.add("compareInteger", 60, new Specification(
					allocator.allocSpecification(),
					new Data("first", null, NativeTypes.INTEGER),
					new Data("second", null, NativeTypes.INTEGER),
					new Data("result", null, NativeTypes.INTEGER)));
			
			this.add("compareStructure", 62, new Specification(
					allocator.allocSpecification(),
					new Data("first", null, NativeTypes.STRUCTURE),
					new Data("second", null, NativeTypes.STRUCTURE),
					new Data("result", null, NativeTypes.INTEGER)));
			
			this.add("object", 70, new Specification(
					allocator.allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("structure", null, NativeTypes.STRUCTURE),
					new Reference("constructor", null, new ArrayList<>(), new ArrayList<>())));
			
			
			this.add("addInteger", 79, new Specification(
					allocator.allocSpecification(), 
					new Data("data", null, NativeTypes.INTEGER)));
			
			this.add("addComplex", 81, new Specification(
					allocator.allocSpecification(), 
					new Data("data", null, NativeTypes.TEXT)));
			
			this.add("write", 90, new Specification(
					allocator.allocSpecification(), 
					new Data("object", null, NativeTypes.TEXT),
					new Data("path", null, NativeTypes.TEXT)));
			
			this.add("iterate", 100, new Specification(
					allocator.allocSpecification(), 
					new Data("object", null, NativeTypes.TEXT),
					new Data("selector", null, NativeTypes.TEXT),
					new Reference("updater", null, new ArrayList<>(Arrays.asList(new Data("value", null, NativeTypes.TEXT), new Data("index", null, NativeTypes.INTEGER))), new ArrayList<>()),
					new Data("layers", "b0", NativeTypes.INTEGER)));
			
			this.add("size", 110, new Specification(
					allocator.allocSpecification(),
					new Data("object", null, NativeTypes.TEXT),
					new Data("result", null, NativeTypes.TEXT)));
			
			this.add("merge", 120, new Specification(
					allocator.allocSpecification(),
					new Data("source1", null, NativeTypes.TEXT),
					new Data("source2", null, NativeTypes.TEXT),
					new Data("destination", null, NativeTypes.TEXT)));
			
			this.add("load", 130, new Specification(
					allocator.allocSpecification(),
					new Data("source", null, NativeTypes.INTEGER),
					new Data("target", null, NativeTypes.TEXT)));
		} else {
			this.retrieve("init");
			this.retrieve("print");
			this.retrieve("call");
			this.retrieve("defineText");
			this.retrieve("defineInteger");
			this.retrieve("defineStructure");
			this.retrieve("redo");
			this.retrieve("if");
			this.retrieve("compareText");
			this.retrieve("compareInteger");
			this.retrieve("compareStructure");
			this.retrieve("object");
			this.retrieve("addInteger");
			this.retrieve("addComplex");
			this.retrieve("write");
			this.retrieve("iterate");
			this.retrieve("size");
			this.retrieve("merge");
			this.retrieve("load");
		}
		
		manager.getHostStack().pop();
	}
	
	private void add(String name, int color, Specification specification) {
		this.instructionSet.put(name, this.addInstructionDefinition(name, color, specification));
	}
	
	private void retrieve(String name) {
		this.instructionSet.put(name, this.retrieveInstructionDefinition(name));
	}

	public int getInstructionID(String name) {
		return this.instructionSet.get(name);
	}
}