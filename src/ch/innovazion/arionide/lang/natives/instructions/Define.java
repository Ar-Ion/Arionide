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
package ch.innovazion.arionide.lang.natives.instructions;

import java.util.List;

import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.lang.natives.NativeDataCommunicator;

public class Define implements NativeInstruction {
	
	private final Data name;
	private final Data value;
	private final Data local;
	
	public Define(Data name, Data value, Data local) {
		this.name = name;
		this.value = value;
		this.local = local;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(!this.name.getValue().startsWith(SpecificationElement.VAR)) {
			communicator.exception("The variable '" + this.name.getValue() + "' is not defined with the 'New variable' button but with the 'Custom string' one.");
			return false;
		}
		
		String value = this.value.getValue();
		
		if(value.startsWith(SpecificationElement.VAR)) {
			value = communicator.getVariable(value.substring(4)).getValue();
		}
				
		boolean local = this.local.getValue().substring(1).equals("1");
		
		Data data = new Data(this.name.getValue().substring(4), value, this.value.getType());
		
		communicator.setVariable(data.getName(), local, data);
		
		return true;
	}
}