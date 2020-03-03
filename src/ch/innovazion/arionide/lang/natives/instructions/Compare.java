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
import ch.innovazion.arionide.lang.natives.NativeDataCommunicator;
import ch.innovazion.arionide.lang.natives.NativeTypes;
import ch.innovazion.arionide.lang.symbols.Parameter;

public class Compare implements NativeInstruction {

	private final Data var1;
	private final Data var2;
	private final Data result;
	
	public Compare(Data var1, Data var2, Data result) {
		this.var1 = var1;
		this.var2 = var2;
		this.result = result;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		String value1 = this.var1.getDisplayValue();
		String value2 = this.var2.getDisplayValue();

		if(value1.startsWith(Parameter.VAR)) {
			value1 = communicator.getVariable(value1.substring(4)).getDisplayValue();
		}
		
		if(value2.startsWith(Parameter.VAR)) {
			value2 = communicator.getVariable(value2.substring(4)).getDisplayValue();
		}
		
		if(this.var1.getType() == NativeTypes.INTEGER) {
			value1 = value1.substring(1);
		}
		
		if(this.var2.getType() == NativeTypes.INTEGER) {
			value2 = value2.substring(1);
		}
				
		boolean equals = value1.equals(value2);
		
		if(this.result.getDisplayValue().contains(Parameter.VAR)) {
			String varName = this.result.getDisplayValue().substring(4);
			communicator.setVariable(varName, communicator.isDefined(varName) ? communicator.isLocal(varName) : false, new Data(varName, equals ? "b1" : "b0", NativeTypes.INTEGER));
		}
		
		return true;
	}

}
