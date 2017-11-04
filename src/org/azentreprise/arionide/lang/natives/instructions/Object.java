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

import java.util.List;

import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;
import org.azentreprise.arionide.lang.natives.NativeTypes;

public class Object implements NativeInstruction {

	private final Data result;
	private final Data structure;
	private final int constructorRef;
	private final Call constructor;
	
	public Object(Data result, Data structure, Reference constructor) {
		this.result = result;
		this.structure = structure;
		this.constructorRef = Integer.parseInt(constructor.getValue());
		this.constructor = new Call(constructor);
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(this.result.getValue().contains("var@")) {
			String variable = this.result.getValue().substring(4);
			
			String structure = this.structure.getValue();
			
			if(structure.startsWith("var@")) {
				structure = communicator.getVariable(structure.substring(4)).getValue();
			}
			
			String identifier = communicator.allocObject(structure);
			
			communicator.setVariable(variable, true, new Data(variable, identifier, NativeTypes.TEXT));
			
			communicator.bindObject(this.constructorRef, identifier);
			this.constructor.execute(communicator, references);
			communicator.unbindObject(this.constructorRef);
			
			if(communicator.getObject(identifier).isConsistent()) {
				return true;
			} else {
				communicator.exception("Inconsitent object");
				return false;
			}
		} else {
			communicator.exception("You can't use a direct value for an object instance");
			return false;
		}
	}
}