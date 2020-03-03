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
package ch.innovazion.arionide.lang.natives.instructions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ch.innovazion.arionide.lang.natives.NativeDataCommunicator;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.Parameter;

public class Call implements NativeInstruction {
	
	private final Reference reference;
	
	public Call(Reference reference) {
		this.reference = reference;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		int refID = Integer.parseInt(this.reference.getDisplayValue());
		
		if(references.contains(refID)) {
			Stack<Integer> theStack = communicator.getStack();
			
			if(theStack.size() > 65535) {
				communicator.exception("Stack overflow");
				return false;
			}
			
			List<Parameter> specVars = new ArrayList<>();
			
			for(Parameter element : this.reference.getLazyParameters()) {
				String value = element.getDisplayValue();
												
				if(value.startsWith(Parameter.VAR)) {
					Parameter specElement = communicator.getVariable(value.substring(4)).clone();
					specElement.setName(element.getName());
					specVars.add(specElement);
				} else {
					specVars.add(element.clone());
				}
			}
			
			for(Parameter element : this.reference.getEagerParameters()) {
				String value = element.getDisplayValue();
					
				if(value != null) {
					if(value.startsWith(Parameter.VAR)) {
						Parameter specElement = communicator.getVariable(value.substring(4)).clone();
						specElement.setName(element.getName());
						specVars.add(specElement);
					} else {
						specVars.add(element.clone());
					}
				}
			}
						
			theStack.push(refID);
			
			communicator.initVariablePool();
			
			for(Parameter specVar : specVars) {
				communicator.setVariable(specVar.getName(), true, specVar);
			}
			
			communicator.exec(references.indexOf(refID));
			
			List<Parameter> newVars = new ArrayList<>();
			
			for(Parameter specVar : specVars) {
				newVars.add(communicator.getVariable(specVar.getName())); // Variable might be redefined but not deleted
			}
						
			communicator.clearVariablePool();
			theStack.pop();
						
			for(Parameter newVar : newVars) {
				for(Parameter original : this.reference.getLazyParameters()) { // Rename variable according to the parent's context					
					if(original.getName().equals(newVar.getName())) {
						if(original.getDisplayValue().contains(Parameter.VAR)) {
							newVar.setName(original.getDisplayValue().substring(4));
						}
						
						break;
					}
				}
								
				if(communicator.isDefined(newVar.getName())) {
					communicator.setVariable(newVar.getName(), communicator.isLocal(newVar.getName()), newVar);
				}
			}
			
			return true;
		} else {
			communicator.exception("Dead reference error");
			return false;
		}
	}
}