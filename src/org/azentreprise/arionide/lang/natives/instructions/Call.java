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
package org.azentreprise.arionide.lang.natives.instructions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;

public class Call implements NativeInstruction {
	
	private final Reference reference;
	
	public Call(Reference reference) {
		this.reference = reference;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(references.contains(this.reference)) {
			Stack<Integer> theStack = communicator.getStack();
			
			if(theStack.size() > 65535) {
				communicator.exception("Stack overflow");
				return false;
			}
			
			List<SpecificationElement> specVars = new ArrayList<>();
			
			for(SpecificationElement element : this.reference.getSpecificationParameters()) {
				String value = element.getValue();
								
				if(value.startsWith("var@")) {
					SpecificationElement specElement = communicator.getVariable(value.substring(4)).clone();
					specElement.setName(element.getName());
					specVars.add(specElement);
				}
			}
			
			for(SpecificationElement element : this.reference.getNeededParameters()) {
				String value = element.getValue();
								
				if(value.startsWith("var@")) {
					SpecificationElement specElement = communicator.getVariable(value.substring(4)).clone();
					specElement.setName(element.getName());
					specVars.add(specElement);
				}
			}
			
			theStack.push(Integer.parseInt(this.reference.getValue()));
			
			communicator.initVariablePool();
			
			for(SpecificationElement specVar : specVars) {
				communicator.setVariable(specVar.getName(), true, specVar);
			}
			
			communicator.exec(references.indexOf(this.reference));
			
			List<SpecificationElement> newVars = new ArrayList<>();
			
			for(SpecificationElement specVar : specVars) {
				newVars.add(communicator.getVariable(specVar.getName())); // Variable might be redefined but not deleted
			}
			
			communicator.clearVariablePool();
			theStack.pop();
			
			for(SpecificationElement newVar : newVars) {
				communicator.setVariable(newVar.getName(), communicator.isLocal(newVar.getName()), newVar);
			}
			
			return true;
		} else {
			communicator.exception("Dead reference error");
			return false;
		}
	}
}