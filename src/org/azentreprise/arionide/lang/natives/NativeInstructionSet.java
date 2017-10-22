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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.InstructionSet;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.project.Project;

public class NativeInstructionSet extends InstructionSet implements Serializable {

	private static final long serialVersionUID = 4171728714732308283L;
	
	private final Map<String, Integer> instructionSet = new HashMap<>();
	
	public NativeInstructionSet(Project project) {
		super(project);
	}
	
	public void install() {
		int structID = this.getProject().getProperty("structureGen", Coder.integerDecoder).intValue();
		
		if(this.getProject().getDataManager().newStructure("natives", Arrays.asList()).getMessageType().equals(MessageType.SUCCESS)) {
			List<Integer> parents = Arrays.asList(structID);
			
			
			this.install("init", 0, parents, new Specification(this.getProject().getDataManager().allocSpecification()));
						
			this.install("debug", 30, parents, new Specification(this.getProject().getDataManager().allocSpecification(), 
					new SpecificationElement("Info", NativeTypes.STR, "debug")));
			
		} else {
			this.retrieve("init");
			this.retrieve("nop");
			this.retrieve("debug");
		}
	}
	
	private void install(String name, int color, List<Integer> parents, Specification specification) {
		this.instructionSet.put(name, this.installInstruction(name, color, parents, specification));
	}
	
	private void retrieve(String name) {
		this.instructionSet.put(name, this.retrieveInstruction(name));
	}

	public int getStructureEntry() {
		return this.instructionSet.get("init");
	}
	
	public int getInstructionID(String name) {
		return this.instructionSet.get(name);
	}
	
	public List<String> getInstructions() {
		List<String> list = new ArrayList<>(this.instructionSet.keySet());
		list.remove("init");
		return list;
	}
}