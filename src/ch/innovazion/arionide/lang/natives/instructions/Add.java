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

public class Add implements NativeInstruction {

	private final Data data;
	
	public Add(Data data) {
		this.data = data;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		Object object = communicator.getBoundObject();
		
		if(object != null) {
			String value = this.data.getValue();
			
			if(value.startsWith(SpecificationElement.VAR)) {
				value = communicator.getVariable(value.substring(4)).getValue();
			}
			
			object.add(value, communicator);
			
			return true;
		} else {
			communicator.exception("Invalid state error: No object is bound to this context");
			return false;
		}
	}
}