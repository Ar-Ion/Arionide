/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.lang.natives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.InstructionSet;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.project.Project;

public class NativeInstructionSet extends InstructionSet {
	
	private final Map<String, Integer> instructionSet = new HashMap<>();
	
	public NativeInstructionSet(Project project) {
		super(project);
	}
	
	public void install() {
		int structID = this.getProject().getProperty("structureGen", Coder.integerDecoder).intValue();
		
		if(this.getProject().getDataManager().newStructure("natives", Arrays.asList()).getMessageType().equals(MessageType.SUCCESS)) {
			List<Integer> parents = Arrays.asList(structID);

			this.install("init", 0, parents, new Specification(this.getProject().getDataManager().allocSpecification()));
			
			this.install("print", 10, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(), 
					new Data("message", "debug", NativeTypes.TEXT)));
			
			this.install("call", 20, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Reference("reference", null, new ArrayList<>(), new ArrayList<>())));
		
			this.install("defineText", 28, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("value", null, NativeTypes.TEXT),
					new Data("local", "0$$$b0", NativeTypes.INTEGER)));
			
			this.install("defineInteger", 30, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("value", null, NativeTypes.INTEGER),
					new Data("local", "0$$$b0", NativeTypes.INTEGER)));
			
			this.install("defineStructure", 32, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("value", null, NativeTypes.STRUCTURE),
					new Data("local", "0$$$b0", NativeTypes.INTEGER)));
			
			this.install("redo", 40, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Reference("predicate", null, new ArrayList<>(Arrays.asList(new Data("condition", null, NativeTypes.INTEGER))), new ArrayList<>())));
		
			this.install("if", 50, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Reference("predicate", null, new ArrayList<>(Arrays.asList(new Data("condition", null, NativeTypes.INTEGER))), new ArrayList<>()),
					new Reference("true", null, new ArrayList<>(), new ArrayList<>()),
					new Reference("false", null, new ArrayList<>(), new ArrayList<>())));
			
			this.install("compareText", 58, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Data("first", null, NativeTypes.TEXT),
					new Data("second", null, NativeTypes.TEXT),
					new Data("result", null, NativeTypes.INTEGER)));
			
			this.install("compareInteger", 60, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Data("first", null, NativeTypes.INTEGER),
					new Data("second", null, NativeTypes.INTEGER),
					new Data("result", null, NativeTypes.INTEGER)));
			
			this.install("compareStructure", 62, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Data("first", null, NativeTypes.STRUCTURE),
					new Data("second", null, NativeTypes.STRUCTURE),
					new Data("result", null, NativeTypes.INTEGER)));
			
			this.install("object", 70, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Data("name", null, NativeTypes.TEXT),
					new Data("structure", null, NativeTypes.STRUCTURE),
					new Reference("constructor", null, new ArrayList<>(), new ArrayList<>())));
			
			
			this.install("addInteger", 79, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(), 
					new Data("data", null, NativeTypes.INTEGER)));
			
			this.install("addComplex", 81, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(), 
					new Data("data", null, NativeTypes.TEXT)));
			
			this.install("write", 90, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(), 
					new Data("path", null, NativeTypes.TEXT),
					new Data("object", null, NativeTypes.TEXT)));
			
			this.install("iterate", 100, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(), 
					new Data("object", null, NativeTypes.TEXT),
					new Data("selector", null, NativeTypes.TEXT),
					new Reference("updater", null, new ArrayList<>(Arrays.asList(new Data("value", null, NativeTypes.TEXT), new Data("index", null, NativeTypes.INTEGER))), new ArrayList<>()),
					new Data("layers", "b0", NativeTypes.INTEGER)));
			
			this.install("size", 110, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Data("object", null, NativeTypes.TEXT),
					new Data("result", null, NativeTypes.TEXT)));
			
			this.install("merge", 120, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Data("source1", null, NativeTypes.TEXT),
					new Data("source2", null, NativeTypes.TEXT),
					new Data("destination", null, NativeTypes.TEXT)));
			
			this.install("load", 130, parents, new Specification(
					this.getProject().getDataManager().allocSpecification(),
					new Data("source", null, NativeTypes.TEXT),
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
	}
	
	private void install(String name, int color, List<Integer> parents, Specification specification) {
		this.instructionSet.put(name, this.installInstruction(name, color, parents, specification));
	}
	
	private void retrieve(String name) {
		this.instructionSet.put(name, this.retrieveInstruction(name));
	}

	public int getInstructionID(String name) {
		return this.instructionSet.get(name);
	}
}