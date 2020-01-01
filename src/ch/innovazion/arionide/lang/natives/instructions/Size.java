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

import java.util.List;

import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.Object;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.lang.natives.NativeDataCommunicator;
import ch.innovazion.arionide.lang.natives.NativeTypes;

public class Size implements NativeInstruction {
	
	private final Data object;
	private final Data result;
	
	public Size(Data object, Data result) {
		this.object = object;
		this.result = result;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(this.object.getValue().startsWith(SpecificationElement.VAR)) {
			SpecificationElement element = communicator.getVariable(this.object.getValue().substring(4));
			
			if(element != null) {
				String result = this.result.getValue();
				
				if(result.startsWith(SpecificationElement.VAR)) {
					result = result.substring(4);
					
					Object object = communicator.getObject(element.getValue());
					
					if(object != null) {
						communicator.setVariable(result, communicator.isDefined(element.getValue()) && communicator.isLocal(element.getValue()), new Data(result, "d" + object.getSize(), NativeTypes.INTEGER));
						return true;
					} else {
						communicator.exception("Invalid object: " + result);
					}
				} else {
					communicator.exception("You can't use a direct value for this variable");
				}
			} else {
				communicator.exception("Dead variable: " + this.object.getValue());
			}
		} else {
			communicator.exception("You can't use a direct value for an object instance");
		}
		
		return false;
	}
}